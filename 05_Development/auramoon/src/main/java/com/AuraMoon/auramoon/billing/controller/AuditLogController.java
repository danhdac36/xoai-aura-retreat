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
    public String renderDashboard(
            @org.springframework.web.bind.annotation.RequestParam(value = "search", required = false) String search,
            @org.springframework.web.bind.annotation.RequestParam(value = "page", defaultValue = "0") int page,
            org.springframework.ui.Model model) {
        org.springframework.data.domain.Page<com.AuraMoon.auramoon.billing.dto.AuditLogDTO> logs = auditLogService.getLogs(search, page, 5);
        model.addAttribute("logsPage", logs != null ? logs : org.springframework.data.domain.Page.empty());
        model.addAttribute("currentSearch", search);
        return "admin/audit";
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<String> getAuditLogDetails(@org.springframework.web.bind.annotation.PathVariable("id") int id) {
        String details = auditLogService.getDetailsById(id);
        return org.springframework.http.ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(details != null ? details : "{}");
    }
}
