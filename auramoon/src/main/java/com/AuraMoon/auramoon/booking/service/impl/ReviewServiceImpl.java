package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Review;
import com.AuraMoon.auramoon.booking.exception.BookingNotCompletedException;
import com.AuraMoon.auramoon.booking.exception.ReviewAlreadyExistsException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.ReviewRepository;
import com.AuraMoon.auramoon.booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Override
    public boolean canSubmitReview(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
                
        if (!"COMPLETED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BookingNotCompletedException("Chỉ có thể đánh giá khi đơn hàng đã hoàn tất (COMPLETED).");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new ReviewAlreadyExistsException("Bạn đã đánh giá trải nghiệm này rồi.");
        }
        
        return true;
    }

    @Override
    public Review submitReview(Integer bookingId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Điểm đánh giá phải từ 1 đến 5 sao.");
        }

        // Validate first
        canSubmitReview(bookingId);

        Booking booking = bookingRepository.findById(bookingId).get();
        Review review = Review.builder()
                .booking(booking)
                .rating(rating)
                .comment(comment)
                .build();
                
        return reviewRepository.save(review);
    }
}
