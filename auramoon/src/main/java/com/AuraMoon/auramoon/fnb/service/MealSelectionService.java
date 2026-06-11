package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionRequest;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
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
import java.math.RoundingMode;
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

    public List<MenuItemResponse> getFilteredMenuForGuest(Integer guestId) {
        List<MenuItem> allItems = menuItemRepository.findAll().stream()
                .filter(MenuItem::getIsAvailable)
                .collect(Collectors.toList());

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);
        List<String> allergies = new ArrayList<>();
        List<String> preferences = new ArrayList<>();

        if (profileOpt.isPresent()) {
            DietaryProfile profile = profileOpt.get();
            String allergiesStr = profile.getFoodAllergies();
            String preferenceStr = profile.getDietaryPreference();

            if (allergiesStr != null && !allergiesStr.isBlank()) {
                allergies = Arrays.stream(allergiesStr.split("[,;]"))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            if (preferenceStr != null && !preferenceStr.isBlank()) {
                preferences = Arrays.stream(preferenceStr.split("[,;]"))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }

        List<MenuItemResponse> responseList = new ArrayList<>();
        for (MenuItem item : allItems) {
            MenuItemResponse response = new MenuItemResponse();
            response.setId(item.getId());
            response.setItemName(item.getItemName());
            response.setPrice(item.getPrice());
            response.setIngredient(item.getIngredient());
            response.setIsAvailable(item.getIsAvailable());

            // Enrich nutrition facts and images dynamically based on the item name
            enrichNutritionFacts(response);

            // 1. Check Allergy Violations
            List<String> violatedAllergies = new ArrayList<>();
            String itemNameLower = item.getItemName().toLowerCase();
            String ingredientLower = item.getIngredient() != null ? item.getIngredient().toLowerCase() : "";

            for (String allergy : allergies) {
                // Support both English and Vietnamese mapping
                String checkAllergy = allergy;
                if ("peanut".equals(allergy)) checkAllergy = "đậu phộng";
                if ("đậu phộng".equals(allergy)) checkAllergy = "peanut";
                if ("shrimp".equals(allergy)) checkAllergy = "tôm";
                if ("tôm".equals(allergy)) checkAllergy = "shrimp";
                if ("cashew".equals(allergy)) checkAllergy = "hạt điều";
                if ("hạt điều".equals(allergy)) checkAllergy = "cashew";

                if (itemNameLower.contains(allergy) || ingredientLower.contains(allergy) ||
                    itemNameLower.contains(checkAllergy) || ingredientLower.contains(checkAllergy)) {
                    
                    // Translate to friendly names for UI warning
                    String friendlyName = allergy;
                    if ("peanut".equals(allergy)) friendlyName = "đậu phộng";
                    if ("shrimp".equals(allergy)) friendlyName = "hải sản";
                    if ("cashew".equals(allergy)) friendlyName = "hạt điều";
                    if (!violatedAllergies.contains(friendlyName)) {
                        violatedAllergies.add(friendlyName);
                    }
                }
            }

            if (!violatedAllergies.isEmpty()) {
                response.setIsAvailableForGuest(false);
                String listStr = String.join(" và ", violatedAllergies);
                response.setWarningMessage("Món ăn này có chứa " + listStr + ", nằm trong danh sách dị ứng của bạn.");
            } else {
                response.setIsAvailableForGuest(true);
                response.setWarningMessage(null);
            }

            // 2. Check Recommendations based on Dietary Preferences (e.g. vegan/chay)
            boolean isVegFriendly = true;
            List<String> animalProducts = Arrays.asList(
                "beef", "pork", "chicken", "meat", "seafood", "fish", "shrimp", "squid", "crab", "lobster", "snail",
                "bò", "heo", "lợn", "gà", "thịt", "cá", "tôm", "mực", "cua", "ghẹ", "ốc"
            );
            for (String animalProduct : animalProducts) {
                if (itemNameLower.contains(animalProduct) || ingredientLower.contains(animalProduct)) {
                    isVegFriendly = false;
                    break;
                }
            }

            boolean isRecommended = false;
            for (String pref : preferences) {
                if ((pref.contains("vegan") || pref.contains("chay")) && isVegFriendly) {
                    isRecommended = true;
                    break;
                }
            }
            response.setIsRecommended(isRecommended);

            responseList.add(response);
        }

        return responseList;
    }

    private void enrichNutritionFacts(MenuItemResponse response) {
        String name = response.getItemName().toLowerCase();
        if (name.contains("hồi") || name.contains("salmon")) {
            response.setImageUrl("https://lh3.googleusercontent.com/aida/ADBb0uhcj3_Ba-K6jKYvOIV8HbVPdW-XVgmUoglPtSZ7hBhwhteY74jw7D7zrT5GQ4IRHQGDCYpYRum1-ET2R5LhiA82cnSizWjQlfHkXTO-vlmBVNymbUEhlQ6b275jCnkSNIolq-xRPnLe9vu0vaGwV--laSH0jwalr4xCPlZmlYRorrca_LkNb9HP3imYXU4IhnCizTxIx0RXzzYXjwDDYk0Mt2y6uywbMTp338LC8fbJwrqdXhARUunUvjY");
            response.setCalories(345);
            response.setProtein("32g");
            response.setCarbs("12g");
            response.setFats("18g");
            response.setFiber("2g");
        } else if (name.contains("salad")) {
            response.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuDvCaliK_ZWDU9xw0wEt-Cb-v-FE0m9qYLyg9aIJaoAHWs_IhRNBvb2VMWfdwopQ3A7VMS23w5AgITj5H1A23dsi2p780AWGLAAiU-EQLSIu9lJFWGvbninxoq6GsNK6YzyDRhCsoAiGMjKiP0dU2fKLZUq6qyNRHXDHtSFd1dcciJxt7ByBaPPnrHoAOnQLS1YR-LaMij9YusMh0e9WYQFk0BFh18jz2Q6RS7cajwFNKWoXuDvcB3OI2SVl3Ziq61HrlbIbF3becQ");
            response.setCalories(210);
            response.setProtein("8g");
            response.setCarbs("24g");
            response.setFats("14g");
            response.setFiber("6g");
        } else if (name.contains("cơm") || name.contains("rice bowl")) {
            response.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCRAmP6rs8IPSfFwt6W7xbrPqUEGjAp2tBe1aPn9hYypoXFMQhntfq7elR5amMD2Bf9_us_dnC8V6gEoWW36D7u3XP2n_RzVM2CiYpZoAdllefRoX_7AnxPyyzBKB1WsBiQcO7gmPihQJXukRSgEfkLa_xr74R8ad2II3C4QY7czuFhNCERXOA3JeveliO6Y3VitdkMF_qZ82iYKsaUWnNZloDVC9vs1IwMo0CRH15VZi_2A0qMb28VqdjVzsgSsdhy5RIIsq5jxyQ");
            response.setCalories(290);
            response.setProtein("15g");
            response.setCarbs("48g");
            response.setFats("5g");
            response.setFiber("9g");
        } else if (name.contains("tôm") || name.contains("shrimp")) {
            response.setImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuAzZUuNZzYCO1zjBkT5-2CqC5apkrAnX-ryycO2n0itZ6abpJ-YECMO0GTu2LWHLLOKkmq4F8gczkuKH2bjLOKtecmnPSfx5gE4mnD90Fuj0WUZPtidEiqwDf3aPEo8q0Oxf8nXGoeEE-1Tvspi3sp6YdHSeNkwcNRGp8DHjMOef0GMC8cFHgr_6maW9bCdlcS_QoYM6TZzOnEToQ_y9p6nJKKC15YqSuS98e4dOjsCL02yTWUA9vu2Xht3Fze1cReJPEbNehvd4zU");
            response.setCalories(280);
            response.setProtein("22g");
            response.setCarbs("15g");
            response.setFats("12g");
            response.setFiber("3g");
        } else {
            response.setImageUrl("https://lh3.googleusercontent.com/aida/ADBb0uhcj3_Ba-K6jKYvOIV8HbVPdW-XVgmUoglPtSZ7hBhwhteY74jw7D7zrT5GQ4IRHQGDCYpYRum1-ET2R5LhiA82cnSizWjQlfHkXTO-vlmBVNymbUEhlQ6b275jCnkSNIolq-xRPnLe9vu0vaGwV--laSH0jwalr4xCPlZmlYRorrca_LkNb9HP3imYXU4IhnCizTxIx0RXzzYXjwDDYk0Mt2y6uywbMTp338LC8fbJwrqdXhARUunUvjY");
            response.setCalories(150);
            response.setProtein("4g");
            response.setCarbs("18g");
            response.setFats("3g");
            response.setFiber("2g");
        }
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
            if (!item.getIsAvailable()) {
                return new MealSelectionResponse("Menu item '" + item.getItemName() + "' is not available", "FAILED");
            }

            String itemNameLower = item.getItemName().toLowerCase();
            String ingredientLower = item.getIngredient() != null ? item.getIngredient().toLowerCase() : "";
            for (String allergy : allergies) {
                String checkAllergy = allergy;
                if ("peanut".equals(allergy)) checkAllergy = "đậu phộng";
                if ("đậu phộng".equals(allergy)) checkAllergy = "peanut";
                if ("shrimp".equals(allergy)) checkAllergy = "tôm";
                if ("tôm".equals(allergy)) checkAllergy = "shrimp";
                if ("cashew".equals(allergy)) checkAllergy = "hạt điều";
                if ("hạt điều".equals(allergy)) checkAllergy = "cashew";

                if (itemNameLower.contains(allergy) || ingredientLower.contains(allergy) ||
                    itemNameLower.contains(checkAllergy) || ingredientLower.contains(checkAllergy)) {
                    
                    String friendlyName = allergy;
                    if ("peanut".equals(allergy)) friendlyName = "đậu phộng";
                    if ("shrimp".equals(allergy)) friendlyName = "hải sản/tôm";
                    if ("cashew".equals(allergy)) friendlyName = "hạt điều";
                    allergyViolations.add("Món ăn '" + item.getItemName() + "' chứa chất gây dị ứng: " + friendlyName);
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

        // Bill to folio if it is A-La-Carte (UC19)
        // Calculated with 5% service charge as shown in mockup
        if ("A-La-Carte".equalsIgnoreCase(request.getMealType()) && totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal serviceCharge = totalCost.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalWithServiceFee = totalCost.add(serviceCharge);

            BigDecimal currentExtraFb = folio.getTotalExtraFb() != null ? folio.getTotalExtraFb() : BigDecimal.ZERO;
            folio.setTotalExtraFb(currentExtraFb.add(totalWithServiceFee));

            BigDecimal totalPackage = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
            folio.setFinalAmount(totalPackage.add(folio.getTotalExtraFb()));

            guestFolioRepository.save(folio);
        }

        return new MealSelectionResponse("Daily meals selected successfully.", "SUCCESS");
    }
}
