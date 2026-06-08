package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import org.springframework.stereotype.Service;

@Service
public class MealOrderService {

    public MealOrderResponse createMealOrder(MealOrderRequest request) {

        if (request.getGuestId() == null) {
            return new MealOrderResponse("Guest ID is required", "FAILED");
        }

        if (request.getBookingId() == null) {
            return new MealOrderResponse("Booking ID is required", "FAILED");
        }

        if (request.getMenuItemIds() == null || request.getMenuItemIds().isEmpty()) {
            return new MealOrderResponse("At least one menu item is required", "FAILED");
        }

        return new MealOrderResponse("Meal order has been recorded successfully.", "SUCCESS");
    }

    public MealOrderResponse updateMealOrderStatus(Long orderId, String status) {

        if (orderId == null) {
            return new MealOrderResponse("Meal order ID is required", "FAILED");
        }

        if (status == null || status.isBlank()) {
            return new MealOrderResponse("Meal order status is required", "FAILED");
        }

        return new MealOrderResponse("Meal order status has been updated successfully.", status);
    }
}