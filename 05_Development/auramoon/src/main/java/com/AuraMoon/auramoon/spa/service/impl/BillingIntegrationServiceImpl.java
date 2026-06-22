package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.service.BillingIntegrationService;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BillingIntegrationServiceImpl implements BillingIntegrationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<Integer> findFolioIdByBookingId(Integer bookingId) {
        try {
            String jpql = "SELECT gf.id FROM GuestFolio gf WHERE gf.bookingId = :bookingId AND gf.isDelete = false";
            Integer folioId = entityManager.createQuery(jpql, Integer.class)
                    .setParameter("bookingId", bookingId)
                    .getSingleResult();
            return Optional.ofNullable(folioId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void createFolioItem(Integer folioId, Integer referenceId, String serviceCategory, 
                                String description, BigDecimal amount, Integer createdBy) {
        GuestFolio guestFolio = entityManager.getReference(GuestFolio.class, folioId);
        
        FolioItem folioItem = FolioItem.builder()
                .guestFolio(guestFolio)
                .serviceCategory(serviceCategory)
                .referenceId(referenceId)
                .description(description)
                .amount(amount)
                .createBy(createdBy)
                .createAt(LocalDateTime.now())
                .status("UNPAID")
                .build();
                
        entityManager.persist(folioItem);
    }
}
