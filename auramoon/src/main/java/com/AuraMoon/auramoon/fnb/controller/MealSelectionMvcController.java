package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionRequest;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.fnb.service.MealSelectionService;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
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

import java.math.BigDecimal;
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
    public String indexPage(Model model, RedirectAttributes redirectAttributes) {
        // Auto-seed if Guest 1 is missing
        Optional<User> userOpt = userRepository.findById(1);
        if (userOpt.isEmpty()) {
            seedDemoDataInternal();
        }
        
        // Directly show the menu page for Guest 1 (Nguyen Van A)
        return getFilteredMenuForGuest(1, model, redirectAttributes);
    }

    @GetMapping("/view")
    public String getFilteredMenuForGuest(@RequestParam Integer guestId, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(guestId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Khách hàng với ID " + guestId + " không tồn tại. Vui lòng quay lại trang chủ!");
            return "redirect:/fnb/selection";
        }

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy booking nào cho khách hàng này.");
            return "redirect:/fnb/selection";
        }

        User guest = userOpt.get();
        Booking activeBooking = bookings.get(0); // Take first active booking
        List<com.AuraMoon.auramoon.fnb.dto.MenuItemResponse> filteredMenuItems = mealSelectionService.getFilteredMenuForGuest(guestId);
        Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(guestId);

        model.addAttribute("guest", guest);
        model.addAttribute("booking", activeBooking);
        model.addAttribute("menuItems", filteredMenuItems);
        model.addAttribute("dietaryProfile", profileOpt.orElse(null));

        return "selection/menu";
    }

    @PostMapping("/submit")
    public String selectDailyMeals(@RequestParam Integer guestId,
                                   @RequestParam Integer bookingId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mealDate,
                                   @RequestParam String mealType,
                                   @RequestParam(required = false) List<Integer> menuItemIds,
                                   @RequestParam(required = false) String note,
                                   RedirectAttributes redirectAttributes) {
        
        MealSelectionRequest request = new MealSelectionRequest();
        request.setGuestId(guestId);
        request.setBookingId(bookingId);
        request.setMealDate(mealDate);
        request.setMealType(mealType);
        request.setMenuItemIds(menuItemIds);
        request.setNote(note);

        MealSelectionResponse response = mealSelectionService.selectDailyMeals(request);

        if ("SUCCESS".equals(response.getStatus())) {
            redirectAttributes.addFlashAttribute("success", "Đã ghi nhận đặt bữa ăn " + mealType + " thành công cho ngày " + mealDate + "!");
        } else if ("ALLERGY_VIOLATION".equals(response.getStatus())) {
            redirectAttributes.addFlashAttribute("errors", response.getDetails());
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
        }

        return "redirect:/fnb/selection";
    }

    @PostMapping("/seed")
    @Transactional
    public String seedDemoData(RedirectAttributes redirectAttributes) {
        try {
            seedDemoDataInternal();
            redirectAttributes.addFlashAttribute("success", "Đã khởi tạo lại dữ liệu demo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cài đặt dữ liệu: " + e.getMessage());
        }
        return "redirect:/fnb/selection";
    }

    private void seedDemoDataInternal() {
        // Run migrations/alters just in case
        try {
            entityManager.createNativeQuery("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('[USER]') AND name = 'is_delete') ALTER TABLE [USER] ADD is_delete BIT DEFAULT 0;").executeUpdate();
            entityManager.createNativeQuery("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('MENU_ITEM') AND name = 'is_delete') ALTER TABLE MENU_ITEM ADD is_delete BIT DEFAULT 0;").executeUpdate();
            entityManager.createNativeQuery("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('PHYSICAL_HEALTH_PROFILE') AND name = 'is_delete') ALTER TABLE PHYSICAL_HEALTH_PROFILE ADD is_delete BIT DEFAULT 0;").executeUpdate();
            entityManager.createNativeQuery("IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('DIETARY_PROFILE') AND name = 'is_delete') ALTER TABLE DIETARY_PROFILE ADD is_delete BIT DEFAULT 0;").executeUpdate();
        } catch (Exception e) {
            // Ignore if columns already exist
        }

        // Clean tables to prevent constraint violations
        entityManager.createNativeQuery("DELETE FROM MEAL_ORDER_ITEM").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM MEAL_ORDER").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM FOLIO_ITEM").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM GUEST_FOLIO").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM BOOKING").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM VILLA").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM VILLA_TYPE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM RETREAT_PACKAGE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM DIETARY_PROFILE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM [USER]").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM [ROLE]").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM MENU_ITEM").executeUpdate();

        // Seed [ROLE]
        entityManager.createNativeQuery("SET IDENTITY_INSERT [ROLE] ON; INSERT INTO [ROLE] (role_id, role_name) VALUES (1, 'GUEST'); SET IDENTITY_INSERT [ROLE] OFF;").executeUpdate();

        // Seed [USER]
        entityManager.createNativeQuery("SET IDENTITY_INSERT [USER] ON; INSERT INTO [USER] (user_id, role_id, email, password_hash, full_name, gender, date_of_birth, phone, Identify_code, status, last_login, is_delete) VALUES (1, 1, 'guest@fpt.edu.vn', 'password_hash_placeholder', 'Minh', 'Nam', '1995-08-15', '0987654321', 'ID123456789', 'Active', GETDATE(), 0); SET IDENTITY_INSERT [USER] OFF;").executeUpdate();

        // Seed DIETARY_PROFILE
        entityManager.createNativeQuery("SET IDENTITY_INSERT DIETARY_PROFILE ON; INSERT INTO DIETARY_PROFILE (dietary_id, user_id, food_allergies, diatary_preference, update_at, is_delete) VALUES (1, 1, N'hải sản, hạt điều', N'vegan', GETDATE(), 0); SET IDENTITY_INSERT DIETARY_PROFILE OFF;").executeUpdate();

        // Seed MENU_ITEM matching the mockup ingredients, prices, and status exactly
        entityManager.createNativeQuery("SET IDENTITY_INSERT MENU_ITEM ON; " +
                "INSERT INTO MENU_ITEM (menu_item_id, item_name, price, ingredient, is_available, create_at, update_at, is_delete) VALUES " +
                "(1, N'Cá Hồi Nướng Hương Thảo', 420.00, N'Cá hồi Na Uy nướng chậm cùng các loại rau củ hữu cơ từ vườn Aura, phục vụ kèm sốt bơ chanh thảo mộc.', 1, GETDATE(), GETDATE(), 0), " +
                "(2, N'Salad Aura Thanh Lọc', 280.00, N'Tổng hợp hạt quinoa, bơ sáp Đắk Lắk và rau mầm tươi, cung cấp đầy đủ chất xơ và vitamin cho buổi trưa nhẹ nhàng.', 1, GETDATE(), GETDATE(), 0), " +
                "(3, N'Tôm Nướng Muối Hạt & Hạt Điều', 320.00, N'Món ăn này có chứa hải sản và hạt điều, nằm trong danh sách dị ứng của bạn.', 1, GETDATE(), GETDATE(), 0), " +
                "(4, N'Bát Cơm Gạo Lứt Chay', 240.00, N'Sự kết hợp cân bằng giữa tinh bột phức hợp, nấm rừng và đậu hũ hữu cơ nướng tương.', 1, GETDATE(), GETDATE(), 0), " +
                "(5, N'Nước Ép Cần Tây Hữu Cơ', 80.00, N'Cần tây nguyên chất, táo xanh hữu cơ giúp lọc cơ thể.', 1, GETDATE(), GETDATE(), 0); " +
                "SET IDENTITY_INSERT MENU_ITEM OFF;").executeUpdate();

        // Seed RETREAT_PACKAGE
        entityManager.createNativeQuery("SET IDENTITY_INSERT RETREAT_PACKAGE ON; INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, description, is_active, is_delete, price, create_at, update_at) VALUES (1, 'Health', 'Detox & Yoga Journey', 5, N'Gói trị liệu sức khỏe toàn diện 5 ngày', 1, 0, 800.00, GETDATE(), GETDATE()); SET IDENTITY_INSERT RETREAT_PACKAGE OFF;").executeUpdate();

        // Seed VILLA_TYPE
        entityManager.createNativeQuery("SET IDENTITY_INSERT VILLA_TYPE ON; INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, is_delete) VALUES (1, 'Garden Pool Villa', 250.00, 0); SET IDENTITY_INSERT VILLA_TYPE OFF;").executeUpdate();

        // Seed VILLA
        entityManager.createNativeQuery("SET IDENTITY_INSERT VILLA ON; INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) VALUES (1, 1, 'VILLA-101', 2, 'Available', 'Clean', 0); SET IDENTITY_INSERT VILLA OFF;").executeUpdate();

        // Seed BOOKING
        entityManager.createNativeQuery("SET IDENTITY_INSERT BOOKING ON; INSERT INTO BOOKING (booking_id, guest_id, package_id, assigned_villa_id, checkin_date, checkout_date, total_guests, create_at, update_at, booking_status, payment_status, is_delete) VALUES (1, 1, 1, 1, GETDATE(), DATEADD(day, 5, GETDATE()), 1, GETDATE(), GETDATE(), 'Active', 'Deposited', 0); SET IDENTITY_INSERT BOOKING OFF;").executeUpdate();

        // Seed GUEST_FOLIO
        entityManager.createNativeQuery("SET IDENTITY_INSERT GUEST_FOLIO ON; INSERT INTO GUEST_FOLIO (folio_id, booking_id, total_package_amout, total_extra_fb, final_amount, status, is_delete, create_at, update_at) VALUES (1, 1, 800.00, 0.00, 800.00, 'Active', 0, GETDATE(), GETDATE()); SET IDENTITY_INSERT GUEST_FOLIO OFF;").executeUpdate();
    }
}
