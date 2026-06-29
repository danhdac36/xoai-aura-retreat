# Delete Sensitive Health Data (UC-05)

Triển khai Use Case 05 theo chuẩn của SRS (Module 1 - Data Privacy Management). Chức năng này cho phép Guest (khách hàng) thực hiện xoá vĩnh viễn dữ liệu nhạy cảm liên quan đến sức khoẻ và dị ứng (Physical Health Profile, Dietary Profile) ra khỏi hệ thống để tuân thủ quyền riêng tư (Right to be forgotten).

> [!IMPORTANT]
> **User Review Required**
> Vui lòng kiểm tra và phản hồi các câu hỏi mở bên dưới để thống nhất cách triển khai trước khi tôi bắt đầu viết code.

## Open Questions

> [!WARNING]
> 1. **Điều kiện xoá (After the stay)**: Theo mô tả trong SRS, khách hàng yêu cầu xoá dữ liệu "after the stay". Hệ thống có cần kiểm tra xem khách có Booking nào vừa hoàn thành (Completed) hay không trước khi cho phép xoá? Hay khách có thể chủ động xoá bất cứ lúc nào họ muốn (miễn là họ tự nguyện)? <Trả lời: Khách phải hoàn tất thanh toán, booking phải hoàn thành>
> 2. **Xử lý bảng Consent**: Dữ liệu sức khoẻ (`Physical_Health_Profile`, `Dietary_Profile`) sẽ bị xoá vĩnh viễn (Hard delete). Tuy nhiên, với bảng `Consent` (Sự đồng thuận), chúng ta nên xoá hoàn toàn (Hard delete) hay chỉ đổi trạng thái `consentStatus = false` để giữ lại làm bằng chứng (Audit trail) cho việc khách đã từng đồng ý và sau đó rút lại? <Trả lời: bảng consent sẽ đổi trạng thái `consentStatus = false`>

## Proposed Changes

### 1. Service Layer

#### [MODIFY] `IProfileService.java` & `ProfileServiceImpl.java`

- Thêm phương thức `void deleteSensitiveProfile(Integer userId)`.
- **Logic xử lý**:
  - Tìm và thực hiện **Hard Delete** (xoá vật lý) trên repository của `PhysicalHealthProfile` và `DietaryProfile` của userId tương ứng.
  - Xử lý bảng `Consent` (xoá hoặc set về false tuỳ theo quyết định từ Open Questions).
  - Ghi nhận `AuditLog` với actionType là `DELETE_HEALTH_PROFILE` để đảm bảo tuân thủ truy vết (BR-15).

---

### 2. Controller Layer

#### [MODIFY] `ProfileController.java`

- Thêm API endpoint `@PostMapping("/health/delete")`.
- Xác thực người dùng hiện tại (extractUserId).
- Gọi hàm xoá từ Service.
- Redirect về lại trang `/profile/health` với thông báo thành công (ví dụ: `?deleted=true`).

---

### 3. View (UI) Layer

#### [MODIFY] `update-health-profile.html`

- Thêm một tuỳ chọn / nút bấm (Button) có giao diện cảnh báo (màu đỏ - Danger) ở cuối trang với nội dung: **"Xóa dữ liệu nhạy cảm"**.
- Nút bấm này sẽ gọi một form POST đến `/profile/health/delete`.
- Thêm một Modal hoặc Alert Confirmation (Xác nhận) bằng Javascript trước khi người dùng thực sự xoá, vì đây là hành động không thể phục hồi (Permanent deletion).
- Bổ sung thông báo (Success Message) trên UI khi dữ liệu được xoá thành công.

## Verification Plan

### Manual Verification

1. Đăng nhập bằng tài khoản Guest, truy cập vào trang "Hồ sơ sức khỏe & dinh dưỡng".
2. Điền thông tin, check đồng ý và lưu hồ sơ thành công (dữ liệu vào DB).
3. Bấm vào nút "Xóa dữ liệu nhạy cảm", xác nhận hộp thoại.
4. Kiểm tra trên UI xem thông tin đã được làm trắng hay chưa.
5. Kiểm tra trực tiếp trong DB (`Physical_Health_Profile`, `Dietary_Profile`) để đảm bảo các record đã thực sự bị xoá.
6. Kiểm tra bảng `Audit_Log` xem sự kiện `DELETE_HEALTH_PROFILE` đã được ghi nhận đúng chuẩn chưa.
