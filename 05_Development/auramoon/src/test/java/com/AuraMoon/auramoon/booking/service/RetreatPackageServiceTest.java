package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.service.impl.RetreatPackageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RetreatPackageServiceTest {

    @Mock
    private RetreatPackageRepository retreatPackageRepository;

    @InjectMocks
    private RetreatPackageServiceImpl retreatPackageService;

    @Test
    public void UC06_TC_001_getAllPackages() {
        // Arrange
        RetreatPackage p1 = RetreatPackage.builder().id(1).isActive(true).build();
        RetreatPackage p2 = RetreatPackage.builder().id(2).isActive(true).build();
        when(retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse()).thenReturn(Arrays.asList(p1, p2));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.getAllActivePackages();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void UC06_TC_002_filterPackagesByType() {
        // Arrange
        RetreatPackage p1 = RetreatPackage.builder().id(1).typePackage("Yoga").build();
        when(retreatPackageRepository.findByTypePackageAndIsActiveTrueAndIsDeleteFalse("Yoga")).thenReturn(Arrays.asList(p1));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.getPackagesByType("Yoga");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getTypePackage());
    }
}
