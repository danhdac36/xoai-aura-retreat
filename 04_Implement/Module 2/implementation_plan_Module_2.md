# Kế Hoạch Triển Khai & Báo Cáo Đối Chiếu Mã Nguồn - Module 2

Bản kế hoạch này thực hiện phân tích, đánh giá mã nguồn hiện tại đã viết cho Module 2, đối chiếu với các tài liệu đặc tả yêu cầu và thiết kế kỹ thuật, từ đó đề xuất các cải tiến cần thiết để hoàn thiện Module 2 một cách chặt chẽ nhất.

## 1. Đánh giá Đối chiếu Mã nguồn Hiện tại vs Đặc tả Yêu cầu

Tôi đã tiến hành kiểm tra toàn bộ các Controller, Service và Entity của cấu phần `booking` và rút ra các điểm đối chiếu sau:

### Các phần ĐÃ được hiện thực hóa đúng chuẩn:
1.  **UC06 (Browse Packages):** Lớp `RetreatPackageServiceImpl` đã triển khai tìm kiếm và xem chi tiết gói nghỉ dưỡng thành công.
2.  **UC07 (Book Package & Pay Deposit):** Phương thức `createBooking` và `confirmPayment` trong `BookingServiceImpl` đã xử lý đúng luồng tạo booking ở trạng thái `PENDING` và cập nhật thành `CONFIRMED` + tạo `GuestFolio` sau khi có callback thanh toán.
3.  **UC08 (Check-in Guest):** Phương thức `performCheckIn` trong `CheckInServiceImpl` đã thực hiện gán biệt thự vật lý và mã hóa số định danh (CCCD/Passport) qua `EncryptionService` để đảm bảo bảo mật PII.
4.  **UC09 (Manage Villa Status):** Cập nhật trạng thái dọn dẹp và đặt phòng của Villa vật lý đồng bộ khi check-in (`OCCUPIED` / `CLEANED`).
5.  **UC10 (Itinerary Timeline):** Lớp `ItineraryServiceImpl` đã liên kết dữ liệu thời gian lưu trú, spa và ăn uống thành dạng timeline.

---

## 2. Các điểm Thiếu hụt & Đề xuất Cải tiến (Gaps)

Qua đối chiếu chặt chẽ với đặc tả `Module_2.md` và `AI_RULES.md`, mã nguồn hiện tại có một số điểm chưa tối ưu cần hoàn thiện:

### A. Tích hợp Authentication (Xác thực thực tế)
*   **Hiện trạng:** Ở `BookingController` và `CheckInController`, ID của khách hàng (`guestId`) đang bị gán cứng (mocked) bằng `1`.
*   **Đề xuất:** Thay thế gán cứng bằng việc lấy thông tin user đã đăng nhập từ hệ thống bảo mật của Spring Security (`SecurityContextHolder`).

### B. Thuật toán gán phòng khi Check-in (UC08)
*   **Hiện trạng:** Ở `CheckInController` (dòng 29), danh sách phòng trống để lễ tân gán khi check-in đang bị gán cứng loại biệt thự ID = 1 (`villaRepository.findByVillaType_IdAnd... (1, "AVAILABLE")`).
*   **Đề xuất:** Hệ thống phải tải động (dynamically query) danh sách biệt thự trống thuộc đúng loại `VillaType` mà khách hàng đã đặt trong Booking cụ thể đó.

### C. Nhật ký kiểm toán (Audit Logging - BR-15)
*   **Hiện trạng:** Đặc tả yêu cầu ghi nhật ký hệ thống đối với các hành động quan trọng (tạo đơn, check-in, đổi trạng thái phòng). Hiện tại codebase chưa triển khai cơ chế ghi log này vào DB.
*   **Đề xuất:** Tạo thêm service hoặc tích hợp ghi log kiểm toán vào DB khi thực hiện Check-in hoặc xác nhận thanh toán.

---

## 3. Đề xuất Thay đổi Mã nguồn (Proposed Changes)

> [!WARNING]
> Dưới đây là đề xuất các thay đổi chi tiết. Tôi sẽ **KHÔNG** tự ý thay đổi bất kỳ file code nào cho đến khi nhận được sự phê duyệt của bạn.

### `booking` Module

#### [MODIFY] [BookingController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/BookingController.java)
*   Thay đổi cơ chế lấy `guestId` từ gán cứng sang lấy động từ thông tin người dùng đăng nhập hiện tại (Spring Security/Session).

#### [MODIFY] [CheckInController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/CheckInController.java)
*   Sửa đổi logic hiển thị danh sách phòng trống: Lễ tân khi click check-in đơn đặt phòng nào thì hệ thống sẽ gọi API tải động các phòng trống thuộc đúng loại phòng đã đặt của đơn đó, thay vì gán cứng loại phòng 1 như hiện tại.

#### [MODIFY] [CheckInServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/CheckInServiceImpl.java)
*   Thêm kiểm tra tính tương thích giữa hạng phòng vật lý gán thực tế và hạng phòng khách đặt trực tuyến (tránh gán sai hạng phòng).
*   Tích hợp ghi Audit Log sự kiện check-in của khách.

---

## 4. Kế hoạch xác minh (Verification Plan)

### Kiểm thử Tự động (Automated Tests)
Chạy lại bộ unit tests của module sau khi cập nhật code để đảm bảo build thành công:
```bash
d:\su26-swp391-se2023-g6\auramoon\mvnw.cmd test
```

### Kiểm thử Thủ công (Manual Verification)
1.  Đăng nhập vào tài khoản Khách hàng khác nhau và thực hiện đặt gói trị liệu trực tuyến để kiểm tra việc lấy `guestId` động.
2.  Mở màn hình Lễ tân, chọn Check-in cho đơn đặt phòng hạng Villa cao cấp (Premium) và kiểm tra danh sách phòng gợi ý xem có đúng là các phòng Premium trống hay không.
3.  Vào Database kiểm tra trường số định danh xem có được lưu dưới dạng chuỗi đã mã hóa an toàn hay không.
