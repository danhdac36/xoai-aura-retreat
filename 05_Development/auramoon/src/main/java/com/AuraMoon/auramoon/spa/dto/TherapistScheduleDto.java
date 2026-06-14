package com.AuraMoon.auramoon.spa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapistScheduleDto {
    private Integer scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String serviceName;
    private String roomName;
    private String note;
    private String status;
}
