package com.AuraMoon.auramoon.common.advice;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class SidebarAdvice {

    private final BookingRepository bookingRepository;

    @ModelAttribute("hasBookings")
    public boolean hasBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsResponse) {
            UserDetailsResponse user = (UserDetailsResponse) auth.getPrincipal();
            boolean isGuest = user.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_GUEST".equalsIgnoreCase(a.getAuthority()) 
                            || "GUEST".equalsIgnoreCase(a.getAuthority()));
            if (isGuest) {
                return !bookingRepository.findByGuestId(user.getId()).isEmpty();
            }
            return true;
        }
        return false;
    }

    @ModelAttribute("hasActiveBooking")
    public boolean hasActiveBooking() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsResponse) {
            UserDetailsResponse user = (UserDetailsResponse) auth.getPrincipal();
            boolean isGuest = user.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_GUEST".equalsIgnoreCase(a.getAuthority()) 
                            || "GUEST".equalsIgnoreCase(a.getAuthority()));
            if (isGuest) {
                List<Booking> bookings = bookingRepository.findByGuestId(user.getId());
                return bookings.stream().anyMatch(b -> 
                        "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus())
                        || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus())
                        || "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()));
            }
            return true;
        }
        return false;
    }
}
