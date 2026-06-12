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

    private static final List<String> FALLBACK_IMAGES = Arrays.asList(
        "/img/pexels-alesiakozik-6544376.jpg",
        "/img/pexels-arina-krasnikova-6654115.jpg",
        "/img/pexels-elizabeth-zernetska-86424040-9001223.jpg",
        "/img/pexels-kamrujjamanjewel-24866519.jpg",
        "/img/pexels-leongsan-35132140.jpg",
        "/img/pexels-rachel-claire-6127215.jpg",
        "/img/pexels-saveurssecretes-6289992.jpg",
        "/img/pexels-spike-yuu-926249888-19999942.jpg",
        "/img/pexels-thu-huynh-639083784-19141541.jpg",
        "/img/anna-pelzer-IGfIGP5ONV0-unsplash.jpg",
        "/img/chad-montano-eeqbbemH9-c-unsplash.jpg",
        "/img/lily-banse--YHSwy6uqvk-unsplash.jpg",
        "/img/joseph-gonzalez-zcUgjyqEwe8-unsplash.jpg",
        "/img/pexels-lucasandrade-19781592.jpg",
        "/img/image3_202409300146524974.jpg"
    );

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

            // 1. Check Allergy Violations (BR-06)
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

            // 2. Check Recommendations based on Dietary Preferences (Vegan / Chay)
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

    // ─── Image resolution: keyword → local /img/ file ─────────────────────────
    private String resolveLocalImageUrl(String itemName, Integer itemId) {
        if (itemName == null) return FALLBACK_IMAGES.get(0);
        String lower = itemName.toLowerCase();

        if (lower.contains("hồi") || lower.contains("salmon"))
            return "/img/anna-tukhfatullina-food-photographer-stylist-Mzy-OjtCI70-unsplash.jpg";
        if (lower.contains("salad") || lower.contains("rau trộn"))
            return "/img/anna-pelzer-IGfIGP5ONV0-unsplash.jpg";
        if (lower.contains("phở"))
            return "/img/Pho-ga-ha-noi.jpg";
        if (lower.contains("bún bò"))
            return "/img/Bún_bò_Huế.jpg";
        if (lower.contains("bún chả"))
            return "/img/Bún_chả_Vietnamese_food.jpg";
        if (lower.contains("bánh xèo"))
            return "/img/banhxeo.jpg";
        if (lower.contains("bánh cuốn"))
            return "/img/banhcuon.jpg";
        if (lower.contains("bánh bèo"))
            return "/img/BanhBeo2.jpg";
        if (lower.contains("gỏi cuốn") || lower.contains("goi cuon"))
            return "/img/goi-cuon-nha-hang-qua-ngon.jpg";
        if (lower.contains("bánh canh"))
            return "/img/1d3dbc6e-banh-canh-cua-sai-gon-2-min.jpg";
        if (lower.contains("hủ tiếu"))
            return "/img/Hu-tieu-nam-vang(2).jpg";
        if (lower.contains("bún thịt nướng"))
            return "/img/bun-thit-nuong-kieu-mien-nam.jpg";
        if (lower.contains("nem rán") || lower.contains("chả giò"))
            return "/img/cach-lam-mon-nem-ran-thom-ngon-chuan-vi-don-gian-tai-nha-avt-1200x676.jpg";
        if (lower.contains("cơm") && (lower.contains("gà") || lower.contains("hội an")))
            return "/img/dd2876f6-com-ga-hoi-an.jpg";
        if (lower.contains("tôm") || lower.contains("shrimp"))
            return "/img/istockphoto-1299419373-612x612.jpg";
        if (lower.contains("cơm") || lower.contains("rice"))
            return "/img/image3_202409300146524974.jpg";
        if (lower.contains("nước ép") || lower.contains("juice") || lower.contains("sinh tố") || lower.contains("trà"))
            return "/img/istockphoto-2214231242-612x612.jpg";
        if (lower.contains("gà") || lower.contains("chicken"))
            return "/img/Pho-ga-ha-noi.jpg";
        if (lower.contains("bò") || lower.contains("beef"))
            return "/img/Bún_bò_Huế.jpg";
        if (lower.contains("heo") || lower.contains("lợn") || lower.contains("pork"))
            return "/img/Bún_chả_Vietnamese_food.jpg";
        if (lower.contains("cá ") || lower.contains("fish"))
            return "/img/joseph-gonzalez-zcUgjyqEwe8-unsplash.jpg";
        if (lower.contains("bún") || lower.contains("mì ") || lower.contains("miến"))
            return "/img/bun-thit-nuong-kieu-mien-nam.jpg";
        // Rotate fallback images by item ID for visual variety
        int idx = (itemId != null ? Math.abs(itemId) : 0) % FALLBACK_IMAGES.size();
        return FALLBACK_IMAGES.get(idx);
    }

    private void enrichNutritionFactsViewModel(MenuItemViewModel vm) {
        String name = vm.getItemName() != null ? vm.getItemName().toLowerCase() : "";
        // Local image mapping (BR: no DB change)
        String imageUrl = resolveLocalImageUrl(vm.getItemName(), vm.getId());
        vm.setImageUrl(imageUrl);
        if (imageUrl != null && imageUrl.startsWith("/img/")) {
            vm.setImageFileName(imageUrl.substring(5));
        } else {
            vm.setImageFileName(imageUrl);
        }
        // Nutrition facts by dish type
        if (name.contains("hồi") || name.contains("salmon")) {
            vm.setCalories(345); vm.setProtein("32g"); vm.setCarbs("12g"); vm.setFats("18g"); vm.setFiber("2g");
        } else if (name.contains("salad")) {
            vm.setCalories(210); vm.setProtein("8g"); vm.setCarbs("24g"); vm.setFats("14g"); vm.setFiber("6g");
        } else if (name.contains("cơm") || name.contains("rice bowl")) {
            vm.setCalories(290); vm.setProtein("15g"); vm.setCarbs("48g"); vm.setFats("5g"); vm.setFiber("9g");
        } else if (name.contains("tôm") || name.contains("shrimp")) {
            vm.setCalories(280); vm.setProtein("22g"); vm.setCarbs("15g"); vm.setFats("12g"); vm.setFiber("3g");
        } else if (name.contains("phở") || name.contains("bún") || name.contains("mì ") || name.contains("hủ tiếu")) {
            vm.setCalories(380); vm.setProtein("20g"); vm.setCarbs("55g"); vm.setFats("8g"); vm.setFiber("3g");
        } else if (name.contains("gà") || name.contains("chicken")) {
            vm.setCalories(320); vm.setProtein("28g"); vm.setCarbs("20g"); vm.setFats("10g"); vm.setFiber("2g");
        } else if (name.contains("bò") || name.contains("beef")) {
            vm.setCalories(350); vm.setProtein("30g"); vm.setCarbs("18g"); vm.setFats("15g"); vm.setFiber("1g");
        } else if (name.contains("nước ép") || name.contains("sinh tố") || name.contains("trà")) {
            vm.setCalories(80); vm.setProtein("1g"); vm.setCarbs("20g"); vm.setFats("0g"); vm.setFiber("1g");
        } else {
            vm.setCalories(150); vm.setProtein("4g"); vm.setCarbs("18g"); vm.setFats("3g"); vm.setFiber("2g");
        }
    }

    // ─── UC19: All menu items for À-La-Carte tab ───────────────────────────────
    public List<MenuItemViewModel> getAllMenuItemsForAlacarte(Integer guestId) {
        List<MenuItem> allItems = menuItemRepository.findAll().stream()
                .filter(MenuItem::getIsAvailable)
                .collect(Collectors.toList());

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);
        List<String> allergies = new ArrayList<>();
        if (profileOpt.isPresent() && profileOpt.get().getFoodAllergies() != null) {
            String raw = profileOpt.get().getFoodAllergies();
            if (!raw.isBlank()) {
                allergies = Arrays.stream(raw.split("[,;]"))
                        .map(String::trim).map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }

        List<MenuItemViewModel> result = new ArrayList<>();
        for (MenuItem item : allItems) {
            MenuItemViewModel vm = new MenuItemViewModel();
            vm.setId(item.getId());
            vm.setItemName(item.getItemName());
            vm.setPrice(item.getPrice());
            vm.setIngredient(item.getIngredient());
            vm.setIsAvailable(item.getIsAvailable());
            enrichNutritionFactsViewModel(vm);

            String itemNameLower = item.getItemName().toLowerCase();
            String ingredientLower = item.getIngredient() != null ? item.getIngredient().toLowerCase() : "";
            List<String> violated = new ArrayList<>();

            for (String allergy : allergies) {
                String check = allergy;
                if ("peanut".equals(allergy))    check = "đậu phộng";
                else if ("đậu phộng".equals(allergy)) check = "peanut";
                else if ("shrimp".equals(allergy))    check = "tôm";
                else if ("tôm".equals(allergy))       check = "shrimp";
                else if ("cashew".equals(allergy))    check = "hạt điều";
                else if ("hạt điều".equals(allergy))  check = "cashew";
                else if ("hải sản".equals(allergy))   check = "seafood";
                else if ("seafood".equals(allergy))   check = "hải sản";

                if (itemNameLower.contains(allergy) || ingredientLower.contains(allergy)
                        || itemNameLower.contains(check) || ingredientLower.contains(check)) {
                    String friendly = allergy;
                    if ("peanut".equals(allergy))  friendly = "đậu phộng";
                    if ("shrimp".equals(allergy) || "tôm".equals(allergy)) friendly = "hải sản";
                    if ("cashew".equals(allergy))  friendly = "hạt điều";
                    if (!violated.contains(friendly)) violated.add(friendly);
                }
            }

            if (!violated.isEmpty()) {
                vm.setIsAvailableForGuest(false);
                vm.setWarningMessage("Món ăn này có chứa " + String.join(" và ", violated) + ", nằm trong danh sách dị ứng của bạn.");
            } else {
                vm.setIsAvailableForGuest(true);
                vm.setWarningMessage(null);
            }
            vm.setIsRecommended(false); // No recommendation badge for à la carte
            result.add(vm);
        }
        return result;
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

        // BR-11: A-la-carte phải ghi phí vào Guest Folio
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
