package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.ScheduleDto;
import com.AuraMoon.auramoon.spa.service.ScheduleService;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/spa/therapists")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TherapistRepository therapistRepository;

    @GetMapping("/{code}/schedules")
    public String getDailySchedule(
            @PathVariable("code") String therapistCode,
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        // Gọi List DTO thay vì Entity
        List<ScheduleDto> schedules = scheduleService.getScheduleForTherapist(therapistCode, date);

        // Lấy tên chuyên viên
        String therapistName = therapistRepository.findTherapistNameByCode(therapistCode);
        model.addAttribute("therapistName", therapistName != null ? therapistName : "Không xác định");

        model.addAttribute("schedules", schedules);
        model.addAttribute("therapistCode", therapistCode);
        model.addAttribute("selectedDate", date);
        model.addAttribute("previousDate", date.minusDays(1));
        model.addAttribute("nextDate", date.plusDays(1));

        return "therapist_schedule";
    }

    // API xử lý khi người dùng đổi trạng thái trên giao diện
    @PostMapping("/{code}/schedules/{scheduleId}/status")
    public String updateStatus(
            @PathVariable("code") String therapistCode,
            @PathVariable("scheduleId") Integer scheduleId,
            @RequestParam("status") String status,
            @RequestParam("date") String date) {

        scheduleService.updateScheduleStatus(scheduleId, status);

        // Đổi xong thì load lại trang và giữ nguyên ngày đang xem
        return "redirect:/spa/therapists/" + therapistCode + "/schedules?date=" + date;
    }
}