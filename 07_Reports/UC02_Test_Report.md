# Báo cáo Hướng dẫn Kiểm thử (Test Report Guide) - UC02

**Mục tiêu:** Hướng dẫn chi tiết cách thức kiểm thử thực tế và tự động cho Use Case 02 (Hoàn thiện hồ sơ sức khỏe & dinh dưỡng cá nhân) sau khi đã triển khai mã nguồn thành công.

---

## 1. Môi trường & Điều kiện tiên quyết (Prerequisites)

- Ứng dụng Spring Boot đã chạy (`mvn spring-boot:run` hoặc chạy từ IDE).
- Một tài khoản kiểm thử thuộc nhóm quyền `GUEST` (ví dụ: email `guest@auramoon.com`, password `Password123`).
- Dữ liệu cơ sở dữ liệu (Database) rỗng hoặc đã có sẵn bản ghi hồ sơ cũ để kiểm tra tính năng hiển thị/ghi đè.

---

## 2. Các Kịch bản Kiểm thử Thủ công (Manual Test Cases)

### Kịch bản 1: Kiểm tra tải giao diện & Giải mã hồ sơ (View Profile)
*Kiểm tra khả năng giải mã dữ liệu cũ và hiển thị thông tin rõ (plaintext) lên màn hình.*

- **Bước 1:** Đăng nhập vào hệ thống với tài khoản GUEST.
- **Bước 2:** Truy cập trực tiếp đường dẫn: `http://localhost:8080/profile/me`.
- **Bước 3 (Kiểm tra kết quả):**
  - Giao diện "Hồ Sơ Sức Khỏe & Dinh Dưỡng" hiển thị với thiết kế sang trọng, tối ưu cho thương hiệu AuraMoon.
  - Các ô nhập dữ liệu: *Tình trạng y tế, Chấn thương, Dị ứng thực phẩm, Sở thích ăn kiêng* phải hiển thị đúng văn bản thô dạng dễ đọc (nếu DB đã lưu dạng mã hóa từ trước).
  - **Quy tắc BR-08:** Ô checkbox *"Tôi đồng ý cho phép Xoài Aura Retreat thu thập..."* phải **LUÔN LUÔN BỊ BỎ TRỐNG (Unchecked)** mỗi lần tải trang, bất kể lần trước user đã chọn hay chưa.

---

### Kịch bản 2: Kiểm tra Chặn Lưu khi chưa Đồng ý Bảo mật (Validation MSG-03)
*Kiểm tra nghiệp vụ buộc người dùng chấp nhận chính sách thu thập dữ liệu y tế nhạy cảm.*

- **Bước 1:** Tại trang `http://localhost:8080/profile/me`, nhập thử một vài thông tin mới vào các trường text.
- **Bước 2:** Đảm bảo ô checkbox đồng ý dữ liệu **không được chọn**.
- **Bước 3:** Nhấn nút **"Lưu Hồ Sơ Sức Khỏe"**.
- **Bước 4 (Kiểm tra kết quả):**
  - Trang không được chuyển hướng (đứng yên tại chỗ).
  - Xuất hiện thông báo lỗi màu đỏ nổi bật: `MSG-03: Bạn phải đồng ý với các điều khoản bảo mật dữ liệu y tế để tiếp tục`.
  - Các dữ liệu vừa nhập ở các ô text không bị mất đi (được giữ lại nhờ Model Binding).

---

### Kịch bản 3: Cập nhật hồ sơ thành công (Successful Update & Redirect)
*Kiểm tra luồng xử lý ghi nhận dữ liệu thành công.*

- **Bước 1:** Nhập thông tin y tế và chế độ ăn tùy ý.
- **Bước 2:** **Đánh dấu chọn (Tick)** vào ô checkbox đồng ý dữ liệu.
- **Bước 3:** Nhấn nút **"Lưu Hồ Sơ Sức Khỏe"**.
- **Bước 4 (Kiểm tra kết quả):**
  - Hệ thống chuyển hướng (redirect) về lại trang `http://localhost:8080/profile/me?success=true`.
  - Có thông báo màu xanh lá cây báo thành công: `Hồ sơ sức khỏe & dinh dưỡng đã được lưu thành công!`.
  - Ô checkbox đồng ý quay về trạng thái **Unchecked** (BR-08).

---

### Kịch bản 4: Kiểm tra tính toàn vẹn dữ liệu trong DB (Database Check)
*Kiểm tra dữ liệu được ghi vào DB có bị lộ plaintext hay không và các bản ghi Audit/Consent có khớp hay không.*

Sau khi chạy **Kịch bản 3** thành công, hãy truy vấn cơ sở dữ liệu bằng công cụ DB (H2 Console / DBeaver / SQL Server Management Studio) để xác minh 4 điểm sau:

1. **Bảng `PHYSICAL_HEALTH_PROFILE`:**
   - Dữ liệu cột `medical_conditions` và `injuries` phải lưu dưới dạng chuỗi Base64 mã hóa AES (ví dụ: `4H/Uq1Xo...`), không được chứa plaintext `"Tiểu đường"`, `"Đau lưng"`.
   - Cột `update_at` phải ghi nhận thời gian vừa lưu.

2. **Bảng `DIETARY_PROFILE`:**
   - Dữ liệu cột `food_allergies` và `diatary_preference` cũng phải mã hóa tương tự dạng Base64.
   - Cột `update_at` cập nhật thời gian chính xác.

3. **Bảng `CONSENT`:**
   - Xuất hiện 1 dòng bản ghi mới gắn với `user_id` của Guest.
   - Cột `consent_status` có giá trị `1` (true).
   - Cột `consent_version` lưu đúng `"1.0"`.

4. **Bảng `AUDIT_LOG`:**
   - Xuất hiện 1 dòng bản ghi audit mới có `action_type` = `"UPDATE_HEALTH_PROFILE"`.
   - Cột `actor_id` tương ứng với `user_id` của Guest.
   - Cột `details` lưu: `"Guest updated health and dietary profile"`.

---

### Kịch bản 5: Xử lý ngoại lệ hệ thống (System Error MSG-15)
*Kiểm tra lỗi hệ thống (ví dụ: mất kết nối DB khi đang lưu).*

- **Mô phỏng lỗi:** Trong lúc chạy thử nghiệm, bạn có thể tạm dừng dịch vụ DB hoặc sửa mã nguồn để ném ra một ngoại lệ tùy ý (`RuntimeException`) trong `ProfileServiceImpl.saveSensitiveProfile()`.
- **Thao tác:** Tick chọn đồng ý và nhấn nút **"Lưu Hồ Sơ Sức Khỏe"**.
- **Kiểm tra kết quả:**
  - Hệ thống bắt được Exception, không bị crash trang lỗi trắng (Whitelabel Error Page).
  - Tải lại trang profile kèm thông báo lỗi: `MSG-15: Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.`.
