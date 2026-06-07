package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> { // Chú ý: Đổi ID từ Long sang Integer cho khớp với Entity của cậu nhé
    
    // Dấu "_" báo cho JPA biết: chui vào biến "therapist", tìm thuộc tính "therapistCode"
    List<Schedule> findByTherapist_TherapistCodeAndStartTimeBetween(String therapistCode, LocalDateTime startOfDay, LocalDateTime endOfDay);
}