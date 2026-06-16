package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;

import java.time.LocalDate;
import java.util.List;

public interface TherapistScheduleService {
    List<TherapistScheduleDto> getDailySchedule(String therapistCode, LocalDate date);
    void updateSessionStatus(Integer scheduleId, String therapistCode, String newStatus);
}
