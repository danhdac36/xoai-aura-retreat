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

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    // RPT-TC-001
    @Test
    @DisplayName("RPT-TC-001: Generate Occupancy Report Data")
    void testGenerateOccupancyReportData() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        ReportDataDTO result = reportService.generateReportData(startDate, endDate, "OCCUPANCY");
        // EXPECT RED: should fail because it returns null
        assertNotNull(result, "RPT-TC-001 TDD: Expected result to not be null");
        assertFalse(result.getOccupancyRows().isEmpty());
    }

    // RPT-TC-002
    @Test
    @DisplayName("RPT-TC-002: Generate Therapist Utilization Data")
    void testGenerateTherapistUtilizationData() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        ReportDataDTO result = reportService.generateReportData(startDate, endDate, "UTILIZATION");
        // EXPECT RED: should fail because it returns null
        assertNotNull(result, "RPT-TC-002 TDD: Expected result to not be null");
        assertEquals(3, result.getUtilizationRows().size());
    }

    // RPT-TC-003
    @Test
    @DisplayName("RPT-TC-003: Export Excel File")
    void testExportExcelFile() {
        ReportDataDTO fakeData = new ReportDataDTO(); // Assume populated
        byte[] bytes = reportService.exportToExcel(fakeData);
        // EXPECT RED: returns empty byte array
        assertTrue(bytes.length > 0, "RPT-TC-003 TDD: Expected byte array to have content");
    }

    // RPT-TC-004
    @Test
    @DisplayName("RPT-TC-004: Empty Data Returns Excel with Headers Only")
    void testEmptyDataReturnsExcelWithHeadersOnly() {
        ReportDataDTO emptyData = new ReportDataDTO();
        byte[] bytes = reportService.exportToExcel(emptyData);
        // EXPECT RED: returns empty byte array
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
