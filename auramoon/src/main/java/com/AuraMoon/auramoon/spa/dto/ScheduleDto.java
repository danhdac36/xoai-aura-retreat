package com.AuraMoon.auramoon.spa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String startTime; // "HH:mm"
    private String endTime; // "HH:mm"
    private String timeRange; // "HH:mm - HH:mm"
    private String customerName;
    private String treatmentName;
    private String roomName;
    private String notes;
    private String status;
}
