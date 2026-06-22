# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển cho UC11

**Document ID:** AURA-SPA-TDD-UC11
**Version:** 1.0  
**Date:** 2026-06-14
**Status:** Approved  
**Author:** DuongLD
**Reviewed by:** [x] Tech Lead
**DPO Sign-off:** [x] Approved
**Approved by:** [x] Principal Architect
**Classification:** Internal - Confidential

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | DuongLD | Khởi tạo tài liệu - TDD spec cho UC11 Schedule Spa/Treatment Session |

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | GAP-SPA-011 |
| Module | Spa Management |
| Spec gốc | AURA-SPA-IMP-UC11 |
| Priority | 🔴 P0 |
| Sprint | S1 |
| Data Classification | Internal |

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Giao diện không hỗ trợ Ken Burns Effect / Blur | Đã cập nhật `schedule.html` với hiệu ứng CSS xịn xò. | Kiểm tra thủ công giao diện. |
| L2 | API Get Available Slots chưa bóc tách theo buổi | Giao diện đã chia list giờ thành "Sáng, Chiều, Tối". | Test case xác nhận render đúng cụm giờ tùy theo mảng giờ trả về. |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

```
Spa Management bao gồm các layer:
├── Controller (SpaScheduleController)
└── Presentation (schedule.html - Javascript Fetch API)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC-11 | Chức năng tải gói đang kích hoạt, chọn ngày, chọn giờ. |

## 4. Test Case Specification

### SPA-TC-11-001 — Lấy thành công Active Package của User

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleController.getActivePackage()`
**Test File:** `SpaScheduleControllerTest.java` (To be implemented)
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* User có 1 package trạng thái `Pending`.

**Test Steps:**
1. Gọi GET `/booking-spa/active-package`.
2. Assert Status = 200 OK.
3. Assert JSON Body chứa `bookingId`, `serviceName`, `durationMinutes`.

**Expected Result (PASS):**
* Trả về thông tin gói hợp lệ để hiển thị lên Giao diện.

### SPA-TC-11-002 — Báo lỗi khi User không có Active Package

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleController.getActivePackage()`
**Test File:** `SpaScheduleControllerTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* User không có package nào hoặc tất cả đều đã `Scheduled`.

**Test Steps:**
1. Gọi GET `/booking-spa/active-package`.
2. Assert Status = 404 Not Found.

**Expected Result (PASS):**
* API trả về 404, Giao diện hiển thị thông báo khách hàng chưa có gói Spa nào.

### SPA-TC-11-003 — Tính toán đúng Khung Giờ Trống (Available Slots)

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleController.getAvailableTimeSlots()`
**Test File:** `SpaScheduleControllerTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Truyền vào `date` và `duration`.
* Hệ thống có lịch đã bị đặt vào lúc 10:00.

**Test Steps:**
1. Gọi GET `/booking-spa/available-slots?date=2026-06-15&duration=60`.
2. Mock Service kiểm tra khung giờ 10:00 - 11:00 báo hết phòng.
3. Assert JSON response không chứa chuỗi `"10:00"`.

**Expected Result (PASS):**
* Danh sách khung giờ trả về tự động gạch bỏ những khung giờ đã kín lịch, bảo vệ người dùng khỏi việc chọn nhầm.

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `SPA-TC-11-001` | `SpaScheduleControllerTest.java` | [x] | [x] | Gắn vào /booking-spa |
| `SPA-TC-11-002` | `SpaScheduleControllerTest.java` | [x] | [x] | |
| `SPA-TC-11-003` | `SpaScheduleControllerTest.java` | [x] | [x] | |
