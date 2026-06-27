package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;

import java.util.List;

public interface ItineraryService {
    ItineraryTimelineDTO getTimelineForGuest(Integer guestId);

    List<BookingHistoryDTO> getBookingHistory(Integer guestId);

    ItineraryTimelineDTO getTimelineForBooking(Integer bookingId);
}
