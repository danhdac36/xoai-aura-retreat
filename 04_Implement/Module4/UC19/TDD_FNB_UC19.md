# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Đặc tả Kiểm thử Hướng Phát triển - UC19 A-la-carte Order

- **Document ID**: XOAIAURA-TDD-FNB-019
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
- `Retreat.md` — Functional requirements (UC19)
- `SDS_Document.md` — Database Schema
- `EDS_FNB_UC19.md` — Technical Specification

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-23 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC19 |

---

## 1. Thông vị Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-UC19 |
| **Module** | F&B / Billing |
| **Spec gốc** | XOAIAURA-FNB-IMP-019 |
| **Priority** | 🔴 P0 |
| **Data Classification** | Internal |
| **Compliance Scope** | Hạn chế món ăn có hại theo NĐ 356/2025 |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Chưa ghi nợ món A-la-carte vào Billing | GuestFolio bị thất thoát tiền doanh thu | Ép Transaction: Nếu `isExtraCharge=true`, F&B service phải update thành công tổng `totalExtraFb` vào `GuestFolio`. Test case mô phỏng sự tăng lên của Folio Balance. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`F&B UC19` bao gồm các layer:
- Domain Logic (Allergy filter)
- Application / Services (`MealOrderServiceImpl.createMealOrder` with `isExtraCharge=true`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `Retreat.md` UC19 | Gọi món ngoài & Tính phí vào Villa |
| EDS-FNB-019 | Transaction ACID update Folio |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Gọi món thành công & Ghi nợ | `createMealOrder()` | `FNB-TC-008` |
| TC-COND-002 | Số lượng không hợp lệ | `createMealOrder()` | `FNB-TC-009` |
| TC-COND-003 | Món ăn không tồn tại | `createMealOrder()` | `FNB-TC-010` |
| TC-COND-004 | Xung đột nguyên liệu dị ứng | `createMealOrder()` | `FNB-TC-011` |

---

## 4. Test Case Specification

### `FNB-TC-008` — Gọi món A-la-carte và ghi nợ thành công
- **Severity**: HIGH
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có `Booking` ID 100 hợp lệ.
- DB có `MenuItem` ID 1 (Giá: 15,000 VND).
- DB có `GuestFolio` tương ứng với tổng phí phụ thu (F&B) hiện tại là 50,000 VND.

#### Test Steps:
1. Gọi `createMealOrder` với request chứa Item ID 1 x 2 (Tổng 30,000 VND), `isExtraCharge=true`.
2. Theo dõi repository `GuestFolioRepository.save()` và `FolioItemRepository.save()`.

#### Expected Result (PASS):
- Trả về `MealOrderResponse`.
- Thuộc tính `totalExtraFb` của Folio tăng từ 50,000 lên 80,000.
- `FolioItem` được tạo mới.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-009` — Gọi món với số lượng không hợp lệ
- **Severity**: LOW
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- Dữ liệu request truyền lên `quantity` = -5.

#### Test Steps:
1. Gọi `createMealOrder()`.

#### Expected Result (PASS):
- Ném ra `FnbException` lỗi `FNB-002`.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-010` — Khách hàng gọi món không tồn tại
- **Severity**: LOW
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- Dữ liệu request truyền lên `menuItemId` = 999 (không có trong DB).

#### Test Steps:
1. Gọi `createMealOrder()`.

#### Expected Result (PASS):
- Ném ra `FnbException` lỗi `FNB-003`.

- **Current Status**: 🟢 Written & Passed

---

### `FNB-TC-011` — Khách gọi món chứa nguyên liệu cấm kỵ (Dị ứng)
- **Severity**: CRITICAL
- **Feature Under Test**: `MealOrderServiceImpl.createMealOrder()`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- DB có Guest bị dị ứng "Peanuts" và "Shellfish".
- Món ăn được chọn chứa "đậu phộng".

#### Test Steps:
1. Gọi `createMealOrder()`.

#### Expected Result (PASS):
- Ném ra `FnbException` lỗi `FNB-001`. Đơn hàng bị block hoàn toàn, không tạo hóa đơn.

- **Current Status**: 🟢 Written & Passed

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-008` | `UC19AlacarteOrderTest.java` | [x] | [x] | |
| `FNB-TC-009` | `UC19AlacarteOrderTest.java` | [x] | [x] | |
| `FNB-TC-010` | `UC19AlacarteOrderTest.java` | [x] | [x] | |
| `FNB-TC-011` | `UC19AlacarteOrderTest.java` | [x] | [x] | |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvn test` — tất cả unit tests xanh.
- [x] Logic cộng gộp tiền tự động cập nhật được GuestFolio mà không bị rớt dữ liệu (Transaction).
- [x] Khóa an toàn Allergy filter vẫn hoạt động bình thường trên UC19.
