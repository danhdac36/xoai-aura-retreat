package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.service.MealOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fnb/orders")
public class MealOrderController {

    private final MealOrderService mealOrderService;

    public MealOrderController(MealOrderService mealOrderService) {
        this.mealOrderService = mealOrderService;
    }

    @PostMapping
    public MealOrderResponse createMealOrder(@RequestBody MealOrderRequest request) {
        return mealOrderService.createMealOrder(request);
    }

    @PutMapping("/{orderId}/status")
    public MealOrderResponse updateMealOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        return mealOrderService.updateMealOrderStatus(orderId, status);
    }
}