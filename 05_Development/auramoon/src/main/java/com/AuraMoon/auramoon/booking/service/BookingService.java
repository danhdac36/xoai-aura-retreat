package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.BookingRequestDTO;
import com.AuraMoon.auramoon.booking.dto.BookingResponseDTO;

public interface BookingService {
    BookingResponseDTO createBooking(Integer guestId, BookingRequestDTO request);
    void confirmPayment(Integer bookingId, String transactionCode);
    boolean hasActiveBooking(Integer guestId);
}
