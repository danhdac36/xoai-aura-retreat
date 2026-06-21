package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;

public interface CheckInService {
    void performCheckIn(CheckInRequestDTO request);
}
