package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.entity.Review;
import com.AuraMoon.auramoon.booking.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ManagerReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @GetMapping("/manager/reviews")
    public String viewReviews(Model model) {
        List<Review> reviews = reviewRepository.findAll();
        
        Map<Integer, String> guestNames = new HashMap<>();
        for (Review r : reviews) {
            Integer guestId = r.getBooking().getGuestId();
            if (guestId != null && !guestNames.containsKey(guestId)) {
                userRepository.findById(guestId).ifPresent(user -> {
                    guestNames.put(guestId, user.getFullName());
                });
            }
        }
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("guestNames", guestNames);
        return "manager/reviews";
    }
}
