# Báo Cáo Phân Tích: Xử Lý Lịch Spa Cho Đoàn Nhiều Người (1 Booking)

## 1. Đặt Vấn Đề (Problem Statement)

Hiện tại, bảng `BOOKING` thường chỉ lưu thông tin của **Người đại diện đặt phòng** (`guest_id`) và **Tổng số khách** (`total_guests`).

Tuy nhiên, đặc thù của dịch vụ Spa / Trị liệu là tính **cá nhân hóa** và **giới hạn tài nguyên** (số lượng Kỹ thuật viên / Giường Spa tại một thời điểm là có hạn). Nếu 1 đoàn có 3 người (mua 1 Retreat Package), họ hoàn toàn có quyền và nhu cầu đi Spa vào các khung giờ khác nhau, với các Kỹ thuật viên (Therapist) khác nhau.

Nếu ta gắn cứng lịch Spa vào `booking_id`, ta sẽ không thể chia nhỏ lịch cho từng người, dẫn đến việc khó quản lý ca làm việc của nhân viên và gây xung đột giờ giấc.

---

## 2. Giải Pháp Thiết Kế Cơ Sở Dữ Liệu (Database Design)

Để giải quyết triệt để, ta áp dụng mô hình **Tách biệt "Người đặt phòng" và "Người thụ hưởng dịch vụ"**.

### Bước 1: Tạo bảng `BOOKING_GUEST` (Danh sách thành viên đoàn)

Thay vì chỉ lưu một con số `total_guests`, ta cần định danh từng người trong đoàn.

- **Tên bảng**: `BOOKING_GUEST` (hoặc `GUEST_MEMBER`)
- **Các cột (Columns)**:
  - `id` (PK)
  - `booking_id` (FK -> BOOKING)
  - `guest_index` (Số thứ tự: 1, 2, 3...)
  - `full_name` (Tên khách - có thể nhập lúc Check-in hoặc để mặc định là "Khách 1", "Khách 2")
  - `phone` (Tùy chọn)

### Bước 2: Thiết kế lại bảng Lịch Spa (`SPA_SCHEDULE`)

Bảng lịch Spa phải tham chiếu đến từng cá nhân thay vì tham chiếu đến cả đoàn.

- **Các cột (Columns)**:
  - `id` (PK)
  - `booking_guest_id` (FK -> BOOKING_GUEST) - *Lưu ý: Không dùng booking_id nữa.*
  - `service_id` (Gói Spa / Liệu trình)
  - `therapist_id` (Kỹ thuật viên phụ trách)
  - `start_time` (Thời gian bắt đầu)
  - `end_time` (Thời gian kết thúc)
  - `status` (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)

> [!TIP]
> Bằng cách này, nếu Booking có 3 người, ta sẽ có 3 record trong `BOOKING_GUEST`, và có thể tạo ra N record trong `SPA_SCHEDULE` phân bổ rải rác vào các khung giờ khác nhau.

---

## 3. Luồng Xử Lý Nghiệp Vụ (Business Logic Flow)

Là một lập trình viên (Backend/System Designer), bạn sẽ cấu trúc luồng chạy như sau:

### Phase 1: Lúc Đặt Phòng (Booking Creation)

Khi hàm `createBooking` chạy, nếu `totalGuests = 3`, hệ thống sẽ:

1. Lưu bảng `BOOKING` bình thường.
2. Chạy vòng lặp (loop) sinh ra 3 dòng trong `BOOKING_GUEST` (Ví dụ: Khách 1, Khách 2, Khách 3) trỏ về `booking_id` vừa tạo.

### Phase 2: Nhập Lịch Spa (Spa Scheduling)

Màn hình của bộ phận Spa (Spa Receptionist) khi khách đến đặt lịch sẽ diễn ra như sau:

1. Tìm kiếm mã Booking (Ví dụ: `#100`).
2. API trả về danh sách: **[Khách 1], [Khách 2], [Khách 3]**.
3. Lễ tân chọn **[Khách 1]** -> Chọn khung giờ 14:00 - 15:00 -> Chọn Kỹ thuật viên A. Lệnh POST gọi xuống Backend.
4. Lễ tân chọn **[Khách 2]** -> Chọn khung giờ 16:00 - 17:00 -> Chọn Kỹ thuật viên B. Lệnh POST gọi xuống Backend.

### Phase 3: Validation ở Backend (Quan trọng)

Khi nhận API đặt lịch cho 1 `booking_guest_id` cụ thể, BE phải kiểm tra:

- **Kiểm tra trùng lịch khách**: Khách này có đang làm dịch vụ khác vào giờ đó không?
- **Kiểm tra Kỹ thuật viên (Therapist)**: `therapist_id` có trống lịch vào khung `start_time` đến `end_time` không?
- **Kiểm tra số lượng khách**: Booking mua gói cho 3 người, thì chỉ được phép hoàn thành tối đa 3 liệu trình tiêu chuẩn của gói đó (trừ khi khách mua thêm).

---

## 4. Tổng Kết Ưu/Nhược Điểm

| Tiêu chí             | Mô tả                                                                                                                                                                                                                                                                           |
| :--------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Ưu điểm**   | - Đáp ứng chính xác nhu cầu thực tế của các khu Resort/Spa cao cấp.`<br>`- Dễ dàng chấm công, tính KPI hoặc hoa hồng cho từng Kỹ thuật viên phục vụ.`<br>`- Giảm thiểu hoàn toàn lỗi "Overbooking" (1 nhân viên làm cho 2 khách cùng lúc). |
| **Thách thức** | - Cần cập nhật lại Frontend (UI) để hiển thị danh sách thành viên trong đoàn thay vì chỉ hiển thị cục bộ Mã Booking.`<br>`- Entity relationship trong JPA/Hibernate sẽ phải thêm `@OneToMany` từ `Booking` -> `BookingGuest` -> `SpaSchedule`.  |

> [!IMPORTANT]
> Với source code hiện tại của bạn, nếu bạn muốn thực hiện điều này, bước đầu tiên là bạn cần bổ sung Entity `BookingGuest` và chỉnh sửa lại vòng lặp lưu danh sách khách đi kèm ngay sau dòng `bookingRepository.save(booking);` trong `BookingServiceImpl`.
