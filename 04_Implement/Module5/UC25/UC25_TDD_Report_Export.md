# TEST-DRIVEN DEVELOPMENT SPECIFICATION

# Đặc tả Kiểm thử Hướng Phát triển — UC25 Export Report

**Document ID:** AURA-REPORT-TDD-025
**Version:** 1.0
**Date:** 2026-06-13
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Phùng Giang Hải– Backend Developer
**Reviewed by:** [x] Phùng Giang Hải
**DPO Sign-off:** [x] N/A — Không xử lý PII
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**

* `01_SRS/SRS_Document_SWP391_G6.md` (UC25 — Section 1.3.2 & Section 3.3.2)
* `03_Implement/UC25/UC25_EDS_Report_Export.md` (AURA-REPORT-IMP-025 v1.0)
* `03_Implement/UC24/UC24_EDS_Dashboard.md` (AURA-DASH-IMP-024 v2.0)
* `Document/Retreat.md` — Section 4, Module 5

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Test data dùng dữ liệu SYNTHETIC. Không dùng PII thật.

# CHANGELOG

> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày      | Người thực hiện | Nội dung thay đổi                                     |
| ---------- | ------------------- | -------------------------------------------------------- |
| 2026-06-13 | Sinh viên 5        | Khởi tạo tài liệu — TDD spec cho UC25 Export Report |
| 2026-06-16 | Agent               | Cập nhật Test Cases từ Daily sang Monthly logic      |

# MỤC LỤC

1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field                           | Value                                                                                                   |
| ------------------------------- | ------------------------------------------------------------------------------------------------------- |
| **Feature / Gap ID**      | `UC25`                                                                                                |
| **Module**                | `Module 5: Performance Report Export`                                                                 |
| **Spec gốc**             | `AURA-REPORT-IMP-025 v1.0`                                                                            |
| **Priority**              | 🟡 P2                                                                                                   |
| **Sprint**                | `S3 (2026-06-09 → 2026-06-20)`                                                                       |
| **Milestone**             | `M3 Alpha - 2026-07-11`                                                                               |
| **Data Classification**   | `Internal`                                                                                            |
| **Compliance Scope**      | N/A — Không xử lý dữ liệu nhạy cảm                                                              |
| **Upstream Dependencies** | `UC24 (Dashboard logic), BookingRepository, VillaRepository, ScheduleRepository, TherapistRepository` |
| **Downstream Consumers**  | `Manager (file Excel tải về)`                                                                       |

# 2. Logic Issues Resolved

> **Bắt buộc điền trước khi viết test.**

| #  | Spec gốc (sai / thiếu)                                                                         | Thực tế (schema / policy)                                                                        | Fix áp dụng trong test                                                                                                                                  |
| -- | ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| L1 | SRS chỉ ghi "export monthly report to Excel" — không xác định nội dung cụ thể các cột | Không có wireframe/mockup chi tiết cho file Excel                                               | Thiết kế 2 sheets: (1) Room Occupancy (Date, Total, Occupied, Rate, Available), (2) Therapist Utilization (Name, Code, Total, Completed, No-Show, Rate) |
| L2 | Therapist Utilization: không có công thức rõ ràng                                          | DB `SCHEDULE` có `start_time`, `end_time` nhưng không có trường `status` trực tiếp | Utilization = (sessions COMPLETED / total sessions assigned) × 100. Lấy status từ `TREATMENT_BOOKING.status` JOIN với `SCHEDULE.treatment_id`     |
| L3 | SRS ghi "PDF/Excel" — chưa xác định thư viện                                              | Dự án Java Spring Boot                                                                           | Dùng Apache POI (`poi-ooxml:5.2.5`) theo ADR-025. PDF để phase sau nếu cần                                                                         |
| L4 | Cột DB `total_package_amout` bị typo                                                         | Schema thực tế giữ tên `total_package_amout`                                                 | Entity mapping giữ nguyên typo — không ảnh hưởng UC25 trực tiếp                                                                                  |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
[Report Export] bao gồm các layer:
├── DTO (OccupancyReportRow, TherapistUtilizationRow, ReportDataDTO)
├── Service (ReportServiceImpl — business logic + Excel generation)
├── Controller (ReportController — preview + download)
└── View (manager/report.html — Thymeleaf preview)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source                      | Items Derived                                                                                  |
| --------------------------- | ---------------------------------------------------------------------------------------------- |
| `SRS.md` UC25             | Manager muốn xuất báo cáo Occupancy & Therapist Utilization hàng tháng ra Excel          |
| `SRS.md` §3.3.2          | Giao diện: Export button, Data Filters, KPI Cards, Data Grid, Pagination                      |
| `BR-13`                   | Chỉ dữ liệu từ giao dịch COMPLETED mới được tính                                     |
| `ADR-025`                 | Dùng Apache POI tạo .xlsx, 2 sheets                                                          |
| `Retreat.md` §4 Module 5 | Manager muốn xuất "Tỷ lệ Lấp đầy phòng & Mức độ sử dụng Chuyên viên trị liệu" |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                                             | Coverage Item                              | Test Cases     |
| ------------ | ---------------------------------------------------------- | ------------------------------------------ | -------------- |
| TC-COND-001  | Tính Occupancy data chính xác theo tháng                | `ReportServiceImpl.generateReportData()` | `RPT-TC-001` |
| TC-COND-002  | Tính Therapist Utilization chính xác theo therapist     | `ReportServiceImpl.generateReportData()` | `RPT-TC-002` |
| TC-COND-003  | Export Excel thành công (2 sheets, header, data)         | `ReportServiceImpl.exportToExcel()`      | `RPT-TC-003` |
| TC-COND-004  | Empty data → File Excel có header nhưng không có data | `ReportServiceImpl.exportToExcel()`      | `RPT-TC-004` |
| TC-COND-005  | Invalid date range (startDate > endDate)                   | `ReportServiceImpl.generateReportData()` | `RPT-TC-005` |
| TC-COND-006  | Chỉ Manager/Admin truy cập                               | `ReportController`                       | `RPT-TC-006` |
| TC-COND-007  | File download response headers đúng                      | `ReportController.exportReport()`        | `RPT-TC-007` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4)  | Applied To                                            | Rationale                                                  |
| ------------------------ | ----------------------------------------------------- | ---------------------------------------------------------- |
| Equivalence Partitioning | Input date range (valid / empty / future / past)      | Phân lớp tương đương cho khoảng thời gian         |
| Boundary Value Analysis  | Date range: startDate = endDate (1 ngày)             | Kiểm tra biên khoảng lọc                               |
| Error Guessing           | startDate > endDate, DB rỗng                         | Kiểm tra đầu vào sai và trường hợp dữ liệu rỗng |
| Output Domain Testing    | File Excel: 2 sheets, đúng header, đúng row count | Kiểm tra output file format                               |

## TDS-05 — Test Data Requirements

| Fixture ID | Type    | Value / Logic                                                    | Mục đích                |
| ---------- | ------- | ---------------------------------------------------------------- | -------------------------- |
| `FX-101` | DB seed | `Villa × 10 (3 OCCUPIED, 7 AVAILABLE)`                        | Occupancy Rate = 30%       |
| `FX-102` | DB seed | `Booking × 5 (status: CHECKED_IN, checkin_date: 2026-06-15)`  | Active bookings            |
| `FX-103` | DB seed | `Therapist × 3`                                               | 3 chuyên viên trị liệu |
| `FX-104` | DB seed | `Schedule × 12 sessions (4/therapist)`                        | Total sessions assigned    |
| `FX-105` | DB seed | `TreatmentBooking × 12 (8 COMPLETED, 2 NO_SHOW, 2 SCHEDULED)` | Utilization = 66.7%        |
| `FX-106` | DB seed | Empty tables (no booking, no schedule)                           | Empty data test            |

# 4. Test Case Specification

## RPT-TC-001 — Generate Occupancy Report Data (Happy Path)

**Severity:** `HIGH`
**Feature Under Test:** `ReportServiceImpl.generateReportData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/service/ReportServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-001`

**Preconditions:**

* Mock `VillaRepository.count()` = 10
* Mock `BookingRepository` trả về 5 bookings active trong tháng 06/2026 (FX-101, FX-102)

**Test Steps:**

1. Arrange: Setup mock repositories
2. Act: Gọi `reportService.generateReportData(2026-06-01, 2026-06-30, "OCCUPANCY")`
3. Assert: Kiểm tra `ReportDataDTO.occupancyRows`

**Expected Result (PASS — hành vi đúng):**

* `occupancyRows` có ≥ 1 row
* Mỗi row có: `monthLabel`, `totalVillas = 10`, `occupiedVillas`, `occupancyRate` = (occupied/10) × 100
* `avgOccupancyRate` tính đúng trung bình các tháng

**Expected Result (FAIL — dấu hiệu lỗi):**

* `occupancyRows` rỗng, hoặc `totalVillas` = 0, hoặc rate > 100%

**Current Status:** 🔴 Not written
**Implementation Note:** Cần loop qua từng tháng trong khoảng thời gian, đếm số booking active vào tháng đó.

## RPT-TC-002 — Generate Therapist Utilization Data (Happy Path)

**Severity:** `HIGH`
**Feature Under Test:** `ReportServiceImpl.generateReportData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/service/ReportServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-002`

**Preconditions:**

* Mock `TherapistRepository.findAll()` trả về 3 therapists (FX-103)
* Mock `ScheduleRepository` trả về 12 sessions (FX-104)
* Mock join với `TreatmentBooking`: 8 COMPLETED, 2 NO_SHOW, 2 SCHEDULED (FX-105)

**Test Steps:**

1. Arrange: Setup mock repositories
2. Act: Gọi `reportService.generateReportData(2026-06-01, 2026-06-30, "UTILIZATION")`
3. Assert: Kiểm tra `ReportDataDTO.utilizationRows`

**Expected Result (PASS):**

* `utilizationRows.size()` = 3 (1 row/therapist)
* Mỗi row có: `therapistName`, `totalSessions = 4`, `completedSessions`, `noShowSessions`
* `utilizationRate` = (completed / total) × 100
* `avgUtilizationRate` tính đúng trung bình

**Expected Result (FAIL):**

* Row count ≠ 3, hoặc utilization > 100%, hoặc completed + noShow > total

**Current Status:** 🔴 Not written

## RPT-TC-003 — Export Excel File (Happy Path)

**Severity:** `HIGH`
**Feature Under Test:** `ReportServiceImpl.exportToExcel()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/service/ReportServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-003`

**Preconditions:**

* `ReportDataDTO` đã được populate với dữ liệu test (FX-101 → FX-105)

**Test Steps:**

1. Arrange: Build `ReportDataDTO` với 5 occupancy rows + 3 utilization rows
2. Act: Gọi `reportService.exportToExcel(reportData)`
3. Assert: Kiểm tra byte[] trả về

**Expected Result (PASS):**

* `byte[].length > 0`
* Parse byte[] bằng `XSSFWorkbook(new ByteArrayInputStream(bytes))` thành công
* Workbook có 2 sheets: "Room Occupancy" + "Therapist Utilization"
* Sheet "Room Occupancy": Row 0 = header, Row 1-5 = data
* Sheet "Therapist Utilization": Row 0 = header, Row 1-3 = data
* Header cells có font Bold

**Expected Result (FAIL):**

* `byte[]` rỗng hoặc null
* Parse bằng POI throw `InvalidFormatException`
* Sheet count ≠ 2

**Current Status:** 🔴 Not written
**Implementation Note:** Test cần import `org.apache.poi.xssf.usermodel.XSSFWorkbook` để verify file content.

## RPT-TC-004 — Empty Data Returns Excel with Headers Only

**Severity:** `MEDIUM`
**Feature Under Test:** `ReportServiceImpl.exportToExcel()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/service/ReportServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-004`

**Preconditions:**

* `ReportDataDTO` với `occupancyRows = emptyList()`, `utilizationRows = emptyList()`

**Test Steps:**

1. Arrange: Build ReportDataDTO rỗng
2. Act: Gọi `exportToExcel(emptyData)`
3. Assert: File Excel hợp lệ

**Expected Result (PASS):**

* byte[] > 0 (file vẫn hợp lệ)
* 2 sheets tồn tại
* Mỗi sheet chỉ có 1 header row, không có data rows
* Không throw NullPointerException

**Expected Result (FAIL):**

* NullPointerException khi iterate empty list
* File Excel bị corrupt

**Current Status:** 🔴 Not written

## RPT-TC-005 — Invalid Date Range

**Severity:** `MEDIUM`
**Feature Under Test:** `ReportServiceImpl.generateReportData()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/service/ReportServiceImplTest.java`
**TDD Phase:** 🔴 RED — chưa implement
**Condition Ref:** `TC-COND-005`

**Preconditions:**

* startDate = 2026-06-30
* endDate = 2026-06-01 (startDate > endDate)

**Test Steps:**

1. Arrange: Không cần mock — validation xảy ra trước query
2. Act: Gọi `generateReportData(2026-06-30, 2026-06-01, "ALL")`
3. Assert: Exception thrown

**Expected Result (PASS):**

* Throw `IllegalArgumentException` với message "Ngày bắt đầu phải trước ngày kết thúc"

**Expected Result (FAIL):**

* Không throw exception, hoặc query DB với range sai trả về kết quả lộn xộn

**Current Status:** 🔴 Not written

## SECURITY TEST CASES

### RPT-TC-006 — Unauthorized Access Attempt

**Severity:** `HIGH`
**OWASP:** `A01:2021` — `Broken Access Control`
**Feature Under Test:** `ReportController` — Endpoints `/manager/report` và `/manager/report/export`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/controller/ReportControllerTest.java`
**TDD Phase:** 🔴 RED

**Preconditions:**

* User đăng nhập với role GUEST hoặc RECEPTIONIST

**Test Steps (Attack Simulation):**

1. Login với tài khoản Guest
2. Truy cập `GET /manager/report`
3. Truy cập `GET /manager/report/export?startDate=2026-06-01&endDate=2026-06-30`
4. Kiểm tra response

**Expected Result (PASS = hệ thống an toàn):**

* Cả 2 endpoint: Redirect về login hoặc HTTP 403
* Không tải được file Excel

**Expected Result (FAIL = lỗ hổng tồn tại):**

* HTTP 200 + hiển thị dữ liệu hoặc tải file → Lỗi RBAC nghiêm trọng

**Current Status:** 🔴 Not written

### RPT-TC-007 — Verify Download Response Headers

**Severity:** `MEDIUM`
**Feature Under Test:** `ReportController.exportReport()`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/controller/ReportControllerTest.java`
**TDD Phase:** 🔴 RED

**Preconditions:**

* User Manager đăng nhập, có dữ liệu report

**Test Steps:**

1. Gọi `GET /manager/report/export?startDate=2026-06-01&endDate=2026-06-30`
2. Kiểm tra HTTP response headers

**Expected Result (PASS):**

* `Content-Type` = `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
* `Content-Disposition` chứa `attachment; filename=AuraMoon_Report_20260601_20260630.xlsx`

**Expected Result (FAIL):**

* Thiếu Content-Disposition → Browser mở file inline thay vì download
* Content-Type sai → File bị corrupt khi mở

**Current Status:** 🔴 Not written

## INTEGRATION TEST CASES

### RPT-TC-INT-001 — Full Export Flow (E2E)

**Severity:** `HIGH`
**Feature Under Test:** `Full flow: Manager → Controller → Service → Repository → Excel File`
**Test File:** `src/test/java/com/AuraMoon/auramoon/report/ReportIntegrationTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001, TC-COND-002, TC-COND-003`

**Preconditions:**

* Database seed: 10 Villas, 5 Bookings, 3 Therapists, 12 Schedules
* User Manager đã đăng nhập

**Test Steps:**

1. Seed data vào test database
2. Gọi `GET /manager/report/export?startDate=2026-06-01&endDate=2026-06-30&reportType=ALL`
3. Parse response body thành XSSFWorkbook
4. Verify sheets và data

**Expected Result (PASS):**

* HTTP 200
* Response body parseable thành XSSFWorkbook
* Sheet "Room Occupancy" có data rows
* Sheet "Therapist Utilization" có 3 data rows

**Expected Result (FAIL):**

* HTTP 500 hoặc file corrupt
* Sheet count ≠ 2 hoặc data sai

**Current Status:** 🔴 Not written

# 5. Red-Green-Refactor Tracker

| TC ID              | Test File                      | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| ------------------ | ------------------------------ | ---------------- | ----------------- | ---------------- |
| `RPT-TC-001`     | `ReportServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `RPT-TC-002`     | `ReportServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `RPT-TC-003`     | `ReportServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `RPT-TC-004`     | `ReportServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `RPT-TC-005`     | `ReportServiceImplTest.java` | `[x]`          | `[x]`     | -               |
| `RPT-TC-006`     | `ReportControllerTest.java`  | `[x]`          | `[x]`     | -               |
| `RPT-TC-007`     | `ReportControllerTest.java`  | `[x]`          | `[x]`     | -               |
| `RPT-TC-ERR-001` | `ReportControllerTest.java`  | `[x]`          | `[x]`     | -               |
| `RPT-TC-INT-001` | `ReportIntegrationTest.java` | `[x]`          | `[x]`     | -               |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)

- [X] Spec kỹ thuật UC25 EDS (AURA-REPORT-IMP-025 v1.0) đã được review và approve
- [X] Logic Issues (Section 2) đã được confirm
- [X] UC24 (Dashboard) đã hoạt động — logic tính Occupancy / Utilization đã verify
- [X] Apache POI dependency đã thêm vào `pom.xml` và build thành công
- [X] Entity mapping cho `Booking`, `Villa`, `Schedule`, `Therapist` đã hoàn chỉnh
- [X] Test fixtures (Section 3 TDS-05) đã được chuẩn bị

## Exit Criteria (Điều kiện kết thúc — DoD)

- [ ] Tất cả unit tests xanh (7/7 test cases)
- [ ] Integration test xanh (1/1)
- [ ] File Excel mở thành công trong Microsoft Excel / LibreOffice Calc
- [ ] File Excel chứa đúng 2 sheets với header và dữ liệu chính xác
- [ ] Tên file tải về có format: `AuraMoon_Report_YYYYMMDD_YYYYMMDD.xlsx`
- [ ] UTF-8 tiếng Việt hiển thị đúng trong file Excel
- [ ] Chỉ Manager/Admin truy cập được (RBAC verified)

## Suspension Criteria (Điều kiện tạm dừng)

* Apache POI dependency conflict với các thư viện khác trong project
* UC24 (Dashboard) chưa hoạt động — chưa có logic tính Occupancy / Utilization để reuse
* Database schema thay đổi bảng SCHEDULE hoặc TREATMENT_BOOKING

# 7. Rollback Plan

```bash
# Revert code (không có DB schema change):
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/report/
git checkout -- auramoon/src/main/resources/templates/manager/report.html
git checkout -- auramoon/src/main/resources/static/js/report/
git checkout -- auramoon/src/main/resources/static/css/report/
git checkout -- auramoon/src/test/java/com/AuraMoon/auramoon/report/

# Nếu cần revert dependency:
git checkout -- auramoon/pom.xml
mvn clean install

# UC25 là Read-only + Export → không ảnh hưởng dữ liệu DB
```
