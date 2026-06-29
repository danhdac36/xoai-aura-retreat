package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Integer bookingId;
    private Integer guestId;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private Integer totalGuests;
    private String bookingStatus;
    private String paymentStatus;
    private String retreatPackageName;
    private String requestedVillaTypeName;
    private String assignedVillaCode;
}
