package com.AuraMoon.auramoon.yoga.repository;

import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface YogaRegistrationRepository extends JpaRepository<YogaRegistration, Integer> {

    long countBySchedule_IdAndStatus(Integer scheduleId, String status);

    List<YogaRegistration> findByBookingIdAndStatus(Integer bookingId, String status);

    List<YogaRegistration> findBySchedule_IdAndStatus(Integer scheduleId, String status);

    Optional<YogaRegistration> findByBookingIdAndSchedule_IdAndStatus(Integer bookingId, Integer scheduleId, String status);

    @Query("SELECT r FROM YogaRegistration r " +
           "WHERE r.bookingId = :bookingId " +
           "AND r.status = 'REGISTERED' " +
           "AND r.schedule.isDelete = false " +
           "AND r.schedule.startTime < :endTime " +
           "AND r.schedule.endTime > :startTime")
    List<YogaRegistration> findOverlappingRegistrations(
            @Param("bookingId") Integer bookingId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
