package com.AuraMoon.auramoon.billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NightAuditScheduledTask {

    @Autowired
    private INightAuditService nightAuditService;

    // Chạy lúc 00:00 hàng ngày
    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyAudit() {
        try {
            // Actor ID = 0 cho SYSTEM
            nightAuditService.executeAudit("NIGHT_AUDIT_AUTO", 0);
            System.out.println("Automated Night Audit completed successfully.");
        } catch (Exception e) {
            System.err.println("Automated Night Audit failed: " + e.getMessage());
        }
    }
}
