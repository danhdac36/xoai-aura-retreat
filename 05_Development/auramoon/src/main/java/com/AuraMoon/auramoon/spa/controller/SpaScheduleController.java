package com.AuraMoon.auramoon.spa.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.service.SpaScheduleService;

@Controller
@RequestMapping("/guest/booking-spa")
public class SpaScheduleController {

    private final SpaScheduleService spaScheduleService;
    private final TreatmentBookingRepository treatmentBookingRepository;
    private final BookingRepository bookingRepository;

    public SpaScheduleController(SpaScheduleService spaScheduleService,
            TreatmentBookingRepository treatmentBookingRepository,
            BookingRepository bookingRepository) {
        this.spaScheduleService = spaScheduleService;
        this.treatmentBookingRepository = treatmentBookingRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public String showSchedulePage() {
        return "spa/booking-spa";
    }

    @GetMapping("/active-package")
    @ResponseBody
    public ResponseEntity<?> getActivePackage(@AuthenticationPrincipal UserDetailsResponse userDetails) {
        Integer userId = userDetails.getId();

        List<TreatmentBooking> unscheduledBookings = treatmentBookingRepository.findUnscheduledBookingsByGuestId(userId);

        // 1. Tìm TreatmentBooking thuộc các Booking đang Checked-In
        Optional<TreatmentBooking> checkedInBooking = unscheduledBookings.stream()
                .filter(tb -> {
                    Booking b = bookingRepository.findById(tb.getBookingId()).orElse(null);
                    return b != null && ("CHECKED_IN".equalsIgnoreCase(b.getBookingStatus())
                            || "Checked-In".equalsIgnoreCase(b.getBookingStatus())
                            || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus()));
                })
                .findFirst();

        if (checkedInBooking.isPresent()) {
            TreatmentBooking booking = checkedInBooking.get();
            Map<String, Object> activePackage = new HashMap<>();
            activePackage.put("bookingId", booking.getBookingId());
            activePackage.put("serviceId", booking.getTreatmentService().getId());
            activePackage.put("serviceName", booking.getTreatmentService().getServiceName());
            activePackage.put("durationMinutes", booking.getTreatmentService().getDurationMinutes());
            activePackage.put("priceInfo", "Đã bao gồm trong Gói Retreat");
            return ResponseEntity.ok(activePackage);
        }

        // 2. Nếu không có booking Checked-In, kiểm tra xem có booking sắp tới nào (CONFIRMED hoặc PENDING)
        boolean hasUpcomingBooking = unscheduledBookings.stream()
                .anyMatch(tb -> {
                    Booking b = bookingRepository.findById(tb.getBookingId()).orElse(null);
                    return b != null && ("CONFIRMED".equalsIgnoreCase(b.getBookingStatus())
                            || "PENDING".equalsIgnoreCase(b.getBookingStatus()));
                });

        if (hasUpcomingBooking) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error",
                            "Bạn chỉ được phép đặt lịch Spa sau khi đã thực hiện Check-In tại quầy lễ tân."));
        }

        // 3. Không tìm thấy bất kỳ gói booking nào khả dụng
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Bạn chưa thực hiện đặt trước"));
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam("date") String dateStr,
            @RequestParam(value = "duration", defaultValue = "60") Integer duration,
            @RequestParam(value = "bookingId", required = false) Integer bookingId) {
        LocalDate date = LocalDate.parse(dateStr);
        List<String> availableSlots = spaScheduleService.getAvailableTimeSlots(date, duration, bookingId);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> scheduleSession(@RequestBody SpaScheduleRequest request,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        try {
            Integer userId = userDetails.getId();

            // 2. Chống IDOR: Xác minh bookingId trong request có đúng là của khách hàng
            // đang đăng nhập không
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