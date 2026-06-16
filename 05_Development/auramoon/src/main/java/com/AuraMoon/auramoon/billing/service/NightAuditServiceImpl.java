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
}
