package com.AuraMoon.auramoon.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.build.AllowSysOut;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterDto {

    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
}
