package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;

public interface SpaManualBookingService {
    
    SpaScheduleResponse bookAdditionalService(SpaScheduleRequest request, Integer receptionistUserId);
}
