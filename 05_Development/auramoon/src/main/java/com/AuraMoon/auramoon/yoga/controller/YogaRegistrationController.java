package com.AuraMoon.auramoon.yoga.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationRequest;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationResponse;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.exception.HealthWarningException;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.service.IYogaRegistrationService;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/guest/booking-yoga")
public class YogaRegistrationController {

    private final IYogaRegistrationService yogaRegistrationService;
    private final BookingRepository bookingRepository;
    private final YogaRegistrationRepository yogaRegistrationRepository;

    public YogaRegistrationController(
            IYogaRegistrationService yogaRegistrationService,
            BookingRepository bookingRepository,
            YogaRegistrationRepository yogaRegistrationRepository) {
        this.yogaRegistrationService = yogaRegistrationService;
        this.bookingRepository = bookingRepository;
        this.yogaRegistrationRepository = yogaRegistrationRepository;
    }

    @GetMapping
    public String showSchedulePage() {
        return "yoga/booking-yoga";
    }

    @GetMapping("/active-booking")
    @ResponseBody
    public ResponseEntity<?> getActiveBooking(@AuthenticationPrincipal UserDetailsResponse userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        List<Booking> bookings = bookingRepository.findByGuestId(userDetails.getId());
        Optional<Booking> checkedInBooking = bookings.stream()
                .filter(b -> "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus())
                        || "Checked-In".equalsIgnoreCase(b.getBookingStatus()))
                .findFirst();

        if (checkedInBooking.isPresent()) {
            return ResponseEntity.ok(Map.of("bookingId", checkedInBooking.get().getId()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Bạn chỉ được đăng ký lớp Yoga sau khi đã thực hiện Check-In."));
    }

    @GetMapping("/available-schedules")
    @ResponseBody
    public ResponseEntity<?> getAvailableSchedules(
            @RequestParam("date") String dateStr,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<YogaSchedule> schedules = yogaRegistrationService.getAvailableSchedules(date);

            List<Map<String, Object>> result = new ArrayList<>();
            for (YogaSchedule s : schedules) {
                Map<String, Object> map = new HashMap<>();
                map.put("scheduleId", s.getId());
                map.put("className", s.getYogaClass().getClassName());
                map.put("durationMinutes", s.getYogaClass().getDurationMinutes());
                map.put("imageUrl", s.getYogaClass().getImageUrl());
                map.put("instructorName", s.getInstructor().getUser().getFullName());
                map.put("location", s.getLocation());
                map.put("startTime", s.getStartTime().toString());
                map.put("endTime", s.getEndTime().toString());
                map.put("maxCapacity", s.getMaxCapacity());
                
                long registeredCount = yogaRegistrationRepository.countBySchedule_IdAndStatus(s.getId(), "REGISTERED");
                long remainingSpots = s.getMaxCapacity() - registeredCount;
                map.put("remainingSpots", Math.max(0, remainingSpots));
                
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> registerYogaClass(
            @RequestBody YogaRegistrationRequest request,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", Map.of("code", "IAM-001", "message", "Authentication required")));
            }

            Integer userId = userDetails.getId();

            // 1. Chống IDOR: Xác minh bookingId trong request có đúng là của khách hàng
            // đang đăng nhập không
            Booking booking = bookingRepository.findById(request.getBookingId()).orElse(null);
            if (booking == null || !booking.getGuestId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error",
                                Map.of("code", "YOGA-006", "message", "Bạn không có quyền đặt lịch cho gói này.")));
            }

            YogaRegistrationResponse response = yogaRegistrationService.registerYogaClass(request);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Đăng ký tham gia lớp học Yoga thành công.");
            result.put("data", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (HealthWarningException e) {
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode());
            errorDetail.put("message", e.getMessage());
            errorDetail.put("warningCategory", e.getWarningCategory());
            errorWrapper.put("error", errorDetail);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorWrapper);

        } catch (YogaBusinessException e) {
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode());
            errorDetail.put("message", e.getMessage());
            errorWrapper.put("error", errorDetail);

            HttpStatus status = HttpStatus.BAD_REQUEST;
            if ("YOGA-002".equals(e.getErrorCode()) || "YOGA-003".equals(e.getErrorCode()) || "YOGA-007".equals(e.getErrorCode())) {
                status = HttpStatus.CONFLICT;
            } else if ("YOGA-006".equals(e.getErrorCode())) {
                status = HttpStatus.FORBIDDEN;
            }
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

    @GetMapping("/instructor/schedules/{scheduleId}/participants")
    @ResponseBody
    public ResponseEntity<?> getParticipants(
            @PathVariable("scheduleId") Integer scheduleId,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", Map.of("code", "IAM-001", "message", "Authentication required")));
            }

            List<com.AuraMoon.auramoon.yoga.dto.YogaParticipantResponse> response = yogaRegistrationService
                    .getParticipantsForSchedule(scheduleId, userDetails.getId());
            return ResponseEntity.ok(response);

        } catch (YogaBusinessException e) {
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode());
            errorDetail.put("message", e.getMessage());
            errorWrapper.put("error", errorDetail);

            HttpStatus status = HttpStatus.BAD_REQUEST;
            if ("YOGA-008".equals(e.getErrorCode()) || "YOGA-006".equals(e.getErrorCode())) {
                status = HttpStatus.FORBIDDEN;
            }
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
