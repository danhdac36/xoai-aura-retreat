# ĐẶC TẢ CHI TIẾT MÃ NGUỒN BACKEND - UC16 & UC19
**Dự án:** Xoai Aura Retreat Management System (HOS-03)  
**Phân hệ:** F&B (Food & Beverage)  
**Quy chuẩn tuân thủ:** Rule 1 (Spring Boot MVC), Rule 2 (Mã nguồn bọc trong file Markdown)

Tài liệu này chứa toàn bộ mã nguồn Backend của **UC16 (Khách chọn bữa ăn dinh dưỡng)** và **UC19 (Gọi món ngoài)** đã được refactor sang Spring Boot MVC thuần túy, loại bỏ các cổng kết nối RESTful API.

---

## MỤC LỤC
1. [DTOs (Data Transfer Objects)](#1-dtos-data-transfer-objects)
2. [Service Layer (Nhiệp vụ lọc và đặt món)](#2-service-layer-nhiệp-vụ-lọc-và-đặt-món)
3. [Controller Layer (Spring MVC Thymeleaf)](#3-controller-layer-spring-mvc-thymeleaf)

---

## 1. DTOs (Data Transfer Objects)

### 📄 [MealSelectionForm.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealSelectionForm.java)
```java
package com.AuraMoon.auramoon.fnb.dto;

import java.time.LocalDate;
import java.util.List;

public class MealSelectionForm {
    private Integer guestId;
    private Integer bookingId;
    private LocalDate mealDate;
    private String mealType;
    private List<Integer> menuItemIds;
    private String note;

    public MealSelectionForm() {}

    public Integer getGuestId() { return guestId; }
    public void setGuestId(Integer guestId) { this.guestId = guestId; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public List<Integer> getMenuItemIds() { return menuItemIds; }
    public void setMenuItemIds(List<Integer> menuItemIds) { this.menuItemIds = menuItemIds; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
```

### 📄 [MenuItemViewModel.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MenuItemViewModel.java)
```java
package com.AuraMoon.auramoon.fnb.dto;

import java.math.BigDecimal;

public class MenuItemViewModel {
    private Integer id;
    private String itemName;
    private BigDecimal price;
    private String ingredient;
    private Boolean isAvailable;
    private String imageUrl;
    private Integer calories;
    private String protein;
    private String carbs;
    private String fats;
    private String fiber;
    private Boolean isAvailableForGuest;
    private String warningMessage;
    private Boolean isRecommended;

    public MenuItemViewModel() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public String getProtein() { return protein; }
    public void setProtein(String protein) { this.protein = protein; }

    public String getCarbs() { return carbs; }
    public void setCarbs(String carbs) { this.carbs = carbs; }

    public String getFats() { return fats; }
    public void setFats(String fats) { this.fats = fats; }

    public String getFiber() { return fiber; }
    public void setFiber(String fiber) { this.fiber = fiber; }

    public Boolean getIsAvailableForGuest() { return isAvailableForGuest; }
    public void setIsAvailableForGuest(Boolean isAvailableForGuest) { this.isAvailableForGuest = isAvailableForGuest; }

    public String getWarningMessage() { return warningMessage; }
    public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }

    public Boolean getIsRecommended() { return isRecommended; }
    public void setIsRecommended(Boolean isRecommended) { this.isRecommended = isRecommended; }
}
```

### 📄 [MealSelectionResponse.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealSelectionResponse.java)
```java
package com.AuraMoon.auramoon.fnb.dto;

import java.util.List;

public class MealSelectionResponse {
    private String message;
    private String status;
    private List<String> details;

    public MealSelectionResponse() {}

    public MealSelectionResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public MealSelectionResponse(String message, String status, List<String> details) {
        this.message = message;
        this.status = status;
        this.details = details;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
```

---

## 2. Service Layer (Nhiệp vụ lọc và đặt món)

### 📄 [MealSelectionService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealSelectionService.java)
```java
package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.*;
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
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
    private final FolioItemRepository folioItemRepository;

    public MealSelectionService(DietaryProfileRepository dietaryProfileRepository,
                                MenuItemRepository menuItemRepository,
                                MealOrderRepository mealOrderRepository,
                                MealOrderItemRepository mealOrderItemRepository,
                                BookingRepository bookingRepository,
                                GuestFolioRepository guestFolioRepository,
                                FolioItemRepository folioItemRepository) {
        this.dietaryProfileRepository = dietaryProfileRepository;
        this.menuItemRepository = menuItemRepository;
        this.mealOrderRepository = mealOrderRepository;
        this.mealOrderItemRepository = mealOrderItemRepository;
        this.bookingRepository = bookingRepository;
        this.guestFolioRepository = guestFolioRepository;
        this.folioItemRepository = folioItemRepository;
    }

    public List<MenuItemViewModel> getPersonalizedMenu(Integer guestId) {
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

        List<MenuItemViewModel> responseList = new ArrayList<>();
        for (MenuItem item : allItems) {
            MenuItemViewModel response = new MenuItemViewModel();
            response.setId(item.getId());
            response.setItemName(item.getItemName());
            response.setPrice(item.getPrice());
            response.setIngredient(item.getIngredient());
            response.setIsAvailable(item.getIsAvailable());

            enrichNutritionFactsViewModel(response);

            // BR-06: Lọc / đánh dấu món dị ứng
            List<String> violatedAllergies = new ArrayList<>();
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

            // Đánh dấu món ăn khuyên dùng (Chay / Vegan)
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

    private void enrichNutritionFactsViewModel(MenuItemViewModel response) {
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
    public MealSelectionResponse submitMealSelection(MealSelectionForm form) {
        if (form.getGuestId() == null) {
            return new MealSelectionResponse("Guest ID is required", "FAILED");
        }
        if (form.getBookingId() == null) {
            return new MealSelectionResponse("Booking ID is required", "FAILED");
        }
        if (form.getMealDate() == null) {
            return new MealSelectionResponse("Meal date is required", "FAILED");
        }
        if (form.getMealType() == null || form.getMealType().isBlank()) {
            return new MealSelectionResponse("Meal type is required", "FAILED");
        }
        if (form.getMenuItemIds() == null || form.getMenuItemIds().isEmpty()) {
            return new MealSelectionResponse("At least one menu item must be selected", "FAILED");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(form.getBookingId());
        if (bookingOpt.isEmpty()) {
            return new MealSelectionResponse("Booking not found", "FAILED");
        }
        Booking booking = bookingOpt.get();
        if (!booking.getGuestId().equals(form.getGuestId())) {
            return new MealSelectionResponse("Booking does not belong to the specified guest", "FAILED");
        }

        Optional<GuestFolio> folioOpt = guestFolioRepository.findByBookingId(form.getBookingId());
        if (folioOpt.isEmpty()) {
            return new MealSelectionResponse("Folio not found for this booking", "FAILED");
        }
        GuestFolio folio = folioOpt.get();

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(form.getGuestId());
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

        for (Integer itemId : form.getMenuItemIds()) {
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

        // BR-16: trạng thái đơn hàng bắt đầu là Pending
        MealOrder order = MealOrder.builder()
                .bookingId(form.getBookingId())
                .folioId(folio.getId())
                .guestId(form.getGuestId())
                .orderedAt(form.getMealDate().atStartOfDay())
                .orderedBy(form.getGuestId())
                .placeOrder(form.getMealType())
                .note(form.getNote())
                .orderStatus("PENDING")
                .build();

        MealOrder savedOrder = mealOrderRepository.save(order);

        BigDecimal totalCost = BigDecimal.ZERO;
        List<String> itemNames = new ArrayList<>();
        for (MenuItem item : selectedItems) {
            BigDecimal itemPrice = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            totalCost = totalCost.add(itemPrice);
            itemNames.add(item.getItemName());

            MealOrderItem orderItem = MealOrderItem.builder()
                    .mealOrder(savedOrder)
                    .menuItem(item)
                    .quantity(1)
                    .price(itemPrice)
                    .build();

            mealOrderItemRepository.save(orderItem);
        }

        // BR-11: A-la-carte phải ghi phí vào Guest Folio (Thêm 5% phí phục vụ)
        if ("A-La-Carte".equalsIgnoreCase(form.getMealType()) && totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal serviceCharge = totalCost.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalWithServiceFee = totalCost.add(serviceCharge);

            BigDecimal currentExtraFb = folio.getTotalExtraFb() != null ? folio.getTotalExtraFb() : BigDecimal.ZERO;
            folio.setTotalExtraFb(currentExtraFb.add(totalWithServiceFee));

            BigDecimal totalPackage = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
            folio.setFinalAmount(totalPackage.add(folio.getTotalExtraFb()));

            guestFolioRepository.save(folio);

            // Tạo FolioItem tương ứng cho hóa đơn A_LA_CARTE
            FolioItem folioItem = FolioItem.builder()
                    .guestFolio(folio)
                    .serviceCategory("FNB")
                    .referenceId(savedOrder.getId())
                    .description("Gọi món ngoài (A-La-Carte): " + String.join(", ", itemNames))
                    .amount(totalWithServiceFee)
                    .createAt(LocalDateTime.now())
                    .createBy(form.getGuestId())
                    .status("PENDING")
                    .build();

            folioItemRepository.save(folioItem);
        }

        return new MealSelectionResponse("Daily meals selected successfully.", "SUCCESS");
    }
}
```

---

## 3. Controller Layer (Spring MVC Thymeleaf)

### 📄 [MealSelectionMvcController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionMvcController.java)
```java
package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionForm;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemViewModel;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.fnb.service.MealSelectionService;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import jakarta.persistence.EntityManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/fnb/selection")
public class MealSelectionMvcController {

    private final MealSelectionService mealSelectionService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final DietaryProfileRepository dietaryProfileRepository;
    private final MenuItemRepository menuItemRepository;
    private final GuestFolioRepository guestFolioRepository;
    private final EntityManager entityManager;

    public MealSelectionMvcController(MealSelectionService mealSelectionService,
                                      UserRepository userRepository,
                                      BookingRepository bookingRepository,
                                      DietaryProfileRepository dietaryProfileRepository,
                                      MenuItemRepository menuItemRepository,
                                      GuestFolioRepository guestFolioRepository,
                                      EntityManager entityManager) {
        this.mealSelectionService = mealSelectionService;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.dietaryProfileRepository = dietaryProfileRepository;
        this.menuItemRepository = menuItemRepository;
        this.guestFolioRepository = guestFolioRepository;
        this.entityManager = entityManager;
    }

    @GetMapping
    @Transactional
    public String getPersonalizedMenuPage(@RequestParam(required = false) Integer guestId, Model model) {
        if (guestId == null) {
            guestId = 1;
        }
        Optional<User> userOpt = userRepository.findById(guestId);
        if (userOpt.isEmpty()) {
            seedDemoDataInternal();
            userOpt = userRepository.findById(guestId);
        }

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy booking nào cho khách hàng.");
            return "fnb/selection";
        }

        User guest = userOpt.get();
        Booking activeBooking = bookings.get(0);
        List<MenuItemViewModel> filteredMenuItems = mealSelectionService.getPersonalizedMenu(guestId);
        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);

        model.addAttribute("guest", guest);
        model.addAttribute("booking", activeBooking);
        model.addAttribute("menuItems", filteredMenuItems);
        model.addAttribute("dietaryProfile", profileOpt.orElse(null));
        model.addAttribute("form", new MealSelectionForm());

        return "fnb/selection";
    }

    @PostMapping
    public String submitMealSelection(@ModelAttribute("form") MealSelectionForm form, RedirectAttributes redirectAttributes) {
        MealSelectionResponse response = mealSelectionService.submitMealSelection(form);

        if ("SUCCESS".equals(response.getStatus())) {
            redirectAttributes.addFlashAttribute("success", "Đã ghi nhận đặt bữa ăn " + form.getMealType() + " thành công cho ngày " + form.getMealDate() + "!");
        } else if ("ALLERGY_VIOLATION".equals(response.getStatus())) {
            redirectAttributes.addFlashAttribute("errors", response.getDetails());
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
        }

        return "redirect:/fnb/selection?guestId=" + form.getGuestId();
    }

    // Deprecated endpoints kept for backward compatibility and test consistency
    @GetMapping("/view")
    public String getFilteredMenuForGuest(@RequestParam Integer guestId, Model model, RedirectAttributes redirectAttributes) {
        return getPersonalizedMenuPage(guestId, model);
    }

    @PostMapping("/submit")
    public String selectDailyMeals(@RequestParam Integer guestId,
                                   @RequestParam Integer bookingId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mealDate,
                                   @RequestParam String mealType,
                                   @RequestParam(required = false) List<Integer> menuItemIds,
                                   @RequestParam(required = false) String note,
                                   RedirectAttributes redirectAttributes) {
        MealSelectionForm form = new MealSelectionForm();
        form.setGuestId(guestId);
        form.setBookingId(bookingId);
        form.setMealDate(mealDate);
        form.setMealType(mealType);
        form.setMenuItemIds(menuItemIds);
        form.setNote(note);
        return submitMealSelection(form, redirectAttributes);
    }
}
```
