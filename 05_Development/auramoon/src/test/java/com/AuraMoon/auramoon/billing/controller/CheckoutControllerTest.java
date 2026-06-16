package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.exception.PendingOrdersExistException;
import com.AuraMoon.auramoon.billing.service.BillingService;
import com.AuraMoon.auramoon.billing.service.VNPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillingService billingService;

    @Mock
    private VNPayService vnPayService;

    @Mock
    private com.AuraMoon.auramoon.billing.service.AuditLogService auditLogService;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
    }

    @Test
    @DisplayName("BIL21-TC-005 - Hiển thị trang Hóa đơn gộp thành công")
    void showCheckoutPage_Success_ReturnsViewAndModel() throws Exception {
        // Arrange
        Integer bookingId = 1;
        CheckoutViewDTO dto = CheckoutViewDTO.builder().build();
        when(billingService.getCheckoutData(bookingId)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/billing/checkout").param("bookingId", String.valueOf(bookingId)))
                .andExpect(status().isOk())
                .andExpect(view().name("billing/checkout/checkout"))
                .andExpect(model().attributeExists("data"))
                .andExpect(model().attribute("pageTitle", "Hóa đơn Gộp & Check-out"));

        verify(billingService, times(1)).getCheckoutData(bookingId);
    }

    @Test
    @DisplayName("BIL-TC-001 - Thanh toán CASH thành công")
    void processPayment_Cash_RedirectsToSuccess() throws Exception {
        // Arrange
        Integer bookingId = 1;
        Payment payment = new Payment();
        payment.setId(100);
        when(billingService.getCheckoutData(anyInt())).thenReturn(CheckoutViewDTO.builder().balanceDue(BigDecimal.TEN).build());
        when(billingService.initiatePayment(bookingId, "CASH", "CASH")).thenReturn(payment);

        // Act & Assert
        mockMvc.perform(post("/billing/checkout/{bookingId}/pay", bookingId)
                .param("paymentMethod", "CASH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/checkout/success?paymentId=100"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(billingService, times(1)).completePaymentAndCheckout(100, null);
    }

    @Test
    @DisplayName("BIL-TC-002 - Chặn thanh toán nếu còn đơn hàng Pending")
    void processPayment_PendingOrdersExist_RedirectsBackWithError() throws Exception {
        // Arrange
        Integer bookingId = 1;
        when(billingService.getCheckoutData(anyInt())).thenReturn(CheckoutViewDTO.builder().balanceDue(BigDecimal.TEN).build());
        when(billingService.initiatePayment(anyInt(), anyString(), anyString()))
                .thenThrow(new PendingOrdersExistException("Còn đơn hàng đang chờ"));

        // Act & Assert
        mockMvc.perform(post("/billing/checkout/{bookingId}/pay", bookingId)
                .param("paymentMethod", "CASH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/checkout?bookingId=" + bookingId))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(billingService, never()).completePaymentAndCheckout(anyInt(), anyString());
    }

    @Test
    @DisplayName("BIL-TC-003 - Sinh URL VNPay & Redirect")
    void processPayment_VNPay_RedirectsToVNPayUrl() throws Exception {
        // Arrange
        Integer bookingId = 1;
        Payment payment = new Payment();
        payment.setId(101);
        payment.setAmount(new BigDecimal("1000000"));

        when(billingService.getCheckoutData(anyInt())).thenReturn(CheckoutViewDTO.builder().balanceDue(BigDecimal.TEN).build());
        when(billingService.initiatePayment(bookingId, "VNPAY", "VNPAY")).thenReturn(payment);
        when(vnPayService.createPaymentUrl(any(BigDecimal.class), eq(101), anyString()))
                .thenReturn("http://sandbox.vnpayment.vn/testurl");

        // Act & Assert
        mockMvc.perform(post("/billing/checkout/{bookingId}/pay", bookingId)
                .param("paymentMethod", "VNPAY")
                .header("X-Forwarded-Proto", "http")
                .header("X-Forwarded-Host", "localhost"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://sandbox.vnpayment.vn/testurl"));

        verify(billingService, never()).completePaymentAndCheckout(anyInt(), anyString());
    }

    @Test
    @DisplayName("BIL-TC-004 - VNPay Callback Thành công")
    void vnpayReturn_Success_RedirectsToSuccess() throws Exception {
        // Arrange
        when(vnPayService.verifySignature(anyMap())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/billing/checkout/vnpay-return")
                .param("vnp_TxnRef", "102")
                .param("vnp_ResponseCode", "00")
                .param("vnp_TransactionNo", "123456789")
                .param("vnp_SecureHash", "hash123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/checkout/success?paymentId=102"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(billingService, times(1)).completePaymentAndCheckout(102, "123456789");
    }

    @Test
    @DisplayName("BIL-TC-005 - VNPay Callback Thất bại")
    void vnpayReturn_Failed_RedirectsToError() throws Exception {
        // Arrange
        when(vnPayService.verifySignature(anyMap())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/billing/checkout/vnpay-return")
                .param("vnp_TxnRef", "103")
                .param("vnp_ResponseCode", "24")
                .param("bookingId", "1")
                .param("vnp_SecureHash", "hash123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/checkout?bookingId=1"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(billingService, times(1)).markPaymentAsFailed(103);
        verify(billingService, never()).completePaymentAndCheckout(anyInt(), anyString());
    }

    @Test
    @DisplayName("BIL-TC-006 - VNPay Callback Sai Chữ Ký")
    void vnpayReturn_InvalidSignature_RedirectsToError() throws Exception {
        // Arrange
        when(vnPayService.verifySignature(anyMap())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/billing/checkout/vnpay-return")
                .param("vnp_TxnRef", "104")
                .param("vnp_ResponseCode", "00")
                .param("bookingId", "1")
                .param("vnp_SecureHash", "invalidhash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/checkout?bookingId=1"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(billingService, never()).completePaymentAndCheckout(anyInt(), anyString());
        verify(billingService, never()).markPaymentAsFailed(anyInt());
    }
}
