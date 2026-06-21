package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;
import com.AuraMoon.auramoon.billing.exception.NightAuditAlreadyExecutedException;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class NightAuditServiceImpl implements INightAuditService {

    @Autowired
    private IFolioConsolidationService folioConsolidationService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public NightAuditResultDTO executeAudit(String triggerType, Integer actorId) {
        // 1. Kiểm tra E1: Đã chạy trong ngày hôm nay chưa
        boolean alreadyRun = auditLogRepository.existsByActionTypeAndTimestampDate(triggerType, LocalDate.now());
        if (alreadyRun) {
            throw new NightAuditAlreadyExecutedException("Night Audit has already been completed for this date.");
        }

        // 2. Chạy quá trình gom dữ liệu (Consolidation)
        NightAuditResultDTO result = folioConsolidationService.consolidateAllActiveFolios(actorId);

        // 3. Chuẩn bị nội dung log
        String details;
        if (result.getTotalActiveFolios() == 0 && result.getGrandTotalRevenue().compareTo(BigDecimal.ZERO) == 0) {
            details = "No new charges";
        } else {
            details = String.format("totalActiveFolios: %d, spaChargesPosted: %s, fnbChargesPosted: %s, grandTotalRevenue: %s",
                    result.getTotalActiveFolios(),
                    result.getSpaChargesPosted(),
                    result.getFnbChargesPosted(),
                    result.getGrandTotalRevenue());
        }

        // 4. Lưu log
        auditLogRepository.saveAuditLog(triggerType, actorId, details);

        return result;
    }

    @Override
    public boolean hasRunToday() {
        return auditLogRepository.existsByActionTypeAndTimestampDate("NIGHT_AUDIT_MANUAL", LocalDate.now())
                || auditLogRepository.existsByActionTypeAndTimestampDate("NIGHT_AUDIT_AUTO", LocalDate.now());
    }

    @Autowired
    private com.AuraMoon.auramoon.billing.repository.GuestFolioRepository guestFolioRepository;
    
    @Autowired
    private com.AuraMoon.auramoon.billing.repository.FolioItemRepository folioItemRepository;

    @Override
    public com.AuraMoon.auramoon.billing.dto.NightAuditDashboardDTO getDashboardData() {
        com.AuraMoon.auramoon.billing.dto.NightAuditDashboardDTO dashboardDTO = new com.AuraMoon.auramoon.billing.dto.NightAuditDashboardDTO();
        NightAuditResultDTO kpiCards = new NightAuditResultDTO();
        
        // Kpi Cards: Total active folios
        kpiCards.setTotalActiveFolios((int) guestFolioRepository.countByStatus("OPEN"));
        
        LocalDate today = LocalDate.now();
        if (hasRunToday()) {
            // Already run: get consolidated amounts from FolioItem
            kpiCards.setFnbChargesPosted(folioItemRepository.sumChargesByCategoryAndDate("F_AND_B", today));
            kpiCards.setSpaChargesPosted(folioItemRepository.sumChargesByCategoryAndDate("SPA", today));
        } else {
            // Not run yet: get pending amounts from MealOrder and TreatmentBooking
            kpiCards.setFnbChargesPosted(folioItemRepository.sumPendingFnbCharges());
            kpiCards.setSpaChargesPosted(folioItemRepository.sumPendingSpaCharges());
        }
        
        kpiCards.setGrandTotalRevenue(kpiCards.getFnbChargesPosted().add(kpiCards.getSpaChargesPosted()));
        dashboardDTO.setKpiCards(kpiCards);
        
        // Folio Items List
        java.util.List<Object[]> rawTransactions = folioItemRepository.getRecentFolioTransactions(today);
        java.util.List<com.AuraMoon.auramoon.billing.dto.FolioTransactionDTO> transactions = new java.util.ArrayList<>();
        for (Object[] row : rawTransactions) {
            com.AuraMoon.auramoon.billing.dto.FolioTransactionDTO dto = new com.AuraMoon.auramoon.billing.dto.FolioTransactionDTO();
            dto.setGuestName((String) row[0]);
            dto.setBookingId("#BK-" + row[1]);
            String cat = (String) row[2];
            dto.setCategory(cat != null && cat.equals("F_AND_B") ? "FNB" : cat);
            dto.setAmount((BigDecimal) row[3]);
            transactions.add(dto);
        }
        dashboardDTO.setFolioItemsList(transactions);
        
        // Audit History
        java.util.List<com.AuraMoon.auramoon.billing.entity.AuditLog> logs = auditLogRepository.findTop5ByActionTypeStartingWithOrderByTimestampDesc("NIGHT_AUDIT");
        java.util.List<com.AuraMoon.auramoon.billing.dto.AuditHistoryDTO> history = new java.util.ArrayList<>();
        for (com.AuraMoon.auramoon.billing.entity.AuditLog log : logs) {
            com.AuraMoon.auramoon.billing.dto.AuditHistoryDTO h = new com.AuraMoon.auramoon.billing.dto.AuditHistoryDTO();
            java.time.LocalDateTime dt = new java.sql.Timestamp(log.getTimestamp().getTime()).toLocalDateTime();
            h.setDate(dt);
            h.setStatus("SUCCESS");
            h.setOperator(log.getActorId() != null && log.getActorId() == 0 ? "SYSTEM" : "USER");
            h.setRecordsText("Completed");
            h.setTotalAmountText(log.getDetails());
            history.add(h);
        }
        dashboardDTO.setAuditHistory(history);
        
        return dashboardDTO;
    }
}
