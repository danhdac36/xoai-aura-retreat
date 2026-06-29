package com.AuraMoon.auramoon.auth.dto;

import com.AuraMoon.auramoon.auth.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyAccountDto {
    private Integer id;
    private String email;
    private String roleName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public static MyAccountDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return MyAccountDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
