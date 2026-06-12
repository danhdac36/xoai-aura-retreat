package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MealPrepResponse;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealOrderServiceTest {

    @Mock
    private MealOrderRepository mealOrderRepository;

    @Mock
    private MealOrderItemRepository mealOrderItemRepository;

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealOrderService mealOrderService;

    private MealOrder orderPending;
    private MealOrder orderPreparing;
    private MealOrder orderReady;
    private User testUser;
    private Booking testBooking;
    private Villa testVilla;
    private DietaryProfile testDietaryProfile;
    private MenuItem testMenuItem;
    private MealOrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        orderPending = MealOrder.builder()
                .id(1)
                .bookingId(10)
                .guestId(5)
                .orderedAt(LocalDateTime.of(2026, 6, 12, 12, 0))
                .placeOrder("Lunch")
                .note("No peanut")
                .orderStatus("PENDING")
                .build();

        orderPreparing = MealOrder.builder()
                .id(2)
                .bookingId(10)
                .guestId(5)
                .orderedAt(LocalDateTime.of(2026, 6, 12, 13, 0))
                .placeOrder("Dinner")
                .note("")
                .orderStatus("PREPARING")
                .build();

        orderReady = MealOrder.builder()
                .id(3)
                .bookingId(10)
                .guestId(5)
                .orderedAt(LocalDateTime.of(2026, 6, 12, 14, 0))
                .placeOrder("A-La-Carte")
                .note("")
                .orderStatus("READY")
                .build();

        testUser = User.builder()
                .id(5)
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        testVilla = Villa.builder()
                .id(100)
                .villaCode("Villa 3")
                .build();

        testBooking = Booking.builder()
                .id(10)
                .guestId(5)
                .assignedVilla(testVilla)
                .build();

        testDietaryProfile = DietaryProfile.builder()
                .id(20)
                .userId(5)
                .foodAllergies("peanut, shrimp")
                .dietaryPreference("vegan")
                .build();

        testMenuItem = MenuItem.builder()
                .id(50)
                .itemName("Vegan Rice Bowl")
                .price(new BigDecimal("12.00"))
                .ingredient("rice, avocado, tofu")
                .isAvailable(true)
                .build();

        testOrderItem = MealOrderItem.builder()
                .id(200)
                .mealOrder(orderPending)
                .menuItem(testMenuItem)
                .quantity(2)
                .price(new BigDecimal("12.00"))
                .build();
    }

    @Test
    void testGetDailyMealOrders_MaskingAndFields() {
        LocalDate date = LocalDate.of(2026, 6, 12);
        when(mealOrderRepository.findAllByOrderedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(orderPending));
        when(userRepository.findById(5)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(10)).thenReturn(Optional.of(testBooking));
        when(dietaryProfileRepository.findByUserId(5)).thenReturn(Optional.of(testDietaryProfile));
        when(mealOrderItemRepository.findAllByMealOrderId(1)).thenReturn(Arrays.asList(testOrderItem));

        List<MealPrepResponse> result = mealOrderService.getDailyMealOrders(date);

        assertEquals(1, result.size());
        MealPrepResponse response = result.get(0);
        assertEquals(1, response.getOrderId());
        assertEquals("John Doe", response.getGuestName());
        assertEquals("Villa 3", response.getRoomNumber());
        assertEquals("PENDING", response.getOrderStatus());
        assertEquals("peanut, shrimp", response.getFoodAllergies()); // Food allergies present
        
        // Verify items list
        assertEquals(1, response.getItems().size());
        assertEquals("Vegan Rice Bowl", response.getItems().get(0).getItemName());
        assertEquals(2, response.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateMealOrderStatus_PendingToPreparing_Success() {
        when(mealOrderRepository.findById(1)).thenReturn(Optional.of(orderPending));

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(1, "PREPARING");

        assertEquals("PREPARING", response.getStatus());
        assertEquals("Meal order status has been updated successfully.", response.getMessage());
        verify(mealOrderRepository, times(1)).save(orderPending);
        assertEquals("PREPARING", orderPending.getOrderStatus());
    }

    @Test
    void testUpdateMealOrderStatus_PreparingToReady_Success() {
        when(mealOrderRepository.findById(2)).thenReturn(Optional.of(orderPreparing));

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(2, "READY");

        assertEquals("READY", response.getStatus());
        assertEquals("Meal order status has been updated successfully.", response.getMessage());
        verify(mealOrderRepository, times(1)).save(orderPreparing);
        assertEquals("READY", orderPreparing.getOrderStatus());
    }

    @Test
    void testUpdateMealOrderStatus_PreparingToPending_Blocked() {
        when(mealOrderRepository.findById(2)).thenReturn(Optional.of(orderPreparing));

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(2, "PENDING");

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMessage().contains("Invalid state transition"));
        verify(mealOrderRepository, never()).save(orderPreparing);
        assertEquals("PREPARING", orderPreparing.getOrderStatus()); // Unchanged
    }

    @Test
    void testUpdateMealOrderStatus_ReadyToPreparing_Blocked() {
        when(mealOrderRepository.findById(3)).thenReturn(Optional.of(orderReady));

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(3, "PREPARING");

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMessage().contains("Invalid state transition"));
        verify(mealOrderRepository, never()).save(orderReady);
        assertEquals("READY", orderReady.getOrderStatus()); // Unchanged
    }

    @Test
    void testUpdateMealOrderStatus_NoChange() {
        when(mealOrderRepository.findById(1)).thenReturn(Optional.of(orderPending));

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(1, "PENDING");

        assertEquals("PENDING", response.getStatus());
        assertEquals("Meal order status has been updated successfully.", response.getMessage());
        verify(mealOrderRepository, never()).save(any(MealOrder.class));
    }

    @Test
    void testUpdateMealOrderStatus_NotFound() {
        when(mealOrderRepository.findById(999)).thenReturn(Optional.empty());

        MealOrderResponse response = mealOrderService.updateMealOrderStatus(999, "PREPARING");

        assertEquals("FAILED", response.getStatus());
        assertEquals("Meal order not found", response.getMessage());
    }
}
