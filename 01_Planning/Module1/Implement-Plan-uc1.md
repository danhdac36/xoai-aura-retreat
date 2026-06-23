# KẾ HOẠCH THỰC THI THI CÔNG CODE (IMPLEMENTATION PLAN)
## Module 1 - UC1: Đăng ký & Đăng nhập Hệ thống

**Tài liệu tham chiếu:**
- `04-Implement/UC1/EDS-UC1.md` (Đặc tả thiết kế)
- `00-Policy/principles.md` (Nguyên tắc dự án)

---

## I. Căn cứ tuân thủ các nguyên tắc cốt lõi
1. **Kiến trúc (Nguyên tắc 1):** Tất cả luồng xử lý sẽ sử dụng Spring Boot MVC `@Controller` trả về giao diện Thymeleaf (không dùng `@RestController` trả JSON). Dữ liệu form truyền qua `@ModelAttribute`. Đặt tên tuân thủ Java Naming Convention và hướng đối tượng.
2. **Giao diện & Tài nguyên (Nguyên tắc 4):** View sẽ đặt tại `templates/auth/`. Tuyệt đối không viết CSS/JS nội tuyến. Tất cả tài nguyên tĩnh liên quan đến UC1 sẽ được tách vào `static/css/auth/` và `static/js/auth/`.
3. **Đóng gói mã nguồn (Nguyên tắc 2):** Toàn bộ mã nguồn code thực thi khi thi công sẽ được bọc trong các file Markdown, không sinh trực tiếp mã nguồn vào repo nếu chưa có chỉ định.
4. **Kiểm duyệt (Nguyên tắc 3):** File kế hoạch này là cơ sở để xin phê duyệt. Các khối code chỉ được tạo sau khi kế hoạch này được đồng ý.
5. **Bảo mật (EDS-UC1):** Triển khai JPA Converter `AesDataEncryptor` cho PII (CCCD) và sử dụng BCrypt cho Password. Tích hợp Google SSO qua OAuth2.

---

## II. Phân chia cấu trúc gói (Package & Directory Structure)

Hệ thống sẽ được thi công theo các gói mã nguồn sau:
```text
src/main/java/com/AuraMoon/auramoon/
 ├── config/
 │    ├── AesDataEncryptor.java        # Xử lý mã hóa AES-256 tự động
 │    ├── SecurityConfig.java          # Cấu hình form login và OAuth2 SSO
 │    └── OAuth2LoginSuccessHandler.java # Xử lý tự động tạo/liên kết tài khoản Google
 ├── entity/
 │    └── User.java                    # Entity ánh xạ bảng [USER]
 ├── repository/
 │    └── UserRepository.java          # Spring Data JPA 
 ├── dto/
 │    ├── UserRegistrationDto.java     # Form DTO đăng ký
 │    └── UserLoginDto.java            # Form DTO đăng nhập
 ├── service/
 │    ├── IAuthService.java            # Interface
 │    └── impl/
 │         └── AuthServiceImpl.java    # Xử lý BCrypt, verify token, gửi mail
 └── controller/
      └── AuthController.java          # Spring MVC trả về Thymeleaf views

src/main/resources/
 ├── templates/auth/
 │    ├── login.html
 │    └── register.html
 └── static/
      ├── css/auth/
      │    ├── login.css
      │    └── register.css
      └── js/auth/
           ├── login-validation.js
           └── register-validation.js
```

---

## III. Các Giai Đoạn Thi Công (Phased Execution)

### Phase 1: Thi công Tầng Cơ Sở Dữ Liệu & Model (Data Layer)
*   **1.1.** Viết class `AesDataEncryptor` implement `AttributeConverter` từ JPA để thực hiện mã hóa thông tin nhạy cảm ở tầng Java (cụ thể là `Identify_code`).
*   **1.2.** Viết class Entity `User` với các annotation `@Entity`, `@Table`, và đặc biệt là `@Convert` cho trường PII.
*   **1.3.** Viết interface `UserRepository` kế thừa `JpaRepository`, cung cấp các method tùy chỉnh như `findByEmail(String email)` và `findByVerifyToken(String token)`.

### Phase 2: Thi công Tầng Nghiệp Vụ (Service Layer)
*   **2.1.** Xây dựng các lớp DTO (`UserRegistrationDto`) chứa các Validation Annotation (`@NotBlank`, `@Email`, `@Size` cho mật khẩu).
*   **2.2.** Viết interface `IAuthService` và class `AuthServiceImpl`. Triển khai logic:
    - Băm mật khẩu (BCrypt).
    - Sinh UUID làm `verify_token` cho người mới đăng ký, set status là PENDING.
    - Hàm verify để cập nhật status thành ACTIVE và clear token.

### Phase 3: Thi công Tầng Bảo Mật (Security Layer)
*   **3.1.** Cấu hình `SecurityConfig`: Phân quyền các endpoint public (`/login`, `/register`, `/verify-email`), cấu hình formLogin trỏ tới trang đăng nhập tự tạo và cấu hình `oauth2Login`.
*   **3.2.** Cấu hình SSO: Viết `OAuth2LoginSuccessHandler` để đón callback từ Google, trích xuất thông tin Email. Nếu email chưa có trong Database, tự động insert record mới (Role: GUEST, Status: ACTIVE).

### Phase 4: Thi công Tầng Điều Hướng & Giao Diện (MVC Controller & View)
*   **4.1.** Viết `AuthController` với các phương thức `@GetMapping` để render form (kèm object ModelAttribute) và `@PostMapping` để xử lý form submit, return view redirect (tuân thủ Nguyên tắc 1).
*   **4.2.** Viết mã HTML Thymeleaf (`login.html`, `register.html`), map các trường với th:field. Hiển thị validation errors nếu form nhập sai.
*   **4.3.** Viết mã CSS và JS độc lập theo cấu trúc (Nguyên tắc 4) để thực hiện layout và client-side validation, sau đó nhúng `<link>` và `<script>` vào giao diện.

### Phase 5: Chuẩn bị file Code & Kiểm thử (Review & Testing)
*   **5.1.** Đóng gói tất cả mã Java, HTML, CSS, JS vào các file `.md` (Nguyên tắc 2).
*   **5.2.** Đề xuất các đoạn Unit Test căn bản (nếu được yêu cầu).
*   **5.3.** Đệ trình kết quả để phía Project Owner (Người dùng) xem xét và phê duyệt theo (Nguyên tắc 3) trước khi áp dụng vào source gốc của ứng dụng.

---
**Trạng thái kế hoạch:** Sẵn sàng thực thi. Vui lòng phản hồi để duyệt kế hoạch này và bắt đầu triển khai code.
