package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestFolioRepository extends JpaRepository<GuestFolio, Integer> {
    Optional<GuestFolio> findByBookingId(Integer bookingId);
    List<GuestFolio> findByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
    List<GuestFolio> findByStatus(String status);
    long countByStatus(String status);
}
