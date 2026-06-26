package com.AuraMoon.auramoon.hr;

import com.AuraMoon.auramoon.common.entity.User;
import com.AuraMoon.auramoon.common.entity.Role;
import com.AuraMoon.auramoon.spa.dto.TherapistDTO;
import com.AuraMoon.auramoon.spa.service.TherapistService;
import com.AuraMoon.auramoon.common.service.UserService;
import com.AuraMoon.auramoon.hr.service.StaffProfileAggregator;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import com.AuraMoon.auramoon.common.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StaffProfileAggregatorTest {

    @Mock
    private UserService userService;

    @Mock
    private TherapistService therapistService;

    @InjectMocks
    private StaffProfileAggregator aggregator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnProfileWithKpiForTherapist() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setName("THERAPIST");
        user.setRole(role);
        
        TherapistDTO tDto = new TherapistDTO();
        tDto.setTherapistCode("T-001");

        when(userService.getUserAndRole(1L)).thenReturn(user);
        when(therapistService.getTherapistInfo(1L)).thenReturn(tDto);

        // Act
        FullStaffProfileDTO result = aggregator.getAggregatedProfile(1L);

        // Assert
        assertNotNull(result);
        assertEquals("THERAPIST", result.getRoleName());
        assertEquals("T-001", result.getTherapistCode());
        verify(therapistService, times(1)).getTherapistInfo(1L);
    }

    @Test
    public void shouldReturnProfileWithoutKpiForReceptionist() {
        // Arrange
        User user = new User();
        user.setId(2L);
        Role role = new Role();
        role.setName("RECEPTIONIST");
        user.setRole(role);
        
        when(userService.getUserAndRole(2L)).thenReturn(user);

        // Act
        FullStaffProfileDTO result = aggregator.getAggregatedProfile(2L);

        // Assert
        assertNotNull(result);
        assertEquals("RECEPTIONIST", result.getRoleName());
        assertNull(result.getTherapistCode());
        verify(therapistService, never()).getTherapistInfo(anyLong());
    }

    @Test
    public void shouldThrowResourceNotFoundForInvalidId() {
        // Arrange
        when(userService.getUserAndRole(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            aggregator.getAggregatedProfile(99L);
        });
    }

    @Test
    public void shouldRedactPasswordHashFromDto() {
        // Arrange
        User user = new User();
        user.setId(3L);
        Role role = new Role();
        role.setName("MANAGER");
        user.setRole(role);
        user.setPasswordHash("super_secret_bcrypt_hash");
        
        when(userService.getUserAndRole(3L)).thenReturn(user);

        // Act
        FullStaffProfileDTO result = aggregator.getAggregatedProfile(3L);

        // Assert
        assertNotNull(result);
        assertNull(result.getPassword(), "Password must be redacted");
        assertNull(result.getPasswordHash(), "Password Hash must be redacted");
    }
}
