package com.AuraMoon.auramoon.fnb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChefDashboardOrderResponse {
    private Integer orderId;
    private Integer bookingId;
    private Integer guestId;
    private String placeOrder;
    private String note;
    private String orderStatus;
    private LocalDateTime orderedAt;
    private String foodAllergies;
    private Boolean hasAllergyWarning;
    private List<ChefDashboardItemResponse> items;
}
