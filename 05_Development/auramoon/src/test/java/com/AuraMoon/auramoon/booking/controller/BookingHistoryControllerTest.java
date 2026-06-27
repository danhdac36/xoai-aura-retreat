package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.dto.UserDetailsResponse;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingHistoryController.class, properties = "spring.security.filter.chains.enabled=false")
public class BookingHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItineraryService itineraryService;

    private UserDetailsResponse mockGuestUserDetails;
    private UserDetailsResponse mockManagerUserDetails;

    @BeforeEach
    public void setup() {
        User guestUser = User.builder().id(1).role(com.AuraMoon.auramoon.auth.enums.UserRole.GUEST).build();
        mockGuestUserDetails = new UserDetailsResponse(guestUser, Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST")));

        User managerUser = User.builder().id(99).role(com.AuraMoon.auramoon.auth.enums.UserRole.MANAGER).build();
        mockManagerUserDetails = new UserDetailsResponse(managerUser, Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }

    @Test
    public void BKG_TC_001_GuestViewsOwnHistory() throws Exception {
        // Arrange
        Mockito.when(itineraryService.getBookingHistory(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/booking/history")
                .principal(new UsernamePasswordAuthenticationToken(mockGuestUserDetails, null, mockGuestUserDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookings"));

        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(1);
    }

    @Test
    public void BKG_TC_002_ManagerViewsGuestHistory() throws Exception {
        // Arrange
        Mockito.when(itineraryService.getBookingHistory(2)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/booking/history").param("guestId", "2")
                .principal(new UsernamePasswordAuthenticationToken(mockManagerUserDetails, null, mockManagerUserDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookings"));

        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(2);
    }

    @Test
    public void BKG_TC_003_GuestAccessesAnotherGuestHistory_ShouldIgnoreGuestId() throws Exception {
        // Arrange
        Mockito.when(itineraryService.getBookingHistory(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        // Guest tries to access guestId=2
        mockMvc.perform(get("/booking/history").param("guestId", "2")
                .principal(new UsernamePasswordAuthenticationToken(mockGuestUserDetails, null, mockGuestUserDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookings"));

        // Verify that it ignores guestId=2 and uses guestId=1 from Principal
        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(1);
        Mockito.verify(itineraryService, Mockito.never()).getBookingHistory(2);
    }

    @Test
    public void BKG_TC_004_GuestViewsItineraryDetails() throws Exception {
        // Arrange
        com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO timelineDTO = new com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO();
        timelineDTO.setGuestName("Khach Hang Test");
        Mockito.when(itineraryService.getTimelineForGuest(100)).thenReturn(timelineDTO);
        
        // Act & Assert
        mockMvc.perform(get("/booking/itinerary").param("bookingId", "100")
                .principal(new UsernamePasswordAuthenticationToken(mockGuestUserDetails, null, mockGuestUserDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary"))
                .andExpect(model().attributeExists("timeline"));

        Mockito.verify(itineraryService, Mockito.times(1)).getTimelineForGuest(100);
    }
}
