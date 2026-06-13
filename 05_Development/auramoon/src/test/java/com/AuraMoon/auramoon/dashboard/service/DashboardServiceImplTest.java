package com.AuraMoon.auramoon.dashboard.service;

import com.AuraMoon.auramoon.dashboard.dto.RevenueDashboardDTO;
import com.AuraMoon.auramoon.dashboard.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    // DASH-TC-001
    @Test
    @DisplayName("DASH-TC-001: Verify Revenue Calculation (Happy Path)")
    void testVerifyRevenueCalculation() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        // Act
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");

        // Assert - expecting failure (RED) because implementation is null
        assertNotNull(result, "DASH-TC-001 TDD: Expected result to not be null");
        assertEquals(13000000.0, result.getTotalRevenue());
    }

    // DASH-TC-002
    @Test
    @DisplayName("DASH-TC-002: Date Range Filtering")
    void testDateRangeFiltering() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result, "DASH-TC-002 TDD: Expected result to not be null");
        assertEquals(8000000.0, result.getPackageRevenue());
    }

    // DASH-TC-003
    @Test
    @DisplayName("DASH-TC-003: Ignore UNPAID Transactions")
    void testIgnoreUnpaidTransactions() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result, "DASH-TC-003 TDD: Expected result to not be null");
        assertEquals(0.0, result.getTotalRevenue());
    }

    // DASH-TC-004
    @Test
    @DisplayName("DASH-TC-004: Empty Data Returns Zero Values")
    void testEmptyDataReturnsZeroValues() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result, "DASH-TC-004 TDD: Expected result to not be null");
        assertEquals(0.0, result.getTotalRevenue());
        assertEquals(0.0, result.getOccupancyRate());
    }

    // DASH-TC-005
    @Test
    @DisplayName("DASH-TC-005: Occupancy Rate Calculation")
    void testOccupancyRateCalculation() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result, "DASH-TC-005 TDD: Expected result to not be null");
        assertEquals(30.0, result.getOccupancyRate());
    }

    // DASH-TC-007
    @Test
    @DisplayName("DASH-TC-007: Category Filter (SPA only)")
    void testCategoryFilterSpaOnly() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "SPA");
        assertNotNull(result, "DASH-TC-007 TDD: Expected result to not be null");
        assertEquals(2000000.0, result.getSpaRevenue());
        assertEquals(0.0, result.getFbRevenue());
    }
}
