package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import java.util.List;

public interface RetreatPackageService {

    List<RetreatPackageDTO> getAllActivePackages();

    List<RetreatPackageDTO> getPackagesByType(String typePackage);

    List<String> getAllActivePackageTypes();

    List<RetreatPackageDTO> searchPackages(String typePackage, Integer minDays, Integer maxDays, Double minPrice, Double maxPrice);

    RetreatPackageDTO getPackageById(Integer id);

    List<RetreatPackageDTO> getPopularPackages();
}