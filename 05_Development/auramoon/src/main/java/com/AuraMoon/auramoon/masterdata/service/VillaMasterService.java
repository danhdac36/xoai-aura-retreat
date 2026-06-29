package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.booking.entity.Villa;
import java.util.List;

public interface VillaMasterService {
    List<Villa> getAllVillas();
    Villa getVillaById(Integer id);
    Villa createVilla(Villa villa);
    Villa updateVilla(Integer id, Villa villa);
    void deleteVilla(Integer id);
}
