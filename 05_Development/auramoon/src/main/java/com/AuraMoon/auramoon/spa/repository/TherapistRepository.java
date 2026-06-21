package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Integer> {

        long countByStatus(String status);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT t FROM Therapist t " +
                        "WHERE t.status = 'AVAILABLE' " +
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM Schedule s " +
                        "  WHERE s.therapist.therapistCode = t.therapistCode AND s.isDelete = false " +
                        "  AND s.startTime < :reqEnd AND s.endTime > :reqStart" +
                        ") ORDER BY t.therapistCode ASC")
        List<Therapist> findAvailableTherapistsWithLock(@Param("reqStart") LocalDateTime reqStart,
                        @Param("reqEnd") LocalDateTime reqEnd);

        @Query("SELECT u.fullName FROM User u JOIN Therapist t ON u.id = t.id WHERE t.therapistCode = :therapistCode")
        String findTherapistNameByCode(@Param("therapistCode") String therapistCode);

        Therapist findByTherapistCode(String therapistCode);

        @Query("SELECT new com.AuraMoon.auramoon.spa.dto.TherapistDetailDto(" +
           "t.therapistCode, u.fullName, t.status, " +
           "(SELECT COUNT(s) FROM Schedule s WHERE s.therapist.id = t.id AND s.isDelete = false AND s.startTime >= :startOfDay AND s.startTime < :endOfDay)) " +
           "FROM Therapist t JOIN User u ON t.id = u.id ORDER BY t.therapistCode ASC")
        List<com.AuraMoon.auramoon.spa.dto.TherapistDetailDto> findAllTherapistsWithDetails(@Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);
}
