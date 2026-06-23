package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.ChefDashboardOrderResponse;
import com.AuraMoon.auramoon.fnb.service.IMealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ChefFnbController {

    @Autowired
    private IMealOrderService mealOrderService;

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
