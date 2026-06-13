package com.AuraMoon.auramoon.report.service.impl;

import com.AuraMoon.auramoon.report.dto.ReportDataDTO;
import com.AuraMoon.auramoon.report.service.ReportService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public ReportDataDTO generateReportData(LocalDate startDate, LocalDate endDate, String reportType) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        return null;
    }

    @Override
    public byte[] exportToExcel(ReportDataDTO reportData) {
        return new byte[0];
    }
}
