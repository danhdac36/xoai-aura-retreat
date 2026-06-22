package com.AuraMoon.auramoon.dashboard.dto;

import lombok.Data;
import java.util.List;

@Data
public class RevenueDashboardDTO {
    private Double packageRevenue = 0.0;
    private Double spaRevenue = 0.0;
    private Double fbRevenue = 0.0;
    private Double totalRevenue = 0.0;
    private Double occupancyRate = 0.0;
    private Double therapistUtilization = 0.0;
    private List<TransactionSummary> recentTransactions;
    private List<TrendItem> monthlyTrend;

    @Data
    public static class TrendItem {
        private String label;
        private Double revenue;
        private Double percentage;
        
        public TrendItem(String label, Double revenue, Double percentage) {
            this.label = label;
            this.revenue = revenue;
            this.percentage = percentage;
        }
    }

    public Double getPackagePercentage() {
        return (totalRevenue != null && totalRevenue > 0) ? (packageRevenue / totalRevenue) * 100 : 0.0;
    }

    public Double getSpaPercentage() {
        return (totalRevenue != null && totalRevenue > 0) ? (spaRevenue / totalRevenue) * 100 : 0.0;
    }

    public Double getFbPercentage() {
        return (totalRevenue != null && totalRevenue > 0) ? (fbRevenue / totalRevenue) * 100 : 0.0;
    }

    public Double getSpaOffset() {
        return -getPackagePercentage();
    }

    public Double getFbOffset() {
        return -(getPackagePercentage() + getSpaPercentage());
    }
}
