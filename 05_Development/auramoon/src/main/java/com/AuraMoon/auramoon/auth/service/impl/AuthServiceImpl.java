package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trên hệ thống!");
        }

        Role guestRole = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new IllegalStateException("Không cấu hình được vai trò GUEST mặc định"));

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setRole(guestRole);
        user.setStatus("INACTIVE");
        user.setIsDelete(false);
        String token = UUID.randomUUID().toString();
        user.setVerifyToken(token);

        userRepository.save(user);

        sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerifyToken(token);
        if (user == null) {
            return false;
        }

        user.setStatus("ACTIVE");
        user.setVerifyToken(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User createGoogleUser(String email, String fullName) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.getRole() != null) {
                user.getRole().getRoleName(); // Force lazy load proxy while session is active
            }
            return user;
        }

        Role guestRole = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new IllegalStateException("Không cấu hình được vai trò GUEST mặc định"));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password for SSO login
        newUser.setRole(guestRole);
        newUser.setStatus("ACTIVE"); // Google accounts are auto-active
        newUser.setIsDelete(false);
        newUser.setVerifyToken(null);

        User savedUser = userRepository.save(newUser);
        if (savedUser.getRole() != null) {
            savedUser.getRole().getRoleName(); // Force lazy load proxy while session is active
        }
        return savedUser;
    }

    private void sendVerificationEmail(String email, String token) {
        if (mailSender == null) {
            System.out.println("[WARNING] JavaMailSender chưa được cấu hình. Link kích hoạt: http://localhost:8080/auth/verify-email?token=" + token);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Xoai Aura Retreat] Xác thực kích hoạt tài khoản");
            message.setText("Cảm ơn bạn đã lựa chọn nghỉ dưỡng tại Xoai Aura Retreat.\n" +
                    "Vui lòng click vào đường dẫn sau để kích hoạt tài khoản của bạn:\n" +
                    "http://localhost:8080/auth/verify-email?token=" + token);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Gửi mail kích hoạt thất bại: " + e.getMessage());
            System.out.println("Link kích hoạt dự phòng: http://localhost:8080/auth/verify-email?token=" + token);

            throw new IllegalStateException("Gửi email xác thực thất bại!", e);
        }
    }
}
