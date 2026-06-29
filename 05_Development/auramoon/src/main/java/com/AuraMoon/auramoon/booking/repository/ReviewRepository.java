package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByBookingId(Integer bookingId);
    
    java.util.List<Review> findByIsDeleteFalse();
}
