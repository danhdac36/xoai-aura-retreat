package com.AuraMoon.auramoon.fnb.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
    private Integer id;
    private String itemName;
    private BigDecimal price;
    private String ingredient;
    private Boolean isAvailable;
}
