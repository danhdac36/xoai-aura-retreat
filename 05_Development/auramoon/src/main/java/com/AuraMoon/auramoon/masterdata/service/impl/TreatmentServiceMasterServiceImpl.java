package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.masterdata.service.TreatmentServiceMasterService;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentServiceMasterServiceImpl implements TreatmentServiceMasterService {

    private final TreatmentServiceRepository treatmentServiceRepository;

    @Override
    public List<TreatmentService> getAllTreatmentServices() {
        return treatmentServiceRepository.findAll();
    }

    @Override
    public TreatmentService getTreatmentServiceById(Integer id) {
        return treatmentServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dịch vụ Spa không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public TreatmentService createTreatmentService(TreatmentService treatmentService) {
        treatmentService.setIsDelete(false);
        return treatmentServiceRepository.save(treatmentService);
    }

    @Override
    @Transactional
    public TreatmentService updateTreatmentService(Integer id, TreatmentService treatmentService) {
        TreatmentService existing = getTreatmentServiceById(id);
        existing.setTreatmentCode(treatmentService.getTreatmentCode());
        existing.setServiceName(treatmentService.getServiceName());
        existing.setDurationMinutes(treatmentService.getDurationMinutes());
        existing.setPrice(treatmentService.getPrice());
        existing.setIsAvailable(treatmentService.getIsAvailable());
        return treatmentServiceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTreatmentService(Integer id) {
        TreatmentService existing = getTreatmentServiceById(id);
        existing.setIsDelete(true);
        treatmentServiceRepository.save(existing);
    }
}
