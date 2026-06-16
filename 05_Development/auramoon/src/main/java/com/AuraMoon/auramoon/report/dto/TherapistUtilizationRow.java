package com.AuraMoon.auramoon.report.dto;

import lombok.Data;

@Data
public class TherapistUtilizationRow {
    private String therapistName;
    private int totalSessions;
    private int completedSessions;
    private int noShowSessions;
    private double utilizationRate;
}
