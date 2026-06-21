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
            @RequestParam(value = "minDays", required = false) Integer minDays,
            @RequestParam(value = "maxDays", required = false) Integer maxDays,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            Model model){

        List<RetreatPackageDTO> packages = retreatPackageService.searchPackages(
                type,
                minDays,
                maxDays,
                minPrice,
                maxPrice);

        model.addAttribute("packages", packages);
        model.addAttribute("types", retreatPackageService.getAllActivePackageTypes());

        model.addAttribute("selectedType", type == null || type.trim().isEmpty() ? "All" : type);
        model.addAttribute("minDays", minDays != null ? minDays : 2);
        model.addAttribute("maxDays", maxDays != null ? maxDays : 7);
        model.addAttribute("minPrice", minPrice != null ? minPrice : 10000000.0);
        model.addAttribute("maxPrice", maxPrice != null ? maxPrice : 50000000.0);

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
