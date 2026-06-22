package com.AuraMoon.auramoon.spa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapistDetailDto {
    private String therapistCode;
    private String fullName;
    private String status; // AVAILABLE, BUSY, OFF_DUTY
    private Long todaySessionCount;
}
