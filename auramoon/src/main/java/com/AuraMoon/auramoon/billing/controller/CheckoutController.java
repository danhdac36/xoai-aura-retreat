package com.AuraMoon.auramoon.billing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/billing")
public class CheckoutController {

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        // Trong tương lai sẽ lấy dữ liệu tổng hợp từ các module khác
        // và đẩy vào model (Ví dụ: thông tin khách, chi tiết phòng, spa, f&b)

        model.addAttribute("pageTitle", "Hóa đơn Gộp & Check-out");

        // Trả về tên file view (src/main/resources/templates/billing/checkout.html)
        return "billing/checkout";
    }
}
