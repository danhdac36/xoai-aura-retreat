# Checklist thực hiện Module 2

- [x] Hiện thực hóa các Service hiện có:
  - [x] `EncryptionServiceImpl.java` (Mã hóa/giải mã AES-256 cho CCCD/Passport)
  - [x] `VillaServiceImpl.java` (Kiểm tra phòng trống, cập nhật trạng thái Villa)
  - [x] `BookingServiceImpl.java` (Tạo đặt phòng, callback thanh toán và khởi tạo Guest Folio)
  - [x] `CheckInServiceImpl.java` (Nhận phòng, gán phòng vật lý và mã hóa số định danh)
  - [x] `ItineraryServiceImpl.java` (Dựng Timeline lịch trình chuyến đi tổng hợp)
- [x] Chạy kiểm thử tự động để xác nhận bộ test pass:
  - [x] Chạy `mvn test -Dtest=BookingServiceTest,CheckInServiceTest`
- [x] Tạo mới các Controller:
  - [x] `BookingController.java`
  - [x] `CheckInController.java`
  - [x] `VillaStatusController.java`
  - [x] `ItineraryController.java`
- [x] Tạo mới giao diện người dùng Thymeleaf HTML & Tệp tĩnh CSS/JS:
  - [x] `booking/create-form.html` & `booking/success.html`
  - [x] `reception/bookings.html` & `reception/villas.html`
  - [x] `guest/itinerary.html`
  - [x] Tệp tĩnh CSS và JS tương ứng cho từng View (tách biệt theo Module)
