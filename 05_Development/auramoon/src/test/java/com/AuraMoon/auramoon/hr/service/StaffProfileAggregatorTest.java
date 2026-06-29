package com.AuraMoon.auramoon.hr.service;

import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class StaffProfileAggregatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private TreatmentBookingRepository treatmentBookingRepository;

    @Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private com.AuraMoon.auramoon.booking.repository.BookingRepository bookingRepository;

    @Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    @InjectMocks
    private StaffProfileAggregator aggregator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnProfileWithTherapistInfo() {
        User user = new User();
        user.setId(1);
        Role role = new Role();
        role.setRoleName("THERAPIST");
        user.setRole(role);
        
        Therapist therapist = new Therapist();
        therapist.setTherapistCode("T-001");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(therapistRepository.findById(1)).thenReturn(Optional.of(therapist));
        when(auditLogRepository.findByActorIdOrderByTimestampDesc(eq(1), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());
        when(scheduleRepository.findByTherapistIdOrderByStartTimeDesc(eq(1), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        FullStaffProfileDTO result = aggregator.getAggregatedProfile(1L, 0, 10, null);

        assertNotNull(result);
        assertEquals("THERAPIST", result.getRoleName());
        assertEquals("T-001", result.getTherapistCode());
        verify(therapistRepository, times(1)).findById(1);
    }

    @Test
    public void shouldReturnProfileWithoutTherapistInfo() {
        User user = new User();
        user.setId(2);
        Role role = new Role();
        role.setRoleName("RECEPTIONIST");
        user.setRole(role);
        
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(auditLogRepository.findByActorIdOrderByTimestampDesc(eq(2), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        FullStaffProfileDTO result = aggregator.getAggregatedProfile(2L, 0, 10, null);

        assertNotNull(result);
        assertEquals("RECEPTIONIST", result.getRoleName());
        assertNull(result.getTherapistCode());
        verify(therapistRepository, never()).findById(anyInt());
    }

    @Test
    public void shouldRedactPassword() {
        User user = new User();
        user.setId(3);
        Role role = new Role();
        role.setRoleName("MANAGER");
        user.setRole(role);
        user.setPasswordHash("super_secret_hash");
        
        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(auditLogRepository.findByActorIdOrderByTimestampDesc(eq(3), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        FullStaffProfileDTO result = aggregator.getAggregatedProfile(3L, 0, 10, null);

        assertNotNull(result);
        assertNull(result.getPassword(), "Password must be redacted");
        assertNull(result.getPasswordHash(), "Password hash must be redacted");
    }
}

