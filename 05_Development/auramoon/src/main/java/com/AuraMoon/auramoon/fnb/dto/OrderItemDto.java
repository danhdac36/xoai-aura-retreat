package com.AuraMoon.auramoon.fnb.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Integer menuItemId;
    private Integer quantity;
}
