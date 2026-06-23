# QUY TẮC PHÁT TRIỂN KỸ THUẬT CHO AI (AI RULES)
## Dự án: Xoai Aura Retreat & Spa Management System

---

## 1. Tiêu Chuẩn Viết Code Java (Backend)

*   **Kiến trúc Phân tầng (Layered Architecture):**
    *   **Repository Layer:** Chỉ xử lý kết nối DB qua Spring Data JPA (cấm viết câu lệnh SQL cứng nếu không cần tối ưu).
    *   **Service Layer:** Chứa toàn bộ business logic. Tách biệt rõ giữa Interface (ví dụ: `BookingService.java`) và Class Implementation (ví dụ: `BookingServiceImpl.java`).
    *   **Controller Layer:** Chỉ tiếp nhận Request, gọi Service xử lý và trả về Response (HTML Thymeleaf hoặc JSON).
*   **Chống Lỗi Lombok trên Java 24:**
    *   *Rủi ro:* Lombok annotation processor có thể bị lỗi không sinh Getter/Setter trong môi trường Java 24.
    *   *Khắc phục:* Viết rõ ràng các hàm Getter/Setter (explicit Java code) cho các trường dữ liệu nhạy cảm hoặc quan trọng trong các lớp Entity thay vì lạm dụng Lombok `@Getter/@Setter`.

---

## 2. Tiêu Chuẩn Bảo Mật & Bảo Vệ Dữ Liệu Nhạy Cảm (PII)

*   **Bắt buộc mã hóa thông tin định danh:**
    *   Mọi thông tin định danh cá nhân nhạy cảm (như số CCCD/Passport của khách hàng ở Module 2) **bắt buộc** phải được mã hóa tại tầng Backend trước khi lưu xuống Database.
    *   Sử dụng lớp `EncryptionService` để thực hiện mã hóa đối xứng (`AES-256`) đối với các trường nhạy cảm này.
*   **Ngăn chặn rò rỉ dữ liệu (Data Minimization):**
    *   Tuyệt đối không lưu dữ liệu nhạy cảm dạng văn bản thuần (plaintext).
    *   Khi log dữ liệu ra console/file, bắt buộc phải che giấu thông tin (Masking) hoặc không ghi nhật ký các biến chứa mật khẩu, CCCD, thông tin bệnh lý.

---

## 3. Quản Lý Ngoại Lệ (Exception Handling)

*   **Không trả về lỗi hệ thống chung chung:**
    *   Mọi ngoại lệ nghiệp vụ phải được quản lý bằng các Business Exception cụ thể kế thừa từ `RuntimeException` (ví dụ: `BookingNotFoundException`, `VillaNotAvailableException`).
*   **Nhất quán tiền tố mã lỗi (Error Code Prefix):**
    *   Mỗi cấu phần phải sử dụng mã lỗi có tiền tố nhất quán để dễ dàng truy vết:
        *   `BOOK-` cho cấu phần Đặt phòng/Booking.
        *   `AUTH-` cho cấu phần Xác thực/Tài khoản.
        *   `SPA-` cho cấu phần Spa.
        *   `FNB-` cho cấu phần Dịch vụ ăn uống.
