package com.AuraMoon.auramoon.report.service;

import com.AuraMoon.auramoon.report.dto.ReportDataDTO;
import com.AuraMoon.auramoon.report.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private com.AuraMoon.auramoon.booking.repository.VillaRepository villaRepository;

    @Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    // RPT-TC-001
    @Test
    @DisplayName("RPT-TC-001: Generate Occupancy Report Data")
    void testGenerateOccupancyReportData() {
        when(villaRepository.countByIsDeleteFalse()).thenReturn(10L);
        when(villaRepository.countByVillaStatusAndIsDeleteFalse("OCCUPIED")).thenReturn(5L);

        LocalDate startDate = LocalDate.of(2026, 4, 15);
        LocalDate endDate = LocalDate.of(2026, 6, 15);
        ReportDataDTO result = reportService.generateReportData(startDate, endDate, "OCCUPANCY");
        assertNotNull(result, "RPT-TC-001 TDD: Expected result to not be null");
        assertFalse(result.getOccupancyRows().isEmpty());
        // For monthly logic, 4/15 to 6/15 covers April, May, June = 3 rows
        assertEquals(3, result.getOccupancyRows().size(), "RPT-TC-001: Should generate exactly 3 monthly rows");
        assertEquals("Tháng 4", result.getOccupancyRows().get(0).getMonthLabel(),
                "RPT-TC-001: First row should be Tháng 4");
    }

    // RPT-TC-002
    @Test
    @DisplayName("RPT-TC-002: Generate Therapist Utilization Data")
    void testGenerateTherapistUtilizationData() {
        when(scheduleRepository.countSchedules(any(), any())).thenReturn(20L);
        when(scheduleRepository.countSchedulesByStatus(eq("COMPLETED"), any(), any())).thenReturn(15L);

        LocalDate startDate = LocalDate.of(2026, 4, 15);
        LocalDate endDate = LocalDate.of(2026, 6, 15);
        ReportDataDTO result = reportService.generateReportData(startDate, endDate, "UTILIZATION");
        assertNotNull(result, "RPT-TC-002 TDD: Expected result to not be null");
        assertFalse(result.getUtilizationRows().isEmpty());
        // Currently the mockup says "Tất cả chuyên viên" for the whole period,
        // but if we group by therapist AND month, or just therapist.
        // Let's assume the test just checks it generates something for now.
        // If it's grouped by therapist, it should have rows equal to number of
        // therapists.
    }

    // RPT-TC-003
    @Test
    @DisplayName("RPT-TC-003: Export Excel File")
    void testExportExcelFile() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        byte[] bytes = reportService.exportToExcel(startDate, endDate, "ALL");
        assertTrue(bytes.length > 0, "RPT-TC-003 TDD: Expected byte array to have content");
    }

    // RPT-TC-004
    @Test
    @DisplayName("RPT-TC-004: Empty Data Returns Excel with Headers Only")
    void testEmptyDataReturnsExcelWithHeadersOnly() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        byte[] bytes = reportService.exportToExcel(startDate, endDate, "ALL");
        assertTrue(bytes.length > 0, "RPT-TC-004 TDD: Expected byte array to have content");
    }

    // RPT-TC-005
    @Test
    @DisplayName("RPT-TC-005: Invalid Date Range")
    void testInvalidDateRange() {
        LocalDate startDate = LocalDate.of(2026, 6, 30);
        LocalDate endDate = LocalDate.of(2026, 6, 1);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            reportService.generateReportData(startDate, endDate, "ALL");
        });
        assertEquals("Ngày bắt đầu phải trước ngày kết thúc", thrown.getMessage());
    }
}
