package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.request.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.entity.User;

public interface IAuthService {
    void register(UserRegistrationDto registrationDto);
    boolean verifyEmail(String token);
    User findByEmail(String email);
    User createGoogleUser(String email, String fullName);
    void resetPassword(String email);
}
