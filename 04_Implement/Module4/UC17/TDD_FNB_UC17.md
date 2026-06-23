# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Đặc tả Kiểm thử Hướng Phát triển - UC17 Chef Dashboard

- **Document ID**: XOAIAURA-TDD-FNB-017
- **Version**: 1.0
- **Date**: 2026-06-23
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Lê Đức Dương — Backend Developer
- **Reviewed by**: [ ] Tech Lead — Pending
- **DPO Sign-off**: [ ] Pending
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `Retreat.md` — Functional requirements (UC17)
- `SDS_Document.md` — Database Schema
- `EDS_FNB_UC17.md` — Technical Specification

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-23 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC17 |

---

## 1. Thông vị Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-UC17 |
| **Module** | F&B / Kitchen |
| **Spec gốc** | XOAIAURA-FNB-IMP-017 |
| **Priority** | 🔴 P0 |
| **Data Classification** | Sensitive-PII (Chỉ giới hạn ở Dị ứng thức ăn) |
| **Compliance Scope** | Nghị định 356/2025 Điều 4 |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Đầu bếp có thể thấy cả bệnh lý y tế. | Lệnh Data Minimization cấm Đầu bếp xem thông tin y tế. | Test case đảm bảo DTO trả về cho Đầu bếp `ChefDashboardOrderResponse` CHỈ chứa `foodAllergies`, các trường bệnh lý khác không được query/truyền sang. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`F&B UC17` bao gồm các layer:
- Application / Services (`MealOrderServiceImpl`)
- Controller (`ChefFnbController`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `Retreat.md` UC17 | Xem bảng điều khiển tổng hợp đơn món ăn |
| Data Minimization | Không rò rỉ bệnh lý y tế cho Đầu bếp |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Hiển thị chính xác các đơn hàng trong ngày | `MealOrderServiceImpl.getChefDashboardOrders()` | `FNB-TC-003` |
| TC-COND-002 | Che giấu dữ liệu PII (Bệnh lý) | `MealOrderServiceImpl.getChefDashboardOrders()` | `FNB-TC-004` |

---

## 4. Test Case Specification

### `FNB-TC-003` — Lấy danh sách đơn đặt món trong ngày thành công
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderServiceImpl.getChefDashboardOrders()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có các MealOrder với `createdAt` rơi vào ngày hôm nay.

#### Test Steps:
1. Gọi hàm `getChefDashboardOrders(LocalDate.now())`
2. Kiểm tra danh sách `ChefDashboardOrderResponse` trả về.

#### Expected Result (PASS):
- Danh sách trả về chính xác số lượng đơn hàng, ánh xạ đúng các `items` của đơn đó.

#### Expected Result (FAIL):
- Sai số lượng đơn, lỗi do query sai khung thời gian.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-004` — Che giấu dữ liệu bệnh lý (Data Minimization)
- **Severity**: CRITICAL
- **Legal**: NĐ 356/2025 Data Minimization
- **Feature Under Test**: `MealOrderServiceImpl.getChefDashboardOrders()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DB có đơn hàng của khách hàng X.
- Khách hàng X có `DietaryProfile` chứa `foodAllergies` = "Peanuts" và bảng Health Profile (nếu có liên kết) chứa `medicalHistory` = "Heart disease".

#### Test Steps:
1. Gọi hàm `getChefDashboardOrders()`.
2. Kiểm tra thuộc tính của `ChefDashboardOrderResponse`.

#### Expected Result (PASS):
- Response chỉ chứa trường `foodAllergies` ("Peanuts"). Không có bất kỳ trường nào chứa "Heart disease".

#### Expected Result (FAIL):
- Bệnh sử nhạy cảm bị tiết lộ cho Đầu bếp -> Vi phạm nghiêm trọng NĐ 356.

- **Current Status**: 🟢 Written & Passed

---

## SECURITY TEST CASES

### `FNB-TC-SEC-002` — Kiểm soát truy cập Chef Dashboard
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-284 — Improper Access Control
- **Feature Under Test**: `ChefFnbController.getChefDashboard()`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- User có Role `GUEST`.

#### Test Steps:
1. GUEST gọi GET `/fnb/chef/dashboard`.

#### Expected Result (PASS):
- Ném ra lỗi 403 Forbidden.

#### Expected Result (FAIL):
- GUEST xem được toàn bộ đơn đặt món của khách hàng khác -> Rò rỉ dữ liệu.

- **Current Status**: 🟢 Written & Passed

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-003` | `UC17ChefDashboardTest.java` | [x] | [x] | |
| `FNB-TC-004` | `UC17ChefDashboardTest.java` | [x] | [x] | |
| `FNB-TC-SEC-002` | `UC17ChefDashboardTest.java` | [x] | [x] | |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvn test` — tất cả unit tests xanh.
- [x] Logic che giấu được 100% bệnh sử y tế, chỉ chừa lại `foodAllergies`.
- [x] Không rò rỉ quyền truy cập cho role GUEST.
