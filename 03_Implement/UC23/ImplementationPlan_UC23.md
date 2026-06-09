# Implementation Plan: UC23 - Review & Rating

## 1. Overview
Triển khai tính năng đánh giá và xếp hạng cho khách hàng sau khi check-out (UC23), bao gồm việc thiết kế backend (Service, Controller, Exception) và tích hợp giao diện (Frontend) từ Stitch với TailwindCSS.

## 2. Các file tài liệu liên quan
Kế hoạch này đi kèm với 2 tài liệu đặc tả chi tiết:
- [Đặc tả Hệ thống (EDS v2.0)](UC23_EDS_Review.md)
- [Đặc tả Kiểm thử (TDD v1.0)](UC23_TDD_Review.md)

## 3. Kiến trúc Backend
### 3.1. Entity & Repository
- Entity `Review` đã có sẵn tại `com.AuraMoon.auramoon.booking.entity.Review`.
- Khởi tạo `ReviewRepository` kế thừa `JpaRepository` và thêm hàm `existsByBookingId` để chặn spam đánh giá 2 lần.
- Cập nhật `BookingRepository` để có thể xác thực trạng thái `COMPLETED` khi đánh giá.

### 3.2. Business Logic (Service)
- `ReviewService.java`:
  - `canSubmitReview(Integer bookingId)`: Check `COMPLETED` và `existsByBookingId`.
  - `submitReview(Integer bookingId, Integer rating, String comment)`: Validate form và save xuống DB.

### 3.3. Controller
- `ReviewController.java`:
  - `GET /review?bookingId={id}`: Hiển thị form đánh giá (Thymeleaf).
  - `POST /review/submit`: Xử lý submit, trả về trang `success`.

## 4. Tích hợp Frontend (Stitch & TailwindCSS)
- Lấy mã HTML từ màn hình Stitch ID `b8c236eb88684102843237856331a7fb`.
- Sử dụng **TailwindCSS** theo đúng yêu cầu của người dùng để lột tả thiết kế.
- Áp dụng Rule 4: Javascript nội tuyến điều khiển sự kiện click chọn sao (rating) sẽ được tách ra file tĩnh tại `src/main/resources/static/js/review/rating.js`.

## 5. Các bước triển khai
1. Viết và hoàn thiện các file tài liệu EDS và TDD.
2. Xây dựng Controller, Service, Exception cho Backend.
3. Fetch mã HTML từ Stitch và gắn vào `src/main/resources/templates/review/submit.html`.
4. Bóc tách Javascript và xử lý Tailwind.
5. Review và đóng gói mã.
