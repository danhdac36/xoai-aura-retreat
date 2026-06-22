package com.AuraMoon.auramoon.housekeeping.controller;

import com.AuraMoon.auramoon.housekeeping.service.IHousekeepingService;
import com.AuraMoon.auramoon.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/housekeeping")
public class HousekeepingController {

    private final IHousekeepingService housekeepingService;

    @Autowired
    public HousekeepingController(IHousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
    }

    @GetMapping
    public String dashboard(Model model) {
        var villas = housekeepingService.getDirtyAndCleaningVillas();
        long totalDirty = villas.stream().filter(v -> "DIRTY".equals(v.getCleaningStatus())).count();
        long totalCleaning = villas.stream().filter(v -> "CLEANING".equals(v.getCleaningStatus())).count();
        model.addAttribute("villas", villas);
        model.addAttribute("totalDirty", totalDirty);
        model.addAttribute("totalCleaning", totalCleaning);
        return "manager/housekeeping-dashboard";
    }

    @PostMapping("/assign")
    public String assignHousekeeper(
            @RequestParam("villaId") Integer villaId,
            @RequestParam(value = "keeperName", defaultValue = "Housekeeping Staff") String keeperName,
            @AuthenticationPrincipal User actor,
            RedirectAttributes redirectAttributes) {
        try {
            // Because User implements UserDetails, we can cast/use it directly.
            // Assuming User has getId() method. If it's a CustomUserDetails, we might need a cast.
            // But let's assume actor.getId() works, or we use a fallback if actor is null.
            Integer actorId = (actor != null && actor.getId() != null) ? actor.getId() : 1; 
            housekeepingService.assignHousekeeper(villaId, keeperName, actorId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã phân công dọn dẹp thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi phân công: " + e.getMessage());
        }
        return "redirect:/manager/housekeeping";
    }

    @PostMapping("/approve")
    public String approveClean(
            @RequestParam("villaId") Integer villaId,
            @AuthenticationPrincipal User actor,
            RedirectAttributes redirectAttributes) {
        try {
            Integer actorId = (actor != null && actor.getId() != null) ? actor.getId() : 1;
            housekeepingService.approveAndUpdateToClean(villaId, actorId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã nghiệm thu Villa thành trạng thái sạch.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi nghiệm thu: " + e.getMessage());
        }
        return "redirect:/manager/housekeeping";
    }

    @PostMapping("/reject")
    public String rejectClean(
            @RequestParam("villaId") Integer villaId,
            @AuthenticationPrincipal User actor,
            RedirectAttributes redirectAttributes) {
        try {
            Integer actorId = (actor != null && actor.getId() != null) ? actor.getId() : 1;
            housekeepingService.rejectCleaning(villaId, actorId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối nghiệm thu, yêu cầu dọn lại.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi từ chối: " + e.getMessage());
        }
        return "redirect:/manager/housekeeping";
    }
}
