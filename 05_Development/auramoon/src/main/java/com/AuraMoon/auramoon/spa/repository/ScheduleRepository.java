package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByStartTimeBetweenAndIsDeleteFalse(LocalDateTime start, LocalDateTime end);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.treatmentBooking tb " +
            "JOIN FETCH tb.treatmentService ts " +
            "JOIN FETCH s.room r " +
            "WHERE s.therapist.therapistCode = :therapistCode " +
            "AND s.startTime >= :startDate AND s.startTime < :endDate " +
            "AND s.isDelete = false " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findDailyScheduleForTherapist(
            @org.springframework.data.repository.query.Param("therapistCode") String therapistCode,
            @org.springframework.data.repository.query.Param("startDate") LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") LocalDateTime endDate
    );
}
