package com.AuraMoon.auramoon.billing.service;

public interface IPdfGeneratorService {
    /**
     * Kết xuất Hóa đơn gộp theo chuẩn Bộ Tài chính (BR-21).
     * Bắt buộc hiển thị: Logo, Tên công ty, MST, Bảng chi tiết,
     * VAT 10% tách riêng, Tổng tiền bằng chữ.
     * @param folioId ID của Guest Folio
     * @return byte[] chứa nội dung PDF
     */
    byte[] generateConsolidatedInvoice(Integer folioId) throws Exception;
}
