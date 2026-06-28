package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Review;
import com.AuraMoon.auramoon.common.enums.BookingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository; // Requires a BookingRepository to persist Booking

    @Test
    public void shouldReturnOnlyNonDeletedReviews() {
        reviewRepository.deleteAll();

        // Arrange
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.CHECKED_OUT.name());
        // Populate required booking fields here as needed
        booking = bookingRepository.save(booking);

        Review review1 = new Review();
        review1.setBooking(booking);
        review1.setRating(5);
        review1.setComment("Great!");
        review1.setIsDelete(false);

        Review review2 = new Review();
        review2.setBooking(booking);
        review2.setRating(1);
        review2.setComment("Bad");
        review2.setIsDelete(true);

        // Act
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        
        List<Review> reviews = reviewRepository.findByIsDeleteFalse();

        // Assert
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getComment()).isEqualTo("Great!");
    }
}

