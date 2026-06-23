package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.impl.VillaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VillaServiceImplTest {

    @Mock
    private VillaRepository villaRepository;

    @InjectMocks
    private VillaServiceImpl villaService;

    @Test
    @DisplayName("UC09-TC-001: checkVillaAvailability trả về true khi không truyền ngày và có villa trống")
    public void checkVillaAvailability_noDates_returnsTrue() {
        // Arrange
        Integer villaTypeId = 1;
        Villa villa = Villa.builder().id(1).villaStatus("AVAILABLE").build();
        when(villaRepository.findByVillaType_IdAndVillaStatusAndIsDeleteFalse(villaTypeId, "AVAILABLE"))
                .thenReturn(Collections.singletonList(villa));

        // Act
        boolean result = villaService.checkVillaAvailability(villaTypeId, null, null);

        // Assert
        assertTrue(result);
        verify(villaRepository, times(1))
                .findByVillaType_IdAndVillaStatusAndIsDeleteFalse(villaTypeId, "AVAILABLE");
    }

    @Test
    @DisplayName("UC09-TC-002: checkVillaAvailability trả về true khi truyền ngày và không có trùng lịch")
    public void checkVillaAvailability_withDatesNoOverlap_returnsTrue() {
        // Arrange
        Integer villaTypeId = 1;
        LocalDateTime checkin = LocalDateTime.now();
        LocalDateTime checkout = LocalDateTime.now().plusDays(2);
        when(villaRepository.countAvailableVillasWithoutOverlap(villaTypeId, checkin, checkout)).thenReturn(1L);

        // Act
        boolean result = villaService.checkVillaAvailability(villaTypeId, checkin, checkout);

        // Assert
        assertTrue(result);
        verify(villaRepository, times(1))
                .countAvailableVillasWithoutOverlap(villaTypeId, checkin, checkout);
    }

    @Test
    @DisplayName("UC09-TC-003: checkVillaAvailability trả về false khi truyền ngày và có trùng lịch đặt")
    public void checkVillaAvailability_withDatesOverlap_returnsFalse() {
        // Arrange
        Integer villaTypeId = 1;
        LocalDateTime checkin = LocalDateTime.now();
        LocalDateTime checkout = LocalDateTime.now().plusDays(2);
        when(villaRepository.countAvailableVillasWithoutOverlap(villaTypeId, checkin, checkout)).thenReturn(0L);

        // Act
        boolean result = villaService.checkVillaAvailability(villaTypeId, checkin, checkout);

        // Assert
        assertFalse(result);
        verify(villaRepository, times(1))
                .countAvailableVillasWithoutOverlap(villaTypeId, checkin, checkout);
    }

    @Test
    @DisplayName("UC09-TC-004: updateVillaStatuses ném ngoại lệ khi truyền trạng thái villa không hợp lệ")
    public void updateVillaStatuses_invalidVillaStatus_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            villaService.updateVillaStatuses(1, "INVALID_STATUS", "CLEAN");
        });
        assertTrue(exception.getMessage().contains("Trạng thái Villa không hợp lệ"));
    }

    @Test
    @DisplayName("UC09-TC-005: updateVillaStatuses ném ngoại lệ khi truyền trạng thái dọn dẹp không hợp lệ")
    public void updateVillaStatuses_invalidCleaningStatus_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            villaService.updateVillaStatuses(1, "AVAILABLE", "INVALID_CLEANING");
        });
        assertTrue(exception.getMessage().contains("Trạng thái dọn dẹp không hợp lệ"));
    }

    @Test
    @DisplayName("UC09-TC-006: updateVillaStatuses ném ngoại lệ khi không tìm thấy villa")
    public void updateVillaStatuses_villaNotFound_throwsException() {
        // Arrange
        when(villaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            villaService.updateVillaStatuses(999, "AVAILABLE", "CLEAN");
        });
        assertTrue(exception.getMessage().contains("Không tìm thấy Villa với id"));
    }

    @Test
    @DisplayName("UC09-TC-007: updateVillaStatuses ném ngoại lệ khi chuyển từ OCCUPIED sang MAINTENANCE")
    public void updateVillaStatuses_occupiedToMaintenance_throwsException() {
        // Arrange
        Integer villaId = 1;
        Villa villa = Villa.builder().id(villaId).villaStatus("OCCUPIED").cleaningStatus("CLEAN").build();
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            villaService.updateVillaStatuses(villaId, "MAINTENANCE", "DIRTY");
        });
        assertTrue(exception.getMessage().contains("Không thể chuyển Villa đang có khách (OCCUPIED) sang bảo trì"));
    }

    @Test
    @DisplayName("UC09-TC-008: updateVillaStatuses ném ngoại lệ khi chuyển từ MAINTENANCE sang OCCUPIED")
    public void updateVillaStatuses_maintenanceToOccupied_throwsException() {
        // Arrange
        Integer villaId = 1;
        Villa villa = Villa.builder().id(villaId).villaStatus("MAINTENANCE").cleaningStatus("DIRTY").build();
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            villaService.updateVillaStatuses(villaId, "OCCUPIED", "CLEAN");
        });
        assertTrue(exception.getMessage().contains("Không thể chuyển trực tiếp biệt thự đang bảo trì (MAINTENANCE) sang trạng thái đang có khách"));
    }

    @Test
    @DisplayName("UC09-TC-009: updateVillaStatuses ném ngoại lệ khi chuyển từ OCCUPIED sang AVAILABLE + CLEAN trực tiếp")
    public void updateVillaStatuses_occupiedToAvailableClean_throwsException() {
        // Arrange
        Integer villaId = 1;
        Villa villa = Villa.builder().id(villaId).villaStatus("OCCUPIED").cleaningStatus("CLEAN").build();
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            villaService.updateVillaStatuses(villaId, "AVAILABLE", "CLEAN");
        });
        assertTrue(exception.getMessage().contains("Villa sau khi khách check-out phải ở trạng thái DIRTY trước khi được dọn dẹp"));
    }

    @Test
    @DisplayName("UC09-TC-010: updateVillaStatuses thực hiện cập nhật thành công với trạng thái hợp lệ")
    public void updateVillaStatuses_validTransition_updatesStatuses() {
        // Arrange
        Integer villaId = 1;
        Villa villa = Villa.builder().id(villaId).villaStatus("AVAILABLE").cleaningStatus("CLEAN").build();
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));

        // Mock Security Context
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("receptionist_user");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        villaService.updateVillaStatuses(villaId, "MAINTENANCE", "DIRTY");

        // Assert
        assertEquals("MAINTENANCE", villa.getVillaStatus());
        assertEquals("DIRTY", villa.getCleaningStatus());
        verify(villaRepository, times(1)).save(villa);

        // Clear security context
        SecurityContextHolder.clearContext();
    }
}
