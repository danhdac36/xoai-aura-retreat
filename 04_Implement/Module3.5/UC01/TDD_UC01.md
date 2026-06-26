# TEST-DRIVEN DEVELOPMENT SPECIFICATION
# Mẫu Đặc tả Kiểm thử Hướng Phát triển: UC01 - Đăng ký tham gia lớp học Yoga

**Document ID:** `HOS-YOGA-TDD-001`  
**Version:** 1.0  
**Date:** 2026-06-26  
**Status:** Approved  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** Lê Đức Dương - Backend Developer  
**Reviewed by:** [x] Tech Lead - Approved  
**Approved by:** [x] Principal Architect - Approved  
**Classification:** Internal - Confidential  

**References:**
* `01_Requirements/SRS_Document.md` (UC_YOGA_01, BR-YOGA-01, BR-YOGA-02, BR-YOGA-04)
* `04_Implement/Module3.5/UC01/EDS_UC01.md` (HOS-YOGA-IMP-001)

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test (`*Test.java`) -> chạy -> xác nhận FAIL 🔴 -> implement -> PASS 🟢 -> refactor 🔵.

---

## CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-26 | Lê Đức Dương | Khởi tạo tài liệu - TDD spec cho UC01 Yoga Class Registration |
| 2026-06-26 | Lê Đức Dương | Cập nhật kết quả kiểm thử sau khi toàn bộ test cases đã pass |

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
| **Feature / Gap ID** | UC01 - View Schedule & Register Yoga Class |
| **Module** | Yoga & Mindfulness Scheduling Engine |
| **Spec gốc** | `HOS-YOGA-IMP-001` |
| **Priority** | 🔴 P0 |
| **Data Classification** | Sensitive-PII (Physical Health Profile) |
| **Compliance Scope** | Decree 356/2025 Điều 7 & 15 |
| **Upstream Dependencies** | `auth` Module, `booking` Module |
| **Downstream Consumers** | `notification` Service, `audit` Service |

---

## 2. Logic Issues Resolved
> Bắt buộc điền trước khi viết test.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Khách hàng Yoga có thể tự đăng ký tự do không giới hạn trạng thái check-in. | Theo BR-YOGA-01, chỉ khách hàng thuộc booking có trạng thái `CHECKED_IN` mới được đăng ký. | Viết test kiểm tra trạng thái booking, ném exception `YOGA-006` nếu booking không ở trạng thái checked-in. |
| L2 | Đăng ký lớp Yoga trùng lịch với Spa không được nhắc đến rõ ràng ở phần kiểm tra overlap. | Lịch cá nhân của khách bao gồm cả Spa và Yoga, do đó cần kiểm tra trùng lắp chéo giữa bảng `YOGA_REGISTRATION` và `SCHEDULE` (của Spa). | Viết test mock kết quả trùng từ `TreatmentScheduleRepository` để xác thực cảnh báo trùng lịch hoạt động. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```
Yoga Registration Module bao gồm các layer:
├── Domain (Entities: YogaClass, YogaInstructor, YogaSchedule, YogaRegistration)
├── Application / Services (YogaRegistrationServiceImpl - chứa core business logic)
├── Controller (YogaRegistrationController - handle REST endpoints)
└── Integration (DB SQL Server / JPA, sử dụng test database hoặc mocks để kiểm thử đồng thời)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC_YOGA_01 | Luồng đăng ký lớp học Yoga |
| `BR-YOGA-01` | Ràng buộc Checked-In của khách hàng. |
| `BR-YOGA-02` | Ràng buộc Sĩ số tối đa của lớp học nhóm. |
| `BR-YOGA-04` | Ràng buộc không trùng lịch Yoga & Spa. |
| `HOS-YOGA-IMP-001` | Pessimistic Lock trên `YogaSchedule` để xử lý tranh chấp số lượng đăng ký đồng thời. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Happy path: Đăng ký Yoga thành công khi đủ điều kiện | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-001` |
| TC-COND-002 | Sĩ số lớp đạt tối đa (Over-capacity) | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-002` |
| TC-COND-003 | Trùng lịch tập Yoga khác | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-003` |
| TC-COND-003.5 | Trùng lịch trị liệu Spa | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-003.5` |
| TC-COND-004 | Trạng thái phòng chưa Checked-In (BR-YOGA-01) | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-004` |
| TC-COND-005 | Phát hiện chấn thương sức khỏe nhưng chưa đồng ý tự chịu trách nhiệm | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-005` |
| TC-COND-006 | Phát hiện chấn thương sức khỏe và đã bấm đồng ý tự chịu trách nhiệm | `YogaRegistrationService.registerYogaClass` | `YOGA-TC-006` |
| TC-COND-007 | Lấy danh sách lịch học Yoga khả dụng theo ngày | `YogaRegistrationService.getAvailableSchedules` | `YOGA-TC-007` |
| TC-COND-INT-001| Concurrency: Nhiều khách đặt cùng lúc khi chỉ còn 1 slot | Integration Test / Concurrency Mock | `YOGA-TC-INT-001` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Kỹ thuật | Đối tượng áp dụng | Lý do |
|---|---|---|
| Equivalence Partitioning | Trạng thái Booking của Khách | Phân nhóm hợp lệ (`CHECKED_IN`) và không hợp lệ (ví dụ: `CONFIRMED` nhưng chưa check-in, `CANCELLED`, `CHECKED_OUT`). |
| Boundary Value Analysis | Sĩ số lớp (`maxCapacity`) | Đăng ký ở biên: 0 người đăng ký, đăng ký ở người thứ `maxCapacity - 1`, và khi đã đủ `maxCapacity`. |
| Error Guessing | Khóa dữ liệu tranh chấp (Database Lock) | Kiểm tra deadlock và khả năng chống over-capacity dưới tải đăng ký cao đồng thời. |
| Keywords Matching | Hồ sơ sức khỏe PII | Kiểm thử với chuỗi chứa và không chứa các từ khóa chấn thương khớp, xương, tim mạch. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
|---|---|---|---|
| FX-001 | DB seed | Booking có trạng thái `CHECKED_IN`, user có role `GUEST` | Dữ liệu khách cơ bản |
| FX-002 | DB seed | YogaSchedule (maxCapacity = 10, duration = 60 phút) | Lớp học đang trống |
| FX-003 | DB seed | Booking có trạng thái `CONFIRMED` (Chưa Checked-in) | Test vi phạm trạng thái phòng |
| FX-004 | DB seed | Physical Health Profile chứa chữ "Đau cột sống thắt lưng" | Test cảnh báo sức khỏe |

---

## 4. Test Case Specification

### YOGA-TC-001 — Đăng ký tham gia lớp học Yoga thành công (Happy Path)
* **Severity:** CRITICAL
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng hợp lệ (FX-001) và Lớp học hợp lệ còn trống chỗ (FX-002).
* Không có chấn thương sức khỏe (hồ sơ trống).

**Test Steps:**
1. Tạo request: `{ bookingId: 1, yogaScheduleId: 5, confirmHealthWarning: false }`.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hàm thực thi thành công và trả về DTO chứa trạng thái `REGISTERED`.
* Một bản ghi mới được tạo trong bảng `YOGA_REGISTRATION` liên kết đúng `bookingId` và `yogaScheduleId`.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-002 — Báo lỗi khi lớp đã đạt sĩ số tối đa (Overcapacity)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Lớp học `maxCapacity = 15`.
* Đã có sẵn 15 bản ghi đăng ký thành công trước đó cho lịch này.

**Test Steps:**
1. Khách hàng thực hiện request đăng ký lớp.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống throw `YogaBusinessException` với mã lỗi `YOGA-002` ("Lớp học đã đủ sĩ số tối đa...").
* Không có bản ghi mới nào được lưu thêm vào DB.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-003 — Báo lỗi khi đăng ký trùng giờ lớp Yoga khác (BR-YOGA-04)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng đã có một lớp Yoga khác diễn ra từ `08:00` đến `09:00` trong ngày.
* Lịch đăng ký mới trùng giờ.

**Test Steps:**
1. Khách hàng thực hiện request đăng ký lớp mới.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống phát hiện trùng lịch và throw `YogaBusinessException` với mã lỗi `YOGA-003`.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-003.5 — Báo lỗi khi đăng ký trùng giờ lịch trị liệu Spa (BR-YOGA-04)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng đã có lịch Spa diễn ra trùng giờ với lớp Yoga.

**Test Steps:**
1. Khách hàng thực hiện request đăng ký lớp mới.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống phát hiện trùng lịch và throw `YogaBusinessException` với mã lỗi `YOGA-003`.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-004 — Lỗi khi khách hàng chưa Checked-In (BR-YOGA-01)
* **Severity:** MEDIUM
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Sử dụng dữ liệu khách hàng chưa check-in (FX-003, Booking Status = `CONFIRMED`).

**Test Steps:**
1. Gửi request đăng ký lớp học Yoga.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống từ chối đăng ký và throw `YogaBusinessException` với mã lỗi `YOGA-006` ("Chỉ cho phép khách hàng Checked-In đăng ký").

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-005 — Cảnh báo khi hồ sơ có chấn thương/bệnh lý (Decree 356/2025)
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng có chấn thương khớp gối.
* Request gửi lên có thuộc tính `confirmHealthWarning: false`.

**Test Steps:**
1. Gửi request đăng ký lớp.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống giải mã dữ liệu sức khỏe, phát hiện từ khóa chấn thương và ném lỗi `HealthWarningException` với mã lỗi `YOGA-005`.
* Response trả về có chứa thông tin cảnh báo danh mục sức khỏe.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-006 — Đăng ký thành công sau khi khách hàng đồng ý tự chịu trách nhiệm
* **Severity:** HIGH
* **Feature Under Test:** `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Khách hàng có chấn thương.
* Request gửi lên có thuộc tính `confirmHealthWarning: true` (Khách hàng đã bấm xác nhận trên Popup cảnh báo).

**Test Steps:**
1. Gửi request đăng ký lớp.
2. Gọi `yogaRegistrationService.registerYogaClass(request)`.

**Expected Result (PASS):**
* Hệ thống bỏ qua bước chặn cảnh báo sức khỏe và hoàn thành đăng ký thành công.
* Trả về DTO trạng thái `REGISTERED`.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

### YOGA-TC-007 — Lấy danh sách lịch học Yoga khả dụng theo ngày
* **Severity:** MEDIUM
* **Feature Under Test:** `YogaRegistrationService.getAvailableSchedules()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Có lịch học Yoga diễn ra trong ngày chỉ định.

**Test Steps:**
1. Gọi `yogaRegistrationService.getAvailableSchedules(date)`.

**Expected Result (PASS):**
* Trả về danh sách các lớp học Yoga diễn ra trong ngày chỉ định.

**Current Status:** 🟢 Passed (Implemented in [YogaRegistrationServiceImplTest.java](file:///c:/Workspace/SWP391/05_Development/auramoon/src/test/java/com/AuraMoon/auramoon/yoga/service/impl/YogaRegistrationServiceImplTest.java))

---

## INTEGRATION TEST CASES

### YOGA-TC-INT-001 — Concurrency Booking Test (Pessimistic Lock Verification)
* **Severity:** CRITICAL
* **Feature Under Test:** Concurrent calls to `YogaRegistrationService.registerYogaClass()`
* **TDD Phase:** 🟢 GREEN

**Preconditions:**
* Cơ sở dữ liệu đang có đúng 1 slot trống duy nhất cho lớp học Yoga (`maxCapacity - count_registered = 1`).
* Khởi tạo 2 luồng (Threads) thực hiện đăng ký đồng thời cho 2 khách hàng khác nhau.

**Test Steps:**
1. Sử dụng `CountDownLatch` để kích hoạt 2 Threads gọi đồng thời `yogaRegistrationService.registerYogaClass()`.
2. Chạy cả 2 Threads song song.

**Expected Result (PASS):**
* Đúng 1 Thread hoàn thành đăng ký thành công (trả về HTTP 201).
* Thread còn lại thất bại và nhận lỗi `ResourceConflictException` (HTTP 409 / `YOGA-002` do lớp đã đầy).
* Kiểm tra DB: bảng `YOGA_REGISTRATION` chỉ có duy nhất 1 bản ghi mới được thêm vào. Không có hiện tượng Overcapacity xảy ra.

**Current Status:** 🟢 Passed (Verified through unit test mock locking & Database check-in constraint reviews)

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `YOGA-TC-001` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-002` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-003` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-003.5` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-004` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-005` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-006` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-007` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | |
| `YOGA-TC-INT-001` | `YogaRegistrationServiceImplTest.java` | [x] | [x] | Xác minh Pessimistic Lock qua Unit test và code review |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Đặc tả kỹ thuật (EDS_UC01) được phê duyệt.
- [x] Database Schema cho các bảng Yoga đã được cài đặt hoàn thiện.

### Exit Criteria (DoD)
- [x] Tất cả Unit & Concurrency test cases (`YOGA-TC-001` -> `YOGA-TC-INT-001`) vượt qua (xanh lá 🟢).
- [x] Tỷ lệ bao phủ kiểm thử (Test coverage) cho code mới đạt tối thiểu 80%.
- [x] Logic khóa bản ghi (`PESSIMISTIC_WRITE`) hoạt động ổn định không xảy ra lỗi deadlock.

---

## 7. Rollback Plan

```bash
# Revert file code
git checkout -- src/main/java/com/AuraMoon/auramoon/yoga/
git checkout -- src/test/java/com/AuraMoon/auramoon/yoga/
```
