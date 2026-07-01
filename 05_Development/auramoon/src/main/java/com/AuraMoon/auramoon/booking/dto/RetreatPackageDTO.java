package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackageDTO {
    private Integer id;
    private String typePackage;
    private String packageName;
    private Integer durationDays;
    private String description;
    private BigDecimal price;
}
