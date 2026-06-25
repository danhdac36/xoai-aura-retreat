package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.dto.OrderItemDto;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
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

    @Mock
    private AesDataEncryptor aesDataEncryptor;

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @Mock
    private MealOrderRepository mealOrderRepository;

    @Mock
    private MealOrderItemRepository mealOrderItemRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

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

        when(aesDataEncryptor.convertToEntityAttribute(any())).thenAnswer(invocation -> invocation.getArgument(0));
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

    @Test
    @DisplayName("UC16 - Khách đặt buffet với số lượng lớn hơn số người trong phòng thành công")
    public void createMealOrder_buffetQuantityExceedsGuestsLimit_succeeds() {
        // Arrange
        Integer guestId = 1;
        Integer bookingId = 100;

        Booking booking = Booking.builder()
                .guestId(guestId)
                .bookingStatus("Checked-In")
                .totalGuests(2) // 2 guests
                .build();
        booking.setId(bookingId);

        MenuItem item = MenuItem.builder()
                .itemName("Phở bò")
                .price(BigDecimal.valueOf(0))
                .isAvailable(true)
                .build();
        item.setId(1);

        GuestFolio folio = GuestFolio.builder().build();
        folio.setId(200);

        MealOrder savedOrder = MealOrder.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .orderStatus("PENDING")
                .build();
        savedOrder.setId(77);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(folio));
        when(menuItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(dietaryProfileRepository.findByUserId(guestId)).thenReturn(Optional.empty());
        when(aesDataEncryptor.convertToEntityAttribute(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mealOrderRepository.findByBookingId(bookingId)).thenReturn(List.of());
        when(folioItemRepository.findByGuestFolioId(any())).thenReturn(List.of());
        when(mealOrderRepository.save(any(MealOrder.class))).thenReturn(savedOrder);

        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .isExtraCharge(false) // free buffet
                .servingTime("08:30")
                .items(List.of(OrderItemDto.builder().menuItemId(1).quantity(5).build())) // quantity = 5 > 2 guests
                .build();

        // Act
        com.AuraMoon.auramoon.fnb.dto.MealOrderResponse response = mealOrderService.createMealOrder(request);

        // Assert
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        assertEquals(77, response.getMealOrderId());
        assertEquals("PENDING", response.getOrderStatus());
        verify(mealOrderRepository, times(1)).save(any(MealOrder.class));
    }
}
