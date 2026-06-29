# Kế hoạch thực thi Use Case: Quên Mật Khẩu (Forgot Password)

Tính năng này cho phép người dùng khôi phục tài khoản bằng cách hệ thống tự động sinh ra một mật khẩu 6 ký tự ngẫu nhiên, mã hóa mật khẩu này vào Database và gửi phiên bản rõ (plain text) qua email cho người dùng.

## Câu hỏi mở (Open Questions)
- Nội dung Email mẫu (Subject và Body) cho email cấp lại mật khẩu mà mình tự thiết kế đã phù hợp chưa, hay bạn có yêu cầu nội dung cụ thể nào khác không?

## Các thay đổi đề xuất (Proposed Changes)

---

### Backend Auth Module

#### [MODIFY] [IAuthService.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/IAuthService.java)
- Thêm khai báo phương thức: `void resetPassword(String email);`

#### [MODIFY] [AuthServiceImpl.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/service/impl/AuthServiceImpl.java)
- Implement phương thức `resetPassword(String email)` với annotation `@Transactional` để đảm bảo Rollback nếu gửi mail lỗi.
- Viết hàm phụ trợ `generateRandomString(int length)` sử dụng `SecureRandom` để sinh đúng 6 ký tự.
- **Quy trình:**
  1. Gọi `userRepository.findByEmail(email)`. Nếu `null` ném ra `IllegalArgumentException("Email không tồn tại trong hệ thống, vui lòng kiểm tra lại.")`.
  2. Sinh mật khẩu mới, băm bằng `passwordEncoder.encode()`.
  3. Cập nhật `user.setPasswordHash()` và `save(user)`.
  4. Gọi `mailSender.send()` để gửi email. Bắt lỗi `MailException` để ném `IllegalStateException` kích hoạt Rollback.

#### [MODIFY] [AuthController.java](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/controller/AuthController.java)
- Thêm phương thức GET `/auth/forgot-password`: Khởi tạo model và trả về view `auth/forgot-password`.
- Thêm phương thức POST `/auth/forgot-password`:
  - Gọi `authService.resetPassword(email)`.
  - Nếu thành công: `return "redirect:/auth/login?resetSuccess=true";`
  - Nếu bắt được Exception (Email không tồn tại, lỗi gửi mail): gán lỗi vào model và quay lại trang form.

---

### Giao Diện (Frontend)

#### [NEW] [forgot-password.html](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/auth/forgot-password.html)
- Tạo file HTML mới trong `src/main/resources/templates/auth/`.
- Thiết kế một Form nhập `email` và nút Submit "Khôi phục mật khẩu".
- Hiển thị thông báo lỗi nếu có (`th:if="${error}"`).
- Đảm bảo có token CSRF an toàn.
- Sử dụng layout có sẵn của hệ thống (copy khung từ file `login.html`).

#### [MODIFY] [login.html](file:///d:/Learning/SWP391/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/auth/login.html)
- Cập nhật hiển thị Flash message thông báo lấy mật khẩu thành công khi URL có query parameter `?resetSuccess=true`.
- Cập nhật URL thẻ `<a>` ở dòng chữ "Quên mật khẩu?" để chuyển hướng đến `/auth/forgot-password`.

---

## Kế hoạch Xác minh (Verification Plan)

### Automated Tests
- Chạy `mvn clean test-compile` để xác minh không có lỗi biên dịch.

### Manual Verification
- Gửi yêu cầu kiểm tra tới `/auth/forgot-password` bằng một email hợp lệ để xác nhận Mật khẩu trong DB đã thay đổi đúng dạng BCrypt.
- Xác nhận gửi một email không tồn tại sẽ hiển thị đúng lỗi trên màn hình.
