package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.masterdata.dto.RetreatItineraryDto;
import java.util.List;

public interface RetreatPackageMasterService {
    List<RetreatPackage> getAllRetreatPackages();
    RetreatPackage getRetreatPackageById(Integer id);
    RetreatPackage createRetreatPackage(RetreatPackage retreatPackage);
    RetreatPackage updateRetreatPackage(Integer id, RetreatPackage retreatPackage);
    void deleteRetreatPackage(Integer id);
    RetreatItineraryDto parseDescription(String raw);
}
