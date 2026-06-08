package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;

import java.util.List;

public interface RetreatPackageService {
    List<RetreatPackageDTO> getAllActivePackages();
    List<RetreatPackageDTO> getPackagesByType(String typePackage);
    List<String> getAllActivePackageTypes();
}
