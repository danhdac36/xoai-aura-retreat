# TEST-DRIVEN DEVELOPMENT SPECIFICATION
# Mẫu Đặc tả Kiểm thử Hướng Phát triển: UC03 - Xem danh sách học viên & Ghi chú sức khỏe (Huấn luyện viên)

**Document ID:** `HOS-YOGA-TDD-003`  
**Version:** 1.0  
**Date:** 2026-06-26  
**Status:** Approved  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** Lê Đức Dương - Backend Developer  
**Reviewed by:** [x] Tech Lead - Approved  
**Approved by:** [x] Principal Architect - Approved  
**Classification:** Internal - Confidential  

**References:**
* `01_Requirements/SRS_Document.md` (UC_YOGA_03, SEC-PII-02)
* `04_Implement/Module3.5/UC03/EDS_UC03.md` (HOS-YOGA-IMP-003)
* `Decree 356/2025` Điều 7 & 15 (Bảo vệ dữ liệu nhạy cảm)

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test (`*Test.java`) -> chạy -> xác nhận FAIL 🔴 -> implement -> PASS 🟢 -> refactor 🔵.

---

## CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-26 | Lê Đức Dương | Khởi tạo tài liệu - TDD spec cho UC03 |
| 2026-06-26 | Lê Đức Dương | Hoàn thành viết unit tests (TDD) và triển khai thành công UC03 |

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
|---|---|
| **Feature / Gap ID** | UC03 - View Class Participant List & Health Notes (Instructor) |
| **Module** | Yoga & Mindfulness Scheduling Engine |
| **Spec gốc** | `HOS-YOGA-IMP-003` |
| **Priority** | 🟠 P1 |
| **Data Classification** | Sensitive-PII (Physical Health Profile) |
| **Compliance Scope** | Decree 356/2025 (VN GDPR) |
| **Upstream Dependencies** | `auth` Module, `spa` Module (Physical Health Profile) |
| **Downstream Consumers** | Giao diện Huấn luyện viên Yoga |

---

## 2. Logic Issues Resolved
> Bắt buộc điền trước khi viết test.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Huấn luyện viên có thể xem toàn bộ bệnh lý của khách hàng. | Nguyên tắc Data Minimization (NĐ 356/2025): Chỉ được xem thông tin sức khỏe liên quan vận động. | Viết test kiểm tra xem các ghi chú về dị ứng thức ăn hay thông tin thanh toán có bị loại bỏ/không hiển thị hay không. |
| L2 | Giáo viên có quyền xem học viên của tất cả các lớp Yoga trong resort. | Theo thiết kế bảo mật để tránh IDOR, chỉ giáo viên được phân công đứng lớp mới có quyền xem thông tin học viên của lớp đó. | Viết test giả lập một giáo viên khác cố gắng truy cập danh sách lớp và mong đợi ném exception `YOGA-008`. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```
Yoga Registration Module bao gồm các layer:
├── Domain (Entities: YogaClass, YogaInstructor, YogaSchedule, YogaRegistration)
├── Application / Services (YogaRegistrationServiceImpl - chứa logic lấy danh sách & giải mã)
└── Controller (YogaRegistrationController - handle endpoints cho Huấn luyện viên)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC_YOGA_03 | Giáo viên xem danh sách học viên |
| `SEC-PII-02` | Tối thiểu hóa thông tin nhạy cảm (Data Minimization) |
| `HOS-YOGA-IMP-003` | Thiết kế phân quyền giáo viên theo `instructor_id` |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-008 | Happy path: Giáo viên lấy danh sách học viên lớp mình dạy thành công | `YogaRegistrationService.getParticipantsForSchedule` | `YOGA-TC-013` |
| TC-COND-009 | Chặn giáo viên truy cập lớp học của giáo viên khác (IDOR check) | `YogaRegistrationService.getParticipantsForSchedule` | `YOGA-TC-014` |
| TC-COND-010 | Data Minimization: Lọc đúng chấn thương/bệnh lý vận động và ẩn dị ứng | `YogaRegistrationService.getParticipantsForSchedule` | `YOGA-TC-015` |
| TC-COND-011 | Lớp học chưa có học viên đăng ký | `YogaRegistrationService.getParticipantsForSchedule` | `YOGA-TC-016` |

---

## 4. Test Case Specification

### YOGA-TC-013 — Lấy danh sách học viên lớp mình phụ trách thành công (Happy Path)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.getParticipantsForSchedule()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Giáo viên hợp lệ đang phụ trách lớp học.
* Có học viên đã đăng ký lớp học này thành công.

**Test Steps:**
1. Gọi `yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId)`.

**Expected Result (PASS):**
* Hàm thực thi thành công và trả về danh sách `YogaParticipantResponse` chứa đầy đủ họ tên học viên, số phòng và ghi chú chấn thương (đã giải mã).

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-014 — Chặn giáo viên khác truy cập xem danh sách học viên (IDOR Check)
* **Severity:** CRITICAL
* **Feature Under Test:** `YogaRegistrationService.getParticipantsForSchedule()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Lớp học `scheduleId` được gán cho Giáo viên A (`instructorUserId = 200`).
* Giáo viên B (`instructorUserId = 201`) cố gắng gọi xem danh sách học viên lớp này.

**Test Steps:**
1. Giáo viên B gọi `yogaRegistrationService.getParticipantsForSchedule(scheduleId, 201)`.

**Expected Result (PASS):**
* Hệ thống từ chối truy cập và throw `YogaBusinessException` với mã lỗi `YOGA-008` ("Bạn không có quyền truy cập...").

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-015 — Thực hiện nguyên tắc Tối thiểu hóa dữ liệu (Data Minimization Verification)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.getParticipantsForSchedule()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng đăng ký có hồ sơ sức khỏe chứa:
  - `injuries`: "Đau khớp gối trái" (liên quan vận động)
  - `medicalConditions`: "Huyết áp thấp" (liên quan vận động)
  - Ngoài ra trong Dietary Profile hoặc trường dị ứng có thông tin khác không liên quan vận động.
* Giáo viên của lớp gọi lấy danh sách học viên.

**Test Steps:**
1. Gọi `yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId)`.

**Expected Result (PASS):**
* Ghi chú chấn thương và bệnh lý liên quan vận động được giải mã thành công trong response.
* Không có bất kỳ rò rỉ nào về thông tin dị ứng ăn uống hay dữ liệu thanh toán nhạy cảm khác trong danh sách trả về.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-016 — Lớp học không có học viên đăng ký
* **Severity:** MEDIUM
* **Feature Under Test:** `YogaRegistrationService.getParticipantsForSchedule()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Lớp học chưa có học viên nào đăng ký (`REGISTERED`).

**Test Steps:**
1. Giáo viên của lớp gọi `yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId)`.

**Expected Result (PASS):**
* Trả về danh sách rỗng (`empty list`) mà không ném lỗi.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `YOGA-TC-013` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-014` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-015` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-016` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Đặc tả kỹ thuật (EDS_UC03) được tạo và kiểm duyệt.

### Exit Criteria (DoD)
- [x] Tất cả các test cases (`YOGA-TC-013` -> `YOGA-TC-016`) vượt qua thành công (xanh lá 🟢).
- [x] Test coverage cho logic mới đạt tối thiểu 80%.

---

## 7. Rollback Plan

```bash
# Revert file code
git checkout -- src/main/java/com/AuraMoon/auramoon/yoga/
git checkout -- src/test/java/com/AuraMoon/auramoon/yoga/
```
