package com.AuraMoon.auramoon.spa.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.service.SpaScheduleService;

@Controller
@RequestMapping("/booking-spa")
public class SpaScheduleController {

    private final SpaScheduleService spaScheduleService;
    private final TreatmentBookingRepository treatmentBookingRepository;

    public SpaScheduleController(SpaScheduleService spaScheduleService,
            TreatmentBookingRepository treatmentBookingRepository) {
        this.spaScheduleService = spaScheduleService;
        this.treatmentBookingRepository = treatmentBookingRepository;
    }

    @GetMapping
    public String showSchedulePage() {
        return "spa/booking-spa";
    }

    @GetMapping("/active-package")
    @ResponseBody
    public ResponseEntity<?> getActivePackage(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để xem thông tin gói Spa của bạn."));
        }

        Optional<TreatmentBooking> firstBooking = treatmentBookingRepository.findUnscheduledBookingsByGuestId(userId)
                .stream().findFirst();

        if (firstBooking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Bạn chưa thực hiện đặt trước"));
        }

        TreatmentBooking booking = firstBooking.get();
        Map<String, Object> activePackage = new HashMap<>();
        activePackage.put("bookingId", booking.getBookingId());
        activePackage.put("serviceId", booking.getTreatmentService().getId());
        activePackage.put("serviceName", booking.getTreatmentService().getServiceName());
        activePackage.put("durationMinutes", booking.getTreatmentService().getDurationMinutes());
        activePackage.put("priceInfo", "Đã bao gồm trong Gói Retreat");

        return ResponseEntity.ok(activePackage);
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam("date") String dateStr,
            @RequestParam(value = "duration", defaultValue = "60") Integer duration) {
        LocalDate date = LocalDate.parse(dateStr);
        List<String> availableSlots = spaScheduleService.getAvailableTimeSlots(date, duration);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> scheduleSession(@RequestBody SpaScheduleRequest request, HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error",
                                Map.of("code", "AUTH-401", "message", "Vui lòng đăng nhập để tiếp tục.")));
            }

            // Chống IDOR: Xác minh bookingId trong request có đúng là của khách hàng đang
            // đăng nhập không
            boolean isOwner = treatmentBookingRepository.findUnscheduledBookingsByGuestId(userId).stream()
                    .anyMatch(b -> b.getBookingId().equals(request.getBookingId()));

            if (!isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error",
                                Map.of("code", "AUTH-403", "message", "Bạn không có quyền đặt lịch cho gói này.")));
            }

            SpaScheduleResponse response = spaScheduleService.scheduleSession(request);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Spa appointment booked successfully.");
            result.put("data", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (SpaBusinessException e) {
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode());
            errorDetail.put("message", e.getMessage());
            errorWrapper.put("error", errorDetail);

            HttpStatus status = "SPA-010".equals(e.getErrorCode()) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorWrapper);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", "SYS-500");
            errorDetail.put("message", "System Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            errorWrapper.put("error", errorDetail);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
        }
    }
}