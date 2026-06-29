package com.AuraMoon.auramoon.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatItineraryDto {
    private String generalDescription;
    private List<String> dailyActivities;
}
