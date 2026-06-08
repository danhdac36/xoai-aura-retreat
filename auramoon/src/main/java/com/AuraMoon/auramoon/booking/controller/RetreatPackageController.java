package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/packages")
@RequiredArgsConstructor
public class RetreatPackageController {

    private final RetreatPackageService retreatPackageService;

    @GetMapping
    public String listPackages(@RequestParam(value = "type", required = false) String type, Model model) {
        List<RetreatPackageDTO> packages;
        if (type != null && !type.trim().isEmpty()) {
            packages = retreatPackageService.getPackagesByType(type);
            model.addAttribute("selectedType", type);
        } else {
            packages = retreatPackageService.getAllActivePackages();
            model.addAttribute("selectedType", "All");
        }

        model.addAttribute("packages", packages);
        model.addAttribute("types", retreatPackageService.getAllActivePackageTypes());
        return "booking/packages";
    }
}
