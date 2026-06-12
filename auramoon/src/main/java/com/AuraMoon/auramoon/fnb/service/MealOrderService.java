package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MealPrepResponse;
import com.AuraMoon.auramoon.fnb.dto.OrderItemDto;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealOrderService {

    private final MealOrderRepository mealOrderRepository;
    private final MealOrderItemRepository mealOrderItemRepository;
    private final DietaryProfileRepository dietaryProfileRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public MealOrderService(MealOrderRepository mealOrderRepository,
                            MealOrderItemRepository mealOrderItemRepository,
                            DietaryProfileRepository dietaryProfileRepository,
                            BookingRepository bookingRepository,
                            UserRepository userRepository) {
        this.mealOrderRepository = mealOrderRepository;
        this.mealOrderItemRepository = mealOrderItemRepository;
        this.dietaryProfileRepository = dietaryProfileRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

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

        // Standard save logic
        MealOrder order = MealOrder.builder()
                .guestId(request.getGuestId())
                .bookingId(request.getBookingId())
                .folioId(request.getBookingId()) // Mock/default folioId to match bookingId
                .orderedAt(LocalDateTime.now())
                .orderedBy(request.getGuestId())
                .placeOrder("Lunch") // Default value
                .note(request.getNote())
                .orderStatus("PENDING")
                .build();

        MealOrder savedOrder = mealOrderRepository.save(order);
        return new MealOrderResponse("Meal order has been recorded successfully.", "SUCCESS");
    }

    @Transactional
    public MealOrderResponse updateMealOrderStatus(Integer orderId, String status) {
        if (orderId == null) {
            return new MealOrderResponse("Meal order ID is required", "FAILED");
        }

        if (status == null || status.isBlank()) {
            return new MealOrderResponse("Meal order status is required", "FAILED");
        }

        Optional<MealOrder> orderOpt = mealOrderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return new MealOrderResponse("Meal order not found", "FAILED");
        }

        MealOrder order = orderOpt.get();
        String currentStatus = order.getOrderStatus();
        if (currentStatus == null) {
            currentStatus = "PENDING";
        }

        String targetStatus = status.trim().toUpperCase();

        if (currentStatus.equalsIgnoreCase(targetStatus)) {
            return new MealOrderResponse("Meal order status has been updated successfully.", currentStatus);
        }

        // Strict state transition validation (BR-16)
        boolean isValid = false;
        if ("PENDING".equalsIgnoreCase(currentStatus)) {
            if ("PREPARING".equalsIgnoreCase(targetStatus)) {
                isValid = true;
            }
        } else if ("PREPARING".equalsIgnoreCase(currentStatus)) {
            if ("READY".equalsIgnoreCase(targetStatus) || "READY FOR DELIVERY".equalsIgnoreCase(targetStatus)) {
                isValid = true;
            }
        }

        if (!isValid) {
            return new MealOrderResponse("Invalid state transition from " + currentStatus + " to " + targetStatus, "FAILED");
        }

        order.setOrderStatus(targetStatus);
        mealOrderRepository.save(order);

        return new MealOrderResponse("Meal order status has been updated successfully.", targetStatus);
    }

    @Transactional(readOnly = true)
    public List<MealPrepResponse> getDailyMealOrders(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<MealOrder> orders = mealOrderRepository.findAllByOrderedAtBetween(start, end);

        List<MealPrepResponse> responseList = new ArrayList<>();
        for (MealOrder order : orders) {
            String guestName = "";
            if (order.getGuestId() != null) {
                guestName = userRepository.findById(order.getGuestId())
                        .map(com.AuraMoon.auramoon.auth.entity.User::getFullName)
                        .orElse("");
            }

            String roomNumber = "";
            if (order.getBookingId() != null) {
                roomNumber = bookingRepository.findById(order.getBookingId())
                        .map(com.AuraMoon.auramoon.booking.entity.Booking::getAssignedVilla)
                        .map(com.AuraMoon.auramoon.booking.entity.Villa::getVillaCode)
                        .orElse("");
            }

            // Data Minimization: only query and return dietary allergies (food_allergies field), hiding physical medical data (UC20)
            String foodAllergies = "";
            if (order.getGuestId() != null) {
                foodAllergies = dietaryProfileRepository.findByUserId(order.getGuestId())
                        .map(com.AuraMoon.auramoon.fnb.entity.DietaryProfile::getFoodAllergies)
                        .orElse("");
            }

            List<MealOrderItem> items = mealOrderItemRepository.findAllByMealOrderId(order.getId());
            List<OrderItemDto> itemDtos = new ArrayList<>();
            for (MealOrderItem item : items) {
                itemDtos.add(new OrderItemDto(
                        item.getMenuItem() != null ? item.getMenuItem().getId() : null,
                        item.getMenuItem() != null ? item.getMenuItem().getItemName() : "",
                        item.getQuantity() != null ? item.getQuantity() : 0
                ));
            }

            responseList.add(new MealPrepResponse(
                    order.getId(),
                    order.getBookingId(),
                    order.getGuestId(),
                    guestName,
                    roomNumber,
                    order.getOrderedAt() != null ? order.getOrderedAt().toString() : "",
                    order.getPlaceOrder(),
                    order.getNote(),
                    order.getOrderStatus(),
                    foodAllergies,
                    itemDtos
            ));
        }

        return responseList;
    }
}