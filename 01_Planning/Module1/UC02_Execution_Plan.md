# Kế Hoạch Thực Thi (Execution Plan) - UC02

**Mục tiêu**: Hiện thực hóa UC02 (Cập nhật Hồ sơ Sức khỏe & Dinh dưỡng) dựa trên thiết kế kỹ thuật EDS v1.3.

> [!IMPORTANT]
> - Phần thông tin nhạy cảm bắt buộc phải được mã hóa trước khi lưu xuống Database.
> - Form dữ liệu phải sử dụng ModelAttribute kèm BindingResult để xử lý lỗi.
> - Bắt buộc ghi Audit Log (`AuditLogRepository`) và lưu `Consent` xuống DB khi Submit thành công (BR-15).
> - Thuộc tính `update_at` phải được cập nhật ở mọi thao tác lưu.

## Các Bước Thực Thi (Proposed Changes)

---

### 1. Tầng Repository Layer
Chúng ta cần làm việc với 4 bảng.

#### [NEW] [PhysicalHealthProfileRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/spa/repository/PhysicalHealthProfileRepository.java)
- Tạo Interface kế thừa `JpaRepository<PhysicalHealthProfile, Integer>`.
- Thêm method `findByUserId(Integer userId)`.

#### [CHECK] [DietaryProfileRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/DietaryProfileRepository.java)
- Có sẵn. Đảm bảo dùng đúng method `findByUserId(Integer userId)`.

#### [CHECK] [ConsentRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/repository/ConsentRepository.java)
- Đã tồn tại. Dùng để lưu bản ghi Consent.

#### [CHECK] [AuditLogRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/billing/repository/AuditLogRepository.java)
- Đã tồn tại. Dùng để lưu vết hành động.

---

### 2. Tầng Service Layer
Xử lý logic mã hóa (gọi `SensitiveProfileDto`), cập nhật `update_at`, lưu AuditLog và Consent.

#### [NEW] [IProfileService.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/IProfileService.java)
- `SensitiveProfileDto getSensitiveProfile(Integer userId)`
- `void saveSensitiveProfile(SensitiveProfileDto inputDto, Integer userId)`

#### [NEW] [ProfileServiceImpl.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/ProfileServiceImpl.java)
- Triển khai logic implement `IProfileService`.
- Inject `PhysicalHealthProfileRepository`, `DietaryProfileRepository`, `ConsentRepository`, `AuditLogRepository`, và `IUserRepository`.
- **Luồng 1 (Mã hóa)**: Gọi `toEncryptedPhysicalProfile()` và `toEncryptedDietaryProfile()` từ DTO.
- **Luồng 2 (Update_At)**: Gán `setUpdatedAt(LocalDateTime.now())` (từ lớp `BaseEntity`) cho 2 Profile Entity trước khi save để tuân thủ lưu vết thời gian.
- **Luồng 3 (Consent)**: Lưu/Cập nhật bảng `CONSENT` (`consentStatus = true`, gán `User`).
- **Luồng 4 (Audit)**: Khởi tạo đối tượng `AuditLog`: `actionType = "UPDATE_HEALTH_PROFILE"`, `actorId = userId`, `timestamp = new Date()`. Lưu qua `AuditLogRepository`.

---

### 3. Tầng Controller Layer
Nhận Request Form từ Guest, Validate và điều hướng View.

#### [NEW] [ProfileController.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/controller/ProfileController.java)
- Tạo controller với `@RequestMapping("/profile")`.
- **GET `/profile/me`**: Lấy `userId`, gọi Service lấy `SensitiveProfileDto`, gán vào `Model` và trả về `"auth/profile"`.
- **POST `/profile/update`**: 
  - Nhận Submit từ form qua `@Valid @ModelAttribute("profileDto") SensitiveProfileDto dto`.
  - Nếu `!dto.isHasConsent()`, `return "auth/profile"` kèm `model.addAttribute("error", "MSG-03: Bạn phải đồng ý...")`.
  - Wrap trong khối `try-catch`, bắt Exception và trả về `MSG-15: Đã xảy ra lỗi hệ thống...` nếu lưu Database thất bại.
  - Nếu thành công, `return "redirect:/profile/me?success=true"`.

---

### 4. Tầng View (UI) Layer
Xây dựng giao diện nhập liệu y tế.

#### [NEW] [profile.html](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/auth/profile.html)
- Viết bằng Thymeleaf. Form liên kết với object qua `th:object="${profileDto}"`.
- Tách làm 2 khu vực: "Tình trạng Y tế" (`th:field="*{medicalConditions}"`) và "Chế độ Dinh dưỡng" (`th:field="*{dietaryPreference}"`).
- Checkbox Đồng ý: `<input type="checkbox" th:field="*{hasConsent}" />` (Tuyệt đối không checked mặc định).
- Khối báo lỗi: `<div th:if="${error}" class="alert alert-danger" th:text="${error}"></div>` để hiển thị MSG-03 và MSG-15.
