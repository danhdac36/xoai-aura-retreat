package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import com.AuraMoon.auramoon.masterdata.service.VillaTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VillaTypeMasterServiceImpl implements VillaTypeMasterService {

    private final VillaTypeRepository villaTypeRepository;

    @Override
    public List<VillaType> getAllVillaTypes() {
        return villaTypeRepository.findAll();
    }

    @Override
    public VillaType getVillaTypeById(Integer id) {
        return villaTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loại Villa không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public VillaType createVillaType(VillaType villaType) {
        villaType.setIsDelete(false);
        return villaTypeRepository.save(villaType);
    }

    @Override
    @Transactional
    public VillaType updateVillaType(Integer id, VillaType villaType) {
        VillaType existing = getVillaTypeById(id);
        existing.setTypeName(villaType.getTypeName());
        existing.setImage(villaType.getImage());
        existing.setPricePerDay(villaType.getPricePerDay());
        return villaTypeRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteVillaType(Integer id) {
        VillaType existing = getVillaTypeById(id);
        existing.setIsDelete(true);
        villaTypeRepository.save(existing);
    }
}
