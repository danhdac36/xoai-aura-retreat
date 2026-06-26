package com.AuraMoon.auramoon.yoga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaRegistrationResponse {
    private Integer registrationId;
    private Integer bookingId;
    private Integer yogaScheduleId;
    private String className;
    private String location;
    private LocalDateTime startTime;
    private String status;
}
