package com.AuraMoon.auramoon.housekeeping.service;

import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UC28 - Housekeeping Management - Unit Test Suite
 * Test Data Classification: SYNTHETIC
 * Based on: UC28_EDS_Housekeeping.md (Section 13)
 */
@ExtendWith(MockitoExtension.class)
public class HousekeepingServiceTest {

    @Mock
    private VillaRepository villaRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private HousekeepingServiceImpl housekeepingService;

    private Villa dirtyVilla;
    private Villa cleaningVilla;
    private Villa cleanVilla;

    @BeforeEach
    void setUp() {
        // FX-001: Villa DIRTY
        dirtyVilla = Villa.builder()
                .id(2)
                .villaCode("V-002")
                .villaStatus("OCCUPIED")
                .cleaningStatus("DIRTY")
                .isDelete(false)
                .build();

        // FX-002: Villa CLEANING
        cleaningVilla = Villa.builder()
                .id(3)
                .villaCode("V-003")
                .villaStatus("OCCUPIED")
                .cleaningStatus("CLEANING")
                .isDelete(false)
                .build();

        // FX-003: Villa CLEAN
        cleanVilla = Villa.builder()
                .id(1)
                .villaCode("V-001")
                .villaStatus("AVAILABLE")
                .cleaningStatus("CLEAN")
                .isDelete(false)
                .build();
    }

    // ========================================================================
    // UC28-TC-001 — Lấy danh sách Villa DIRTY/CLEANING thành công
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-001: getDirtyAndCleaningVillas returns only DIRTY and CLEANING villas")
    void getDirtyAndCleaningVillas_ReturnsCorrectList() {
        // Arrange
        when(villaRepository.findByCleaningStatusInAndIsDeleteFalse(Arrays.asList("DIRTY", "CLEANING")))
                .thenReturn(Arrays.asList(dirtyVilla, cleaningVilla));

        // Act
        List<Villa> result = housekeepingService.getDirtyAndCleaningVillas();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(v -> "V-002".equals(v.getVillaCode())));
        assertTrue(result.stream().anyMatch(v -> "V-003".equals(v.getVillaCode())));
        // V-001 (CLEAN) must NOT be in the list
        assertFalse(result.stream().anyMatch(v -> "V-001".equals(v.getVillaCode())));

        verify(villaRepository, times(1))
                .findByCleaningStatusInAndIsDeleteFalse(Arrays.asList("DIRTY", "CLEANING"));
    }

    // ========================================================================
    // UC28-TC-002 — Phân công nhân viên dọn dẹp thành công
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-002: assignHousekeeper changes DIRTY -> CLEANING and logs audit")
    void assignHousekeeper_Success_WhenStatusIsDirty() {
        // Arrange
        when(villaRepository.findById(2)).thenReturn(Optional.of(dirtyVilla));
        when(villaRepository.save(any(Villa.class))).thenReturn(dirtyVilla);

        // Act
        housekeepingService.assignHousekeeper(2, "Nguyễn Văn A", 1);

        // Assert
        assertEquals("CLEANING", dirtyVilla.getCleaningStatus());

        verify(villaRepository, times(1)).save(dirtyVilla);
        verify(auditLogRepository, times(1)).saveAuditLog(
                eq("HOUSEKEEPING_ASSIGN"),
                eq(1),
                contains("V-002"));
    }

    // ========================================================================
    // UC28-TC-003 — Duyệt sạch thành công (Approve)
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-003: approveAndUpdateToClean changes to CLEAN/AVAILABLE and logs audit")
    void approveAndUpdateToClean_Success_WhenStatusIsCleaning() {
        // Arrange
        when(villaRepository.findById(3)).thenReturn(Optional.of(cleaningVilla));
        when(villaRepository.save(any(Villa.class))).thenReturn(cleaningVilla);

        // Act
        housekeepingService.approveAndUpdateToClean(3, 1);

        // Assert
        assertEquals("CLEAN", cleaningVilla.getCleaningStatus());
        assertEquals("AVAILABLE", cleaningVilla.getVillaStatus());

        verify(villaRepository, times(1)).save(cleaningVilla);
        verify(auditLogRepository, times(1)).saveAuditLog(
                eq("HOUSEKEEPING_APPROVE"),
                eq(1),
                contains("V-003"));
    }

    // ========================================================================
    // UC28-TC-004 — Từ chối nghiệm thu (Reject)
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-004: rejectCleaning changes CLEANING -> DIRTY and logs audit")
    void rejectCleaning_Success_ChangesStatusBackToDirty() {
        // Arrange
        when(villaRepository.findById(3)).thenReturn(Optional.of(cleaningVilla));
        when(villaRepository.save(any(Villa.class))).thenReturn(cleaningVilla);

        // Act
        housekeepingService.rejectCleaning(3, 1);

        // Assert
        assertEquals("DIRTY", cleaningVilla.getCleaningStatus());

        verify(villaRepository, times(1)).save(cleaningVilla);
        verify(auditLogRepository, times(1)).saveAuditLog(
                eq("HOUSEKEEPING_REJECT"),
                eq(1),
                contains("V-003"));
    }

    // ========================================================================
    // UC28-TC-005 — Duyệt Villa đã sạch rồi (Error - HK-002)
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-005: approveAndUpdateToClean throws when villa already CLEAN (HK-002)")
    void approveAndUpdateToClean_Throws_WhenAlreadyClean() {
        // Arrange
        when(villaRepository.findById(1)).thenReturn(Optional.of(cleanVilla));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> housekeepingService.approveAndUpdateToClean(1, 1)
        );

        assertEquals("Villa is already clean.", exception.getMessage());

        // Villa should NOT be saved
        verify(villaRepository, never()).save(any(Villa.class));
        // Audit log should NOT be recorded
        verify(auditLogRepository, never()).saveAuditLog(anyString(), anyInt(), anyString());
    }

    // ========================================================================
    // UC28-TC-006 — Villa không tồn tại (Error - HK-003)
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-006: approveAndUpdateToClean throws when villa not found (HK-003)")
    void approveAndUpdateToClean_Throws_WhenVillaNotFound() {
        // Arrange
        when(villaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> housekeepingService.approveAndUpdateToClean(999, 1)
        );

        assertTrue(exception.getMessage().contains("Villa not found"));

        verify(villaRepository, never()).save(any(Villa.class));
        verify(auditLogRepository, never()).saveAuditLog(anyString(), anyInt(), anyString());
    }

    // ========================================================================
    // UC28-TC-007 — Assign Villa không phải DIRTY (Error - Invalid State)
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-007: assignHousekeeper throws when villa is not DIRTY")
    void assignHousekeeper_Throws_WhenNotDirty() {
        // Arrange - Villa is CLEANING, not DIRTY
        when(villaRepository.findById(3)).thenReturn(Optional.of(cleaningVilla));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> housekeepingService.assignHousekeeper(3, "Trần B", 1)
        );

        assertTrue(exception.getMessage().contains("DIRTY"));

        verify(villaRepository, never()).save(any(Villa.class));
        verify(auditLogRepository, never()).saveAuditLog(anyString(), anyInt(), anyString());
    }

    // ========================================================================
    // UC28-TC-008 — Empty list khi không có Villa nào cần dọn
    // ========================================================================
    @Test
    @DisplayName("UC28-TC-008: getDirtyAndCleaningVillas returns empty list when all villas clean")
    void getDirtyAndCleaningVillas_ReturnsEmptyList_WhenAllClean() {
        // Arrange
        when(villaRepository.findByCleaningStatusInAndIsDeleteFalse(Arrays.asList("DIRTY", "CLEANING")))
                .thenReturn(Collections.emptyList());

        // Act
        List<Villa> result = housekeepingService.getDirtyAndCleaningVillas();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
