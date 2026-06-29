# Kế hoạch thực thi: Master Data Management (UC04)

Dựa trên tài liệu đặc tả UC04, tài liệu hiện thực hóa XOA-MOD1-IMP-004, và **thiết kế Database (DB.sql) đã được cập nhật**, mục tiêu của kế hoạch này là bổ sung các tính năng quản trị danh mục (Master Data).

## 1. Mục tiêu (Goals)
- Giữ nguyên vị trí các Entity hiện có (đã kế thừa `BaseEntity` với đầy đủ thuộc tính `isDelete` và trạng thái).
- Bổ sung cơ chế lọc Soft Delete mặc định bằng `@SQLRestriction` trên tất cả Entity Master Data.
- Tích hợp ghi log hệ thống bằng cách sử dụng chung `AuditLog` entity từ module `billing`.
- Thiết lập Spring Security cho các endpoint quản trị `/admin/master-data/**` (Role: ADMIN).
- Xây dựng luồng giao diện cập nhật động (AJAX) bằng Spring MVC + Thymeleaf Fragments.

## 2. Lưu ý từ Database Schema
Tất cả các bảng (`RETREAT_PACKAGE`, `TREATMENT_SERVICE`, `VILLA_TYPE`, `VILLA`, `YOGA_CLASS`, `MENU_ITEM`) đều đã có sẵn cờ `is_delete BIT DEFAULT 0` trong Database. Tuy nhiên, các cờ trạng thái Hoạt động/Khả dụng có sự khác biệt về tên gọi:
- `RETREAT_PACKAGE`: `is_active`
- `TREATMENT_SERVICE`, `MENU_ITEM`: `is_available`
- `VILLA`: `villa_status`
- `YOGA_CLASS`, `VILLA_TYPE`: Không có cột trạng thái (chỉ có `is_delete`).

*Lưu ý khi code Entity:* Các cờ trạng thái (nếu có) phải map chính xác tên cột trong DB. Tính năng "bật/tắt" trạng thái trên UI sẽ không áp dụng cho Yoga và VillaType.

## 3. Chi tiết triển khai (Proposed Changes)

### 3.1. Cập nhật Entities (Thêm Soft Delete)
Bổ sung annotation `@SQLRestriction("is_delete = 0")` (hoặc `is_delete = false` tùy cấu hình dialect) lên đầu class cho các Entity:
- `RetreatPackage`
- `TreatmentService`
- `VillaType`
- `Villa`
- `YogaClass`
- `MenuItem`

### 3.2. DTO & Service Layer (`masterdata`)
Tạo package `masterdata` chứa logic quản lý chung.
- **`RetreatItineraryDto.java`**: DTO chứa `String generalDescription` và `List<String> dailyActivities`.
- **`MasterDataService.java`**: Interface và class Impl đảm nhiệm việc phân phối thao tác CRUD xuống các repository của từng module.
- Thực hiện logic parse chuỗi phân cách `[DAY]` trong trường `description` của `RetreatPackage`.
- Thao tác Xóa (Delete) trong Service sẽ là việc gọi update `isDelete = true` rồi lưu lại.

### 3.3. Aspect & Audit Logging
- Tạo **`AuditLogAspect.java`** sử dụng AOP (`@AfterReturning`) bắt các sự kiện Create/Update/SoftDelete từ `MasterDataService`.
- Tự động lưu vết xuống DB thông qua `AuditLogRepository` (đã có ở `billing`).

### 3.4. Controller & Security (`masterdata/controller`)
- **`MasterDataController.java`**: Cung cấp endpoints nhận HTTP GET/POST, thay vì trả về Redirect thì sẽ trả về nội dung của từng Fragment HTML.
- **`SecurityConfig.java`**: Bổ sung phân quyền `.requestMatchers("/admin/master-data/**").hasRole("ADMIN")`.

### 3.5. Frontend & UI
- **`templates/master-data/index.html`** & **`fragments.html`**: Layout trang quản trị và các khối component.
- **`static/js/module1/master-data.js`**: Viết script gọi Fetch API (hoặc jQuery) để thay thế nội dung DOM mà không reload trang.

## 4. Kế hoạch kiểm thử (Verification Plan)
- Chạy unit test kiểm tra logic parse mảng `[DAY]` của gói nghỉ dưỡng.
- Thử nghiệm trên UI: Tạo, sửa thông tin, và xóa một dịch vụ bất kỳ. Dữ liệu thay đổi trên View tức thời bằng JS.
- Kiểm tra DB: Bản ghi vừa xóa chỉ được bật cờ `is_delete = 1`, không biến mất vật lý.
- Xác nhận bảng `AUDIT_LOG` có lưu một bản ghi mới khớp với thao tác vừa diễn ra do tài khoản ADMIN thực hiện.
