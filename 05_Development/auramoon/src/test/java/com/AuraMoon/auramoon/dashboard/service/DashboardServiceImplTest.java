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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.billing.repository.GuestFolioRepository guestFolioRepository;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.booking.repository.VillaRepository villaRepository;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.booking.repository.BookingRepository bookingRepository;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.billing.repository.FolioItemRepository folioItemRepository;

    @org.mockito.Mock
    private com.AuraMoon.auramoon.auth.repository.UserRepository userRepository;

    // DASH-TC-001
    @Test
    @DisplayName("DASH-TC-001: Verify Revenue Calculation (Happy Path)")
    void testVerifyRevenueCalculation() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        com.AuraMoon.auramoon.billing.entity.GuestFolio folio = new com.AuraMoon.auramoon.billing.entity.GuestFolio();
        folio.setId(1);
        folio.setTotalPackageAmount(java.math.BigDecimal.valueOf(8000000));
        when(guestFolioRepository.findByStatusAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(java.util.List.of(folio));

        com.AuraMoon.auramoon.billing.entity.FolioItem fbItem = new com.AuraMoon.auramoon.billing.entity.FolioItem();
        fbItem.setServiceCategory("F&B");
        fbItem.setAmount(java.math.BigDecimal.valueOf(3000000));
        com.AuraMoon.auramoon.billing.entity.FolioItem spaItem = new com.AuraMoon.auramoon.billing.entity.FolioItem();
        spaItem.setServiceCategory("Spa");
        spaItem.setAmount(java.math.BigDecimal.valueOf(2000000));

        when(folioItemRepository.findByGuestFolioIdIn(any())).thenReturn(java.util.List.of(fbItem, spaItem));

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");

        assertNotNull(result);
        assertEquals(13000000.0, result.getTotalRevenue());
    }

    // DASH-TC-002
    @Test
    @DisplayName("DASH-TC-002: Date Range Filtering")
    void testDateRangeFiltering() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        com.AuraMoon.auramoon.billing.entity.GuestFolio folio = new com.AuraMoon.auramoon.billing.entity.GuestFolio();
        folio.setId(1);
        folio.setTotalPackageAmount(java.math.BigDecimal.valueOf(8000000));
        when(guestFolioRepository.findByStatusAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(java.util.List.of(folio));
        when(folioItemRepository.findByGuestFolioIdIn(any())).thenReturn(java.util.Collections.emptyList());

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result);
        assertEquals(8000000.0, result.getPackageRevenue());
    }

    // DASH-TC-003
    @Test
    @DisplayName("DASH-TC-003: Ignore UNPAID Transactions")
    void testIgnoreUnpaidTransactions() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        when(guestFolioRepository.findByStatusAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(java.util.Collections.emptyList()); // No paid transactions

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result);
        assertEquals(0.0, result.getTotalRevenue());
    }

    // DASH-TC-004
    @Test
    @DisplayName("DASH-TC-004: Empty Data Returns Zero Values")
    void testEmptyDataReturnsZeroValues() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        when(guestFolioRepository.findByStatusAndCreatedAtBetween(any(), any(), any())).thenReturn(java.util.Collections.emptyList());
        when(villaRepository.countByIsDeleteFalse()).thenReturn(0L);
        when(scheduleRepository.countSchedules(any(), any())).thenReturn(0L);

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result);
        assertEquals(0.0, result.getTotalRevenue());
        assertEquals(0.0, result.getOccupancyRate());
    }

    // DASH-TC-005
    @Test
    @DisplayName("DASH-TC-005: Occupancy Rate Calculation")
    void testOccupancyRateCalculation() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        when(villaRepository.countByIsDeleteFalse()).thenReturn(10L);
        when(villaRepository.countByVillaStatusAndIsDeleteFalse("OCCUPIED")).thenReturn(3L);

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result);
        assertEquals(30.0, result.getOccupancyRate());
    }

    // DASH-TC-007
    @Test
    @DisplayName("DASH-TC-007: Category Filter (SPA only)")
    void testCategoryFilterSpaOnly() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        com.AuraMoon.auramoon.billing.entity.GuestFolio folio = new com.AuraMoon.auramoon.billing.entity.GuestFolio();
        folio.setId(1);
        folio.setTotalPackageAmount(java.math.BigDecimal.valueOf(8000000));
        when(guestFolioRepository.findByStatusAndCreatedAtBetween(any(), any(), any()))
                .thenReturn(java.util.List.of(folio));

        com.AuraMoon.auramoon.billing.entity.FolioItem spaItem = new com.AuraMoon.auramoon.billing.entity.FolioItem();
        spaItem.setServiceCategory("Spa");
        spaItem.setAmount(java.math.BigDecimal.valueOf(2000000));

        when(folioItemRepository.findByGuestFolioIdIn(any())).thenReturn(java.util.List.of(spaItem));

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "SPA");
        assertNotNull(result);
        assertEquals(2000000.0, result.getSpaRevenue());
        assertEquals(2000000.0, result.getTotalRevenue());
        assertEquals(0.0, result.getFbRevenue());
    }

    // DASH-TC-008
    @Test
    @DisplayName("DASH-TC-008: Therapist Utilization Calculation")
    void testTherapistUtilizationCalculation() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        
        when(scheduleRepository.countSchedules(any(), any())).thenReturn(100L);
        when(scheduleRepository.countSchedulesByStatus(any(), any(), any())).thenReturn(75L);

        RevenueDashboardDTO result = dashboardService.getDashboardData(startDate, endDate, "ALL");
        assertNotNull(result);
        assertEquals(75.0, result.getTherapistUtilization());
    }
}
