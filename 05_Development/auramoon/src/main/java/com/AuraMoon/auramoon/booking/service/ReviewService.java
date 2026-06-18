package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.entity.Review;

public interface ReviewService {
    boolean canSubmitReview(Integer bookingId);
    Review submitReview(Integer bookingId, Integer rating, String comment);
}
