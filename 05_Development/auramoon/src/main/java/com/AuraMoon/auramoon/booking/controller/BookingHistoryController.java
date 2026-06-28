package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookingHistoryController {

    private final ItineraryService itineraryService;

    @GetMapping("/booking/history")
    public String viewHistory(@RequestParam(value = "guestId", required = false) Integer guestId,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @RequestParam(value = "status", required = false) String status,
                              @AuthenticationPrincipal UserDetailsResponse currentUser,
                              Model model) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        Integer targetGuestId = currentUser.getId();

        String roleName = "";
        if (currentUser.getAuthorities() != null && !currentUser.getAuthorities().isEmpty()) {
            roleName = currentUser.getAuthorities().iterator().next().getAuthority();
        }

        if ("ROLE_MANAGER".equals(roleName) || "ROLE_ADMIN".equals(roleName)) {
            if (guestId != null) {
                targetGuestId = guestId;
            }
        }

        Page<BookingHistoryDTO> bookingsPage = itineraryService.getBookingHistory(targetGuestId, status, page, size);
        model.addAttribute("bookingsPage", bookingsPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("targetGuestId", targetGuestId);
        return "guest/itinerary_history";
    }
}

