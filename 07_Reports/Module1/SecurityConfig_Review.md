# Code Review: `SecurityConfig.java`
**Đường dẫn file:** `05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/config/SecurityConfig.java`

File `SecurityConfig.java` là trái tim của hệ thống bảo mật trong dự án Spring Boot, chịu trách nhiệm quản lý xác thực (Authentication) và phân quyền (Authorization) cho toàn bộ ứng dụng Auramoon.

Dưới đây là phần phân tích chi tiết ý nghĩa của từng dòng code, cấu trúc và phương thức trong file:

## 1. Class Annotations (Đánh dấu cấu hình)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig { ... }
```
- **`@Configuration`**: Đánh dấu class này là một lớp cấu hình của Spring, cho phép Spring Boot quét và khởi tạo các `@Bean` được định nghĩa bên trong để đưa vào Application Context.
- **`@EnableWebSecurity`**: Kích hoạt tính năng bảo mật web của Spring Security, ghi đè lên các cấu hình bảo mật mặc định để áp dụng các thiết lập tùy chỉnh của dự án.

## 2. Tiêm phụ thuộc (Dependency Injection)
```java
@Autowired
private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
```
- Dùng để tiêm (inject) Bean `OAuth2LoginSuccessHandler`. Handler này chứa logic tùy chỉnh sẽ được thực thi ngay sau khi người dùng đăng nhập thành công bằng Google (OAuth2). Ví dụ: kiểm tra xem email đã tồn tại trong database chưa, tự động tạo tài khoản hoặc chuyển hướng người dùng tương ứng với Role của họ.

## 3. Định nghĩa các mảng phân quyền (Role-based Endpoints)
```java
private static final String[] PUBLIC_ENDPOINTS = { "/", "/home", "/auth/login", ... };
private static final String[] GUEST_ENDPOINTS = { "/user/**", "/orders/**", ... };
// Tương tự cho ADMIN_ENDPOINTS, RECEPTIONIST_ENDPOINTS, THERAPIST_ENDPOINTS, v.v.
```
- Khai báo các hằng số mảng String chứa danh sách các đường dẫn (URL pattern) được gán cho từng nhóm quyền cụ thể. 
- Việc tách riêng như thế này giúp code gọn gàng, cực kỳ dễ đọc, dễ bảo trì và dễ dàng mở rộng khi hệ thống phình to. Các route tĩnh như `/css/**`, `/js/**` cũng được đưa vào `PUBLIC_ENDPOINTS` để trình duyệt có thể load giao diện mà không cần đăng nhập.

## 4. Phương thức `securityFilterChain(HttpSecurity http)`
Đây là phương thức quan trọng nhất, định nghĩa "chuỗi bộ lọc bảo mật" (Security Filter Chain) quy định cách các request HTTP được xử lý.

### 4.1. Phân quyền Request (Authorization)
```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
    .requestMatchers(GUEST_ENDPOINTS).hasRole("GUEST")
    ...
    .anyRequest().authenticated())
```
- **`requestMatchers(PUBLIC_ENDPOINTS).permitAll()`**: Ai cũng có quyền truy cập các URL này (kể cả khách chưa đăng nhập).
- **`hasRole("GUEST")`, `hasRole("ADMIN")`,...**: Chỉ những tài khoản đăng nhập có Role tương ứng mới được phép truy cập vào cụm URL đó.
- **`anyRequest().authenticated()`**: Bất kỳ request nào không nằm trong các danh sách trên đều bắt buộc phải đăng nhập (bọc lót bảo mật).

### 4.2. Cấu hình Đăng nhập bằng Form (Form Login)
```java
.formLogin(form -> form
    .loginPage("/auth/login")
    .loginProcessingUrl("/auth/login")
    .usernameParameter("email")
    .passwordParameter("password")
    .defaultSuccessUrl("/", true)
    .failureUrl("/auth/login?error=true")
    .permitAll())
```
- **`loginPage`**: Chỉ định đường dẫn tới trang giao diện đăng nhập (Thymeleaf view). Khi người dùng chưa đăng nhập mà truy cập trang nội bộ, họ sẽ bị redirect về đây.
- **`loginProcessingUrl`**: Chỉ định endpoint mà form HTML sẽ `POST` dữ liệu lên. Spring Security sẽ tự động chặn URL này để xử lý logic xác thực mà không cần ta phải tự viết Controller.
- **`usernameParameter` / `passwordParameter`**: Định nghĩa tên của thẻ `<input name="...">` trong form HTML (thay vì mặc định là `username`, dự án dùng `email`).
- **`defaultSuccessUrl` / `failureUrl`**: Nơi chuyển hướng sau khi đăng nhập thành công hoặc thất bại.

### 4.3. Cấu hình Đăng nhập bằng Google (OAuth2)
```java
.oauth2Login(oauth2 -> oauth2
    .loginPage("/auth/login")
    .successHandler(oAuth2LoginSuccessHandler))
```
- Tích hợp tính năng đăng nhập Google. Nó tái sử dụng chung trang giao diện đăng nhập `/auth/login`. Khi đăng nhập Google thành công, luồng xử lý sẽ được ném sang `oAuth2LoginSuccessHandler` thay vì dùng luồng mặc định.

### 4.4. Cấu hình Đăng xuất (Logout)
```java
.logout(logout -> logout
    .logoutUrl("/auth/logout")
    .logoutSuccessUrl("/auth/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll())
```
- Xử lý khi user gửi request tới `/auth/logout`. Nó sẽ tự động hủy phiên làm việc (Session) của user (`invalidateHttpSession`), xóa cookie định danh `JSESSIONID` trên trình duyệt và đẩy về trang login với param `logout=true`.

### 4.5. Quản lý Session và CSRF
```java
.sessionManagement(sm -> sm.maximumSessions(2))
.csrf(csrf -> csrf.ignoringRequestMatchers("/auth/register", "/auth/forgot-password", "/auth/verify-email"));
```
- **`maximumSessions(2)`**: Giới hạn một user chỉ được đăng nhập tối đa trên 2 thiết bị/trình duyệt cùng lúc. Đăng nhập ở thiết bị thứ 3 có thể làm thiết bị đầu tiên bị kick ra.
- **`csrf`**: Tắt bảo vệ CSRF (Cross-Site Request Forgery) cho một số endpoint cụ thể như Đăng ký, Quên mật khẩu. Điều này thường để tránh lỗi khi user gửi request POST từ các client không hỗ trợ lấy CSRF token.

## 5. Phương thức `authenticationManager(...)`
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```
- **Mục đích**: Expose `AuthenticationManager` thành một Bean để có thể tái sử dụng ở các class khác (Ví dụ tiêm vào Controller hoặc Service).
- Dùng trong các kịch bản cần **xác thực thủ công** (manual authentication). Ví dụ: Sau khi người dùng đăng ký tài khoản thành công, bạn muốn tự động đăng nhập cho họ luôn thay vì bắt họ ra nhập lại mật khẩu, bạn sẽ gọi Bean này.

---
**Nhận xét chung:** File được tổ chức cực kỳ gọn gàng, dễ hiểu và tuân thủ chặt chẽ nguyên tắc khai báo tập trung của Spring Boot 3. Việc tách `Constant String[]` làm cho việc quản lý Role-based access logic trở nên rất sáng sủa.
