package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import jakarta.servlet.http.HttpSession;
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
    public String showItinerary(@RequestParam(value = "guestId", required = false) Integer guestId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Anti-IDOR: Chỉ Lễ tân/Admin mới được xem itinerary của khách khác. Khách chỉ xem của mình.
        if (guestId == null) {
            guestId = currentUser.getId();
        } else {
            String roleName = (currentUser.getRole() != null) ? currentUser.getRole().getRoleName() : "";
            if (!"RECEPTIONIST".equalsIgnoreCase(roleName) && !"ADMIN".equalsIgnoreCase(roleName)) {
                // Phớt lờ guestId trên URL, ép buộc dùng ID của chính currentUser
                guestId = currentUser.getId();
            }
        }

        try {
            ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);
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
