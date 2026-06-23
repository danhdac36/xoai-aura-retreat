package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    private Integer retreatPackageId;
    private Integer villaTypeId;
    private LocalDateTime checkinDate;
    private Integer totalGuests;
    private Boolean privacyConsent;
}
