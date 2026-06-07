package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;

public interface BillingService {
    CheckoutViewDTO getCheckoutData(Integer bookingId);
}
