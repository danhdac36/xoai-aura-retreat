package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;
import com.AuraMoon.auramoon.billing.exception.NightAuditAlreadyExecutedException;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NightAuditServiceTest {

    @Mock
    private IFolioConsolidationService folioConsolidationService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private NightAuditServiceImpl nightAuditService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
    }

    /**
     * UC26-TC-001 — Chạy Night Audit thành công (Happy Path)
     */
    @Test
    void executeAudit_Success_WhenNotAlreadyRun() {
        // Arrange
        // Giả lập chưa có log chạy audit hôm nay
        when(auditLogRepository.existsByActionTypeAndTimestampDate(anyString(), eq(LocalDate.now())))
                .thenReturn(false);

        // Giả lập Consolidation Service trả về kết quả
        NightAuditResultDTO mockResult = new NightAuditResultDTO();
        mockResult.setTotalActiveFolios(1);
        mockResult.setSpaChargesPosted(new BigDecimal("500000.00"));
        mockResult.setFnbChargesPosted(new BigDecimal("200000.00"));
        mockResult.setGrandTotalRevenue(new BigDecimal("700000.00"));

        when(folioConsolidationService.consolidateAllActiveFolios(anyInt()))
                .thenReturn(mockResult);

        // Act
        NightAuditResultDTO result = nightAuditService.executeAudit("NIGHT_AUDIT_MANUAL", 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalActiveFolios());
        assertEquals(new BigDecimal("700000.00"), result.getGrandTotalRevenue());

        // Kiểm tra FolioConsolidationService được gọi đúng 1 lần
        verify(folioConsolidationService, times(1)).consolidateAllActiveFolios(1);

        // Kiểm tra AuditLogRepository được gọi để lưu log
        verify(auditLogRepository, times(1)).saveAuditLog(
                eq("NIGHT_AUDIT_MANUAL"),
                eq(1),
                contains("totalActiveFolios: 1")
        );
    }

    /**
     * UC26-TC-002 — Xử lý khi Audit đã chạy rồi (SRS E1)
     */
    @Test
    void executeAudit_ThrowsException_WhenAlreadyRun() {
        // Arrange
        // Giả lập đã có log chạy audit tự động lúc 00:00 hôm nay
        when(auditLogRepository.existsByActionTypeAndTimestampDate(anyString(), eq(LocalDate.now())))
                .thenReturn(true);

        // Act & Assert
        NightAuditAlreadyExecutedException exception = assertThrows(
                NightAuditAlreadyExecutedException.class,
                () -> nightAuditService.executeAudit("NIGHT_AUDIT_MANUAL", 1)
        );

        assertEquals("Night Audit has already been completed for this date.", exception.getMessage());

        // Kiểm tra không bao giờ gọi hàm consolidation
        verify(folioConsolidationService, never()).consolidateAllActiveFolios(anyInt());
        verify(auditLogRepository, never()).saveAuditLog(anyString(), anyInt(), anyString());
    }

    /**
     * UC26-TC-004 — Không có dữ liệu mới để chốt (SRS A2)
     */
    @Test
    void executeAudit_ReturnsZero_WhenNoNewCharges() {
        // Arrange
        when(auditLogRepository.existsByActionTypeAndTimestampDate(anyString(), eq(LocalDate.now())))
                .thenReturn(false);

        // Trả về DTO rỗng (0 records)
        NightAuditResultDTO emptyResult = new NightAuditResultDTO();
        emptyResult.setTotalActiveFolios(0);
        emptyResult.setSpaChargesPosted(BigDecimal.ZERO);
        emptyResult.setFnbChargesPosted(BigDecimal.ZERO);
        emptyResult.setGrandTotalRevenue(BigDecimal.ZERO);

        when(folioConsolidationService.consolidateAllActiveFolios(0))
                .thenReturn(emptyResult);

        // Act
        NightAuditResultDTO result = nightAuditService.executeAudit("NIGHT_AUDIT_AUTO", 0);

        // Assert
        assertEquals(0, result.getTotalActiveFolios());
        assertEquals(BigDecimal.ZERO, result.getGrandTotalRevenue());

        // Log vẫn phải được lưu với message "No new charges"
        verify(auditLogRepository, times(1)).saveAuditLog(
                eq("NIGHT_AUDIT_AUTO"),
                eq(0),
                contains("No new charges")
        );
    }
}
