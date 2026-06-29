# Báo Cáo Kiến Trúc & Chi Tiết Module Auth (Xác Thực & Phân Quyền)

> [!NOTE]
> Báo cáo này trình bày chi tiết về cấu trúc mã nguồn, vai trò của từng lớp (Class), và luồng hoạt động (Data Flow) của Module `auth` thuộc hệ thống Aura Moon Retreat. Tài liệu phục vụ cho quá trình handover, review code và maintain dự án.

---

## 1. Tổng Quan Kiến Trúc (Architecture Overview)

Module `auth` được thiết kế theo mô hình **Domain-Driven Design (DDD)** kết hợp với mô hình **MVC của Spring Boot**. Đây là module cốt lõi chịu trách nhiệm:
- Đăng nhập (Local Form & Google SSO) và Đăng ký tài khoản.
- Quản lý Hồ sơ cá nhân (Personal Profile) và Hồ sơ sức khỏe nhạy cảm (Sensitive Profile).
- Quản lý tài khoản nội bộ (CRUD accounts cho Admin/Manager).
- Phân quyền theo Role-Based Access Control (RBAC) thông qua **Spring Security**.

Cấu trúc package chuẩn mực bao gồm:
- `config`: Thiết lập Security, Mã hóa, Custom Handlers.
- `controller`: Tiếp nhận HTTP Request và điều hướng giao diện/dữ liệu.
- `service`: Chứa Business Logic cốt lõi.
- `repository`: Giao tiếp Database (JPA).
- `entity`: Cấu trúc bảng Database.
- `dto`: Các Data Transfer Object để truyền tải dữ liệu giữa các layer.

---

## 2. Chi Tiết Các Thành Phần Cốt Lõi (Core Components)

### 2.1. Tầng Thực Thể & Database (Entity & Repository)

- **`User` (Entity):** Mapping với bảng `[USER]`. Chứa thông tin như email, password (mã hóa bcrypt), tên, giới tính, trạng thái (`status`), và đặc biệt là `identifyCode` (được mã hóa AES tự động bằng Converter).
- **`Role` (Entity):** Mapping bảng phân quyền. Các role bao gồm: GUEST, ADMIN, MANAGER, RECEPTIONIST, THERAPIST, CHEFF, YOGA_INSTRUCTOR.
- **`IUserRepository` & `UserRepository`:** Tầng giao tiếp DB với Spring Data JPA. Đã được refactor sử dụng `@EntityGraph(attributePaths = {"role"})` tại hàm `findByEmail` nhằm tránh lỗi N+1 Query và `LazyInitializationException` khi load User.

### 2.2. Tầng Cấu Hình (Configuration)

- **`SecurityConfig`:** Lớp quan trọng nhất định nghĩa luật bảo mật. Cấu hình public cho các URL như `/auth/**`, `/css/**` và kiểm tra quyền (`hasRole`) cho các URL như `/admin/**`, `/manager/**`. Cấu hình Form Login và OAuth2 Login đồng thời chặn nhiều session (1 user tối đa 2 thiết bị).
- **`AesDataEncryptor`:** Một `AttributeConverter` thực hiện mã hóa và giải mã chuỗi bằng thuật toán AES-256 (Mật khẩu: `AuraMoonRetreatWellnessSystem206`). Sử dụng để bảo vệ trường `identifyCode` (CCCD/Passport) của `User` ngay khi lưu xuống DB.
- **`CustomAuthenticationSuccessHandler`:** Lớp lắng nghe sự kiện đăng nhập Local thành công, tự động điều hướng người dùng tới dashboard tương ứng dựa trên Role (VD: ADMIN -> `/admin/home`, GUEST -> `/`).
- **`OAuth2LoginSuccessHandler`:** Lớp xử lý sau khi đăng nhập Google (SSO) thành công. Tự động lấy email từ Google, nếu chưa có thì cấp tài khoản mới và gán Role `GUEST`.

### 2.3. Tầng Dịch Vụ (Service Layer)

- **`AuthServiceImpl`:** Chứa logic xác thực cốt lõi.
  - `register()`: Xử lý đăng ký tài khoản (tạo User INACTIVE, mã hóa mật khẩu, sinh verify token, gửi email).
  - `verifyEmail()`: Xác thực Token từ Email để kích hoạt tài khoản (`ACTIVE`).
  - `createGoogleUser()`: Tự động khởi tạo (auto-provisioning) User nếu chưa tồn tại từ Google SSO.
- **`UserService`:** Implements `UserDetailsService` của Spring Security.
  - `loadUserByUsername(String email)`: Hàm được Spring Security gọi ngầm. Truy xuất User từ DB, chặn đăng nhập nếu trạng thái là `PENDING`. Đóng gói User vào `UserDetailsResponse`.
- **`AccountService` / `AccountServiceImpl`:** Dùng cho quản trị viên (Admin/Manager) để thực hiện CRUD (Tạo, Sửa, Xóa mềm/Khôi phục) các tài khoản nhân sự.
- **`ProfileServiceImpl`:** 
  - Đọc/Ghi dữ liệu thông tin cá nhân cơ bản (`PersonalProfileDto`).
  - Đọc/Ghi/Xóa dữ liệu nhạy cảm (`SensitiveProfileDto`) bao gồm hồ sơ sức khỏe.
  - Xử lý nghiệp vụ thay đổi mật khẩu (`changePassword()`).

### 2.4. Tầng Điều Khiển (Controller Layer)

- **`AuthController`:** 
  - `GET/POST /auth/login`, `GET/POST /auth/register`: Render form và xử lý submit form. Bắt lỗi không khớp mật khẩu xác nhận.
  - `GET /auth/verify-email`: Endpoint nhận token từ email người dùng nhấn vào.
  - `POST /auth/logout`: Đăng xuất, xóa Security Context.
- **`AccountController`:** 
  - Mapping dưới `/manager/account`. Cung cấp màn hình quản lý tài khoản với các tính năng: Liệt kê, Thêm, Sửa chức vụ, Vô hiệu hóa (Deactivate) và Khôi phục (Restore). Hỗ trợ trả về một phần HTML (`Fragment`) nếu request gửi từ Ajax (`XMLHttpRequest`).
- **`ProfileController`:**
  - Định tuyến `/profile/me`: Quản lý thông tin chung. Bắt lỗi Validation (Vd: Phải trên 18 tuổi).
  - Định tuyến `/profile/health`: Cập nhật/Xóa thông tin y tế. Xử lý logic bắt buộc đồng ý điều khoản (`HasConsent` == true).
  - Định tuyến `/profile/my-account`: Quản lý thông tin tài khoản và đổi mật khẩu.

---

## 3. Các Luồng Hoạt Động Điển Hình (Data Flows)

> [!TIP]
> Nắm vững 3 luồng hoạt động dưới đây để dễ dàng fix bug hoặc mở rộng module `auth` trong tương lai.

### 3.1. Luồng Đăng ký & Kích hoạt (Registration Flow)
1. **[User]** điền form tại `/auth/register` $\rightarrow$ `AuthController.registerUser()` nhận Request.
2. Kiểm tra `BindingResult` và đối chiếu Confirm Password.
3. Chuyển DTO xuống `AuthServiceImpl.register()`.
4. Service check tồn tại Email $\rightarrow$ Nếu chưa có, tạo entity `User` trạng thái `INACTIVE`, hash password, sinh UUID làm `verifyToken`.
5. Gọi `IUserRepository.save(User)`.
6. Gọi `JavaMailSender` gửi email chứa link `http://localhost:8080/auth/verify-email?token=UUID`.
7. **[User]** click link $\rightarrow$ `AuthController.verifyEmail()`.
8. Tìm User có token đó $\rightarrow$ Đổi status thành `ACTIVE`, set token = null $\rightarrow$ Save DB.

### 3.2. Luồng Cập Nhật Dữ Liệu Sức Khỏe (Update Sensitive Profile)
1. **[User]** đăng nhập, truy cập `/profile/health`.
2. `ProfileController.viewHealthProfile()` lấy `userId` từ session (trong `Authentication`).
3. Gọi `ProfileService.getSensitiveProfile()` đổ dữ liệu cũ ra Form.
4. **[User]** sửa thông tin và Submit.
5. Controller bắt Request $\rightarrow$ Kiểm tra `dto.isHasConsent()` (Bắt buộc phải check ô Đồng ý).
6. Gọi `ProfileService.saveSensitiveProfile()` $\rightarrow$ Cập nhật thông tin y tế vào bảng DB.

### 3.3. Luồng Bảo Mật Dữ Liệu Lõi (Mã hóa AES)
1. Khi **[Admin/Manager]** tạo hoặc chỉnh sửa `User` có gắn thuộc tính Thẻ căn cước/Passport (trường `identifyCode`).
2. Tầng JPA sẽ tự động gọi hàm `convertToDatabaseColumn` của lớp `AesDataEncryptor`.
3. Chuỗi gốc được mã hóa AES (Symmetric Key) và lưu thành dạng mã hóa (Base64) xuống database.
4. Khi **[User]** xem profile, JPA tự động gọi `convertToEntityAttribute` để giải mã và hiển thị bình thường lên giao diện.

---

## 4. Tổng Kết
Module `auth` được cấu trúc rất tốt, tuân thủ chặt chẽ nguyên lý **Separation of Concerns**. Khả năng bảo mật được đề cao qua việc tích hợp mã hóa AES dữ liệu cá nhân, Spring Security RBAC phân tầng kỹ lưỡng, và có sự kiểm soát CSRF Token cho các Form quan trọng. 

*Phiên bản này đã được refactor để sử dụng `@EntityGraph` cho `User` Entity nhằm tối ưu hóa Query Performance.*
