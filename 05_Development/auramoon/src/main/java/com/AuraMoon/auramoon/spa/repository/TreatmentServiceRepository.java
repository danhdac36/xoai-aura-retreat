package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreatmentServiceRepository extends JpaRepository<TreatmentService, Integer> {
    List<TreatmentService> findByIsAvailableTrueAndIsDeleteFalse();
    Optional<TreatmentService> findByTreatmentCode(String treatmentCode);
}
