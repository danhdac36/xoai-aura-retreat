# TEST-DRIVEN DEVELOPMENT SPECIFICATION

## Mẫu Đặc tả Kiểm thử Hướng Phát triển: Night Audit (UC26)

- **Document ID**: AURA-BILLING-TDD-026
- **Version**: 1.0
- **Date**: 2026-06-16
- **Status**: Approved & Implemented
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3
- **Author**: Phùng Giang Hải - Tech Lead & Module 5 Owner
- **Reviewed by**: Phùng Giang Hải - Tech Lead & Module 5 Owner
- **DPO Sign-off**: `[ ] N/A — Module không xử lý PII`
- **Approved by**: `[x] Phùng Giang Hải`
- **Classification**: Internal — Confidential

### References:
- `02_Requirement/SRS_Document.md` — Functional requirements (UC26, BR-15, BR-20)
- `04_Implement/Module5/UC26/UC26_EDS_Night_Audit.md` — Technical Specification
- `03_Design/Database/DB.sql` & `Database_Status_Standardization.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code. Mọi test data trong đây đều mang phân loại `SYNTHETIC`.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-16 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho Night Audit (UC26) dựa trên EDS đã duyệt. |

---

## MỤC LỤC
1. [Thông tin Module](#1-thông-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)

---

## 1. Thông tin Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | UC26 - Night Audit |
| **Module** | Module 5: Billing / Accounting |
| **Spec gốc** | `AURA-BILLING-IMP-026` |
| **Priority** | 🔴 P0 (Critical cho báo cáo doanh thu) |
| **Data Classification** | Internal (Synthetic Test Data) |
| **Upstream Dependencies** | Spa Module (`TREATMENT_BOOKING`), F&B Module (`MEAL_ORDER`) |
| **Downstream Consumers** | Dashboard Revenue (UC24) |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.** Liệt kê mọi sai lệch giữa spec thiết kế ban đầu và schema thực tế để đảm bảo TDD bám sát sự thật.

| # | Spec gốc / Thiết kế sơ bộ | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Cột category trong EDS là `VARCHAR(20)` giá trị `FNB`. | `DB.sql` là `NVARCHAR(50)`, giá trị chuẩn hóa là `F_AND_B`. | Test DB Repository với kiểu string `F_AND_B`. |
| L2 | Ghi log bằng string tĩnh `NIGHT_AUDIT`. | Chuẩn hóa Action Type: `NIGHT_AUDIT_MANUAL` hoặc `NIGHT_AUDIT_AUTO`. | Assert `AUDIT_LOG.action_type` theo chuẩn mới. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Night Audit (UC26)` bao gồm các layer:
- Repository (Native SQL `INSERT ... SELECT`)
- Services (`NightAuditServiceImpl`, `FolioConsolidationService` dùng Spring `@Transactional`)
- Controller (`NightAuditController` trả về Spring MVC Web View / Redirect)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-26 | Business Flow (Tự động 00:00 và Thủ công). Các ngoại lệ E1, E2, E3, A2. |
| EDS-026 | Sơ đồ MVC, Sequence Diagram cho Happy Path & Error Path. |
| BR-20 | Invariant: Record chốt xong khóa chặt ở bảng `FOLIO_ITEM`. |
| BR-15 | Phải ghi log hệ thống (`AUDIT_LOG`) sau khi hoàn tất. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Thực thi bình thường (Có data) | `executeAudit()`, `consolidateFolioForBooking()` | `UC26-TC-001` |
| TC-COND-002 | Đã chạy rồi (E1) | `executeAudit()` check `AUDIT_LOG` | `UC26-TC-002` |
| TC-COND-003 | Lỗi từng phần (E3) | Transaction nội bộ của vòng lặp `for` | `UC26-TC-003` |
| TC-COND-004 | Không có data mới (A2) | `totalRecordsProcessed == 0` | `UC26-TC-004` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| State Transition Testing | Dữ liệu `status` | Bắt buộc Spa phải `COMPLETED`, F&B phải `DELIVERED`, GuestFolio phải `OPEN`. |
| Error Guessing | DB Constraint / Loop | Simulate lỗi khi Insert để test cơ chế Rollback riêng rẽ. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | 1 Guest Folio `OPEN`, 1 Spa `COMPLETED`, 1 F&B `DELIVERED` | Happy path |
| FX-002 | DB seed | Bảng `AUDIT_LOG` có sẵn `NIGHT_AUDIT_AUTO` cho hôm nay | Test E1 Reject |

---

## 4. Test Case Specification

- **Status**: 🔴 Not written / 🟡 Written-failing / 🟢 Passing

### `UC26-TC-001` — Chạy Night Audit thành công (Happy Path)
- **Severity**: CRITICAL
- **Feature Under Test**: `NightAuditServiceImpl.executeAudit`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/billing/service/NightAuditServiceTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Seed (FX-001): 1 Booking (ID=100) có GuestFolio `OPEN`. Có 1 dịch vụ Spa `COMPLETED` (500k) và 1 F&B `DELIVERED` (200k).
- Chưa có Audit Log nào trong ngày hôm nay.

#### Test Steps:
1. Arrange: Mock `FolioItemRepository` và DB theo FX-001.
2. Act: Gọi `nightAuditService.executeAudit("MANUAL", 1);`
3. Assert: Kiểm tra số lượng `FolioItem` sinh ra.

#### Expected Result (PASS):
- Sinh ra đúng 2 bản ghi `FOLIO_ITEM` (`service_category` = `SPA` và `F_AND_B`), `status` = `UNPAID`.
- Sinh ra 1 bản ghi `AUDIT_LOG` (`action_type` = `NIGHT_AUDIT_MANUAL`).
- `GUEST_FOLIO` có `total_extra_fb` tăng thêm 700k.

- **Current Status**: 🟢 Passing

---

### `UC26-TC-002` — Xử lý khi Audit đã chạy rồi (SRS E1)
- **Severity**: HIGH
- **Feature Under Test**: `NightAuditServiceImpl.executeAudit`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/billing/service/NightAuditServiceTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Seed (FX-002): Có Audit log `NIGHT_AUDIT_AUTO` chạy lúc 00:00 ngày hôm nay.

#### Test Steps:
1. Act: Manager gọi API `POST /billing/night-audit/execute` qua Controller / Service.
2. Assert: Bắt `NightAuditAlreadyExecutedException`.

#### Expected Result (PASS):
- Exception văng ra với message: "Night Audit has already been completed for this date."
- Controller bắt được, ném ra FlashMessage và HTTP 302 Redirect.

- **Current Status**: 🟢 Passing

---

### `UC26-TC-003` — Skip lỗi từng Folio (SRS E3)
- **Severity**: HIGH
- **Feature Under Test**: `FolioConsolidationService.consolidateAllActiveFolios`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/billing/service/FolioConsolidationServiceTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Seed: 2 GuestFolio (A và B) đang `OPEN`.
- Mock: Set cho Folio B bị văng `DataAccessException` khi execute Native Query.

#### Test Steps:
1. Act: Gọi `consolidateAllActiveFolios(1)`.
2. Assert: Kiểm tra danh sách kết quả (NightAuditResultDTO).

#### Expected Result (PASS):
- Folio A chốt thành công, Folio B bị catch Exception ghi log (Skip).
- Vòng lặp KHÔNG bị văng lỗi toàn cục làm chết Batch.
- DTO trả về số record của riêng Folio A.

- **Current Status**: 🟢 Passing

---

### `UC26-TC-004` — Không có dữ liệu mới để chốt (SRS A2)
- **Severity**: LOW
- **Feature Under Test**: `NightAuditServiceImpl.executeAudit`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/billing/service/NightAuditServiceTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Seed: Không có dịch vụ nào mang status `COMPLETED` hoặc `DELIVERED`.

#### Test Steps:
1. Act: Gọi `nightAuditService.executeAudit("AUTO", 0)`.
2. Assert: Check `NightAuditResultDTO` trả về.

#### Expected Result (PASS):
- `totalActiveFolios` = 0, `grandTotalRevenue` = 0.
- Sinh ra `AUDIT_LOG` với nội dung `details` thông báo "No new charges".

- **Current Status**: 🟢 Passing

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC26-TC-001` | `NightAuditServiceTest.java` | [x] | [x] | Xong |
| `UC26-TC-002` | `NightAuditServiceTest.java` | [x] | [x] | Xong |
| `UC26-TC-003` | `FolioConsolidationServiceTest.java` | [x] | [x] | Fix Circular Dependency |
| `UC26-TC-004` | `NightAuditServiceTest.java` | [x] | [x] | Xong |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Spec kỹ thuật `AURA-BILLING-IMP-026` đã được review và approve.
- [x] Database Schema và Naming Convention được chốt.

### Exit Criteria
- [x] Tất cả Unit Test Pass (🟢 GREEN).
- [x] Controller test pass cơ chế Redirect MVC.
