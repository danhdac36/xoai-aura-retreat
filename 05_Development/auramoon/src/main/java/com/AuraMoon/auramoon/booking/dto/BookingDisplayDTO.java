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
public class BookingDisplayDTO {
    private Integer id;
    private Integer guestId;
    private String packageName;      // Tên gói để tránh lazy load
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private String assignedVillaCode; // Mã phòng vật lý đã gán
    private String requestedVillaTypeName;
    private String bookingStatus;
    private Boolean consentApproved;
    private String guestName;
    private String guestPhone;
    private String guestGender;
    private LocalDate guestDateOfBirth;
    private String medicalConditions;
    private String injuries;
}
