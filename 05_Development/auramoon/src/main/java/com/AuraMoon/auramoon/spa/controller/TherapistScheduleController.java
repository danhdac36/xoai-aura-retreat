package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final TherapistRepository therapistRepository;

    @GetMapping("/daily")
    public String getDailySchedule(@RequestParam(value = "date", required = false) String dateStr,
            @AuthenticationPrincipal UserDetailsResponse userDetails, Model model) {

        Therapist therapist = therapistRepository.findById(userDetails.getId()).orElse(null);
        if (therapist == null) {
            return "redirect:/auth/login";
        }

        String therapistCode = therapist.getTherapistCode();

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
            @AuthenticationPrincipal UserDetailsResponse userDetails,
            RedirectAttributes redirectAttributes) {

        Therapist therapist = therapistRepository.findById(userDetails.getId()).orElse(null);
        if (therapist == null) {
            return "redirect:/auth/login";
        }

        String therapistCode = therapist.getTherapistCode();

        try {
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (SpaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại." + e.getMessage());
        }

        return "redirect:/therapist/schedules/daily?date=" + dateStr;
    }
}