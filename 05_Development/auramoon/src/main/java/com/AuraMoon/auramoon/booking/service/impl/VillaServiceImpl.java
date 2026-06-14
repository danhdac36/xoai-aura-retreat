package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VillaServiceImpl implements VillaService {

    private final VillaRepository villaRepository;

    @Override
    public boolean checkVillaAvailability(Integer villaTypeId, LocalDate checkinDate, LocalDate checkoutDate) {
        // Kiểm tra xem có Villa nào thuộc loại villaTypeId đang ở trạng thái AVAILABLE và chưa bị xóa hay không
        List<Villa> availableVillas = villaRepository.findByVillaType_IdAndVillaStatusAndIsDeleteFalse(villaTypeId, "AVAILABLE");
        return !availableVillas.isEmpty();
    }

    @Override
    public void updateVillaStatuses(Integer villaId, String villaStatus, String cleaningStatus) {
        Villa villa = villaRepository.findById(villaId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Villa với id: " + villaId));
        villa.setVillaStatus(villaStatus);
        villa.setCleaningStatus(cleaningStatus);
        villaRepository.save(villa);
    }
}

