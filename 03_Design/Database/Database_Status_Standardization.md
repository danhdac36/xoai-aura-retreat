# Kế hoạch Chuẩn hóa Trạng thái Database (Database Status Standardization Plan)

Dựa trên việc phân tích file `DB.sql`, tôi nhận thấy có rất nhiều bảng đang sử dụng các trường `VARCHAR` cho các trạng thái (status), loại (type), hoặc phương thức (method) mà chưa có ràng buộc cụ thể (CHECK constraints) hay tài liệu chuẩn hóa. Điều này dẫn đến việc các thành viên trong nhóm code không thống nhất (chữ hoa, chữ thường, hoặc giá trị khác nhau).

Dưới đây là **Báo cáo phân tích** và **Kế hoạch đồng bộ trạng thái** cho toàn bộ dự án.

## 1. Vấn đề hiện tại
- Đa số các cột trạng thái đang để `VARCHAR(10)` hoặc `VARCHAR(20)`.
- Thiếu `CHECK constraint` trong DB, dẫn đến có thể insert bất kỳ text nào vào DB.
- Các hằng số (constants/enums) chưa được định nghĩa rõ ràng.

## 2. Quy tắc chung được đề xuất (General Convention)
Để tránh lỗi "người viết hoa, người viết thường", toàn bộ nhóm nên thống nhất sử dụng định dạng **UPPER_SNAKE_CASE** (TẤT CẢ VIẾT HOA và phân cách bằng dấu gạch dưới) cho mọi trạng thái khi lưu vào Database và khi kiểm tra trong code Java (tương ứng với các Enum trong Java).

> [!IMPORTANT]
> **Quy định chung:** Trong Database sẽ lưu dưới dạng String (VARCHAR) viết Hoa toàn bộ. Trong Java, nhóm nên tạo các class `Enum` tương ứng để quản lý thay vì dùng String hardcode.

## 3. Danh sách Chuẩn hóa Trạng thái cho từng Bảng

### Bảng `[USER]`
- **gender** (`VARCHAR(6)`): Giới tính của người dùng.
  - `MALE` (Nam)
  - `FEMALE` (Nữ)
  - `OTHER` (Khác)
- **status** (`VARCHAR(10)`): Trạng thái tài khoản người dùng.
  - `ACTIVE` (Đang hoạt động)
  - `INACTIVE` (Ngừng hoạt động / Chưa xác thực)
  - `BANNED` (Bị khóa)

### Bảng `THERAPIST` (Nhân viên Spa/Trị liệu)
- **status** (`VARCHAR(10)`): Trạng thái làm việc hiện tại của nhân viên.
  - `AVAILABLE` (Đang rảnh)
  - `BUSY` (Đang có khách / Đang làm dịch vụ)
  - `OFF_DUTY` (Nghỉ làm / Hết ca)

### Bảng `TREATMENT_ROOM` (Phòng trị liệu)
- **status** (`VARCHAR(20)`): Tình trạng phòng.
  - `AVAILABLE` (Trống, sẵn sàng sử dụng)
  - `OCCUPIED` (Đang có khách sử dụng)
  - `MAINTENANCE` (Đang bảo trì / Sửa chữa)

### Bảng `VILLA`
- **villa_status** (`VARCHAR(20)`): Tình trạng cho thuê của Villa.
  - `AVAILABLE` (Sẵn sàng cho thuê)
  - `OCCUPIED` (Đang có khách ở)
  - `MAINTENANCE` (Đang bảo trì)
- **cleaning_status** (`VARCHAR(10)`): Tình trạng dọn dẹp.
  - `CLEAN` (Đã dọn sạch)
  - `DIRTY` (Cần dọn dẹp)
  - `CLEANING` (Đang dọn dẹp)

### Bảng `BOOKING` (Đặt phòng / Đặt package)
- **booking_status** (`VARCHAR(20)`): Trạng thái của đơn đặt.
  - `PENDING` (Chờ xác nhận)
  - `CONFIRMED` (Đã xác nhận)
  - `CHECKED_IN` (Khách đã nhận phòng)
  - `CHECKED_OUT` (Khách đã trả phòng)
  - `CANCELLED` (Đã hủy)
- **payment_status** (`VARCHAR(20)`): Tình trạng thanh toán của booking.
  - `UNPAID` (Chưa thanh toán)
  - `PARTIAL` (Đã thanh toán một phần / Đặt cọc)
  - `PAID` (Đã thanh toán đủ)
  - `REFUNDED` (Đã hoàn tiền)

### Bảng `GUEST_FOLIO` (Hồ sơ chi phí của khách trong quá trình lưu trú)
- **status** (`VARCHAR(10)`): 
  - `OPEN` (Đang mở - khách vẫn đang lưu trú và có thể phát sinh thêm phí)
  - `CLOSED` (Đã đóng - khách đã thanh toán và check-out)

### Bảng `TREATMENT_BOOKING` (Đặt lịch dịch vụ Spa)
- **status** (`VARCHAR(20)`):
  - `PENDING` (Chờ xếp lịch)
  - `SCHEDULED` (Đã có lịch / Đã có nhân viên)
  - `COMPLETED` (Đã hoàn thành)
  - `CANCELLED` (Đã hủy)

### Bảng `MEAL_ORDER` (Đặt đồ ăn)
- **order_status** (`VARCHAR(20)`):
  - `PENDING` (Chờ tiếp nhận)
  - `PREPARING` (Đang chuẩn bị đồ ăn)
  - `DELIVERED` (Đã giao cho khách)
  - `CANCELLED` (Đã hủy)

### Bảng `FOLIO_ITEM` (Chi tiết các mục phí phát sinh)
- **status** (`VARCHAR(10)`):
  - `UNPAID` (Chưa thanh toán)
  - `PAID` (Đã thanh toán)
  - `VOIDED` (Bị hủy/Hủy bỏ không tính tiền)

### Bảng `PAYMENT` (Giao dịch thanh toán)
- **payment_method** (`VARCHAR(20)`):
  - `CASH` (Tiền mặt)
  - `CREDIT_CARD` (Thẻ tín dụng/Ghi nợ)
  - `BANK_TRANSFER` (Chuyển khoản)
- **payment_gateway** (`VARCHAR(20)`): 
  - `VNPAY`
  - `MOMO`
  - `DIRECT` (Thanh toán trực tiếp tại quầy)
- **status** (`VARCHAR(10)`):
  - `PENDING` (Đang chờ xử lý)
  - `SUCCESS` (Thanh toán thành công)
  - `FAILED` (Thanh toán thất bại)

## 4. Danh sách Chuẩn hóa các Loại/Hành động (Type/Action/Category)

Để nhất quán, các trường phân loại này cũng sẽ tuân thủ định dạng **UPPER_SNAKE_CASE** (Tất cả viết HOA). Tuy nhiên không đưa vào file `DB.sql` dưới dạng CHECK constraints để linh hoạt mở rộng trong tương lai.

### Bảng `AUDIT_LOG`
- **action_type** (`VARCHAR(50)`): Hành động được gọi từ hệ thống (ví dụ: `AuditLogService`).
  - `VIEW_INVOICE` (Mở xem hóa đơn tổng / trang checkout)
  - `INITIATE_PAYMENT` (Bắt đầu gọi API thanh toán)
  - `COMPLETE_PAYMENT` (Thanh toán thành công)
  - `PAYMENT_FAILED` (Thanh toán thất bại)
  - `CHECKOUT_COMPLETE` (Hoàn tất thủ tục check-out)
  - `NIGHT_AUDIT_MANUAL` (Quản lý chạy chốt sổ thủ công qua giao diện)
  - `NIGHT_AUDIT_AUTO` (Hệ thống tự động chạy chốt sổ lúc nửa đêm)

### Bảng `FOLIO_ITEM` (Chi tiết các hạng mục phát sinh)
- **service_category** (`NVARCHAR(50)`): Dịch vụ phát sinh thuộc loại nào (Spa, Ăn uống, Khác).
  - `SPA` (Dịch vụ Spa/Trị liệu phát sinh)
  - `F_AND_B` (Food & Beverage - Dịch vụ ăn uống phát sinh)
  - `OTHER` (Dịch vụ khác)
