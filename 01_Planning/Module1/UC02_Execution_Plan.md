# Kế Hoạch Thực Thi (Execution Plan) - UC02

**Mục tiêu**: Hiện thực hóa UC02 (Cập nhật Hồ sơ Sức khỏe & Dinh dưỡng) dựa trên thiết kế kỹ thuật EDS v1.1 (Đã cập nhật theo chuẩn Spring MVC).

> [!IMPORTANT]
> Phần thông tin nhạy cảm bắt buộc phải được mã hóa trước khi lưu xuống Database và Form dữ liệu phải sử dụng ModelAttribute kèm BindingResult để xử lý lỗi trực tiếp trên View Thymeleaf.

## Open Questions (Cần bạn xác nhận trước khi code)
1.  Tôi không tìm thấy `PhysicalHealthProfileRepository` trong mã nguồn. Vì entity này thuộc module Spa (`com.AuraMoon.auramoon.spa.entity`), tôi sẽ khởi tạo Repository này tại package `com.AuraMoon.auramoon.spa.repository`. Bạn đồng ý chứ?
2.  Giao diện (View) của trang này sẽ dùng chung Layout Thymeleaf với các trang Auth khác (như trang Login/Register) và được đặt tại `src/main/resources/templates/auth/profile.html`. Có đúng với định hướng UI của dự án không?

## Các Bước Thực Thi (Proposed Changes)

---

### 1. Tầng Repository Layer
Chúng ta cần kho lưu trữ để làm việc với 2 bảng `PHYSICAL_HEALTH_PROFILE` và `DIETARY_PROFILE`.

#### [NEW] [PhysicalHealthProfileRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/spa/repository/PhysicalHealthProfileRepository.java)
- Tạo Interface kế thừa `JpaRepository<PhysicalHealthProfile, Integer>`.
- Thêm method `findByUserId(Integer userId)`.

#### [MODIFY] [DietaryProfileRepository.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/DietaryProfileRepository.java)
- Kiểm tra và thêm method `findByUserId(Integer userId)` nếu chưa có.

---

### 2. Tầng Service Layer
Xử lý logic mã hóa (gọi `SensitiveProfileDto`) và lưu xuống 2 repository trên.

#### [NEW] [IProfileService.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/IProfileService.java)
- Interface định nghĩa 2 hàm: 
  - `SensitiveProfileDto getSensitiveProfile(Integer userId)`
  - `void saveSensitiveProfile(SensitiveProfileDto inputDto)`

#### [NEW] [ProfileServiceImpl.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/ProfileServiceImpl.java)
- Triển khai logic implement `IProfileService`.
- Inject `PhysicalHealthProfileRepository` và `DietaryProfileRepository`.
- **Validation**: Nếu `dto.isHasConsent() == false` thì ném Exception (hoặc đẩy lỗi ở Controller).
- **Mã hóa**: Gọi `toEncryptedPhysicalProfile()` và `toEncryptedDietaryProfile()` từ DTO, nhận về Entity đã mã hóa trước khi gọi `repo.save()`.

---

### 3. Tầng Controller Layer
Nhận Request Form từ Guest, kiểm tra Authorization, Validate và điều hướng View (MVC).

#### [NEW] [ProfileController.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/controller/ProfileController.java)
- Tạo controller với `@RequestMapping("/profile")`.
- **GET `/profile/me`**: Lấy `userId` từ SecurityContextHolder, gọi Service lấy `SensitiveProfileDto`, gán vào `Model` (model.addAttribute("profileDto", dto)) và trả về view `"auth/profile"`.
- **POST `/profile/update`**: 
  - Nhận Submit từ form qua `@Valid @ModelAttribute("profileDto") SensitiveProfileDto dto`.
  - Cắt `BindingResult` để bắt lỗi (Ví dụ lỗi thiếu `hasConsent`). Nếu có lỗi, `return "auth/profile"` để hiển thị lỗi.
  - Nếu hợp lệ, gọi `profileService.saveSensitiveProfile(dto)` và `return "redirect:/profile/me?success=true"`.

---

### 4. Tầng View (UI) Layer
Xây dựng giao diện nhập liệu y tế.

#### [NEW] [profile.html](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/auth/profile.html)
- Viết bằng Thymeleaf. Form liên kết với object qua `th:object="${profileDto}"`.
- Tách làm 2 khu vực: "Tình trạng Y tế" (`th:field="*{medicalConditions}"`) và "Chế độ Dinh dưỡng" (`th:field="*{dietaryPreference}"`).
- Checkbox Đồng ý thu thập dữ liệu y tế: `<input type="checkbox" th:field="*{hasConsent}" />` (Tuyệt đối không checked mặc định).
- Hiển thị thông báo lỗi từ `BindingResult` màu đỏ ngay bên dưới các field nếu Submit lỗi.

---

## Kế hoạch Xác minh (Verification Plan)

### Kiểm tra bằng tay (Manual Verification)
1. Đăng nhập bằng một tài khoản GUEST.
2. Truy cập `/profile/me`, điền form (Ví dụ: Dị ứng "Hải sản", Chấn thương "Đau lưng") và tick đồng ý -> Bấm Lưu. Trình duyệt redirect về trang cũ kèm chữ Thành công.
3. Mở SQL Server / DB console, chạy lệnh `SELECT * FROM PHYSICAL_HEALTH_PROFILE` và `SELECT * FROM DIETARY_PROFILE` để xác nhận dữ liệu đã lưu dạng Base64 Ciphertext.
4. Refresh lại trang `/profile/me`, dữ liệu "Hải sản", "Đau lưng" hiển thị bình thường trong Form (đã giải mã thành công).
5. Thử bỏ tick checkbox đồng ý và Submit lại, hệ thống sẽ giữ nguyên trang hiện tại và báo lỗi đỏ ở checkbox "Bạn phải đồng ý với điều khoản".
