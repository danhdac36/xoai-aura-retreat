package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Review;
import com.AuraMoon.auramoon.booking.exception.BookingNotCompletedException;
import com.AuraMoon.auramoon.booking.exception.ReviewAlreadyExistsException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("REV-TC-001 - Nộp đánh giá hợp lệ")
    void submitReview_ValidBooking_ReviewSavedSuccessfully() {
        // Arrange
        Integer bookingId = 1;
        Integer rating = 5;
        String comment = "Good!";
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingStatus("CHECKED_OUT");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBookingId(bookingId)).thenReturn(false);
        
        Review savedReview = new Review();
        savedReview.setId(100);
        savedReview.setBooking(booking);
        savedReview.setRating(rating);
        savedReview.setComment(comment);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act
        Review result = reviewService.submitReview(bookingId, rating, comment);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Good!", result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("REV-TC-002 - Đơn chưa hoàn tất")
    void canSubmitReview_BookingNotCompleted_ThrowsBookingNotCompletedException() {
        // Arrange
        Integer bookingId = 2;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingStatus("PENDING");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        BookingNotCompletedException exception = assertThrows(BookingNotCompletedException.class, () -> {
            reviewService.canSubmitReview(bookingId);
        });

        assertEquals("Chỉ có thể đánh giá khi đơn hàng đã check-out (CHECKED_OUT).", exception.getMessage());
        verify(reviewRepository, never()).existsByBookingId(anyInt());
    }

    @Test
    @DisplayName("REV-TC-003 - Chặn spam đánh giá 2 lần")
    void canSubmitReview_ReviewAlreadyExists_ThrowsReviewAlreadyExistsException() {
        // Arrange
        Integer bookingId = 1;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingStatus("CHECKED_OUT");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBookingId(bookingId)).thenReturn(true);

        // Act & Assert
        ReviewAlreadyExistsException exception = assertThrows(ReviewAlreadyExistsException.class, () -> {
            reviewService.canSubmitReview(bookingId);
        });

        assertEquals("Bạn đã đánh giá trải nghiệm này rồi.", exception.getMessage());
    }

    @Test
    @DisplayName("REV-TC-XSS - XSS trong comment được escape")
    void submitReview_CommentWithXSS_CommentIsEscaped() {
        // Arrange
        Integer bookingId = 3;
        Integer rating = 5;
        String maliciousComment = "<script>alert(1)</script>";
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingStatus("CHECKED_OUT");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBookingId(bookingId)).thenReturn(false);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

        // Act
        reviewService.submitReview(bookingId, rating, maliciousComment);

        // Assert
        verify(reviewRepository).save(reviewCaptor.capture());
        Review capturedReview = reviewCaptor.getValue();
        
        String expectedEscapedComment = "&lt;script&gt;alert(1)&lt;/script&gt;";
        assertEquals(expectedEscapedComment, capturedReview.getComment());
    }
}
