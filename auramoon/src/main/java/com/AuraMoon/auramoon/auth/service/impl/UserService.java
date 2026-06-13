package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.request.RegisterDto;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public User authenticate(String email, String passwordHash) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (!user.getPasswordHash().equals(passwordHash)) {
            return null;
        }
        if (user.getStatus().equals("BLOCKED")) {
            return null;
        }
        return user;
    }

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public List<User> getAllUser() {
        return null;
    }

    @Override
    public User findUserById(Long id) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(RegisterDto register) {
        if (userRepository.existsByEmail(register.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        Role guestRole = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new RuntimeException("Role GUEST không tồn tại trong hệ thống!"));

        User user = new User();
        user.setFullName(register.getFullName());
        user.setEmail(register.getEmail());
        user.setPasswordHash(register.getPassword());
        user.setRole(guestRole);
        user.setStatus("ACTIVE");
        // user.setIsDelete(false);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {
    }
}
