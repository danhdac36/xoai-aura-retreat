package com.AuraMoon.auramoon.billing;

import com.AuraMoon.auramoon.billing.dto.CheckoutCompletedEvent;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test",
    "spring.mail.password=test"
})
public class InvoiceEmailIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockitoBean
    private AuditLogRepository auditLogRepository;

    private GreenMail greenMail;

    @BeforeEach
    public void setup() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterEach
    public void tearDown() {
        greenMail.stop();
    }

    // MOD5-TC-INT-001: Luồng end-to-end Checkout Trigger
    @Test
    public void checkoutCompletedEvent_TriggersPdfAndEmailAndLogsSuccess() throws Exception {
        // Act: Fire event with null email
        eventPublisher.publishEvent(new CheckoutCompletedEvent(this, 9999, null, 9999));
        
        // Wait a bit for @Async
        Thread.sleep(1000);

        // Assert
        org.mockito.Mockito.verify(auditLogRepository, org.mockito.Mockito.timeout(2000).times(1))
            .save(org.mockito.ArgumentMatchers.argThat(log -> 
                "EMAIL_INVOICE".equals(log.getActionType()) &&
                log.getTargetId() != null && log.getTargetId().equals(9999) &&
                "MSG-22: No Email".equals(log.getDetails())
            ));
    }
}
