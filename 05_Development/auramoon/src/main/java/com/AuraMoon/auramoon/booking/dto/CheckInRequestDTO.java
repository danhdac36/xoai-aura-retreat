package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequestDTO {
    private Integer bookingId;
    @NotBlank(message = "Số CCCD/Hộ chiếu không được để trống")
    @Pattern(regexp = "^(\\d{12}|[A-Za-z0-9]{8,12})$", message = "Số CCCD phải bao gồm đúng 12 chữ số, hoặc số Hộ chiếu từ 8-12 ký tự chữ và số")
    private String identifyCode;
    private Integer villaId;
    private Boolean privacyConsent;
    private String fullName;
    private String phone;
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate dateOfBirth;
}
