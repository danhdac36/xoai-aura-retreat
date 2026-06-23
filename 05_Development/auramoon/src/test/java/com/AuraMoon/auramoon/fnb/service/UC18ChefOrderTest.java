package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UC18ChefOrderTest {

    @Mock
    private MealOrderRepository mealOrderRepository;

    @InjectMocks
    private MealOrderServiceImpl mealOrderService;

    @Test
    @DisplayName("FNB-TC-005 - Cập nhật trạng thái chuẩn bị hợp lệ")
    public void updatePrepStatus_validOrderAndChefRole_updatesStatusAndSaves() {
        // Arrange
        Integer orderId = 55;
        MealOrder order = MealOrder.builder()
                .bookingId(100)
                .folioId(200)
                .guestId(1)
                .orderStatus("PENDING")
                .build();
        order.setId(orderId);

        when(mealOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        mealOrderService.updatePrepStatus(orderId, "PREPARING");

        // Assert
        assertEquals("PREPARING", order.getOrderStatus());
        verify(mealOrderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("FNB-TC-006 - Chặn cập nhật lùi trạng thái")
    public void updatePrepStatus_invalidStatusTransition_throwsValidationException() {
        // Arrange
        Integer orderId = 55;
        MealOrder order = MealOrder.builder()
                .bookingId(100)
                .folioId(200)
                .guestId(1)
                .orderStatus("READY")
                .build();
        order.setId(orderId);

        when(mealOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.updatePrepStatus(orderId, "PENDING"); // READY -> PENDING is invalid
        });
        assertEquals("FNB-001", exception.getErrorCode());
    }

    @Test
    @DisplayName("FNB-TC-007 - Cập nhật trạng thái đơn hàng không tồn tại")
    public void updatePrepStatus_orderIdNotFound_throwsNotFoundException() {
        // Arrange
        Integer orderId = 9999;
        when(mealOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.updatePrepStatus(orderId, "PREPARING");
        });
        assertEquals("FNB-003", exception.getErrorCode());
    }
}
