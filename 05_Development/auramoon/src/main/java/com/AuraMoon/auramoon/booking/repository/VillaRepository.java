package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Villa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Integer> {
    List<Villa> findByVillaType_IdAndVillaStatusAndIsDeleteFalse(Integer typeId, String status);
    
    long countByIsDeleteFalse();
    long countByVillaStatusAndIsDeleteFalse(String status);

    // UC28 - Housekeeping Management
    List<Villa> findByCleaningStatusInAndIsDeleteFalse(List<String> cleaningStatuses);
}
