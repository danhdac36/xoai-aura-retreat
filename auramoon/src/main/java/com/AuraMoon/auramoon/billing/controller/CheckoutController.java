package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.billing.exception.PendingOrdersExistException;
import com.AuraMoon.auramoon.billing.service.BillingService;
import com.AuraMoon.auramoon.billing.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/billing")
@RequiredArgsConstructor
public class CheckoutController {

    private final BillingService billingService;
    private final VNPayService vnPayService;

    @GetMapping("/checkout")
    public String showCheckoutPage(@RequestParam Integer bookingId, Model model) {
        try {
            CheckoutViewDTO data = billingService.getCheckoutData(bookingId);
            model.addAttribute("data", data);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("pageTitle", "Hóa đơn Gộp & Check-out");
        return "billing/checkout/checkout";
    }

    @PostMapping("/checkout/{bookingId}/pay")
    public String processPayment(@PathVariable Integer bookingId,
            @RequestParam(required = false) String paymentMethod,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            CheckoutViewDTO data = billingService.getCheckoutData(bookingId);
            if (data.getBalanceDue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                billingService.completeCheckoutWithoutPayment(bookingId);
                redirectAttributes.addFlashAttribute("successMessage", "Check-out thành công! Bạn không có khoản nợ nào cần thanh toán.");
                return "redirect:/billing/checkout/success?bookingId=" + bookingId; // Or redirect to a general success page
            }

            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn phương thức thanh toán.");
                return "redirect:/billing/checkout?bookingId=" + bookingId;
            }

            String gateway = "CASH".equals(paymentMethod) ? "CASH" : "VNPAY";
            Payment payment = billingService.initiatePayment(bookingId, paymentMethod, gateway);

            if ("CASH".equals(paymentMethod)) {
                billingService.completePaymentAndCheckout(payment.getId(), null);
                redirectAttributes.addFlashAttribute("successMessage", "Thanh toán Tiền mặt và Check-out thành công!");
                return "redirect:/billing/checkout/success?paymentId=" + payment.getId();
            } else {
                // Sửa lỗi sinh sai URL khi chạy qua Ngrok
                String scheme = request.getHeader("X-Forwarded-Proto") != null ? request.getHeader("X-Forwarded-Proto")
                        : request.getScheme();
                String host = request.getHeader("X-Forwarded-Host") != null ? request.getHeader("X-Forwarded-Host")
                        : request.getServerName();
                String port = "";
                if (request.getHeader("X-Forwarded-Host") == null && request.getServerPort() != 80
                        && request.getServerPort() != 443) {
                    port = ":" + request.getServerPort();
                }

                String baseUrl = scheme + "://" + host + port;
                String returnUrl = baseUrl + "/billing/checkout/vnpay-return?bookingId=" + bookingId;

                String vnpayUrl = vnPayService.createPaymentUrl(payment.getAmount(), payment.getId(), returnUrl);
                return "redirect:" + vnpayUrl;
            }
        } catch (PendingOrdersExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể Check-out: Khách còn đơn hàng Spa/F&B đang thực hiện.");
            return "redirect:/billing/checkout?bookingId=" + bookingId;
        }
    }

    @GetMapping("/checkout/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params,
            @RequestParam(required = false) Integer bookingId,
            RedirectAttributes redirectAttributes) {
        if (vnPayService.verifySignature(params)) {
            Integer paymentId = Integer.parseInt(params.get("vnp_TxnRef"));
            if ("00".equals(params.get("vnp_ResponseCode"))) {
                billingService.completePaymentAndCheckout(paymentId, params.get("vnp_TransactionNo"));
                redirectAttributes.addFlashAttribute("successMessage", "Thanh toán VNPay thành công!");
                return "redirect:/billing/checkout/success?paymentId=" + paymentId;
            } else {
                billingService.markPaymentAsFailed(paymentId);
                redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng hủy giao dịch hoặc thẻ lỗi.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi bảo mật chữ ký VNPay!");
        }

        if (bookingId != null) {
            return "redirect:/billing/checkout?bookingId=" + bookingId;
        }
        return "redirect:/billing/checkout";
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(
            @RequestParam(required = false) Integer paymentId, 
            @RequestParam(required = false) Integer bookingId, 
            Model model) {
        
        if (paymentId != null) {
            Payment payment = billingService.getPaymentById(paymentId);
            bookingId = payment.getGuestFolio().getBookingId();
        }

        if (bookingId == null) {
            return "redirect:/billing/checkout";
        }

        model.addAttribute("successMessage", "Thanh toán thành công. Check-out hoàn tất!");
        model.addAttribute("bookingId", bookingId);
        return "billing/checkout/checkout_success";
    }
}
