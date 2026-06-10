# Xây dựng tính năng View Revenue Dashboard (UC24)

Tính năng Bảng điều khiển Doanh thu (Revenue Dashboard) cho phép Quản lý (Manager) xem báo cáo và biểu đồ doanh thu chi tiết được phân rã theo Gói Retreat, Spa, và F&B. Đồng thời hệ thống cũng tính toán tỷ lệ lấp đầy phòng và hiệu suất chuyên viên.

## User Review Required

> [!IMPORTANT]
> - Do dự án tuân thủ kiến trúc Spring Boot MVC, trang Dashboard sẽ được render bằng Thymeleaf.
> - Các biểu đồ (Pie/Bar chart) sẽ sử dụng thư viện **Chart.js** (hoặc ECharts) tích hợp qua Javascript trên UI.
> - Yêu cầu xác nhận: Dữ liệu tính toán doanh thu sẽ dựa trên trạng thái hóa đơn (GuestFolio) là `PAID` và các khoản thanh toán (Payment) là `SUCCESS` để đảm bảo độ chính xác (BR-13: completed transaction records only). Bạn có đồng ý với logic này không?

## Open Questions (Đã giải đáp)

> [!TIP]
> 1. **Về Layout UI:** Tôi đã kiểm tra `admin-layout.html`. Nó đã tích hợp sẵn Sidebar, Header và TailwindCSS. Do đó, trang Dashboard chỉ cần kế thừa layout này (dùng `th:replace="~{layout/admin-layout :: layout(~{::content})"`) mà không cần tốn công thiết kế lại khung bên ngoài.
> 2. **Về Tỷ lệ sử dụng chuyên viên:** Tôi sẽ áp dụng phương án dựa trên Trạng thái (status) của bảng `THERAPIST`. Công thức: **(Số lượng chuyên viên đang `BUSY` / Tổng số chuyên viên) * 100%**. Phương án này sẽ phản ánh hiệu suất tại thời điểm hiện tại của hệ thống đúng như yêu cầu của bạn.
## Proposed Changes

Các thay đổi được nhóm vào module mới là `dashboard` để giữ kiến trúc tách bạch theo tính năng.

### Module Dashboard

#### [NEW] `src/main/java/com/AuraMoon/auramoon/dashboard/dto/RevenueDashboardDTO.java`
- Tạo DTO chứa dữ liệu tổng hợp: `totalRevenue`, `packageRevenue`, `spaRevenue`, `fbRevenue`, `occupancyRate`, `therapistUtilization`, kèm danh sách dữ liệu dùng cho biểu đồ theo từng ngày/tháng.

#### [NEW] `src/main/java/com/AuraMoon/auramoon/dashboard/service/DashboardService.java`
- Định nghĩa interface chứa hàm `getDashboardData(LocalDate startDate, LocalDate endDate)`.

#### [NEW] `src/main/java/com/AuraMoon/auramoon/dashboard/service/impl/DashboardServiceImpl.java`
- Triển khai logic tính toán:
  - Lấy tổng `totalPackageAmount` từ `GuestFolio` (Trạng thái `PAID`).
  - Lấy `amount` từ `FolioItem` (phân nhóm theo `serviceCategory` là Spa hoặc F&B).
  - Thống kê tỷ lệ lấp đầy từ bảng `Booking`.

#### [NEW] `src/main/java/com/AuraMoon/auramoon/dashboard/controller/DashboardController.java`
- Endpoint `GET /manager/dashboard`.
- Nhận param `startDate` và `endDate`, gọi Service và trả về View `manager/dashboard`.

---

### Module Billing & Booking (Repository Updates)

#### [MODIFY] `src/main/java/com/AuraMoon/auramoon/billing/repository/GuestFolioRepository.java`
- Thêm query JPQL: `@Query("SELECT SUM(g.totalPackageAmount) FROM GuestFolio g WHERE g.status = 'PAID' AND g.updatedAt BETWEEN :startDate AND :endDate")`

#### [MODIFY] `src/main/java/com/AuraMoon/auramoon/billing/repository/FolioItemRepository.java`
- Thêm query JPQL tính tổng theo category: `@Query("SELECT SUM(f.amount) FROM FolioItem f JOIN f.guestFolio g WHERE g.status = 'PAID' AND f.serviceCategory LIKE %:category% AND g.updatedAt BETWEEN :start AND :end")`

---

### Frontend UI

#### [NEW] `src/main/resources/templates/manager/dashboard.html`
- Thiết kế giao diện Dashboard theo phong cách TailwindCSS hiện đại, cao cấp.
- Bao gồm các thẻ Card hiển thị số liệu tổng quan (KPIs).
- Bao gồm Canvas để vẽ biểu đồ.

#### [NEW] `src/main/resources/static/js/manager/dashboard/chart-init.js`
- Tách biệt Javascript vẽ biểu đồ bằng thư viện Chart.js (tuân thủ Nguyên tắc 4).

## Verification Plan

### Automated Tests
- Sẽ có Unit Test cho `DashboardServiceImpl` để kiểm thử logic cộng dồn doanh thu.
- TDD Spec (`UC24_TDD_Dashboard.md`) sẽ mô tả chi tiết các Test Case.

### Manual Verification
- Chạy hệ thống, đăng nhập bằng tài khoản Manager.
- Truy cập `/manager/dashboard`.
- Thay đổi bộ lọc ngày tháng và kiểm tra xem biểu đồ có thay đổi tương ứng và các số liệu có khớp với dữ liệu trong Database (bảng `GUEST_FOLIO` và `FOLIO_ITEM`) hay không.
