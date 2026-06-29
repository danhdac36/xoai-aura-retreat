# Đặc tả Use Case (Use Case Specification): Quên Mật Khẩu (Forgot Password)

## 1. Thông tin chung

- **Tên Use Case:** Quên mật khẩu
- **Actor (Người dùng):** Người dùng chưa đăng nhập (Guest, Staff, Admin) đã có tài khoản trên hệ thống.
- **Mục tiêu:** Cho phép người dùng lấy lại quyền truy cập vào tài khoản khi quên mật khẩu bằng cách hệ thống tự động cấp phát và gửi mật khẩu mới qua email.

## 2. Tiền điều kiện (Pre-conditions)

- Người dùng chưa đăng nhập vào hệ thống.
- Người dùng đã cung cấp và sở hữu địa chỉ email hợp lệ liên kết với tài khoản tại hệ thống.

## 3. Hậu điều kiện (Post-conditions)

- **Thành công:** Mật khẩu của người dùng được hệ thống tự động reset thành một chuỗi ngẫu nhiên 6 ký tự. Người dùng nhận được mật khẩu này qua email và có thể dùng nó để đăng nhập ngay lập tức.
- **Thất bại:** Mật khẩu cũ không thay đổi nếu email không tồn tại hoặc hệ thống gửi email thất bại.

## 4. Các luồng sự kiện (Flow of Events)

### 4.1. Luồng cơ bản (Normal Flow)

1. Trên trang Đăng nhập, người dùng bấm vào liên kết **"Quên mật khẩu?"** (Forgot Password).
2. Hệ thống chuyển hướng đến trang Quên mật khẩu và yêu cầu người dùng nhập **Email** đã đăng ký.
3. Người dùng nhập Email và bấm nút **"Khôi phục mật khẩu"**.
4. Hệ thống kiểm tra Email có tồn tại trong cơ sở dữ liệu hay không.
5. Nếu Email hợp lệ, hệ thống tự động tạo ra một mật khẩu mới ngẫu nhiên gồm đúng **6 ký tự bất kỳ** (có thể bao gồm chữ và số).
6. Hệ thống tiến hành mã hóa (hash) mật khẩu 6 ký tự này và cập nhật đè lên mật khẩu cũ trong cơ sở dữ liệu.
7. Hệ thống gửi một email chứa **mật khẩu mới (dạng rõ - plain text)** vừa được tạo đến địa chỉ email của người dùng.
8. Tại trang quên mật khẩu hiển thị thông báo: "Mật khẩu mới đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư."
9. Người dùng mở email, lấy mật khẩu mới gồm 6 ký tự và đăng nhập thành công vào hệ thống.

### 4.2. Luồng ngoại lệ (Alternative/Exception Flows)

- **E1: Email không tồn tại trong hệ thống:**
  - Tại bước 4, hệ thống phát hiện email không khớp với bất kỳ tài khoản nào.
  - Hệ thống báo lỗi trực tiếp trên màn hình: "Email không tồn tại trong hệ thống, vui lòng kiểm tra lại." (Hoặc sử dụng thông báo chung chung nếu muốn bảo mật chống dò email).
- **E2: Lỗi hệ thống khi gửi Email:**
  - Tại bước 7, nếu dịch vụ gửi email (ví dụ SendGrid/SMTP) bị lỗi không thể gửi được.
  - Hệ thống *không* cập nhật mật khẩu mới vào cơ sở dữ liệu (rollback transaction).
  - Hệ thống báo lỗi: "Đã xảy ra sự cố khi gửi email khôi phục. Vui lòng thử lại sau."

## 5. Quy tắc nghiệp vụ (Business Rules)

- **BR-1 (Định dạng mật khẩu tự tạo):** Hệ thống chỉ tạo đúng 6 ký tự ngẫu nhiên (ví dụ: `A7x9B2`, `qwerty`, tuỳ thuộc vào hàm random). Độ dài này ghi đè lên quy tắc mật khẩu tối thiểu 8 ký tự thông thường khi tạo mới.
- **BR-2 (Khuyến cáo đổi mật khẩu):** Mật khẩu tự sinh qua email có mức độ bảo mật không cao và dễ bị lộ. Người dùng nên được hệ thống khuyến cáo thay đổi lại mật khẩu cá nhân an toàn hơn ngay trong lần đăng nhập đầu tiên sau khi khôi phục.
- **BR-3 (Gửi email):** Mật khẩu rõ (plain text) chỉ được lưu tạm thời trên RAM để gửi email, tuyệt đối không được ghi log dạng rõ ra file hoặc lưu dạng plain text vào cơ sở dữ liệu.
