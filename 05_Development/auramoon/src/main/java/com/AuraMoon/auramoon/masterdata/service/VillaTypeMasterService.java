package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.booking.entity.VillaType;
import java.util.List;

public interface VillaTypeMasterService {
    List<VillaType> getAllVillaTypes();
    VillaType getVillaTypeById(Integer id);
    VillaType createVillaType(VillaType villaType);
    VillaType updateVillaType(Integer id, VillaType villaType);
    void deleteVillaType(Integer id);
}
