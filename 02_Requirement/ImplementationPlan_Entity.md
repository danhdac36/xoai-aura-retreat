# Kế hoạch Triển khai Thiết kế Entity theo Mô hình DDD (Package by Feature)

Kế hoạch này giải quyết yêu cầu phân bổ 21 bảng trong CSDL (`DB.sql`) thành các Entity Java tương ứng, đặt vào đúng các module nghiệp vụ (auth, booking, spa, fnb, billing) và tuân thủ chặt chẽ chuẩn thiết kế **Loose Coupling** giữa các module.

## 1. Cập nhật Tài liệu

### 1.1. Tạo mới tài liệu `01_SRS/EntityDiagram.md`
- Tài liệu này sẽ đóng vai trò như một "Bản đồ" (Map) để mọi lập trình viên trong dự án dễ dàng tra cứu Entity nào thuộc Module nào.
- Trình bày rõ cách liên kết khóa ngoại (Foreign Key) giữa các module.

### 1.2. Chỉnh sửa `02_SDD/PackageDiagram.md`
- Xóa bỏ ghi chú về việc tập trung Entity tại `common.entity`.
- Thêm ghi chú mới xác nhận dự án sử dụng **Domain-Driven Design (DDD)**: Entity của module nào nằm tại module đó. Liên kết chéo module sẽ sử dụng ID (Integer) thay vì Object Relation.

## 2. Tổ chức lại Thư mục Code
- **Xóa**: Thư mục `common/entity` đã tạo ở bước trước (Chỉ giữ lại class `BaseEntity.java` trong thư mục này).
- **Tạo**: Tạo lại thư mục `entity` bên trong 5 module: `auth`, `booking`, `spa`, `fnb`, `billing`.

## 3. Chiến lược Code Entity (Java 21, Spring Boot, JPA)

### 3.1. BaseEntity
Do CSDL có sự không đồng nhất về tên cột audit (VD: bảng dùng `create_at`, bảng dùng `created_at`, bảng dùng `update_at`, bảng dùng `last_update`), thiết kế `BaseEntity` chuẩn với các Annotation `@CreatedDate`, `@LastModifiedDate`.
Với các bảng có tên cột khác đi, dùng `@AttributeOverride` ở class con để ghi đè tên cột mà vẫn tận dụng được `BaseEntity`.

### 3.2. Chiến lược Ánh xạ Quan hệ (Relationships Strategy)
Đây là phần cốt lõi của Cách 1:
- **Intra-module (Các bảng CÙNG một module):** Dùng `@ManyToOne(fetch = FetchType.LAZY)` và ánh xạ đối tượng (Ví dụ: `MealOrderItem` -> `MealOrder`).
- **Inter-module (Các bảng KHÁC module):** Chỉ lưu ID kiểu `Integer` (Ví dụ: `MealOrder` trỏ tới `GuestFolio` sẽ lưu `private Integer folioId;`).

### 3.3. Bảng Phân bổ chi tiết 21 Entities
(Dưới đây là kế hoạch phân bổ cho 21 entity)

#### Khối `auth.entity`
1. `Role` (Không kế thừa BaseEntity vì không có cột thời gian).
2. `User` (Kế thừa BaseEntity, Override `updatedAt` -> `last_update`, `@ManyToOne Role`).
3. `Consent` (Kế thừa BaseEntity, Override `updatedAt` -> `update_at`, `@ManyToOne User`).

#### Khối `booking.entity`
4. `VillaType`
5. `Villa` (`@ManyToOne VillaType`)
6. `RetreatPackage`
7. `Booking` (`guestId` trỏ về User, `@ManyToOne RetreatPackage`, `@ManyToOne Villa`)
8. `Review` (`@ManyToOne Booking`)

#### Khối `spa.entity`
9. `PhysicalHealthProfile` (`userId` trỏ về User)
10. `Therapist` (`userId` trỏ về User - Khóa chính kiêm khóa ngoại)
11. `TreatmentService`
12. `TreatmentRoom`
13. `TreatmentBooking` (`bookingId` trỏ Booking, `folioId` trỏ Folio, `@ManyToOne TreatmentService`)
14. `Schedule` (`@ManyToOne TreatmentBooking`, `@ManyToOne Therapist`, `@ManyToOne TreatmentRoom`)

#### Khối `fnb.entity`
15. `DietaryProfile` (`userId` trỏ về User)
16. `MenuItem`
17. `MealOrder` (`bookingId` trỏ Booking, `folioId` trỏ Folio, `guestId` trỏ User)
18. `MealOrderItem` (`@ManyToOne MealOrder`, `@ManyToOne MenuItem`)

#### Khối `billing.entity`
19. `GuestFolio` (`bookingId` trỏ về Booking)
20. `FolioItem` (`@ManyToOne GuestFolio`)
21. `Payment` (`@ManyToOne GuestFolio`)

## 4. Verification Plan (Kiểm tra)
- Kiểm tra syntax Java 21 hợp lệ (Sử dụng các Annotation Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`).
- Rà soát các `@Table(name="...")` và `@Column(name="...")` để đảm bảo khớp 100% với `DB.sql`.
- Đảm bảo biên dịch (Compile) thành công với Maven sau khi tạo xong toàn bộ Entity.
