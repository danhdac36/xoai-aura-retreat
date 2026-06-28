package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.service.IManagerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerReviewController {

    @Autowired
    private IManagerReviewService managerReviewService;

    @GetMapping("/manager/reviews")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String renderReviewDashboard(Model model) {
        model.addAttribute("reviews", managerReviewService.getVisibleReviews());
        Object metrics = managerReviewService.getMetrics();
        model.addAttribute("metrics", metrics != null ? metrics : java.util.Map.of("totalReviews", 0, "averageRating", "0.0"));
        return "manager/reviews";
    }

    @org.springframework.web.bind.annotation.PostMapping("/manager/reviews/hide/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String hideReview(@org.springframework.web.bind.annotation.PathVariable long id) {
        managerReviewService.hideReview(id);
        return "redirect:/manager/reviews";
    }
}
