package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.fnb.dto.*;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderItemRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MealOrderServiceImpl implements IMealOrderService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MealOrderRepository mealOrderRepository;

    @Autowired
    private MealOrderItemRepository mealOrderItemRepository;

    @Autowired
    private DietaryProfileRepository dietaryProfileRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestFolioRepository guestFolioRepository;

    @Autowired
    private FolioItemRepository folioItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenu() {
        List<MenuItem> allItems = menuItemRepository.findByIsAvailableTrue();
        List<MenuItemResponse> responses = new ArrayList<>();

        for (MenuItem item : allItems) {
            responses.add(MenuItemResponse.builder()
                    .id(item.getId())
                    .itemName(item.getItemName())
                    .price(item.getPrice())
                    .ingredient(item.getIngredient())
                    .isAvailable(item.getIsAvailable())
                    .build());
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getFilteredMenu(Integer userId, Integer bookingId) {
        if (userId == null || bookingId == null) {
            throw new FnbException("FNB-002", "User ID and Booking ID must not be null.");
        }
        if (userId <= 0 || bookingId <= 0) {
            throw new FnbException("FNB-002", "User ID and Booking ID must be positive.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new FnbException("FNB-003", "Booking not found with ID: " + bookingId));

        if (!booking.getGuestId().equals(userId)) {
            throw new FnbException("FNB-004", "User does not own this booking.");
        }

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(userId);
        String foodAllergies = profileOpt.map(DietaryProfile::getFoodAllergies).orElse("");

        List<MenuItem> allItems = menuItemRepository.findByIsAvailableTrue();
        List<MenuItemResponse> filtered = new ArrayList<>();

        for (MenuItem item : allItems) {
            if (isSafe(item, foodAllergies)) {
                filtered.add(MenuItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .price(item.getPrice())
                        .ingredient(item.getIngredient())
                        .isAvailable(item.getIsAvailable())
                        .build());
            }
        }

        return filtered;
    }

    @Override
    public MealOrderResponse createMealOrder(MealOrderRequest request) {
        if (request == null || request.getBookingId() == null || request.getGuestId() == null) {
            throw new FnbException("FNB-002", "Booking ID and Guest ID must not be null.");
        }
        if (request.getBookingId() <= 0 || request.getGuestId() <= 0) {
            throw new FnbException("FNB-002", "Booking ID and Guest ID must be positive.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new FnbException("FNB-002", "Meal order must contain at least one item.");
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new FnbException("FNB-002", "Booking not found with ID: " + request.getBookingId()));

        String bStatus = booking.getBookingStatus();
        if (bStatus == null || (!bStatus.equalsIgnoreCase("Checked-In") && !bStatus.equalsIgnoreCase("Checked-in")
                && !bStatus.equalsIgnoreCase("ACTIVE"))) {
            throw new FnbException("FNB-002", "Lượt đặt phòng không ở trạng thái ACTIVE tại thời điểm gọi món.");
        }

        if (!booking.getGuestId().equals(request.getGuestId())) {
            throw new FnbException("FNB-004", "User does not own this booking.");
        }

        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(request.getGuestId());
        String allergies = profileOpt.map(DietaryProfile::getFoodAllergies).orElse("");

        List<ErrorDetail> allergenConflicts = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto orderItemDto : request.getItems()) {
            if (orderItemDto.getQuantity() == null || orderItemDto.getQuantity() <= 0) {
                throw new FnbException("FNB-002", "Item quantity must be positive.");
            }

            MenuItem menuItem = menuItemRepository.findById(orderItemDto.getMenuItemId())
                    .orElseThrow(() -> new FnbException("FNB-003",
                            "Menu item not found with ID: " + orderItemDto.getMenuItemId()));

            if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
                throw new FnbException("FNB-003", "Món ăn hiện đã hết hoặc ngừng phục vụ.");
            }

            // Check allergen conflict
            String ingredient = menuItem.getIngredient();
            if (allergies != null && !allergies.trim().isEmpty() && ingredient != null) {
                String[] allergyArr = allergies.split(",");
                for (String allergy : allergyArr) {
                    String cleanAllergy = allergy.trim();
                    if (!cleanAllergy.isEmpty() && hasAllergyConflict(ingredient, cleanAllergy)) {
                        allergenConflicts.add(ErrorDetail.builder()
                                .field("menuItemId")
                                .rejectedValue(menuItem.getId())
                                .allergenMatched(cleanAllergy)
                                .build());
                    }
                }
            }

            BigDecimal itemCost = menuItem.getPrice().multiply(BigDecimal.valueOf(orderItemDto.getQuantity()));
            totalAmount = totalAmount.add(itemCost);
        }

        if (!allergenConflicts.isEmpty()) {
            MenuItem conflictItem = menuItemRepository.findById(request.getItems().get(0).getMenuItemId()).orElse(null);
            String itemName = conflictItem != null ? conflictItem.getItemName() : "Món ăn";
            throw new FnbException("FNB-001",
                    "Không thể gọi món: '" + itemName
                            + "' có chứa nguyên liệu gây dị ứng trong hồ sơ sức khỏe của bạn.",
                    allergenConflicts);
        }

        GuestFolio folio = guestFolioRepository.findByBookingId(booking.getId())
                .orElseThrow(() -> new FnbException("FNB-002",
                        "Resort Guest Folio not found for booking: " + booking.getId()));

        MealOrder order = MealOrder.builder()
                .bookingId(booking.getId())
                .folioId(folio.getId())
                .guestId(booking.getGuestId())
                .orderedAt(LocalDateTime.now())
                .orderedBy(request.getGuestId())
                .placeOrder(request.getPlaceOrder())
                .note(request.getNote())
                .orderStatus("PENDING")
                .build();

        order = mealOrderRepository.save(order);

        List<MealOrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto orderItemDto : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(orderItemDto.getMenuItemId()).get();
            MealOrderItem orderItem = MealOrderItem.builder()
                    .mealOrder(order)
                    .menuItem(menuItem)
                    .quantity(orderItemDto.getQuantity())
                    .price(menuItem.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        mealOrderItemRepository.saveAll(orderItems);
        mealOrderItemRepository.saveAll(orderItems);

        // Update Folio charges
        BigDecimal currentExtra = folio.getTotalExtraFb() == null ? BigDecimal.ZERO : folio.getTotalExtraFb();
        folio.setTotalExtraFb(currentExtra.add(totalAmount));
        BigDecimal packageAmt = folio.getTotalPackageAmount() == null ? BigDecimal.ZERO : folio.getTotalPackageAmount();
        folio.setFinalAmount(packageAmt.add(folio.getTotalExtraFb()));
        guestFolioRepository.save(folio);

        // Create Folio Item record
        FolioItem folioItem = FolioItem.builder()
                .guestFolio(folio)
                .serviceCategory("Extra F&B")
                .referenceId(order.getId())
                .description("Extra F&B Order #" + order.getId())
                .amount(totalAmount)
                .createAt(LocalDateTime.now())
                .createBy(request.getGuestId())
                .status("PENDING")
                .build();
        folioItemRepository.save(folioItem);

        return MealOrderResponse.builder()
                .mealOrderId(order.getId())
                .bookingId(order.getBookingId())
                .guestId(order.getGuestId())
                .placeOrder(order.getPlaceOrder())
                .totalAmount(totalAmount)
                .orderStatus(order.getOrderStatus())
                .orderedAt(order.getOrderedAt())
                .build();
    }

    @Override
    public void updatePrepStatus(Integer orderId, String status) {
        if (orderId == null || status == null) {
            throw new FnbException("FNB-002", "Order ID and status must not be null.");
        }

        MealOrder order = mealOrderRepository.findById(orderId)
                .orElseThrow(() -> new FnbException("FNB-003", "Meal order not found with ID: " + orderId));

        String currentStatus = order.getOrderStatus();
        if (currentStatus == null) {
            currentStatus = "PENDING";
        }

        // Validate state machine transitions
        boolean valid = false;
        if (currentStatus.equalsIgnoreCase("PENDING")) {
            if (status.equalsIgnoreCase("PREPARING") || status.equalsIgnoreCase("CANCELLED")) {
                valid = true;
            }
        } else if (currentStatus.equalsIgnoreCase("PREPARING")) {
            if (status.equalsIgnoreCase("READY")) {
                valid = true;
            }
        } else if (currentStatus.equalsIgnoreCase("READY")) {
            if (status.equalsIgnoreCase("DELIVERED")) {
                valid = true;
            }
        }

        if (!valid) {
            throw new FnbException("FNB-001", "Invalid state transition from " + currentStatus + " to " + status + ".");
        }

        order.setOrderStatus(status);
        mealOrderRepository.save(order);
    }

    private boolean isSafe(MenuItem item, String allergies) {
        if (allergies == null || allergies.trim().isEmpty()) {
            return true;
        }
        if (item.getIngredient() == null || item.getIngredient().trim().isEmpty()) {
            return true;
        }
        String[] allergyArray = allergies.split(",");
        String ingredient = item.getIngredient();
        for (String allergy : allergyArray) {
            String cleanAllergy = allergy.trim();
            if (!cleanAllergy.isEmpty() && hasAllergyConflict(ingredient, cleanAllergy)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAllergyConflict(String ingredient, String allergy) {
        String ing = ingredient.toLowerCase();
        String alg = allergy.toLowerCase();
        if (ing.contains(alg)) {
            return true;
        }
        // Translation mapping helper
        if (alg.contains("peanut") && ing.contains("đậu phộng"))
            return true;
        if (alg.contains("đậu phộng") && ing.contains("peanut"))
            return true;
        if (alg.contains("shellfish") && (ing.contains("tôm") || ing.contains("cua") || ing.contains("hải sản")))
            return true;
        if (alg.contains("seafood")
                && (ing.contains("tôm") || ing.contains("cua") || ing.contains("hải sản") || ing.contains("seafood")))
            return true;
        return false;
    }
}
