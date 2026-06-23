# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Đặc tả Kiểm thử Hướng Phát triển - UC20 Data Minimization (F&B)

- **Document ID**: XOAIAURA-TDD-FNB-020
- **Version**: 1.0
- **Date**: 2026-06-23
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Lê Đức Dương — Backend Developer
- **Reviewed by**: [ ] Tech Lead — Pending
- **DPO Sign-off**: [x] NĐ 356/2025 - Compliance Verified
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `Retreat.md` — Functional requirements (UC20)
- `EDS_FNB_UC20.md` — Technical Specification

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-23 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC20 |

---

## 1. Thông vị Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-UC20 |
| **Module** | F&B / Security / Data Privacy |
| **Spec gốc** | XOAIAURA-FNB-IMP-020 |
| **Priority** | 🔴 P0 (Compliance) |
| **Data Classification** | Sensitive-PII |
| **Compliance Scope** | Hạn chế dữ liệu y tế đối với khối Bếp |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Đầu bếp có thể truy cập `medicalHistory` | Vi phạm NĐ 356 | DTO Controller trả về không được chứa bất kỳ field nào có tên `medicalHistory`, `healthNotes`... |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`F&B UC20` (Nằm chung với scope của UC17 - Chef Dashboard):
- DTO Mapping (`ChefDashboardOrderResponse`)
- `MealOrderServiceImpl`

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `Retreat.md` UC20 | Đầu bếp không được xem bệnh lý, chỉ xem dị ứng |
| NĐ 356/2025 | Nguyên tắc hạn chế dữ liệu (Data Minimization) |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | DTO không chứa bệnh lý | `getChefDashboardOrders()` | `FNB-TC-004` (Được tái sử dụng từ UC17) |

---

## 4. Test Case Specification

### `FNB-TC-004` — Che giấu dữ liệu bệnh lý (Data Minimization)
*(Ghi chú: Test Case này được ánh xạ và thực thi tại `UC17ChefDashboardTest.java` do UC20 là một Non-Functional Requirement gắn chặt vào UC17)*

- **Severity**: CRITICAL
- **Feature Under Test**: `MealOrderServiceImpl.getChefDashboardOrders()`, DTO `ChefDashboardOrderResponse`
- **TDD Phase**: 🟢 GREEN
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- DB có đơn hàng của khách hàng X.
- Khách hàng X có bảng `DietaryProfile` chứa `foodAllergies` = "Peanuts" và `medicalHistory` = "Heart disease".

#### Test Steps:
1. Gọi hàm `getChefDashboardOrders()`.
2. Kiểm tra thuộc tính của `ChefDashboardOrderResponse`.
3. Dùng Java Reflection để cố gắng tìm hàm `getMedicalHistory()` trong DTO.

#### Expected Result (PASS):
- DTO Response chỉ chứa trường `foodAllergies` ("Peanuts").
- Java Reflection ném lỗi `NoSuchMethodException` khi quét hàm `getMedicalHistory()`. Không có bất kỳ trường nào chứa "Heart disease".

- **Current Status**: 🟢 Written & Passed

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-004` | `UC17ChefDashboardTest.java` | [x] | [x] | Test được nhúng trực tiếp trong UC17 |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] Không tồn tại bất kỳ Entity/DTO nào được serialize ra JSON (Gửi cho khối Bếp) mà chứa `medicalHistory` hoặc thông tin sức khỏe nhạy cảm.
- [x] Bài test Reflection chặn phương thức `getMedicalHistory` Passed.
