package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.common.enums.PaymentTransactionStatus;
import com.AuraMoon.auramoon.common.enums.PaymentMethod;
import com.AuraMoon.auramoon.common.enums.PaymentGateway;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.PaymentRepository;
import com.AuraMoon.auramoon.billing.service.VNPayService;
import com.AuraMoon.auramoon.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/billing/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final GuestFolioRepository guestFolioRepository;
    private final PaymentRepository paymentRepository;
    private final VNPayService vnPayService;
    private final BookingService bookingService;

    @GetMapping("/pay")
    public String initiateDeposit(@RequestParam Integer bookingId, HttpServletRequest request) {
        GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("GuestFolio not found for bookingId: " + bookingId));

        // Thanh toán đặt cọc mặc định là 100% giá trị gói
        Payment payment = Payment.builder()
                .guestFolio(folio)
                .amount(folio.getTotalPackageAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER.name())
                .paymentGateway(PaymentGateway.VNPAY.name())
                .paymentDate(LocalDateTime.now())
                .status(PaymentTransactionStatus.PENDING.name())
                .build();
        payment = paymentRepository.save(payment);

        String scheme = request.getHeader("X-Forwarded-Proto") != null ? request.getHeader("X-Forwarded-Proto") : request.getScheme();
        String host = request.getHeader("X-Forwarded-Host") != null ? request.getHeader("X-Forwarded-Host") : request.getServerName();
        String port = "";
        if (request.getHeader("X-Forwarded-Host") == null && request.getServerPort() != 80 && request.getServerPort() != 443) {
            port = ":" + request.getServerPort();
        }

        String baseUrl = scheme + "://" + host + port;
        String returnUrl = baseUrl + "/billing/deposit/vnpay-return?bookingId=" + bookingId;

        String vnpayUrl = vnPayService.createPaymentUrl(payment.getAmount(), payment.getId(), returnUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params,
                              @RequestParam(required = false) Integer bookingId,
                              RedirectAttributes redirectAttributes) {
        if (vnPayService.verifySignature(params)) {
            Integer paymentId = Integer.parseInt(params.get("vnp_TxnRef"));
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            Integer actualBookingId = payment.getGuestFolio().getBookingId();

            if ("00".equals(params.get("vnp_ResponseCode"))) {
                payment.setStatus(PaymentTransactionStatus.SUCCESS.name());
                payment.setTransactionCode(params.get("vnp_TransactionNo"));
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                // Cập nhật trạng thái Booking
                bookingService.confirmPayment(actualBookingId, params.get("vnp_TransactionNo"));
                
                return "redirect:/booking/success?bookingId=" + actualBookingId;
            } else {
                payment.setStatus(PaymentTransactionStatus.FAILED.name());
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);
                
                redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán đặt cọc thất bại hoặc bị hủy.");
                // Trở về trang package hoặc trang thông báo lỗi. Ở đây tạm redirect về danh sách package.
                return "redirect:/packages"; 
            }
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi bảo mật chữ ký VNPay!");
        return "redirect:/";
    }
}
