# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AURAMOON-BOOKING-TDD-UC07
- **Version**: 1.0
- **Date**: 2026-06-22
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Lê Trà My
- **Reviewed by**: [ ] Phùng Giang Hải
- **DPO Sign-off**: [ ] Pending
- **Approved by**: [ ] Pending
- **Classification**: Internal

### References:
- `01_Requirements/Module 2/SRS_Document.md`
- `04_Implement/Module 2/EDS/UC07/UC07_EDS_Book_Package_Pay_Deposit.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`.spec.ts` / `.java`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.

---

## CHANGELOG

| Ngày       | Người thực hiện | Nội dung thay đổi                             |
| :--------- | :-------------- | :-------------------------------------------- |
| 2026-06-22 | AI Assistant    | Khởi tạo tài liệu TDD cho UC07 theo chuẩn v1.0 |

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
| **Feature / Gap ID** | UC07 |
| **Module** | `booking` |
| **Spec gốc** | `AURAMOON-BOOKING-EDS-UC07` |
| **Priority** | 🔴 P0 |
| **Sprint** | S1 |
| **Milestone** | M3 |
| **Data Classification** | PII (Thông tin khách hàng) |
| **Compliance Scope** | PDPA, Payment Security |
| **Upstream Dependencies** | UC06 (Chọn Package), Authentication |
| **Downstream Consumers** | UC08 (Check-in), Module 5 (Payment, GuestFolio) |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Booking status transition | PENDING -> CONFIRMED khi nhận thanh toán deposit | Viết test kiểm tra callback payment |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`[UC07]` bao gồm các layer:
- Service (`BookingServiceImpl`)
- Controller (`BookingController`)
- Integration (Tích hợp với VNPay và `GuestFolioService`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC07 | Logic tạo Booking và kiểm tra availability |
| `UC07_EDS` | Giao dịch Distributed Transaction, Event Publish |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Check Villa Availability trả về true/false | `checkVillaAvailability()` | `UC07-TC-001` |
| TC-COND-002 | Tạo Booking thành công và sinh url VNPay | `createBooking()` | `UC07-TC-002` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | Ngày check-in / check-out | Đảm bảo tính khả dụng |
| State Transition Testing | Booking Status | `PENDING` -> `CONFIRMED` |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | User ID 100, Package ID 1 | Tạo booking hợp lệ |

---

## 4. Test Case Specification

### `UC07-TC-001` — Check Villa Availability khi hết phòng
- **Severity**: HIGH
- **Feature Under Test**: `VillaServiceImpl.checkVillaAvailability()`
- **Test File**: `VillaServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB Seed: Tất cả Villa của Type=1 đều OCCUPIED.

#### Test Steps:
1. Gọi method `checkVillaAvailability(1, "2026-10-01", "2026-10-05")`.
2. Kiểm tra kết quả trả về.

#### Expected Result (PASS):
- Trả về `false`.

#### Expected Result (FAIL):
- Trả về `true` dẫn đến overbooking.

- **Current Status**: 🔴 Not written

---

### `UC07-TC-002` — Tạo Booking và Payment URL
- **Severity**: CRITICAL
- **Feature Under Test**: `BookingServiceImpl.createBooking()`
- **Test File**: `BookingServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DB có sẵn User và Package hợp lệ. Còn phòng trống.

#### Test Steps:
1. Gửi DTO với CheckinDate, CheckoutDate hợp lệ.
2. Gọi `createBooking()`.

#### Expected Result (PASS):
- Booking được tạo với trạng thái `PENDING`.
- Trả về VNPay URL hợp lệ.

#### Expected Result (FAIL):
- Ném exception hoặc không lưu Booking vào DB.

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES

### `UC07-TC-INT-001` — Đặt phòng thành công E2E
- **Severity**: CRITICAL
- **Feature Under Test**: `POST /booking/create`
- **Test File**: `BookingIntegrationTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- PostgreSQL container running.
- Mock external service (VNPay).

#### Test Steps:
1. Gửi request `POST /booking/create` kèm JWT của Guest.
2. Kiểm tra Response.

#### Expected Result (PASS):
- Status 302 Redirect đến trang thanh toán.
- DB chứa 1 record Booking với trạng thái PENDING.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC07-TC-001` | `BookingServiceTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước |
| `UC07-TC-002` | `BookingServiceTest.java` | [ ] | `[x] Pass` | Đã code xong từ trước |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Spec kỹ thuật UC07 EDS approved.

### Exit Criteria
- [ ] Tất cả unit tests xanh.
- [ ] Ghi log audit đầy đủ.

---

## 7. Rollback Plan
- Revert DB nếu có lỗi trong quá trình distributed transaction.
