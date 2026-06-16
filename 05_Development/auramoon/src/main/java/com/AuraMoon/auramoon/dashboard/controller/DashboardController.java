package com.AuraMoon.auramoon.dashboard.controller;

import com.AuraMoon.auramoon.dashboard.dto.RevenueDashboardDTO;
import com.AuraMoon.auramoon.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller("managerDashboardController")
@RequestMapping("/manager")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String showDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String category,
            Model model) {
        // Authorization check is delegated to Module 1 (SessionInterceptor/Global Auth)
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1); // Default to start of current month
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        RevenueDashboardDTO dashboardData;
        try {
            dashboardData = dashboardService.getDashboardData(startDate, endDate, category);
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Database error", e);
        }

        model.addAttribute("data", dashboardData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("category", category);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("this is " + auth.getAuthorities());
        return "manager/dashboard";
    }
}
