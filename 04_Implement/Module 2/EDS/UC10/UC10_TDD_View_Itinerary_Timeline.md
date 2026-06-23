# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AURAMOON-BOOKING-TDD-UC10
- **Version**: 1.0
- **Date**: 2026-06-22
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Lê Trà My
- **Reviewed by**: [ ] Phùng Giang Hải
- **DPO Sign-off**: [x] IDOR safe
- **Approved by**: [ ] Pending
- **Classification**: Internal

### References:
- `01_Requirements/Module 2/SRS_Document.md`
- `04_Implement/Module 2/EDS/UC10/UC10_EDS_View_Itinerary_Timeline.md`

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.

---

## CHANGELOG

| Ngày       | Người thực hiện | Nội dung thay đổi                             |
| :--------- | :-------------- | :-------------------------------------------- |
| 2026-06-22 | AI Assistant    | Khởi tạo tài liệu TDD cho UC10 theo chuẩn v1.0 |

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
| **Feature / Gap ID** | UC10 |
| **Module** | `booking` |
| **Spec gốc** | `AURAMOON-BOOKING-EDS-UC10` |
| **Priority** | 🔴 P0 |
| **Sprint** | S1 |
| **Milestone** | M3 |
| **Data Classification** | PII |
| **Compliance Scope** | IDOR Prevention |
| **Upstream Dependencies** | UC07, UC11, UC16 |
| **Downstream Consumers** | N/A |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Template sinh tĩnh | Đã chuyển sang query thực tế từ `SpaBooking` và `MealOrder` | Test kiểm tra dữ liệu trả về phải trùng khớp với dữ liệu mock của Spa và Meal |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`[UC10]` bao gồm các layer:
- Service (`ItineraryServiceImpl`)
- Controller (`ItineraryController`)
- Repository (`BookingRepository`, `SpaBookingRepository`, `MealOrderRepository`)

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Sinh lịch trình động với đủ check-in, check-out, spa, meal | `getTimelineForGuest()` | `UC10-TC-001` |
| TC-COND-002 | Tránh lọt dữ liệu (IDOR) | `showItinerary()` | `UC10-TC-002` |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | 1 Booking, 1 SpaBooking (15:00), 1 MealOrder (12:00) | Test happy path tạo động lịch trình |
| FX-002 | DB seed | Guest B không có Booking | Test IDOR và error handling |

---

## 4. Test Case Specification

### `UC10-TC-001` — Lấy Itinerary Timeline động thành công
- **Severity**: HIGH
- **Feature Under Test**: `ItineraryServiceImpl.getTimelineForGuest()`
- **Test File**: `ItineraryServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Mock `BookingRepository` trả về Booking ID 1001.
- Mock `SpaBookingRepository` trả về 1 SpaBooking lúc 15:00.
- Mock `MealOrderRepository` trả về 1 MealOrder lúc 12:00.

#### Test Steps:
1. Gọi `getTimelineForGuest(guestId)`.
2. Kiểm tra danh sách TimelineEvent.

#### Expected Result (PASS):
- Danh sách trả về chứa 4 sự kiện.
- Được sắp xếp theo thời gian tăng dần: Check-in -> Meal (12:00) -> Spa (15:00) -> Check-out.

#### Expected Result (FAIL):
- Danh sách trả về thiếu sự kiện, hoặc sai thứ tự thời gian.

- **Current Status**: 🔴 Not written

---

## SECURITY TEST CASES

### `UC10-TC-002` — Ngăn chặn Guest A xem Timeline của Guest B (IDOR)
- **Severity**: CRITICAL
- **OWASP**: A01:2021-Broken Access Control
- **Feature Under Test**: `ItineraryController`
- **Test File**: `ItinerarySecurityTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- JWT Token chứa `guestId = 100` (Guest A).
- DB có Timeline của Guest B (`guestId = 200`).

#### Test Steps (Attack Simulation):
1. Guest A gọi HTTP GET `/booking/itinerary`. (Lưu ý hệ thống KHÔNG nhận tham số guestId từ URL).
2. Kiểm tra Service layer nhận tham số nào.

#### Expected Result (PASS):
- `guestId` truyền vào service luôn là 100 (lấy trực tiếp từ JWT context).
- Guest A chỉ thấy được thông tin của mình.

#### Expected Result (FAIL):
- Hệ thống vô tình để lộ đường dẫn dạng `/booking/itinerary?guestId=200` và trả về data của B.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC10-TC-001` | `ItineraryTimelineServiceTest.java` | [x] | `[ ]` | Đang đợi sửa logic từ template tĩnh sang query động |
| `UC10-TC-002` | `ItinerarySecurityTest.java` | [ ] | `[hash]` | N/A |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Spec kỹ thuật UC10 (Bản thiết kế Động) đã được duyệt.

### Exit Criteria
- [ ] Tất cả unit tests xanh.

---

## 7. Rollback Plan
- Revert code `git checkout -- ...`
