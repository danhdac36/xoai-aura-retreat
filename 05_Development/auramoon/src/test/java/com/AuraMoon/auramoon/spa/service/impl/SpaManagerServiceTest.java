package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.dto.TherapistDetailDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpaManagerServiceTest {

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private SpaManagerServiceImpl spaManagerService;

    private Therapist therapist;

    @BeforeEach
    void setUp() {
        therapist = new Therapist();
        therapist.setTherapistCode("TH001");
        therapist.setStatus("AVAILABLE");
    }

    @Test
    void TC001_getAllTherapistsWithDetails_ReturnsList() {
        TherapistDetailDto dto = new TherapistDetailDto("TH001", "Nguyen A", "AVAILABLE", 2L);
        when(therapistRepository.findAllTherapistsWithDetails(any(), any())).thenReturn(Arrays.asList(dto));

        List<TherapistDetailDto> result = spaManagerService.getAllTherapistsWithDetails();

        assertEquals(1, result.size());
        assertEquals("TH001", result.get(0).getTherapistCode());
    }

    @Test
    void TC002_updateTherapistStatus_Success() {
        when(therapistRepository.findByTherapistCode("TH001")).thenReturn(therapist);

        spaManagerService.updateTherapistStatus("TH001", "BUSY", 1);

        assertEquals("BUSY", therapist.getStatus());
        verify(therapistRepository).save(therapist);
    }

    @Test
    void TC003_updateTherapistStatus_InvalidStatus_ThrowsException() {
        assertThrows(SpaBusinessException.class, () -> 
            spaManagerService.updateTherapistStatus("TH001", "INVALID", 1)
        );
    }

    @Test
    void updateTherapistStatus_OffDuty_AutoReassign_Success() {
        when(therapistRepository.findByTherapistCode("TH001")).thenReturn(therapist);
        
        Schedule schedule = new Schedule();
        schedule.setStartTime(LocalDateTime.now().plusHours(1));
        schedule.setEndTime(LocalDateTime.now().plusHours(2));
        
        when(scheduleRepository.findFutureSchedules(eq("TH001"), any())).thenReturn(Arrays.asList(schedule));
        
        Therapist replacement = new Therapist();
        replacement.setTherapistCode("TH002");
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(Arrays.asList(replacement));

        spaManagerService.updateTherapistStatus("TH001", "OFF_DUTY", 1);

        assertEquals("OFF_DUTY", therapist.getStatus());
        assertEquals(replacement, schedule.getTherapist());
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void updateTherapistStatus_OffDuty_NoReplacement_ThrowsException() {
        when(therapistRepository.findByTherapistCode("TH001")).thenReturn(therapist);
        
        Schedule schedule = new Schedule();
        schedule.setStartTime(LocalDateTime.now().plusHours(1));
        
        when(scheduleRepository.findFutureSchedules(eq("TH001"), any())).thenReturn(Arrays.asList(schedule));
        
        // Return only the current therapist, so no replacement found
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(Arrays.asList(therapist));

        SpaBusinessException ex = assertThrows(SpaBusinessException.class, () -> 
            spaManagerService.updateTherapistStatus("TH001", "OFF_DUTY", 1)
        );
        assertTrue(ex.getMessage().contains("Không thể chuyển ca tự động"));
    }
}
