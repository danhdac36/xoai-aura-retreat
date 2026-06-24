package com.AuraMoon.auramoon.fnb.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealOrderRequest {
    private Integer bookingId;
    private Integer guestId;
    private String placeOrder;
    private String note;
    private Boolean isExtraCharge;
    private String servingTime;
    private List<OrderItemDto> items;
}
