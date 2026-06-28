# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## Mẫu Đặc tả Kiểm thử Hướng Phát triển: Booking History & Itinerary

- **Document ID**: AURA-MOD5-TDD-034
- **Version**: 1.0
- **Date**: 2026-06-27
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Antigravity — Developer
- **Reviewed by**: [x] Tech Lead
- **DPO Sign-off**: [x] Approved
- **Approved by**: [x] Principal Architect
- **Classification**: Internal — Confidential

### References:
- `04_Implement/Module5/UC34/UC34_EDS_BookingHistory.md` (AURA-MOD5-IMP-034) — Technical Specification
- `01_Requirements/SRS.md` — Functional requirements
- `NĐ 356/2025` — Legal basis for Data Privacy

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`.java`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-27 | Antigravity | Khởi tạo tài liệu — TDD spec cho UC34 Booking History |

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
| **Feature / Gap ID** | UC34 |
| **Module** | Booking Tracking (Module 5) |
| **Spec gốc** | AURA-MOD5-IMP-034 |
| **Priority** | 🔴 P0 |
| **Sprint** | S2 (2026-06-15 -> 2026-06-29) |
| **Data Classification** | Confidential / Sensitive-PII |
| **Compliance Scope** | NĐ 356/2025 Điều X (Data Minimization) |
| **Upstream Dependencies** | Identity & Access Management, Booking Service |
| **Downstream Consumers** | Guest Dashboard, Manager Staff Profile |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Chưa có API riêng để lấy danh sách lịch sử Booking | Dùng chung 1 trang `/booking/history` truyền `guestId` | Controller xử lý check Role: MANAGER được dùng `guestId`, GUEST ép buộc dùng `currentUser.id` |
| L2 | Lịch sử Booking hiển thị chung Timeline | Tách biệt `BookingHistoryDTO` và `ItineraryTimelineDTO` | Viết 2 API / method riêng biệt: `/history` và `/itinerary` |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Booking Tracking` bao gồm các layer:
- Domain (Booking, Spa, F&B, Yoga Entities)
- Services (`ItineraryServiceImpl` mock repositories)
- Controller (`BookingHistoryController` mock services với MockMvc)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `AURA-MOD5-IMP-034` §3 | Phân quyền truy cập lịch sử (ADR-034) |
| `AURA-MOD5-IMP-034` §9 | Endpoint `/booking/history` và `/booking/itinerary` |
| NĐ 356/2025 | Chặn truy cập chéo GuestId (Data Privacy) |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Quyền GUEST chỉ xem lịch sử của mình | `BookingHistoryController` | `BKG-TC-001`, `BKG-TC-003` |
| TC-COND-002 | Quyền MANAGER xem lịch sử của mọi Guest | `BookingHistoryController` | `BKG-TC-002` |
| TC-COND-003 | Mapping dữ liệu Lịch trình chuẩn xác | `ItineraryServiceImpl` | `BKG-SVC-001`, `BKG-SVC-002` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Boundary Value Analysis | Parameter `guestId` | Đảm bảo tính năng anti-IDOR |
| State Transition Testing | Booking Status | Lọc booking trạng thái CHECKED_IN / CONFIRMED |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | Mock User | `GUEST` (id=1) | Khách hàng thông thường |
| FX-002 | Mock User | `MANAGER` (id=99) | Quản lý hệ thống |
| FX-003 | Mock Booking| `CHECKED_IN` booking data | Trả về timeline hợp lệ |

---

## 4. Test Case Specification

### `BKG-TC-001` — Guest Views Own History
- **Severity**: HIGH
- **Legal**: NĐ 356/2025 Data Privacy
- **Feature Under Test**: `BookingHistoryController.getBookingHistory()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/booking/controller/BookingHistoryControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Đã authenticate với role GUEST (ID: 1).

#### Test Steps:
1. Mock `itineraryService.getBookingHistory(1)` trả về danh sách rỗng.
2. Thực hiện `GET /booking/history` bằng `MockMvc`.
3. Kiểm tra HTTP Status, View Name, và Model attribute.

#### Expected Result (PASS):
- Status `200 OK`.
- View name `guest/itinerary_history`.
- `itineraryService.getBookingHistory(1)` được gọi đúng 1 lần.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Dùng `Principal` hoặc `UserDetailsResponse` để truyền context.

---

### `BKG-TC-002` — Manager Views Guest History
- **Severity**: HIGH
- **Feature Under Test**: `BookingHistoryController.getBookingHistory()`
- **Test File**: `BookingHistoryControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- Đã authenticate với role MANAGER (ID: 99).

#### Test Steps:
1. Thực hiện `GET /booking/history?guestId=2` bằng `MockMvc`.
2. Kiểm tra kết quả.

#### Expected Result (PASS):
- Status `200 OK`.
- `itineraryService.getBookingHistory(2)` được gọi (sử dụng guestId từ param thay vì ID của MANAGER).

- **Current Status**: 🔴 Not written

---

### `BKG-TC-003` — Guest Accesses Another Guest History (IDOR Prevention)
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-639 — Authorization Bypass Through User-Controlled Key
- **Legal**: NĐ 356/2025 Data Privacy
- **Feature Under Test**: `BookingHistoryController.getBookingHistory()`
- **Test File**: `BookingHistoryControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Đã authenticate với role GUEST (ID: 1).

#### Test Steps:
1. GUEST thực hiện `GET /booking/history?guestId=2` bằng `MockMvc`.
2. Kiểm tra service method nào được gọi.

#### Expected Result (PASS = hệ thống an toàn):
- Controller **phớt lờ** `guestId=2`.
- Gọi `itineraryService.getBookingHistory(1)` (ép dùng ID của GUEST).

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Kẻ tấn công xem được lịch sử đặt phòng của người khác.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Controller cần check role thủ công hoặc thông qua Annotation.

---

### `BKG-TC-004` — View Itinerary Details
- **Severity**: MEDIUM
- **Feature Under Test**: `BookingHistoryController.getBookingItinerary()`
- **Test File**: `BookingHistoryControllerTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Authenticated User.

#### Test Steps:
1. `GET /booking/itinerary?bookingId=100`
2. Kiểm tra View Name `guest/itinerary` và Model Attribute `timeline`.

#### Expected Result (PASS):
- Status `200 OK`.

- **Current Status**: 🔴 Not written

---

### `BKG-SVC-001` — ItineraryService Map Booking to History DTO
- **Severity**: MEDIUM
- **Feature Under Test**: `ItineraryServiceImpl.getBookingHistory(userId)`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Expected Result (PASS):
- Map Entity `Booking` thành `BookingHistoryDTO` (ẩn các trường không cần thiết). Trả về danh sách DTO hợp lệ.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `BKG-TC-001` | `BookingHistoryControllerTest.java` | [ ] | Pending | |
| `BKG-TC-002` | `BookingHistoryControllerTest.java` | [ ] | Pending | |
| `BKG-TC-003` | `BookingHistoryControllerTest.java` | [ ] | Pending | |
| `BKG-TC-004` | `BookingHistoryControllerTest.java` | [ ] | Pending | |
| `BKG-SVC-001` | `ItineraryServiceImplTest.java` | [ ] | Pending | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AURA-MOD5-IMP-034` đã được review và approve.
- [x] Test fixtures đã được chuẩn bị.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh.
- [ ] Controller áp dụng kiểm tra phân quyền (Anti-IDOR).
- [ ] UI History và Itinerary tách biệt theo chuẩn EDS v2.0.

---

## 7. Rollback Plan

### Revert implementation files
```bash
git restore 05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/BookingHistoryController.java
git restore 05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImpl.java
```
