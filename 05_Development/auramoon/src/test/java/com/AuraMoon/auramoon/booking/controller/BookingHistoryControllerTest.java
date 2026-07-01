package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItineraryService itineraryService;

    private UserDetailsResponse mockGuestUserDetails;
    private UserDetailsResponse mockManagerUserDetails;

    @BeforeEach
    public void setup() {
        Role guestRole = new Role(1, "GUEST");
        User guestUser = User.builder().id(1).role(guestRole).build();
        mockGuestUserDetails = new UserDetailsResponse(guestUser);

        Role managerRole = new Role(2, "MANAGER");
        User managerUser = User.builder().id(99).role(managerRole).build();
        mockManagerUserDetails = new UserDetailsResponse(managerUser);
    }

    @Test
    @DisplayName("BKG-TC-001: Guest xem lịch sử đặt phòng của chính mình")
    public void BKG_TC_001_GuestViewsOwnHistory() throws Exception {
        // Arrange
        Page<com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO> mockPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 5), 0);
        Mockito.when(itineraryService.getBookingHistory(1, null, 0, 5)).thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/booking/history")
                .with(user(mockGuestUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookingsPage"));

        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(eq(1), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("BKG-TC-002: Manager xem lịch sử đặt phòng của guest khác")
    public void BKG_TC_002_ManagerViewsGuestHistory() throws Exception {
        // Arrange
        Mockito.when(itineraryService.getBookingHistory(eq(2), any(), anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act & Assert
        mockMvc.perform(get("/booking/history").param("guestId", "2")
                .with(user(mockManagerUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookingsPage"));

        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(eq(2), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("BKG-TC-003: Guest cố gắng truy cập lịch sử của guest khác → bị bỏ qua guestId")
    public void BKG_TC_003_GuestAccessesAnotherGuestHistory_ShouldIgnoreGuestId() throws Exception {
        // Arrange
        Mockito.when(itineraryService.getBookingHistory(eq(1), any(), anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act & Assert - Guest tries to access guestId=2
        mockMvc.perform(get("/booking/history").param("guestId", "2")
                .with(user(mockGuestUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("guest/itinerary_history"))
                .andExpect(model().attributeExists("bookingsPage"));

        // Verify that it ignores guestId=2 and uses guestId=1 from Principal
        Mockito.verify(itineraryService, Mockito.times(1)).getBookingHistory(eq(1), any(), anyInt(), anyInt());
        Mockito.verify(itineraryService, Mockito.never()).getBookingHistory(eq(2), any(), anyInt(), anyInt());
    }
}
