package com.AuraMoon.auramoon.report.service;

import com.AuraMoon.auramoon.report.dto.ReportDataDTO;
import java.time.LocalDate;

public interface ReportService {
    ReportDataDTO generateReportData(LocalDate startDate, LocalDate endDate, String reportType);
    byte[] exportToExcel(ReportDataDTO reportData);
}
