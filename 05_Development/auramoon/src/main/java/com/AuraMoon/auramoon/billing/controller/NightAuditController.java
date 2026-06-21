package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;
import com.AuraMoon.auramoon.billing.exception.NightAuditAlreadyExecutedException;
import com.AuraMoon.auramoon.billing.service.INightAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/billing/night-audit")
public class NightAuditController {

    @Autowired
    private INightAuditService nightAuditService;

    @GetMapping
    public String showDashboard(Model model) {
        model.addAttribute("businessDate", LocalDate.now());
        // For UI purposes, we could query actual historical data here
        // Currently leaving simple placeholders for MVC binding
        return "billing/night-audit-dashboard";
    }

    @PostMapping("/execute")
    public String executeManualAudit(RedirectAttributes redirectAttributes) {
        try {
            // ID cố định cho Manager (trong thực tế lấy từ SecurityContext)
            Integer managerId = 1; 
            NightAuditResultDTO result = nightAuditService.executeAudit("NIGHT_AUDIT_MANUAL", managerId);
            
            // Thêm thông báo thành công (MSG-20)
            redirectAttributes.addFlashAttribute("successMessage", "Night Audit process completed. " 
                + "Processed " + result.getTotalActiveFolios() + " active folios. "
                + "Total revenue consolidated: " + result.getGrandTotalRevenue() + " VND.");
                
        } catch (NightAuditAlreadyExecutedException e) {
            // E1: Đã chạy rồi (BIL-003)
            redirectAttributes.addFlashAttribute("errorMessage", "Night Audit has already been completed for this date.");
        } catch (Exception e) {
            // E2: Lỗi hệ thống (BIL-004) - MSG-21
            redirectAttributes.addFlashAttribute("errorMessage", "Night Audit process failed. Please review the error log and retry manually.");
        }
        
        return "redirect:/billing/night-audit";
    }
}
