# Bản đồ Entity và Phân bổ Module (Domain-Driven Design)

Tài liệu này mô tả sự phân bổ 21 Entity (ánh xạ từ Database Schema) vào 5 module nghiệp vụ lõi của dự án `com.xoai.retreat`. Dự án tuân thủ nghiêm ngặt **Kiến trúc Modular Monolith** và **Loose Coupling** cho các Entity.

## Nguyên tắc Thiết kế Khóa Ngoại (Foreign Key)

1.  **Quan hệ Intra-module (Cùng module):** Sử dụng các Annotation của JPA như `@ManyToOne`, `@OneToMany` kết hợp `@JoinColumn` để ánh xạ trực tiếp thành Java Object.
2.  **Quan hệ Inter-module (Khác module):** KHÔNG ánh xạ Object. Khóa ngoại trỏ sang module khác chỉ được lưu dưới dạng `Integer` (VD: `private Integer bookingId;`) để cắt đứt sự phụ thuộc (dependency) giữa các module, cho phép dễ dàng nâng cấp lên Microservices sau này.

---

## Phân bổ Entity theo Module

### 1. Module `auth` (Xác thực & Người dùng)
Quản lý các tài khoản cốt lõi và sự cho phép về mặt pháp lý (Privacy Consent).
- **`Role`**: Quyền hệ thống.
- **`User`**: Thực thể trung tâm.
- **`Consent`**: Sự chấp thuận điều khoản bảo vệ dữ liệu (Data Privacy).
  - *Quan hệ:* `Consent` -> `User` (Cùng module: `@ManyToOne`). `User` -> `Role` (Cùng module: `@ManyToOne`).

### 2. Module `booking` (Đặt chỗ & Lưu trú)
Nơi xử lý các giao dịch đặt phòng và bán các gói nghỉ dưỡng.
- **`VillaType`**: Phân loại villa.
- **`Villa`**: Danh sách phòng cụ thể.
- **`RetreatPackage`**: Các gói dịch vụ nghỉ dưỡng trọn gói (Detox, Yoga...).
- **`Booking`**: Đơn đặt lưu trú cốt lõi.
  - *Quan hệ liên module:* `guestId` (lưu ID trỏ về `User` ở auth).
  - *Quan hệ cùng module:* `@ManyToOne RetreatPackage`, `@ManyToOne Villa`.
- **`Review`**: Đánh giá sau kỳ lưu trú. (`@ManyToOne Booking`).

### 3. Module `spa` (Trị liệu & Chăm sóc sức khỏe)
Chứa các logic về Spa và Lịch trình y tế. Dữ liệu y tế đặc biệt nhạy cảm nên được bảo vệ nghiêm ngặt ở đây.
- **`PhysicalHealthProfile`**: Hồ sơ sức khỏe thể chất y tế (Chấn thương, bệnh lý).
  - *Quan hệ liên module:* `userId` (lưu ID trỏ về `User`).
- **`Therapist`**: Chuyên viên trị liệu.
  - *Quan hệ liên module:* `userId` (lưu ID trỏ về `User` ở auth - khóa chính kiêm khóa ngoại).
- **`TreatmentService`**: Dịch vụ Spa.
- **`TreatmentRoom`**: Phòng trị liệu.
- **`TreatmentBooking`**: Lượt đặt dịch vụ Spa.
  - *Quan hệ liên module:* `bookingId` (trỏ Booking), `folioId` (trỏ Folio ở billing).
- **`Schedule`**: Lịch trình thực hiện. (`@ManyToOne TreatmentBooking`, `@ManyToOne Therapist`, `@ManyToOne TreatmentRoom`).

### 4. Module `fnb` (Ẩm thực & Nhà hàng)
Xử lý các order đồ ăn và chế độ ăn kiêng.
- **`DietaryProfile`**: Hồ sơ dị ứng / sở thích ăn uống. Đầu bếp chỉ được xem profile này, không được xem PhysicalHealthProfile ở `spa`.
  - *Quan hệ liên module:* `userId` (lưu ID trỏ về `User`).
- **`MenuItem`**: Món ăn.
- **`MealOrder`**: Đơn gọi đồ ăn tổng.
  - *Quan hệ liên module:* `bookingId` (trỏ Booking), `folioId` (trỏ Folio), `guestId` (trỏ User).
- **`MealOrderItem`**: Chi tiết đơn đồ ăn. (`@ManyToOne MealOrder`, `@ManyToOne MenuItem`).

### 5. Module `billing` (Thanh toán)
Gom mọi khoản phí (phòng, ăn uống, spa) vào chung một Hóa đơn (Folio).
- **`GuestFolio`**: Hóa đơn lưu trú tổng.
  - *Quan hệ liên module:* `bookingId` (lưu ID trỏ về `Booking`).
- **`FolioItem`**: Khoản tính phí phụ trội. (`@ManyToOne GuestFolio`).
- **`Payment`**: Giao dịch thanh toán. (`@ManyToOne GuestFolio`).
