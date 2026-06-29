# Báo Cáo Phân Tích Luồng Xử Lý (Execution Flow Report) - UC02

**Ngày tạo:** 2026-06-23
**Mục tiêu:** Diễn giải chi tiết đường đi của dữ liệu từ giao diện người dùng xuống tới cơ sở dữ liệu dựa trên mã nguồn đã thiết kế tại file `UC02_Implementation_Code.md`.

Hệ thống có 2 luồng (flow) chính: Luồng tải trang hiển thị thông tin và Luồng lưu cập nhật thông tin.

---

## 1. Luồng 1: Tải giao diện và Giải mã hồ sơ (View Profile)
*Luồng này kích hoạt khi người dùng (Guest) truy cập trang Hồ sơ hoặc tải lại trang.*

1. **Guest (Browser) -> Controller:** 
   - Guest thực hiện request `GET /profile/me`.
   - Lớp `ProfileController` bắt request tại phương thức `viewProfile()`.

2. **Controller -> Spring Security:**
   - Tại phương thức `extractUserId(authentication)`, hệ thống trích xuất thông tin User đang đăng nhập từ `UserDetailsResponse` để lấy ra `userId`. Nếu chưa đăng nhập, redirect về trang login.

3. **Controller -> Service:**
   - Controller gọi phương thức `profileService.getSensitiveProfile(userId)` truyền xuống tầng Service (`ProfileServiceImpl`).

4. **Service -> Repository (DB):**
   - Lớp `ProfileServiceImpl` gọi `physicalHealthProfileRepository.findByUserId(userId)` và `dietaryProfileRepository.findByUserId(userId)` để truy vấn hai bản ghi từ DB (lúc này dữ liệu vẫn đang ở dạng chuỗi Base64 mã hóa AES).

5. **Service -> DTO (Giải mã):**
   - Hai Entity vừa lấy lên được truyền vào phương thức static `SensitiveProfileDto.fromEntities(phys, diet)`.
   - Tại đây, DTO gọi qua lớp `AesDataEncryptor` để sử dụng hàm `convertToEntityAttribute()`, thực hiện **Giải mã (Decrypt)** các trường nhạy cảm (`medicalConditions`, `injuries`, v.v.) về lại văn bản rõ (plaintext).
   - *Quy tắc BR-08:* Thuộc tính `hasConsent` được cố tình set cứng bằng `false` để checkbox trên giao diện luôn trống.

6. **Controller -> View (Thymeleaf):**
   - Controller nhận lại object `SensitiveProfileDto` (đã giải mã) và đẩy vào `Model` qua lệnh `model.addAttribute("profileDto", dto)`.
   - Controller trả về tên View `auth/profile`. Thymeleaf sẽ render file `profile.html` kết hợp với dữ liệu DTO và gửi HTML hoàn chỉnh về lại cho Browser.

---

## 2. Luồng 2: Cập nhật, Mã hóa và Lưu trữ (Update Profile)
*Luồng này kích hoạt khi người dùng điền Form và bấm nút "Lưu Hồ Sơ".*

1. **Guest (Browser) -> Controller:**
   - Form HTML POST dữ liệu lên đường dẫn `/profile/update`.
   - Lớp `ProfileController` bắt request tại phương thức `updateProfile()`.
   - Dữ liệu từ form tự động binding vào object `SensitiveProfileDto` nhờ annotation `@ModelAttribute`. Annotation `@Valid` tiến hành kiểm tra tính hợp lệ sơ bộ.

2. **Controller (Validation Nội bộ):**
   - Controller kiểm tra `if (!dto.isHasConsent())`. Nếu Guest chưa tick đồng ý, nó chặn lại ngay lập tức và gắn thông báo lỗi `MSG-03`. Lập tức trả về lại view `auth/profile` kèm lỗi đỏ.

3. **Controller -> Service:**
   - Nếu hợp lệ, Controller trích xuất `userId` và gọi phương thức `profileService.saveSensitiveProfile(dto, userId)`.

4. **Service -> DTO (Mã hóa) -> Repository (Sức khỏe & Dinh dưỡng):**
   - `ProfileServiceImpl` lấy Entity PhysicalHealthProfile cũ (nếu có).
   - Truyền Entity vào hàm `dto.toEncryptedPhysicalProfile(phys)`. Tại đây, DTO gọi `AesDataEncryptor.convertToDatabaseColumn()` để **Mã hóa (Encrypt)** các đoạn text do user nhập thành Base64 Ciphertext.
   - Hàm `phys.setUpdatedAt(LocalDateTime.now())` được gọi để ghi dấu thời gian cập nhật.
   - Gọi `physicalHealthProfileRepository.save(phys)` để lưu trữ xuống DB.
   - *Luồng tương tự diễn ra song song cho thông tin `DietaryProfile`.*

5. **Service -> Repository (Ghi nhận Consent):**
   - Service khởi tạo một object `Consent` mới (`consentStatus = true`, `consentVersion = "1.0"`).
   - Gọi `consentRepository.save(consent)` để xác nhận sự đồng ý hợp pháp của user.

6. **Service -> Repository (Ghi nhận Audit Log):**
   - Tuân thủ quy tắc BR-15, Service khởi tạo đối tượng `AuditLog` với các tham số: `actionType = "UPDATE_HEALTH_PROFILE"`, người thao tác (`actorId`), và thời gian (`timestamp`).
   - Gọi `auditLogRepository.save(log)` để lưu vết vào hệ thống thanh tra (Audit).

7. **Service -> Controller -> Browser (Thành công):**
   - Sau khi 4 thao tác ghi DB trên thành công mỹ mãn trong 1 `Transaction`, Service hoàn tất và trả quyền điều khiển về Controller.
   - Controller thực hiện lệnh `return "redirect:/profile/me?success=true"`.
   - Browser nhận được mã 302 Redirect, lập tức gọi lại Luồng 1 (phía trên) nhưng có mang thêm tham số `success=true` để in ra dòng chữ báo xanh "Cập nhật thành công".
   - *Trường hợp lỗi mạng/DB Exception E2:* Khối `try/catch` ở Controller sẽ tóm lấy lỗi, gán mã `MSG-15` và load lại view để user biết.
