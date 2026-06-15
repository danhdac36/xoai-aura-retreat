# TIÊU CHUẨN VÀ HƯỚNG DẪN KIỂM THỬ (TESTING GUIDELINES)
## Dự án: Xoai Aura Retreat & Spa Management System

---

## 1. Tiêu Chuẩn Viết Unit Test

*   **Không tải Spring Context:**
    *   Tuyệt đối **KHÔNG** sử dụng `@SpringBootTest` hoặc `@SpringBootTest(classes = ...)` cho các bài Unit Test. Việc load toàn bộ Spring context làm thời gian chạy test bị kéo dài hàng chục giây một cách lãng phí.
    *   Chỉ sử dụng Mockito thuần túy thông qua annotation `@ExtendWith(MockitoExtension.class)` ở mức Class.
*   **Cơ chế Mocking:**
    *   Sử dụng `@Mock` để giả lập các Repository và Service phụ thuộc.
    *   Sử dụng `@InjectMocks` để tiêm các mock này vào lớp Service đang được kiểm thử thực tế.

---

## 2. Quy Chuẩn Đặt Tên & Cấu Trúc Test Case

*   **Quy ước đặt tên phương thức test:**
    *   Đặt tên theo chuẩn tiếng Anh rõ ràng để công cụ dễ nhận diện:
        *   `[tênPhươngThức]_[điềuKiệnKiểmThử]_[kếtQuảMongĐợi]`
        *   Ví dụ: `createBooking_noVillasAvailable_throwsVillaNotAvailableException`
*   **Bắt buộc sử dụng `@DisplayName`:**
    *   Mỗi phương thức test phải có annotation `@DisplayName` bằng **tiếng Việt có dấu** để reviewer hoặc quản lý dễ dàng đọc hiểu kịch bản nghiệp vụ.
    *   Ví dụ: `@DisplayName("UC07-TC-002: Đặt gói thất bại và ném lỗi khi hết phòng trống")`
*   **Cấu trúc 3A (Arrange - Act - Assert):**
    *   Bên trong mỗi test case phải ghi rõ các comment ngăn cách các phần:
        *   `// Arrange`: Chuẩn bị mock data, thiết lập hành vi giả lập cho repository.
        *   `// Act`: Gọi phương thức nghiệp vụ thực tế cần test.
        *   `// Assert`: Kiểm tra kết quả trả về, bắt exception, và verify các lượt tương tác với database.

---

## 3. Quản Lý Dữ Liệu Kiểm Thử (Test Data Rules)

*   **Tuyệt đối bảo mật thông tin thật:**
    *   **CẤM** sử dụng thông tin định danh cá nhân thật (CCCD/Passport thật, tên thật, email thật) của khách hàng làm dữ liệu mẫu trong code test.
    *   Chỉ sử dụng dữ liệu giả lập (Synthetic Test Data) an toàn (ví dụ: CCCD giả lập `"012345678901"`, tên mẫu `"Nguyen Van A"`, email mẫu `"guest@example.com"`).
*   **Kịch bản kiểm thử bảo mật bắt buộc:**
    *   Kiểm thử chống tấn công mã độc **Cross-Site Scripting (XSS)**: Truyền các chuỗi độc hại dạng `<script>...</script>` vào bình luận Review (Module 2) và kiểm tra xem hệ thống Backend có tự động escape sang dạng an toàn (`&lt;script&gt;`) hay không.
