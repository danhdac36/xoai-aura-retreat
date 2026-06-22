package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.service.impl.ItineraryServiceImpl;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItineraryTimelineServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MealOrderRepository mealOrderRepository;

    @InjectMocks
    private ItineraryServiceImpl itineraryService;

    @Test
    public void UC10_TC_001_getTimelineForGuest_ReturnsDynamicData() {
        // Arrange
        Integer guestId = 100;
        User guest = User.builder().id(guestId).fullName("Nguyen Van A").build();
        Booking booking = Booking.builder()
                .id(1001)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now())
                .checkoutDate(LocalDate.now().plusDays(2))
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1001)).thenReturn(Collections.emptyList());
        when(mealOrderRepository.findByBookingId(1001)).thenReturn(Collections.emptyList());

        // Act
        ItineraryTimelineDTO result = itineraryService.getTimelineForGuest(guestId);

        // Assert
        // In the new dynamic design, since we haven't mocked any Spa/Meal data (or they are empty),
        // we expect ONLY 2 events: Check-in and Check-out.
        // However, the current code generates template events (Yoga, Lunch, Spa, Dinner) -> Total 6 events.
        assertEquals(2, result.getEvents().size(), "Expected only Check-in and Check-out if no dynamic activities found.");
    }
}
