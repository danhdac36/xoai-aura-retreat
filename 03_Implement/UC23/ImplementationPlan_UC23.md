# Implementation Plan: UC23 - Review & Rating

## 1. Overview
Triển khai tính năng đánh giá và xếp hạng cho khách hàng sau khi check-out (UC23), bao gồm việc thiết kế backend (Service, Controller, Exception) và tích hợp giao diện (Frontend) từ Stitch với TailwindCSS.
Tính năng này sẽ được nối trực tiếp từ luồng thanh toán (UC22) thông qua Phương án 1: Đặt nút "Đánh giá ngay" trên màn hình Checkout Success.

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
- Tích hợp với `CheckoutController.java` (UC22):
  - Bổ sung hàm `getPaymentById` trong `BillingService` để lấy `bookingId`.
  - Sửa đổi `checkoutSuccess()` để ném `bookingId` ra `checkout_success.html`.

## 4. Tích hợp Frontend (Stitch & TailwindCSS)
- Cập nhật `checkout_success.html`: Bổ sung nút bấm "Đánh giá trải nghiệm của bạn" trỏ về `/review(bookingId=X)`.
- Tạo mới View Review: Lấy mã HTML từ màn hình Stitch ID `b8c236eb88684102843237856331a7fb`.
- Sử dụng **TailwindCSS** theo đúng yêu cầu của người dùng để lột tả thiết kế.
- Áp dụng Rule 4: Javascript nội tuyến điều khiển sự kiện click chọn sao (rating) sẽ được tách ra file tĩnh tại `src/main/resources/static/js/booking/review/rating.js`.

## 5. Các bước triển khai
1. Viết và hoàn thiện các file tài liệu EDS và TDD.
2. Xây dựng Controller, Service, Exception cho Backend.
3. Fetch mã HTML từ Stitch và gắn vào `src/main/resources/templates/booking/review/submit.html`.
4. Bóc tách Javascript và xử lý Tailwind.
5. Review và đóng gói mã.

## 6. Nhật ký sửa lỗi (Bug Fixes) trong quá trình tích hợp UC21-UC22-UC23
Trong quá trình vận hành liên thông, một số lỗi đã phát sinh và được vá nóng thành công:
1. **Lỗi LazyInitializationException ở CheckoutController**: Sửa bằng cách bổ sung `@Transactional(readOnly = true)` vào `BillingServiceImpl.getPaymentById` và chủ động gọi `payment.getGuestFolio().getBookingId()` trước khi return.
2. **Lỗi TemplateResolutionException (404/500)**: Sửa đường dẫn return sai trong `CheckoutController.java` từ `billing/checkout_success` thành đường dẫn chuẩn xác `billing/checkout/checkout_success`.
3. **Lỗi Biên dịch do sai DTO Mapping**: Cập nhật lại `CheckoutViewDTO.builder()` trong `BillingServiceImpl` (sửa tên field `groupedServices` thành `groupedExtraServices`, xóa trường không tồn tại `totalExtra`, thêm trường `payments`).
4. **Lỗi SQL Invalid column name `created_at`**: Thêm `@AttributeOverrides` vào `Booking.java` để ép Hibernate trỏ đúng vào cột `create_at` và `update_at` trong Database thay vì cột mặc định.
