package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/billing")
@RequiredArgsConstructor
public class CheckoutController {

    private final BillingService billingService;

    @GetMapping("/checkout")
    public String showCheckoutPage(@RequestParam(required = false, defaultValue = "1") Integer bookingId, Model model) {
        // Lấy dữ liệu tổng hợp từ service (Truyền mặc định bookingId = 1 để test nếu không có param)
        try {
            CheckoutViewDTO data = billingService.getCheckoutData(bookingId);
            model.addAttribute("data", data);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("pageTitle", "Hóa đơn Gộp & Check-out");
        return "billing/checkout";
    }
}
