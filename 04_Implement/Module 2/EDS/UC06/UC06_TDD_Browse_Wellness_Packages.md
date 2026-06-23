# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AURAMOON-BOOKING-TDD-UC06
- **Version**: 1.0
- **Date**: 2026-06-22
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Lê Trà My
- **Reviewed by**: [ ] Phùng Giang Hải
- **DPO Sign-off**: [x] N/A — Không xử lý PII
- **Approved by**: [ ] Pending
- **Classification**: Internal

### References:
- `01_Requirements/Module 2/SRS_Document.md`
- `04_Implement/Module 2/EDS/UC06/UC06_EDS_Browse_Wellness_Packages.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`.spec.ts` / `.java`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.

---

## CHANGELOG

| Ngày       | Người thực hiện | Nội dung thay đổi                             |
| :--------- | :-------------- | :-------------------------------------------- |
| 2026-06-22 | AI Assistant    | Khởi tạo tài liệu TDD cho UC06 theo chuẩn v1.0 |

---

## MỤC LỤC
1. [Thông tin Module](#1-thông-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)
7. [Rollback Plan](#7-rollback-plan)

---

## 1. Thông tin Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | UC06 |
| **Module** | `booking` |
| **Spec gốc** | `AURAMOON-BOOKING-EDS-UC06` |
| **Priority** | 🟠 P1 |
| **Sprint** | S1 |
| **Milestone** | M3 |
| **Data Classification** | Public (Catalogue) |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | N/A (Root catalog) |
| **Downstream Consumers** | UC07 |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Bộ lọc chưa định nghĩa rõ ràng | Filter theo `typePackage`, `price`, `duration` | Test case phải bao phủ đa điều kiện lọc |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`[UC06]` bao gồm các layer:
- Service (`PackageServiceImpl`)
- Controller (`PackageController`)
- Integration (DB Queries với `PackageRepository`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC06 | Tính năng Browse và Filter Package |
| `UC06_EDS` | API Endpoints và Query logic |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Lấy danh sách tất cả các gói không filter | `getAllPackages()` | `UC06-TC-001` |
| TC-COND-002 | Lọc theo Type | `filterByType()` | `UC06-TC-002` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | Input filters | Giảm số lượng test case cần thiết |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | 3 Packages (1 Yoga, 1 Detox, 1 Stress) | Test lấy danh sách và lọc |

---

## 4. Test Case Specification

### `UC06-TC-001` — Lấy danh sách tất cả các Wellness Packages
- **Severity**: HIGH
- **Feature Under Test**: `PackageServiceImpl.getAllPackages()`
- **Test File**: `PackageServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có sẵn dữ liệu (FX-001).

#### Test Steps:
1. Gọi method `getAllPackages()`.
2. Kiểm tra danh sách trả về.

#### Expected Result (PASS):
- Trả về danh sách chứa đúng số lượng Package đang Active (isDelete = false).

#### Expected Result (FAIL):
- Trả về rỗng hoặc chứa cả package đã bị soft-delete.

- **Current Status**: 🔴 Not written

---

### `UC06-TC-002` — Lọc gói theo Package Type
- **Severity**: MEDIUM
- **Feature Under Test**: `PackageServiceImpl.filterPackagesByType()`
- **Test File**: `PackageServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DB có sẵn dữ liệu (FX-001).

#### Test Steps:
1. Gọi method `filterPackagesByType("Yoga")`.
2. Kiểm tra danh sách trả về.

#### Expected Result (PASS):
- Trả về chỉ các package có type là Yoga.

#### Expected Result (FAIL):
- Trả về sai loại hoặc ném exception nếu type không tồn tại (nên trả về list rỗng).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES

### `UC06-TC-INT-001` — Browse Packages API
- **Severity**: HIGH
- **Feature Under Test**: `GET /packages`
- **Test File**: `PackageControllerIntegrationTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- App đang chạy, H2/PostgreSQL container ready.
- DB Seeded.

#### Test Steps:
1. Gọi HTTP GET `/packages`.
2. Kiểm tra HTTP Status và JSON body / Thymeleaf Model.

#### Expected Result (PASS):
- `200 OK`.
- Model chứa danh sách packages hợp lệ.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC06-TC-001` | `RetreatPackageServiceTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước |
| `UC06-TC-002` | `RetreatPackageServiceTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Spec kỹ thuật UC06 EDS approved.
- [x] Data fixtures chuẩn bị xong.

### Exit Criteria
- [ ] Tất cả unit tests xanh.
- [ ] Tích hợp API test xanh.

---

## 7. Rollback Plan
- Revert code `git checkout -- ...`
