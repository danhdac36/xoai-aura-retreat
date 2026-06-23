# BÁO CÁO ĐÁNH GIÁ MÃ NGUỒN — UC08 CHECK IN GUEST

* **Mã Tài liệu**: `AURAMOON-BOOKING-REVIEW-UC08`
* **Ngày thực hiện**: 2026-06-21
* **Trạng thái**: Đã đối chiếu (Không tự ý sửa mã nguồn hệ thống)

---

## PHẦN 1: TỔNG HỢP CÁC LỖI LOGIC, BẢO MẬT & BẤT ĐỒNG BỘ PHÁT HIỆN

Dưới đây là các điểm lỗi bảo mật và sự không khớp trong code của bạn so với tài liệu [UC08_EDS_Check_In_Guest.md](file:///d:/su26-swp391-se2023-g6/04_Implement/Module%202/EDS/UC08_EDS_Check_In_Guest.md):

| STT | Thành phần ảnh hưởng | Vấn đề | Tác động thực tế | Mức độ nghiêm trọng |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **AesDataEncryptor.java** | Khóa mã hóa `SECRET_KEY = "AuraMoonEncrypt!"` được viết cứng (hardcoded) trong mã nguồn. | Vi phạm nghiêm trọng ADR-001 và chính sách bảo mật (không được commit key lên Git). Rò rỉ dữ liệu nhạy cảm nếu repo bị lộ. | **Nguy hiểm (Vulnerability)** |
| 2 | **AesDataEncryptor.java** | Sử dụng key dài 16 byte (128-bit) dẫn đến thuật toán mã hóa chạy ở chế độ **AES-128**. | Không tuân thủ yêu cầu pháp lý & SLA bảo mật bắt buộc của DPO/EDS là **AES-256** (yêu cầu key 32 byte). | **Nghiêm trọng (Compliance Failure)** |
| 3 | **CheckInController.java** | Map URL `@PostMapping("/checkin")` (không có dấu gạch) và `@GetMapping("/bookings")`. | Khác biệt so với đặc tả API chính thức của EDS (`POST /receptionist/check-in` và `GET /receptionist/check-in`). | **Thấp (Sai lệch EDS)** |
| 4 | **CheckInController.java** | Redirect thành công về `/receptionist/bookings`. | Không khớp với đặc tả của tài liệu thiết kế (Redirect về `/receptionist/dashboard?success=checkin&bookingId=...`). | **Thấp (Sai lệch EDS)** |

---

## PHẦN 2: HƯỚNG DẪN CÁCH SỬA CHI TIẾT (KHI BẠN CẦN SỬA)

Nếu bạn muốn tự chỉnh sửa code để khắc phục các vấn đề trên, hãy thực hiện các thay đổi sau:

### 1. Khắc phục lỗi hardcode key và nâng cấp lên AES-256
Để không commit key mã hóa lên Git, bạn nên nạp key từ file cấu hình `application.properties` hoặc biến môi trường `ENCRYPTION_KEY` và inject tĩnh vào Converter JPA.

* **File cần sửa**: [AesDataEncryptor.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/config/AesDataEncryptor.java)
* **Đề xuất sửa**:
  1. Thêm cấu hình trong `application.properties` (dùng key dài 32 ký tự để đạt chuẩn AES-256):
     ```properties
     # 32 characters key for AES-256
     app.encryption.key=AuraMoonRetreatWellnessSystem2026
     ```
  2. Cập nhật `AesDataEncryptor.java` nhận key từ Spring Context tĩnh (Static Injection):
     ```java
     @Converter
     public class AesDataEncryptor implements AttributeConverter<String, String> {
         private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
         private static String secretKey;

         @org.springframework.beans.factory.annotation.Value("${app.encryption.key:AuraMoonRetreatWellnessSystem2026}")
         public void setSecretKey(String key) {
             AesDataEncryptor.secretKey = key;
         }

         @Override
         public String convertToDatabaseColumn(String attribute) {
             if (attribute == null || attribute.isEmpty()) return null;
             try {
                 Cipher cipher = Cipher.getInstance(ALGORITHM);
                 SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
                 cipher.init(Cipher.ENCRYPT_MODE, key);
                 return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
             } catch (Exception e) {
                 throw new IllegalStateException("Lỗi mã hóa thông tin cá nhân nhạy cảm", e);
             }
         }

         @Override
         public String convertToEntityAttribute(String dbData) {
             if (dbData == null || dbData.isEmpty()) return null;
             try {
                 Cipher cipher = Cipher.getInstance(ALGORITHM);
                 SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
                 cipher.init(Cipher.DECRYPT_MODE, key);
                 return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
             } catch (Exception e) {
                 return dbData;
             }
         }
     }
     ```

---

### 2. Đồng bộ hóa Endpoint URL và đường dẫn Redirect
* **File cần sửa**: [CheckInController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/CheckInController.java)
* **Đề xuất sửa**:
  1. Đổi ánh xạ URL cho phương thức performCheckIn:
     ```java
     // Đổi dòng 36 từ:
     @PostMapping("/checkin")
     // Thành:
     @PostMapping("/check-in")
     ```
  2. Sửa lại redirect khi thành công:
     ```java
     // Đổi dòng 42 từ:
     return "redirect:/receptionist/bookings";
     ```
     Thành cấu hình Redirect chứa thông tin mã hóa Booking:
     ```java
     redirectAttributes.addAttribute("bookingId", request.getBookingId());
     return "redirect:/receptionist/dashboard";
     ```
  3. Cập nhật lại thẻ Form trong file giao diện [bookings.html](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/reception/bookings.html#L104) để trỏ đến đúng endpoint mới:
     ```html
     <form th:action="@{/receptionist/check-in}" method="post" th:object="${checkInRequest}">
     ```
