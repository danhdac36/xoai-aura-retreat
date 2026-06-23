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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @PersistenceContext
    private EntityManager entityManager;

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
        if (bStatus == null || (!bStatus.equalsIgnoreCase(com.AuraMoon.auramoon.common.enums.BookingStatus.CHECKED_IN.name()) && !bStatus.equalsIgnoreCase("Checked-in")
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

        if (!Boolean.TRUE.equals(request.getIsExtraCharge())) {
            int totalGuests = booking.getTotalGuests() != null ? booking.getTotalGuests() : 1;
            int maxAllowedQuantity = totalGuests * 3;

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
            int alreadyOrderedToday = mealOrderRepository.sumQuantityOfFreeMealOrdersToday(booking.getId(), startOfDay, endOfDay);
            int totalRequestedQuantity = request.getItems().stream().mapToInt(OrderItemDto::getQuantity).sum();

            if (alreadyOrderedToday + totalRequestedQuantity > maxAllowedQuantity) {
                int remainingFreeMeals = maxAllowedQuantity - alreadyOrderedToday;
                if (remainingFreeMeals <= 0) {
                    throw new FnbException("FNB-005", "Quý khách đã sử dụng hết " + maxAllowedQuantity + " phần ăn miễn phí của ngày hôm nay. Vui lòng đặt qua Thực đơn A-La-Carte nếu có nhu cầu phát sinh.");
                } else {
                    throw new FnbException("FNB-005", "Quý khách chỉ còn lại " + remainingFreeMeals + " phần ăn miễn phí trong ngày hôm nay. Đơn hàng yêu cầu " + totalRequestedQuantity + " phần. Vui lòng giảm số lượng hoặc đặt qua Thực đơn A-La-Carte.");
                }
            }
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

        // Update Folio charges if this is an A-la-carte order
        if (Boolean.TRUE.equals(request.getIsExtraCharge())) {
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
                    .status("UNPAID")
                    .build();
            folioItemRepository.save(folioItem);
        }

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

        // If status is CANCELLED, void the corresponding FolioItem and deduct the charge
        if (status.equalsIgnoreCase("CANCELLED")) {
            String queryStr = "SELECT fi FROM FolioItem fi WHERE fi.serviceCategory = :category AND fi.referenceId = :refId";
            List<FolioItem> folioItems = entityManager.createQuery(queryStr, FolioItem.class)
                    .setParameter("category", "Extra F&B")
                    .setParameter("refId", orderId)
                    .getResultList();
            for (FolioItem folioItem : folioItems) {
                if (!"VOIDED".equalsIgnoreCase(folioItem.getStatus())) {
                    folioItem.setStatus("VOIDED");
                    entityManager.merge(folioItem);

                    GuestFolio folio = folioItem.getGuestFolio();
                    if (folio != null) {
                        BigDecimal currentExtra = folio.getTotalExtraFb() == null ? BigDecimal.ZERO : folio.getTotalExtraFb();
                        BigDecimal newExtra = currentExtra.subtract(folioItem.getAmount());
                        if (newExtra.compareTo(BigDecimal.ZERO) < 0) {
                            newExtra = BigDecimal.ZERO;
                        }
                        folio.setTotalExtraFb(newExtra);

                        BigDecimal packageAmt = folio.getTotalPackageAmount() == null ? BigDecimal.ZERO : folio.getTotalPackageAmount();
                        folio.setFinalAmount(packageAmt.add(folio.getTotalExtraFb()));
                        entityManager.merge(folio);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChefDashboardOrderResponse> getChefDashboardOrders(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Object[]> orderRows = mealOrderRepository.findChefDashboardOrdersByDateRange(startOfDay, endOfDay);
        List<ChefDashboardOrderResponse> orderResponses = new ArrayList<>();

        for (Object[] row : orderRows) {
            Integer orderId = row[0] != null ? ((Number) row[0]).intValue() : null;
            Integer bookingId = row[1] != null ? ((Number) row[1]).intValue() : null;
            Integer guestId = row[2] != null ? ((Number) row[2]).intValue() : null;
            String placeOrder = row[3] != null ? row[3].toString() : null;
            String note = row[4] != null ? row[4].toString() : null;
            String orderStatus = row[5] != null ? row[5].toString() : null;

            LocalDateTime orderedAt = null;
            if (row[6] != null) {
                if (row[6] instanceof java.sql.Timestamp) {
                    orderedAt = ((java.sql.Timestamp) row[6]).toLocalDateTime();
                } else if (row[6] instanceof LocalDateTime) {
                    orderedAt = (LocalDateTime) row[6];
                } else {
                    orderedAt = LocalDateTime.parse(row[6].toString());
                }
            }

            String foodAllergies = null;
            if (guestId != null) {
                Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);
                foodAllergies = profileOpt.map(DietaryProfile::getFoodAllergies).orElse(null);
            }

            List<Object[]> itemRows = orderId != null ?
                    mealOrderItemRepository.findChefDashboardItemsByOrderId(orderId) : new ArrayList<>();
            List<ChefDashboardItemResponse> itemResponses = new ArrayList<>();
            boolean orderHasAllergy = false;

            for (Object[] itemRow : itemRows) {
                Integer menuItemId = itemRow[0] != null ? ((Number) itemRow[0]).intValue() : null;
                String itemName = itemRow[1] != null ? itemRow[1].toString() : null;
                Integer quantity = itemRow[2] != null ? ((Number) itemRow[2]).intValue() : null;

                BigDecimal price = null;
                if (itemRow[3] != null) {
                    if (itemRow[3] instanceof BigDecimal) {
                        price = (BigDecimal) itemRow[3];
                    } else if (itemRow[3] instanceof Number) {
                        price = BigDecimal.valueOf(((Number) itemRow[3]).doubleValue());
                    } else {
                        price = new BigDecimal(itemRow[3].toString());
                    }
                }

                String ingredient = itemRow[4] != null ? itemRow[4].toString() : null;

                boolean isAllergic = false;
                if (ingredient != null && !ingredient.trim().isEmpty() && foodAllergies != null && !foodAllergies.trim().isEmpty()) {
                    String[] allergyArray = foodAllergies.split(",");
                    for (String allergy : allergyArray) {
                        String cleanAllergy = allergy.trim();
                        if (!cleanAllergy.isEmpty() && hasAllergyConflict(ingredient, cleanAllergy)) {
                            isAllergic = true;
                            break;
                        }
                    }
                }

                if (isAllergic) {
                    orderHasAllergy = true;
                }

                itemResponses.add(ChefDashboardItemResponse.builder()
                        .menuItemId(menuItemId)
                        .itemName(itemName)
                        .quantity(quantity)
                        .price(price)
                        .ingredient(ingredient)
                        .isAllergic(isAllergic)
                        .build());
            }

            orderResponses.add(ChefDashboardOrderResponse.builder()
                    .orderId(orderId)
                    .bookingId(bookingId)
                    .guestId(guestId)
                    .placeOrder(placeOrder)
                    .note(note)
                    .orderStatus(orderStatus)
                    .orderedAt(orderedAt)
                    .foodAllergies(foodAllergies)
                    .hasAllergyWarning(orderHasAllergy)
                    .items(itemResponses)
                    .build());
        }

        return orderResponses;
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

    @Override
    @Transactional(readOnly = true)
    public int getSumQuantityOfFreeMealOrdersToday(Integer bookingId, LocalDateTime start, LocalDateTime end) {
        if (bookingId == null || start == null || end == null) {
            return 0;
        }
        return mealOrderRepository.sumQuantityOfFreeMealOrdersToday(bookingId, start, end);
    }
}
