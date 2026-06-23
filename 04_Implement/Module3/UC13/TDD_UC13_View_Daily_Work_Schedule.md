# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển cho UC13

**Document ID:** AURA-SPA-TDD-UC13
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
| 2026-06-14 | DuongLD | Khởi tạo tài liệu - TDD spec cho UC13 View Daily Work Schedule (Thymeleaf) |

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | GAP-SPA-013 |
| Module | Therapist Portal Module |
| Spec gốc | AURA-SPA-IMP-UC13 |
| Priority | 🔴 P0 |
| Sprint | S2 |
| Data Classification | Internal |

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Lỗi N+1 Query khi gọi vòng lặp lấy tên Phòng/Dịch vụ | JPA sinh ra N câu SQL rác | Sẽ viết test case đảm bảo chỉ 1 câu JPQL `JOIN FETCH` được gọi. |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

```
Therapist Portal bao gồm các layer:
├── Domain (Entity Schedule, Therapist, TreatmentRoom, TreatmentService)
├── Services (TherapistScheduleService)
├── Repository (ScheduleRepository)
└── Controller (TherapistScheduleController - Spring MVC)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC-13 | Chức năng xem lịch làm việc hàng ngày của Chuyên viên. |
| `ADR-004` | Yêu cầu hiệu năng: Bắt buộc dùng `JOIN FETCH` để tránh N+1. |

## 4. Test Case Specification

### TH-TC-13-001 — Luồng thành công (Happy Path): Chuyên viên có lịch làm việc

**Severity:** HIGH
**Feature Under Test:** `TherapistScheduleController.getDailySchedule()`
**Test File:** `TherapistScheduleControllerTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Có 1 `Therapist` với mã `TH01` tồn tại trong Database.
* Database có 2 bản ghi `Schedule` khớp với `TH01` trong ngày `2026-06-15`.

**Test Steps:**
1. Giả lập đăng nhập dưới quyền `TH01`.
2. Dùng MockMvc gửi request GET `/therapist/schedules/daily?date=2026-06-15`.
3. Assert HTTP Status là 200 OK.
4. Assert View Name trả về là `spa/therapist-schedule`.
5. Assert Model chứa attribute `schedules` và có độ dài là 2.

**Expected Result (PASS):**
* Controller trả về đúng giao diện kèm theo danh sách 2 lịch làm việc của chuyên viên trong Model.

### TH-TC-13-002 — Luồng trống (Empty State): Chuyên viên không có lịch nào

**Severity:** MEDIUM
**Feature Under Test:** `TherapistScheduleController.getDailySchedule()`
**Test File:** `TherapistScheduleControllerTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Có 1 `Therapist` với mã `TH02` tồn tại.
* Database KHÔNG CÓ bản ghi `Schedule` nào khớp với `TH02` trong ngày được truy vấn.

**Test Steps:**
1. Giả lập đăng nhập dưới quyền `TH02`.
2. Dùng MockMvc gửi request GET `/therapist/schedules/daily?date=2026-06-15`.
3. Assert HTTP Status là 200 OK.
4. Assert View Name trả về là `spa/therapist-schedule`.
5. Assert Model chứa attribute `schedules` là một danh sách rỗng.

**Expected Result (PASS):**
* Trả về giao diện bình thường, Thymeleaf tự động render thông báo "Hôm nay bạn không có ca làm việc nào" dựa vào danh sách rỗng.

### TH-TC-13-003 — Luồng lỗi (Error): Không tìm thấy mã therapist_code

**Severity:** HIGH
**Feature Under Test:** `TherapistScheduleController.getDailySchedule()`
**Test File:** `TherapistScheduleControllerTest.java`
**TDD Phase:** 🟢 GREEN

**Preconditions:**
* Một User bất kỳ hoặc một Session bị lỗi mất thông tin gửi request.
* `therapist_code` truyền vào (hoặc móc từ Session) không hợp lệ.

**Test Steps:**
1. Gửi request GET với Session chứa mã chuyên viên không hợp lệ.
2. Assert HTTP Status là 302 REDIRECT (đẩy về trang login hoặc trang báo lỗi).
3. (Hoặc) Assert Model chứa lỗi và render lại trang hiện tại.

**Expected Result (PASS):**
* Ngăn chặn việc truy cập lịch, đẩy người dùng ra khỏi trang Therapist.

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `TH-TC-13-001` | `TherapistScheduleControllerTest.java` | [x] | [x] | Happy Path |
| `TH-TC-13-002` | `TherapistScheduleControllerTest.java` | [x] | [x] | Empty State |
| `TH-TC-13-003` | `TherapistScheduleControllerTest.java` | [x] | [x] | Error Handling |
