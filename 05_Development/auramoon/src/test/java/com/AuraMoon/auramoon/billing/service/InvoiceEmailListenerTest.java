package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.CheckoutCompletedEvent;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.billing.service.impl.InvoiceEmailListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceEmailListenerTest {

    @Mock
    private IPdfGeneratorService pdfGeneratorService;

    @Mock
    private IEmailNotificationService emailNotificationService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private InvoiceEmailListener listener;

    // MOD5-TC-003: Dừng gửi Email khi khách không có Email (MSG-22)
    @Test
    public void handleCheckoutEvent_NullEmail_StopsAndLogsMsg22() throws Exception {
        CheckoutCompletedEvent event = new CheckoutCompletedEvent(this, 1001, null, 2001);

        listener.handleCheckoutEvent(event);

        verify(emailNotificationService, never()).sendInvoiceEmail(any(), anyInt(), any());
        verify(auditLogRepository, times(1))
                .save(argThat(log -> "MSG-22: No Email".equals(log.getDetails()) && log.getTargetId().equals(1001)));
    }

    // MOD5-TC-004: Xử lý an toàn khi SMTP lỗi (MSG-23)
    @Test
    public void handleCheckoutEvent_SmtpError_CatchesExceptionAndLogsMsg23() throws Exception {
        CheckoutCompletedEvent event = new CheckoutCompletedEvent(this, 1001, "test@qa.com", 2001);
        byte[] dummyPdf = new byte[] { 1, 2 };

        when(pdfGeneratorService.generateConsolidatedInvoice(2001)).thenReturn(dummyPdf);
        doThrow(new RuntimeException("SMTP Failure")).when(emailNotificationService).sendInvoiceEmail("test@qa.com", 2001, dummyPdf);

        listener.handleCheckoutEvent(event);

        verify(auditLogRepository, times(1))
                .save(argThat(log -> log.getDetails().contains("MSG-23") && log.getTargetId().equals(1001)));
    }
}
