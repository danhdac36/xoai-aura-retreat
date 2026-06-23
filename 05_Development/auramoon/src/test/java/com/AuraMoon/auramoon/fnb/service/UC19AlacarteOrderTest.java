package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.OrderItemDto;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UC19AlacarteOrderTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MealOrderRepository mealOrderRepository;

    @Mock
    private MealOrderItemRepository mealOrderItemRepository;

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @InjectMocks
    private MealOrderServiceImpl mealOrderService;

    @Test
    @DisplayName("FNB-TC-008 - Gọi món A-la-carte và ghi nợ thành công")
    public void createAlacarteOrder_validAcarteSelection_savesOrderAndChargesFolio() {
        // Arrange
        Integer guestId = 1;
        Integer bookingId = 100;
        Integer folioId = 200;

        Booking booking = Booking.builder()
                .guestId(guestId)
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        MenuItem menuItem = MenuItem.builder()
                .itemName("Trà đá")
                .price(BigDecimal.valueOf(15000))
                .ingredient("trà, đá")
                .isAvailable(true)
                .build();
        menuItem.setId(1);

        GuestFolio folio = GuestFolio.builder()
                .bookingId(bookingId)
                .totalPackageAmount(BigDecimal.valueOf(1000000))
                .totalExtraFb(BigDecimal.valueOf(50000))
                .finalAmount(BigDecimal.valueOf(1050000))
                .status("Pending")
                .build();
        folio.setId(folioId);

        MealOrder savedOrder = MealOrder.builder()
                .bookingId(bookingId)
                .folioId(folioId)
                .guestId(guestId)
                .orderStatus("PENDING")
                .build();
        savedOrder.setId(55);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(dietaryProfileRepository.findByUserId(guestId)).thenReturn(Optional.empty());
        when(menuItemRepository.findById(1)).thenReturn(Optional.of(menuItem));
        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(folio));
        when(mealOrderRepository.save(any(MealOrder.class))).thenReturn(savedOrder);

        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .placeOrder("VILLA_V101")
                .note("Ít đá")
                .isExtraCharge(true)
                .items(List.of(OrderItemDto.builder().menuItemId(1).quantity(2).build())) // Total: 30000
                .build();

        // Act
        MealOrderResponse response = mealOrderService.createMealOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(55, response.getMealOrderId());
        assertEquals(BigDecimal.valueOf(30000), response.getTotalAmount());
        assertEquals("PENDING", response.getOrderStatus());

        // Verify Folio is updated: 50000 + 30000 = 80000
        assertEquals(BigDecimal.valueOf(80000), folio.getTotalExtraFb());
        assertEquals(BigDecimal.valueOf(1080000), folio.getFinalAmount());

        verify(guestFolioRepository, times(1)).save(folio);
        verify(folioItemRepository, times(1)).save(any(FolioItem.class));
        verify(mealOrderRepository, times(1)).save(any(MealOrder.class));
    }

    @Test
    @DisplayName("FNB-TC-009 - Gọi món với số lượng không hợp lệ")
    public void createAlacarteOrder_invalidQuantity_throwsValidationException() {
        // Arrange
        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(100)
                .guestId(1)
                .isExtraCharge(true)
                .items(List.of(OrderItemDto.builder().menuItemId(1).quantity(-5).build()))
                .build();

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.createMealOrder(request);
        });
        assertEquals("FNB-002", exception.getErrorCode());
    }

    @Test
    @DisplayName("FNB-TC-010 - Khách hàng gọi món không tồn tại")
    public void createAlacarteOrder_menuItemNotFound_throwsNotFoundException() {
        // Arrange
        Integer guestId = 1;
        Integer bookingId = 100;

        Booking booking = Booking.builder()
                .guestId(guestId)
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(menuItemRepository.findById(999)).thenReturn(Optional.empty());

        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .isExtraCharge(true)
                .items(List.of(OrderItemDto.builder().menuItemId(999).quantity(1).build()))
                .build();

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.createMealOrder(request);
        });
        assertEquals("FNB-003", exception.getErrorCode());
    }

    @Test
    @DisplayName("FNB-TC-011 - Khách gọi món chứa nguyên liệu cấm kỵ (Dị ứng)")
    public void createAlacarteOrder_allergenConflict_throwsAllergenConflictException() {
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
                .foodAllergies("Peanuts, Shellfish")
                .build();

        MenuItem menuItem = MenuItem.builder()
                .itemName("Gỏi đu đủ đậu phộng")
                .price(BigDecimal.valueOf(45000))
                .ingredient("đu đủ, cà rốt, đậu phộng, bò khô")
                .isAvailable(true)
                .build();
        menuItem.setId(10);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(dietaryProfileRepository.findByUserId(guestId)).thenReturn(Optional.of(profile));
        when(menuItemRepository.findById(10)).thenReturn(Optional.of(menuItem));

        MealOrderRequest request = MealOrderRequest.builder()
                .bookingId(bookingId)
                .guestId(guestId)
                .isExtraCharge(true)
                .items(List.of(OrderItemDto.builder().menuItemId(10).quantity(1).build()))
                .build();

        // Act & Assert
        FnbException exception = assertThrows(FnbException.class, () -> {
            mealOrderService.createMealOrder(request);
        });
        assertEquals("FNB-001", exception.getErrorCode());
        assertNotNull(exception.getDetails());
        assertEquals(1, exception.getDetails().size());
        assertEquals("menuItemId", exception.getDetails().get(0).getField());
        assertEquals(10, exception.getDetails().get(0).getRejectedValue());
        assertEquals("Peanuts", exception.getDetails().get(0).getAllergenMatched());
    }
}
