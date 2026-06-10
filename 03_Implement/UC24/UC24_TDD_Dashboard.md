# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-DASH-TDD-024
**Version:** 1.0
**Date:** 2026-06-10
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021
**Author:** AI Assistant – Backend Developer
**Reviewed by:** [x] User Tech Lead
**Classification:** Internal – Confidential

**References:**
* `01_SRS/SRS_Document_SWP391_G6.md` (UC24)
* `03_Implement/UC24/UC24_EDS_Dashboard.md` — Technical Specification

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.

# CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-10 | AI Assistant | Khởi tạo tài liệu — TDD spec cho UC24 Dashboard |

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `UC24` |
| **Module** | `Dashboard & Analytics` |
| **Spec gốc** | `AURA-DASH-IMP-024` |
| **Priority** | 🟠 P1 |
| **Data Classification** | `Internal` |
| **Upstream Dependencies** | `BillingService, BookingService` |

# 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| L1 | Chưa có công thức tính tỷ lệ chuyên viên (Therapist Utilization) rõ ràng | DB hiện tại chưa có bảng Tracking giờ làm việc chuẩn xác | Tạm thời giả định công suất là 8h/ngày hoặc bỏ qua tỷ lệ này chờ Spec mới |
| L2 | Tính doanh thu từ Payment hay GuestFolio? | `GuestFolio` lưu trữ breakdown (Package vs Extra Fb/Spa) | Sử dụng `GuestFolio` (status PAID) thay vì Payment để lấy chi tiết nguồn doanh thu |

# 3. Test Design Specification (TDS)

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| TC-COND-001 | Tính tổng doanh thu chính xác từ các hóa đơn đã thanh toán | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-001` |
| TC-COND-002 | Lọc đúng khoảng thời gian (Date Range Filtering) | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-002` |
| TC-COND-003 | Loại bỏ các hóa đơn chưa thanh toán (UNPAID) | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-003` |

# 4. Test Case Specification

## DASH-TC-001 — Verify Revenue Calculation (Happy Path)

**Severity:** HIGH
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001`

**Preconditions:**
* Có 1 GuestFolio status 'PAID' (Package: 10M, Spa: 2M, F&B: 1M).

**Test Steps:**
1. Khởi tạo Mock Data cho Repository trả về list Folio và FolioItem hợp lệ.
2. Gọi `dashboardService.getDashboardData(start, end)`.
3. Kiểm tra đối tượng `RevenueDashboardDTO` trả về.

**Expected Result (PASS):**
* `packageRevenue` = 10,000,000
* `spaRevenue` = 2,000,000
* `fbRevenue` = 1,000,000
* `totalRevenue` = 13,000,000

**Expected Result (FAIL):**
* Nếu tính sai, số liệu trả về không khớp tổng hoặc bằng 0.

## DASH-TC-002 — Ignore UNPAID Transactions

**Severity:** CRITICAL
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-003`

**Preconditions:**
* Có 1 GuestFolio status 'UNPAID' (Package: 5M).

**Test Steps:**
1. Chạy hàm lấy Dashboard.
2. Kiểm tra DTO trả về.

**Expected Result (PASS):**
* `totalRevenue` = 0 (Vì hóa đơn chưa thanh toán không được tính vào doanh thu).

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật UC24 đã được review và approve.
- [x] Đã thiết lập xong DTO và Repository interfaces.

## Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] API trả về chuẩn JSON / Giao diện Thymeleaf render đủ biểu đồ.
- [ ] Biểu đồ hiển thị màu sắc đúng chuẩn TailwindCSS của dự án.
