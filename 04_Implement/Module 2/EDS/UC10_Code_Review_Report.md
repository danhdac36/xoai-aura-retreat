# BÁO CÁO ĐÁNH GIÁ & KẾT QUẢ SỬA ĐỔI MÃ NGUỒN — UC10 VIEW BOOKING DETAILS & ITINERARY TIMELINE

* **Mã Tài liệu**: `AURAMOON-BOOKING-REVIEW-UC10`
* **Ngày thực hiện**: 2026-06-21
* **Trạng thái**: Đã khắc phục thành công toàn bộ các lỗi logic, redirect và kiểm thử

---

## PHẦN 1: TỔNG HỢP CÁC LỖI ĐÃ KHẮC PHỤC & TRẠNG THÁI HIỆN TẠI

| STT | Thành phần ảnh hưởng | Vấn đề | Hành động khắc phục | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **ItineraryServiceImpl.java** | Lọc trạng thái `"CHECKED-IN"` (gạch nối) thay vì `"CHECKED_IN"` (gạch dưới). | Đã sửa đổi bộ lọc dùng `equalsIgnoreCase` hỗ trợ cả `"CHECKED_IN"` và `"CHECKED-IN"`. | **Đã sửa ✅** |
| 2 | **ItineraryServiceImplTest.java** | Giả lập trạng thái `.bookingStatus("CHECKED-IN")` trong UnitTest che giấu lỗi. | Đã sửa trạng thái giả lập thành `"CHECKED_IN"` để khớp chuẩn DB. | **Đã sửa ✅** |
| 3 | **ItineraryController.java** | Chuyển hướng lỗi về `/guest/dashboard?error=no_booking` gây lỗi **404 Not Found**. | Đã đổi hướng về trang dashboard thực tế `/profile/home?error=no_booking`. | **Đã sửa ✅** |
| 4 | **ItineraryController.java** | URL map là `/itinerary` thay vì `/booking/itinerary`. | Đã đổi map URL thành `@GetMapping("/booking/itinerary")` để khớp EDS. | **Đã sửa ✅** |
| 5 | **ItineraryController.java** | Trả về view `"guest/itinerary"` thay vì `"booking/itinerary"`. | Giữ nguyên vì file mẫu thymeleaf thực tế nằm ở thư mục `guest/`. | **Khớp Giao diện ✅** |

---

## PHẦN 2: CHI TIẾT CÁC THAY ĐỔI ĐÃ ÁP DỤNG

### 1. Khắc phục lỗi lọc trạng thái hoạt động của đặt phòng (Underscore vs Hyphen)
* **File ảnh hưởng**: [ItineraryServiceImpl.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImpl.java)
```java
// Dòng 38: Bộ lọc được nâng cấp thành không phân biệt chữ hoa/thường và chấp nhận cả hai định dạng:
.filter(b -> "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus()) 
        || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus()) 
        || "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()))
```

### 2. Sửa đổi file kiểm thử khớp với ràng buộc thực tế
* **File ảnh hưởng**: [ItineraryServiceImplTest.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImplTest.java)
```java
// Dòng 98: Trạng thái giả lập của đặt phòng được chuyển thành:
.bookingStatus("CHECKED_IN")
```

### 3. Đồng bộ hóa Controller: Endpoint URL & Đường dẫn Redirect
* **File ảnh hưởng**: [ItineraryController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/ItineraryController.java)
```java
// Dòng 22: Đổi sang endpoint chuẩn theo EDS
@GetMapping("/booking/itinerary")

// Dòng 47: Khắc phục lỗi chuyển hướng 404 bằng cách trỏ về trang profile thực tế:
return "redirect:/profile/home?error=no_booking";
```

---

## PHẦN 3: KẾT QUẢ XÁC MINH (VERIFICATION)

Tôi đã chạy bộ kiểm thử tự động của module booking qua Maven wrapper:
1. `.\mvnw.cmd test-compile` -> **BUILD SUCCESS**
2. Chạy toàn bộ các bài test liên quan (`ItineraryServiceImplTest`, `CheckInServiceTest`, `BookingServiceTest`):
   ```powershell
   .\mvnw.cmd test "-Dtest=ItineraryServiceImplTest,CheckInServiceTest,BookingServiceTest"
   ```
   * Kết quả: **Tests run: 9, Failures: 0, Errors: 0, Skipped: 0**
   * Trạng thái build: **BUILD SUCCESS**
