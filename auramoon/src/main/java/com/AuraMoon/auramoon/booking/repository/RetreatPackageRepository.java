package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetreatPackageRepository extends JpaRepository<RetreatPackage, Integer> {
    List<RetreatPackage> findByIsActiveTrue();
    List<RetreatPackage> findByTypePackageAndIsActiveTrue(String typePackage);

    @Query("SELECT DISTINCT r.typePackage FROM RetreatPackage r WHERE r.isActive = true")
    List<String> findDistinctTypePackageByIsActiveTrue();
}
