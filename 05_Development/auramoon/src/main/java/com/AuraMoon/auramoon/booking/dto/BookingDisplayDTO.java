package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDisplayDTO {
    private Integer id;
    private Integer guestId;
    private String packageName;      // Tên gói để tránh lazy load
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private String assignedVillaCode; // Mã phòng vật lý đã gán
    private String bookingStatus;
    private Boolean consentApproved;
}
