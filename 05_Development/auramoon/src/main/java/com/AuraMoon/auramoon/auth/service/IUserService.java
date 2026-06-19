package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.entity.User;

import java.util.List;

public interface IUserService {
    User createUser(User user);
    List<User> getAllUser();
    User findUserById(Long id);
    User findUserByEmail(String email);
    User registerUser(UserRegistrationDto register);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
