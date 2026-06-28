th.êm 1 trang mới my-account.html để làm điều đấy  hãy làm cho tôi 1 trang để user có thể xem được thông tin, trạng thái tài khoản của mình

# Chức năng Đổi Mật khẩu (Change Password)

Tạo chức năng cho phép tất cả các loại tài khoản (Role) đều có thể thay đổi mật khẩu của chính mình thông qua giao diện cá nhân (`/profile`).

## Proposed Changes

### DTO (Data Transfer Object)

- **[NEW]** `ChangePasswordDto.java`: Chứa các trường `currentPassword`, `newPassword`, `confirmPassword`. Có các annotation validation để đảm bảo mật khẩu mới đủ độ phức tạp và khớp với confirm.

### Service Layer

- **[MODIFY]** `IProfileService.java`: Thêm phương thức `void changePassword(Integer userId, ChangePasswordDto dto)`.
- **[MODIFY]** `ProfileServiceImpl.java`:
  - Triển khai `changePassword`.
  - Kiểm tra mật khẩu hiện tại bằng `PasswordEncoder`.
  - Kiểm tra mật khẩu mới không trùng mật khẩu cũ.
  - Cập nhật mật khẩu mới (đã mã hóa) và lưu lại (gồm cả Audit Log).

### Controller Layer

- **[MODIFY]** `ProfileController.java`:
  - Thêm `@GetMapping("/change-password")` để hiển thị trang đổi mật khẩu.
  - Thêm `@PostMapping("/change-password")` để nhận form và xử lý đổi mật khẩu thông qua `IProfileService`.

### Views (HTML)

- **[NEW]** `auth/change-password.html`: Giao diện thay đổi mật khẩu, đồng bộ với thiết kế hiện tại (giống trang Profile).
- **[MODIFY]** `sidebar.html`: Thêm 1 menu mục "Change Password" (hoặc "Đổi Mật khẩu") dưới phần **Account** bên cạnh "My Profile".

## Verification Plan

### Manual Verification

- Đăng nhập bằng một tài khoản (vd Admin hoặc Guest).
- Chuyển tới mục Đổi mật khẩu.
- Thử nhập sai mật khẩu hiện tại -> Báo lỗi.
- Thử nhập mật khẩu mới và xác nhận không khớp -> Báo lỗi.
- Đổi mật khẩu thành công -> Chuyển hướng với thông báo thành công.
- Đăng xuất và đăng nhập lại bằng mật khẩu mới để xác nhận.
