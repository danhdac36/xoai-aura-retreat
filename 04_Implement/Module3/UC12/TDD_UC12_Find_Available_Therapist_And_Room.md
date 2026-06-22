# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển cho UC12

**Document ID:** AURA-SPA-TDD-UC12
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
| 2026-06-14 | DuongLD | Khởi tạo tài liệu - TDD spec cho UC12 Find Available Therapist and Room |

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | GAP-SPA-012 |
| Module | Spa Management |
| Spec gốc | AURA-SPA-IMP-UC12 |
| Priority | 🔴 P0 |
| Sprint | S1 |
| Data Classification | Internal |

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Quên không Lock DB khi kiểm tra trùng lịch. | `@Lock(PESSIMISTIC_WRITE)` được sử dụng | Thêm test case xác nhận gọi Repo đúng số lần. |
| L2 | SQL `NOT IN` dễ dính Null Trap | Thay bằng `NOT EXISTS` | Mocking Repository xử lý trả về danh sách rỗng khi bị kẹt. |
| L3 | Trả về 1 record bằng `Optional` dễ lỗi khi Package có nhiều Session. | Đổi hàm trả về `List<TreatmentBooking>` | Sử dụng `List.of()` và `Collections.emptyList()` thay vì `Optional`. |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

```
Spa Management bao gồm các layer:
├── Domain (Entity)
├── Services (SpaScheduleServiceImpl)
├── Repository (TreatmentRoomRepository, TherapistRepository, TreatmentBookingRepository)
└── Controller (SpaScheduleController)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC-12 | Hành vi đặt lịch tự động ghép phòng và chuyên viên |
| `ADR-001` | Ràng buộc tránh Double Booking bằng Lock DB |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
|---|---|---|
| Error Guessing | API Endpoint | Dự đoán lỗi người dùng chọn khung giờ đã kín hoặc Dịch vụ không nằm trong gói. |

## 4. Test Case Specification

### SPA-TC-001 — Book lịch Spa thành công khi có đủ phòng và nhân viên

**Severity:** CRITICAL
**Feature Under Test:** `SpaScheduleServiceImpl.scheduleSession()`
**Test File:** `SpaScheduleServiceImplTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Có 1 `TreatmentBooking` chưa được Schedule.
* Database có `TreatmentRoom` và `Therapist` trống trong khung giờ.

**Test Steps:**
1. Khởi tạo Mock Data cho Room và Therapist trả về List có phần tử.
2. Gọi hàm `spaScheduleService.scheduleSession(request)`.
3. Assert kết quả không null và ID phòng khớp.

**Expected Result (PASS):**
* Trả về `SpaScheduleResponse` mang thông tin Room ID và Therapist Code. Hệ thống lưu thành công `Schedule`.

### SPA-TC-002 — Báo lỗi khi hết Phòng rảnh (Conflict Trùng Lịch)

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleServiceImpl.scheduleSession()`
**Test File:** `SpaScheduleServiceImplTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khung giờ đã kín phòng. Repository trả về danh sách rỗng.

**Test Steps:**
1. Khởi tạo Mock `findAvailableRoomsWithLock()` trả về `Collections.emptyList()`.
2. Bọc hàm `scheduleSession` bằng `assertThrows(SpaBusinessException.class)`.

**Expected Result (PASS):**
* Exception ném ra mang mã lỗi `SPA-010` ("No available Therapist or Therapy Room could be found.").

### SPA-TC-003 — Báo lỗi khi hết Chuyên viên rảnh (Conflict Trùng Lịch)

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleServiceImpl.scheduleSession()`
**Test File:** `SpaScheduleServiceImplTest.java`
**TDD Phase:** 🟢 GREEN

**Expected Result (PASS):**
* Giống hệt SPA-TC-002. Exception ném ra mang mã lỗi `SPA-010` do hàm `findAvailableTherapistsWithLock` trả về danh sách rỗng.

### SPA-TC-004 — Báo lỗi khi Dịch vụ không nằm trong gói Retreat của khách

**Severity:** HIGH
**Feature Under Test:** `SpaScheduleServiceImpl.scheduleSession()`
**Test File:** `SpaScheduleServiceImplTest.java`
**TDD Phase:** 🟢 GREEN

**Expected Result (PASS):**
* Ném ra Exception `SPA-001` khi `treatmentBookingRepository.findByBookingIdAndTreatmentService_Id()` trả về `Collections.emptyList()`.

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `SPA-TC-001` | `SpaScheduleServiceImplTest.java` | [x] | [x] | Refactored from Optional to List |
| `SPA-TC-002` | `SpaScheduleServiceImplTest.java` | [x] | [x] | Refactored Null Trap logic |
| `SPA-TC-003` | `SpaScheduleServiceImplTest.java` | [x] | [x] | |
| `SPA-TC-004` | `SpaScheduleServiceImplTest.java` | [x] | [x] | Refactored Error Message |
