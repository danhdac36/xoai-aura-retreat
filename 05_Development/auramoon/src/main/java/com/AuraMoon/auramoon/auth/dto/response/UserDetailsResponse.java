package com.AuraMoon.auramoon.auth.dto.response;

import com.AuraMoon.auramoon.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;
import java.util.Collection;

public class UserDetailsResponse implements UserDetails {
    private User user; // Chứa nguyên cái Entity User của bạn

    public UserDetailsResponse(User user) {
        this.user = user;
    }

    // Các hàm bắt buộc của Spring
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().getRoleName();
        if (!roleName.startsWith("ROLE_"))
            roleName = "ROLE_" + roleName;
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // --- CÁC HÀM CUSTOM ĐỂ THYMELEAF GỌI ĐƯỢC ---
    public String getFullName() {
        return user.getFullName();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getRoleName() {
        return user.getRole().getRoleName();
    }

    public Integer getId() {
        return user.getId();
    }

    // Các hàm config tài khoản (mặc định return true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(user.getStatus());
    }
}
