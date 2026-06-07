package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Chú ý: Dùng Controller, không dùng RestController
import org.springframework.ui.Model; // Dùng Model của Spring UI
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/spa/therapists") // Đổi link một chút cho giống giao diện web
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * Trả về giao diện web xem lịch của Therapist
     */
    @GetMapping("/{code}/schedules")
    public String getDailySchedule(
            @PathVariable("code") String therapistCode,
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model) {
        
        // Nếu người dùng không chọn ngày, mặc định lấy ngày hôm nay
        if (date == null) {
            date = LocalDate.now();
        }

        // 1. Lấy dữ liệu từ Service
        List<Schedule> schedules = scheduleService.getScheduleForTherapist(therapistCode, date);
        
        // 2. Ném dữ liệu vào Model để gửi sang file HTML (Thymeleaf/JSP)
        model.addAttribute("schedules", schedules);
        model.addAttribute("therapistCode", therapistCode);
        model.addAttribute("selectedDate", date);
        
        // 3. Trả về tên của file HTML giao diện (nằm trong thư mục src/main/resources/templates/spa/)
        return "therapist_schedule";
    }
}