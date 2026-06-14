package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

<<<<<<< HEAD:05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/UserService.java
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
=======
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản ứng với email: " + email);
>>>>>>> NMNGocc:auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/UserService.java
        }

        if ("PENDING".equals(user.getStatus())) {
            throw new UsernameNotFoundException("Tài khoản chưa kích hoạt. Vui lòng kiểm tra email.");
        }

<<<<<<< HEAD:05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/UserService.java
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
=======
        String roleName = user.getRole().getRoleName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
>>>>>>> NMNGocc:auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/UserService.java
    }
}
