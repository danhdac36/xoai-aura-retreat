# TEST-DRIVEN DEVELOPMENT SPECIFICATION - UC17 & UC18 & UC20
## Đặc tả Kiểm thử Hướng Phát triển - Phân hệ nhà bếp F&B

- **Document ID**: SWP391-FNB-TDD-UC17
- **Version**: 1.0
- **Date**: 2026-06-12
- **Status**: Draft / In Review
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Student 4 — F&B Module Owner
- **Reviewed by**: Team Lead
- **DPO Sign-off**: [ ] Pending
- **Approved by**: Principal Architect
- **Classification**: Internal — Confidential

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-12 | Student 4 | Khởi tạo tài liệu TDD spec cho Bảng điều phối bếp & Quy trình trạng thái đơn hàng |

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
| **Feature / Gap ID** | GAP-FNB-KDS |
| **Module** | Dietary F&B Management - Kitchen Display System (KDS) |
| **Spec gốc** | `SWP391-FNB-IMP-UC17` |
| **Priority** | 🔴 P0 |
| **Sprint** | S2 (2026-06-05 -> 2026-06-19) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Sensitive-PII |
| **Compliance Scope** | Nghị định 356/2025/NĐ-CP Điều 4 (Bảo vệ dữ liệu cá nhân y tế nhạy cảm) |
| **Upstream Dependencies** | `auth` (User), `booking` (Booking), `fnb` (MealOrder, DietaryProfile) |
| **Downstream Consumers** | `fnb` (KDS Dashboard View) |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Chưa ràng buộc luồng chuyển trạng thái đơn hàng một chiều ở Backend. | Quy tắc BR-16: Chỉ được đi theo luồng Pending -> Preparing -> Ready. | Viết test kiểm tra và chặn các luồng chuyển đổi trạng thái không hợp lệ (ví dụ: READY -> PREPARING). |
| L2 | Chưa áp dụng giảm thiểu dữ liệu khi Chef truy vấn KDS Dashboard. | Quy tắc BR-07: Chef chỉ được thấy dị ứng thực phẩm, hồ sơ bệnh lý vật lý phải ẩn. | Viết test kiểm tra xem các thông tin y tế nhạy cảm không liên quan đến ẩm thực có được lọc bỏ khỏi API hay không. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`MealOrderService` bao gồm các layer được kiểm thử:
- Application / Services Layer: Kiểm thử thông qua Mockito giả lập cơ sở dữ liệu (`MealOrderRepository`, `DietaryProfileRepository`, `BookingRepository`).

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC17, UC18, UC20 | Giao diện điều phối bếp, cập nhật trạng thái và giảm thiểu dữ liệu nhạy cảm. |
| BR-07 | F&B Staff chỉ được xem dị ứng và sở thích ăn uống. Bệnh lý vật lý phải bị ẩn. |
| BR-16 | Trạng thái đơn hàng chỉ được tiến một chiều: `PENDING` -> `PREPARING` -> `READY`. |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Đảm bảo Dashboard chỉ trả về dị ứng thực phẩm, loại bỏ bệnh lý vật lý. | `MealOrderService.getDailyMealOrders()` | `FNB-TC-001` |
| TC-COND-002 | Đổi trạng thái hợp lệ từ PENDING -> PREPARING. | `MealOrderService.updateMealOrderStatus()` | `FNB-TC-002` |
| TC-COND-003 | Đổi trạng thái hợp lệ từ PREPARING -> READY. | `MealOrderService.updateMealOrderStatus()` | `FNB-TC-003` |
| TC-COND-004 | Chặn đổi trạng thái ngược từ READY -> PREPARING. | `MealOrderService.updateMealOrderStatus()` | `FNB-TC-004` |
| TC-COND-005 | Chặn đổi trạng thái ngược từ PREPARING -> PENDING. | `MealOrderService.updateMealOrderStatus()` | `FNB-TC-005` |

---

## 4. Test Case Specification

### `FNB-TC-001` — Tải dashboard và kiểm tra giảm thiểu dữ liệu (Allergy warning & Medical info masked)
- **Severity**: HIGH
- **OWASP**: A04:2021-Insecure Design
- **Legal**: Nghị định 356/2025/NĐ-CP
- **Feature Under Test**: `MealOrderService.getDailyMealOrders()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealOrderServiceTest`
- **TDD Phase**: 🔴 RED — chưa implement logic giảm thiểu dữ liệu và truy vấn dashboard.

#### Preconditions:
- Mock `MealOrderRepository` trả về danh sách MealOrders của ngày 2026-06-12.
- Mock `DietaryProfileRepository` trả về DietaryProfile có `foodAllergies = "peanut"` và giả lập có dữ liệu bệnh lý vật lý (ví dụ: đau lưng, huyết áp).

#### Test Steps:
1. Gọi hàm `getDailyMealOrders(LocalDate.of(2026, 6, 12))`.
2. Kiểm tra danh sách kết quả trả về (`List<MealPrepResponse>`).
3. Kiểm tra xem trường dị ứng thực phẩm có chứa `"peanut"` hay không.
4. Kiểm tra xem các trường bệnh lý vật lý có xuất hiện trong response hay không (mong muốn: không tồn tại/không có).

#### Expected Result (PASS):
- Trả về đúng danh sách MealPrepResponse.
- `foodAllergies` của khách hàng được hiển thị là `"peanut"`.
- Không chứa bất kỳ thông tin bệnh lý vật lý nhạy cảm nào khác.

---

### `FNB-TC-002` — Đổi trạng thái hợp lệ từ PENDING sang PREPARING
- **Severity**: MEDIUM
- **Feature Under Test**: `MealOrderService.updateMealOrderStatus()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealOrderServiceTest`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Mock `MealOrderRepository` trả về MealOrder có status ban đầu là `"PENDING"`.

#### Test Steps:
1. Gọi hàm `updateMealOrderStatus(1, "PREPARING")`.
2. Kiểm tra xem status trả về là `"PREPARING"`.

#### Expected Result (PASS):
- Status được cập nhật thành công và trả về trạng thái mới.

---

### `FNB-TC-003` — Đổi trạng thái hợp lệ từ PREPARING sang READY
- **Severity**: MEDIUM
- **Feature Under Test**: `MealOrderService.updateMealOrderStatus()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealOrderServiceTest`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Mock `MealOrderRepository` trả về MealOrder có status ban đầu là `"PREPARING"`.

#### Test Steps:
1. Gọi hàm `updateMealOrderStatus(1, "READY")`.
2. Kiểm tra xem status trả về là `"READY"`.

---

### `FNB-TC-004` — Chặn đổi trạng thái ngược từ READY sang PREPARING
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.updateMealOrderStatus()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealOrderServiceTest`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Mock `MealOrderRepository` trả về MealOrder có status ban đầu là `"READY"`.

#### Test Steps:
1. Gọi hàm `updateMealOrderStatus(1, "PREPARING")`.
2. Xác nhận hệ thống chặn lại và trả về lỗi phản hồi hoặc ném ra ngoại lệ `IllegalArgumentException` / `FNB-003`.

#### Expected Result (PASS):
- Trả về phản hồi lỗi `"FAILED"` hoặc ném ra lỗi, không cho phép cập nhật CSDL.

---

### `FNB-TC-005` — Chặn đổi trạng thái ngược từ PREPARING sang PENDING
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.updateMealOrderStatus()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealOrderServiceTest`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Mock `MealOrderRepository` trả về MealOrder có status ban đầu là `"PREPARING"`.

#### Test Steps:
1. Gọi hàm `updateMealOrderStatus(1, "PENDING")`.
2. Xác nhận hệ thống chặn lại và trả về lỗi phản hồi hoặc ném ra ngoại lệ.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-001` | `MealOrderServiceTest.java` | [ ] | [Pending] | |
| `FNB-TC-002` | `MealOrderServiceTest.java` | [ ] | [Pending] | |
| `FNB-TC-003` | `MealOrderServiceTest.java` | [ ] | [Pending] | |
| `FNB-TC-004` | `MealOrderServiceTest.java` | [ ] | [Pending] | |
| `FNB-TC-005` | `MealOrderServiceTest.java` | [ ] | [Pending] | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- Thiết kế `SWP391-FNB-IMP-UC17.md` được phê duyệt.
- Dữ liệu mẫu bảng `MEAL_ORDER` đã được khai báo.

### Exit Criteria (Điều kiện kết thúc)
- Bộ unit test chạy qua 100% không gặp lỗi.
- Đảm bảo tính bất biến của trạng thái (BR-16) và giảm thiểu dữ liệu nhạy cảm (BR-07, UC20).

---

## 7. Rollback Plan

### Revert implementation files
```bash
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealOrderService.java
```
