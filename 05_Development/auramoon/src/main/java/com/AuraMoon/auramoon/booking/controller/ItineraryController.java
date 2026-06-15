package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
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

    @GetMapping("/itinerary")
    public String showItinerary(@RequestParam(value = "guestId", required = false) Integer guestId, Model model, jakarta.servlet.http.HttpSession session) {
        com.AuraMoon.auramoon.auth.entity.User currentUser = (com.AuraMoon.auramoon.auth.entity.User) session.getAttribute("currentUser");
        if (guestId == null) {
            if (currentUser != null) {
                guestId = currentUser.getId();
            } else {
                return "redirect:/login";
            }
        }
        try {
            ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);
            model.addAttribute("timeline", timeline);
            return "guest/itinerary";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
