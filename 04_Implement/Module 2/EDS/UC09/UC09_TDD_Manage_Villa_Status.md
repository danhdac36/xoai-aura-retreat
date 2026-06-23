# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AURAMOON-BOOKING-TDD-UC09
- **Version**: 1.0
- **Date**: 2026-06-22
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Lê Trà My
- **Reviewed by**: [ ] Phùng Giang Hải
- **DPO Sign-off**: [ ] Pending
- **Approved by**: [ ] Pending
- **Classification**: Internal

### References:
- `01_Requirements/Module 2/SRS_Document.md`
- `04_Implement/Module 2/EDS/UC09/UC09_EDS_Manage_Villa_Status.md`
- `04_Implement/Module 5/UC28/UC28_EDS_Housekeeping.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.

---

## CHANGELOG

| Ngày       | Người thực hiện | Nội dung thay đổi                             |
| :--------- | :-------------- | :-------------------------------------------- |
| 2026-06-22 | AI Assistant    | Khởi tạo tài liệu TDD cho UC09 theo chuẩn v1.0 |

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
| **Feature / Gap ID** | UC09 |
| **Module** | `booking` |
| **Spec gốc** | `AURAMOON-BOOKING-EDS-UC09` |
| **Priority** | 🟡 P2 |
| **Sprint** | S1 |
| **Milestone** | M3 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | UC28 (Housekeeping update cleaningStatus) |
| **Downstream Consumers** | N/A |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | UC09 cho phép Receptionist đổi status dọn dẹp | Cập nhật theo quyết định mới: Lễ tân chỉ xem bảng trạng thái (view-only), thao tác dọn dẹp thuộc về UC28 | Test case verify tính view-only của Dashboard Lễ tân |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`[UC09]` bao gồm các layer:
- Service (`VillaServiceImpl.getVillaStatuses()`)
- Controller (`VillaController.showVillaStatusDashboard()`)
- Integration (`VillaRepository`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| Yêu cầu thay đổi | UC09 Lễ tân chỉ được nhìn bằng mắt xem phòng còn sạch không. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Hiển thị đúng trạng thái villaStatus và cleaningStatus | `getVillaStatuses()` | `UC09-TC-001` |
| TC-COND-002 | Receptionist không có quyền gọi API sửa đổi cleaningStatus | Authorization layer | `UC09-TC-002` |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | 1 phòng AVAILABLE+CLEAN, 1 phòng OCCUPIED, 1 phòng DIRTY | Kiểm tra view data mapping |

---

## 4. Test Case Specification

### `UC09-TC-001` — Lấy danh sách trạng thái phòng hiển thị lên Dashboard
- **Severity**: HIGH
- **Feature Under Test**: `VillaServiceImpl.getVillaStatuses()`
- **Test File**: `VillaServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có sẵn dữ liệu Villa (FX-001).

#### Test Steps:
1. Gọi `getVillaStatuses()`.
2. Map dữ liệu vào DTO.

#### Expected Result (PASS):
- Dữ liệu trả về phân nhóm đúng: Phòng Sẵn sàng, Phòng Đang ở, Phòng Cần dọn.
- Các thuộc tính `villaStatus` và `cleaningStatus` được map chính xác.

#### Expected Result (FAIL):
- Bỏ sót trạng thái kết hợp (VD: AVAILABLE nhưng DIRTY).

- **Current Status**: 🔴 Not written

---

## SECURITY TEST CASES

### `UC09-TC-002` — Ngăn Lễ Tân đổi trạng thái dọn dẹp (RBAC)
- **Severity**: HIGH
- **OWASP**: A01:2021-Broken Access Control
- **Feature Under Test**: API thay đổi trạng thái (thuộc UC28)
- **Test File**: `HousekeepingSecurityTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- JWT Role: RECEPTIONIST.

#### Test Steps:
1. Thực hiện gọi HTTP POST `/housekeeping/approve`.
2. Kiểm tra Response.

#### Expected Result (PASS):
- `403 Forbidden` do Lễ tân không có quyền đổi `cleaning_status`.

#### Expected Result (FAIL):
- Request thành công (Lễ tân đổi được trạng thái dọn dẹp -> Vi phạm quy tắc).

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC09-TC-001` | `VillaDisplayServiceTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước |
| `UC09-TC-002` | `HousekeepingSecurityTest.java` | [ ] | `[hash]` | N/A |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] DB đã phân tách 2 trường status.

### Exit Criteria
- [ ] Không tồn tại nút/form cho phép Lễ tân sửa `cleaningStatus` trên giao diện.

---

## 7. Rollback Plan
- N/A (View only feature).
