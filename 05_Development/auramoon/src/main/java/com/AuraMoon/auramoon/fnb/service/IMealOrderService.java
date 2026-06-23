package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.ChefDashboardOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import java.time.LocalDate;
import java.util.List;

public interface IMealOrderService {

    List<MenuItemResponse> getFilteredMenu(Integer userId, Integer bookingId);

    MealOrderResponse createMealOrder(MealOrderRequest request);

    void updatePrepStatus(Integer orderId, String status);

    List<MenuItemResponse> getAllMenu();

    List<ChefDashboardOrderResponse> getChefDashboardOrders(LocalDate date);

    int getSumQuantityOfFreeMealOrdersToday(Integer bookingId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}

