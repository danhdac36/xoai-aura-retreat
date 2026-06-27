package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @GetMapping("/booking/itinerary")
    public String showItinerary(@RequestParam(value = "guestId", required = false) Integer guestId,
                                @RequestParam(value = "bookingId", required = false) Integer bookingId,
                                Model model, 
                                @AuthenticationPrincipal UserDetailsResponse currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            ItineraryTimelineDTO timeline;
            
            if (bookingId != null) {
                // Lấy timeline cho một booking cụ thể (từ Lịch sử)
                timeline = itineraryService.getTimelineForBooking(bookingId);
            } else {
                // Lấy timeline hiện tại của guest (Anti-IDOR)
                if (guestId == null) {
                    guestId = currentUser.getId();
                } else {
                    String roleName = (currentUser.getAuthorities() != null && !currentUser.getAuthorities().isEmpty()) 
                            ? currentUser.getAuthorities().iterator().next().getAuthority() : "";
                    if (!"ROLE_RECEPTIONIST".equalsIgnoreCase(roleName) && 
                        !"ROLE_ADMIN".equalsIgnoreCase(roleName) && 
                        !"ROLE_MANAGER".equalsIgnoreCase(roleName)) {
                        guestId = currentUser.getId();
                    }
                }
                timeline = itineraryService.getTimelineForGuest(guestId);
            }

            model.addAttribute("timeline", timeline);
            return "guest/itinerary";
        } catch (IllegalArgumentException e) {
            // Chuẩn hóa Redirect theo EDS sang trang dashboard thực tế (/profile/home)
            return "redirect:/profile/home?error=no_booking";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
