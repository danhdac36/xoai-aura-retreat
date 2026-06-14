package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        // 1. Get therapist code from session
        String therapistCode = (String) session.getAttribute("therapistCode");

        // ---- TẠM THỜI FAKE DỮ LIỆU ĐỂ TEST KHI CHƯA GHÉP CODE LOGIN ----
        if (therapistCode == null || therapistCode.trim().isEmpty()) {
            therapistCode = "NV001"; // Mã chuyên viên giả lập
            // (Sau này ghép code login xong thì cậu xóa dòng trên đi và mở comment 2 dòng
            // dưới ra nhé)
            // return "redirect:/login";
        }
        // ----------------------------------------------------------------

        // 2. Parse date, default to today if not provided
        LocalDate targetDate = LocalDate.now();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                targetDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                // Ignore parsing error and use today
            }
        }

        // 3. Fetch schedules
        List<TherapistScheduleDto> schedules = therapistScheduleService.getDailySchedule(therapistCode, targetDate);

        // 4. Add to model
        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedDate", targetDate);

        return "spa/therapist-schedule";
    }
}
