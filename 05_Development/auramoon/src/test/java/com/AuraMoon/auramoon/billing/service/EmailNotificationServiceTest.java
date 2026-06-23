package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.service.impl.EmailNotificationServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    // MOD5-TC-002: Gửi Email đính kèm mảng byte thành công
    @Test
    public void sendInvoiceEmail_Success() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        byte[] dummyPdf = new byte[] { 1, 2, 3 };
        emailNotificationService.sendInvoiceEmail("test@qa.com", dummyPdf);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}
