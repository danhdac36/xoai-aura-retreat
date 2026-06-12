# Báo cáo Phân tích & Đề xuất Tối ưu Cơ sở Dữ liệu HoS

Bản báo cáo này phân tích cấu trúc của file [DB.sql](file:///d:/su26-swp391-se2023-g6/Template/DB.sql) và đề xuất các giải pháp tối ưu hóa thiết kế hệ thống, cải thiện hiệu năng truy vấn, đồng thời đồng bộ hóa tốt hơn với mô hình thực thể (Entities) của dự án Java/Spring Boot hiện tại.

---

## 1. Tính Toàn vẹn Dữ liệu & Chuẩn hóa (Data Integrity & Normalization)

### 1.1. Chuẩn hóa thiết kế Nhiều-Nhiều (Many-to-Many) của Gói Trị liệu
Trong bảng `RETREAT_PACKAGE`, cột `services` hiện đang được khai báo dưới dạng `NVARCHAR(MAX)` để lưu trữ danh sách các dịch vụ đi kèm.
* **Vấn đề:** Điều này vi phạm Dạng chuẩn 1 (1NF). Nếu lưu danh sách ID dịch vụ dưới dạng chuỗi phân tách bởi dấu phẩy hoặc JSON:
  * Không thể thiết lập khóa ngoại để kiểm tra tính toàn vẹn (ví dụ: nếu xóa một dịch vụ trong `TREATMENT_SERVICE`, hệ thống không tự động kiểm tra được xem dịch vụ đó có đang nằm trong gói nào không).
  * Hiệu năng truy vấn tìm kiếm các dịch vụ nằm trong gói rất tệ (phải dùng `LIKE '%id%'` dẫn đến Table Scan).
* **Giải pháp:** Loại bỏ cột `services` trong bảng `RETREAT_PACKAGE` và tạo một bảng trung gian `RETREAT_PACKAGE_SERVICE`.

### 1.2. Thiếu các ràng buộc khóa ngoại (Missing Foreign Key Constraints)
Một số bảng chứa các trường định danh tham chiếu đến bảng khác nhưng chưa khai báo ràng buộc khóa ngoại `FOREIGN KEY` ở mức cơ sở dữ liệu:
* **Bảng `MEAL_ORDER`:**
  * `guest_id INT NOT NULL` thiếu ràng buộc đến `[USER](user_id)`.
  * `ordered_by INT` thiếu ràng buộc đến `[USER](user_id)` (nhân viên đặt món hộ).
  * `folio_id INT NOT NULL` thiếu ràng buộc đến `GUEST_FOLIO(folio_id)`.
* **Bảng `TREATMENT_BOOKING`:**
  * `folio_id INT` thiếu ràng buộc đến `GUEST_FOLIO(folio_id)`.

### 1.3. Khắc phục lỗi chính tả & Không nhất quán về đặt tên (Naming & Typo issues)
* **Lỗi chính tả:**
  * Bảng `GUEST_FOLIO`: Cột `total_package_amout` nên sửa thành `total_package_amount` (thiếu chữ 'n').
  * Bảng `DIETARY_PROFILE`: Cột `diatary_preference` nên sửa thành `dietary_preference` để nhất quán với tên bảng `DIETARY_PROFILE` và đúng chính tả tiếng Anh.
* **Không nhất quán trong kiểm soát Auditing (Ngày tạo/Cập nhật):**
  * Hầu hết các thực thể trong Spring Boot kế thừa từ [BaseEntity.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/common/entity/BaseEntity.java) sử dụng `create_at` và `update_at`.
  * Tuy nhiên, bảng `[USER]` lại sử dụng `created_at` (có chữ 'ed') và `last_update` thay vì `update_at`. Điều này làm giảm tính tái sử dụng và đồng bộ của BaseEntity (như trong [User.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/auth/entity/User.java) đang phải tự cấu hình lại auditing thủ công).
  * Cột `Identify_code` trong bảng `[USER]` viết hoa chữ cái đầu (`Identify_code`), trong khi các cột khác viết thường hoàn toàn theo dạng `snake_case`. Nên sửa thành `identity_code`.

---

## 2. Tối ưu hóa Hiệu năng (Performance Optimization)

Do dự án sử dụng SQL Server, các cột làm khóa ngoại (`FOREIGN KEY`) **không được tự động tạo Index** (không giống một số hệ quản trị CSDL khác). Việc thiếu index trên khóa ngoại sẽ dẫn đến việc SQL Server phải duyệt qua toàn bộ bảng (Table Scan) khi thực hiện các phép JOIN hoặc khi kiểm tra ràng buộc khi xóa/cập nhật bản ghi cha.

### 2.1. Đề xuất tạo Indexes cho các Khóa ngoại thường xuyên JOIN:
* `idx_user_role` trên `[USER](role_id)`
* `idx_booking_guest` trên `BOOKING(guest_id)`
* `idx_booking_package` trên `BOOKING(package_id)`
* `idx_booking_villa` trên `BOOKING(assigned_villa_id)`
* `idx_guest_folio_booking` trên `GUEST_FOLIO(booking_id)`
* `idx_treatment_booking_booking` trên `TREATMENT_BOOKING(booking_id)`
* `idx_treatment_booking_service` trên `TREATMENT_BOOKING(service_id)`
* `idx_schedule_treatment` trên `SCHEDULE(treatment_id)`

### 2.2. Đề xuất tạo Indexes cho việc tìm kiếm trạng thái & khoảng thời gian (Date & Status queries):
* **Kiểm tra trạng thái trống của phòng/villa:**
  * Tạo index kết hợp (Composite Index) trên `BOOKING(checkin_date, checkout_date)` để tối ưu hóa thuật toán tìm kiếm villa còn trống trong một khoảng thời gian cụ thể.
* **Xếp lịch trị liệu:**
  * Tạo index kết hợp trên `SCHEDULE(start_time, end_time)` giúp kiểm tra nhanh chóng xem kỹ thuật viên (therapist) hoặc phòng trị liệu có bị trùng lịch trong ca làm việc hay không.

---

## 3. Ràng buộc dữ liệu & Kiểu dữ liệu (Data Constraints & Sizes)

### 3.1. Ràng buộc miền giá trị trạng thái (`CHECK` Constraints)
Các trường trạng thái trong hệ thống như `booking_status`, `payment_status`, `order_status`, `villa_status`... đang dùng kiểu `VARCHAR` mà không có ràng buộc kiểm tra.
* **Nguy cơ:** Ứng dụng Java có thể ghi dữ liệu sai chính tả hoặc trạng thái không hợp lệ vào DB (ví dụ: `'COMPLETD'` thay vì `'COMPLETED'`).
* **Giải pháp:** Sử dụng ràng buộc `CHECK` ở tầng Database để đồng bộ chặt chẽ với các Enum trong ứng dụng Spring Boot.

### 3.2. Giới hạn độ dài chuỗi quá nhỏ
Bảng `PAYMENT` định nghĩa:
* `payment_method VARCHAR(10)`
* `payment_gateway VARCHAR(10)`
* **Vấn đề:** Độ dài `10` ký tự là quá ngắn. Các phương thức thanh toán phổ biến như `BANK_TRANSFER` (13 ký tự), `CREDIT_CARD` (11 ký tự) hay các cổng thanh toán như `VNPAY_SANDBOX` (13 ký tự) sẽ bị lỗi tràn bộ đệm (String truncation error) khi insert.
* **Giải pháp:** Tăng độ dài lên tối thiểu `VARCHAR(30)`.

---

## 4. Đồng bộ hóa Xóa mềm (Soft Delete)

Hiện tại, cấu trúc thực thể Java sử dụng trường `is_delete` để thực hiện xóa mềm (Soft Delete).
* Một số bảng trong SQL đã có `is_delete BIT DEFAULT 0`.
* Tuy nhiên, các bảng quan trọng khác như `[USER]`, `THERAPIST`, `MENU_ITEM` lại **chưa có** trường này.
* **Đề xuất:** Thêm cột `is_delete BIT DEFAULT 0` ON `[USER]` và `MENU_ITEM` để khi cần xóa một tài khoản hoặc một món ăn, hệ thống không làm mất dữ liệu lịch sử hóa đơn/đặt chỗ liên quan (tránh lỗi vi phạm khóa ngoại khi xóa cứng).

---

## 5. SQL Script đề xuất cập nhật cấu trúc (Migration Script)

Dưới đây là mã SQL mẫu dùng để nâng cấp và tối ưu hóa cơ sở dữ liệu hiện tại mà không làm mất mát cấu trúc chính:

```sql
-- 1. Thêm cột xóa mềm & Đồng bộ Auditing cho bảng USER
ALTER TABLE [USER] ADD is_delete BIT DEFAULT 0;
EXEC sp_rename 'USER.created_at', 'create_at', 'COLUMN';
EXEC sp_rename 'USER.last_update', 'update_at', 'COLUMN';
EXEC sp_rename 'USER.Identify_code', 'identity_code', 'COLUMN';

-- 2. Sửa lỗi chính tả trong bảng GUEST_FOLIO và DIETARY_PROFILE
EXEC sp_rename 'GUEST_FOLIO.total_package_amout', 'total_package_amount', 'COLUMN';
EXEC sp_rename 'DIETARY_PROFILE.diatary_preference', 'dietary_preference', 'COLUMN';

-- 3. Bổ sung các ràng buộc Khóa ngoại còn thiếu
-- Bổ sung FK cho MEAL_ORDER
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_GUEST 
    FOREIGN KEY (guest_id) REFERENCES [USER](user_id);
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_STAFF 
    FOREIGN KEY (ordered_by) REFERENCES [USER](user_id);
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_FOLIO 
    FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id);

-- Bổ sung FK cho TREATMENT_BOOKING
ALTER TABLE TREATMENT_BOOKING ADD CONSTRAINT FK_TREATMENT_BOOKING_FOLIO 
    FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id);

-- 4. Tăng độ dài cột cho bảng PAYMENT để tránh tràn dữ liệu
ALTER TABLE PAYMENT ALTER COLUMN payment_method VARCHAR(30);
ALTER TABLE PAYMENT ALTER COLUMN payment_gateway VARCHAR(30);

-- 5. Chuẩn hóa Nhiều-Nhiều cho Gói Trị liệu (RETREAT_PACKAGE và TREATMENT_SERVICE)
-- Tạo bảng trung gian
CREATE TABLE RETREAT_PACKAGE_SERVICE (
    package_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (package_id, service_id),
    CONSTRAINT FK_PKG_SVC_PACKAGE KEY (package_id) REFERENCES RETREAT_PACKAGE(package_id),
    CONSTRAINT FK_PKG_SVC_SERVICE KEY (service_id) REFERENCES TREATMENT_SERVICE(service_id)
);
-- Lưu ý: Sau khi chạy script này, bạn có thể drop cột 'services' của bảng RETREAT_PACKAGE
-- ALTER TABLE RETREAT_PACKAGE DROP COLUMN services;

-- 6. Tạo các INDEX tối ưu hiệu năng tìm kiếm và JOIN
-- Index khóa ngoại
CREATE NONCLUSTERED INDEX IX_USER_role_id ON [USER](role_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_guest_id ON BOOKING(guest_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_package_id ON BOOKING(package_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_assigned_villa_id ON BOOKING(assigned_villa_id);
CREATE NONCLUSTERED INDEX IX_GUEST_FOLIO_booking_id ON GUEST_FOLIO(booking_id);
CREATE NONCLUSTERED INDEX IX_TREATMENT_BOOKING_booking_id ON TREATMENT_BOOKING(booking_id);
CREATE NONCLUSTERED INDEX IX_TREATMENT_BOOKING_service_id ON TREATMENT_BOOKING(service_id);
CREATE NONCLUSTERED INDEX IX_SCHEDULE_treatment_id ON SCHEDULE(treatment_id);

-- Index khoảng thời gian (Tối ưu check trống phòng và xếp lịch)
CREATE NONCLUSTERED INDEX IX_BOOKING_dates ON BOOKING(checkin_date, checkout_date);
CREATE NONCLUSTERED INDEX IX_SCHEDULE_times ON SCHEDULE(start_time, end_time);

-- 7. Thêm Ràng buộc CHECK cho các trạng thái chính để đảm bảo toàn vẹn miền giá trị
ALTER TABLE BOOKING ADD CONSTRAINT CHK_booking_status 
    CHECK (booking_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'CHECKED_OUT'));

ALTER TABLE BOOKING ADD CONSTRAINT CHK_payment_status 
    CHECK (payment_status IN ('UNPAID', 'PARTIAL', 'PAID', 'REFUNDED'));

ALTER TABLE GUEST_FOLIO ADD CONSTRAINT CHK_folio_status 
    CHECK (status IN ('UNPAID', 'PAID', 'CANCELLED'));
```
