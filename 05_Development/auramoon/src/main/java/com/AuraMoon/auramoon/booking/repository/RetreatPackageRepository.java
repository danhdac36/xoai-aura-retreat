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
                                                AND (:minDays IS NULL OR r.durationDays >= :minDays)
                                                AND (:maxDays IS NULL OR r.durationDays <= :maxDays)
                                                AND (:minPrice IS NULL OR r.price >= :minPrice)
                                                AND (:maxPrice IS NULL OR r.price <= :maxPrice)
                                                """)
        List<RetreatPackage> searchPackages(
                        @Param("typePackage") String typePackage,
                        @Param("minDays") Integer minDays,
                        @Param("maxDays") Integer maxDays,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice);
}