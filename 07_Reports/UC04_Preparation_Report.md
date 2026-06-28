# Báo cáo Phân tích và Chuẩn bị Triển khai UC04: Quản lý Master Data

Dựa trên việc phân tích toàn bộ mã nguồn trong thư mục `05_Development` và tài liệu đặc tả `UC04_Specification.md`, dưới đây là báo cáo về hiện trạng, các thành phần cần thiết phải xây dựng và những ảnh hưởng (impacts) tới hệ thống hiện tại để chuẩn bị cho Usecase 4.

## 1. Mục tiêu của UC04

Cho phép Administrator quản lý (Thêm, Sửa, Xóa mềm) 4 danh mục dữ liệu gốc:

- Hạng Villa (Villa Categories)
- Dịch vụ Trị liệu (Spa Services)
- Gói Nghỉ dưỡng (Retreat Packages)
- Hồ sơ Nhân viên (Staff Records)

**Yêu cầu cốt lõi**:

- **Không xóa vật lý** (Soft Delete Integration - BR-13).
- **Ghi log mọi thao tác** (Audit Trail Management - BR-15).
- Các Validation chặt chẽ về giá và code.

---

## 2. Hiện trạng Source Code (05_Development)

### Các Thực thể (Entities) đã có:

- `VillaType.java`: Đã có trường `isDelete` (Boolean).
- `TreatmentService.java`: Đã có trường `isDelete` và `isAvailable`.
- `RetreatPackage.java`: Đã có trường `isActive`, **NHƯNG ĐANG THIẾU trường `isDelete`** theo yêu cầu BR-13 của đặc tả.
- `User.java` / `Therapist.java`: Đã có trường `status`.
- `AuditLog.java`: Đã tồn tại trong package `billing`.

### Các Repository đã có:

- `VillaTypeRepository`, `TreatmentServiceRepository`, `RetreatPackageRepository`, `UserRepository`.
- **Thiếu**: Các phương thức truy vấn danh sách loại trừ các bản ghi đã xóa (ví dụ: `findByIsDeleteFalse()`).

### Các Service đã có:

- `AuditLogService` đã tồn tại để phục vụ việc ghi log.
- `RetreatPackageService`, `SpaManagerService`, `VillaService`.
- **Thiếu**: Các hàm xử lý nghiệp vụ cho Admin (Create, Update, Soft Delete) đi kèm với logic lưu `AuditLog` và kiểm tra ràng buộc trước khi xóa (Safety check).

### Controller và Giao diện (UI):

- **Thiếu hoàn toàn**: Chưa có `MasterDataController.java` cho Admin.
- **Thiếu hoàn toàn**: Chưa có giao diện HTML (`master-data.html`) tích hợp 4 tabs theo yêu cầu UX/UI.

---

## 3. Những thay đổi cần thiết (Proposed Changes)

> [!IMPORTANT]
> Cần cập nhật Database Schema để bổ sung cột `is_delete` cho bảng `RETREAT_PACKAGE` nhằm đáp ứng yêu cầu BR-13.

### 3.1. Cập nhật Database & Entity

- Bổ sung trường `isDelete` vào `RetreatPackage.java`:
  ```java
  @Column(name = "is_delete")
  @Builder.Default
  private Boolean isDelete = false;
  ```
- Cập nhật schema trong `DB.sql` và `Insert_Mock_Data.sql` cho bảng `RETREAT_PACKAGE`.

### 3.2. Cập nhật Repositories

- Sửa lại các câu query hiện tại trong `VillaTypeRepository`, `TreatmentServiceRepository`, `RetreatPackageRepository` thành `findByIsDeleteFalse()` hoặc thêm điều kiện `is_delete = 0` vào `@Query`.

### 3.3. Xây dựng Controller & Service mới

- Tạo **`MasterDataController`**: Chịu trách nhiệm render trang quản lý gồm 4 tabs và xử lý các API Ajax (hoặc form submit) để thêm/sửa/xóa mềm.
- Tạo **`MasterDataService`** (hoặc bổ sung vào các service hiện có):
  - Hàm thêm mới: Validate dữ liệu -> Lưu DB -> Gọi `AuditLogService.logAction(...)`.
  - Hàm sửa: Kiểm tra tồn tại -> Cập nhật (ngoại trừ unique code) -> Gọi `AuditLogService`.
  - Hàm xóa mềm (Soft Delete): Kiểm tra xem Villa/Service/Package có đang nằm trong booking chưa hoàn thành không (Safety Check) -> Đổi cờ `isDelete = true` -> Gọi `AuditLogService`.

### 3.4. Xây dựng Giao diện (UI)

- Tạo file `src/main/resources/templates/auth/master-data.html`.
- Thiết kế layout có 4 tabs (Villa Categories, Spa Services, Retreat Packages, Staff Records).
- Xây dựng các Modals form để Thêm mới/Cập nhật dữ liệu.

---

## 4. Đánh giá Mức độ Ảnh hưởng (Impact Analysis)

> [!WARNING]
> Việc áp dụng Soft Delete sẽ ảnh hưởng đến tất cả các câu truy vấn hiển thị dữ liệu cho Khách hàng (Guest) và Lễ tân (Receptionist) hiện tại.

1. **Luồng Đặt phòng (Booking)**: `CheckInController`, `BookingController` phải đảm bảo dropdown chọn Hạng Villa hoặc Gói Nghỉ dưỡng chỉ lấy các bản ghi có `is_delete = false` và `is_active = true`.
2. **Luồng Đặt Spa (Spa Schedule)**: Các màn hình hiển thị danh sách Dịch vụ Trị liệu (`TreatmentService`) cho Therapist và Khách hàng cần được kiểm tra lại để chắc chắn đã filter những dịch vụ bị xóa mềm.
3. **Database Migration**: Do bảng `RETREAT_PACKAGE` bị thiếu cột `is_delete`, bất cứ ai đang chạy bản DB cũ ở local sẽ cần phải chạy script alter table hoặc import lại database mới.

---

## Open Questions (Câu hỏi chờ xác nhận)

1. Để xử lý Soft Delete an toàn (Safety Check - E3), bạn muốn chặn hoàn toàn việc xóa mềm nếu có Booking/Lịch trình đang "Active", hay chỉ hiển thị cảnh báo (Warning) và vẫn cho phép Admin xóa mềm (Booking cũ vẫn giữ reference ID)?

- Tôi muốn hiển thị cảnh báo và vẫn cho Admin xóa mềm, nhưng những cái đang được hoạt động thì sẽ vẫn dùng nốt, chỉ là không book được cái đã xóa mềm nữa.

1. Giao diện quản lý `MasterData` nên làm dạng SSR (gửi Form load lại trang) hay dạng gọi API AJAX (mượt mà không tải lại trang)? Đề xuất là dùng AJAX cho phần Master Data này. -> làm SSR
2. Đối với Hồ sơ Nhân viên (Staff Records), việc xóa mềm sẽ tương đương với việc cập nhật `status = 'INACTIVE'` (Nghỉ việc) hay phải thêm một cột `is_delete` riêng cho `USER`? -> tất cả đều có trong bảng User, tôi không có bảng Staff Records. hãy sử dụng cả status và is_delete
