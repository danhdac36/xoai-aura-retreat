package com.AuraMoon.auramoon.hr.controller;

import com.AuraMoon.auramoon.hr.service.IStaffProfileAggregator;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StaffProfileController {

    @Autowired
    private IStaffProfileAggregator aggregator;

    @GetMapping("/manager/staff/profile/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String viewStaffProfile(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String filter,
            Model model) {
        FullStaffProfileDTO profile = aggregator.getAggregatedProfile(id, page, size, filter);
        if (profile == null) {
            return "redirect:/manager/dashboard";
        }
        model.addAttribute("profile", profile);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("currentPage", page);
        return "manager/staff-profile";
    }
}
