package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.dto.OrderItemDto;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UC16MealSelectionTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private MealOrderServiceImpl mealOrderService;

    @Test
    @DisplayName("UC16 - Lấy thực đơn đã lọc dị ứng thành công cho khách")
    public void getFilteredMenu_guestHasDietaryProfile_returnsFilteredSafeMenu() {
        // Arrange
        Integer guestId = 1;
        Integer bookingId = 100;

        Booking booking = Booking.builder()
                .guestId(guestId)
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        DietaryProfile profile = DietaryProfile.builder()
                .userId(guestId)
                .foodAllergies("Peanuts")
                .build();

        MenuItem item1 = MenuItem.builder()
                .itemName("Món xào tỏi")
                .price(BigDecimal.valueOf(50000))
                .ingredient("tỏi, rau xanh, dầu ăn")
                .isAvailable(true)
                .build();
        item1.setId(1);

        MenuItem item2 = MenuItem.builder()
                .itemName("Gỏi khô bò")
                .price(BigDecimal.valueOf(60000))
                .ingredient("bò khô, rau thơm, đậu phộng, đu đủ")
                .isAvailable(true)
                .build();
        item2.setId(2);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(dietaryProfileRepository.findByUserId(guestId)).thenReturn(Optional.of(profile));
        when(menuItemRepository.findByIsAvailableTrue()).thenReturn(List.of(item1, item2));

        // Act
        List<MenuItemResponse> result = mealOrderService.getFilteredMenu(guestId, bookingId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Món xào tỏi", result.get(0).getItemName());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(dietaryProfileRepository, times(1)).findByUserId(guestId);
        verify(menuItemRepository, times(1)).findByIsAvailableTrue();
    }

    @Test
    @DisplayName("UC16 - Lấy thực đơn với Booking ID không hợp lệ ném ra ngoại lệ")
    public void getFilteredMenu_invalidBookingId_throwsValidationException() {
        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.getFilteredMenu(1, -5);
        });
        assertEquals("FNB-002", exception.getErrorCode());
    }

    @Test
    @DisplayName("UC16 - Lấy thực đơn khi không tìm thấy Booking ID ném lỗi 404")
    public void getFilteredMenu_bookingNotFound_throwsResourceNotFoundException() {
        // Arrange
        Integer guestId = 1;
        Integer bookingId = 99999;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.getFilteredMenu(guestId, bookingId);
        });
        assertEquals("FNB-003", exception.getErrorCode());
    }

    @Test
    @DisplayName("FNB-TC-SEC-001 - Khách A không thể gọi món bằng booking của Khách B (IDOR)")
    public void createMealOrder_guestDoesNotOwnBooking_throwsForbiddenException() {
        // Arrange
        Integer guestIdA = 1;
        Integer guestIdB = 2;
        Integer bookingId = 100;

        Booking booking = Booking.builder()
                .guestId(guestIdB)
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(bookingId)
                .guestId(guestIdA)
                .items(List.of(OrderItemDto.builder().menuItemId(1).quantity(1).build()))
                .build();

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.createMealOrder(request);
        });
        assertEquals("FNB-004", exception.getErrorCode());
        assertEquals("User does not own this booking.", exception.getMessage());
    }
}
