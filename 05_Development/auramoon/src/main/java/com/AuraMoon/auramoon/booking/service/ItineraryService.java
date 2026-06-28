package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;

import java.util.List;

public interface ItineraryService {
    ItineraryTimelineDTO getTimelineForGuest(Integer guestId);

    org.springframework.data.domain.Page<BookingHistoryDTO> getBookingHistory(Integer guestId, String status, int page, int size);

    ItineraryTimelineDTO getTimelineForBooking(Integer bookingId);
}
