package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByGuestId(Integer guestId);
    List<Booking> findByBookingStatusInAndCheckinDateBetween(List<String> statuses, LocalDate start, LocalDate end);
    boolean existsByGuestIdAndBookingStatusIn(Integer guestId, List<String> statuses);
}

