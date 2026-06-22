# TEST-DRIVEN DEVELOPMENT SPECIFICATION

# Đặc tả Kiểm thử Hướng Phát triển — UC24 Revenue Dashboard

**Document ID:** AURA-DASH-TDD-024
**Version:** 2.0
**Date:** 2026-06-13
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Phùng Giang Hải– Backend Developer
**Reviewed by:** [x] Phùng Giang Hải
**DPO Sign-off:** [x] N/A — Không xử lý PII
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**

* `01_SRS/SRS_Document_SWP391_G6.md` (UC24 — Section 2.6.1 & Section 3.1.13)
* `03_Implement/UC24/UC24_EDS_Dashboard.md` (AURA-DASH-IMP-024 v2.0)
* `Document/Retreat.md` — Section 4, Module 5

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Test data dùng dữ liệu SYNTHETIC. Không dùng PII thật.

# CHANGELOG

> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày      | Người thực hiện | Nội dung thay đổi                                                         |
| ---------- | ------------------- | ---------------------------------------------------------------------------- |
| 2026-06-10 | Sinh viên 5        | Khởi tạo tài liệu — TDD spec cho UC24 Dashboard                         |
| 2026-06-13 | Sinh viên 5        | Viết lại hoàn chỉnh theo TDD Template v1.0 — bổ sung tất cả sections |

# MỤC LỤC

1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field                           | Value                                                                                                    |
| ------------------------------- | -------------------------------------------------------------------------------------------------------- |
| **Feature / Gap ID**      | `UC24`                                                                                                 |
| **Module**                | `Module 5: Revenue Analytics Dashboard`                                                                |
| **Spec gốc**             | `AURA-DASH-IMP-024 v2.0`                                                                               |
| **Priority**              | 🟠 P1                                                                                                    |
| **Sprint**                | `S3 (2026-06-09 → 2026-06-20)`                                                                        |
| **Milestone**             | `M3 Alpha - 2026-07-11`                                                                                |
| **Data Classification**   | `Internal`                                                                                             |
| **Compliance Scope**      | N/A — Không xử lý dữ liệu nhạy cảm                                                               |
| **Upstream Dependencies** | `BillingService (UC21), PaymentService (UC22), BookingRepository, VillaRepository, ScheduleRepository` |
| **Downstream Consumers**  | `UC25 (Export Report), Manager UI`                                                                     |

# 2. Logic Issues Resolved

> **Bắt buộc điền trước khi viết test.**

| #  | Spec gốc (sai / thiếu)                                         | Thực tế (schema / policy)                                                           | Fix áp dụng trong test                                                                         |
| -- | ---------------------------------------------------------------- | ------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| L1 | SRS không có công thức tính Therapist Utilization rõ ràng | DB hiện tại không có bảng tracking giờ làm việc chuyên viên                 | Giả định công suất = 8 session/ngày/therapist. Utilization = (completed / capacity) × 100 |
| L2 | Tính doanh thu từ bảng PAYMENT hay GUEST_FOLIO?               | `GUEST_FOLIO` lưu breakdown (Package vs Extra Fb/Spa). `PAYMENT` chỉ lưu tổng | Sử dụng `GUEST_FOLIO` (status = PAID) + `FOLIO_ITEM` để lấy chi tiết theo category     |
| L3 | SRS ghi "Donut Chart" nhưng không xác định library          | Dự án Spring MVC + Thymeleaf không có charting backend                            | Dùng Chart.js phía frontend. Backend chỉ trả dữ liệu số qua Model attributes              |
| L4 | Cột DB `total_package_amout` bị typo                         | Schema thực tế giữ tên `total_package_amout`                                    | Entity mapping phải dùng `@Column(name = "total_package_amout")` — typo giữ nguyên        |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
[Dashboard & Analytics] bao gồm các layer:
├── DTO (RevenueDashboardDTO, TransactionSummary)
├── Service (DashboardServiceImpl — business logic tổng hợp)
├── Controller (DashboardController — bind Model + return View)
└── View (manager/dashboard.html — Thymeleaf + Chart.js)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source              | Items Derived                                                                     |
| ------------------- | --------------------------------------------------------------------------------- |
| `SRS.md` UC24     | Manager muốn xem Revenue Dashboard chia theo Package, Spa, F&B                   |
| `SRS.md` §3.1.13 | Dashboard có Filter (Time, Category), Donut Chart, Line Chart, Transaction Table |
| `BR-13`           | Chỉ doanh thu từ giao dịch COMPLETED/PAID mới được tính                   |
| `PF-05`           | Revenue dashboard loading ≤ 8 giây                                              |
| `PF-08`           | Dashboard requests ≤ 50 req/min                                                  |
| `ADR-024`         | Dùng GUEST_FOLIO + FOLIO_ITEM, tách DTO                                         |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                                                    | Coverage Item                               | Test Cases      |
| ------------ | ----------------------------------------------------------------- | ------------------------------------------- | --------------- |
| TC-COND-001  | Tính tổng doanh thu chính xác từ hóa đơn PAID             | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-001` |
| TC-COND-002  | Lọc đúng khoảng thời gian (Date Range)                       | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-002` |
| TC-COND-003  | Loại bỏ hóa đơn UNPAID khỏi tính toán                     | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-003` |
| TC-COND-004  | Dashboard trả về giá trị mặc định khi không có dữ liệu | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-004` |
| TC-COND-005  | Tính Occupancy Rate chính xác                                  | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-005` |
| TC-COND-006  | Chỉ Manager/Admin được truy cập                              | `DashboardController`                     | `DASH-TC-006` |
| TC-COND-007  | Lọc theo Category (SPA only, FB only)                            | `DashboardServiceImpl.getDashboardData()` | `DASH-TC-007` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4)  | Applied To                                | Rationale                                            |
| ------------------------ | ----------------------------------------- | ---------------------------------------------------- |
| Equivalence Partitioning | Input date range (valid / empty / future) | Phân lớp tương đương cho khoảng thời gian   |
| Boundary Value Analysis  | Date range: startDate = endDate (1 ngày) | Kiểm tra biên khoảng lọc                         |
| State Transition Testing | GuestFolio.status: PENDING → PAID        | Chỉ PAID mới được tính vào revenue            |
| Error Guessing           | BookingId không tồn tại, DB rỗng      | Kiểm tra các trường hợp dữ liệu bất thường |

## TDS-05 — Test Data Requirements

| Fixture ID | Type    | Value / Logic                                                                             | Mục đích                                     |
| ---------- | ------- | ----------------------------------------------------------------------------------------- | ----------------------------------------------- |
| `FX-001` | DB seed | `GuestFolio { status: 'PAID', total_package_amout: 10000000, create_at: '2026-06-01' }` | Happy path — hóa đơn đã thanh toán       |
| `FX-002` | DB seed | `FolioItem { service_category: 'Extra Spa', amount: 2000000, status: 'PAID' }`          | Doanh thu Spa bổ sung                          |
| `FX-003` | DB seed | `FolioItem { service_category: 'Extra F&B', amount: 1000000, status: 'PAID' }`          | Doanh thu F&B bổ sung                          |
| `FX-004` | DB seed | `GuestFolio { status: 'PENDING', total_package_amout: 5000000 }`                        | Hóa đơn chưa thanh toán — phải bị loại |
| `FX-005` | DB seed | `Villa { villa_status: 'OCCUPIED' } × 3, Villa { villa_status: 'AVAILABLE' } × 7`     | Occupancy Rate = 30%                            |
| `FX-006` | DB seed | `Schedule { status: 'COMPLETED' } × 16 sessions`                                       | Therapist Utilization calculation               |

# 4. Test Case Specification

## DASH-TC-001 — Verify Revenue Calculation (Happy Path)

**Severity:** `HIGH`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-001`

**Preconditions:**

* Mock `GuestFolioRepository` trả về 1 folio (FX-001) — status PAID, packageAmount = 10,000,000
* Mock `FolioItemRepository` trả về 2 items: FX-002 (Spa: 2,000,000) + FX-003 (F&B: 1,000,000)

**Test Steps:**

1. Arrange: Setup mock repositories trả về dữ liệu FX-001, FX-002, FX-003
2. Act: Gọi `dashboardService.getDashboardData(LocalDate.of(2026,6,1), LocalDate.of(2026,6,30), "ALL")`
3. Assert: Kiểm tra `RevenueDashboardDTO` trả về

**Expected Result (PASS — hành vi đúng):**

* `packageRevenue` = 10,000,000
* `spaRevenue` = 2,000,000
* `fbRevenue` = 1,000,000
* `totalRevenue` = 13,000,000

**Expected Result (FAIL — dấu hiệu lỗi):**

* Nếu tính sai, số liệu trả về không khớp tổng, hoặc bằng 0, hoặc thiếu 1 category

**Current Status:** 🔴 Not written
**Implementation Note:** Service phải dùng Java Stream `Collectors.groupingBy(FolioItem::getServiceCategory)` rồi SUM amount theo nhóm.

## DASH-TC-002 — Date Range Filtering

**Severity:** `HIGH`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-002`

**Preconditions:**

* Mock `GuestFolioRepository` trả về:
  - Folio A: create_at = 2026-01-15, status = PAID, amount = 5,000,000
  - Folio B: create_at = 2026-06-15, status = PAID, amount = 8,000,000

**Test Steps:**

1. Arrange: Setup mock trả về cả 2 folios
2. Act: Gọi `getDashboardData(2026-06-01, 2026-06-30, "ALL")`
3. Assert: Chỉ Folio B được tính

**Expected Result (PASS):**

* `packageRevenue` = 8,000,000 (chỉ Folio B, vì Folio A ngoài khoảng lọc)

**Expected Result (FAIL):**

* Revenue = 13,000,000 (tính cả Folio A ngoài khoảng → lỗi WHERE clause)

**Current Status:** 🔴 Not written
**Implementation Note:** Repository query phải có điều kiện `create_at BETWEEN startDate AND endDate`.

## DASH-TC-003 — Ignore UNPAID Transactions (BR-13)

**Severity:** `CRITICAL`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-003`

**Preconditions:**

* Mock `GuestFolioRepository` trả về 1 folio FX-004 — status = PENDING, amount = 5,000,000
* Không có folio nào status = PAID

**Test Steps:**

1. Arrange: Setup mock trả về chỉ folio PENDING
2. Act: Gọi `getDashboardData(startDate, endDate, "ALL")`
3. Assert: DTO trả về

**Expected Result (PASS — hành vi đúng):**

* `totalRevenue` = 0
* `packageRevenue` = 0
* `spaRevenue` = 0
* `fbRevenue` = 0

**Expected Result (FAIL — dấu hiệu lỗi):**

* `totalRevenue` = 5,000,000 → BR-13 bị vi phạm (tính cả hóa đơn chưa thanh toán)

**Current Status:** 🔴 Not written
**Implementation Note:** Repository query PHẢI lọc `WHERE status = 'PAID'`. Đây là business rule bắt buộc.

## DASH-TC-004 — Empty Data Returns Zero Values

**Severity:** `MEDIUM`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-004`

**Preconditions:**

* Mock `GuestFolioRepository` trả về empty list
* Mock `VillaRepository` trả về count = 0

**Test Steps:**

1. Arrange: Setup tất cả mock trả về empty
2. Act: Gọi `getDashboardData(startDate, endDate, "ALL")`
3. Assert: DTO không null, các giá trị = 0

**Expected Result (PASS):**

* `totalRevenue` = 0
* `occupancyRate` = 0.0
* `therapistUtilization` = 0.0
* `recentTransactions` = empty list
* Không throw exception

**Expected Result (FAIL):**

* NullPointerException hoặc ArithmeticException (chia cho 0)

**Current Status:** 🔴 Not written
**Implementation Note:** Cần kiểm tra division by zero khi total villas = 0 hoặc total capacity = 0.

## DASH-TC-005 — Occupancy Rate Calculation

**Severity:** `MEDIUM`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-005`

**Preconditions:**

* Mock `VillaRepository.count()` = 10
* Mock `VillaRepository.countByVillaStatus("OCCUPIED")` = 3 (FX-005)

**Test Steps:**

1. Arrange: Setup mock villa repository
2. Act: Gọi `getDashboardData(startDate, endDate, "ALL")`
3. Assert: Kiểm tra occupancyRate

**Expected Result (PASS):**

* `occupancyRate` = 30.0 (3/10 × 100)

**Expected Result (FAIL):**

* Giá trị khác 30.0, hoặc division by zero

**Current Status:** 🔴 Not written

## DASH-TC-007 — Category Filter (SPA only)

**Severity:** `MEDIUM`
**Feature Under Test:** `DashboardServiceImpl.getDashboardData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/service/DashboardServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-007`

**Preconditions:**

* Mock trả về FolioItems: Extra Spa (2M), Extra F&B (1M)

**Test Steps:**

1. Arrange: Setup mock với cả 2 loại FolioItem
2. Act: Gọi `getDashboardData(startDate, endDate, "SPA")`
3. Assert: Chỉ tính doanh thu Spa

**Expected Result (PASS):**

* `spaRevenue` = 2,000,000
* `fbRevenue` = 0 (bị lọc bỏ bởi category filter)

**Expected Result (FAIL):**

* `fbRevenue` ≠ 0 → Category filter không hoạt động

**Current Status:** 🔴 Not written

## SECURITY TEST CASES

### DASH-TC-006 — Unauthorized Access Attempt

**Severity:** `HIGH`
**OWASP:** `A01:2021` — `Broken Access Control`
**Feature Under Test:** `DashboardController` — Endpoint `/manager/dashboard`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/controller/DashboardControllerTest.java`
**TDD Phase:** 🔴 RED

**Preconditions:**

* User đăng nhập với role GUEST hoặc RECEPTIONIST

**Test Steps (Attack Simulation):**

1. Login với tài khoản Guest
2. Truy cập trực tiếp `GET /manager/dashboard`
3. Kiểm tra response

**Expected Result (PASS = hệ thống an toàn):**

* Redirect về trang login hoặc HTTP 403 Forbidden
* Không hiển thị dữ liệu doanh thu

**Expected Result (FAIL = lỗ hổng tồn tại):**

* HTTP 200 + hiển thị dữ liệu Dashboard → Lỗi RBAC nghiêm trọng

**Current Status:** 🔴 Not written

## INTEGRATION TEST CASES

### DASH-TC-INT-001 — Full Dashboard Flow (E2E)

**Severity:** `HIGH`
**Feature Under Test:** `Full flow: Manager truy cập → Controller → Service → Repository → View`
**Test File:** `src/test/java/com/AuraMoon/auramoon/dashboard/DashboardIntegrationTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001, TC-COND-003`

**Preconditions:**

* Database có seed data: 2 GuestFolio (1 PAID, 1 PENDING), 3 FolioItems (2 Spa, 1 F&B)
* User Manager đã đăng nhập

**Test Steps:**

1. Seed data vào test database
2. Gọi `GET /manager/dashboard?startDate=2026-06-01&endDate=2026-06-30`
3. Kiểm tra response HTML chứa đúng giá trị

**Expected Result (PASS):**

* HTTP 200
* HTML chứa totalRevenue chính xác (chỉ từ folio PAID)
* HTML chứa chart data attributes

**Expected Result (FAIL):**

* Revenue bao gồm cả folio PENDING
* HTTP 500 hoặc trang lỗi

**Current Status:** 🔴 Not written

# 5. Red-Green-Refactor Tracker

| TC ID               | Test File                         | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| ------------------- | --------------------------------- | ---------------- | ----------------- | ---------------- |
| `DASH-TC-001`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-002`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-003`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-004`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-005`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-006`     | `DashboardControllerTest.java`  | `[x]`          | `[x]`     | -               |
| `DASH-TC-007`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-008`     | `DashboardServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `DASH-TC-ERR-001` | `DashboardControllerTest.java`  | `[x]`          | `[x]`     | -               |
| `DASH-TC-INT-001` | `DashboardIntegrationTest.java` | `[x]`          | `[x]`     | -               |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)

- [X] Spec kỹ thuật UC24 EDS (AURA-DASH-IMP-024 v2.0) đã được review và approve
- [X] Logic Issues (Section 2) đã được confirm
- [X] Entity mapping cho `GuestFolio`, `FolioItem`, `Booking`, `Villa` đã hoàn chỉnh
- [X] Test fixtures (Section 3 TDS-05) đã được chuẩn bị
- [X] UC21 (Billing) và UC22 (Payment) đã hoạt động — đảm bảo có dữ liệu test

## Exit Criteria (Điều kiện kết thúc — DoD)

- [ ] Tất cả unit tests xanh (7/7 test cases)
- [ ] Integration test xanh (1/1)
- [ ] Dashboard load < 8 giây (PF-05)
- [ ] Biểu đồ Donut và Line Chart render chính xác
- [ ] Chỉ Manager/Admin truy cập được (RBAC verified)
- [ ] Tất cả revenue chỉ từ hóa đơn PAID (BR-13 verified)

## Suspension Criteria (Điều kiện tạm dừng)

* Module UC21/UC22 chưa hoạt động → không có dữ liệu PAID để test
* Chart.js library chưa được thêm vào project
* Database schema thay đổi cấu trúc bảng GUEST_FOLIO hoặc FOLIO_ITEM

# 7. Rollback Plan

```bash
# Revert code (không có DB schema change):
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/dashboard/
git checkout -- auramoon/src/main/resources/templates/manager/
git checkout -- auramoon/src/main/resources/static/js/dashboard/
git checkout -- auramoon/src/main/resources/static/css/dashboard/
git checkout -- auramoon/src/test/java/com/AuraMoon/auramoon/dashboard/

# UC24 là Read-only → không ảnh hưởng dữ liệu DB
# Gap vẫn OPEN → giữ nguyên entry trong task tracking
```
