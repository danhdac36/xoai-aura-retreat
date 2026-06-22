package com.AuraMoon.auramoon.report.dto;

import lombok.Data;

@Data
public class OccupancyReportRow {
    private String monthLabel;
    private int totalVillas;
    private int occupiedVillas;
    private double occupancyRate;
}
