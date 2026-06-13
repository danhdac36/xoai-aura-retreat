package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.service.SpaScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/spa/schedules")
public class SpaScheduleController {

    private final SpaScheduleService spaScheduleService;
    private final TreatmentBookingRepository treatmentBookingRepository;

    public SpaScheduleController(SpaScheduleService spaScheduleService, TreatmentBookingRepository treatmentBookingRepository) {
        this.spaScheduleService = spaScheduleService;
        this.treatmentBookingRepository = treatmentBookingRepository;
    }

    @GetMapping
    public String showSchedulePage() {
        return "spa/schedule";
    }

    @GetMapping("/active-package")
    @ResponseBody
    public ResponseEntity<?> getActivePackage() {
        // Lấy tự động 1 gói Spa bất kỳ có sẵn trong Database để làm MOCK cho "Gói của User hiện tại"
        // (Thay vì hardcode. Khi có Auth, sẽ dùng User ID để tìm đúng gói).
        Optional<TreatmentBooking> firstBooking = treatmentBookingRepository.findAll().stream()
                .filter(b -> !"Scheduled".equals(b.getStatus())) // Lọc gói chưa đặt lịch
                .findFirst();

        if (firstBooking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Không tìm thấy gói Spa Retreat nào khả dụng trong hệ thống"));
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
            @org.springframework.web.bind.annotation.RequestParam("date") String dateStr,
            @org.springframework.web.bind.annotation.RequestParam(value = "duration", defaultValue = "60") Integer duration) {
        LocalDate date = LocalDate.parse(dateStr);
        List<String> availableSlots = spaScheduleService.getAvailableTimeSlots(date, duration);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> scheduleSession(@RequestBody SpaScheduleRequest request) {
        try {
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
            e.printStackTrace(); // Print to server console
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", "SYS-500");
            errorDetail.put("message", "System Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            errorWrapper.put("error", errorDetail);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
        }
    }
}
