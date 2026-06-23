# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Đặc tả Kiểm thử Hướng Phát triển - UC16 Meal Selection

- **Document ID**: XOAIAURA-TDD-FNB-016
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
- `Retreat.md` — Functional requirements (UC16)
- `SDS_Document.md` — Database Schema
- `EDS_FNB_UC16.md` — Technical Specification

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-23 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC16 |

---

## 1. Thông vị Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-UC16 |
| **Module** | F&B / Dietary |
| **Spec gốc** | XOAIAURA-FNB-IMP-016 |
| **Priority** | 🔴 P0 |
| **Data Classification** | Sensitive-PII |
| **Compliance Scope** | Nghị định 356/2025 Điều 4 |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Chưa có logic lọc dị ứng | `DIETARY_PROFILE.food_allergies` so khớp `MENU_ITEM.ingredient` | Test case phải mô phỏng khách bị dị ứng "peanut" và đảm bảo món ăn có "đậu phộng" bị loại trừ. |
| L2 | Tính phí kép UC16 | `GUEST_FOLIO.total_extra_fb` | Món UC16 không được tính phí phụ thu (trái ngược với UC19). |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`F&B UC16` bao gồm các layer:
- Domain (pure logic: `hasAllergyConflict`)
- Application / Services (`MealOrderServiceImpl`)
- Controller (`MealOrderController`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `Retreat.md` UC16 | Lọc món ăn dị ứng tự động |
| Data Minimization | Không rò rỉ bệnh lý y tế |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Lọc thực đơn có thành phần dị ứng | `MealOrderServiceImpl.getFilteredMenu()` | `FNB-TC-001` |
| TC-COND-002 | Khách hàng cố tình gọi món dị ứng | `MealOrderServiceImpl.createMealOrder()` | `FNB-TC-002` |

### TDS-05 — Test Data Requirements
| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | Khách hàng bị dị ứng `peanut` | Test lọc món ăn |
| FX-002 | DB seed | Món ăn có `ingredient = "bơ đậu phộng"` | Test thuật toán so khớp |

---

## 4. Test Case Specification

### `FNB-TC-001` — Lọc món ăn gây dị ứng khỏi thực đơn
- **Severity**: HIGH
- **Legal**: NĐ 356/2025 Data Minimization & Safety
- **Feature Under Test**: `MealOrderServiceImpl.getFilteredMenu()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có FX-001 (khách dị ứng `peanut`) và FX-002 (món `bơ đậu phộng`).

#### Test Steps:
1. Gọi hàm `getFilteredMenu(guestId, bookingId)`
2. Kiểm tra danh sách `MenuItemResponse` trả về.

#### Expected Result (PASS):
- Danh sách trả về không chứa món ăn có id tương ứng với FX-002.

#### Expected Result (FAIL):
- Danh sách trả về vẫn chứa FX-002 -> Rủi ro chết người cho khách hàng.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-002` — Chặn đơn hàng chứa món dị ứng do cố tình submit
- **Severity**: CRITICAL
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DB có FX-001 và FX-002.

#### Test Steps:
1. Khởi tạo `MealOrderRequest` chứa `menuItemId` của FX-002.
2. Gọi hàm `createMealOrder()`.

#### Expected Result (PASS):
- Băng ra ngoại lệ `FnbException` với code `FNB-001` và thông báo chứa tên thành phần dị ứng bị vi phạm.

#### Expected Result (FAIL):
- Đơn hàng được tạo thành công -> Vi phạm quy tắc an toàn.

- **Current Status**: 🟢 Written & Passed

---

## SECURITY TEST CASES

### `FNB-TC-SEC-001` — Đặt món ăn cho người khác (IDOR)
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-284 — Improper Access Control
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- User A (guestId = 1) đã đăng nhập.
- Booking B (bookingId = 99) thuộc về User B.

#### Test Steps:
1. User A gọi `createMealOrder()` truyền vào `bookingId = 99`.
2. Kiểm tra kết quả trả về.

#### Expected Result (PASS):
- Ném ra lỗi `FNB-004: User does not own this booking`.

#### Expected Result (FAIL):
- Đơn đặt món được tạo cho Booking B, bị tính phí vào phòng của User B.

- **Current Status**: 🟢 Written & Passed

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-001` | `UC16MealSelectionTest.java` | [x] | [x] | |
| `FNB-TC-002` | `UC16MealSelectionTest.java` | [x] | [x] | |
| `FNB-TC-SEC-001` | `UC16MealSelectionTest.java` | [x] | [x] | |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvn test` — tất cả unit tests xanh.
- [x] Logic chặn được món chứa nguyên liệu dị ứng (cả tiếng Anh và tiếng Việt như `peanut`/`đậu phộng`).
- [x] Không rò rỉ IDOR khi gọi món khác booking.
