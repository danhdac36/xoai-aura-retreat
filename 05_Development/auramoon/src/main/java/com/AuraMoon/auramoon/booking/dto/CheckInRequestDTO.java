package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequestDTO {
    private Integer bookingId;
    private String identifyCode;
    private Integer villaId;
    private Boolean privacyConsent;
    private String fullName;
    private String phone;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate dateOfBirth;
}
