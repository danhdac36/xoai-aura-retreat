package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionRequest;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealSelectionServiceTest {

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MealOrderRepository mealOrderRepository;

    @Mock
    private MealOrderItemRepository mealOrderItemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @InjectMocks
    private MealSelectionService mealSelectionService;

    private MenuItem itemNormal;
    private MenuItem itemAllergy;
    private MenuItem itemMeat;

    @BeforeEach
    void setUp() {
        itemNormal = MenuItem.builder()
                .itemName("Steamed Rice")
                .price(new BigDecimal("10.00"))
                .ingredient("rice, water")
                .isAvailable(true)
                .build();
        itemNormal.setId(1);

        itemAllergy = MenuItem.builder()
                .itemName("Peanut Butter Toast")
                .price(new BigDecimal("15.00"))
                .ingredient("bread, peanut butter")
                .isAvailable(true)
                .build();
        itemAllergy.setId(2);

        itemMeat = MenuItem.builder()
                .itemName("Beef Noodles")
                .price(new BigDecimal("25.00"))
                .ingredient("noodles, beef broth, beef slices")
                .isAvailable(true)
                .build();
        itemMeat.setId(3);
    }

    @Test
    void testGetFilteredMenuForGuest_NoProfile() {
        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.empty());

        List<MenuItem> result = mealSelectionService.getFilteredMenuForGuest(1);

        assertEquals(3, result.size());
        assertTrue(result.contains(itemNormal));
        assertTrue(result.contains(itemAllergy));
        assertTrue(result.contains(itemMeat));
    }

    @Test
    void testGetFilteredMenuForGuest_WithAllergy() {
        DietaryProfile profile = DietaryProfile.builder()
                .userId(1)
                .foodAllergies("peanut")
                .dietaryPreference("")
                .build();

        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.of(profile));

        List<MenuItem> result = mealSelectionService.getFilteredMenuForGuest(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(itemNormal));
        assertFalse(result.contains(itemAllergy)); // Filtered out due to peanut
        assertTrue(result.contains(itemMeat));
    }

    @Test
    void testGetFilteredMenuForGuest_WithVeganPreference() {
        DietaryProfile profile = DietaryProfile.builder()
                .userId(1)
                .foodAllergies("")
                .dietaryPreference("vegan")
                .build();

        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.of(profile));

        List<MenuItem> result = mealSelectionService.getFilteredMenuForGuest(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(itemNormal));
        assertTrue(result.contains(itemAllergy));
        assertFalse(result.contains(itemMeat)); // Filtered out due to beef
    }

    @Test
    void testSelectDailyMeals_Success() {
        MealSelectionRequest request = new MealSelectionRequest();
        request.setGuestId(1);
        request.setBookingId(10);
        request.setMealDate(LocalDate.of(2026, 6, 10));
        request.setMealType("Lunch");
        request.setMenuItemIds(Collections.singletonList(1));
        request.setNote("No ice");

        Booking booking = Booking.builder().guestId(1).build();
        booking.setId(10);

        GuestFolio folio = GuestFolio.builder()
                .bookingId(10)
                .totalExtraFb(BigDecimal.ZERO)
                .totalPackageAmount(new BigDecimal("500.00"))
                .build();
        folio.setId(20);

        when(bookingRepository.findById(10)).thenReturn(Optional.of(booking));
        when(guestFolioRepository.findByBookingId(10)).thenReturn(Optional.of(folio));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.empty());
        when(menuItemRepository.findById(1)).thenReturn(Optional.of(itemNormal));

        MealOrder savedOrder = MealOrder.builder().id(100).build();
        when(mealOrderRepository.save(any(MealOrder.class))).thenReturn(savedOrder);

        MealSelectionResponse response = mealSelectionService.selectDailyMeals(request);

        assertEquals("SUCCESS", response.getStatus());
        verify(mealOrderRepository, times(1)).save(any(MealOrder.class));
        verify(mealOrderItemRepository, times(1)).save(any(MealOrderItem.class));
        verify(guestFolioRepository, times(1)).save(any(GuestFolio.class));
        assertEquals(new BigDecimal("10.00"), folio.getTotalExtraFb());
        assertEquals(new BigDecimal("510.00"), folio.getFinalAmount());
    }

    @Test
    void testSelectDailyMeals_AllergyViolation() {
        MealSelectionRequest request = new MealSelectionRequest();
        request.setGuestId(1);
        request.setBookingId(10);
        request.setMealDate(LocalDate.of(2026, 6, 10));
        request.setMealType("Lunch");
        request.setMenuItemIds(Collections.singletonList(2)); // Item 2 has peanut

        Booking booking = Booking.builder().guestId(1).build();
        booking.setId(10);

        GuestFolio folio = GuestFolio.builder().bookingId(10).build();

        DietaryProfile profile = DietaryProfile.builder()
                .userId(1)
                .foodAllergies("peanut")
                .build();

        when(bookingRepository.findById(10)).thenReturn(Optional.of(booking));
        when(guestFolioRepository.findByBookingId(10)).thenReturn(Optional.of(folio));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.of(profile));
        when(menuItemRepository.findById(2)).thenReturn(Optional.of(itemAllergy));

        MealSelectionResponse response = mealSelectionService.selectDailyMeals(request);

        assertEquals("ALLERGY_VIOLATION", response.getStatus());
        assertNotNull(response.getDetails());
        assertEquals(1, response.getDetails().size());
        assertTrue(response.getDetails().get(0).contains("violating allergy: peanut"));
        verify(mealOrderRepository, never()).save(any(MealOrder.class));
    }
}
