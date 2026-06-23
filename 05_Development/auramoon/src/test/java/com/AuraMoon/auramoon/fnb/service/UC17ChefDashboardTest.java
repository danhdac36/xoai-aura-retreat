package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.ChefDashboardOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.OrderItemDto;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UC17ChefDashboardTest {

    @Mock
    private MealOrderRepository mealOrderRepository;

    @Mock
    private MealOrderItemRepository mealOrderItemRepository;

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @InjectMocks
    private MealOrderServiceImpl mealOrderService;

    @Test
    @DisplayName("FNB-TC-003 - Lấy danh sách đơn đặt món trong ngày thành công")
    public void getChefDashboardOrders_returnsOrdersSuccessfully() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Object[] row1 = new Object[]{1, 100, 10, "VILLA_01", "Ghi chú 1", "PENDING", startOfDay.plusHours(10)};
        List<Object[]> rows = Collections.singletonList(row1);

        when(mealOrderRepository.findChefDashboardOrdersByDateRange(any(), any())).thenReturn(rows);
        
        DietaryProfile profile = DietaryProfile.builder()
                .userId(10)
                .foodAllergies("Peanuts")
                .build();
        when(dietaryProfileRepository.findByUserId(anyInt())).thenReturn(Optional.of(profile));

        Object[] itemRow1 = new Object[]{5, "Món A", 2, BigDecimal.valueOf(50000), "tỏi, thịt"};
        when(mealOrderItemRepository.findChefDashboardItemsByOrderId(anyInt())).thenReturn(Collections.singletonList(itemRow1));

        // Act
        List<ChefDashboardOrderResponse> results = mealOrderService.getChefDashboardOrders(today);

        // Assert
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getOrderId());
        assertEquals("Peanuts", results.get(0).getFoodAllergies());
        assertEquals("VILLA_01", results.get(0).getPlaceOrder());
        assertEquals(1, results.get(0).getItems().size());
        assertEquals("Món A", results.get(0).getItems().get(0).getItemName());
    }

    @Test
    @DisplayName("FNB-TC-004 - Che giấu dữ liệu bệnh lý (Data Minimization)")
    public void getChefDashboardOrders_enforcesDataMinimization() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Object[] row1 = new Object[]{2, 101, 11, "VILLA_02", "None", "PREPARING", startOfDay.plusHours(12)};
        List<Object[]> rows = Collections.singletonList(row1);

        when(mealOrderRepository.findChefDashboardOrdersByDateRange(any(), any())).thenReturn(rows);
        
        // Giả lập DietaryProfile chỉ chứa thông tin thức ăn. Các thông tin MedicalHistory nằm ở bảng khác 
        // và KHÔNG ĐƯỢC query/join vào Service này.
        DietaryProfile profile = DietaryProfile.builder()
                .userId(11)
                .foodAllergies("Shellfish")
                // medicalHistory = "Heart disease" -> Lớp Service hoàn toàn không chứa trường này
                .build();
        when(dietaryProfileRepository.findByUserId(anyInt())).thenReturn(Optional.of(profile));
        when(mealOrderItemRepository.findChefDashboardItemsByOrderId(anyInt())).thenReturn(List.of());

        // Act
        List<ChefDashboardOrderResponse> results = mealOrderService.getChefDashboardOrders(today);

        // Assert
        assertEquals(1, results.size());
        
        // Đảm bảo đối tượng trả về (ChefDashboardOrderResponse) chỉ chứa trường foodAllergies
        // và KHÔNG có bất kỳ thuộc tính nào chứa thông tin y tế nhạy cảm.
        ChefDashboardOrderResponse response = results.get(0);
        assertEquals("Shellfish", response.getFoodAllergies());
        
        // Assert Object type doesn't have medicalHistory
        assertThrows(NoSuchMethodException.class, () -> {
            ChefDashboardOrderResponse.class.getMethod("getMedicalHistory");
        });
    }
}
