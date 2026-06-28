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

import org.thymeleaf.TemplateEngine;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.auth.entity.User;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    // MOD5-TC-002: Gửi Email đính kèm mảng byte thành công
    @Test
    public void sendInvoiceEmail_Success() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        GuestFolio mockFolio = new GuestFolio();
        mockFolio.setBookingId(1);
        Booking mockBooking = new Booking();
        mockBooking.setGuestId(1);
        User mockUser = new User();
        mockUser.setEmail("test@qa.com");

        when(guestFolioRepository.findById(1)).thenReturn(Optional.of(mockFolio));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(mockBooking));
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(templateEngine.process(anyString(), any())).thenReturn("<html></html>");

        byte[] dummyPdf = new byte[] { 1, 2, 3 };
        emailNotificationService.sendInvoiceEmail("test@qa.com", 1, dummyPdf);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}
