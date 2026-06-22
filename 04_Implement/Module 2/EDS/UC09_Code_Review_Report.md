# BÁO CÁO ĐÁNH GIÁ & KẾT QUẢ SỬA ĐỔI MÃ NGUỒN — UC09 MANAGE VILLA STATUS

* **Mã Tài liệu**: `AURAMOON-BOOKING-REVIEW-UC09`
* **Ngày thực hiện**: 2026-06-21
* **Trạng thái**: Đã khắc phục thành công các lỗi logic và kiểm thử (Giữ nguyên cấu hình phân quyền Admin theo yêu cầu)

---

## PHẦN 1: TỔNG HỢP CÁC LỖI ĐÃ KHẮC PHỤC & TRẠNG THÁI HIỆN TẠI

| STT | Thành phần ảnh hưởng | Vấn đề | Hành động khắc phục | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **VillaServiceImpl.java** | Tập hợp `VALID_CLEANING_STATUSES` thiếu trạng thái `"CLEANING"`. | Đã thêm `"CLEANING"` vào tập hợp. Đảm bảo UI và Backend thống nhất. | **Đã sửa ✅** |
| 2 | **CheckInServiceTest.java** | Mockito kiểm thử `updateVillaStatuses` dùng `"CLEANED"` bị lệch với code service `"CLEAN"`. | Đã đổi `"CLEANED"` thành `"CLEAN"`. | **Đã sửa ✅** |
| 3 | **CheckInServiceTest.java** | Mock `EncryptionService` và assert mã hóa bị sai do JPA Attribute Converter tự động xử lý. | Đã xóa mock dư thừa và assert đúng giá trị thô. | **Đã sửa ✅** |
| 4 | **CheckInServiceTest.java** | Assert trạng thái check-in sử dụng `"CHECKED-IN"` trong khi enum dùng `"CHECKED_IN"`. | Đã sửa `"CHECKED-IN"` thành `"CHECKED_IN"`. | **Đã sửa ✅** |
| 5 | **SecurityConfig.java** | Phân quyền vai trò ADMIN vào trang Sơ đồ Villa không khớp với EDS. | **Giữ nguyên không sửa đổi** theo yêu cầu của bạn. | **Giữ nguyên ⚠️** |
| 6 | **VillaStatusController.java** & **villas.html** | Cấu trúc endpoint sử dụng PathVariable thay vì QueryParam của EDS. | Đã chuyển đổi endpoint sang dạng `POST /receptionist/villa/status` nhận `villaId` để khớp đặc tả API. | **Đã sửa ✅** |

---

## PHẦN 2: CHI TIẾT CÁC THAY ĐỔI ĐÃ ÁP DỤNG

### 1. Đồng bộ trạng thái "CLEANING" trong Service
* **File ảnh hưởng**: [VillaServiceImpl.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/VillaServiceImpl.java)
```java
// VALID_CLEANING_STATUSES được cập nhật để cho phép "CLEANING"
private static final Set<String> VALID_CLEANING_STATUSES = Set.of("CLEAN", "DIRTY", "CLEANING");
```

### 2. Sửa lỗi kiểm thử trong `CheckInServiceTest.java`
* **File ảnh hưởng**: [CheckInServiceTest.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/booking/service/CheckInServiceTest.java)
* **Kết quả**: Đã loại bỏ các Mock/Assert sai lệch của `EncryptionService`, sửa đúng chuỗi hằng số của enum (`CHECKED_IN`), và đồng bộ trạng thái vệ sinh về `"CLEAN"`. Lệnh kiểm thử `mvnw test -Dtest=CheckInServiceTest` đã chạy thành công 100%.

### 3. Đồng bộ hóa Endpoint URL và Giao diện HTML
* **Files ảnh hưởng**: 
  * [VillaStatusController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/VillaStatusController.java) (Chuyển `@PostMapping("/{id}/status")` thành `@PostMapping("/status")` nhận `@RequestParam("villaId")`).
  * [villas.html](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/resources/templates/reception/villas.html) (Đổi form action gửi dữ liệu đến `/receptionist/villa/status` kèm input ẩn `villaId`).

---

## PHẦN 3: KẾT QUẢ KIỂM TRA (VERIFICATION)

Đã chạy kiểm tra biên dịch và chạy các bài kiểm thử liên quan qua Maven wrapper:
1. `.\mvnw.cmd test-compile` -> **BUILD SUCCESS** (Tất cả mã nguồn và test case biên dịch thành công, không có lỗi cú pháp).
2. `.\mvnw.cmd test -Dtest=CheckInServiceTest` -> **BUILD SUCCESS** (2/2 test case chạy thành công, không có lỗi Mockito hay Assertion).
3. `.\mvnw.cmd test -Dtest=BookingServiceTest` -> **BUILD SUCCESS** (3/3 test case chạy thành công).
