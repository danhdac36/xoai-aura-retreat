# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển: UC11 - Lên lịch phiên Spa/Trị liệu

**Document ID:** HOS-SPA-TDD-011  
**Version:** 1.0  
**Date:** 2024-06-14  
**Status:** Approved  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** Senior Java Developer  
**Reviewed by:** [ ] Tech Lead - Pending  
**Approved by:** [ ] Principal Architect - Pending  
**Classification:** Internal - Confidential

**References:**
* `01_Requirements/SRS_Document.md` — Functional requirements (UC11, BR-04, BR-05, BR-15)
* `04_Implement/Module3/UC11/EDS_UC11.md` (HOS-SPA-IMP-011) — Technical Specification

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test (`.spec.ts` hoặc `*Test.java`) -> chạy -> xác nhận FAIL 🔴 -> implement -> PASS 🟢 -> refactor 🔵.

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2024-06-14 | Senior Java Developer | Khởi tạo tài liệu - TDD spec cho UC11 Spa Scheduling |

## MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | UC11 - Spa Scheduling |
| Module | Spa & Treatment Management |
| Spec gốc | HOS-SPA-IMP-011 |
| Priority | 🔴 P0 |
| Sprint | S[N] |
| Data Classification | Internal |
| Upstream Dependencies | Booking Module, Retreat Package Module |
| Downstream Consumers | Notification Service, Audit Service |

## 2. Logic Issues Resolved
> Bắt buộc điền trước khi viết test.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Message "No suitable Villa available" (MSG-08) ghi nhầm ở UC11 trong SRS | Message lỗi chuẩn khi hết phòng/nhân viên Spa phải là MSG-10 | Sử dụng mã lỗi `SPA-010` kèm message "No available Therapist or Therapy Room could be found." |

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```
Spa Scheduling Module bao gồm các layer:
├── Domain (Entities: TreatmentBooking, Schedule, Therapist, TreatmentRoom)
├── Application / Services (SpaScheduleService - core logic)
├── Controller (SpaScheduleController)
└── Integration (DB SQL Server / JPA, Testcontainers cho integration testing)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC-11 | Flow book lịch Spa |
| `BR-04` | Cần cả Phòng và Chuyên viên trống cùng lúc. Không cho phép trùng lịch. |
| `BR-05` | Khách chỉ được book dịch vụ có trong gói. |
| `HOS-SPA-IMP-011` | Dùng Pessimistic Locking + Overlap Query để giải quyết tranh chấp (Concurrency). |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Happy path: Book thành công khi có đủ tài nguyên | `SpaScheduleService.scheduleSession` | `SPA-TC-001` |
| TC-COND-002 | Conflict: Không đủ phòng trống trong khoảng thời gian (Overlap) | `SpaScheduleService.scheduleSession` | `SPA-TC-002` |
| TC-COND-003 | Conflict: Không có chuyên viên trống trong khoảng thời gian | `SpaScheduleService.scheduleSession` | `SPA-TC-003` |
| TC-COND-004 | Rule BR-05: Dịch vụ không nằm trong gói | `SpaScheduleService.scheduleSession` | `SPA-TC-004` |
| TC-COND-INT-001 | Concurrency: Tránh Double-booking với nhiều request cùng lúc | Integration Test (Pessimistic Lock) | `SPA-TC-INT-001` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique | Applied To | Rationale |
|---|---|---|
| Boundary Value Analysis | Thời gian overlap của lịch | Kiểm tra biên của `start_time` và `end_time` (e.g. lịch A kết thúc đúng lúc lịch B bắt đầu thì có cho phép không -> Phải cho phép). |
| Error Guessing | Database Locking | Kiểm tra Deadlock và tính toàn vẹn dữ liệu khi có concurrent requests. |
| Equivalence Partitioning | Valid/Invalid Services | Phân vùng kiểm thử dịch vụ có/không có trong gói Retreat. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
|---|---|---|---|
| FX-001 | DB seed | 1 Guest, 1 Booking (có gói dịch vụ S1) | Base data |
| FX-002 | DB seed | 1 TreatmentRoom (Active), 1 Therapist (Active) | Tài nguyên rảnh rỗi |
| FX-003 | DB seed | 1 Schedule (10:00 - 11:00 cho Room1, Therapist1) | Dùng để test Overlap |

## 4. Test Case Specification

### SPA-TC-001 — Book lịch Spa thành công (Happy Path)
**Severity:** CRITICAL
**Feature Under Test:** `SpaScheduleService.scheduleSession()`
**TDD Phase:** 🔴 RED

**Preconditions:**
* Dữ liệu từ FX-001 và FX-002 (Có đủ 1 Phòng và 1 Chuyên viên trống).
* Chưa có lịch nào bị trùng.

**Test Steps:**
1. Request book dịch vụ S1 (thời lượng 60 phút) từ `10:00` đến `11:00`.
2. Gọi `spaScheduleService.scheduleSession(request)`.

**Expected Result (PASS):**
* Hàm trả về `SpaScheduleResponse` chứa `scheduleId`, `roomId`, `therapistCode`.
* Bảng `SCHEDULE` có 1 bản ghi mới (start_time: 10:00, end_time: 11:00).
* (Optional) Gọi `NotificationService` để gửi nhắc nhở.

**Current Status:** 🟢 PASS

---

### SPA-TC-002 — Báo lỗi khi hết Phòng (Room Overlap)
**Severity:** HIGH
**Feature Under Test:** `SpaScheduleService.scheduleSession()`
**TDD Phase:** 🔴 RED

**Preconditions:**
* Dữ liệu từ FX-001, FX-002, FX-003. Lịch đã có: `10:00 - 11:00` chiếm Room1 và Therapist1.
* DB có Therapist2 rảnh rỗi, nhưng chỉ có duy nhất Room1.

**Test Steps:**
1. Khách hàng B request book dịch vụ từ `10:30` đến `11:30`.
2. Gọi `spaScheduleService.scheduleSession(request)`.

**Expected Result (PASS):**
* Hệ thống throw `ResourceConflictException` với mã lỗi `SPA-010` (MSG-10: "No available Therapist or Therapy Room could be found.").
* Không có bản ghi mới nào trong `SCHEDULE`.

**Current Status:** 🟢 PASS

---

### SPA-TC-003 — Báo lỗi khi hết Chuyên viên (Therapist Overlap)
**Severity:** HIGH
**Feature Under Test:** `SpaScheduleService.scheduleSession()`
**TDD Phase:** 🔴 RED

**Preconditions:**
* Giống TC-002 nhưng đổi lại: Hệ thống có nhiều Phòng, nhưng chỉ có 1 Chuyên viên (đang bận từ 10:00 - 11:00).

**Test Steps:**
1. Khách hàng B request book dịch vụ từ `09:30` đến `10:30`.
2. Gọi `spaScheduleService.scheduleSession(request)`.

**Expected Result (PASS):**
* Hệ thống throw `ResourceConflictException` (`SPA-010`).

**Current Status:** 🟢 PASS

---

### SPA-TC-004 — Lỗi Dịch vụ không nằm trong gói (BR-05)
**Severity:** MEDIUM
**Feature Under Test:** `SpaScheduleService.scheduleSession()`
**TDD Phase:** 🔴 RED

**Preconditions:**
* Khách hàng thuộc FX-001 nhưng gói dịch vụ không bao gồm dịch vụ S2.

**Test Steps:**
1. Khách hàng request book dịch vụ S2.
2. Gọi `spaScheduleService.scheduleSession(request)`.

**Expected Result (PASS):**
* Hệ thống throw `ValidationException` với mã lỗi `SPA-001` (Service not found or not in package).

**Current Status:** 🟢 PASS

---

### INTEGRATION TEST CASES

#### SPA-TC-INT-001 — Kiểm thử Concurrent Bookings (Chống Double-booking bằng Pessimistic Lock)
**Severity:** CRITICAL  
**Feature Under Test:** `SpaScheduleService` với Transaction Management  
**TDD Phase:** 🔴 RED  

**Preconditions:**
* DB Test SQL Server.
* Hệ thống có đúng 1 Room rảnh và 1 Therapist rảnh.

**Test Steps (Simulate Race Condition):**
1. Khởi tạo 2 Threads đồng thời.
2. Thread 1: Khách A book dịch vụ S1 từ 10:00 - 11:00.
3. Thread 2: Khách B book dịch vụ S1 từ 10:00 - 11:00.
4. Chạy 2 Threads cùng lúc (sử dụng `CountDownLatch` hoặc `ExecutorService`).

**Expected Result (PASS):**
* 1 Thread thành công, tạo bản ghi `SCHEDULE`.
* 1 Thread thất bại với exception `ResourceConflictException` do không tìm thấy tài nguyên trống (do bị Lock và sau khi Unlock thì query Overlap bắt được).
* DB chỉ có đúng 1 bản ghi `SCHEDULE` trong khung giờ 10:00 - 11:00.

**Current Status:** 🟢 PASS

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `SPA-TC-001` | `SpaScheduleServiceImplTest.java` | [x] | 🟢 PASS | |
| `SPA-TC-002` | `SpaScheduleServiceImplTest.java` | [x] | 🟢 PASS | |
| `SPA-TC-003` | `SpaScheduleServiceImplTest.java` | [x] | 🟢 PASS | |
| `SPA-TC-004` | `SpaScheduleServiceImplTest.java` | [x] | 🟢 PASS | |
| `SPA-TC-INT-001`| `SpaScheduleIntegrationTest.java` | [x] | 🟢 PASS | Đã handle bằng Pessimistic Lock |

## 6. Entry / Exit Criteria

**Entry Criteria**
- [x] Spec kỹ thuật (EDS UC11) đã được duyệt.
- [x] DB Schema đầy đủ các bảng `SCHEDULE`, `TREATMENT_ROOM`, `THERAPIST`.

**Exit Criteria (DoD)**
- [x] Tất cả unit tests (SPA-TC-001 -> 004) vượt qua 🟢.
- [x] Integration test concurrency vượt qua mà không bị Deadlock.
- [x] Code logic đã handle đúng Pessimistic Write Lock.

## 7. Rollback Plan

```bash
# Revert file code
git checkout -- src/main/java/com/AuraMoon/auramoon/spa/service/SpaScheduleService.java
```
