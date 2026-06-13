package com.AuraMoon.auramoon.billing.dto;

import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CheckoutViewDTO {
    private GuestFolio folio;
    
    // Lịch sử thanh toán
    private List<Payment> payments; 
    private BigDecimal totalPaid; // TỔNG ĐÃ THANH TOÁN (Từ bảng Payment)
    
    // Dịch vụ phát sinh (Gom nhóm tự động linh hoạt theo serviceCategory)
    private Map<String, List<FolioItem>> groupedExtraServices; 
    
    private BigDecimal totalCost; // TỔNG CHI PHÍ = Tiền gói + Tổng Folio Items
    private BigDecimal balanceDue; // CẦN THANH TOÁN THÊM = Total Cost - Total Paid
}
