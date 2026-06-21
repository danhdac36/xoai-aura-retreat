package com.AuraMoon.auramoon.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditHistoryDTO {
    private LocalDateTime date;
    private String status; // SUCCESS or ERROR
    private String operator; // SYSTEM or User
    private String recordsText; // e.g., "118 Items"
    private String totalAmountText; // e.g., "68,000,000₫"
}
