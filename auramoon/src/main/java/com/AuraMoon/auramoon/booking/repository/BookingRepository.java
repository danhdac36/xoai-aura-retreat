package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByGuestId(Integer guestId);
}
