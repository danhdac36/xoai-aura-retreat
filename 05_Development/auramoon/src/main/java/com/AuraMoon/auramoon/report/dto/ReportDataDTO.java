package com.AuraMoon.auramoon.report.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReportDataDTO {
    private List<OccupancyReportRow> occupancyRows;
    private List<TherapistUtilizationRow> utilizationRows;
    private double avgOccupancyRate;
    private double avgUtilizationRate;
}
