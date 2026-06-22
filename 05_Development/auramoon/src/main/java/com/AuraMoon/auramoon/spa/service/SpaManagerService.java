package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.TherapistDetailDto;
import com.AuraMoon.auramoon.spa.dto.ScheduleDto;

import java.time.LocalDate;
import java.util.List;

public interface SpaManagerService {
    List<TherapistDetailDto> getAllTherapistsWithDetails();
    void updateTherapistStatus(String therapistCode, String newStatus, Integer actorId);
    List<ScheduleDto> getScheduleForTherapist(String therapistCode, LocalDate date);
}
