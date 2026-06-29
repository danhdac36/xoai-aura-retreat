package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import java.util.List;

public interface TreatmentServiceMasterService {
    List<TreatmentService> getAllTreatmentServices();
    TreatmentService getTreatmentServiceById(Integer id);
    TreatmentService createTreatmentService(TreatmentService treatmentService);
    TreatmentService updateTreatmentService(Integer id, TreatmentService treatmentService);
    void deleteTreatmentService(Integer id);
}
