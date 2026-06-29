package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.service.IManagerReviewService;
import com.AuraMoon.auramoon.billing.service.IAuditLogService;
import com.AuraMoon.auramoon.common.enums.AuditLogActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    @Autowired
    private IManagerReviewService managerReviewService;
    
    @Autowired
    private IAuditLogService auditLogService;

    @PostMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public String hideReview(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        managerReviewService.hideReview(id);
        auditLogService.logActivity(AuditLogActionType.HIDE_REVIEW.name(), 1, id.intValue());
        redirectAttributes.addFlashAttribute("message", "Review hidden successfully");
        return "redirect:/manager/reviews";
    }
}
