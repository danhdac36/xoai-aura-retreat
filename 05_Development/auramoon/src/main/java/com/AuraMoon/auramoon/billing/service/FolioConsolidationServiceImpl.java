package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FolioConsolidationServiceImpl implements IFolioConsolidationService {

    @Autowired
    private GuestFolioRepository guestFolioRepository;

    @Autowired
    private FolioItemRepository folioItemRepository;

    @Autowired
    @Lazy
    private FolioConsolidationServiceImpl self; // self-injection to invoke @Transactional methods

    @Override
    public NightAuditResultDTO consolidateAllActiveFolios(Integer actorId) {
        NightAuditResultDTO result = new NightAuditResultDTO();

        // 1. Lấy tất cả Folio đang OPEN
        List<GuestFolio> activeFolios = guestFolioRepository.findByStatus("OPEN");

        int successCount = 0;
        BigDecimal totalSpa = BigDecimal.ZERO;
        BigDecimal totalFnb = BigDecimal.ZERO;

        // 2. Loop qua từng Folio
        for (GuestFolio folio : activeFolios) {
            try {
                // Sử dụng self-injection để đảm bảo transaction nội bộ hoạt động độc lập cho từng folio
                if (self != null) {
                    self.consolidateSingleFolio(folio, actorId, result);
                } else {
                    this.consolidateSingleFolio(folio, actorId, result);
                }
                successCount++;
            } catch (Exception e) {
                // Log lỗi và tiếp tục với folio tiếp theo (E3 - Skip lỗi từng Folio)
                System.err.println("Error consolidating Folio ID " + folio.getId() + ": " + e.getMessage());
            }
        }

        result.setTotalActiveFolios(successCount);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void consolidateSingleFolio(GuestFolio folio, Integer actorId, NightAuditResultDTO globalResult) {
        // Gom F&B
        int fnbRecords = folioItemRepository.consolidateFnbCharges(folio.getBookingId(), folio.getId(), actorId);
        
        // Gom Spa
        int spaRecords = folioItemRepository.consolidateSpaCharges(folio.getBookingId(), folio.getId(), actorId);

        // Tính tổng tiền các charge mới hôm nay
        BigDecimal fnbAmount = folioItemRepository.sumChargesByFolioAndCategoryToday(folio.getId(), "F_AND_B");
        BigDecimal spaAmount = folioItemRepository.sumChargesByFolioAndCategoryToday(folio.getId(), "SPA");

        // Cập nhật vào globalResult (Thread-safety không phải lo vì chạy batch single thread)
        if (fnbAmount != null && fnbAmount.compareTo(BigDecimal.ZERO) > 0) {
            globalResult.setFnbChargesPosted(globalResult.getFnbChargesPosted().add(fnbAmount));
        }
        if (spaAmount != null && spaAmount.compareTo(BigDecimal.ZERO) > 0) {
            globalResult.setSpaChargesPosted(globalResult.getSpaChargesPosted().add(spaAmount));
        }

        // Cập nhật lại total_extra_fb cho GUEST_FOLIO
        BigDecimal newFnb = fnbAmount == null ? BigDecimal.ZERO : fnbAmount;
        BigDecimal newSpa = spaAmount == null ? BigDecimal.ZERO : spaAmount;
        BigDecimal addedAmount = newFnb.add(newSpa);

        if (addedAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentExtra = folio.getTotalExtraFb() == null ? BigDecimal.ZERO : folio.getTotalExtraFb();
            folio.setTotalExtraFb(currentExtra.add(addedAmount));
            guestFolioRepository.save(folio);
            
            globalResult.setGrandTotalRevenue(globalResult.getGrandTotalRevenue().add(addedAmount));
        }
    }
}
