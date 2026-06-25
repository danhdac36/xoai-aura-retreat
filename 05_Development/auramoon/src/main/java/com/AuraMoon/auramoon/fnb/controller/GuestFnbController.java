package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.service.IMealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GuestFnbController {

    @Autowired
    private IMealOrderService mealOrderService;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/fnb/meal-selection")
    public String getMealSelectionPage(
            @AuthenticationPrincipal UserDetailsResponse userDetails,
            @RequestParam(required = false) Integer guestId,
            @RequestParam(required = false) Integer bookingId,
            Model model) {
        if (userDetails == null && guestId == null)
            return "redirect:/login";

        if (guestId == null) {
            guestId = userDetails.getId();
        }

        if (bookingId == null) {
            for (Booking b : bookingRepository.findByGuestId(guestId)) {
                if (com.AuraMoon.auramoon.common.enums.BookingStatus.CHECKED_IN.name().equalsIgnoreCase(b.getBookingStatus())
                        || "ACTIVE".equalsIgnoreCase(b.getBookingStatus())) {
                    bookingId = b.getId();
                    break;
                }
            }
        }

        List<MenuItemResponse> menuItems = new ArrayList<>();
        if (guestId != null && bookingId != null) {
            try {
                menuItems = mealOrderService.getFilteredMenu(guestId, bookingId);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin Booking hợp lệ.");
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("guestId", guestId);
        model.addAttribute("bookingId", bookingId);

        MealOrderRequest orderForm = new MealOrderRequest();
        orderForm.setGuestId(guestId);
        orderForm.setBookingId(bookingId);
        orderForm.setIsExtraCharge(false); // UC16 -> NOT extra charge
        model.addAttribute("orderForm", orderForm);

        return "fnb/meal-selection";
    }

    @PostMapping("/fnb/meal-selection")
    public String createMealOrderMvc(
            @AuthenticationPrincipal UserDetailsResponse userDetails,
            @ModelAttribute("orderForm") MealOrderRequest request,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null)
            return "redirect:/login";
        Integer guestId = userDetails.getId();
        Integer bookingId = null;
        for (Booking b : bookingRepository.findByGuestId(guestId)) {
            if (com.AuraMoon.auramoon.common.enums.BookingStatus.CHECKED_IN.name().equalsIgnoreCase(b.getBookingStatus())
                    || "ACTIVE".equalsIgnoreCase(b.getBookingStatus())) {
                bookingId = b.getId();
                break;
            }
        }

        request.setGuestId(guestId);
        request.setBookingId(bookingId);
        request.setIsExtraCharge(false);

        try {
            mealOrderService.createMealOrder(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt món ăn cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/fnb/meal-selection";
    }
}