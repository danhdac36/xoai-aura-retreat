package com.AuraMoon.auramoon.booking.service;
import java.util.List;
public interface IManagerReviewService {
    void hideReview(long id);
    Object getMetrics();
    List<Object> getVisibleReviews();
}
