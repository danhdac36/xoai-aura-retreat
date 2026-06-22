package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;

public interface CheckInService {
    void performCheckIn(CheckInRequestDTO request);
    java.util.List<com.AuraMoon.auramoon.booking.dto.BookingDisplayDTO> getAllBookingsForDisplay();
}
