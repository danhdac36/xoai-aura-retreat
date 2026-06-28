package com.AuraMoon.auramoon.billing.service;

public interface IEmailNotificationService {
    /**
     * Gửi email đính kèm file PDF dưới dạng ByteArrayResource.
     * 
     * @param toEmail       Địa chỉ email khách hàng
     * @param folioId       ID của hóa đơn (GuestFolio)
     * @param pdfAttachment Mảng byte chứa nội dung PDF
     */
    void sendInvoiceEmail(String toEmail, Integer folioId, byte[] pdfAttachment) throws Exception;

    /**
     * Gửi email nhắc lịch hẹn Spa thành công cho khách hàng.
     * 
     * @param toEmail      Địa chỉ email của khách
     * @param guestName    Tên đầy đủ của khách
     * @param serviceName  Tên dịch vụ trị liệu Spa
     * @param roomName     Tên phòng điều trị
     * @param startTime    Thời gian hẹn bắt đầu (định dạng String)
     */
    void sendSpaBookingReminderEmail(String toEmail, String guestName, String serviceName, String roomName, String startTime) throws Exception;
}
