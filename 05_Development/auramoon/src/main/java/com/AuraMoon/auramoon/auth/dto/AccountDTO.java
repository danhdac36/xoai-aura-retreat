package com.AuraMoon.auramoon.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountDTO {

    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    // Password is now generated for new users, so we don't bind it from the form
    // We remove passwordRaw from validation

    @NotNull(message = "Chưa chọn chức vụ")
    private Integer roleId;

    private String phone;
    
    private String identifyCode;
}
