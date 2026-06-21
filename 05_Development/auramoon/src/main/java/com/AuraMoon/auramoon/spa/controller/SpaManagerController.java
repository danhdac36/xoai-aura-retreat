package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.spa.dto.ScheduleDto;
import com.AuraMoon.auramoon.spa.dto.TherapistDetailDto;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.service.SpaManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager/spa/therapists")
@RequiredArgsConstructor
public class SpaManagerController {

    private final SpaManagerService spaManagerService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String getTherapistsList(Model model, 
                                    @RequestParam(value = "selectedCode", required = false) String selectedCode,
                                    @RequestParam(value = "date", required = false) String dateStr) {
        List<TherapistDetailDto> therapists = spaManagerService.getAllTherapistsWithDetails();
        model.addAttribute("therapists", therapists);

        if (selectedCode != null && !selectedCode.isEmpty()) {
            LocalDate targetDate = LocalDate.now();
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {
                    targetDate = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    // Ignore parsing error and use today
                }
            }
            List<ScheduleDto> schedules = spaManagerService.getScheduleForTherapist(selectedCode, targetDate);
            model.addAttribute("schedules", schedules);
            model.addAttribute("selectedCode", selectedCode);
            model.addAttribute("selectedDate", targetDate);
        }

        return "spa/manager-therapists";
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String updateStatus(
            @RequestParam("therapistCode") String therapistCode,
            @RequestParam("status") String status,
            @AuthenticationPrincipal UserDetailsResponse userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            spaManagerService.updateTherapistStatus(therapistCode, status, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (SpaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:/manager/spa/therapists";
    }
}
