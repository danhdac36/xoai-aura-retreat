# Kế hoạch Thực thi (Implementation Plan) - UC03: Quản lý Nhân viên

Tài liệu này vạch ra các bước lập trình chi tiết để triển khai Use Case 03 dựa trên tài liệu đặc tả `UC03_EDS.md` và **bám sát hoàn toàn vào source code hiện tại** của dự án `auramoon`.

## 1. Mục Tiêu (Goal)
Triển khai chức năng Quản lý tài khoản nhân viên (CRUD: Thêm, Xem, Vô hiệu hóa) sử dụng kiến trúc Spring Boot MVC, Thymeleaf, Spring Data JPA, và Spring Security. Thực thi các rule về RBAC và Soft Delete.

## 2. Phân Tích Hiện Trạng (Source Code Analysis)
Sau khi review source code trong `05_Development`:
- **Entities**: Đã tồn tại sẵn `User`, `Role` (trong `com.AuraMoon.auramoon.auth.entity`), `Therapist` (trong module `spa`), `AuditLog` (trong module `billing`). Đặc biệt `identifyCode` của User đã được mã hóa sẵn với `AesDataEncryptor`.
- **Layout**: Đã có sẵn `admin-layout.html` trong `templates/layout/`.
- **CSS Framework**: Dự án đang dùng Tailwind CSS (`tailwind-config.html`).

Do đó, kế hoạch sẽ tập trung vào việc **tái sử dụng (reuse)** các entity hiện có, và chỉ xây dựng thêm lớp Service, Controller, View cho chức năng Quản lý Nhân viên.

---

## 3. Các Bước Triển Khai Chi Tiết (Proposed Changes)

### 3.1. Tầng Repository (`src/main/java/com/AuraMoon/auramoon/auth/repository`)
* **[MODIFY] `UserRepository.java`** (nếu chưa có sẵn các method này):
  - Thêm method `List<User> findByIsDeleteFalseAndRole_RoleNameIn(List<String> roleNames);` (để lấy danh sách nhân viên: Therapist, Chef, Receptionist, Admin).

### 3.2. Tầng DTO & Service (`src/main/java/com/AuraMoon/auramoon/auth`)
* **[NEW] `dto/StaffDTO.java`**:
  - Chứa thông tin form nhập: `fullName`, `email`, `passwordRaw`, `roleId`, `phone`, `identifyCode`.
  - Sử dụng Validation annotations (`@NotBlank`, `@Email`, `@NotNull`).
* **[NEW] `service/StaffService.java`** & **[NEW] `service/impl/StaffServiceImpl.java`**:
  - Logic `@Transactional`.
  - `createStaff(StaffDTO dto)`: 
    + Check email trùng `userRepository.existsByEmail()`.
    + Mã hóa password bằng `PasswordEncoder`.
    + Save `User`. Nếu role là THERAPIST, tạo tiếp bản ghi `Therapist` qua `therapistRepository`.
    + Ghi log bằng `auditLogService.log()`.
  - `getAllStaffs()`: Lấy danh sách nhân viên hiển thị (không lấy role GUEST).
  - `deactivateStaff(Integer id)`: Tìm User, set `status = "INACTIVE"` và `isDelete = true`, gọi save().

### 3.3. Tầng Controller (`src/main/java/com/AuraMoon/auramoon/auth/controller`)
* **[NEW] `StaffController.java`**:
  - Lớp được gắn `@Controller` và `@RequestMapping("/manager/staff")`.
  - `GET /` -> Trả về view `manager/staff-list`. Push list User vào model.
  - `GET /create` -> Trả về view `manager/staff-form` kèm `new StaffDTO()`. Lấy list Role từ `RoleRepository` truyền xuống view để render thẻ `<select>`.
  - `POST /create` -> `@Valid @ModelAttribute("staffDTO") StaffDTO`. Nếu có lỗi (`BindingResult.hasErrors()`), return lại view `staff-form`. Nếu thành công, `redirect:/manager/staff`.
  - `POST /{id}/deactivate` -> Gọi service vô hiệu hóa, `redirect:/manager/staff`.

### 3.4. Tầng View / Thymeleaf (`src/main/resources/templates/manager`)
Tạo 2 giao diện mới, kế thừa `layout/admin-layout.html` bằng Thymeleaf Layout Dialect:
* **[NEW] `staff-list.html`**:
  - Giao diện bảng (table) dùng TailwindCSS.
  - Cột: ID, Họ tên, Email, Chức vụ, Trạng thái, Hành động (Nút khóa tài khoản).
* **[NEW] `staff-form.html`**:
  - Giao diện Form thêm mới. 
  - Render thẻ select cho Role.
  - Hiển thị lỗi validation màu đỏ dưới mỗi field (dùng `th:if="${#fields.hasErrors('email')}"`).

### 3.5. Cấu hình Security (`src/main/java/com/AuraMoon/auramoon/auth/config/SecurityConfig.java`)
* **[MODIFY] `SecurityConfig.java`** (hoặc WebSecurityConfigurer tương tự đang có):
  - Cấu hình phân quyền: `.requestMatchers("/manager/staff/**").hasRole("ADMIN")`.

---

## 4. Kế hoạch Kiểm thử (Verification Plan)
- **Tạo nhân viên**: Truy cập `/manager/staff/create`, nhập form hợp lệ, chọn Role THERAPIST. 
- **DB Check**: Mở Data Source, kiểm tra xem bảng `USER` có insert chưa, bảng `THERAPIST` có sinh mã `therapist_code` tự động không.
- **Validation Check**: Để trống email, submit form -> Hệ thống phải giữ lại trang `staff-form.html` và báo chữ màu đỏ.
- **Khóa tài khoản**: Bấm nút Deactivate -> Tài khoản phải đổi `status` thành `INACTIVE` thay vì mất hẳn khỏi DB.
