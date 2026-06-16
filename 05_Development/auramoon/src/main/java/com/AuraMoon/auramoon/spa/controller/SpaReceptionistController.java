package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.service.SpaManualBookingService;
import com.AuraMoon.auramoon.spa.repository.SpaBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking-spa")
public class SpaReceptionistController {

    private final SpaManualBookingService spaManualBookingService;
    private final SpaBookingRepository spaBookingRepository;
    private final TreatmentServiceRepository treatmentServiceRepository;

    public SpaReceptionistController(SpaManualBookingService spaManualBookingService,
            SpaBookingRepository spaBookingRepository,
            TreatmentServiceRepository treatmentServiceRepository) {
        this.spaManualBookingService = spaManualBookingService;
        this.spaBookingRepository = spaBookingRepository;
        this.treatmentServiceRepository = treatmentServiceRepository;
    }

    @GetMapping("/manual")
    public String showManualBookingPage(Model model) {
        // Mở toang: Load thẳng danh sách dịch vụ
        model.addAttribute("services", treatmentServiceRepository.findByIsAvailableTrueAndIsDeleteFalse());
        return "spa/receptionist-booking";
    }

    @GetMapping("/checked-in-bookings")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCheckedInBookings() {
        // Mở toang: Trả thẳng dữ liệu API
        return ResponseEntity.ok(spaBookingRepository.findCheckedInBookingsWithGuestDetails());
    }

    @PostMapping("/manual")
    public String createManualBooking(
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("serviceId") Integer serviceId,
            @RequestParam("startTime") String startTimeStr,
            @RequestParam(value = "note", required = false) String note,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Lấy userId, nếu chưa đăng nhập thì tự động lấy ID = 1 (Admin/Receptionist) để
        // chống lỗi DB
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            userId = 1;
        }

        try {
            SpaScheduleRequest request = new SpaScheduleRequest();
            request.setBookingId(bookingId);
            request.setServiceId(serviceId);
            request.setStartTime(LocalDateTime.parse(startTimeStr));
            request.setNote(note);

            SpaScheduleResponse response = spaManualBookingService.bookAdditionalService(request, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch Spa thành công!");
            redirectAttributes.addFlashAttribute("scheduleData", response);
        } catch (SpaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
        }

        return "redirect:/booking-spa/manual";
    }
}