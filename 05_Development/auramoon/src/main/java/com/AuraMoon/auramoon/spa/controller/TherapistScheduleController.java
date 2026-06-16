package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/therapist/schedules")
@RequiredArgsConstructor
public class TherapistScheduleController {

    private final TherapistScheduleService therapistScheduleService;

    @GetMapping("/daily")
    public String getDailySchedule(@RequestParam(value = "date", required = false) String dateStr,
            HttpSession session, Model model) {

        // Lấy mã nhân viên, chưa đăng nhập tự gán "TH0001" để test
        String therapistCode = (String) session.getAttribute("therapistCode");
        if (therapistCode == null || therapistCode.trim().isEmpty()) {
            therapistCode = "TH0001";
        }

        LocalDate targetDate = LocalDate.now();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                targetDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                // Ignore parsing error and use today
            }
        }

        List<TherapistScheduleDto> schedules = therapistScheduleService.getDailySchedule(therapistCode, targetDate);

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedDate", targetDate);
        model.addAttribute("therapistCode", therapistCode);

        return "spa/therapist-schedule";
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam("scheduleId") Integer scheduleId,
            @RequestParam("status") String status,
            @RequestParam("date") String dateStr,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String therapistCode = (String) session.getAttribute("therapistCode");
        if (therapistCode == null || therapistCode.trim().isEmpty()) {
            therapistCode = "TH0001";
        }

        try {
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (SpaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
        }

        return "redirect:/therapist/schedules/daily?date=" + dateStr;
    }
}