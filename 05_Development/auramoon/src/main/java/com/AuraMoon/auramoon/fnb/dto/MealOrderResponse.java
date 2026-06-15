package com.AuraMoon.auramoon.fnb.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealOrderResponse {
    private Integer mealOrderId;
    private Integer bookingId;
    private Integer guestId;
    private String placeOrder;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderedAt;
}
