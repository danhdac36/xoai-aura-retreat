package com.AuraMoon.auramoon.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolioTransactionDTO {
    private String guestName;
    private String bookingId; // Hiển thị chuỗi format, ví dụ #BK-102
    private String category;
    private BigDecimal amount;
}
