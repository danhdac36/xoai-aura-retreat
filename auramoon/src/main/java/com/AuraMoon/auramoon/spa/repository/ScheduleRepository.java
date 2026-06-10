package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.dto.ScheduleDto; // Thêm import này
import com.AuraMoon.auramoon.spa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Thêm import này
import org.springframework.data.repository.query.Param; // Thêm import này
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByTherapist_TherapistCodeAndStartTimeBetween(String therapistCode, LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    @Query("SELECT new com.AuraMoon.auramoon.spa.dto.ScheduleDto(" +
            "s.id, s.startTime, s.endTime, tb.status, " +
            "u.fullName, ts.serviceName, r.roomName, tb.note, " +
            "php.medicalConditions, php.injuries) " +
            "FROM Schedule s " +
            "JOIN s.treatmentBooking tb " +
            "JOIN tb.treatmentService ts " +
            "LEFT JOIN s.room r " +
            "JOIN Booking b ON tb.bookingId = b.id " +
            "JOIN User u ON b.guestId = u.id " +
            "LEFT JOIN PhysicalHealthProfile php ON u.id = php.userId " +
            "WHERE s.therapist.therapistCode = :therapistCode " +
            "AND s.startTime BETWEEN :start AND :end " +
            "ORDER BY s.startTime ASC")
    List<ScheduleDto> findScheduleDtoForTherapist(@Param("therapistCode") String therapistCode,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}