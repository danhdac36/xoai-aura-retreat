package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.masterdata.service.VillaMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VillaMasterServiceImpl implements VillaMasterService {

    private final VillaRepository villaRepository;

    @Override
    public List<Villa> getAllVillas() {
        return villaRepository.findAll();
    }

    @Override
    public Villa getVillaById(Integer id) {
        return villaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Villa không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public Villa createVilla(Villa villa) {
        villa.setIsDelete(false);
        return villaRepository.save(villa);
    }

    @Override
    @Transactional
    public Villa updateVilla(Integer id, Villa villa) {
        Villa existing = getVillaById(id);
        existing.setVillaCode(villa.getVillaCode());
        existing.setVillaType(villa.getVillaType());
        existing.setLimitPerson(villa.getLimitPerson());
        existing.setVillaStatus(villa.getVillaStatus());
        existing.setCleaningStatus(villa.getCleaningStatus());
        existing.setMaintenanceNote(villa.getMaintenanceNote());
        return villaRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteVilla(Integer id) {
        Villa existing = getVillaById(id);
        existing.setIsDelete(true);
        villaRepository.save(existing);
    }
}
