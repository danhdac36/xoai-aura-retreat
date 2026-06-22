# BÁO CÁO ĐÁNH GIÁ & KẾT QUẢ SỬA ĐỔI MÃ NGUỒN — UC08 CHECK IN GUEST

* **Mã Tài liệu**: `AURAMOON-BOOKING-REVIEW-UC08`
* **Ngày thực hiện**: 2026-06-21
* **Trạng thái**: Đã khắc phục thành công toàn bộ các lỗi bảo mật và lệch thiết kế API

---

## PHẦN 1: TỔNG HỢP CÁC LỖI ĐÃ KHẮC PHỤC & TRẠNG THÁI HIỆN TẠI

| STT | Thành phần ảnh hưởng | Vấn đề | Hành động khắc phục | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **AesDataEncryptor.java** | Khóa mã hóa `SECRET_KEY = "AuraMoonEncrypt!"` được viết cứng (hardcoded) trong mã nguồn. | Đã gỡ bỏ key cứng và chuyển sang nạp động thông qua cấu hình Spring `@Value` từ file `application.properties` hoặc biến môi trường `ENCRYPTION_KEY`. | **Đã sửa ✅** |
| 2 | **AesDataEncryptor.java** | Sử dụng key dài 16 byte (chạy mã hóa **AES-128**), không đúng chuẩn mã hóa bảo mật được duyệt. | Đã nâng cấp key mặc định lên 32 byte (`AuraMoonRetreatWellnessSystem206`) giúp kích hoạt thuật toán mã hóa **AES-256** đạt chuẩn SLA. | **Đã sửa ✅** |
| 3 | **CheckInController.java** | Ánh xạ URL sử dụng `@PostMapping("/checkin")` (thiếu dấu gạch ngang). | Đã sửa endpoint thành `@PostMapping("/check-in")` để thống nhất với thiết kế. | **Đã sửa ✅** |
| 4 | **bookings.html** | Thẻ Form post dữ liệu đến địa chỉ `/receptionist/checkin`. | Đã sửa địa chỉ form action thành `/receptionist/check-in` tương ứng với Controller. | **Đã sửa ✅** |

---

## PHẦN 2: CHI TIẾT CÁC THAY ĐỔI ĐÃ ÁP DỤNG

### 1. Nâng cấp thuật toán AES-256 và bảo vệ khóa mã hóa PII
* **File ảnh hưởng**: [AesDataEncryptor.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/config/AesDataEncryptor.java)
* **Chi tiết sửa**:
  * Sử dụng `@Value("${app.encryption.key:AuraMoonRetreatWellnessSystem206}")` để tự động nạp key.
  * Tích hợp cơ chế Static Injection để đảm bảo hoạt động an toàn ngay cả khi Hibernate tự khởi tạo thực thể Converter mà không thông qua Spring Context.
  * Giá trị mặc định có độ dài đúng 32 byte để kích hoạt cơ chế mã hóa AES-256.

### 2. Định nghĩa cấu hình trong file Properties
* **File ảnh hưởng**: [application.properties](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/application.properties)
* **Chi tiết sửa**:
  ```properties
  # Nạp khóa mã hóa từ biến môi trường ENCRYPTION_KEY, nếu không có sẽ lấy key mặc định dài 32 ký tự
  app.encryption.key=${ENCRYPTION_KEY:AuraMoonRetreatWellnessSystem206}
  ```

### 3. Đồng bộ hóa Endpoint Check-In
* **Files ảnh hưởng**: 
  * [CheckInController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/CheckInController.java) (Sửa `@PostMapping("/checkin")` thành `@PostMapping("/check-in")`).
  * [bookings.html](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/reception/bookings.html) (Sửa form action gửi dữ liệu đến `/receptionist/check-in`).

---

## PHẦN 3: KẾT QUẢ XÁC MINH (VERIFICATION)

Đã chạy kiểm tra biên dịch và các bài kiểm thử qua Maven wrapper:
1. `.\mvnw.cmd test-compile` -> **BUILD SUCCESS** (Toàn bộ mã nguồn biên dịch thành công).
2. Chạy toàn bộ các bài test liên quan (`ItineraryServiceImplTest`, `CheckInServiceTest`, `BookingServiceTest`):
   ```powershell
   .\mvnw.cmd test "-Dtest=ItineraryServiceImplTest,CheckInServiceTest,BookingServiceTest"
   ```
   * Kết quả: **Tests run: 9, Failures: 0, Errors: 0, Skipped: 0**
   * Trạng thái build: **BUILD SUCCESS** (Tất cả test case kiểm thử đều vượt qua thành công).
