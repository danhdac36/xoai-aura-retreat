# Báo Cáo Phân Tích Luồng Đăng Nhập (Login Flow)
**Dự án:** Xoai Aura Retreat (Aura Moon)
**Phân hệ:** Authentication (UC1)
**Ngày phân tích:** 16/06/2026

Tuân thủ nguyên tắc dự án (Project Principles), báo cáo này mô tả chi tiết đường đi của luồng đăng nhập truyền thống (Username/Password), đi từ lúc người dùng thao tác trên giao diện (View) cho đến khi lấy được dữ liệu dưới CSDL (Model).

Hệ thống sử dụng **Spring Security**, do đó Controller truyền thống sẽ được thay thế một phần bởi các Bộ lọc bảo mật (Security Filters).

---

## 1. Tầng Giao Diện (View)
- **Tên file:** `login.html` (nằm trong `src/main/resources/templates/auth/login.html`)
- **Hành động:** Khi người dùng nhập Email và Mật khẩu, rồi bấm nút "Đăng nhập".
- **URL & Phương thức:** Trình duyệt sẽ gửi một HTTP Request với thông tin như sau:
  - **Method:** `POST`
  - **URL:** `/auth/login`
  - **Parameters:** `email` và `password` (phải đặt tên biến html (name) chính xác vì Spring Security sẽ đọc hai biến này).

---

## 2. Tầng Điều Hướng & Cấu Hình (Controller & Security Filter)
Thay vì nhảy vào `@PostMapping("/login")` trong `AuthController` như logic thông thường, Spring Security sẽ chặn luồng này lại ngay tại cửa.
- **Lớp xử lý:** `SecurityConfig.java` và `UsernamePasswordAuthenticationFilter` của Spring.
- **Luồng xử lý:** 
  1. Trong `SecurityConfig.java`, chúng ta đã định nghĩa:
     ```java
     .formLogin(form -> form
             .loginPage("/auth/login")
             .loginProcessingUrl("/auth/login") // <--- Chặn POST request tại đây
             .usernameParameter("email")
             .passwordParameter("password")
             .defaultSuccessUrl("/", true)
     )
     ```
  2. Filter của Spring Security sẽ bắt lấy request `POST /auth/login`, bóc tách `email` và `password` do View gửi lên.
  3. Nó sẽ tạo ra một vé thông hành tạm thời chưa xác thực (`UsernamePasswordAuthenticationToken`) và chuyển giao cho `AuthenticationManager` để kiểm tra.

> **Lưu ý:** Hàm `@PostMapping("/login")` bên trong `AuthController` hiện đang dư thừa vì Spring Security đã lấy quyền kiểm soát luồng `POST` này trước khi nó kịp đến Controller.

---

## 3. Tầng Xử Lý Nghiệp Vụ (Service)
Để xác thực được `email` người dùng vừa nhập có trong hệ thống hay không, Spring Security sẽ gọi đến một service chuyên biệt.
- **Lớp xử lý:** `UserService.java` (nằm trong `com.AuraMoon.auramoon.auth.service.impl`)
- **Interface triển khai:** `UserDetailsService` (chuẩn của Spring Security).
- **Method:** `loadUserByUsername(String email)`
- **Luồng xử lý chi tiết:**
  1. Nhận tham số `email` từ Spring Security.
  2. **Gọi sang Repository:** Gọi hàm `userRepository.findByEmail(email)`.
  3. **Kiểm tra nghiệp vụ:**
     - Nếu không tìm thấy: Ném lỗi `UsernameNotFoundException`.
     - Nếu tài khoản có trạng thái là `PENDING`: Ném lỗi báo chưa kích hoạt email.
  4. **Ánh xạ Quyền (Role):** Lấy Role từ thực thể User ra, đảm bảo nó có tiền tố `ROLE_` (ví dụ `ROLE_ADMIN`, `ROLE_GUEST`).
  5. **Trả về kết quả:** Đóng gói tất cả thành một đối tượng `UserDetails` tiêu chuẩn và trả về cho Spring Security.

*(Lúc này Spring Security sẽ tự động lấy cái `passwordHash` trong `UserDetails` đó đem so sánh với cái `password` thô mà người dùng nhập từ View bằng thuật toán mã hóa (VD: BCrypt). Nếu khớp => Đăng nhập thành công).*

---

## 4. Tầng Tương Tác Dữ Liệu (Repository)
Đây là cầu nối giữa Java và Database.
- **Interface:** `IUserRepository.java` (kế thừa `JpaRepository`)
- **Method được gọi:** `findByEmail(String email)`
- **Nhiệm vụ:** Hibernate/JPA sẽ tự động dịch method này thành câu lệnh SQL: `SELECT * FROM User WHERE email = ?` để tìm kiếm dữ liệu dưới CSDL SQL Server và map vào Model.

---

## 5. Tầng Dữ Liệu Thực Thể (Model/Entity)
- **Class:** `User.java` (nằm trong `com.AuraMoon.auramoon.auth.entity`)
- **Thuộc tính liên quan:** `email`, `passwordHash`, `status`, `role`.
- Đây là đích đến cuối cùng. Dữ liệu từ bảng `User` dưới database được đổ vào Class này và trả ngược về cho Repository -> Service.

---

### Tổng Kết Luồng (Sequence Flow)
`View (POST /auth/login)` ➔ `Security Filter (bắt email/pass)` ➔ `UserService.loadUserByUsername(email)` ➔ `IUserRepository.findByEmail(email)` ➔ `Model (Entity User)` ➔ `UserService (Trả về UserDetails)` ➔ `Security Filter (So sánh Pass -> Tạo Session)`.
