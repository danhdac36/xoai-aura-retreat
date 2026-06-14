package com.AuraMoon.auramoon.report.service.impl;

import com.AuraMoon.auramoon.report.dto.ReportDataDTO;
import com.AuraMoon.auramoon.report.dto.OccupancyReportRow;
import com.AuraMoon.auramoon.report.dto.TherapistUtilizationRow;
import com.AuraMoon.auramoon.report.service.ReportService;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final VillaRepository villaRepository;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    public ReportDataDTO generateReportData(LocalDate startDate, LocalDate endDate, String reportType) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        ReportDataDTO dto = new ReportDataDTO();
        
        List<OccupancyReportRow> occupancyRows = new ArrayList<>();
        List<TherapistUtilizationRow> utilizationRows = new ArrayList<>();
        
        if ("OCCUPANCY".equalsIgnoreCase(reportType) || "ALL".equalsIgnoreCase(reportType)) {
            long totalVillas = villaRepository.countByIsDeleteFalse();
            
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                OccupancyReportRow row = new OccupancyReportRow();
                row.setDate(date);
                row.setTotalVillas((int) totalVillas);
                
                // Demo logic: count current occupied
                long occupied = villaRepository.countByVillaStatusAndIsDeleteFalse("OCCUPIED");
                row.setOccupiedVillas((int) occupied);
                row.setOccupancyRate(totalVillas > 0 ? ((double) occupied / totalVillas) * 100 : 0.0);
                occupancyRows.add(row);
            }
        }
        
        if ("UTILIZATION".equalsIgnoreCase(reportType) || "ALL".equalsIgnoreCase(reportType)) {
            TherapistUtilizationRow row = new TherapistUtilizationRow();
            row.setTherapistName("Tất cả chuyên viên");
            LocalDateTime s = startDate.atStartOfDay();
            LocalDateTime e = endDate.atTime(LocalTime.MAX);
            
            long total = scheduleRepository.countSchedules(s, e);
            long completed = scheduleRepository.countSchedulesByStatus("COMPLETED", s, e);
            
            row.setTotalSessions((int) total);
            row.setCompletedSessions((int) completed);
            row.setNoShowSessions((int) (total - completed));
            row.setUtilizationRate(total > 0 ? ((double) completed / total) * 100 : 0.0);
            
            utilizationRows.add(row);
        }
        
        dto.setOccupancyRows(occupancyRows);
        dto.setUtilizationRows(utilizationRows);
        
        double avgOcc = occupancyRows.stream().mapToDouble(OccupancyReportRow::getOccupancyRate).average().orElse(0.0);
        double avgUtil = utilizationRows.stream().mapToDouble(TherapistUtilizationRow::getUtilizationRate).average().orElse(0.0);
        
        dto.setAvgOccupancyRate(avgOcc);
        dto.setAvgUtilizationRate(avgUtil);
        
        return dto;
    }

    @Override
    public byte[] exportToExcel(LocalDate startDate, LocalDate endDate, String reportType) {
        ReportDataDTO data = generateReportData(startDate, endDate, reportType);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            if ("OCCUPANCY".equalsIgnoreCase(reportType) || "ALL".equalsIgnoreCase(reportType)) {
                Sheet sheet = workbook.createSheet("Tỷ lệ Lấp đầy");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Ngày");
                headerRow.createCell(1).setCellValue("Tổng số phòng");
                headerRow.createCell(2).setCellValue("Phòng có khách");
                headerRow.createCell(3).setCellValue("Tỷ lệ (%)");
                
                int rowIdx = 1;
                for (OccupancyReportRow rowData : data.getOccupancyRows()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(rowData.getDate().toString());
                    row.createCell(1).setCellValue(rowData.getTotalVillas());
                    row.createCell(2).setCellValue(rowData.getOccupiedVillas());
                    row.createCell(3).setCellValue(rowData.getOccupancyRate());
                }
            }
            
            if ("UTILIZATION".equalsIgnoreCase(reportType) || "ALL".equalsIgnoreCase(reportType)) {
                Sheet sheet = workbook.createSheet("Hiệu suất Chuyên viên");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Chuyên viên");
                headerRow.createCell(1).setCellValue("Tổng phiên");
                headerRow.createCell(2).setCellValue("Hoàn thành");
                headerRow.createCell(3).setCellValue("Vắng mặt");
                headerRow.createCell(4).setCellValue("Tỷ lệ (%)");
                
                int rowIdx = 1;
                for (TherapistUtilizationRow rowData : data.getUtilizationRows()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(rowData.getTherapistName());
                    row.createCell(1).setCellValue(rowData.getTotalSessions());
                    row.createCell(2).setCellValue(rowData.getCompletedSessions());
                    row.createCell(3).setCellValue(rowData.getNoShowSessions());
                    row.createCell(4).setCellValue(rowData.getUtilizationRate());
                }
            }
            
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Fail to generate Excel file: " + e.getMessage());
        }
    }
}
