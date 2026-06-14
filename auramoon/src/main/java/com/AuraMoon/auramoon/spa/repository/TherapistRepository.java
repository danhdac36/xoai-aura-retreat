package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

public interface TherapistRepository extends JpaRepository<Therapist, Integer> {

    long countByStatus(String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Therapist t " +
           "WHERE t.status = 'Active' " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Schedule s " +
           "  WHERE s.therapist.therapistCode = t.therapistCode AND s.isDelete = false " +
           "  AND s.startTime < :reqEnd AND s.endTime > :reqStart" +
           ") ORDER BY t.therapistCode ASC")
    List<Therapist> findAvailableTherapistsWithLock(@Param("reqStart") LocalDateTime reqStart, @Param("reqEnd") LocalDateTime reqEnd);
}
