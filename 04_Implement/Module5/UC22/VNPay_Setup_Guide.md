# Hướng dẫn Thiết lập và Chạy thử VNPay Sandbox (Local & Ngrok)

Tài liệu này hướng dẫn cách cấu hình môi trường Local (máy tính cá nhân) để tích hợp thành công Cổng thanh toán VNPay mà không gặp lỗi "Không tìm thấy website" và đảm bảo bảo mật tuyệt đối cho Source Code.

---

## 1. Cơ chế Bảo mật Mã bí mật (Secret Management)

Hệ thống Aura Moon đã được cấu hình chuẩn bảo mật OWASP. **KHÔNG BAO GIỜ** ghi trực tiếp `vnp_TmnCode` và `vnp_HashSecret` vào file `application.properties` trên Git.

**Hướng dẫn dành cho thành viên nhóm khi Clone code về:**
1. Code kéo từ GitLab về sẽ không chạy được ngay vì thiếu mã VNPay.
2. Vào thư mục `src/main/resources`, tự tạo một file mới tên là **`application-secret.properties`**.
3. Điền 2 dòng mã VNPay Sandbox của nhóm vào file đó:
   ```properties
   vnp_TmnCode_Secret=1QVBJUD0
   vnp_HashSecret_Secret=VPB91PYW6LSRIJHIYZA9IUEPT0JLVRFA
   ```
*(Lưu ý: File này đã được đưa vào `.gitignore`, nên bạn cứ yên tâm lưu trữ trên máy mà không sợ bị push nhầm lên mạng).*

---

## 2. Lấy thông tin tài khoản VNPay Sandbox (Nếu chưa có)

1. Truy cập [https://sandbox.vnpayment.vn/devreg/](https://sandbox.vnpayment.vn/devreg/)
2. Điền thông tin đăng ký (Email, Tên website, URL tạm như `http://localhost:8080`).
3. Nhận Email từ VNPay chứa cặp mã `vnp_TmnCode` và `vnp_HashSecret`.
4. Điền vào file `application-secret.properties` như hướng dẫn ở Phần 1.

---

## 3. Chạy Ngrok để giả lập Internet (Bắt buộc)

VNPay không thể trả kết quả giao dịch về `localhost`. Bạn phải dùng Ngrok để tạo đường hầm public.

1. Khởi động ứng dụng Spring Boot (ấn Run trên IDE, chạy ở port 8080).
2. Tải và cài đặt Ngrok ([https://ngrok.com/](https://ngrok.com/)).
3. Mở Terminal / PowerShell gõ lệnh kết nối tài khoản (chỉ làm 1 lần):
   ```bash
   ngrok config add-authtoken <Mã_Token_Của_Bạn>
   ```
4. Khởi chạy Ngrok trỏ vào port của Spring Boot:
   ```bash
   ngrok http 8080
   ```
5. Ngrok sẽ sinh ra một Public URL (VD: `https://abcd-123.ngrok-free.app`). Giữ nguyên cửa sổ đen này không tắt.

---

## 4. Cấu hình Return URL trên VNPay Dashboard

Để VNPay cho phép thanh toán qua link Ngrok, bạn phải khai báo link này với VNPay.

1. Đăng nhập [https://sandbox.vnpayment.vn/merchantv2/](https://sandbox.vnpayment.vn/merchantv2/)
2. Vào phần **Quản lý tham số**.
3. Cập nhật ô **Return URL** thành địa chỉ Ngrok cộng thêm đuôi xử lý API.
   VD: `https://abcd-123.ngrok-free.app/billing/checkout/vnpay-return`
4. Lưu lại cấu hình.

---

## 5. Tiến hành Test Thanh Toán

1. Mở trình duyệt web.
2. Truy cập hệ thống của bạn bằng link Ngrok thay vì Localhost:
   `https://abcd-123.ngrok-free.app/billing/checkout?bookingId=1`
3. (Nếu Ngrok hiện trang cảnh báo màu xanh, bấm nút **Visit Site**).
4. Bấm "Thanh toán VNPay". Hệ thống lúc này sẽ tự động nhận diện Domain Ngrok thông qua các header `X-Forwarded-*` và chuyển hướng mượt mà sang cổng quét mã QR của VNPay.

---
*Lưu ý: Khi ứng dụng được đẩy lên Server thật (Production) có tên miền thật như `auramoon.com`, bạn không cần chạy Ngrok nữa. Chỉ việc đổi mã VNPay Sandbox thành mã thật trên Server là xong.*
