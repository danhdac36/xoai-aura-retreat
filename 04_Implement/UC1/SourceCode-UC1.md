# BÁO CÁO CHI TIẾT MÃ NGUỒN THI CÔNG - USE CASE 1 (AUTHENTICATION & SSO)
> **Tuân thủ Principle 2:** Toàn bộ code thi công được bọc trong file Markdown này để Project Owner (User) review trước khi áp dụng chính thức vào source gốc của dự án.

---

## 1. Cấu hình hệ thống (`application.properties`)
> **Yêu cầu:** Các mục cấu hình SMTP Email và Google OAuth2 Client được comment lại để User tự điền.

```properties
# ===================================================================
# EMAIL SMTP CONFIGURATION (Chờ cấu hình chi tiết)
# ===================================================================
# spring.mail.host=smtp.gmail.com
# spring.mail.port=587
# spring.mail.username=dien-email-cua-ban@gmail.com
# spring.mail.password=dien-mat-khau-ung-dung-cua-ban
# spring.mail.properties.mail.smtp.auth=true
# spring.mail.properties.mail.smtp.starttls.enable=true

# ===================================================================
# GOOGLE OAUTH2 SSO CONFIGURATION (Chờ cấu hình chi tiết)
# ===================================================================
# spring.security.oauth2.client.registration.google.client-id=DIEN_GOOGLE_CLIENT_ID_CUA_BAN
# spring.security.oauth2.client.registration.google.client-secret=DIEN_GOOGLE_CLIENT_SECRET_CUA_BAN
# spring.security.oauth2.client.registration.google.scope=profile,email
# spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
```

---

## 2. Mã nguồn Java (Backend)

### 2.1. JPA Attribute Converter (`AesDataEncryptor.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.config`
*   **Mục đích:** Tự động mã hóa/giải mã cột `Identify_code` (CCCD) bằng AES-128 khi lưu xuống/đọc lên từ database.
*   **Lưu ý:** Độ dài cột `Identify_code` trong database mặc định là `VARCHAR(20)`. Để chứa được chuỗi Base64 sau khi mã hóa, cần chạy lệnh SQL sau trong DB:
    ```sql
    ALTER TABLE [USER] ALTER COLUMN Identify_code VARCHAR(255);
    ```

```java
package com.AuraMoon.auramoon.auth.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class AesDataEncryptor implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "AuraMoonEncrypt!"; // Đúng 16 ký tự cho khóa AES-128

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Lỗi mã hóa thông tin cá nhân nhạy cảm", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new IllegalStateException("Lỗi giải mã thông tin cá nhân nhạy cảm", e);
        }
    }
}
```

### 2.2. Entity User sửa đổi (`User.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.entity`
*   **Nội dung sửa đổi:** Bổ sung `@Convert` cho `identifyCode` và thêm thuộc tính `verifyToken`.

```java
package com.AuraMoon.auramoon.auth.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "[USER]")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "updatedAt", column = @Column(name = "last_update"))
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Column(name = "gender", length = 6)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 20)
    private String phone;

    @Convert(converter = AesDataEncryptor.class)
    @Column(name = "Identify_code", length = 255) // Đổi sang 255 để tránh tràn bộ đệm mã hóa
    private String identifyCode;

    @Lob
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "verify_token", length = 255)
    private String verifyToken;
}
```

### 2.3. Repository User (`IUserRepository.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.repository`
*   **Nội dung sửa đổi:** Đổi ID type từ `Long` thành `Integer` để khớp với `User.id` và bổ sung method `findByVerifyToken`.

```java
package com.AuraMoon.auramoon.auth.repository;

import com.AuraMoon.auramoon.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User findByVerifyToken(String verifyToken);
}
```

### 2.4. DTO Đăng ký (`UserRegistrationDto.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.dto`

```java
package com.AuraMoon.auramoon.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 50, message = "Họ và tên không được vượt quá 50 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 30, message = "Mật khẩu phải từ 6 đến 30 ký tự")
    private String password;

    @NotBlank(message = "Mật khẩu xác nhận không được để trống")
    private String confirmPassword;
}
```

### 2.5. Service Interfaces & Implementations
#### 2.5.1. `IAuthService.java`
*   **Package:** `com.AuraMoon.auramoon.auth.service`

```java
package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.entity.User;

public interface IAuthService {
    void register(UserRegistrationDto registrationDto);
    boolean verifyEmail(String token);
    User findByEmail(String email);
    User createGoogleUser(String email, String fullName);
}
```

#### 2.5.2. `AuthServiceImpl.java`
*   **Package:** `com.AuraMoon.auramoon.auth.service.impl`

```java
package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.JavaMailSender;
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
        user.setStatus("PENDING");
        
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
            return user;
        }

        Role guestRole = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new IllegalStateException("Không cấu hình được vai trò GUEST mặc định"));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString())); // Mật khẩu ngẫu nhiên cho SSO
        newUser.setRole(guestRole);
        newUser.setStatus("ACTIVE"); // Auto-active khi login SSO Google
        newUser.setVerifyToken(null);

        return userRepository.save(newUser);
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
        }
    }
}
```

#### 2.5.3. CustomUserDetailsService (`CustomUserDetailsService.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.service.impl`
*   **Mục đích:** Load thông tin User để Spring Security xác thực Form Login từ Database.

```java
package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản ứng với email: " + email);
        }

        if ("PENDING".equals(user.getStatus())) {
            throw new UsernameNotFoundException("Tài khoản chưa kích hoạt. Vui lòng kiểm tra email.");
        }

        String roleName = user.getRole().getRoleName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
```

### 2.6. Security Configuration (`SecurityConfig.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.config`
*   **Nội dung sửa đổi:** Bổ sung `@Configuration`, `@EnableWebSecurity`, `@EnableJpaAuditing` để tự động hóa audit time, tích hợp OAuth2 Login và UserDetailsService.

```java
package com.AuraMoon.auramoon.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableJpaAuditing // Cần thiết để kích hoạt JPA Auditing cho BaseEntity
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/home",
            "/about-us",
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/login-admin",
            "/auth/verify-email",
            "/fnb/uc19-alacarte-order",
            "/packages",
            "/css/**",
            "/js/**",
            "/images/**"
    };

    private static final String[] GUEST_ENDPOINTS = {
            "/user/**",
            "/orders/**",
            "/cart/**",
            "/profile/**"
    };

    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/dashboard/**",
            "/manage/**"
    };

    private static final String[] RECEPTIONIST_ENDPOINTS = {
            "/receptionist/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(GUEST_ENDPOINTS).hasRole("GUEST")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(RECEPTIONIST_ENDPOINTS).hasRole("RECEPTIONIST")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email") // Định danh username là email
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .successHandler(oAuth2LoginSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(sm -> sm
                        .maximumSessions(2))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/register", "/auth/forgot-password", "/auth/verify-email"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 2.7. Google OAuth2 Success Handler (`OAuth2LoginSuccessHandler.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.config`

```java
package com.AuraMoon.auramoon.auth.config;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private IAuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email != null) {
            authService.createGoogleUser(email, name != null ? name : "Google User");
        }

        setDefaultTargetUrl("/home");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

### 2.8. Auth Controller (`AuthController.java`)
*   **Package:** `com.AuraMoon.auramoon.auth.controller`

```java
package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new UserRegistrationDto());
        return "auth/registration"; // Khớp với registration.html trong folder templates
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp!");
        }

        if (result.hasErrors()) {
            return "auth/registration";
        }

        try {
            authService.register(registrationDto);
            model.addAttribute("successMessage", "Kích hoạt link xác nhận đã được gửi vào hòm thư của bạn. Vui lòng kiểm tra email.");
            return "auth/register-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/registration";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng liên hệ Admin.");
            return "auth/registration";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean isVerified = authService.verifyEmail(token);
        if (isVerified) {
            model.addAttribute("status", "success");
            model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt thành công! Giờ đây bạn có thể đăng nhập.");
        } else {
            model.addAttribute("status", "error");
            model.addAttribute("message", "Link xác nhận không hợp lệ hoặc đã hết hạn.");
        }
        return "auth/verify-result";
    }
}
```

---

## 3. Giao diện & Tài nguyên Tĩnh (UI/UX - Thymeleaf)

### 3.1. Trang đăng nhập sửa đổi (`login.html`)
*   **Đường dẫn:** `src/main/resources/templates/auth/login.html`
*   **Mục đích:** Tách mã CSS, JS và cập nhật action của form login sang `/auth/login`.

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Aura Moon Retreat - Đăng nhập</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&amp;family=Inter:wght@400;500;600&amp;display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
          rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/auth/login.css}">
    <script th:src="@{/js/auth/login.js}" defer></script>
</head>
<body class="bg-surface font-body-md text-on-surface antialiased overflow-hidden">
<main class="flex min-h-screen">
    <!-- Left Section: Visual Brand Identity -->
    <section class="hidden lg:block lg:w-3/5 relative overflow-hidden">
        <div class="absolute inset-0 z-10 bg-black/10"></div>
        <img alt="Luxury infinity pool at sunrise overlooking misty mountains and rice terraces"
             class="absolute inset-0 w-full h-full object-cover transform scale-105 transition-transform duration-[10000ms] ease-out hover:scale-100"
             src="https://lh3.googleusercontent.com/aida/ADBb0ujC1JqPnqkD3WvY6dkRhH_4HardNKXM59i_-LanZtKuN6k2lYdmgdyutA0atHl4WDHrvlN_44xLo13QA9gV9XDwVo4xEaGh_sg681-ApFG-4koqCZY32ypVUWZ5lWW2RQ9_wRy64LAFlBGd5nVNkpEqpl_W4RRkWSLrK2LuoULx5tFOzEtmgRl6CAeB_CbpDk2S48_lQnNJ1kIoW-uhUG-zv70QdgtAuE8aNHCizzPf-kaioH9oKQyIxkY">
        <div class="absolute inset-0 z-20 flex flex-col justify-between p-margin-desktop">
            <div class="flex items-center gap-stack-sm">
                <span class="font-display-lg text-headline-sm text-surface-lowest tracking-tight">Aura Moon</span>
            </div>
            <div class="max-w-xl">
                <p class="font-display-lg text-headline-lg text-surface-lowest leading-snug">
                    Nơi tâm hồn tìm thấy <br>sự tĩnh lặng tuyệt đối.
                </p>
                <div class="w-24 h-[1px] bg-surface-lowest/40 mt-stack-lg"></div>
            </div>
        </div>
    </section>
    <!-- Right Section: Login Form -->
    <section class="w-full lg:w-2/5 flex flex-col justify-center items-center px-margin-mobile md:px-margin-desktop bg-surface relative">
        <div class="lg:hidden absolute top-8 left-8">
            <span class="font-display-lg text-headline-sm text-primary tracking-tight">Xoài Aura</span>
        </div>
        <div class="w-full max-w-md">
            <div class="mb-section-gap/2">
                <h1 class="font-headline-lg text-headline-lg text-on-surface mb-stack-sm">Chào mừng trở lại</h1>
                <p class="font-body-md text-on-surface-variant">Đăng nhập để tiếp tục hành trình tỉnh thức của bạn.</p>
            </div>

            <!-- Login Form -->
            <form class="space-y-stack-lg" method="post" th:action="@{/auth/login}">
                <!-- Email Field -->
                <div class="space-y-stack-sm">
                    <label class="font-label-md text-label-md text-on-surface-variant uppercase" for="email">Email</label>
                    <input class="w-full bg-surface-container-low border-b-2 border-outline-variant focus:border-secondary transition-colors px-0 py-3 input-minimal font-body-md text-on-surface placeholder-outline-variant"
                           id="email" name="email" placeholder="email@example.com" required type="email">
                </div>
                <!-- Password Field -->
                <div class="space-y-stack-sm">
                    <div class="flex justify-between items-center">
                        <label class="font-label-md text-label-md text-on-surface-variant uppercase" for="password">Mật khẩu</label>
                        <a class="font-label-md text-label-md text-secondary hover:underline underline-offset-4 transition-all" href="/auth/forgot-password">Quên mật khẩu?</a>
                    </div>
                    <input class="w-full bg-surface-container-low border-b-2 border-outline-variant focus:border-secondary transition-colors px-0 py-3 input-minimal font-body-md text-on-surface placeholder-outline-variant"
                           id="password" name="password" placeholder="••••••••" required type="password">
                </div>
                <!-- Main Login Button -->
                <button class="w-full bg-secondary text-white py-4 font-label-md text-label-md rounded-lg shadow-sm hover:opacity-90 active:scale-[0.98] transition-all flex justify-center items-center gap-stack-sm" type="submit">
                    Đăng nhập
                    <span class="material-symbols-outlined text-[20px]">arrow_forward</span>
                </button>
            </form>

            <!-- Notification error / logout -->
            <div class="mt-4">
                <p class="text-red-500 text-sm" th:if="${param.error}">Email hoặc Mật khẩu chưa đúng, hoặc tài khoản chưa kích hoạt.</p>
                <p class="text-green-600 text-sm" th:if="${param.logout}">Bạn đã đăng xuất thành công.</p>
            </div>

            <!-- Divider -->
            <div class="relative flex items-center my-stack-lg">
                <div class="flex-grow border-t border-outline-variant/30"></div>
                <span class="flex-shrink mx-4 font-label-md text-label-md text-on-surface-variant/60 uppercase">Hoặc</span>
                <div class="flex-grow border-t border-outline-variant/30"></div>
            </div>

            <!-- SSO Buttons -->
            <div class="grid grid-cols-1 gap-gutter">
                <a th:href="@{/oauth2/authorization/google}" class="flex items-center justify-center gap-stack-sm py-3 border border-outline-variant/40 rounded-lg hover:bg-surface-container-low transition-colors">
                    <svg class="w-5 h-5" viewBox="0 0 24 24">
                        <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"></path>
                        <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"></path>
                        <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"></path>
                        <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"></path>
                    </svg>
                    <span class="font-label-md text-label-md text-on-surface-variant">Đăng nhập bằng Google</span>
                </a>
            </div>

            <!-- Footer Sign up link -->
            <div class="mt-8 text-center">
                <p class="font-body-md text-on-surface-variant">
                    Chưa có tài khoản?
                    <a class="text-secondary font-label-md text-label-md hover:underline underline-offset-4 ml-1" th:href="@{/auth/register}">Đăng ký ngay</a>
                </p>
            </div>
        </div>
        <!-- Footer Small -->
        <div class="absolute bottom-8 w-full text-center">
            <p class="text-[12px] text-on-surface-variant/50 uppercase tracking-widest font-label-md">© 2024 Xoài Aura Retreat</p>
        </div>
    </section>
</main>
</body>
</html>
```

### 3.2. Trang đăng ký sửa đổi (`registration.html`)
*   **Đường dẫn:** `src/main/resources/templates/auth/registration.html`
*   **Mục đích:** Tách CSS, JS, cập nhật form mapping và tích hợp thông điệp lỗi (validation) từ DTO.

```html
<!DOCTYPE html>
<html class="light" lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Xoai Aura - Đăng ký</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&amp;family=Playfair+Display:wght@400;500;700&amp;display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap"
          rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/auth/registration.css}">
    <script th:src="@{/js/auth/registration.js}" defer></script>
</head>
<body class="bg-background text-on-background font-body-md overflow-hidden">
<main class="split-screen-container">
    <!-- Left Side: Brand Imagery -->
    <section class="brand-panel">
        <div class="absolute inset-0 z-10 bg-on-background/10"></div>
        <img alt="Luxury wellness retreat scene" class="w-full h-full object-cover" src="https://lh3.googleusercontent.com/aida-public/AB6AXuDEOZN_X4oRbPTtTYWiC6c3X9b9FlfssRtrtZbV8pVNGc2rWtO0QuYxSsn3j1grqaKM0-sEkrA344VzH_owDxvp4LkHIor8VpwIXiLj7WY1tDCwz31g2HejNCdZ2-yG0oz6_id87vEAJ1tqolKYkEZpzSbbmCgs2E4n0pMb3DzalwKjBNdpDErbgkiigfGgYbP5N4Y31qUpN8zZ3DBhhyfVNZfeWDfIiGJ5TXSdBZKRhXP73EMruxzrbrF0IDQNsU5Uff0wDY_aJv0">
        <div class="absolute top-10 left-10 z-20">
            <span class="font-headline-sm text-headline-sm text-surface-lowest tracking-tight">Xoai Aura</span>
        </div>
        <div class="absolute bottom-20 left-10 z-20 max-w-md">
            <p class="font-headline-sm text-headline-sm text-surface-lowest mb-4 leading-relaxed">
                "Sự tĩnh lặng là khởi đầu của mọi trí tuệ."
            </p>
            <div class="h-1 w-12 bg-secondary-fixed"></div>
        </div>
    </section>

    <!-- Right Side: Registration Form -->
    <section class="form-panel bg-surface-bright">
        <div class="w-full max-w-[440px]">
            <!-- Header -->
            <div class="mb-6 text-center md:text-left">
                <h1 class="font-headline-lg text-headline-lg text-primary mb-2 text-center">Đăng ký</h1>
                <p class="font-body-md text-on-surface-variant text-center">
                    Tham gia cộng đồng Xoai Aura để trải nghiệm sự cân bằng tuyệt đối.
                </p>
            </div>

            <!-- Validation Error Messages -->
            <div th:if="${error}" class="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm" th:text="${error}"></div>

            <!-- Registration Form -->
            <form class="space-y-4" th:action="@{/auth/register}" method="post" th:object="${registerDto}">
                <!-- Full Name -->
                <div class="flex flex-col gap-1">
                    <label class="font-label-md text-xs text-on-surface-variant uppercase tracking-widest" for="fullName">Họ và tên</label>
                    <input class="luxury-input py-2 px-1 text-body-md text-on-surface" id="fullName" placeholder="Nguyễn Văn A" type="text" th:field="*{fullName}">
                    <span class="text-red-500 text-xs mt-1" th:if="${#fields.hasErrors('fullName')}" th:errors="*{fullName}"></span>
                </div>
                <!-- Email -->
                <div class="flex flex-col gap-1">
                    <label class="font-label-md text-xs text-on-surface-variant uppercase tracking-widest" for="email">Email</label>
                    <input class="luxury-input py-2 px-1 text-body-md text-on-surface" id="email" placeholder="example@xoai-aura.com" type="email" th:field="*{email}">
                    <span class="text-red-500 text-xs mt-1" th:if="${#fields.hasErrors('email')}" th:errors="*{email}"></span>
                </div>
                <!-- Password -->
                <div class="flex flex-col gap-1 relative">
                    <label class="font-label-md text-xs text-on-surface-variant uppercase tracking-widest" for="password">Mật khẩu</label>
                    <div class="relative">
                        <input class="luxury-input w-full py-2 px-1 text-body-md text-on-surface pr-10" id="password" placeholder="••••••••" type="password" th:field="*{password}">
                        <button class="absolute right-1 bottom-3 text-on-surface-variant" type="button" id="togglePassword">
                            <span class="material-symbols-outlined text-[20px]">visibility_off</span>
                        </button>
                    </div>
                    <span class="text-red-500 text-xs mt-1" th:if="${#fields.hasErrors('password')}" th:errors="*{password}"></span>
                </div>
                <!-- Confirm Password -->
                <div class="flex flex-col gap-1 relative">
                    <label class="font-label-md text-xs text-on-surface-variant uppercase tracking-widest" for="confirmPassword">Xác nhận mật khẩu</label>
                    <input class="luxury-input w-full py-2 px-1 text-body-md text-on-surface" id="confirmPassword" placeholder="••••••••" type="password" th:field="*{confirmPassword}">
                    <span class="text-red-500 text-xs mt-1" th:if="${#fields.hasErrors('confirmPassword')}" th:errors="*{confirmPassword}"></span>
                </div>
                <!-- Submit Button -->
                <div class="pt-2">
                    <button class="w-full bg-secondary text-white font-label-md text-sm uppercase tracking-[0.2em] py-4 rounded-lg hover:opacity-90 transition-all shadow-sm active:scale-[0.98]" type="submit">
                        Đăng ký
                    </button>
                </div>
            </form>
            <!-- Navigation Back to Login -->
            <div class="mt-6 text-center">
                <p class="font-body-md text-on-surface-variant">
                    Đã có tài khoản?
                    <a class="text-secondary font-semibold underline underline-offset-4 ml-1 hover:text-on-secondary-container transition-colors" th:href="@{/auth/login}">
                        Đăng nhập ngay
                    </a>
                </p>
            </div>
            <!-- Footer Links -->
            <div class="mt-4 pt-6 border-t border-outline-variant/30 flex justify-center gap-6">
                <a class="font-label-md text-[10px] text-on-surface-variant/60 uppercase tracking-widest hover:text-on-surface transition-colors" href="#">Điều khoản</a>
                <a class="font-label-md text-[10px] text-on-surface-variant/60 uppercase tracking-widest hover:text-on-surface transition-colors" href="#">Bảo mật</a>
            </div>
        </div>
    </section>
</main>
</body>
</html>
```

### 3.3. Trang Đăng ký thành công (`register-success.html`)
*   **Đường dẫn:** `src/main/resources/templates/auth/register-success.html`

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Đăng ký thành công - Aura Moon</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&amp;family=Inter:wght@400;500;600&amp;display=swap" rel="stylesheet">
    <script id="tailwind-config">
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: "#5f5e5b",
                        secondary: "#566342",
                        background: "#fbf9f8",
                        surface: "#ffffff"
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-background font-sans min-h-screen flex items-center justify-center p-6">
    <div class="max-w-md w-full bg-surface p-8 rounded-xl shadow-md border border-neutral-200 text-center">
        <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
        </div>
        <h2 class="font-serif text-3xl text-secondary mb-4">Đăng ký thành công!</h2>
        <p class="text-neutral-600 mb-6 text-sm" th:text="${successMessage}"></p>
        <a th:href="@{/auth/login}" class="inline-block w-full bg-secondary text-white px-6 py-3 rounded-lg font-medium hover:opacity-90 transition-all text-sm">
            Quay lại đăng nhập
        </a>
    </div>
</body>
</html>
```

### 3.4. Trang kết quả xác thực (`verify-result.html`)
*   **Đường dẫn:** `src/main/resources/templates/auth/verify-result.html`

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Kết quả xác thực tài khoản - Aura Moon</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&amp;family=Inter:wght@400;500;600&amp;display=swap" rel="stylesheet">
    <script id="tailwind-config">
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: "#5f5e5b",
                        secondary: "#566342",
                        background: "#fbf9f8",
                        surface: "#ffffff",
                        error: "#ba1a1a"
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-background font-sans min-h-screen flex items-center justify-center p-6">
    <div class="max-w-md w-full bg-surface p-8 rounded-xl shadow-md border border-neutral-200 text-center">
        <!-- Success Icon -->
        <div th:if="${status == 'success'}" class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
        </div>
        <!-- Error Icon -->
        <div th:if="${status == 'error'}" class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-error" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
        </div>

        <h2 class="font-serif text-3xl mb-4" 
            th:classappend="${status == 'success'} ? 'text-secondary' : 'text-error'" 
            th:text="${status == 'success'} ? 'Kích hoạt thành công!' : 'Kích hoạt thất bại'"></h2>
            
        <p class="text-neutral-600 mb-6 text-sm" th:text="${message}"></p>
        <a th:href="@{/auth/login}" class="inline-block w-full bg-secondary text-white px-6 py-3 rounded-lg font-medium hover:opacity-90 transition-all text-sm">
            Quay lại trang đăng nhập
        </a>
    </div>
</body>
</html>
```

---

## 4. Tài nguyên tĩnh biệt lập (Static Assets - JS & CSS)
> **Tuân thủ Principle 4:** Không nhúng CSS, JS nội tuyến. Dưới đây là các tệp tĩnh được đưa vào thư mục `static/` phân bổ theo đúng module `auth`.

### 4.1. CSS Trang Đăng nhập (`login.css`)
*   **Đường dẫn:** `src/main/resources/static/css/auth/login.css`

```css
.material-symbols-outlined {
    font-variation-settings: 'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24;
}

.input-minimal:focus {
    outline: none;
    box-shadow: none;
}

.glass-effect {
    background: rgba(251, 249, 248, 0.05);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(251, 249, 248, 0.1);
}
```

### 4.2. JS Trang Đăng nhập (`login.js`)
*   **Đường dẫn:** `src/main/resources/static/js/auth/login.js`

```javascript
document.addEventListener('DOMContentLoaded', () => {
    // Subtle micro-interaction for the inputs
    document.querySelectorAll('input').forEach(input => {
        input.addEventListener('focus', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.style.color = '#566342'; // secondary color (sage green)
            }
        });
        input.addEventListener('blur', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.style.color = '';
            }
        });
    });

    // Background parallax effect
    document.addEventListener('mousemove', (e) => {
        const img = document.querySelector('img');
        if (img) {
            const x = (window.innerWidth - e.pageX * 2) / 100;
            const y = (window.innerHeight - e.pageY * 2) / 100;
            img.style.transform = `scale(1.05) translate(${x}px, ${y}px)`;
        }
    });
});
```

### 4.3. CSS Trang Đăng ký (`registration.css`)
*   **Đường dẫn:** `src/main/resources/static/css/auth/registration.css`

```css
.material-symbols-outlined {
    font-variation-settings: 'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24;
}

.luxury-input {
    background-color: #fbf9f8; /* sand-light */
    border-top: none;
    border-left: none;
    border-right: none;
    border-bottom: 2px solid #e4e2e1;
    transition: border-color 0.3s ease;
}

.luxury-input:focus {
    outline: none;
    box-shadow: none;
    border-color: #566342; /* sage-green/secondary */
}

.split-screen-container {
    height: 100vh;
    display: flex;
}

.brand-panel {
    width: 50%;
    height: 100%;
    overflow: hidden;
    position: relative;
}

.form-panel {
    width: 50%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0 80px;
}

@media (max-width: 1024px) {
    .brand-panel {
        display: none;
    }

    .form-panel {
        width: 100%;
        padding: 0 40px;
    }
}
```

### 4.4. JS Trang Đăng ký (`registration.js`)
*   **Đường dẫn:** `src/main/resources/static/js/auth/registration.js`

```javascript
document.addEventListener('DOMContentLoaded', () => {
    // Micro-interactions for form inputs
    document.querySelectorAll('.luxury-input').forEach(input => {
        input.addEventListener('focus', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.classList.add('text-secondary');
            }
        });
        input.addEventListener('blur', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.classList.remove('text-secondary');
            }
        });
    });

    // Password visibility toggle logic
    const toggleBtn = document.getElementById('togglePassword');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            const passwordInput = document.getElementById('password');
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            const icon = toggleBtn.querySelector('.material-symbols-outlined');
            if (icon) {
                icon.textContent = type === 'password' ? 'visibility_off' : 'visibility';
            }
        });
    }
});
```
