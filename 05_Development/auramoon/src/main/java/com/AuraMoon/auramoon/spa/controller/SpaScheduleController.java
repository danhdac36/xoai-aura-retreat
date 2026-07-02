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
    public ResponseEntity<?> getActivePackage(
            @RequestParam(value = "date", required = false) String dateStr,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        Integer userId = userDetails.getId();

        // 1. Find the guest's Checked-In Booking
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getGuestId().equals(userId) &&
                        ("CHECKED_IN".equalsIgnoreCase(b.getBookingStatus())
                        || "Checked-In".equalsIgnoreCase(b.getBookingStatus())
                        || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus())))
                .toList();

        if (activeBookings.isEmpty()) {
            boolean hasUpcomingBooking = bookingRepository.findAll().stream()
                    .anyMatch(b -> b.getGuestId().equals(userId) &&
                            ("CONFIRMED".equalsIgnoreCase(b.getBookingStatus())
                            || "PENDING".equalsIgnoreCase(b.getBookingStatus())));
            if (hasUpcomingBooking) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn chỉ được phép đặt lịch Spa sau khi đã thực hiện Check-In tại quầy lễ tân."));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Bạn chưa thực hiện đặt trước"));
        }

        Booking booking = activeBookings.get(0);
        com.AuraMoon.auramoon.booking.entity.RetreatPackage pkg = booking.getRetreatPackage();
        if (pkg == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Booking của bạn không đi kèm gói nghỉ dưỡng."));
        }

        // 2. Determine target date and day number of stay
        LocalDate targetDate = LocalDate.now();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                targetDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Định dạng ngày gửi lên không hợp lệ."));
            }
        }

        LocalDate checkin = booking.getCheckinDate().toLocalDate();
        long dayNumber = java.time.temporal.ChronoUnit.DAYS.between(checkin, targetDate) + 1;

        if (dayNumber < 1 || dayNumber > pkg.getDurationDays()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ngày trị liệu được chọn (" + targetDate + ") nằm ngoài thời gian kỳ nghỉ của bạn (Ngày thứ " + dayNumber + " của gói " + pkg.getDurationDays() + " ngày)."));
        }

        // 3. Find Spa activity in package itinerary for this day
        List<com.AuraMoon.auramoon.booking.entity.RetreatPackageItinerary> itineraries = pkg.getItineraries();
        com.AuraMoon.auramoon.booking.entity.RetreatPackageItinerary dayItin = null;
        if (itineraries != null) {
            dayItin = itineraries.stream()
                    .filter(i -> i.getDayNumber().equals((int) dayNumber) && i.getServiceCode() != null && !i.getServiceCode().trim().isEmpty())
                    .findFirst()
                    .orElse(null);
        }

        if (dayItin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không có dịch vụ Spa nào được thiết kế cho ngày thứ " + dayNumber + " (" + targetDate.toString() + ") trong gói của bạn."));
        }

        // 4. Find corresponding TreatmentBooking
        String serviceCode = dayItin.getServiceCode().trim();
        List<TreatmentBooking> tbList = treatmentBookingRepository.findByBookingId(booking.getId());
        Optional<TreatmentBooking> unscheduledSpa = tbList.stream()
                .filter(tb -> tb.getTreatmentService() != null
                        && serviceCode.equalsIgnoreCase(tb.getTreatmentService().getTreatmentCode())
                        && "PENDING".equalsIgnoreCase(tb.getStatus())
                        && Boolean.FALSE.equals(tb.getIsDelete()))
                .findFirst();

        if (unscheduledSpa.isEmpty()) {
            Optional<TreatmentBooking> scheduledSpa = tbList.stream()
                    .filter(tb -> tb.getTreatmentService() != null
                            && serviceCode.equalsIgnoreCase(tb.getTreatmentService().getTreatmentCode())
                            && !"PENDING".equalsIgnoreCase(tb.getStatus())
                            && Boolean.FALSE.equals(tb.getIsDelete()))
                    .findFirst();

            if (scheduledSpa.isPresent()) {
                TreatmentBooking tb = scheduledSpa.get();
                Map<String, Object> activePackage = new HashMap<>();
                activePackage.put("bookingId", booking.getId());
                activePackage.put("serviceId", tb.getTreatmentService().getId());
                activePackage.put("serviceName", tb.getTreatmentService().getServiceName());
                activePackage.put("durationMinutes", tb.getTreatmentService().getDurationMinutes());
                activePackage.put("priceInfo", "Đã bao gồm trong Gói Retreat (Đã đặt lịch thành công)");
                activePackage.put("isAlreadyScheduled", true);
                return ResponseEntity.ok(activePackage);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy vé trị liệu Spa khả dụng cho ngày thứ " + dayNumber + " (Dịch vụ: " + serviceCode + ")."));
        }

        TreatmentBooking tBooking = unscheduledSpa.get();
        Map<String, Object> activePackage = new HashMap<>();
        activePackage.put("bookingId", tBooking.getBookingId());
        activePackage.put("serviceId", tBooking.getTreatmentService().getId());
        activePackage.put("serviceName", tBooking.getTreatmentService().getServiceName());
        activePackage.put("durationMinutes", tBooking.getTreatmentService().getDurationMinutes());
        activePackage.put("priceInfo", "Đã bao gồm trong Gói Retreat");
        activePackage.put("isAlreadyScheduled", false);
        return ResponseEntity.ok(activePackage);
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