package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestFolioRepository extends JpaRepository<GuestFolio, Integer> {
    Optional<GuestFolio> findByBookingId(Integer bookingId);
}
