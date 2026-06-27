package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.service.IManagerReviewService;
import com.AuraMoon.auramoon.booking.repository.ReviewRepository;
import com.AuraMoon.auramoon.booking.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class ManagerReviewServiceImpl implements IManagerReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void hideReview(long id) {
        reviewRepository.findById((int) id).ifPresent(review -> {
            review.setIsDelete(true);
            reviewRepository.save(review);
        });
    }

    @Override
    public Object getMetrics() {
        List<Review> reviews = reviewRepository.findByIsDeleteFalse();
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return java.util.Map.of("totalReviews", reviews.size(), "averageRating", String.format("%.1f", avg));
    }

    @Autowired
    private com.AuraMoon.auramoon.auth.repository.UserRepository userRepository;

    @Override
    public List<Object> getVisibleReviews() {
        List<Review> reviews = reviewRepository.findByIsDeleteFalse();
        return reviews.stream().map(review -> {
            java.util.Map<String, Object> dto = new java.util.HashMap<>();
            dto.put("id", review.getId());
            dto.put("rating", review.getRating());
            dto.put("comment", review.getComment());
            
            if (review.getBooking() != null) {
                dto.put("bookingId", review.getBooking().getId());
                Integer guestId = review.getBooking().getGuestId();
                if (guestId != null) {
                    com.AuraMoon.auramoon.auth.entity.User user = userRepository.findById(guestId).orElse(null);
                    dto.put("guestName", user != null && user.getFullName() != null ? user.getFullName() : "Guest " + guestId);
                    dto.put("guestId", guestId);
                } else {
                    dto.put("guestName", "Unknown Guest");
                    dto.put("guestId", null);
                }
            } else {
                dto.put("bookingId", "N/A");
                dto.put("guestName", "Unknown Guest");
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}
