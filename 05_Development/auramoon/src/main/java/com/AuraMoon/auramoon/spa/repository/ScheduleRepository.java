package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByStartTimeBetweenAndIsDeleteFalse(LocalDateTime start, LocalDateTime end);
}
