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
public class NightAuditResultDTO {
    private int totalActiveFolios;
    @Builder.Default
    private BigDecimal spaChargesPosted = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal fnbChargesPosted = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal grandTotalRevenue = BigDecimal.ZERO;
}
