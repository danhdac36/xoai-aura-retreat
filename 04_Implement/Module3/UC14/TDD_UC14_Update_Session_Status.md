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
| Upstream Dependencies | Auth (JWT `therapist_code`) |
| Downstream Consumers | Billing (`FOLIO_ITEM`) |

## 2. Logic Issues Resolved
| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | User yêu cầu test case của UC13 (Xem lịch) | UC14 là nghiệp vụ Cập nhật trạng thái session | Tự động điều chỉnh Test Case bám sát EDS của UC14 (Ownership, Update Status, Billing Integration) |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```text
Spa Module bao gồm các layer:
├── Domain: TreatmentBooking, FolioItem, Schedule Entities
├── Services: TherapistScheduleService
├── Controller: TherapistScheduleController
└── Repository: TreatmentBookingRepository, FolioItemRepository, ScheduleRepository
```

### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Quyền sở hữu ca làm việc (Ownership Check) | `TherapistScheduleService` | `UC14-TC-001` |
| TC-COND-002 | Trạng thái chuyển đổi hợp lệ | `TherapistScheduleService` | `UC14-TC-002` |
| TC-COND-003 | Tích hợp Billing (Folio_Item) | `TherapistScheduleService` | `UC14-TC-003` |

## 4. Test Case Specification

### UC14-TC-001 — Lỗi: Chuyên viên cập nhật ca trị liệu của người khác (Ownership Violation)
**Severity:** HIGH  
**Legal:** BR-05  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceTest.java`  
**TDD Phase:** 🔴 RED  

**Preconditions:**
* Có 1 session (treatment_id=10) được phân công cho `therapist_code="NV002"`.

**Test Steps:**
1. Chuyên viên đăng nhập với `therapist_code="NV001"`.
2. Gọi hàm update status của `treatment_id=10` thành `Completed`.

**Expected Result (PASS):**
* Bắn ra exception: `Bạn không có quyền cập nhật ca này` (Forbidden).
* Trạng thái trong DB không thay đổi.

**Current Status:** 🔴 Not written  

### UC14-TC-002 — Luồng lỗi (Error): Cập nhật trạng thái không hợp lệ
**Severity:** MEDIUM  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceTest.java`  
**TDD Phase:** 🔴 RED  

**Preconditions:**
* Session đã ở trạng thái `Completed` hoặc `No-Show`.

**Test Steps:**
1. Cố tình gọi API update lại thành `Scheduled`.

**Expected Result (PASS):**
* Bắn ra exception: `Trạng thái không hợp lệ`.

**Current Status:** 🔴 Not written  

### UC14-TC-003 — Luồng thành công (Happy Path): Cập nhật "Completed" và Ghi nợ vào Folio
**Severity:** CRITICAL  
**Legal:** BR-11  
**Feature Under Test:** `TherapistScheduleService.updateSessionStatus()`  
**Test File:** `TherapistScheduleServiceTest.java`  
**TDD Phase:** 🔴 RED  

**Preconditions:**
* Có session (treatment_id=20) của `NV001` đang ở trạng thái `Scheduled`.
* Session này CÓ `folio_id=5` (Dịch vụ Extra mua thêm).

**Test Steps:**
1. Gọi hàm update status thành `Completed` với `therapist_code="NV001"`.

**Expected Result (PASS):**
* Trạng thái update thành `Completed`.
* Một bản ghi mới được INSERT vào bảng `FOLIO_ITEM` với `folio_id=5`, `amount=giá_dịch_vụ`.

**Current Status:** 🔴 Not written  

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `UC14-TC-001` | `TherapistScheduleServiceTest.java` | [ ] | | |
| `UC14-TC-002` | `TherapistScheduleServiceTest.java` | [ ] | | |
| `UC14-TC-003` | `TherapistScheduleServiceTest.java` | [ ] | | |

## 6. Entry / Exit Criteria

**Entry Criteria**
- [x] Spec kỹ thuật EDS đã được duyệt
- [x] Nắm rõ schema `TREATMENT_BOOKING`, `SCHEDULE`, `FOLIO_ITEM`

**Exit Criteria**
- [ ] Code vượt qua toàn bộ Test Case
- [ ] Xác nhận giao dịch (Transaction) hoạt động đồng bộ
