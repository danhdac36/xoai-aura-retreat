# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-TDD-023
**Version:** 1.0
**Date:** 2026-06-09
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Phùng Giang Hải
**Reviewed by:** [x] Phùng Giang Hải
**DPO Sign-off:** [ ] N/A
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**

* `01_Requirements/SRS.md` — UC23
* `02_SDD/SDS_Document_SWP391_G6.md` — Review Entity

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.

# CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                              |
| ---------- | ------------------- | ------------------------------------------------- |
| 2026-06-09 | AI Agent            | Khởi tạo tài liệu — TDD spec cho UC23 Review |

# MỤC LỤC

1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field                           | Value                          |
| ------------------------------- | ------------------------------ |
| **Feature / Gap ID**      | `UC23`                       |
| **Module**                | `Review & Rating`            |
| **Spec gốc**             | `UC23_EDS_Review.md`         |
| **Priority**              | 🟠 P1                          |
| **Sprint**                | `S4`                         |
| **Milestone**             | `Beta - 2026-07-15`          |
| **Data Classification**   | `Internal`                   |
| **Compliance Scope**      | N/A                            |
| **Upstream Dependencies** | `Booking Module`             |
| **Downstream Consumers**  | `Statistical Dashboard UC24` |

# 2. Logic Issues Resolved

| #  | Spec gốc (sai / thiếu)                                                | Thực tế (schema / policy)                   | Fix áp dụng trong test                                         |
| -- | ----------------------------------------------------------------------- | --------------------------------------------- | ---------------------------------------------------------------- |
| L1 | Khách hàng có thể quay lại URL submit để đánh giá nhiều lần | Bảng Review thiếu ràng buộc Unique        | Bổ sung hàm `existsByBookingId` chặn logic ở tầng Service |
| L2 | Booking PENDING cũng có thể submit                                   | Khách chưa dùng dịch vụ đã đánh giá | Bắt buộc `Booking.status == COMPLETED`                       |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
Review & Rating bao gồm các layer:
├── Services (Kiểm tra logic validate trạng thái và duplication)
├── Controller (Mock Service)
└── Repository (JPA Tests)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source           | Items Derived                              |
| ---------------- | ------------------------------------------ |
| `SRS.md` UC-23 | Đánh giá sao từ 1-5 và comment        |
| `ADR-002`      | Một Booking chỉ được Review một lần |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition           | Coverage Item                       | Test Cases     |
| ------------ | ------------------------ | ----------------------------------- | -------------- |
| TC-COND-001  | Đánh giá hợp lệ     | `ReviewService.submitReview()`    | `REV-TC-001` |
| TC-COND-002  | Booking chưa hoàn tất | `ReviewService.canSubmitReview()` | `REV-TC-002` |
| TC-COND-003  | Đánh giá trùng lặp  | `ReviewService.canSubmitReview()` | `REV-TC-003` |
| TC-COND-004  | XSS trong comment        | `ReviewService.submitReview()`    | `REV-TC-XSS` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4)  | Applied To     | Rationale                                     |
| ------------------------ | -------------- | --------------------------------------------- |
| Boundary Value Analysis  | Rating (1-5)   | Rating không được là 0 hoặc 6           |
| State Transition Testing | Booking Status | Chỉ trạng thái COMPLETED mới được pass |

## TDS-05 — Test Data Requirements

| Fixture ID | Type   | Value / Logic                       | Mục đích    |
| ---------- | ------ | ----------------------------------- | -------------- |
| `FX-001` | Entity | `Booking(id=1, status=COMPLETED)` | Happy path     |
| `FX-002` | Entity | `Booking(id=2, status=PENDING)`   | Invalid status |

# 4. Test Case Specification

## REV-TC-001 — Nộp đánh giá hợp lệ

**Severity:** `HIGH`
**Feature Under Test:** `ReviewService.submitReview()`
**Test File:** `ReviewServiceTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001`

**Preconditions:**

* Booking ID = 1 (COMPLETED). Chưa tồn tại review.

**Test Steps:**

1. Gọi `submitReview(1, 5, "Good!")`.

**Expected Result (PASS — hành vi đúng):**

* Lưu thành công xuống Mock Repository (verify `save` called 1 time).

**Expected Result (FAIL — dấu hiệu lỗi):**

* Băng Exception.

**Current Status:** 🟢 PASS

## REV-TC-002 — Đơn chưa hoàn tất

**Severity:** `HIGH`
**Feature Under Test:** `ReviewService.canSubmitReview()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-002`

**Preconditions:**

* Booking ID = 2 (PENDING).

**Test Steps:**

1. Gọi `canSubmitReview(2)`.

**Expected Result (PASS):**

* Throw `BookingNotCompletedException` (Đơn chưa hoàn tất).

**Expected Result (FAIL):**

* Hàm pass bình thường cho phép khách chưa ở đã đánh giá.

## REV-TC-003 — Chặn spam đánh giá 2 lần

**Severity:** `HIGH`
**Feature Under Test:** `ReviewService.canSubmitReview()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-003`

**Preconditions:**

* Booking ID = 1 (COMPLETED).
* Tồn tại 1 bản ghi Review cho Booking ID = 1.

**Test Steps:**

1. Gọi `canSubmitReview(1)`.

**Expected Result (PASS):**

* Throw `ReviewAlreadyExistsException`.

**Expected Result (FAIL):**

* Cho phép đánh giá đè lên hoặc tạo bản ghi mới (Race Condition / Spam).

## REV-TC-XSS — XSS trong comment (Security Test)

**Severity:** `CRITICAL`
**CWE:** `CWE-79`
**Feature Under Test:** `ReviewService.submitReview()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-004`

**Preconditions:**

* Booking ID = 3 (COMPLETED).

**Test Steps:**

1. Gọi `submitReview(3, 5, "<script>alert(1)</script>")`.
2. Kiểm tra chuỗi trả về / lưu ở DB.

**Expected Result (PASS):**

* Dữ liệu lưu xuống DB được escape HTML, hoặc UI render dùng `th:text` thay vì `th:utext`.

**Expected Result (FAIL):**

* Script tag giữ nguyên và được render trực tiếp ra UI.

# 5. Red-Green-Refactor Tracker

| TC ID          | Test File                  | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| -------------- | -------------------------- | ---------------- | ----------------- | ---------------- |
| `REV-TC-001` | `ReviewServiceTest.java` | `[X]`          | `[X]`           | Passed           |
| `REV-TC-002` | `ReviewServiceTest.java` | `[X]`          | `[X]`           | Passed           |
| `REV-TC-003` | `ReviewServiceTest.java` | `[X]`          | `[X]`           | Passed           |
| `REV-TC-XSS` | `ReviewServiceTest.java` | `[X]`          | `[X]`           | Passed           |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)

- [X] Spec kỹ thuật đã duyệt.
- [X] Template HTML Stitch đã có sẵn.
- [X] Entity `Review` và `Booking` có mapping đúng với CSDL.

## Exit Criteria (Điều kiện kết thúc — DoD)

- [X] Tất cả unit tests xanh.
- [X] Logic chặn đánh giá 2 lần hoạt động 100%.
- [X] Tích hợp giao diện TailwindCSS mượt mà.
- [X] Bấm nút đánh giá trên trang Checkout Success truyền đúng `bookingId`.

## Suspension Criteria (Điều kiện tạm dừng)

* Thymeleaf escape rules không tương thích gây lỗi hiển thị
* Thiếu template UI từ team Design

# 7. Rollback Plan

- **Rủi ro:** Mã lỗi khi redirect trên `ReviewController` có thể gây loop redirect.
- **Rollback:**

```bash
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/ReviewController.java
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ReviewServiceImpl.java
```
