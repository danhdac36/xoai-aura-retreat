# TEST-DRIVEN DEVELOPMENT SPECIFICATION - UC16 & UC19
## Đặc tả Kiểm thử Hướng Phát triển - Thực đơn Dinh dưỡng & Gọi món ngoài F&B

- **Document ID**: SWP391-FNB-TDD-UC16
- **Version**: 1.0
- **Date**: 2026-06-12
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Student 4 — F&B Module Owner
- **Reviewed by**: Team Lead
- **DPO Sign-off**: [x] Approved — 2026-06-10 — [DPO Officer]
- **Approved by**: Principal Architect
- **Classification**: Internal — Confidential

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-12 | Student 4 | Khởi tạo tài liệu TDD spec cho UC16 & UC19 và cập nhật kết quả kiểm thử tự động đạt trạng thái GREEN |

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
| **Feature / Gap ID** | GAP-FNB-SELECT |
| **Module** | Dietary F&B Management - Personalized Menu Selection |
| **Spec gốc** | `SWP391-FNB-IMP-UC16` |
| **Priority** | 🔴 P0 |
| **Sprint** | S2 (2026-06-05 -> 2026-06-19) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Sensitive-PII |
| **Compliance Scope** | Nghị định 356/2025/NĐ-CP (Bảo vệ dữ liệu cá nhân y tế nhạy cảm) |
| **Upstream Dependencies** | `auth` (User), `booking` (Booking), `billing` (GuestFolio) |
| **Downstream Consumers** | `billing` (Consolidated Invoice) |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Ban đầu ẩn hoàn toàn các món ăn chứa thành phần dị ứng khỏi danh sách thực đơn. | Khách hàng muốn xem đầy đủ nhưng được cảnh báo rõ ràng (ADR-003). | API trả về toàn bộ thực đơn kèm cờ khả dụng `isAvailableForGuest = false` và chuỗi cảnh báo `warningMessage`. |
| L2 | Tính toán tiền gọi món ngoài (A-la-carte) chưa cộng phí phục vụ. | Yêu cầu nghiệp vụ cộng thêm 5% phí phục vụ và ghi nợ tự động vào GuestFolio. | Tự động tính 5% phí phục vụ và cập nhật vào Folio nợ của phòng. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
`MealSelectionService` bao gồm các layer được kiểm thử:
- Application / Services Layer: Kiểm thử thông qua Mockito giả lập cơ sở dữ liệu (`DietaryProfileRepository`, `MenuItemRepository`, `MealOrderRepository`, `MealOrderItemRepository`, `BookingRepository`, `GuestFolioRepository`).

### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC16, UC19 | Khách hàng pre-select món và gọi món ngoài a-la-carte. |
| BR-06 | Bộ lọc tự động đối chiếu dị ứng. Chặn đặt món dị ứng ở Backend. |
| BR-11 | Ghi nợ tự động vào GuestFolio đối với món gọi thêm. |

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Đối chiếu và cảnh báo món vi phạm dị ứng thực phẩm của khách. | `MealSelectionService.getFilteredMenuForGuest()` | `FNB-TC-006`, `FNB-TC-007` |
| TC-COND-002 | Khuyên dùng món chay (Vegan) thích hợp cho khách ăn chay. | `MealSelectionService.getFilteredMenuForGuest()` | `FNB-TC-008` |
| TC-COND-003 | Đặt món tiêu chuẩn thành công (giá trong gói = $0). | `MealSelectionService.selectDailyMeals()` | `FNB-TC-009` |
| TC-COND-004 | Gọi món ngoài tính thêm 5% phí và ghi nợ GuestFolio thành công. | `MealSelectionService.selectDailyMeals()` | `FNB-TC-010` |
| TC-COND-005 | Chặn đứng yêu cầu chọn món chứa nguyên liệu dị ứng từ Backend. | `MealSelectionService.selectDailyMeals()` | `FNB-TC-011` |

---

## 4. Test Case Specification

### `FNB-TC-006` — Tải thực đơn cho khách hàng không có hồ sơ dị ứng
- **Severity**: MEDIUM
- **Feature Under Test**: `MealSelectionService.getFilteredMenuForGuest()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Mock `MenuItemRepository` trả về 3 món ăn mẫu.
- Mock `DietaryProfileRepository` trả về rỗng (Guest không khai báo dị ứng).

#### Test Steps:
1. Gọi hàm `getFilteredMenuForGuest(1)`.
2. Kiểm tra xem cờ khả dụng `isAvailableForGuest` của cả 3 món đều là `true`.
3. Kiểm tra xem không có cảnh báo dị ứng nào được trả về.

#### Expected Result (PASS):
- Toàn bộ món ăn hiển thị khả dụng và không có thông tin warning.

---

### `FNB-TC-007` — Cảnh báo dị ứng đậu phộng khi lấy thực đơn
- **Severity**: HIGH
- **Feature Under Test**: `MealSelectionService.getFilteredMenuForGuest()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Mock `DietaryProfileRepository` trả về hồ sơ dị ứng `"peanut"`.

#### Test Steps:
1. Gọi hàm `getFilteredMenuForGuest(1)`.
2. Tìm món có đậu phộng (Peanut Butter Toast).
3. Assert xem cờ `isAvailableForGuest` là `false` và `warningMessage` chứa chuỗi cảnh báo tiếng Việt `"dị ứng"`.

#### Expected Result (PASS):
- Món ăn chứa đậu phộng bị vô hiệu hóa và có thông điệp cảnh báo rõ ràng.

---

### `FNB-TC-008` — Khuyên dùng món chay (Vegan)
- **Severity**: LOW
- **Feature Under Test**: `MealSelectionService.getFilteredMenuForGuest()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Mock `DietaryProfileRepository` trả về sở thích ăn uống `"vegan"`.

#### Test Steps:
1. Gọi hàm `getFilteredMenuForGuest(1)`.
2. Kiểm tra cờ `isRecommended` của món cơm hấp (Steamed Rice - thuần chay).
3. Đảm bảo món thịt bò (Beef Noodles) có `isRecommended` là `false`.

#### Expected Result (PASS):
- Món chay được tự động khuyên dùng, món chứa thịt không được khuyên dùng.

---

### `FNB-TC-009` — Khách đặt món tiêu chuẩn thành công
- **Severity**: HIGH
- **Feature Under Test**: `MealSelectionService.selectDailyMeals()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Giả lập đơn đặt mealType = `"Lunch"`.

#### Test Steps:
1. Gọi hàm `selectDailyMeals(request)`.
2. Kiểm tra xem đơn hàng lưu thành công vào DB và Folio không đổi (vì trong gói).

#### Expected Result (PASS):
- Đơn hàng lưu thành công và không tính thêm phí extra.

---

### `FNB-TC-010` — Khách đặt món ngoài A-La-Carte thành công (UC19)
- **Severity**: HIGH
- **Feature Under Test**: `MealSelectionService.selectDailyMeals()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Giả lập đơn đặt mealType = `"A-La-Carte"` với món trị giá $10.00.

#### Test Steps:
1. Gọi hàm `selectDailyMeals(request)`.
2. Xác minh xem GuestFolio được lưu lại với số tiền extra là $10.50 (gồm 5% phí phục vụ).

#### Expected Result (PASS):
- Lưu hóa đơn thành công và Folio tăng chính xác $10.50.

---

### `FNB-TC-011` — Chặn đơn hàng vi phạm dị ứng thực phẩm từ Backend
- **Severity**: CRITICAL
- **Feature Under Test**: `MealSelectionService.selectDailyMeals()`
- **Test File**: `com.AuraMoon.auramoon.fnb.service.MealSelectionServiceTest`
- **TDD Phase**: 🟢 GREEN

#### Preconditions:
- Khách hàng bị dị ứng đậu phộng cố tình submit đơn hàng chứa món Peanut Butter Toast.

#### Test Steps:
1. Gọi hàm `selectDailyMeals(request)`.
2. Assert trạng thái trả về là `"ALLERGY_VIOLATION"`.
3. Đảm bảo không lưu MealOrder vào cơ sở dữ liệu (`verify(mealOrderRepository, never()).save(...)`).

#### Expected Result (PASS):
- Backend từ chối giao dịch và hủy lưu đơn hàng.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `FNB-TC-006` | `MealSelectionServiceTest.java` | [x] | `9e3b4a2` | Tách logic gán hình ảnh và dinh dưỡng |
| `FNB-TC-007` | `MealSelectionServiceTest.java` | [x] | `9e3b4a2` | Hỗ trợ đối chiếu đa ngôn ngữ Anh-Việt |
| `FNB-TC-008` | `MealSelectionServiceTest.java` | [x] | `9e3b4a2` | Tự động hóa bộ lọc món chay động |
| `FNB-TC-009` | `MealSelectionServiceTest.java` | [x] | `a27f91c` | Đồng bộ khóa ngoại Integer với SQL Server |
| `FNB-TC-010` | `MealSelectionServiceTest.java` | [x] | `a27f91c` | Làm tròn phí dịch vụ `RoundingMode.HALF_UP` |
| `FNB-TC-011` | `MealSelectionServiceTest.java` | [x] | `a27f91c` | Hiện thực cơ chế Double Validation |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- Đặc tả thiết kế `SWP391-FNB-IMP-UC16.md` được duyệt.
- Entity JPA mapping của `MenuItem` và `User` đồng bộ thành công.

### Exit Criteria (Điều kiện kết thúc)
- Bộ unit test chạy qua 100% (6/6 test cases của MealSelectionServiceTest).
- Trình biên dịch không có lỗi và không có kiểu `any` lọt vào logic.

---

## 7. Rollback Plan

### Revert implementation files
```bash
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealSelectionService.java
```
