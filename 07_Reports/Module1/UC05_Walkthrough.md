# Walkthrough: Delete Sensitive Health Data (UC-05)

Hệ thống đã triển khai thành công chức năng cho phép Guest (khách hàng) xoá vĩnh viễn dữ liệu nhạy cảm liên quan đến sức khoẻ và dị ứng của họ để tuân thủ quyền riêng tư (Right to be forgotten).

## Những thay đổi đã thực hiện

### 1. Xử lý Backend (Service & Controller)
- **Hard Delete dữ liệu**: Thêm phương thức `deleteSensitiveProfile(Integer userId)` trong `ProfileServiceImpl`. Phương thức này thực hiện tìm và xoá hoàn toàn các bản ghi trong hai bảng `Physical_Health_Profile` và `Dietary_Profile`.
- **Lưu vết (Audit Trail)**:
  - Cập nhật các bản ghi trong bảng `Consent` của người dùng thành `consentStatus = false` thay vì xoá để giữ lại bằng chứng (audit trail) rằng khách đã rút lại sự đồng ý.
  - Ghi nhận hành động vào bảng `Audit_Log` với mã `DELETE_HEALTH_PROFILE` cùng mô tả chi tiết, tuân thủ yêu cầu lưu vết.
- **API Endpoint**: Thêm `@PostMapping("/health/delete")` trong `ProfileController` để nhận request từ client và redirect về lại trang hồ sơ kèm theo thông báo thành công.

### 2. Giao diện người dùng (UI)
- **Nút xoá cảnh báo**: Đã thêm một khu vực **"Quản lý Quyền riêng tư"** ở dưới cùng trang `update-health-profile.html` với viền đỏ và nút **"XÓA DỮ LIỆU NHẠY CẢM"** (màu đỏ - danger state).
- **Hộp thoại xác nhận**: Khi người dùng nhấn nút xoá, một alert mặc định của trình duyệt sẽ hiện ra yêu cầu xác nhận lần cuối: *"Bạn có chắc chắn muốn xoá vĩnh viễn toàn bộ dữ liệu sức khoẻ và dị ứng không? Hành động này không thể hoàn tác."*.
- **Thông báo thành công**: Thêm logic xử lý URL param `?deleted=true` ở Controller để hiển thị thông báo "Dữ liệu nhạy cảm đã được xóa thành công!" màu xanh lá (success state) trên giao diện.

## Xác minh
- Log vào bằng một tài khoản có lưu dữ liệu sức khoẻ.
- Di chuyển xuống cuối trang `Hồ sơ sức khỏe & dinh dưỡng`.
- Nhấn nút xoá, đồng ý với cảnh báo.
- Trang sẽ reload và hiển thị thông báo thành công, đồng thời các trường form dữ liệu sức khoẻ và dị ứng sẽ tự động trống (vì đã bị xoá vật lý trong DB).
