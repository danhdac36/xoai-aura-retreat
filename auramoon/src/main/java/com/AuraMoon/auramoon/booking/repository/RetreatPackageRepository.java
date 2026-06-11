package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetreatPackageRepository extends JpaRepository<RetreatPackage, Integer> {
        List<RetreatPackage> findTop3ByIsActiveTrueAndIsDeleteFalseOrderByIdAsc();

        List<RetreatPackage> findByIsActiveTrueAndIsDeleteFalse();

        List<RetreatPackage> findByTypePackageAndIsActiveTrueAndIsDeleteFalse(String typePackage);

        Optional<RetreatPackage> findByIdAndIsActiveTrueAndIsDeleteFalse(Integer id);

        @Query("""
                        SELECT DISTINCT r.typePackage
                        FROM RetreatPackage r
                        WHERE r.isActive = true
                        AND r.isDelete = false
                        """)
        List<String> findDistinctTypePackageByIsActiveTrue();

        @Query("""
                                                SELECT r
                                                FROM RetreatPackage r
                                                WHERE r.isActive = true
                                                AND r.isDelete = false
                                                AND (:typePackage IS NULL OR :typePackage = '' OR r.typePackage = :typePackage)
                                                AND (:durationDays IS NULL OR r.durationDays = :durationDays)
                                                AND (
                                                     :priceRange IS NULL OR :priceRange = ''
                                                     OR (:priceRange = 'UNDER_5' AND r.price < 5000000)
                        OR (:priceRange = 'FROM_5_TO_10' AND r.price >= 5000000 AND r.price <= 10000000)
                        OR (:priceRange = 'OVER_10' AND r.price > 10000000)
                                                )
                                                """)
        List<RetreatPackage> searchPackages(
                        @Param("typePackage") String typePackage,
                        @Param("durationDays") Integer durationDays,
                        @Param("priceRange") String priceRange);
}