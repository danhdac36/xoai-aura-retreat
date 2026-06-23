package com.AuraMoon.auramoon.fnb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChefDashboardItemResponse {
    private Integer menuItemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private String ingredient;
    private Boolean isAllergic;
}
