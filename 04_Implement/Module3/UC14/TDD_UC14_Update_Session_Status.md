# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** HOS-M3-TDD-014  
**Version:** 1.0  
**Date:** 2026-06-14  
**Status:** Draft  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** Agent (Senior Java Developer)  
**Reviewed by:** [ ] DuongLD - Pending  
**DPO Sign-off:** [ ] N/A  
**Approved by:** [ ] Pending  
**Classification:** Internal

**References:**
* `FRS_Module3_SpaScheduling.md` — Functional requirements (UC14)
* `EDS_UC14_Update_Session_Status.md` — Technical Specification

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | Agent | Khởi tạo tài liệu - TDD spec cho UC14 Update Session Status |

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | UC14 |
| Module | Spa & Therapy Scheduling Engine |
| Spec gốc | HOS-M3-IMP-014 |
| Priority | 🔴 P0 |
| Sprint | S1 |
| Milestone | M3 |
| Data Classification | Internal |
| Compliance Scope | N/A |
| Upstream Dependencies | Auth (Session `therapistCode`) |
| Downstream Consumers | N/A (Không tích hợp Billing) |

## 2. Logic Issues Resolved
| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Yêu cầu tích hợp Billing và tạo Folio Item | Theo yêu cầu mới, hoàn toàn không tích hợp Billing | Chỉ test cập nhật trạng thái `TREATMENT_BOOKING.status` mà không kiểm tra hay tương tác với Folio |
| L2 | Tìm kiếm theo `treatmentId` | Trùng lặp lượt `SCHEDULE` khi cùng một `treatment_id` được xếp lịch nhiều lần gây lỗi `IncorrectResultSizeDataAccessException` | Chuyển sang tìm kiếm theo khóa chính `scheduleId` là duy nhất |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```text
Spa Module bao gồm các thành phần kiểm thử:
├── Domain: TreatmentBooking, Schedule Entities
├── Services: TherapistScheduleService
├── Controller: TherapistScheduleController
└── Repository: TreatmentBookingRepository, ScheduleRepository
```

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Quyền sở hữu ca làm việc (Ownership Check) | `TherapistScheduleService` | `UC14-TC-001` |
| TC-COND-002 | Trạng thái chuyển đổi hợp lệ | `TherapistScheduleService` | `UC14-TC-002` |
| TC-COND-003 | Cập nhật trạng thái thành công | `TherapistScheduleService` | `UC14-TC-003` |

## 4. Test Case Specification

### UC14-TC-001 — Lỗi: Chuyên viên cập nhật ca trị liệu của người khác (Ownership Violation)
**Severity:** HIGH  
**Legal:** BR-05  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceImplTest.java`  
**TDD Phase:** 🟢 GREEN  

**Preconditions:**
* Có 1 ca làm việc (schedule_id=4) được phân công cho `therapist_code="T003"`.

**Test Steps:**
1. Chuyên viên đăng nhập với `therapist_code="T002"`.
2. Gọi hàm update status của `schedule_id=4` thành `Completed`.

**Expected Result (PASS):**
* Bắn ra exception: `Bạn không có quyền cập nhật ca này` (SPA-011).
* Trạng thái trong DB không thay đổi.

**Current Status:** 🟢 PASS  

### UC14-TC-002 — Luồng lỗi (Error): Cập nhật trạng thái không hợp lệ
**Severity:** MEDIUM  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceImplTest.java`  
**TDD Phase:** 🟢 GREEN  

**Preconditions:**
* Ca trị liệu đã ở trạng thái `Completed` hoặc `No-Show`.

**Test Steps:**
1. Cố tình gọi API/Service update lại thành `Completed`.

**Expected Result (PASS):**
* Bắn ra exception: `Trạng thái không hợp lệ` (SPA-012).

**Current Status:** 🟢 PASS  

### UC14-TC-003 — Luồng thành công (Happy Path): Cập nhật "Completed" và "No-Show"
**Severity:** CRITICAL  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceImplTest.java`  
**TDD Phase:** 🟢 GREEN  

**Preconditions:**
* Có ca làm việc (schedule_id=1) của `T002` đang ở trạng thái `Scheduled`.

**Test Steps:**
1. Gọi hàm update status thành `Completed` với `therapist_code="T002"`.

**Expected Result (PASS):**
* Trạng thái update thành `Completed`.
* Gọi save trên Repository để ghi nhận thay đổi vào Database.

**Current Status:** 🟢 PASS  

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `UC14-TC-001` | `TherapistScheduleServiceImplTest.java` | [x] | 🟢 PASS | Đã chuyển đổi sang kiểm tra quyền sở hữu bằng `scheduleId` |
| `UC14-TC-002` | `TherapistScheduleServiceImplTest.java` | [x] | 🟢 PASS | Kiểm tra chặn chuyển đổi ngược hoặc chuyển đổi từ Completed/No-Show |
| `UC14-TC-003` | `TherapistScheduleServiceImplTest.java` | [x] | 🟢 PASS | Cập nhật thành công trạng thái vào DB và không ghi nhận billing |

## 6. Entry / Exit Criteria

**Entry Criteria**
- [x] Spec kỹ thuật EDS đã được duyệt
- [x] Nắm rõ schema `TREATMENT_BOOKING`, `SCHEDULE`

**Exit Criteria**
- [x] Code vượt qua toàn bộ Test Case
- [x] Xác nhận giao dịch (Transaction) hoạt động đồng bộ
