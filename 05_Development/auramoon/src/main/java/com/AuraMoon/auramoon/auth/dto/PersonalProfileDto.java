package com.AuraMoon.auramoon.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.AuraMoon.auramoon.auth.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProfileDto {
    private String fullName;
    private String email;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Giới tính không hợp lệ")
    private String gender;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 255, message = "Identity code quá dài")
    private String identifyCode;

    @Past(message = "Ngày sinh phải ở trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    public static PersonalProfileDto fromEntity(User user) {
        if (user == null) return new PersonalProfileDto();
        return PersonalProfileDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .gender(user.getGender())
                .phone(user.getPhone())
                .identifyCode(user.getIdentifyCode())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
