# Kế Hoạch Triển Khai - Hiện Thực Hóa Code Nghiệp Vụ & Kiểm Thử Module 2

Hiện thực hóa logic xử lý cho các lớp dịch vụ chính của Module 2 (`BookingServiceImpl`, `CheckInServiceImpl`, `VillaServiceImpl`, và `ItineraryServiceImpl`) và tiến hành xác minh chất lượng bằng bộ unit test hiện có.

## Điểm Cần Người Dùng Xác Nhận

> [!IMPORTANT]
> - **Khóa Mã Hóa (Encryption Key)**: Chúng tôi sẽ triển khai `EncryptionUtils` (hoặc cấu hình lại class mã hóa) để dùng khóa đối xứng AES-256 động thông qua biến môi trường hoặc cấu hình cục bộ để chạy test.
> - **Cơ Sở Dữ Liệu**: Chúng tôi giả định toàn bộ cấu trúc bảng và thực thể (Entities) đã khớp hoàn toàn với cấu trúc DB SQL Server cục bộ.

## Các Thay Đổi Mã Nguồn Đề Xuất

### Backend - Quản lý Đặt phòng & Lưu trú

#### [MODIFY] [BookingServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/BookingServiceImpl.java)
- Triển khai logic tạo đơn đặt phòng (`createBooking`): Kiểm tra loại Villa trống, tự động tính ngày checkout dựa trên thời lượng gói, lưu đơn với trạng thái `PENDING` và `UNPAID`.
- Triển khai logic xác nhận thanh toán (`confirmPayment`): Cập nhật trạng thái thành `CONFIRMED` và `DEPOSITED`, đồng thời khởi tạo bản ghi `GuestFolio` liên kết.

#### [MODIFY] [CheckInServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/CheckInServiceImpl.java)
- Triển khai logic check-in (`performCheckIn`): Cập nhật trạng thái Booking thành `CHECKED-IN`.
- Mã hóa số định danh (CCCD/Passport) bằng AES-256 để bảo mật PII.
- Gán Villa vật lý và cập nhật trạng thái Villa sang `OCCUPIED` (Có khách) thông qua `VillaService`.

#### [MODIFY] [VillaServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/VillaServiceImpl.java)
- Triển khai logic kiểm tra phòng trống (`checkVillaAvailability`) dựa trên loại phòng, ngày đến và ngày đi.
- Triển khai logic cập nhật trạng thái Villa (`updateVillaStatuses`).

#### [MODIFY] [ItineraryServiceImpl.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImpl.java)
- Triển khai logic tổng hợp lịch trình (`getTimelineForGuest`): Truy vấn ngày lưu trú, giả lập/tổng hợp lịch Spa (Module 3) và suất ăn (Module 4) xếp theo thứ tự thời gian tăng dần.

---

## Kế Hoạch Xác Minh (Verification Plan)

### Kiểm Thử Tự Động
Chúng tôi sẽ chạy bộ test kiểm thử đơn vị của Module 2 thông qua Maven:
```powershell
mvn test -Dtest=BookingServiceTest,CheckInServiceTest
```
Xác nhận rằng:
1. `BookingServiceTest` chạy qua thành công toàn bộ 3 test cases.
2. `CheckInServiceTest` chạy qua thành công toàn bộ 2 test cases.

### Kiểm Thử Thủ Công
- Kiểm chứng kết quả biên dịch và chạy test thành công (`BUILD SUCCESS`, `Failures: 0, Errors: 0`).
