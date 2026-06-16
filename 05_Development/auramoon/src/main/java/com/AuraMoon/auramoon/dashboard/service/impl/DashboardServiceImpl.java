package com.AuraMoon.auramoon.dashboard.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.dashboard.dto.RevenueDashboardDTO;
import com.AuraMoon.auramoon.dashboard.dto.TransactionSummary;
import com.AuraMoon.auramoon.dashboard.service.DashboardService;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final GuestFolioRepository guestFolioRepository;
    private final FolioItemRepository folioItemRepository;
    private final VillaRepository villaRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public RevenueDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate, String category) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now().with(LocalTime.MAX);
        
        // 1. Get GuestFolios that are PAID
        List<GuestFolio> folios = guestFolioRepository.findByStatusAndCreatedAtBetween("PAID", startDateTime, endDateTime);
        
        double packageRevenue = 0.0;
        double spaRevenue = 0.0;
        double fbRevenue = 0.0;
        
        List<Integer> folioIds = folios.stream().map(GuestFolio::getId).collect(Collectors.toList());
        List<FolioItem> folioItems = new ArrayList<>();
        if (!folioIds.isEmpty()) {
            folioItems = folioItemRepository.findByGuestFolioIdIn(folioIds);
        }
        
        for (GuestFolio folio : folios) {
            if (folio.getTotalPackageAmount() != null) {
                packageRevenue += folio.getTotalPackageAmount().doubleValue();
            }
        }
        
        for (FolioItem item : folioItems) {
            if (item.getAmount() == null) continue;
            
            String cat = item.getServiceCategory();
            if (cat == null) cat = "Khác";
            
            if (cat.contains("Spa")) {
                spaRevenue += item.getAmount().doubleValue();
            } else if (cat.contains("F&B") || cat.contains("Ẩm thực")) {
                fbRevenue += item.getAmount().doubleValue();
            } else {
                // If there are other categories, we might want to put them into package or fb depending on logic.
                // Assuming it's F&B if not specified as Spa for now based on requirements, or ignore.
                fbRevenue += item.getAmount().doubleValue(); // Or add an 'otherRevenue' field.
            }
        }
        
        double totalRevenue = packageRevenue + spaRevenue + fbRevenue;
        
        // Filter by category if needed
        if ("SPA".equalsIgnoreCase(category)) {
            totalRevenue = spaRevenue;
            packageRevenue = 0.0;
            fbRevenue = 0.0;
        } else if ("FNB".equalsIgnoreCase(category)) {
            totalRevenue = fbRevenue;
            packageRevenue = 0.0;
            spaRevenue = 0.0;
        } else if ("PACKAGE".equalsIgnoreCase(category)) {
            totalRevenue = packageRevenue;
            spaRevenue = 0.0;
            fbRevenue = 0.0;
        }
        
        // 2. Calculate Occupancy Rate
        long totalVillas = villaRepository.countByIsDeleteFalse();
        long occupiedVillas = villaRepository.countByVillaStatusAndIsDeleteFalse("OCCUPIED");
        double occupancyRate = totalVillas > 0 ? ((double) occupiedVillas / totalVillas) * 100 : 0.0;
        
        // 3. Calculate Therapist Utilization
        long totalSchedules = scheduleRepository.countSchedules(startDateTime, endDateTime);
        long completedSchedules = scheduleRepository.countSchedulesByStatus("COMPLETED", startDateTime, endDateTime);
        double utilizationRate = totalSchedules > 0 ? ((double) completedSchedules / totalSchedules) * 100 : 0.0;
        
        // 4. Build Recent Transactions
        List<TransactionSummary> recentTransactions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (GuestFolio folio : folios.stream().limit(5).collect(Collectors.toList())) { // Get top 5
            Booking booking = bookingRepository.findById(folio.getBookingId()).orElse(null);
            String guestName = "Khách hàng ẩn danh";
            if (booking != null) {
                User guest = userRepository.findById(booking.getGuestId()).orElse(null);
                if (guest != null && guest.getFullName() != null) {
                    guestName = guest.getFullName();
                }
            }
            
            TransactionSummary summary = new TransactionSummary();
            summary.setGuestName(guestName);
            summary.setService("Thanh toán Checkout");
            summary.setStatus(folio.getStatus() != null ? folio.getStatus() : "Hoàn tất");
            summary.setDate(folio.getCreatedAt() != null ? folio.getCreatedAt().format(formatter) : "");
            summary.setAmount(folio.getFinalAmount() != null ? folio.getFinalAmount().doubleValue() : 0.0);
            recentTransactions.add(summary);
        }
        
        RevenueDashboardDTO dto = new RevenueDashboardDTO();
        dto.setPackageRevenue(packageRevenue);
        dto.setSpaRevenue(spaRevenue);
        dto.setFbRevenue(fbRevenue);
        dto.setTotalRevenue(totalRevenue);
        dto.setOccupancyRate(occupancyRate);
        dto.setTherapistUtilization(utilizationRate);
        dto.setRecentTransactions(recentTransactions);

        // 5. Calculate Monthly Trend (Last 3 months up to endDateTime)
        LocalDate trendEnd = endDate != null ? endDate : LocalDate.now();
        LocalDate trendStart = trendEnd.minusMonths(2).withDayOfMonth(1);
        List<GuestFolio> trendFolios = guestFolioRepository.findByStatusAndCreatedAtBetween("PAID", trendStart.atStartOfDay(), trendEnd.atTime(LocalTime.MAX));
        
        java.util.Map<String, Double> trendMap = new java.util.LinkedHashMap<>();
        for (int i = 2; i >= 0; i--) {
            LocalDate m = trendEnd.minusMonths(i);
            trendMap.put("Tháng " + m.getMonthValue(), 0.0);
        }
        
        for (GuestFolio folio : trendFolios) {
            if (folio.getCreatedAt() != null && folio.getFinalAmount() != null) {
                String monthLabel = "Tháng " + folio.getCreatedAt().getMonthValue();
                if (trendMap.containsKey(monthLabel)) {
                    trendMap.put(monthLabel, trendMap.get(monthLabel) + folio.getFinalAmount().doubleValue());
                }
            }
        }
        
        double maxTrend = trendMap.values().stream().max(Double::compareTo).orElse(0.0);
        List<RevenueDashboardDTO.TrendItem> monthlyTrend = new ArrayList<>();
        for (java.util.Map.Entry<String, Double> entry : trendMap.entrySet()) {
            double percentage = maxTrend > 0 ? (entry.getValue() / maxTrend) * 100 : 0.0;
            // set min height 2% so empty bar is visible
            percentage = Math.max(percentage, 2.0); 
            monthlyTrend.add(new RevenueDashboardDTO.TrendItem(entry.getKey(), entry.getValue(), percentage));
        }
        dto.setMonthlyTrend(monthlyTrend);
        
        return dto;
    }
}
