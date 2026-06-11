package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
@RequestMapping("/packages")
@RequiredArgsConstructor
public class RetreatPackageController {

    private final RetreatPackageService retreatPackageService;

    @GetMapping
    public String listPackages(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "durationDays", required = false) Integer durationDays,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            Model model) {

        List<RetreatPackageDTO> packages = retreatPackageService.searchPackages(
                type,
                durationDays,
                priceRange);

        model.addAttribute("packages", packages);
        model.addAttribute("types", retreatPackageService.getAllActivePackageTypes());

        model.addAttribute("selectedType", type == null || type.trim().isEmpty() ? "All" : type);
        model.addAttribute("selectedDurationDays", durationDays);
        model.addAttribute("selectedPriceRange", priceRange);

        if (packages.isEmpty()) {
            model.addAttribute("popularPackages", retreatPackageService.getPopularPackages());
        }

        return "booking/packages";
    }

    @GetMapping("/{id}")
    public String packageDetail(@PathVariable("id") Integer id, Model model) {
        RetreatPackageDTO retreatPackage = retreatPackageService.getPackageById(id);
        model.addAttribute("pkg", retreatPackage);
        return "booking/package-detail";
    }
}
