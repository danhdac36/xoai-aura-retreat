package com.AuraMoon.auramoon.booking.service;

import java.time.LocalDate;

public interface VillaService {
    boolean checkVillaAvailability(Integer villaTypeId, LocalDate checkinDate, LocalDate checkoutDate);
    void updateVillaStatuses(Integer villaId, String villaStatus, String cleaningStatus);
}
