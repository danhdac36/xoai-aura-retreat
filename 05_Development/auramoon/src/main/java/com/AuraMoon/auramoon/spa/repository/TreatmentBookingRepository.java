package com.AuraMoon.auramoon.spa.repository;

// 1. Thư viện chuẩn của Java
import java.util.List;

// 2. Thư viện Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 3. Thư viện nội bộ của dự án
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;

public interface TreatmentBookingRepository extends JpaRepository<TreatmentBooking, Integer> {

    List<TreatmentBooking> findByBookingIdAndTreatmentService_Id(Integer bookingId, Integer serviceId);

    @Query(value = "SELECT tb.* FROM TREATMENT_BOOKING tb " +
            "JOIN BOOKING b ON tb.booking_id = b.booking_id " +
            "WHERE b.guest_id = :guestId AND tb.status != 'Scheduled' AND tb.is_delete = 0", nativeQuery = true)
    List<TreatmentBooking> findUnscheduledBookingsByGuestId(@Param("guestId") Integer guestId);
}