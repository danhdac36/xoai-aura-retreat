package com.AuraMoon.auramoon.spa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ScheduleDto {
    private Integer id;
    private String startTime;
    private String endTime;
    private String timeRange;
    private String customerName;
    private String treatmentName;
    private String roomName;
    private String notes;
    private String status;
    private String medicalConditions;
    private String injuries;

    // Repository
    public ScheduleDto(Integer id, LocalDateTime start, LocalDateTime end, String status,
            String customerName, String treatmentName, String roomName, String notes,
            String medicalConditions, String injuries) {
        this.id = id;
        this.status = status;
        this.customerName = customerName;
        this.treatmentName = treatmentName;
        this.roomName = roomName;
        this.notes = notes;
        this.medicalConditions = medicalConditions;
        this.injuries = injuries;

        if (start != null && end != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            this.startTime = start.format(formatter);
            this.endTime = end.format(formatter);
            this.timeRange = this.startTime + " - " + this.endTime;
        }
    }
}