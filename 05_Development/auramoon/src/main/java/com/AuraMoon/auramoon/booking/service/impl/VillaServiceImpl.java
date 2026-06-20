package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VillaServiceImpl implements VillaService {

    private final VillaRepository villaRepository;

    private static final java.util.logging.Logger auditLogger =
            java.util.logging.Logger.getLogger(VillaServiceImpl.class.getName());

    // Các trạng thái hợp lệ theo UC09 EDS
    private static final Set<String> VALID_VILLA_STATUSES = Set.of("AVAILABLE", "OCCUPIED", "MAINTENANCE");
    private static final Set<String> VALID_CLEANING_STATUSES = Set.of("CLEAN", "DIRTY");

    @Override
    public boolean checkVillaAvailability(Integer villaTypeId, LocalDate checkinDate, LocalDate checkoutDate) {
        // Kiểm tra xem có Villa nào thuộc loại villaTypeId đang ở trạng thái AVAILABLE và chưa bị xóa hay không
        List<Villa> availableVillas = villaRepository.findByVillaType_IdAndVillaStatusAndIsDeleteFalse(villaTypeId, "AVAILABLE");
        return !availableVillas.isEmpty();
    }

    @Override
    @Transactional
    public void updateVillaStatuses(Integer villaId, String villaStatus, String cleaningStatus) {
        // 1. Validate Input (BR-05/ADR-002)
        if (!VALID_VILLA_STATUSES.contains(villaStatus)) {
            throw new IllegalArgumentException("[VILLA-400] Trạng thái Villa không hợp lệ: " + villaStatus);
        }
        if (!VALID_CLEANING_STATUSES.contains(cleaningStatus)) {
            throw new IllegalArgumentException("[VILLA-400] Trạng thái dọn dẹp không hợp lệ: " + cleaningStatus);
        }

        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new IllegalArgumentException("[VILLA-404] Không tìm thấy Villa với id: " + villaId));

        String oldVillaStatus = villa.getVillaStatus();
        String oldCleaningStatus = villa.getCleaningStatus();

        // 2. Validate State Machine (Ngăn chuyển trực tiếp từ OCCUPIED sang MAINTENANCE)
        if ("OCCUPIED".equals(oldVillaStatus) && "MAINTENANCE".equals(villaStatus)) {
            throw new IllegalStateException("[VILLA-409] Không thể chuyển Villa đang có khách (OCCUPIED) sang bảo trì (MAINTENANCE). Khách phải check-out trước.");
        }

        // Ngăn chuyển từ OCCUPIED sang AVAILABLE + CLEAN (phải qua DIRTY)
        if ("OCCUPIED".equals(oldVillaStatus) && "AVAILABLE".equals(villaStatus) && "CLEAN".equals(cleaningStatus)) {
            throw new IllegalStateException("[VILLA-409] Villa sau khi khách check-out phải ở trạng thái DIRTY trước khi được dọn dẹp.");
        }

        villa.setVillaStatus(villaStatus);
        villa.setCleaningStatus(cleaningStatus);
        villaRepository.save(villa);

        // 3. Audit Logging (BR-15)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = (auth != null && auth.getName() != null) ? auth.getName() : "SYSTEM";
        auditLogger.info(String.format(
                "AUDIT LOG: [Villa Status Updated] | VillaCode: %s | Status: %s -> %s | Cleaning: %s -> %s | Performed by: %s | Timestamp: %s",
                villa.getVillaCode(), oldVillaStatus, villaStatus, oldCleaningStatus, cleaningStatus, performedBy, Instant.now()
        ));
    }
}

