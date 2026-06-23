# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AURAMOON-BOOKING-TDD-UC08
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
- `04_Implement/Module 2/EDS/UC08/UC08_EDS_Check_In_Guest.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.

---

## CHANGELOG

| Ngày       | Người thực hiện | Nội dung thay đổi                             |
| :--------- | :-------------- | :-------------------------------------------- |
| 2026-06-22 | AI Assistant    | Khởi tạo tài liệu TDD cho UC08 theo chuẩn v1.0 |

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
| **Feature / Gap ID** | UC08 |
| **Module** | `booking` |
| **Spec gốc** | `AURAMOON-BOOKING-EDS-UC08` |
| **Priority** | 🔴 P0 |
| **Sprint** | S1 |
| **Milestone** | M3 |
| **Data Classification** | Sensitive-PII (CCCD, Passport) |
| **Compliance Scope** | PDPA, Encryption |
| **Upstream Dependencies** | UC07 (Booking), UC09 (Villa Status) |
| **Downstream Consumers** | N/A |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Chưa ghi rõ mã hóa CCCD thế nào | Áp dụng `AesDataEncryptor` | Test Case xác nhận CCCD lưu xuống DB là cipher text |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`[UC08]` bao gồm các layer:
- Service (`CheckInServiceImpl`, `AesDataEncryptor`)
- Controller (`CheckInController`)
- Integration (`BookingRepository`, `VillaRepository`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC08 | Lễ tân thực hiện Check-in, CCCD được thu thập. |
| `UC08_EDS` | AES-256 mã hóa identifyCode. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Mã hóa CCCD trước khi lưu DB | `performCheckIn()` | `UC08-TC-001` |
| TC-COND-002 | Trạng thái Villa chuyển sang OCCUPIED | `updateVillaStatuses()` | `UC08-TC-002` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| State Transition Testing | Villa Status | Kiểm tra vòng đời phòng từ AVAILABLE -> OCCUPIED |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | Booking CONFIRMED, Villa AVAILABLE | Test Check-in happy path |

---

## 4. Test Case Specification

### `UC08-TC-001` — Check-in thành công và mã hóa CCCD
- **Severity**: CRITICAL
- **Feature Under Test**: `CheckInServiceImpl.performCheckIn()`
- **Test File**: `CheckInServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB Seed: FX-001.

#### Test Steps:
1. Gửi form check-in kèm CCCD: "001201010123".
2. Gọi `performCheckIn()`.
3. Kiểm tra record được lưu.

#### Expected Result (PASS):
- Status booking chuyển thành `CHECKED-IN`.
- Truy vấn DB trực tiếp: trường `identifyCode` chứa cipher text dạng Base64, không chứa plaintext "001201010123".

#### Expected Result (FAIL):
- Lưu plaintext CCCD xuống DB (Vi phạm bảo mật).

- **Current Status**: 🔴 Not written

---

## SECURITY TEST CASES

### `UC08-TC-002` — Ngăn chặn lộ lọt PII (IDOR / RBAC)
- **Severity**: CRITICAL
- **OWASP**: A01:2021-Broken Access Control
- **Feature Under Test**: `CheckInController`
- **Test File**: `CheckInSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- JWT Role là GUEST.

#### Test Steps:
1. Thực hiện gọi HTTP POST `/receptionist/check-in/execute`.
2. Kiểm tra Response.

#### Expected Result (PASS):
- `403 Forbidden`.

#### Expected Result (FAIL):
- Thực hiện được hàm checkin.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC08-TC-001` | `CheckInEncryptionTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước, JPA Converter tự động handle |
| `UC08-TC-002` | `CheckInSecurityTest.java` | [ ] | `[hash]` | N/A |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Khoá AES đã được cấp trong `.env` hoặc KMS.

### Exit Criteria
- [ ] Không có CCCD nào lưu dạng plain text ở test env.

---

## 7. Rollback Plan
- Xóa cache hoặc reset DB test nếu bị lộ key mock.
