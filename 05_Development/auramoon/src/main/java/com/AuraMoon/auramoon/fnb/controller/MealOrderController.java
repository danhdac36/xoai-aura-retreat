package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.service.IMealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MealOrderController {

    @Autowired
    private IMealOrderService mealOrderService;

    @GetMapping("/api/v1/fnb/menu")
    public ResponseEntity<List<MenuItemResponse>> getFilteredMenu(
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("guestId") Integer guestId) {
        List<MenuItemResponse> menu = mealOrderService.getFilteredMenu(guestId, bookingId);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/api/v1/fnb/menu/all")
    public ResponseEntity<List<MenuItemResponse>> getAllMenu() {
        List<MenuItemResponse> menu = mealOrderService.getAllMenu();
        return ResponseEntity.ok(menu);
    }


    @PostMapping("/api/v1/fnb/meal-orders")
    public ResponseEntity<MealOrderResponse> createMealOrder(@RequestBody MealOrderRequest request) {
        MealOrderResponse response = mealOrderService.createMealOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/api/v1/fnb/chef/orders/{id}/status")
    public ResponseEntity<Void> updatePrepStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status) {
        mealOrderService.updatePrepStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fnb/uc16-meal-selection")
    public String getMealSelectionPage() {
        return "fnb/uc16-meal-selection";
    }

    @GetMapping("/fnb/uc19-alacarte-order")
    public String getAlacarteOrderPage() {
        return "fnb/uc19-alacarte-order";
    }
}

