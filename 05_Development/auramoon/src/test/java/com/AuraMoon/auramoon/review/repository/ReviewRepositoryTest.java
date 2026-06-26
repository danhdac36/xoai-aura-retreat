package com.AuraMoon.auramoon.review.repository;

import com.AuraMoon.auramoon.common.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private IReviewRepository reviewRepository;

    @Test
    public void shouldCalculateAverageRatingExcludingDeleted() {
        // Arrange
        Review activeReview = new Review();
        activeReview.setRating(5);
        activeReview.setIsDelete(false);
        reviewRepository.save(activeReview);

        Review hiddenReview = new Review();
        hiddenReview.setRating(1);
        hiddenReview.setIsDelete(true);
        reviewRepository.save(hiddenReview);

        // Act
        Double avgRating = reviewRepository.getAverageRating();

        // Assert
        assertEquals(5.0, avgRating, "Average rating should only include isDelete=false");
    }
}
