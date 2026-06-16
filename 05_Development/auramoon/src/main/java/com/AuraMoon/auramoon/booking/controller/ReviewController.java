package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String showReviewForm(@RequestParam Integer bookingId, Model model, RedirectAttributes redirectAttributes) {
        try {
            reviewService.canSubmitReview(bookingId);
            model.addAttribute("bookingId", bookingId);
            return "booking/review/submit";
        } catch (Exception e) {
            e.printStackTrace(); // ĐỂ DEBUG LỖI TRÊN CONSOLE
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/"; // Redirect to home or another page on error
        }
    }

    @PostMapping("/submit")
    public String submitReview(
            @RequestParam Integer bookingId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String comment,
            RedirectAttributes redirectAttributes) {
        try {
            if (rating == null || rating == 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn số sao đánh giá.");
                return "redirect:/review?bookingId=" + bookingId;
            }
            reviewService.submitReview(bookingId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá trải nghiệm!");
            return "redirect:/"; // Or redirect to a generic success page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/review?bookingId=" + bookingId;
        }
    }
}
