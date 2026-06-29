# Báo cáo Triển khai Tính năng - Cập nhật Sprint
**Ngày tạo:** 27/06/2026
**Người thực hiện:** Antigravity AI
**Nội dung chính:** Triển khai Phân quyền Yoga Instructor và Chức năng Quản lý Master Data (UC04)

---

## I. Những thay đổi và tính năng được thêm mới

### 1. Phân quyền và Luồng đăng nhập cho `YOGA_INSTRUCTOR`
Khắc phục lỗi luồng đăng nhập và giới hạn quyền truy cập cho nhân viên Yoga.

* **Tính năng:** 
  * Tạo trang chủ riêng biệt cho Yoga Instructor.
  * Tự động điều hướng sau khi đăng nhập thành công.
* **Thay đổi kỹ thuật:**
  * Thêm `instructor_home.html` vào thư mục `templates/auth`.
  * Cập nhật `DashboardController` để map endpoint `/instructor/yoga/home`.
  * Cấu hình `CustomAuthenticationSuccessHandler` để điều hướng `ROLE_YOGA_INSTRUCTOR` về trang Home.
  * Cập nhật `SecurityConfig` chặn các endpoint `/instructor/yoga/**` chỉ cho phép quyền `YOGA_INSTRUCTOR`.
  * Chỉnh sửa giao diện `sidebar.html`, bổ sung nút **Home** và giấu đi các menu nghiệp vụ không liên quan.

### 2. Triển khai Quản lý Danh mục (Master Data - UC04)
Xây dựng một module hoàn chỉnh để Quản trị viên quản lý các thực thể dữ liệu lõi (Hạng Villa, Dịch vụ Spa, Gói Retreat, Nhân sự) mà không bị xung đột với các ràng buộc khóa ngoại (Foreign Keys).

* **Tính năng:**
  * **Cơ chế Xóa mềm (Soft Delete):** Đảm bảo an toàn dữ liệu lịch sử và báo cáo. Dữ liệu khi bị xóa chỉ bị ẩn khỏi giao diện chứ không xóa khỏi cơ sở dữ liệu.
  * **Giao diện Không tải lại trang (No Page Reload):** Nâng cao trải nghiệm người dùng với thao tác chuyển Tab và lưu dữ liệu tức thì.
  * **Chống truy cập trái phép:** Các Role khác có thể được cấp quyền xem dữ liệu danh mục, nhưng chỉ có `ADMIN` mới có thể thêm/sửa/xóa.
* **Thay đổi kỹ thuật:**
  * **Entity (Data Layer):** Bổ sung Annotation `@SQLRestriction("is_delete = 0")` cho các entity `User`, `VillaType`, `TreatmentService`, `RetreatPackage`.
  * **Service Layer:** Tạo mới `IMasterDataService` và `MasterDataServiceImpl` để xử lý các logic `softDelete()` bằng cách cập nhật cờ `isDelete = true`.
  * **Controller Layer:** Tạo `MasterDataController.java` xây dựng luồng trả về Thymeleaf HTML Fragments.
  * **Frontend (Thymeleaf + JS + Tailwind):** 
    * Tạo `index.html`: Cấu trúc Layout tĩnh với các nút Tab.
    * Tạo `fragments.html`: Chứa cấu trúc bảng và form động.
    * Tạo `master-data.js`: Quản lý các lệnh Fetch API (AJAX) để gọi lên máy chủ và thay thế các DOM Element trong HTML.
  * **Bảo mật:** Điều chỉnh `SecurityConfig` cấp phép truy cập xem (`GET`) cho các quyền quản lý và `ADMIN`, bảo mật tuyệt đối các thao tác thay đổi (`POST`).

---

## II. Các tệp tin bị tác động (File Changes)

### Tệp được chỉnh sửa (Modified)
1. `CustomAuthenticationSuccessHandler.java`
2. `SecurityConfig.java`
3. `DashboardController.java`
4. `User.java` (Thêm @SQLRestriction)
5. `VillaType.java` (Thêm @SQLRestriction)
6. `TreatmentService.java` (Thêm @SQLRestriction)
7. `RetreatPackage.java` (Thêm @SQLRestriction)
8. `sidebar.html` (Thêm menu Yoga Home, Master Data)

### Tệp tạo mới (New)
1. `instructor_home.html`
2. `IMasterDataService.java`
3. `MasterDataServiceImpl.java`
4. `MasterDataController.java`
5. `master-data.js`
6. `index.html` (trong thư mục `admin/master-data`)
7. `fragments.html` (trong thư mục `admin/master-data`)

---

## III. Các bước kiểm thử xác nhận (Verification)
1. Truy cập trang Web, đăng nhập tài khoản có quyền `YOGA_INSTRUCTOR`, kiểm tra xem thanh Sidebar và trang chủ có hiển thị đúng chức năng của Yoga không.
2. Đăng xuất, đăng nhập tài khoản có quyền `ADMIN`.
3. Truy cập **Master Data** trên thanh menu bên trái.
4. Chuyển đổi giữa các tab: Hạng Villa, Dịch vụ Spa,... (Dữ liệu sẽ được load vòng tròn).
5. Thử bấm thêm một dữ liệu mới vào bảng (Bảng sẽ hiển thị dòng dữ liệu ngay lập tức mà trang web không hề bị nháy).
6. Bấm xóa một dữ liệu, dùng SQL Server truy vấn bảng gốc, xác nhận cột `is_delete` đã chuyển sang giá trị 1.

*Báo cáo kết thúc.*
