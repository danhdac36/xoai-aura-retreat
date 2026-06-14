# Ví dụ Cấu hình Điều hướng sau Đăng nhập (Post-Login Redirection)

Tài liệu này cung cấp 2 kịch bản cấu hình điều hướng người dùng sau khi đăng nhập thành công bằng Spring Security, thay thế cho cách dùng `HomeController` điều phối thủ công.

---

## Kịch bản A: Hệ thống dùng chung 1 trang đích cho tất cả người dùng
Trong kịch bản này, sau khi người dùng (bất kể role là gì) đăng nhập thành công, họ đều được đưa về một trang chung (ví dụ: trang chủ dự án `/home`).

### 1. Thay đổi trong `SecurityConfig.java`
Bạn chỉ cần thay đổi phương thức `.defaultSuccessUrl("/", true)` thành URL đích mà team mong muốn. Tham số `true` ép buộc hệ thống luôn chuyển hướng về URL này.

```java
// Trong lớp SecurityConfig.java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // ... các cấu hình authorizeHttpRequests ...
        .formLogin(form -> form
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            .usernameParameter("email")
            .passwordParameter("password")
            // SỬA DÒNG DƯỚI ĐÂY: Trỏ về trang mong muốn (ví dụ /home)
            .defaultSuccessUrl("/home", true) 
            .failureUrl("/auth/login?error=true")
            .permitAll())
        // ... các cấu hình khác
    return http.build();
}
```

### 2. Thay đổi trong Controller
Bạn có thể **xóa bỏ** phương thức `index` trong `HomeController.java` hoặc xóa luôn class `HomeController` nếu nó không còn mục đích nào khác.

---

## Kịch bản B: Rẽ nhánh trang đích theo Role (Chuẩn Spring Security)
Trong kịch bản này, hệ thống sẽ kiểm tra quyền (Role) của người dùng ngay khi đăng nhập thành công và tự động chuyển hướng họ về đúng Dashboard tương ứng, loại bỏ sự phụ thuộc vào `HomeController`.

### 1. Tạo class `CustomAuthenticationSuccessHandler`
Tạo một file mới tên là `CustomAuthenticationSuccessHandler.java` trong package `config` (hoặc `security`).

```java
package com.AuraMoon.auramoon.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = "/"; // Mặc định nếu không khớp quyền nào

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                targetUrl = "/admin/home";
                break;
            } else if ("ROLE_GUEST".equals(role)) {
                targetUrl = "/profile/home";
                break;
            } else if ("ROLE_RECEPTIONIST".equals(role)) {
                targetUrl = "/receptionist/home";
                break;
            } else if ("ROLE_THERAPIST".equals(role)) {
                targetUrl = "/therapist/home";
                break;
            } else if ("ROLE_CHEFF".equals(role)) {
                targetUrl = "/F&B/home";
                break;
            } else if ("ROLE_MANAGER".equals(role)) {
                targetUrl = "/management/home";
                break;
            }
        }

        // Thực hiện chuyển hướng
        response.sendRedirect(targetUrl);
    }
}
```

### 2. Thay đổi trong `SecurityConfig.java`
Tiêm (Inject) class Handler vừa tạo vào `SecurityConfig` và thay thế `.defaultSuccessUrl()` bằng `.successHandler()`.

```java
package com.AuraMoon.auramoon.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
// ... các import khác

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Nhúng Handler phân luồng
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ... các cấu hình authorizeHttpRequests ...
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                // SỬ DỤNG SUCCESS HANDLER MỚI ĐỂ RẼ NHÁNH
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/auth/login?error=true")
                .permitAll())
            // ... cấu hình oauth2Login và các thứ khác ...
        return http.build();
    }
}
```

### 3. Thay đổi trong `OAuth2LoginSuccessHandler.java` (Dành cho đăng nhập Google SSO)
Vì đăng nhập Google xử lý bằng Handler riêng (`OAuth2LoginSuccessHandler`), nên để luồng Google cũng được phân nhánh tương tự, bạn thay thế lệnh `response.sendRedirect("/")` ở cuối file `OAuth2LoginSuccessHandler.java` bằng việc gọi đến logic của `CustomAuthenticationSuccessHandler`.

```java
// Trong hàm onAuthenticationSuccess của OAuth2LoginSuccessHandler.java:

// Cuối hàm, thay vì: response.sendRedirect("/");
// Bạn sửa thành:
customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, newAuth);
```
*(Lưu ý: Bạn sẽ cần `@Autowired CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler` vào trong class `OAuth2LoginSuccessHandler`)*

### Lợi ích của Kịch bản B:
- Tách bạch rõ ràng logic bảo mật khỏi Controller thông thường.
- `HomeController` sẽ không còn gánh việc điều phối không thuộc phận sự của mình.
- Dễ dàng bảo trì và bổ sung Role mới sau này.
