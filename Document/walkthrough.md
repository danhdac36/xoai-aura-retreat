# Kết Quả Triển Khai & Báo Cáo Nghiệm Thu - Module 2

Chúng tôi đã hoàn thành toàn bộ các bước triển khai trong thiết kế kỹ thuật cho **Module 2: Đặt Gói Trị Liệu & Phòng Ở**. Toàn bộ mã nguồn đã được biên dịch thành công và vượt qua kiểm thử tự động 100%.

---

## 1. Các Thay Đổi Mã Nguồn Đã Thực Hiện

### Tầng Service (Modify)
- [EncryptionServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/common/service/impl/EncryptionServiceImpl.java): Triển khai thuật toán mã hóa AES-256 để bảo vệ số CCCD/Passport thô của khách.
- [VillaServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/VillaServiceImpl.java): Triển khai logic kiểm tra phòng trống thực tế và cập nhật trạng thái phòng.
- [BookingServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/BookingServiceImpl.java): Triển khai logic đặt phòng (UC07) và confirm đặt cọc thành công, tạo hóa đơn nợ trung tâm `GuestFolio`.
- [CheckInServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/CheckInServiceImpl.java): Triển khai nghiệp vụ check-in của lễ tân (UC08), mã hóa CCCD lưu DB, đổi trạng thái Villa thành `Occupied`. **Đã bổ sung ràng buộc kiểm tra sức chứa phòng vật lý (`limitPerson` của Villa so với `totalGuests` của đơn đặt phòng).**
- [ItineraryServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImpl.java): Triển khai tổng hợp dòng thời gian chuyến đi (UC10) bao gồm check-in, yoga sáng, spa trị liệu, các bữa ăn dinh dưỡng, và check-out. **Đã bổ sung logic cá nhân hóa dòng thời gian hoạt động động dựa trên từng loại gói trị liệu khách đã đặt (`Yoga`, `Detox`/`Weight Loss` hoặc `Stress Relief`).**
- [BookingRepository.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/BookingRepository.java): Thêm phương thức truy vấn `findByGuestId` lấy danh sách đơn của một khách hàng.

### Tầng Controller (New)
- [BookingController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/BookingController.java): Điều phối biểu mẫu đặt phòng, thanh toán đặt cọc giả lập.
- [CheckInController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/CheckInController.java): Quản lý bảng điều khiển lễ tân, nhận check-in.
- [VillaStatusController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/VillaStatusController.java): Quản lý cập nhật dọn dẹp/bảo trì phòng.
- [ItineraryController.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/ItineraryController.java): Điều phối hiển thị hành trình cá nhân cho khách.

### Tầng Giao Diện (New / Modify)
- Sửa đổi nút chọn đặt trong [package-detail.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/package-detail.html) trỏ sang form điền thông tin đặt phòng mới.
- Tạo mới các trang Thymeleaf HTML Premium (CSS Glassmorphism, Google Fonts, micro-animations):
  - [create-form.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/create-form.html): Form đặt phòng.
  - [success.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/success.html): Thông báo thanh toán thành công.
  - [bookings.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/reception/bookings.html): Dashboard check-in lễ tân.
  - [villas.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/reception/villas.html): Sơ đồ phòng Villa.
  - [itinerary.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/guest/itinerary.html): Lịch trình Wellness timeline của khách.
- Tạo các tệp CSS/JS tương ứng phân tách theo đúng Module chuyên biệt (Rule 4):
  - [booking-form.css](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/css/booking/booking-form.css) & [booking-form.js](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/js/booking/booking-form.js)
  - [reception.css](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/css/reception/reception.css) & [reception.js](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/js/reception/reception.js)
  - [itinerary.css](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/css/guest/itinerary.css) & [itinerary.js](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/js/guest/itinerary.js)

---

## 2. Kết Quả Kiểm Thử (Verification Output)

Chúng tôi đã chạy toàn bộ các tests và xác nhận kết quả thành công:

### Lệnh chạy kiểm thử:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"; .\mvnw.cmd clean test
```

### Kết quả (Console Output):
```text
[INFO] Running com.AuraMoon.auramoon.booking.service.BookingServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.981 s -- in com.AuraMoon.auramoon.booking.service.BookingServiceTest
[INFO] Running com.AuraMoon.auramoon.booking.service.CheckInServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.149 s -- in com.AuraMoon.auramoon.booking.service.CheckInServiceTest
[INFO] Running com.AuraMoon.auramoon.AuramoonApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.524 s -- in com.AuraMoon.auramoon.AuramoonApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Mọi test case (Happy Path, Validation Error, Security ID encryption, Room assignment type-matching, v.v.) đã vượt qua kiểm định thành công.
