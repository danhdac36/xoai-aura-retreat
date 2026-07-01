package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.masterdata.dto.RetreatItineraryDto;
import com.AuraMoon.auramoon.masterdata.service.RetreatPackageMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetreatPackageMasterServiceImpl implements RetreatPackageMasterService {

    private final RetreatPackageRepository retreatPackageRepository;

    @Override
    public List<RetreatPackage> getAllRetreatPackages() {
        return retreatPackageRepository.findAll();
    }

    @Override
    public RetreatPackage getRetreatPackageById(Integer id) {
        return retreatPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gói nghỉ dưỡng không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public RetreatPackage createRetreatPackage(RetreatPackage retreatPackage) {
        retreatPackage.setIsDelete(false);
        return retreatPackageRepository.save(retreatPackage);
    }

    @Override
    @Transactional
    public RetreatPackage updateRetreatPackage(Integer id, RetreatPackage retreatPackage) {
        RetreatPackage existing = getRetreatPackageById(id);
        existing.setTypePackage(retreatPackage.getTypePackage());
        existing.setPackageName(retreatPackage.getPackageName());
        existing.setDurationDays(retreatPackage.getDurationDays());
        existing.setDescription(retreatPackage.getDescription());
        existing.setIsActive(retreatPackage.getIsActive());
        existing.setPrice(retreatPackage.getPrice());
        return retreatPackageRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRetreatPackage(Integer id) {
        RetreatPackage existing = getRetreatPackageById(id);
        existing.setIsDelete(true);
        retreatPackageRepository.save(existing);
    }

    @Override
    public RetreatItineraryDto parseDescription(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return RetreatItineraryDto.builder()
                    .generalDescription("")
                    .dailyActivities(new ArrayList<>())
                    .build();
        }

        String[] parts = raw.split("\\[DAY\\]");
        String generalDescription = parts[0].trim();
        List<String> dailyActivities = new ArrayList<>();
        
        for (int i = 1; i < parts.length; i++) {
            dailyActivities.add(parts[i].trim());
        }

        return RetreatItineraryDto.builder()
                .generalDescription(generalDescription)
                .dailyActivities(dailyActivities)
                .build();
    }
}
