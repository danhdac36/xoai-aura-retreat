package com.AuraMoon.auramoon.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NightAuditDashboardDTO {
    private NightAuditResultDTO kpiCards;
    private List<FolioTransactionDTO> folioItemsList;
    private List<AuditHistoryDTO> auditHistory;
}
