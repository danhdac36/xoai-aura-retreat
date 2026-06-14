package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;

public interface ItineraryService {
    ItineraryTimelineDTO getTimelineForGuest(Integer guestId);
}
