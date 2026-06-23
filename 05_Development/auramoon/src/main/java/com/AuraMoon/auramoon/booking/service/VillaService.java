package com.AuraMoon.auramoon.booking.service;

import java.time.LocalDateTime;

public interface VillaService {
    boolean checkVillaAvailability(Integer villaTypeId, LocalDateTime checkinDate, LocalDateTime checkoutDate);
    void updateVillaStatuses(Integer villaId, String villaStatus, String cleaningStatus);
    java.util.List<com.AuraMoon.auramoon.booking.dto.VillaDisplayDTO> getAllVillasForDisplay();
}
