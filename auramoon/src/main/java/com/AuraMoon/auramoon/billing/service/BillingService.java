package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;

import com.AuraMoon.auramoon.billing.entity.Payment;

public interface BillingService {
    CheckoutViewDTO getCheckoutData(Integer bookingId);
    
    Payment initiatePayment(Integer bookingId, String paymentMethod, String paymentGateway);
    void completePaymentAndCheckout(Integer paymentId, String transactionCode);
    void markPaymentAsFailed(Integer paymentId);
}
