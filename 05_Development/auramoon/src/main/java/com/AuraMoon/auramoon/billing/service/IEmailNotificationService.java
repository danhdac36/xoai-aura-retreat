package com.AuraMoon.auramoon.billing.service;

public interface IEmailNotificationService {
    /**
     * Gửi email đính kèm file PDF dưới dạng ByteArrayResource.
     * 
     * @param toEmail       Địa chỉ email khách hàng
     * @param pdfAttachment Mảng byte chứa nội dung PDF
     */
    void sendInvoiceEmail(String toEmail, byte[] pdfAttachment) throws Exception;
}
