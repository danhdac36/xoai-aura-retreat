package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.dto.CheckoutCompletedEvent;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.billing.service.IEmailNotificationService;
import com.AuraMoon.auramoon.billing.service.IPdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InvoiceEmailListener {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEmailListener.class);

    private final IPdfGeneratorService pdfGeneratorService;
    private final IEmailNotificationService emailNotificationService;
    private final AuditLogRepository auditLogRepository;

    public InvoiceEmailListener(IPdfGeneratorService pdfGeneratorService,
            IEmailNotificationService emailNotificationService,
            AuditLogRepository auditLogRepository) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.emailNotificationService = emailNotificationService;
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    @EventListener
    public void handleCheckoutEvent(CheckoutCompletedEvent event) {
        log.info("Received CheckoutCompletedEvent for Booking ID: {}", event.getBookingId());

        if (event.getGuestEmail() == null || event.getGuestEmail().isEmpty()) {
            log.warn("MSG-22: Guest does not have a valid email. Paper invoice required at front desk.");
            logEvent("EMAIL_INVOICE", "MSG-22: No Email", event.getBookingId());
            return;
        }

        try {
            byte[] pdfContent = pdfGeneratorService.generateConsolidatedInvoice(event.getFolioId());
            emailNotificationService.sendInvoiceEmail(event.getGuestEmail(), event.getFolioId(), pdfContent);
            logEvent("EMAIL_INVOICE", "SUCCESS", event.getBookingId());
        } catch (Exception e) {
            log.error("MSG-23: SMTP / Email dispatch error for Booking ID: {}", event.getBookingId(), e);
            logEvent("EMAIL_INVOICE", "MSG-23: " + e.getMessage(), event.getBookingId());
        }
    }

    private void logEvent(String actionType, String status, Integer bookingId) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActionType(actionType);
        auditLog.setDetails(status); // Store status in details
        auditLog.setTargetId(bookingId); // Store bookingId in targetId
        auditLog.setActorId(0); // 0 = System
        // Save to DB
        auditLogRepository.save(auditLog);
    }
}
