package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealOrderRequest;
import com.AuraMoon.auramoon.fnb.dto.MealOrderResponse;
import com.AuraMoon.auramoon.fnb.dto.MenuItemResponse;
import com.AuraMoon.auramoon.fnb.service.IMealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;
import com.AuraMoon.auramoon.fnb.dto.ChefDashboardOrderResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MealOrderController {

    @Autowired
    private IMealOrderService mealOrderService;

    @GetMapping("/api/v1/fnb/menu")
    public ResponseEntity<List<MenuItemResponse>> getFilteredMenu(
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("guestId") Integer guestId) {
        List<MenuItemResponse> menu = mealOrderService.getFilteredMenu(guestId, bookingId);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/api/v1/fnb/menu/all")
    public ResponseEntity<List<MenuItemResponse>> getAllMenu() {
        List<MenuItemResponse> menu = mealOrderService.getAllMenu();
        return ResponseEntity.ok(menu);
    }

    @PostMapping("/api/v1/fnb/meal-orders")
    public ResponseEntity<MealOrderResponse> createMealOrderApi(@RequestBody MealOrderRequest request) {
        MealOrderResponse response = mealOrderService.createMealOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/api/v1/fnb/chef/orders/{id}/status")
    public ResponseEntity<Void> updatePrepStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status) {
        mealOrderService.updatePrepStatus(id, status);
        return ResponseEntity.ok().build();
    }

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