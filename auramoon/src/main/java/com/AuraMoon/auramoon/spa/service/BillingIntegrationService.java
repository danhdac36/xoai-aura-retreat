package com.AuraMoon.auramoon.spa.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface BillingIntegrationService {
    
    Optional<Integer> findFolioIdByBookingId(Integer bookingId);
    
    void createFolioItem(Integer folioId, Integer referenceId, String serviceCategory, 
                         String description, BigDecimal amount, Integer createdBy);
}
