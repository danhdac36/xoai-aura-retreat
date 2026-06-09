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
    public String indexPage() {
        return "selection/index";
    }

    @GetMapping("/view")
    public String getFilteredMenuForGuest(@RequestParam Integer guestId, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(guestId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Khách hàng với ID " + guestId + " không tồn tại. Vui lòng click 'Cài Đặt Dữ Liệu Demo Nhanh' để khởi tạo!");
            return "redirect:/fnb/selection";
        }

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy booking nào cho khách hàng này. Vui lòng bấm cài đặt dữ liệu demo!");
            return "redirect:/fnb/selection";
        }

        User guest = userOpt.get();
        Booking activeBooking = bookings.get(0); // Take first active booking
        List<MenuItem> filteredMenuItems = mealSelectionService.getFilteredMenuForGuest(guestId);
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

        return "redirect:/fnb/selection/view?guestId=" + guestId;
    }

    @PostMapping("/seed")
    @Transactional
    public String seedDemoData(RedirectAttributes redirectAttributes) {
        try {
            // 1. Seed Role
            Role guestRole = entityManager.find(Role.class, 1);
            if (guestRole == null) {
                guestRole = new Role();
                guestRole.setId(1);
                guestRole.setRoleName("GUEST");
                entityManager.persist(guestRole);
                entityManager.flush();
            }

            // 2. Seed User (guest)
            Optional<User> userOpt = userRepository.findById(1);
            User guest;
            if (userOpt.isEmpty()) {
                guest = User.builder()
                        .email("guest@fpt.edu.vn")
                        .passwordHash("password_hash_placeholder")
                        .fullName("Nguyễn Văn A")
                        .gender("Nam")
                        .dateOfBirth(LocalDate.of(1995, 8, 15))
                        .phone("0987654321")
                        .identifyCode("ID123456789")
                        .status("Active")
                        .role(guestRole)
                        .build();
                guest.setId(1);
                userRepository.save(guest);
            } else {
                guest = userOpt.get();
            }

            // 3. Seed DietaryProfile for guest (allergies: peanut, shrimp, preference: vegan)
            Optional<DietaryProfile> profileOpt = dietaryProfileRepository.findByUserId(1);
            if (profileOpt.isEmpty()) {
                DietaryProfile profile = DietaryProfile.builder()
                        .userId(1)
                        .foodAllergies("peanut, tôm")
                        .dietaryPreference("vegan")
                        .build();
                dietaryProfileRepository.save(profile);
            }

            // 4. Seed Menu Items
            menuItemRepository.deleteAll(); // Clean menu
            
            MenuItem item1 = MenuItem.builder()
                    .itemName("Bánh mì quả bơ chay")
                    .price(BigDecimal.ZERO) // Free in package
                    .ingredient("bánh mì, quả bơ, cà chua, xà lách, sốt bơ thực vật")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item1);

            MenuItem item2 = MenuItem.builder()
                    .itemName("Salad củ quả dầu olive")
                    .price(new BigDecimal("12.00")) // Paid extra
                    .ingredient("rau xà lách, cà chua, dưa leo, dầu olive hữu cơ")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item2);

            MenuItem item3 = MenuItem.builder()
                    .itemName("Sốt đậu phộng truyền thống")
                    .price(new BigDecimal("5.00"))
                    .ingredient("đậu phộng, tỏi, ớt, nước tương gia vị")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item3);

            MenuItem item4 = MenuItem.builder()
                    .itemName("Mì xào tôm đặc biệt")
                    .price(new BigDecimal("22.00"))
                    .ingredient("mì sợi, tôm tươi, mực, tỏi, mỡ heo")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item4);

            MenuItem item5 = MenuItem.builder()
                    .itemName("Thịt bò né thượng hạng")
                    .price(new BigDecimal("35.00"))
                    .ingredient("thịt bò, trứng gà, bơ động vật")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item5);

            MenuItem item6 = MenuItem.builder()
                    .itemName("Nước ép cần tây hữu cơ")
                    .price(new BigDecimal("8.00"))
                    .ingredient("cần tây nguyên chất, táo xanh hữu cơ")
                    .isAvailable(true)
                    .build();
            menuItemRepository.save(item6);

            // 5. Seed Retreat Package
            RetreatPackage testPackage = entityManager.find(RetreatPackage.class, 1);
            if (testPackage == null) {
                testPackage = RetreatPackage.builder()
                        .typePackage("Health")
                        .packageName("Detox & Yoga Journey")
                        .durationDays(5)
                        .price(new BigDecimal("800.00"))
                        .description("Gói trị liệu sức khỏe toàn diện 5 ngày")
                        .isActive(true)
                        .build();
                testPackage.setId(1);
                entityManager.persist(testPackage);
                entityManager.flush();
            }

            // 6. Seed Villa Type
            VillaType villaType = entityManager.find(VillaType.class, 1);
            if (villaType == null) {
                villaType = VillaType.builder()
                        .typeName("Garden Pool Villa")
                        .pricePerDay(new BigDecimal("250.00"))
                        .build();
                villaType.setId(1);
                entityManager.persist(villaType);
                entityManager.flush();
            }

            // 7. Seed Villa
            Villa villa = entityManager.find(Villa.class, 1);
            if (villa == null) {
                villa = Villa.builder()
                        .villaCode("VILLA-101")
                        .limitPerson(2)
                        .villaStatus("Available")
                        .cleaningStatus("Clean")
                        .villaType(villaType)
                        .build();
                villa.setId(1);
                entityManager.persist(villa);
                entityManager.flush();
            }

            // 8. Seed Booking
            bookingRepository.deleteAll(); // Clean bookings
            Booking booking = Booking.builder()
                    .guestId(1)
                    .retreatPackage(testPackage)
                    .assignedVilla(villa)
                    .checkinDate(LocalDate.now())
                    .checkoutDate(LocalDate.now().plusDays(5))
                    .totalGuests(1)
                    .bookingStatus("Active")
                    .paymentStatus("Deposited")
                    .build();
            booking.setId(1);
            bookingRepository.save(booking);

            // 9. Seed Guest Folio
            guestFolioRepository.deleteAll(); // Clean folios
            GuestFolio folio = GuestFolio.builder()
                    .bookingId(1)
                    .totalPackageAmount(new BigDecimal("800.00"))
                    .totalExtraFb(BigDecimal.ZERO)
                    .finalAmount(new BigDecimal("800.00"))
                    .status("Active")
                    .build();
            folio.setId(1);
            guestFolioRepository.save(folio);

            redirectAttributes.addFlashAttribute("success", "Đã cài đặt dữ liệu demo mẫu thành công! Bạn có thể sử dụng Guest ID = 1 để thử nghiệm lọc và đặt thực đơn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cài đặt dữ liệu demo: " + e.getMessage());
        }

        return "redirect:/fnb/selection";
    }
}
