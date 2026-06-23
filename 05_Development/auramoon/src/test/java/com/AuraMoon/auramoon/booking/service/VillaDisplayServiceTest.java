package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.VillaDisplayDTO;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.impl.VillaServiceImpl;
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
public class VillaDisplayServiceTest {

    @Mock
    private VillaRepository villaRepository;

    @InjectMocks
    private VillaServiceImpl villaService;

    @Test
    public void UC09_TC_001_getAllVillasForDisplay() {
        // Arrange
        Villa v1 = Villa.builder().id(1).villaStatus("AVAILABLE").cleaningStatus("CLEAN").build();
        Villa v2 = Villa.builder().id(2).villaStatus("OCCUPIED").cleaningStatus("CLEAN").build();
        Villa v3 = Villa.builder().id(3).villaStatus("AVAILABLE").cleaningStatus("DIRTY").build();

        when(villaRepository.findAll()).thenReturn(Arrays.asList(v1, v2, v3));

        // Act
        List<VillaDisplayDTO> result = villaService.getAllVillasForDisplay();

        // Assert
        assertEquals(3, result.size());
        assertEquals("AVAILABLE", result.get(0).getVillaStatus());
        assertEquals("CLEAN", result.get(0).getCleaningStatus());
    }
}
