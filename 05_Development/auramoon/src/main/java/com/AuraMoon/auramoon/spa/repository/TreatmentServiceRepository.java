package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentServiceRepository extends JpaRepository<TreatmentService, Integer> {
    List<TreatmentService> findByIsAvailableTrueAndIsDeleteFalse();
}
