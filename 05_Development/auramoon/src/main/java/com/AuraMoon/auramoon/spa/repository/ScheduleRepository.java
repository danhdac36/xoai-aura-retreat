package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.dto.ScheduleDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
        List<Schedule> findByStartTimeBetweenAndIsDeleteFalse(LocalDateTime start, LocalDateTime end);

        @Query("SELECT s FROM Schedule s " +
                        "JOIN FETCH s.treatmentBooking tb " +
                        "JOIN FETCH tb.treatmentService ts " +
                        "JOIN FETCH s.room r " +
                        "WHERE s.therapist.therapistCode = :therapistCode " +
                        "AND s.startTime >= :startDate AND s.startTime < :endDate " +
                        "AND s.isDelete = false " +
                        "ORDER BY s.startTime ASC")
        List<Schedule> findDailyScheduleForTherapist(
                        @Param("therapistCode") String therapistCode,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT new com.AuraMoon.auramoon.spa.dto.ScheduleDto(" +
                        "s.id, s.startTime, s.endTime, tb.status, " +
                        "u.fullName, ts.serviceName, r.roomName, tb.note, " +
                        "p.medicalConditions, p.injuries) " +
                        "FROM Schedule s " +
                        "JOIN s.treatmentBooking tb " +
                        "JOIN tb.treatmentService ts " +
                        "LEFT JOIN s.room r " +
                        "LEFT JOIN Booking b ON tb.bookingId = b.id " +
                        "LEFT JOIN User u ON b.guestId = u.id " +
                        "LEFT JOIN PhysicalHealthProfile p ON u.id = p.userId " +
                        "WHERE s.therapist.therapistCode = :therapistCode " +
                        "AND s.startTime >= :startDate AND s.startTime < :endDate " +
                        "AND s.isDelete = false " +
                        "ORDER BY s.startTime ASC")
        List<ScheduleDto> findScheduleDtoForTherapist(
                        @Param("therapistCode") String therapistCode,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(s) FROM Schedule s WHERE s.isDelete = false AND s.startTime BETWEEN :start AND :end")
        long countSchedules(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("SELECT COUNT(s) FROM Schedule s WHERE s.isDelete = false AND s.treatmentBooking.status = :status AND s.startTime BETWEEN :start AND :end")
        long countSchedulesByStatus(@Param("status") String status, @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("SELECT s FROM Schedule s " +
               "WHERE s.therapist.therapistCode = :therapistCode " +
               "AND s.startTime >= :now " +
               "AND s.isDelete = false " +
               "AND s.treatmentBooking.status = 'Scheduled' " +
               "ORDER BY s.startTime ASC")
        List<Schedule> findFutureSchedules(@Param("therapistCode") String therapistCode, @Param("now") LocalDateTime now);

        List<Schedule> findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(Integer bookingId);
}

