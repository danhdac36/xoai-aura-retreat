package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItineraryServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    @Mock
    private com.AuraMoon.auramoon.fnb.repository.MealOrderRepository mealOrderRepository;

    @InjectMocks
    private ItineraryServiceImpl itineraryService;

    @Test
    @DisplayName("ITI10-TC-001 - Lấy Timeline lịch trình thành công cho gói Stress Relief")
    void getTimelineForGuest_Success_StressRelief() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .typePackage("stress relief")
                .build();

        Booking booking = Booking.builder()
                .id(101)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDateTime.of(2026, 6, 20, 0, 0))
                .checkoutDate(LocalDateTime.of(2026, 6, 22, 0, 0))
                .retreatPackage(retreatPackage)
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));

        // Act
        ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);

        // Assert
        assertNotNull(timeline);
        assertEquals(101, timeline.getBookingId());
        assertEquals("Nguyễn Văn Khách", timeline.getGuestName());
        assertFalse(timeline.getEvents().isEmpty());
    }

    @Test
    @DisplayName("ITI10-TC-002 - Lấy Timeline lịch trình thành công cho gói Detox")
    void getTimelineForGuest_Success_Detox() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .typePackage("detox weight loss")
                .build();

        Booking booking = Booking.builder()
                .id(102)
                .guestId(guestId)
                .bookingStatus("CHECKED_IN")
                .checkinDate(LocalDateTime.of(2026, 6, 20, 0, 0))
                .checkoutDate(LocalDateTime.of(2026, 6, 22, 0, 0))
                .retreatPackage(retreatPackage)
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));

        // Act
        ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);

        // Assert
        assertNotNull(timeline);
        assertEquals(102, timeline.getBookingId());
        assertFalse(timeline.getEvents().isEmpty());
    }

    @Test
    @DisplayName("ITI10-TC-003 - Khách hàng không tồn tại -> Exception")
    void getTimelineForGuest_GuestNotFound_ThrowsException() {
        // Arrange
        Integer guestId = 999;
        when(userRepository.findById(guestId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.getTimelineForGuest(guestId);
        });

        assertTrue(exception.getMessage().contains("Không tìm thấy khách hàng"));
    }

    @Test
    @DisplayName("ITI10-TC-004 - Khách hàng không có lịch đặt phòng -> Exception")
    void getTimelineForGuest_NoBookings_ThrowsException() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.getTimelineForGuest(guestId);
        });

        assertTrue(exception.getMessage().contains("chưa có bất kỳ lịch đặt phòng nào"));
    }
}
