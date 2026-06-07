package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getScheduleForTherapist(String therapistCode, LocalDate date) {
        // 1. Biến đổi ngày (LocalDate) thành mốc bắt đầu ngày lúc 00:00:00
        LocalDateTime startOfDay = date.atStartOfDay();

        // 2. Biến đổi ngày (LocalDate) thành mốc kết thúc ngày lúc 23:59:59.999999999
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 3. Gọi xuống tầng Repository để truy vấn DB
        return scheduleRepository.findByTherapist_TherapistCodeAndStartTimeBetween(therapistCode, startOfDay, endOfDay);
    }
}