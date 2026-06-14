package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByGuestFolioIdAndStatus(Integer folioId, String status);
}
