package com.AuraMoon.auramoon.yoga.repository;

import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface YogaScheduleRepository extends JpaRepository<YogaSchedule, Integer> {

       @Lock(LockModeType.PESSIMISTIC_WRITE)
       @Query("SELECT s FROM YogaSchedule s WHERE s.id = :id AND s.isDelete = false")
       Optional<YogaSchedule> findByIdForUpdate(@Param("id") Integer id);

       List<YogaSchedule> findByIsDeleteFalse();

       @Query("SELECT s FROM YogaSchedule s " +
                     "WHERE s.isDelete = false " +
                     "AND s.startTime >= :start " +
                     "AND s.startTime < :end")
       List<YogaSchedule> findSchedulesByDateRange(
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

       @Query("SELECT s FROM YogaSchedule s " +
                     "WHERE s.isDelete = false " +
                     "AND s.instructor.instructorId = :instructorUserId " +
                     "AND s.startTime >= :start " +
                     "AND s.startTime < :end " +
                     "ORDER BY s.startTime ASC")
       List<YogaSchedule> findByInstructorAndDateRange(
                     @Param("instructorUserId") Integer instructorUserId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);
}
