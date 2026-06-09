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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealSelectionService {

    private final DietaryProfileRepository dietaryProfileRepository;
    private final MenuItemRepository menuItemRepository;
    private final MealOrderRepository mealOrderRepository;
    private final MealOrderItemRepository mealOrderItemRepository;
    private final BookingRepository bookingRepository;
    private final GuestFolioRepository guestFolioRepository;

    public MealSelectionService(DietaryProfileRepository dietaryProfileRepository,
                                MenuItemRepository menuItemRepository,
                                MealOrderRepository mealOrderRepository,
                                MealOrderItemRepository mealOrderItemRepository,
                                BookingRepository bookingRepository,
                                GuestFolioRepository guestFolioRepository) {
        this.dietaryProfileRepository = dietaryProfileRepository;
        this.menuItemRepository = menuItemRepository;
        this.mealOrderRepository = mealOrderRepository;
        this.mealOrderItemRepository = mealOrderItemRepository;
        this.bookingRepository = bookingRepository;
        this.guestFolioRepository = guestFolioRepository;
    }

    public List<MenuItem> getFilteredMenuForGuest(Integer guestId) {
        List<MenuItem> allItems = menuItemRepository.findAll().stream()
                .filter(MenuItem::getIsAvailable)
                .filter(item -> item.getIsDelete() == null || !item.getIsDelete())
                .collect(Collectors.toList());

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);
        if (profileOpt.isEmpty()) {
            return allItems;
        }

        DietaryProfile profile = profileOpt.get();
        String allergiesStr = profile.getFoodAllergies();
        String preferenceStr = profile.getDietaryPreference();

        List<String> allergies = new ArrayList<>();
        if (allergiesStr != null && !allergiesStr.isBlank()) {
            allergies = Arrays.stream(allergiesStr.split("[,;]"))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<String> preferences = new ArrayList<>();
        if (preferenceStr != null && !preferenceStr.isBlank()) {
            preferences = Arrays.stream(preferenceStr.split("[,;]"))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        if (allergies.isEmpty() && preferences.isEmpty()) {
            return allItems;
        }

        List<MenuItem> filteredItems = new ArrayList<>();
        for (MenuItem item : allItems) {
            String itemNameLower = item.getItemName().toLowerCase();
            String ingredientLower = item.getIngredient() != null ? item.getIngredient().toLowerCase() : "";

            // 1. Filter by Allergies
            boolean hasAllergyViolation = false;
            for (String allergy : allergies) {
                if (itemNameLower.contains(allergy) || ingredientLower.contains(allergy)) {
                    hasAllergyViolation = true;
                    break;
                }
            }
            if (hasAllergyViolation) {
                continue;
            }

            // 2. Filter by Dietary Preferences (e.g. vegan/chay)
            boolean hasPreferenceViolation = false;
            for (String pref : preferences) {
                if (pref.contains("vegan") || pref.contains("chay")) {
                    List<String> animalProducts = Arrays.asList(
                        "beef", "pork", "chicken", "meat", "seafood", "fish", "shrimp",
                        "squid", "crab", "lobster", "snail", "bò", "heo", "lợn", "gà", "thịt", "cá", "tôm", "mực", "cua", "ghẹ", "ốc"
                    );
                    for (String animalProduct : animalProducts) {
                        if (itemNameLower.contains(animalProduct) || ingredientLower.contains(animalProduct)) {
                            hasPreferenceViolation = true;
                            break;
                        }
                    }
                }
            }
            if (hasPreferenceViolation) {
                continue;
            }

            filteredItems.add(item);
        }

        return filteredItems;
    }

    @Transactional
    public MealSelectionResponse selectDailyMeals(MealSelectionRequest request) {
        if (request.getGuestId() == null) {
            return new MealSelectionResponse("Guest ID is required", "FAILED");
        }
        if (request.getBookingId() == null) {
            return new MealSelectionResponse("Booking ID is required", "FAILED");
        }
        if (request.getMealDate() == null) {
            return new MealSelectionResponse("Meal date is required", "FAILED");
        }
        if (request.getMealType() == null || request.getMealType().isBlank()) {
            return new MealSelectionResponse("Meal type is required", "FAILED");
        }
        if (request.getMenuItemIds() == null || request.getMenuItemIds().isEmpty()) {
            return new MealSelectionResponse("At least one menu item must be selected", "FAILED");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(request.getBookingId());
        if (bookingOpt.isEmpty()) {
            return new MealSelectionResponse("Booking not found", "FAILED");
        }
        Booking booking = bookingOpt.get();
        if (!booking.getGuestId().equals(request.getGuestId())) {
            return new MealSelectionResponse("Booking does not belong to the specified guest", "FAILED");
        }

        Optional<GuestFolio> folioOpt = guestFolioRepository.findByBookingId(request.getBookingId());
        if (folioOpt.isEmpty()) {
            return new MealSelectionResponse("Folio not found for this booking", "FAILED");
        }
        GuestFolio folio = folioOpt.get();

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(request.getGuestId());
        List<String> allergies = new ArrayList<>();
        if (profileOpt.isPresent()) {
            String allergiesStr = profileOpt.get().getFoodAllergies();
            if (allergiesStr != null && !allergiesStr.isBlank()) {
                allergies = Arrays.stream(allergiesStr.split("[,;]"))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }

        List<MenuItem> selectedItems = new ArrayList<>();
        List<String> allergyViolations = new ArrayList<>();

        for (Integer itemId : request.getMenuItemIds()) {
            Optional<MenuItem> itemOpt = menuItemRepository.findById(itemId);
            if (itemOpt.isEmpty()) {
                return new MealSelectionResponse("Menu item with ID " + itemId + " not found", "FAILED");
            }
            MenuItem item = itemOpt.get();
            if (!item.getIsAvailable() || (item.getIsDelete() != null && item.getIsDelete())) {
                return new MealSelectionResponse("Menu item '" + item.getItemName() + "' is not available", "FAILED");
            }

            String itemNameLower = item.getItemName().toLowerCase();
            String ingredientLower = item.getIngredient() != null ? item.getIngredient().toLowerCase() : "";
            for (String allergy : allergies) {
                if (itemNameLower.contains(allergy) || ingredientLower.contains(allergy)) {
                    allergyViolations.add("Menu item '" + item.getItemName() + "' contains ingredient violating allergy: " + allergy);
                }
            }
            selectedItems.add(item);
        }

        if (!allergyViolations.isEmpty()) {
            return new MealSelectionResponse("Allergy validation failed. Selected items violate guest's allergy profile.", "ALLERGY_VIOLATION", allergyViolations);
        }

        MealOrder order = MealOrder.builder()
                .bookingId(request.getBookingId())
                .folioId(folio.getId())
                .guestId(request.getGuestId())
                .orderedAt(request.getMealDate().atStartOfDay())
                .orderedBy(request.getGuestId())
                .placeOrder(request.getMealType())
                .note(request.getNote())
                .orderStatus("PENDING")
                .build();

        MealOrder savedOrder = mealOrderRepository.save(order);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (MenuItem item : selectedItems) {
            BigDecimal itemPrice = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            totalCost = totalCost.add(itemPrice);

            MealOrderItem orderItem = MealOrderItem.builder()
                    .mealOrder(savedOrder)
                    .menuItem(item)
                    .quantity(1)
                    .price(itemPrice)
                    .build();

            mealOrderItemRepository.save(orderItem);
        }

        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentExtraFb = folio.getTotalExtraFb() != null ? folio.getTotalExtraFb() : BigDecimal.ZERO;
            folio.setTotalExtraFb(currentExtraFb.add(totalCost));

            BigDecimal totalPackage = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
            folio.setFinalAmount(totalPackage.add(folio.getTotalExtraFb()));

            guestFolioRepository.save(folio);
        }

        return new MealSelectionResponse("Daily meals selected successfully.", "SUCCESS");
    }
}
