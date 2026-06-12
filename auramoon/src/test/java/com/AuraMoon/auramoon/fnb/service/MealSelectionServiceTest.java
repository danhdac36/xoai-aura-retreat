package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionForm;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemViewModel;
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
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
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

    @Mock
    private FolioItemRepository folioItemRepository;

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
    void testGetPersonalizedMenu_NoProfile() {
        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.empty());

        List<MenuItemViewModel> result = mealSelectionService.getPersonalizedMenu(1);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(MenuItemViewModel::getIsAvailableForGuest));
        assertTrue(result.stream().allMatch(item -> item.getWarningMessage() == null));
    }

    @Test
    void testGetPersonalizedMenu_WithAllergy() {
        DietaryProfile profile = DietaryProfile.builder()
                .userId(1)
                .foodAllergies("peanut")
                .dietaryPreference("")
                .build();

        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.of(profile));

        List<MenuItemViewModel> result = mealSelectionService.getPersonalizedMenu(1);

        assertEquals(3, result.size());
        
        // Find normal item (rice)
        MenuItemViewModel normalRes = result.stream().filter(item -> item.getId() == 1).findFirst().orElseThrow();
        assertTrue(normalRes.getIsAvailableForGuest());
        assertNull(normalRes.getWarningMessage());

        // Find allergy item (peanut toast)
        MenuItemViewModel allergyRes = result.stream().filter(item -> item.getId() == 2).findFirst().orElseThrow();
        assertFalse(allergyRes.getIsAvailableForGuest()); // Should be blocked
        assertNotNull(allergyRes.getWarningMessage());
        assertTrue(allergyRes.getWarningMessage().contains("dị ứng"));
    }

    @Test
    void testGetPersonalizedMenu_WithVeganPreference() {
        DietaryProfile profile = DietaryProfile.builder()
                .userId(1)
                .foodAllergies("")
                .dietaryPreference("vegan")
                .build();

        when(menuItemRepository.findAll()).thenReturn(Arrays.asList(itemNormal, itemAllergy, itemMeat));
        when(dietaryProfileRepository.findByUserId(1)).thenReturn(Optional.of(profile));

        List<MenuItemViewModel> result = mealSelectionService.getPersonalizedMenu(1);

        assertEquals(3, result.size());

        // Normal rice should be recommended (vegan)
        MenuItemViewModel normalRes = result.stream().filter(item -> item.getId() == 1).findFirst().orElseThrow();
        assertTrue(normalRes.getIsRecommended());

        // Beef noodles should NOT be recommended
        MenuItemViewModel meatRes = result.stream().filter(item -> item.getId() == 3).findFirst().orElseThrow();
        assertFalse(meatRes.getIsRecommended());
    }

    @Test
    void testSubmitMealSelection_Success() {
        MealSelectionForm form = new MealSelectionForm();
        form.setGuestId(1);
        form.setBookingId(10);
        form.setMealDate(LocalDate.of(2026, 6, 10));
        form.setMealType("Lunch");
        form.setMenuItemIds(Collections.singletonList(1));
        form.setNote("No ice");

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

        MealSelectionResponse response = mealSelectionService.submitMealSelection(form);

        assertEquals("SUCCESS", response.getStatus());
        verify(mealOrderRepository, times(1)).save(any(MealOrder.class));
        verify(mealOrderItemRepository, times(1)).save(any(MealOrderItem.class));
        
        // Billed to package, so extra F&B should remain zero
        verify(guestFolioRepository, never()).save(any(GuestFolio.class));
        assertEquals(BigDecimal.ZERO, folio.getTotalExtraFb());
    }

    @Test
    void testSubmitMealSelection_ALaCarte_Success() {
        MealSelectionForm form = new MealSelectionForm();
        form.setGuestId(1);
        form.setBookingId(10);
        form.setMealDate(LocalDate.of(2026, 6, 10));
        form.setMealType("A-La-Carte"); // UC19 context
        form.setMenuItemIds(Collections.singletonList(1)); // price = 10.00
        form.setNote("Extra spicy");

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

        MealSelectionResponse response = mealSelectionService.submitMealSelection(form);

        assertEquals("SUCCESS", response.getStatus());
        verify(mealOrderRepository, times(1)).save(any(MealOrder.class));
        verify(mealOrderItemRepository, times(1)).save(any(MealOrderItem.class));
        verify(guestFolioRepository, times(1)).save(any(GuestFolio.class));
        verify(folioItemRepository, times(1)).save(any());
        
        // 10.00 + 5% service charge (0.50) = 10.50
        assertEquals(new BigDecimal("10.50"), folio.getTotalExtraFb());
        assertEquals(new BigDecimal("510.50"), folio.getFinalAmount());
    }

    @Test
    void testSubmitMealSelection_AllergyViolation() {
        MealSelectionForm form = new MealSelectionForm();
        form.setGuestId(1);
        form.setBookingId(10);
        form.setMealDate(LocalDate.of(2026, 6, 10));
        form.setMealType("Lunch");
        form.setMenuItemIds(Collections.singletonList(2)); // Item 2 has peanut

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

        MealSelectionResponse response = mealSelectionService.submitMealSelection(form);

        assertEquals("ALLERGY_VIOLATION", response.getStatus());
        assertNotNull(response.getDetails());
        assertEquals(1, response.getDetails().size());
        assertTrue(response.getDetails().get(0).contains("chất gây dị ứng"));
        verify(mealOrderRepository, never()).save(any(MealOrder.class));
    }
}
