package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Villa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Integer> {
    List<Villa> findByVillaType_IdAndVillaStatusAndIsDeleteFalse(Integer typeId, String status);

    long countByIsDeleteFalse();

    long countByVillaStatusAndIsDeleteFalse(String status);

    // UC28 - Housekeeping Management
    List<Villa> findByCleaningStatusInAndIsDeleteFalse(List<String> cleaningStatuses);

    List<Villa> findByVillaStatusAndIsDeleteFalse(String status);

    @Query("SELECT COUNT(v) FROM Villa v WHERE v.villaType.id = :villaTypeId " +
            "AND v.villaStatus = 'AVAILABLE' AND v.isDelete = false " +
            "AND v.id NOT IN (" +
            "  SELECT b.assignedVilla.id FROM Booking b " +
            "  WHERE b.assignedVilla IS NOT NULL " +
            "  AND b.bookingStatus IN ('CONFIRMED', 'CHECKED_IN') " +
            "  AND b.checkinDate < :checkoutDate " +
            "  AND b.checkoutDate > :checkinDate" +
            ")")
    long countAvailableVillasWithoutOverlap(
            @Param("villaTypeId") Integer villaTypeId,
            @Param("checkinDate") LocalDateTime checkinDate,
            @Param("checkoutDate") LocalDateTime checkoutDate);
}
