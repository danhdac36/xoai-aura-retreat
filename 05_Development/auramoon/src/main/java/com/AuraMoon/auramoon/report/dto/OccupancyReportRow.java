package com.AuraMoon.auramoon.report.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OccupancyReportRow {
    private LocalDate date;
    private int totalVillas;
    private int occupiedVillas;
    private double occupancyRate;
}
