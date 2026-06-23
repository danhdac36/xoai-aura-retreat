package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface SpaScheduleService {
    SpaScheduleResponse scheduleSession(SpaScheduleRequest request);

    List<String> getAvailableTimeSlots(LocalDate date, Integer durationMinutes);
}
