package com.AuraMoon.auramoon.hr.controller;

import com.AuraMoon.auramoon.hr.service.IStaffProfileAggregator;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StaffProfileController {

    @Autowired
    private IStaffProfileAggregator aggregator;

    @GetMapping("/manager/staff/profile/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String viewStaffProfile(@PathVariable Long id, Model model) {
        FullStaffProfileDTO profile = aggregator.getAggregatedProfile(id);
        model.addAttribute("profile", profile);
        return "manager/staff-profile";
    }
}
