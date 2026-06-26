package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.service.IEmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationServiceImpl implements IEmailNotificationService {

    private final JavaMailSender javaMailSender;

    public EmailNotificationServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendInvoiceEmail(String toEmail, byte[] pdfAttachment) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Consolidated Invoice - Xoai Aura Retreat");
        helper.setText(
                "Dear Guest,\n\nThank you for your stay at Xoai Aura Retreat. Please find attached your consolidated invoice.\n\nBest regards,\nXoai Aura Retreat Team");

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
