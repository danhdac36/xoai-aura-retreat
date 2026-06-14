package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TherapistScheduleServiceImpl implements TherapistScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<TherapistScheduleDto> getDailySchedule(String therapistCode, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepository.findDailyScheduleForTherapist(therapistCode, startOfDay, endOfDay);

        return schedules.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TherapistScheduleDto mapToDto(Schedule schedule) {
        String serviceName = "Unknown Service";
        String note = "";
        if (schedule.getTreatmentBooking() != null) {
            note = schedule.getTreatmentBooking().getNote();
            if (schedule.getTreatmentBooking().getTreatmentService() != null) {
                serviceName = schedule.getTreatmentBooking().getTreatmentService().getServiceName();
            }
        }

        String roomName = "Unknown Room";
        if (schedule.getRoom() != null) {
            roomName = schedule.getRoom().getRoomName();
        }

        return TherapistScheduleDto.builder()
                .scheduleId(schedule.getId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .serviceName(serviceName)
                .roomName(roomName)
                .note(note)
                .build();
    }
}
