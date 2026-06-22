package com.AuraMoon.auramoon.housekeeping.service;

import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class HousekeepingServiceImpl implements IHousekeepingService {

    private final VillaRepository villaRepository;
    private final AuditLogRepository auditLogRepository;

    public HousekeepingServiceImpl(VillaRepository villaRepository,
                                   AuditLogRepository auditLogRepository) {
        this.villaRepository = villaRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<Villa> getDirtyAndCleaningVillas() {
        return villaRepository.findByCleaningStatusInAndIsDeleteFalse(
                Arrays.asList("DIRTY", "CLEANING"));
    }

    @Override
    @Transactional
    public void assignHousekeeper(Integer villaId, String keeperName, Integer actorId) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new RuntimeException("Villa not found with id: " + villaId));

        if (!"DIRTY".equals(villa.getCleaningStatus())) {
            throw new IllegalStateException(
                    "Cannot assign housekeeper. Villa cleaning status must be DIRTY, current: " + villa.getCleaningStatus());
        }

        villa.setCleaningStatus("CLEANING");
        villaRepository.save(villa);

        auditLogRepository.saveAuditLog("HOUSEKEEPING_ASSIGN", actorId,
                "Villa " + villa.getVillaCode() + " assigned to " + keeperName);
    }

    @Override
    @Transactional
    public void approveAndUpdateToClean(Integer villaId, Integer actorId) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new RuntimeException("Villa not found with id: " + villaId));

        if ("CLEAN".equals(villa.getCleaningStatus())) {
            throw new IllegalStateException("Villa is already clean.");
        }

        villa.setCleaningStatus("CLEAN");
        villa.setVillaStatus("AVAILABLE");
        villaRepository.save(villa);

        auditLogRepository.saveAuditLog("HOUSEKEEPING_APPROVE", actorId,
                "Villa " + villa.getVillaCode() + " approved clean");
    }

    @Override
    @Transactional
    public void rejectCleaning(Integer villaId, Integer actorId) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new RuntimeException("Villa not found with id: " + villaId));

        villa.setCleaningStatus("DIRTY");
        villaRepository.save(villa);

        auditLogRepository.saveAuditLog("HOUSEKEEPING_REJECT", actorId,
                "Villa " + villa.getVillaCode() + " rejected. Re-clean required.");
    }
}
