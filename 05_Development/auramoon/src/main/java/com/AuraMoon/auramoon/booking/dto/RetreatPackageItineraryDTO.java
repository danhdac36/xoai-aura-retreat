package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackageItineraryDTO {
    private Integer dayNumber;
    private String activityName;
    private String serviceCode;
    private String location;
    private String description;
}
