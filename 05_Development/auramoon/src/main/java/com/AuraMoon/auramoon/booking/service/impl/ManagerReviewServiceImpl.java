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

    @Override
    public List<Object> getVisibleReviews() {
        return new ArrayList<>(reviewRepository.findByIsDeleteFalse());
    }
}
