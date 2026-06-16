# BÁO CÁO RÀ SOÁT KỸ THUẬT & LUỒNG CHẠY (TECHNICAL REVIEW REPORT)
## Phân hệ: Authentication & Sensitive Health Profile — Use Case 1 (Đăng ký, Đăng nhập & Đăng xuất)

**Dự án:** Xoai Aura Retreat - Wellness Resort & Spa Management System  
**Mã dự án:** SWP391-HOS-03  
**Tác giả:** Nhóm Phát Triển G6  
**Người duyệt (Tech Lead):** [Tech Lead Name]  
**Phiên bản:** 1.0 (Hoàn thành thi công và sửa lỗi thực tế)

---

## I. TỔNG QUAN HỆ THỐNG & CÔNG NGHỆ SỬ DỤNG
*   **Kiến trúc:** Spring Boot MVC kết hợp với Thymeleaf HTML (Không dùng RESTful API).
*   **Quản lý phiên đăng nhập:** Quản lý bằng Session truyền thống (`JSESSIONID` cookie và `HttpSession`) thay vì JWT theo đúng yêu cầu nghiệp vụ.
*   **Bảo mật:** Spring Security 6.x tích hợp mã hóa mật khẩu mật độ cao (BCrypt) và đăng nhập bên thứ ba (Google OAuth2 SSO).

---

## II. SƠ ĐỒ LUỒNG CHẠY (EXECUTION FLOWS)

### 1. Luồng Đăng ký tài khoản truyền thống & Xác thực Email (Traditional Registration & Email Verification)
```text
[Người dùng] ➡️ Submit form Đăng ký (Họ tên, Email, Mật khẩu, Xác nhận mật khẩu)
    |
    v
[AuthController.registerUser] ➡️ Validates dữ liệu đầu vào & Khớp mật khẩu
    |
    v
[AuthServiceImpl.register] 
    ├── 1. Kiểm tra Email tồn tại (nếu có -> throw IllegalArgumentException)
    ├── 2. Băm mật khẩu bằng BCrypt (PasswordEncoder)
    ├── 3. Sinh verifyToken ngẫu nhiên (UUID) & Lưu User trạng thái 'PENDING'
    └── 4. Gửi email chứa link kích hoạt (JavaMailSender)
    |
    v
[Người dùng] ➡️ Click link kích hoạt trong Email (verify-email?token=UUID)
    |
    v
[AuthController.verifyEmail] ➡️ Gọi Service cập nhật trạng thái User thành 'ACTIVE' và xóa verifyToken
```

#### 💡 Giải thích chi tiết & Dễ hiểu về Luồng Xác thực Email (Verify Email):
*   **Bản chất:** Là cơ chế kiểm tra xem email đăng ký có thực sự tồn tại và thuộc sở hữu của người đăng ký hay không, đồng thời chặn người dùng đăng nhập cho đến khi tài khoản được xác nhận thành công.
*   **Các bước vận hành ngầm:**
    1.  **Đăng ký tạm thời (Trạng thái PENDING):** Khi điền form đăng ký, mật khẩu được băm bảo mật bằng BCrypt. Hệ thống sẽ lưu tài khoản vào cơ sở dữ liệu với trạng thái `PENDING` (chưa thể đăng nhập) và tạo ra một chuỗi mã ngẫu nhiên duy nhất (**`verify_token`** dạng UUID) gắn liền với tài khoản đó.
    2.  **Gửi link kích hoạt:** Hệ thống gửi một email tự động tới hòm thư của người dùng, chứa đường dẫn kích hoạt: `http://localhost:8080/auth/verify-email?token=[Mã_UUID_ở_trên]`.
    3.  **Xử lý kích hoạt (Verify):** Khi người dùng click vào link trong email, request GET sẽ gửi tham số `token` về `AuthController.verifyEmail()`. Controller gọi xuống Service thực hiện câu lệnh truy vấn tìm User theo mã token này (`userRepository.findByVerifyToken(token)`).
    4.  **Kích hoạt và Xóa vết Token:** 
        *   Nếu tìm thấy User khớp với mã token: Trạng thái User được cập nhật từ `PENDING` thành `ACTIVE` (đủ điều kiện đăng nhập). Đồng thời, trường `verify_token` trong DB của User đó sẽ được đặt về `null` (xóa trắng).
        *   *Ý nghĩa bảo mật:* Việc đặt token về `null` ngay lập tức để ngăn chặn hành vi sử dụng lại link cũ đã xác thực (chống tấn công **Replay Attack**).
        *   Nếu không tìm thấy hoặc token không khớp: Hệ thống lập tức trả về giao diện thông báo lỗi kích hoạt thất bại.
*   **Các lớp (Classes) liên quan mật thiết:**
    *   **`AuthController`**: Cung cấp API endpoint (`/verify-email`) để đón nhận request GET chứa token từ người dùng (khi click vào link trong email).
    *   **`AuthServiceImpl`**: Chứa logic nghiệp vụ lõi (`register`, `verifyUser`). Chịu trách nhiệm tạo mã UUID, lưu token vào DB lúc đăng ký, và kiểm tra, cập nhật trạng thái User từ `PENDING` sang `ACTIVE` khi xác thực.
    *   **`IUserRepository`**: Tương tác với cơ sở dữ liệu, cung cấp hàm `findByVerifyToken(String token)` để truy vấn User mang mã kích hoạt tương ứng.
    *   **`EmailService` (Sử dụng `JavaMailSender`)**: Chịu trách nhiệm thiết lập nội dung và gửi email chứa đường link kích hoạt đến hộp thư của người dùng thông qua cấu hình SMTP.

---

### 2. Luồng Đăng nhập Form Login truyền thống (Form Authentication)
```text
[Người dùng] ➡️ Nhập Email & Mật khẩu tại trang Đăng nhập (/auth/login)
    |
    v
[Spring Security Engine] ➡️ Đánh chặn request POST /auth/login
    |
    v
[UserService.loadUserByUsername] 
    ├── 1. Tìm user theo Email trong DB (nếu không thấy -> throw UsernameNotFoundException)
    ├── 2. Kiểm tra status (nếu là 'PENDING' -> chặn đăng nhập)
    ├── 3. Nạp vai trò (Role) từ DB và map sang định dạng Authority (ROLE_GUEST, ROLE_ADMIN, ...)
    └── 4. Trả về đối tượng UserDetails cho Spring Security xác thực mật khẩu
    |
    v
[Spring Security Engine] ➡️ So khớp mật khẩu băm, nếu khớp:
    |
    v
[HomeController.index] ➡️ Định tuyến thông minh dựa trên Role hiện tại của User:
    ├── ROLE_ADMIN       ➡️ Chuyển hướng đến /admin/home
    ├── ROLE_GUEST       ➡️ Chuyển hướng đến /profile/home
    ├── ROLE_RECEPTIONIST➡️ Chuyển hướng đến /receptionist/home
    └── ...
```

---

### 3. Luồng Đăng nhập Google SSO (OAuth2 Single Sign-On & Auto-Registration)
```text
[Người dùng] ➡️ Click nút Đăng nhập bằng Google
    |
    v
[Spring Security OAuth2 Client] ➡️ Xác thực qua Google và nhận về OAuth2User Principal
    |
    v
[OAuth2LoginSuccessHandler.onAuthenticationSuccess]
    ├── 1. Trích xuất email và tên từ Google Profile
    ├── 2. Gọi AuthService.createGoogleUser (nếu chưa có TK -> Tự động đăng ký vai trò GUEST, status ACTIVE)
    ├── 3. Nạp đè phiên Authentication hiện hành thành OAuth2AuthenticationToken chứa Authority DB (ROLE_GUEST)
    └── 4. Chuyển hướng đến "/" -> Đi qua HomeController để phân luồng Dashboard tự động
```

#### 💡 Giải thích chi tiết & Dễ hiểu về Luồng Đăng nhập/Đăng ký bằng Google (SSO OAuth2):
*   **Bản chất:** Cho phép người dùng đăng nhập ngay lập tức thông qua tài khoản Google. Nếu người dùng đăng nhập lần đầu, hệ thống sẽ tự động tạo tài khoản mới mà không cần qua form đăng ký (Đăng ký tự động).
*   **Các bước vận hành ngầm:**
    1.  **Xác thực với Google:** Khi click vào nút Google, yêu cầu được Spring Security chuyển hướng đến máy chủ của Google. Sau khi người dùng xác nhận cấp quyền, Google gửi trả thông tin định danh (Email, Họ tên, Ảnh đại diện) về cho ứng dụng.
    2.  **Kiểm tra & Tự động Đăng ký (Auto-Registration):** `OAuth2LoginSuccessHandler` tiếp nhận thông tin và kiểm tra trong DB:
        *   **Nếu Email chưa tồn tại:** Hệ thống tự động tạo một tài khoản mới với vai trò là `GUEST` (khách hàng), gán trạng thái là `ACTIVE` ngay lập tức (không cần kích hoạt email vì Google đã xác thực email đó), sinh một mật khẩu ngẫu nhiên an toàn để lưu cấu trúc DB.
        *   **Nếu Email đã tồn tại:** Trích xuất thông tin tài khoản hiện có từ DB.
    3.  **Tái xác thực đồng bộ quyền (Re-authentication - Quan trọng nhất):** 
        *   *Vấn đề:* Mặc định, sau khi đăng nhập Google thành công, Spring Security chỉ cấp quyền chung chung là `ROLE_USER`. Nó hoàn toàn không biết tài khoản này trong DB của chúng ta có quyền gì (ví dụ: `GUEST`, `ADMIN` hay `MANAGER`).
        *   *Giải pháp:* Handler sẽ lấy Role thực tế từ DB của user đó (ví dụ: `GUEST`), tạo ra một `SimpleGrantedAuthority("ROLE_GUEST")`. Sau đó nạp đè đối tượng xác thực trong Session bằng một **`OAuth2AuthenticationToken`** mới chứa đúng quyền hạn thực tế này.
    4.  **Điều hướng phân quyền:** Handler chuyển hướng người dùng đến trang chủ `/`. Tại đây, `HomeController` kiểm tra thấy quyền của Session đã được cập nhật thành `ROLE_GUEST` và tự động redirect người dùng đến `/profile/home` (dashboard khách hàng) một cách trơn tru.
*   **Các lớp (Classes) liên quan mật thiết:**
    *   **`SecurityConfig`**: Cấu hình Spring Security mở cổng OAuth2 Login (`.oauth2Login()`) và chỉ định sử dụng lớp xử lý thành công tùy chỉnh (`OAuth2LoginSuccessHandler`).
    *   **`OAuth2LoginSuccessHandler`**: Là trung tâm xử lý của luồng SSO. Cài đặt Interface `AuthenticationSuccessHandler`, nó hứng kết quả đăng nhập Google, bóc tách thông tin cá nhân, thực thi logic đăng ký tự động và quan trọng nhất là "nạp lại quyền thực tế" cho Security Context.
    *   **`AuthServiceImpl`**: Cung cấp hàm `createGoogleUser(...)` để kiểm tra và lưu nhanh người dùng mới vào cơ sở dữ liệu (cùng một mật khẩu ngẫu nhiên mã hóa) nếu đây là lần đầu người đó đăng nhập bằng Google.
    *   **`HomeController`**: Phân tuyến điều hướng thông minh dựa vào các quyền hạn đã được `OAuth2LoginSuccessHandler` nạp lại để đưa người dùng đến đúng bảng điều khiển dành riêng cho họ.

---

## III. CÁC ĐIỂM ĐẶC BIỆT & GIẢI PHÁP KỸ THUẬT QUAN TRỌNG (TECH DETAIL REVIEWS)

### 1. Giải quyết Lỗi Vòng lặp Phụ thuộc (Circular Dependency)
*   **Vấn đề phát hiện:** `SecurityConfig` cần nhúng `OAuth2LoginSuccessHandler` ➡️ Handler cần nhúng `AuthService` ➡️ `AuthServiceImpl` cần nhúng `PasswordEncoder` ➡️ `PasswordEncoder` lại được định nghĩa là Bean trong `SecurityConfig`. Điều này gây crash Spring ApplicationContext ngay khi khởi chạy.
*   **Giải pháp:** Tách hoàn toàn cấu hình mật khẩu sang một tệp tin độc lập là `PasswordEncoderConfig.java`. Cấu hình định tuyến bảo mật trong `SecurityConfig.java` chỉ nhúng PasswordEncoder, bẻ gãy hoàn toàn vòng lặp.

### 2. Khắc phục lỗi LazyInitializationException (Hibernate proxy - no session)
*   **Vấn đề phát hiện:** Thuộc tính `User.role` được cấu hình là `FetchType.LAZY`. Khi ngoài phạm vi Transaction (như trong `CustomUserDetailsService` hoặc `OAuth2LoginSuccessHandler` khi đã đóng session của Service), việc gọi `user.getRole().getRoleName()` ném ra lỗi `LazyInitializationException` khiến người dùng bị kẹt vòng lặp redirect trang đăng nhập hoặc crash trang OAuth2.
*   **Giải pháp:** 
    *   Thêm `@Transactional(readOnly = true)` cho hàm `loadUserByUsername` trong `UserService.java`.
    *   Chủ động gọi tải sớm proxy `user.getRole().getRoleName()` bên trong phương thức `@Transactional` của `AuthServiceImpl.createGoogleUser(...)` để nạp dữ liệu quyền trước khi đối tượng `User` bị chuyển sang trạng thái *Detached*.

### 3. Mã hóa dữ liệu nhạy cảm ở cấp độ thực thể (PII Encryption at Rest)
*   **Vấn đề phát hiện:** Số CCCD/Hộ chiếu (`Identify_code`) là thông tin định danh cá nhân quan trọng (PII) cần tuân thủ Nghị định 356/2025/NĐ-CP và Luật Cư trú 2020.
*   **Giải pháp:** 
    *   Thiết kế JPA Attribute Converter `AesDataEncryptor.java` sử dụng thuật toán mã hóa đối xứng **AES-128 (mẫu chuẩn)**. Mọi thao tác ghi và đọc trường `identifyCode` của User Entity đều tự động được mã hóa/giải mã ở tầng JPA trước khi lưu xuống DB.
    *   **Lưu ý DB:** Kích thước ban đầu của cột `Identify_code` trong Database là `VARCHAR(20)`. Chuỗi sau khi mã hóa AES và encode Base64 sẽ có độ dài ít nhất là 24 hoặc 32 ký tự, dẫn đến lỗi tràn bộ đệm. Cần chạy tập lệnh DB nâng kích thước lên `VARCHAR(255)`.

### 4. Tách biệt hoàn toàn Tài nguyên tĩnh (Nguyên tắc 4)
*   Không nhúng bất kỳ mã `<script>` hay `<style>` nội tuyến nào trong `login.html` và `registration.html`.
*   Tách toàn bộ mã xử lý UI/UX (Parallax, Input micro-interactions, Client validation) ra các file tĩnh độc lập:
    *   CSS: `static/css/auth/login.css`, `static/css/auth/registration.css`
    *   JS: `static/js/auth/login.js`, `static/js/auth/registration.js`

---

## IV. BẢNG KHỞI TẠO DỮ LIỆU & TÀI KHOẢN KIỂM THỬ (DATA INITIAL)
Tệp cấu hình `DataInitial.java` (được chạy bằng `CommandLineRunner` khi ứng dụng start) tự động nạp sẵn 6 vai trò và 6 tài khoản kiểm thử phân quyền để phục vụ quá trình Review với Tech Lead:

| STT | Tài khoản đăng nhập (Email) | Vai trò (Role) | Mật khẩu | Trang Home đích (Redirect Target) |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `admin@xoai-aura.com` | **ADMIN** | `password123` | `/admin/home` |
| 2 | `guest@xoai-aura.com` | **GUEST** | `password123` | `/profile/home` |
| 3 | `receptionist@xoai-aura.com` | **RECEPTIONIST** | `password123` | `/receptionist/home` |
| 4 | `therapist@xoai-aura.com` | **THERAPIST** | `password123` | `/therapist/home` |
| 5 | `cheff@xoai-aura.com` | **CHEFF** | `password123` | `/F&B/home` |
| 6 | `manager@xoai-aura.com` | **MANAGER** | `password123` | `/management/home` |

*Tất cả tài khoản trên đều có trạng thái mặc định là `ACTIVE` trong DB.*
