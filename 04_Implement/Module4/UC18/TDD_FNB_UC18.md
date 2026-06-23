# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Đặc tả Kiểm thử Hướng Phát triển - UC18 Chef Order Status Update

- **Document ID**: XOAIAURA-TDD-FNB-018
- **Version**: 1.0
- **Date**: 2026-06-23
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Lê Đức Dương — Backend Developer
- **Reviewed by**: [ ] Tech Lead — Pending
- **DPO Sign-off**: [x] N/A
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `Retreat.md` — Functional requirements (UC18)
- `SDS_Document.md` — Database Schema
- `EDS_FNB_UC18.md` — Technical Specification

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-23 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC18 |

---

## 1. Thông vị Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-UC18 |
| **Module** | F&B / Kitchen |
| **Spec gốc** | XOAIAURA-FNB-IMP-018 |
| **Priority** | 🟡 P1 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Không có ràng buộc chiều trạng thái | Đơn bị lùi trạng thái tự do gây lỗi quy trình nghiệp vụ | Áp dụng State Machine Validation (chỉ cho phép tiến, không cho phép lùi). Thêm ngoại lệ FNB-001. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`F&B UC18` bao gồm các layer:
- Domain Logic (State Transition Validation)
- Application / Services (`MealOrderServiceImpl.updatePrepStatus`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `Retreat.md` UC18 | Đầu bếp cập nhật trạng thái món ăn |
| EDS-FNB-018 | State Machine (Chống lùi trạng thái) |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Cập nhật hợp lệ | `updatePrepStatus()` | `FNB-TC-005` |
| TC-COND-002 | Cập nhật đi lùi / nhảy cóc (Không hợp lệ) | `updatePrepStatus()` | `FNB-TC-006` |
| TC-COND-003 | Không tìm thấy Order | `updatePrepStatus()` | `FNB-TC-007` |

---

## 4. Test Case Specification

### `FNB-TC-005` — Cập nhật trạng thái chuẩn bị hợp lệ
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderServiceImpl.updatePrepStatus()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có `MealOrder` ID = 55 với status = `PENDING`.

#### Test Steps:
1. Gọi hàm `updatePrepStatus(55, "PREPARING")`.
2. Kiểm tra hàm `save()` của Repository được gọi.

#### Expected Result (PASS):
- Trạng thái được cập nhật thành `PREPARING` và được lưu thành công.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-006` — Chặn cập nhật lùi trạng thái
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderServiceImpl.updatePrepStatus()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DB có `MealOrder` ID = 55 với status = `READY`.

#### Test Steps:
1. Gọi hàm `updatePrepStatus(55, "PENDING")`.

#### Expected Result (PASS):
- Hàm ném ra ngoại lệ `FnbException` với mã lỗi `FNB-001`.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-007` — Cập nhật trạng thái đơn hàng không tồn tại
- **Severity**: LOW
- **Feature Under Test**: `MealOrderServiceImpl.updatePrepStatus()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- `orderId` = 9999 không tồn tại trong DB.

#### Test Steps:
1. Gọi hàm `updatePrepStatus(9999, "PREPARING")`.

#### Expected Result (PASS):
- Hàm ném ra ngoại lệ `FnbException` với mã lỗi `FNB-003` (Order not found).

- **Current Status**: 🟢 Written & Passed

---

## SECURITY TEST CASES

### `FNB-TC-SEC-003` — Kiểm soát truy cập (RBAC) Cập nhật trạng thái
- **Severity**: HIGH
- **OWASP**: A01:2021 — Broken Access Control
- **Feature Under Test**: `ChefFnbController.updatePrepStatus()`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- User có Role `GUEST`.

#### Test Steps:
1. GUEST gửi POST `/fnb/chef/order/55/status`.

#### Expected Result (PASS):
- Bị từ chối bằng `403 Forbidden` do GUEST không có quyền `CHEF`.

- **Current Status**: 🟢 Written & Passed

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-005` | `UC18ChefOrderTest.java` | [x] | [x] | |
| `FNB-TC-006` | `UC18ChefOrderTest.java` | [x] | [x] | |
| `FNB-TC-007` | `UC18ChefOrderTest.java` | [x] | [x] | |
| `FNB-TC-SEC-003`| `UC18ChefOrderTest.java` | [x] | [x] | |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvn test` — tất cả unit tests xanh.
- [x] Logic State Machine chạy chuẩn (cấm lùi bước).
- [x] Phân quyền chỉ có CHEF mới được đổi trạng thái đơn.
