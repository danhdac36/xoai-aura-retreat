package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import com.AuraMoon.auramoon.booking.dto.RetreatPackageItineraryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetreatPackageServiceImpl implements RetreatPackageService {

    private final RetreatPackageRepository retreatPackageRepository;

    @Override
    public List<RetreatPackageDTO> getAllActivePackages() {
        return retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RetreatPackageDTO> getPackagesByType(String typePackage) {
        return retreatPackageRepository.findByTypePackageAndIsActiveTrueAndIsDeleteFalse(typePackage)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllActivePackageTypes() {
        return retreatPackageRepository.findDistinctTypePackageByIsActiveTrue();
    }

    private RetreatPackageDTO convertToDTO(RetreatPackage retreatPackage) {
        List<RetreatPackageItineraryDTO> itineraryDTOs = retreatPackage.getItineraries() != null ?
                retreatPackage.getItineraries().stream()
                        .map(i -> RetreatPackageItineraryDTO.builder()
                                .dayNumber(i.getDayNumber())
                                .activityName(i.getActivityName())
                                .serviceCode(i.getServiceCode())
                                .location(i.getLocation())
                                .description(i.getDescription())
                                .build())
                        .collect(Collectors.toList()) : null;

        return RetreatPackageDTO.builder()
                .id(retreatPackage.getId())
                .typePackage(retreatPackage.getTypePackage())
                .packageName(retreatPackage.getPackageName())
                .durationDays(retreatPackage.getDurationDays())
                .description(retreatPackage.getDescription())
                .price(retreatPackage.getPrice())
                .itineraries(itineraryDTOs)
                .build();
    }

    @Override
    public List<RetreatPackageDTO> searchPackages(String typePackage, Integer minDays, Integer maxDays, Double minPrice, Double maxPrice) {
        return retreatPackageRepository.searchPackages(typePackage, minDays, maxDays, minPrice, maxPrice)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public RetreatPackageDTO getPackageById(Integer id) {
        RetreatPackage retreatPackage = retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Retreat package not found"));

        return convertToDTO(retreatPackage);
    }

    @Override
    public List<RetreatPackageDTO> getPopularPackages() {
        return retreatPackageRepository.findTop3ByIsActiveTrueAndIsDeleteFalseOrderByIdAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
