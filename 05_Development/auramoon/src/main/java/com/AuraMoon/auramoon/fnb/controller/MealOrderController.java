package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.ChefDashboardOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.service.IMealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MealOrderController {

    @Autowired
    private IMealOrderService mealOrderService;

    @GetMapping("/fnb/uc16-meal-selection")
    public String getMealSelectionPage(
            @RequestParam(value = "guestId", required = false) Integer guestId,
            @RequestParam(value = "bookingId", required = false) Integer bookingId,
            Model model) {

        List<MenuItemResponse> menuItems = new ArrayList<>();

        if (guestId != null && bookingId != null) {
            try {
                menuItems = mealOrderService.getFilteredMenu(guestId, bookingId);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("guestId", guestId);
        model.addAttribute("bookingId", bookingId);

        MealOrderRequest orderForm = new MealOrderRequest();
        orderForm.setGuestId(guestId);
        orderForm.setBookingId(bookingId);
        model.addAttribute("orderForm", orderForm);

        return "fnb/uc16-meal-selection";
    }

    @PostMapping("/fnb/uc16-meal-selection")
    public String createMealOrderMvc(
            @ModelAttribute("orderForm") MealOrderRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            mealOrderService.createMealOrder(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt món ăn cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (request != null && request.getGuestId() != null && request.getBookingId() != null) {
            return "redirect:/fnb/uc16-meal-selection?guestId="
                    + request.getGuestId()
                    + "&bookingId="
                    + request.getBookingId();
        }

        return "redirect:/fnb/uc16-meal-selection";
    }

    @GetMapping("/fnb/uc19-alacarte-order")
    public String getAlacarteOrderPage(
            @RequestParam(value = "guestId", required = false) Integer guestId,
            @RequestParam(value = "bookingId", required = false) Integer bookingId,
            Model model) {
        List<MenuItemResponse> allMenu = mealOrderService.getAllMenu();
        List<MenuItemResponse> menuItems = new ArrayList<>();
        if (allMenu != null) {
            for (MenuItemResponse item : allMenu) {
                if (item.getIngredient() != null && item.getIngredient().contains("Europe")) {
                    menuItems.add(item);
                    if (menuItems.size() >= 50) {
                        break;
                    }
                }
            }
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("guestId", guestId);
        model.addAttribute("bookingId", bookingId);

        MealOrderRequest orderForm = new MealOrderRequest();
        orderForm.setGuestId(guestId);
        orderForm.setBookingId(bookingId);
        model.addAttribute("orderForm", orderForm);

        return "fnb/uc19-alacarte-order";
    }

    @PostMapping("/fnb/uc19-alacarte-order")
    public String createAlacarteOrder(
            @ModelAttribute("orderForm") MealOrderRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            mealOrderService.createMealOrder(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt món A-La-Carte thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (request != null && request.getGuestId() != null && request.getBookingId() != null) {
            return "redirect:/fnb/uc19-alacarte-order?guestId="
                    + request.getGuestId()
                    + "&bookingId="
                    + request.getBookingId();
        }

        return "redirect:/fnb/uc19-alacarte-order";
    }

    @GetMapping("/fnb/chef/dashboard")
    public String getChefDashboard(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        List<ChefDashboardOrderResponse> orders = mealOrderService.getChefDashboardOrders(selectedDate);

        model.addAttribute("orders", orders);
        model.addAttribute("selectedDate", selectedDate);

        return "fnb/uc17-chef-dashboard";
    }

    @PostMapping("/fnb/chef/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable("id") Integer orderId,
            @RequestParam("status") String status,
            @RequestParam(value = "date", required = false) String dateStr,
            RedirectAttributes redirectAttributes) {
        try {
            mealOrderService.updatePrepStatus(orderId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (dateStr != null && !dateStr.trim().isEmpty()) {
            return "redirect:/fnb/chef/dashboard?date=" + dateStr.trim();
        }
        return "redirect:/fnb/chef/dashboard";
    }
}