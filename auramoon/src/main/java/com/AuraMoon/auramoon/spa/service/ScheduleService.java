package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.ScheduleDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<ScheduleDto> getScheduleForTherapist(String therapistCode, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return scheduleRepository.findScheduleDtoForTherapist(therapistCode, startOfDay, endOfDay);
    }

    @Transactional
    public void updateScheduleStatus(Integer scheduleId, String newStatus) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null && schedule.getTreatmentBooking() != null) {
            schedule.getTreatmentBooking().setStatus(newStatus);
            scheduleRepository.save(schedule);
        }
    }

    @Transactional
    public void updateScheduleNote(Integer scheduleId, String note) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null && schedule.getTreatmentBooking() != null) {
            schedule.getTreatmentBooking().setNote(note);
            scheduleRepository.save(schedule);
        }
    }
}