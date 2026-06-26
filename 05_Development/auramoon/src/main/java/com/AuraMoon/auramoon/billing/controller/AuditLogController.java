package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.service.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/audit")
public class AuditLogController {

    @Autowired
    private IAuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String renderDashboard(org.springframework.ui.Model model) {
        org.springframework.data.domain.Page<com.AuraMoon.auramoon.billing.dto.AuditLogDTO> logs = auditLogService.getLogs(null, 0, 50);
        model.addAttribute("logsPage", logs != null ? logs : org.springframework.data.domain.Page.empty());
        return "admin/audit";
    }
}
