package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface TreatmentBookingRepository extends JpaRepository<TreatmentBooking, Integer> {
    List<TreatmentBooking> findByBookingIdAndTreatmentService_Id(Integer bookingId, Integer serviceId);
}
