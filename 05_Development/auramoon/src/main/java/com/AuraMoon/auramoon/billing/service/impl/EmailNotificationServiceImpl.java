package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.service.IEmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationServiceImpl implements IEmailNotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final GuestFolioRepository guestFolioRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public EmailNotificationServiceImpl(JavaMailSender javaMailSender,
                                        TemplateEngine templateEngine,
                                        GuestFolioRepository guestFolioRepository,
                                        BookingRepository bookingRepository,
                                        UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.guestFolioRepository = guestFolioRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendInvoiceEmail(String toEmail, Integer folioId, byte[] pdfAttachment) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Consolidated Invoice - Aura Moon Resort");

        // Fetch Data
        GuestFolio folio = guestFolioRepository.findById(folioId)
                .orElseThrow(() -> new Exception("Folio not found"));
        Booking booking = bookingRepository.findById(folio.getBookingId())
                .orElseThrow(() -> new Exception("Booking not found"));
        User guest = userRepository.findById(booking.getGuestId())
                .orElseThrow(() -> new Exception("Guest not found"));

        // Setup Thymeleaf Context
        Context context = new Context();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        context.setVariable("guestName", guest.getFullName() != null ? guest.getFullName() : guest.getEmail());
        context.setVariable("checkInDate", booking.getCheckinDate() != null ? booking.getCheckinDate().format(formatter) : "");
        context.setVariable("checkOutDate", booking.getCheckoutDate() != null ? booking.getCheckoutDate().format(formatter) : "");
        
        // Use basic fallback if null
        BigDecimal totalRoom = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
        BigDecimal totalDining = folio.getTotalExtraFb() != null ? folio.getTotalExtraFb() : BigDecimal.ZERO;
        // Since GuestFolio doesn't store Spa, we just subtract room and f&b from final to estimate or leave 0.
        // As per user "thông tin dịch vụ thì tùy thuộc vào khách hàng sử dụng thì okey", we will just provide dummy or basic calculations.
        BigDecimal totalSpa = BigDecimal.ZERO;
        BigDecimal totalAmount = folio.getFinalAmount() != null ? folio.getFinalAmount() : BigDecimal.ZERO;

        context.setVariable("totalRoom", totalRoom);
        context.setVariable("totalSpa", totalSpa);
        context.setVariable("totalDining", totalDining);
        context.setVariable("totalAmount", totalAmount);

        // Process HTML
        String htmlContent = templateEngine.process("invoice-template", context);

        helper.setText(htmlContent, true);

        helper.addAttachment("Invoice.pdf", new ByteArrayResource(pdfAttachment));

        javaMailSender.send(message);
    }

     @Override
     public void sendSpaBookingReminderEmail(String toEmail, String guestName, String serviceName, String roomName, String startTime) throws Exception {
         MimeMessage message = javaMailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

         helper.setTo(toEmail);
         helper.setSubject("[Xoai Aura Retreat] Nhắc lịch hẹn Spa của bạn");

         String content = "Kính gửi quý khách " + guestName + ",\n\n" +
                 "Xoài Aura Retreat xin thông báo lịch hẹn Spa của quý khách đã được sắp xếp thành công:\n\n" +
                 "- Dịch vụ: " + serviceName + "\n" +
                 "- Phòng trị liệu: " + roomName + "\n" +
                 "- Thời gian bắt đầu: " + startTime + "\n\n" +
                 "Quý khách vui lòng đến đúng giờ để có trải nghiệm thư giãn tốt nhất.\n\n" +
                 "Trân trọng,\n" +
                 "Đội ngũ Xoài Aura Retreat";

         helper.setText(content);

         javaMailSender.send(message);
     }
}
