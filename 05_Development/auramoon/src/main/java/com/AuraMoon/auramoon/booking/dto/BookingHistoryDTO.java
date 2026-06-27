package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryDTO {
    private Integer bookingId;
    private String packageName;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Double totalAmount;
    private String status;
    private String villaName;
}
