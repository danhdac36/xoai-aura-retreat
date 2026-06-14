package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SpaBookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "SELECT b.booking_id as bookingId, u.full_name as guestName, v.villa_code as villaCode " +
            "FROM BOOKING b " +
            "JOIN [USER] u ON b.guest_id = u.user_id " +
            "LEFT JOIN VILLA v ON b.assigned_villa_id = v.villa_id " +
            "WHERE UPPER(b.booking_status) IN ('CHECKED_IN', 'CHECKED-IN') AND b.is_delete = 0", nativeQuery = true)
    List<Map<String, Object>> findCheckedInBookingsWithGuestDetails();
}
