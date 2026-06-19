package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.TreatmentRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

public interface TreatmentRoomRepository extends JpaRepository<TreatmentRoom, Integer> {
    
    long countByStatusAndIsDeleteFalse(String status);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM TreatmentRoom r " +
           "WHERE r.status = 'AVAILABLE' AND r.isDelete = false " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Schedule s " +
           "  WHERE s.room.id = r.id AND s.isDelete = false " +
           "  AND s.startTime < :reqEnd AND s.endTime > :reqStart" +
           ") ORDER BY r.id ASC")
    List<TreatmentRoom> findAvailableRoomsWithLock(@Param("reqStart") LocalDateTime reqStart, @Param("reqEnd") LocalDateTime reqEnd);
}
