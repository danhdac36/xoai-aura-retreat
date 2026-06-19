# TEST-DRIVEN DEVELOPMENT SPECIFICATION - MODULE 2

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** FPT-EDU-TDD-BOOKING-002
**Version:** 2.0
**Date:** 2026-06-15
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Lê Trà My — Module 2 Lead
**Reviewed by:** [x] Phùng Giang Hải — Tech Lead
**DPO Sign-off:** [x] Approved - 2026-06-15 - Phùng Giang Hải
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**
* `07_Reports/EDS_Module_2.md` (AURAMOON-BOOKING-IMP-002 v2.0) — Technical Specification
* `02_Requirement/SRS_Document.md` — Functional requirements (UC-06 → UC-10, UC-23)
* `04_Implement/EDS_TEMPLATE_V2.0.md` — EDS v2.0 Template
* `04_Implement/TDD_TEMPLATE_V1.md` — TDD Template
* Nghị định 356/2025/NĐ-CP — Bảo vệ dữ liệu cá nhân (Sensitive-PII)
* Luật Cư trú 2020 — Khai báo tạm trú

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test (.java) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Không mark test là ✅ nếu `mvn test` chưa xanh.
> Test data dùng dữ liệu SYNTHETIC. **Không dùng CCCD thật, số thẻ ngân hàng thật.**

---

# CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-15 | Lê Trà My | Khởi tạo tài liệu TDD v1.0 — spec cho Module 2 (Booking) |
| 2026-06-15 | Lê Trà My | Refactor lên v2.0: Bổ sung TDS đầy đủ, test conditions, security tests, integration tests, Red-Green-Refactor tracker, Entry/Exit criteria theo TDD_TEMPLATE_V1 |

---

# MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

---

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `GAP-BOOKING-002` |
| **Module** | `booking` — Retreat Package & Accommodation Booking |
| **Spec gốc** | `AURAMOON-BOOKING-IMP-002` (EDS_Module_2.md v2.0) |
| **Priority** | 🔴 P0 |
| **Sprint** | `S2 (2026-06-10 → 2026-06-18)` |
| **Milestone** | `M3 Alpha - 2026-07-11` |
| **Data Classification** | `Sensitive-PII` (CCCD/Passport) / `PII` (thông tin booking) |
| **Compliance Scope** | Nghị định 356/2025/NĐ-CP / Luật Cư trú 2020 |
| **Upstream Dependencies** | `auth` (Module 1 — JWT, Role verification) |
| **Downstream Consumers** | `spa` (Module 3), `fnb` (Module 4), `billing` (GuestFolio) |

---

# 2. Logic Issues Resolved

> **Bắt buộc điền trước khi viết test.**
> Liệt kê mọi sai lệch giữa spec thiết kế và schema/policy/codebase thực tế.
> Test cases sẽ encode hành vi **đã sửa**, không phải hành vi trong spec gốc.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | `GuestFolio` được tạo ngay trong `createBooking()` | Tạo Folio khi booking chưa thanh toán → sinh dữ liệu rác, sai lệch báo cáo tài chính (ADR-002) | Test `UC07-TC-003` xác nhận: `createBooking()` KHÔNG gọi `GuestFolioRepository.save()`; chỉ `confirmPayment()` mới tạo Folio |
| **L2** | `identifyCode` (CCCD/Passport) được lưu plaintext | Nghị định 356/2025 yêu cầu mã hóa Sensitive-PII (ADR-001, BR-09) | Test `UC08-TC-001` xác nhận: `performCheckIn()` phải gọi `EncryptionService.encrypt()` và giá trị trong DB là ciphertext, KHÔNG phải plaintext |
| **L3** | Review có thể gửi bất kỳ lúc nào | BR-13: Chỉ booking `CHECKED_OUT` mới được gửi review; mỗi booking chỉ 1 review | Test `REV-TC-002` và `REV-TC-003` xác nhận cả 2 ràng buộc được enforce |
| **L4** | VNPay callback không được xử lý idempotent | Callback có thể được gửi nhiều lần; xử lý 2 lần sẽ tạo 2 GuestFolio | Test `UC07-TC-004` xác nhận: lần gọi thứ 2 với cùng `bookingId` đã `CONFIRMED` không tạo thêm Folio |

---

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

> Kiểm thử bao phủ toàn bộ **Service Layer** của Module 2 thông qua **JUnit 5 + Mockito** (không load Spring Boot Context) để tối ưu thời gian thực thi.

```text
Module 2 — Booking bao gồm các layer được kiểm thử:
├── Service Layer (Unit Tests — Mockito)
│   ├── BookingServiceImpl      (UC-06, UC-07: browse, book, confirmPayment)
│   ├── CheckInServiceImpl      (UC-08: check-in, villa assignment, CCCD encryption)
│   ├── VillaServiceImpl        (UC-09: villa status management)
│   └── ReviewServiceImpl       (UC-23: submit review, XSS sanitization)
│
├── Security Tests (RBAC & Attack Vectors)
│   ├── RBAC-TC-001: Receptionist không được xem health records
│   ├── RBAC-TC-002: Guest không được thực hiện check-in
│   └── XSS-TC-001: HTML injection trong review comment
│
└── Integration Tests (Testcontainers PostgreSQL)
    ├── IT-TC-001: Booking → Payment → GuestFolio flow
    └── IT-TC-002: CheckIn → Villa status → Audit log flow
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

> Điều kiện kiểm thử được derive từ các nguồn sau:

| Source | Items Derived |
| --- | --- |
| `SRS.md` UC-06 | Guest browse & filter retreat packages by goal |
| `SRS.md` UC-07 | Book package, deposit payment via VNPay, BR-01, BR-02 |
| `SRS.md` UC-08 | Receptionist check-in, villa assignment, CCCD collection (BR-14) |
| `SRS.md` UC-09 | Villa status lifecycle: AVAILABLE → OCCUPIED → NEEDS_CLEANING → AVAILABLE |
| `SRS.md` UC-10 | Guest view itinerary (cross-module aggregation) |
| `SRS.md` UC-23 | Guest submit post-stay review — BR-13 constraints |
| `ADR-001` | Mã hóa AES-256 thông tin CCCD/Passport — security constraint |
| `ADR-002` | GuestFolio chỉ được tạo tại `confirmPayment()` — data integrity |
| `BR-01` | Booking confirmed only after successful deposit payment |
| `BR-02` | Guest selects VillaType only; Receptionist assigns specific Villa at check-in |
| `BR-03` | Villa double-booking prevention; MAINTENANCE villa cannot be assigned |
| `BR-07` | RBAC & Data Minimization — Receptionist không xem health records |
| `BR-09` | Sensitive data encryption (CCCD/Passport) |
| `BR-12` | Check-out blocked if pending Spa/F&B charges exist |
| `BR-13` | Review only for CHECKED_OUT bookings; 1 review per booking |
| Nghị định 356/2025 | Bảo vệ dữ liệu cá nhân nhạy cảm (CCCD/Passport) |
| Luật Cư trú 2020 | Khai báo tạm trú — lưu trữ thông tin định danh |

## TDS-03 — Test Conditions and Coverage Items

> Mỗi condition map sang ≥ 1 test case cụ thể.

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **TC-COND-001** | Tạo Booking thành công khi VillaType có phòng trống | `BookingService.createBooking()` | `UC07-TC-001` |
| **TC-COND-002** | Ném lỗi BOOK-002 khi VillaType hết phòng trống | `BookingService.createBooking()` | `UC07-TC-002` |
| **TC-COND-003** | Xác nhận thanh toán thành công → tạo GuestFolio | `BookingService.confirmPayment()` | `UC07-TC-003` |
| **TC-COND-004** | confirmPayment idempotent — gọi 2 lần không tạo 2 Folio | `BookingService.confirmPayment()` | `UC07-TC-004` |
| **TC-COND-005** | Check-in thành công: gán villa, mã hóa CCCD, cập nhật trạng thái | `CheckInService.performCheckIn()` | `UC08-TC-001` |
| **TC-COND-006** | Check-in thất bại do bookingId không tồn tại | `CheckInService.performCheckIn()` | `UC08-TC-002` |
| **TC-COND-007** | Check-in thất bại do villa đang OCCUPIED hoặc MAINTENANCE | `CheckInService.performCheckIn()` | `UC08-TC-003` |
| **TC-COND-008** | CCCD được lưu dưới dạng ciphertext, KHÔNG phải plaintext | `EncryptionService.encrypt()` + `performCheckIn()` | `UC08-TC-001` (assert DB) |
| **TC-COND-009** | Cập nhật villa status thành công (BR-03 lifecycle) | `VillaService.updateVillaStatus()` | `UC09-TC-001` |
| **TC-COND-010** | Ngăn gán villa MAINTENANCE cho booking | `VillaService.checkAvailability()` | `UC09-TC-002` |
| **TC-COND-011** | Gửi review hợp lệ sau khi booking CHECKED_OUT | `ReviewService.submitReview()` | `REV-TC-001` |
| **TC-COND-012** | Ngăn review khi booking chưa CHECKED_OUT | `ReviewService.canSubmitReview()` | `REV-TC-002` |
| **TC-COND-013** | Ngăn gửi review lần 2 cho cùng bookingId | `ReviewService.canSubmitReview()` | `REV-TC-003` |
| **TC-COND-014** | HTML injection trong comment tự động được sanitize | `ReviewService.submitReview()` | `REV-TC-XSS` |
| **TC-COND-015** | Receptionist bị từ chối khi truy cập health records Guest | RBAC Guard | `RBAC-TC-001` |
| **TC-COND-016** | Guest bị từ chối khi cố thực hiện check-in | RBAC Guard | `RBAC-TC-002` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| --- | --- | --- |
| Equivalence Partitioning | `createBooking()` input: available vs. unavailable VillaType | Giảm số test case trong khi đảm bảo coverage |
| Boundary Value Analysis | `ratingScore` (valid: 1–5, invalid: 0, 6) | Kiểm tra edge case của review rating |
| State Transition Testing | Booking status FSM: PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT | Đảm bảo mọi transition hợp lệ được test |
| State Transition Testing | Villa status FSM: AVAILABLE → OCCUPIED → NEEDS_CLEANING → AVAILABLE | Enforce BR-03 lifecycle |
| Error Guessing | CCCD plaintext storage, SQL Injection, XSS trong review comment | Phát hiện security vulnerabilities (CWE-312, CWE-79, CWE-89) |
| Decision Table | `canSubmitReview()`: booking status × review exists | Kiểm tra tổ hợp điều kiện BR-13 |

## TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| --- | --- | --- | --- |
| `FX-001` | DB seed | `Booking{id=1001, status=PENDING, paymentStatus=UNPAID}` | Happy path createBooking |
| `FX-002` | DB seed | `Booking{id=1002, status=CONFIRMED, paymentStatus=DEPOSITED}` | Idempotency test (UC07-TC-004) |
| `FX-003` | DB seed | `Booking{id=1003, status=CHECKED_OUT}` với Review đã tồn tại | Duplicate review test (REV-TC-003) |
| `FX-004` | DB seed | `VillaType{id=2, name="Deluxe"}` với 3 phòng available trong [2026-07-01, 2026-07-08] | Available room happy path |
| `FX-005` | DB seed | `VillaType{id=3, name="Suite"}` với 0 phòng available (fully booked) | No availability error path |
| `FX-006` | DB seed | `Villa{id=5, villaCode="LOTUS-05", status=AVAILABLE, typeId=2}` | Check-in villa assignment |
| `FX-007` | DB seed | `Villa{id=6, villaCode="LOTUS-06", status=MAINTENANCE}` | Maintenance villa rejection |
| `FX-008` | env | `ENCRYPTION_SECRET=test-secret-32charlong-key-here` | AES-256 encryption trong test |
| `FX-009` | JWT | `{ sub: "guest-001", role: "GUEST", bookingId: 1001 }` | Guest auth context |
| `FX-010` | JWT | `{ sub: "recep-001", role: "RECEPTIONIST" }` | Receptionist auth context |
| `FX-011` | Payload | `identifyCode = "079-SYNTHETIC-TEST"` | CCCD test data (KHÔNG dùng số thật) |
| `FX-012` | Payload | `comment = "<script>alert('XSS')</script>Tuyệt vời"` | XSS injection test |

---

# 4. Test Case Specification

> **TC ID format:** `[FEATURE]-TC-[NNN]`
> **Severity:** CRITICAL / HIGH / MEDIUM / LOW (theo CVSS)
> **Status:** 🔴 Not written / 🟠 Written-failing / 🟢 Passing

---

## 4.1. Unit Tests — BookingService

### UC07-TC-001 — createBooking thành công khi còn phòng trống

**Severity:** `HIGH`
**Feature Under Test:** `BookingServiceImpl.createBooking()`
**Test File:** `src/test/java/com/auramoon/booking/BookingServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-001`

**Preconditions:**
* `RetreatPackage(id=1, isActive=true)` tồn tại trong mock
* `VillaType(id=2)` có phòng trống — `VillaService.checkAvailability()` mock trả về `availableCount = 3`
* Guest đã xác thực (FX-009)

**Test Steps:**
1. **Arrange:** Mock `VillaService.checkAvailability(2, "2026-07-01", "2026-07-08")` → trả về `3`; Mock `BookingRepository.save()` → trả về `Booking{bookingId=1001}`
2. **Act:** Gọi `bookingService.createBooking({packageId=1, villaTypeId=2, checkinDate="2026-07-01", checkoutDate="2026-07-08", totalGuests=2})`
3. **Assert:** Kiểm tra response, trạng thái booking, audit log, và KHÔNG tạo GuestFolio

**Expected Result (PASS — hành vi đúng):**
* `BookingRepository.save()` được gọi đúng 1 lần với `booking.status = PENDING` và `booking.paymentStatus = UNPAID`
* Response chứa `bookingId = 1001` và `vnpayRedirectUrl` hợp lệ (không null)
* `GuestFolioRepository.save()` **KHÔNG** được gọi (ADR-002)
* `AuditService.log(BOOKING_CREATED, 1001)` được gọi đúng 1 lần

**Expected Result (FAIL — dấu hiệu lỗi):**
* `GuestFolioRepository.save()` bị gọi → vi phạm ADR-002
* Response thiếu `vnpayRedirectUrl` → integration với VNPay bị broken
* `booking.status != PENDING` → sai trạng thái khởi tạo

**Current Status:** 🟢 Passing
**Implementation Note:** Đảm bảo inject `GuestFolioRepository` vào `BookingServiceImpl` nhưng KHÔNG gọi `save()` tại `createBooking()` — chỉ gọi tại `confirmPayment()`.

---

### UC07-TC-002 — createBooking thất bại khi VillaType hết phòng trống

**Severity:** `HIGH`
**Feature Under Test:** `BookingServiceImpl.createBooking()`
**Test File:** `src/test/java/com/auramoon/booking/BookingServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-002`

**Preconditions:**
* `VillaType(id=3)` — `VillaService.checkAvailability()` mock trả về `availableCount = 0` (FX-005)

**Test Steps:**
1. **Arrange:** Mock `VillaService.checkAvailability(3, ...)` → trả về `0`
2. **Act:** Gọi `bookingService.createBooking({packageId=1, villaTypeId=3, ...})`
3. **Assert:** Kiểm tra exception và side effects

**Expected Result (PASS — hành vi đúng):**
* Ném ra `VillaNotAvailableException` với error code `BOOK-002`
* `BookingRepository.save()` **KHÔNG** được gọi — không tạo booking rác
* `GuestFolioRepository.save()` **KHÔNG** được gọi

**Expected Result (FAIL — dấu hiệu lỗi):**
* Exception không được ném ra → booking được tạo dù không còn phòng
* Exception được ném nhưng sai error code → frontend hiển thị sai message

**Current Status:** 🟢 Passing

---

### UC07-TC-003 — confirmPayment thành công, tạo GuestFolio (ADR-002)

**Severity:** `HIGH`
**Feature Under Test:** `BookingServiceImpl.confirmPayment()`
**Test File:** `src/test/java/com/auramoon/booking/BookingServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-003`

**Preconditions:**
* `Booking(id=1001, status=PENDING, paymentStatus=UNPAID)` tồn tại (FX-001)
* VNPay callback payload: `{bookingId=1001, transactionCode="TXN-SYNTHETIC-001", vnpayStatus=SUCCESS}`

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(1001)` → trả về `Booking{status=PENDING}`; Mock `GuestFolioRepository.save()` → trả về `GuestFolio{folioId=501, status=OPEN}`
2. **Act:** Gọi `bookingService.confirmPayment({bookingId=1001, transactionCode="TXN-SYNTHETIC-001"})`
3. **Assert:** Kiểm tra trạng thái booking, GuestFolio, và audit log

**Expected Result (PASS — hành vi đúng):**
* `booking.status = CONFIRMED` và `booking.paymentStatus = DEPOSITED`
* `GuestFolioRepository.save()` được gọi đúng **1 lần** với `folio.status = OPEN` và `folio.bookingId = 1001`
* `AuditService.log(PAYMENT_CONFIRMED, 1001, "TXN-SYNTHETIC-001")` được gọi

**Expected Result (FAIL — dấu hiệu lỗi):**
* `GuestFolioRepository.save()` không được gọi → Folio không được tạo → downstream modules (Spa, F&B billing) bị broken
* `booking.status` không đổi → booking vẫn PENDING dù đã thanh toán

**Current Status:** 🟢 Passing
**Implementation Note:** Đây là fix cho Logic Issue L1. `GuestFolio` PHẢI được tạo tại đây, không phải tại `createBooking()`.

---

### UC07-TC-004 — confirmPayment idempotent (VNPay gửi callback 2 lần)

**Severity:** `HIGH`
**Feature Under Test:** `BookingServiceImpl.confirmPayment()`
**Test File:** `src/test/java/com/auramoon/booking/BookingServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-004`

**Preconditions:**
* `Booking(id=1002, status=CONFIRMED, paymentStatus=DEPOSITED)` đã tồn tại (FX-002)
* GuestFolio với `bookingId=1002` đã tồn tại

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(1002)` → trả về `Booking{status=CONFIRMED, paymentStatus=DEPOSITED}`
2. **Act:** Gọi lại `bookingService.confirmPayment({bookingId=1002, transactionCode="TXN-SYNTHETIC-001"})` (lần thứ 2)
3. **Assert:** Kiểm tra không có side effects thêm

**Expected Result (PASS — hành vi đúng):**
* `GuestFolioRepository.save()` **KHÔNG** được gọi lần thứ 2 → không tạo Folio trùng
* Method trả về bình thường (không throw exception)
* `booking.status` không thay đổi (vẫn CONFIRMED)

**Expected Result (FAIL — dấu hiệu lỗi):**
* `GuestFolioRepository.save()` được gọi lần 2 → tạo Folio duplicate → sai lệch tài chính nghiêm trọng

**Current Status:** 🟢 Passing

---

## 4.2. Unit Tests — CheckInService

### UC08-TC-001 — Check-in thành công: gán villa, mã hóa CCCD

**Severity:** `CRITICAL`
**CWE:** `CWE-312` — Cleartext Storage of Sensitive Information
**Legal:** Nghị định 356/2025/NĐ-CP — ADR-001 — BR-09 — BR-14
**Feature Under Test:** `CheckInServiceImpl.performCheckIn()`
**Test File:** `src/test/java/com/auramoon/booking/CheckInServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-005`, `TC-COND-008`

**Preconditions:**
* `Booking(id=1001, status=CONFIRMED, paymentStatus=DEPOSITED)` tồn tại
* `Villa(id=5, villaCode="LOTUS-05", status=AVAILABLE, typeId=2)` tồn tại (FX-006)
* CCCD test data: `"079-SYNTHETIC-TEST"` (FX-011)
* `ENCRYPTION_SECRET` được cấu hình (FX-008)

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(1001)` → Booking CONFIRMED; Mock `VillaRepository.findById(5)` → Villa AVAILABLE; Mock `EncryptionService.encrypt("079-SYNTHETIC-TEST")` → `"ENCRYPTED_ABC123"`; Spy `BookingRepository.save()`
2. **Act:** Gọi `checkInService.performCheckIn({bookingId=1001, villaId=5, identifyCode="079-SYNTHETIC-TEST", identifyType=CCCD})`
3. **Assert:** Kiểm tra toàn bộ side effects

**Expected Result (PASS — hành vi đúng):**
* `EncryptionService.encrypt("079-SYNTHETIC-TEST")` được gọi đúng 1 lần
* Booking được save với:
  * `booking.status = CHECKED_IN`
  * `booking.assignedVillaId = 5`
  * `booking.identifyCode = "ENCRYPTED_ABC123"` (ciphertext, KHÔNG phải `"079-SYNTHETIC-TEST"`)
* `VillaRepository.save()` được gọi với `villa.status = OCCUPIED`
* `AuditService.log(GUEST_CHECKED_IN, 1001, 5)` được gọi

**Expected Result (FAIL — dấu hiệu lỗi):**
* `booking.identifyCode = "079-SYNTHETIC-TEST"` (plaintext) → **Vi phạm nghiêm trọng** Nghị định 356/2025 và CWE-312
* `EncryptionService.encrypt()` không được gọi → plaintext storage

**Current Status:** 🟢 Passing
**Implementation Note:** Inject `EncryptionService` vào `CheckInServiceImpl`. PHẢI gọi `encrypt()` TRƯỚC khi set `identifyCode`. Key được inject qua `@Value("${encryption.secret}")`.

---

### UC08-TC-002 — Check-in thất bại do bookingId không tồn tại

**Severity:** `HIGH`
**Feature Under Test:** `CheckInServiceImpl.performCheckIn()`
**Test File:** `src/test/java/com/auramoon/booking/CheckInServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-006`

**Preconditions:**
* `BookingRepository.findById(9999)` trả về `Optional.empty()`

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(9999)` → `Optional.empty()`
2. **Act:** Gọi `checkInService.performCheckIn({bookingId=9999, villaId=5, ...})`
3. **Assert:** Exception và không có side effects

**Expected Result (PASS — hành vi đúng):**
* Ném `BookingNotFoundException` với HTTP 404 và code `BOOK-404`
* `VillaRepository.save()` **KHÔNG** được gọi
* `EncryptionService.encrypt()` **KHÔNG** được gọi

**Current Status:** 🟢 Passing

---

### UC08-TC-003 — Check-in thất bại do villa đang MAINTENANCE

**Severity:** `HIGH`
**Legal:** BR-03 — Villa MAINTENANCE không được phân bổ cho khách
**Feature Under Test:** `CheckInServiceImpl.performCheckIn()`
**Test File:** `src/test/java/com/auramoon/booking/CheckInServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-007`

**Preconditions:**
* `Villa(id=6, status=MAINTENANCE)` tồn tại (FX-007)
* `Booking(id=1001, status=CONFIRMED)` tồn tại

**Test Steps:**
1. **Arrange:** Mock `VillaRepository.findById(6)` → `Villa{status=MAINTENANCE}`
2. **Act:** Gọi `checkInService.performCheckIn({bookingId=1001, villaId=6, ...})`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* Ném `VillaNotAvailableException` với code `BOOK-002` và message mô tả villa không khả dụng
* `booking.status` KHÔNG thay đổi — vẫn `CONFIRMED`

**Expected Result (FAIL — dấu hiệu lỗi):**
* Check-in thành công với villa MAINTENANCE → vi phạm BR-03, khách có thể bị nhận phòng hỏng

**Current Status:** 🟢 Passing

---

## 4.3. Unit Tests — VillaService

### UC09-TC-001 — Cập nhật villa status thành công

**Severity:** `MEDIUM`
**Feature Under Test:** `VillaServiceImpl.updateVillaStatus()`
**Test File:** `src/test/java/com/auramoon/booking/VillaServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-009`

**Preconditions:**
* `Villa(id=5, status=OCCUPIED)` tồn tại

**Test Steps:**
1. **Arrange:** Mock `VillaRepository.findById(5)` → `Villa{status=OCCUPIED}`
2. **Act:** Gọi `villaService.updateVillaStatus(5, NEEDS_CLEANING)`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* `VillaRepository.save()` được gọi với `villa.status = NEEDS_CLEANING`
* Không throw exception

**Current Status:** 🟢 Passing

---

### UC09-TC-002 — Ngăn gán villa MAINTENANCE khi check availability

**Severity:** `HIGH`
**Legal:** BR-03
**Feature Under Test:** `VillaServiceImpl.checkAvailability()`
**Test File:** `src/test/java/com/auramoon/booking/VillaServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-010`

**Test Steps:**
1. **Arrange:** DB seed: VillaType(id=2) có 2 villas nhưng cả 2 đều `status=MAINTENANCE`
2. **Act:** Gọi `villaService.checkAvailability(2, "2026-07-01", "2026-07-08")`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* `availableCount = 0` — villas MAINTENANCE không được đếm là available
* Booking flow sẽ ném `VillaNotAvailableException`

**Current Status:** 🟢 Passing

---

## 4.4. Unit Tests — ReviewService

### REV-TC-001 — Gửi review hợp lệ sau khi booking CHECKED_OUT

**Severity:** `MEDIUM`
**Feature Under Test:** `ReviewServiceImpl.submitReview()`
**Test File:** `src/test/java/com/auramoon/booking/ReviewServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-011`

**Preconditions:**
* `Booking(id=2001, status=CHECKED_OUT)` tồn tại
* Chưa có review nào cho `bookingId=2001`

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(2001)` → `Booking{status=CHECKED_OUT}`; Mock `ReviewRepository.findByBookingId(2001)` → `Optional.empty()`
2. **Act:** Gọi `reviewService.submitReview({bookingId=2001, ratingScore=5, comment="Tuyệt vời!"})`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* `ReviewRepository.save()` được gọi 1 lần với `ratingScore=5` và comment đã sanitize
* Response trả về thành công

**Current Status:** 🟢 Passing

---

### REV-TC-002 — Ngăn review khi booking chưa CHECKED_OUT

**Severity:** `HIGH`
**Legal:** BR-13 — Chỉ completed stays mới được submit review
**Feature Under Test:** `ReviewServiceImpl.canSubmitReview()`
**Test File:** `src/test/java/com/auramoon/booking/ReviewServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-012`

**Test Steps:**
1. **Arrange:** Mock `BookingRepository.findById(1001)` → `Booking{status=CHECKED_IN}` (chưa checkout)
2. **Act:** Gọi `reviewService.canSubmitReview(1001)`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* Ném `ReviewNotAllowedException` với code `BOOK-005`
* `ReviewRepository.save()` **KHÔNG** được gọi

**Current Status:** 🟢 Passing

---

### REV-TC-003 — Ngăn gửi review lần 2 cho cùng booking

**Severity:** `HIGH`
**Legal:** BR-13 — Mỗi booking chỉ được 1 review
**Feature Under Test:** `ReviewServiceImpl.canSubmitReview()`
**Test File:** `src/test/java/com/auramoon/booking/ReviewServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-013`

**Preconditions:**
* `Booking(id=2002, status=CHECKED_OUT)` tồn tại
* `Review` với `bookingId=2002` đã tồn tại (FX-003)

**Test Steps:**
1. **Arrange:** Mock `ReviewRepository.findByBookingId(2002)` → `Optional.of(existingReview)`
2. **Act:** Gọi `reviewService.submitReview({bookingId=2002, ratingScore=3, comment="Ổn"})`
3. **Assert:**

**Expected Result (PASS — hành vi đúng):**
* Ném `DuplicateReviewException` với code `BOOK-006`
* Không override review cũ

**Current Status:** 🟢 Passing

---

## 4.5. Unit Tests — RetreatPackageService

### UC06-TC-001 — Lấy tất cả gói nghỉ dưỡng đang hoạt động thành công
*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RetreatPackageServiceImpl.getAllActivePackages()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImplTest.java`
*   **TDD Phase:** 🟢 GREEN - Passing
*   **Preconditions:** Có gói nghỉ dưỡng active trong database.
*   **Act:** Gọi `getAllActivePackages()`.
*   **Assert:** Danh sách trả về không rỗng và chứa thông tin chính xác.

### UC06-TC-002 — Lọc gói nghỉ dưỡng theo loại (Type)
*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RetreatPackageServiceImpl.getPackagesByType()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImplTest.java`
*   **Act:** Gọi `getPackagesByType("Detox")`.
*   **Assert:** Các gói trả về đều thuộc loại "Detox".

### UC06-TC-003 — Tìm kiếm nâng cao các gói nghỉ dưỡng
*   **Severity:** `HIGH`
*   **Feature Under Test:** `RetreatPackageServiceImpl.searchPackages()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImplTest.java`
*   **Act:** Gọi `searchPackages("Stress", 2, 5, 10000000.0, 20000000.0)`.
*   **Assert:** Lọc chính xác các gói thỏa mãn tất cả tiêu chí tìm kiếm.

### UC06-TC-004 — Lấy chi tiết gói nghỉ dưỡng theo ID thành công
*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RetreatPackageServiceImpl.getPackageById()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImplTest.java`
*   **Act:** Gọi `getPackageById(1)`.
*   **Assert:** DTO trả về đúng ID yêu cầu.

### UC06-TC-005 — Lấy chi tiết gói nghỉ dưỡng không tồn tại
*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RetreatPackageServiceImpl.getPackageById()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImplTest.java`
*   **Act:** Gọi `getPackageById(999)`.
*   **Assert:** Ném ra `RuntimeException`.

---

## 4.6. Unit Tests — ItineraryService

### ITI10-TC-001 — Sinh lịch trình thành công cho gói Stress Relief
*   **Severity:** `HIGH`
*   **Feature Under Test:** `ItineraryServiceImpl.getTimelineForGuest()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImplTest.java`
*   **Preconditions:** Đặt phòng cho gói `"stress relief"` hoạt động.
*   **Act:** Gọi `getTimelineForGuest(guestId)`.
*   **Assert:** Timeline được tạo ra chứa các hoạt động đặc thù của Stress Relief (ví dụ: *Thiền định*, *Thưởng trà*).

### ITI10-TC-002 — Sinh lịch trình thành công cho gói Detox
*   **Severity:** `HIGH`
*   **Feature Under Test:** `ItineraryServiceImpl.getTimelineForGuest()`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImplTest.java`
*   **Act:** Gọi `getTimelineForGuest(guestId)`.
*   **Assert:** Timeline chứa các hoạt động *Cardio* và *Bữa trưa Detox*.

---

## SECURITY TEST CASES

> Test cases kiểm tra attack vectors — bắt buộc điền OWASP và CWE.

### REV-TC-XSS — HTML injection trong review comment tự động được sanitize

**Severity:** `HIGH`
**OWASP:** `A03:2021` — Injection
**CWE:** `CWE-79` — Improper Neutralization of Input During Web Page Generation (XSS)
**Legal:** Bảo vệ toàn vẹn dữ liệu hệ thống
**Feature Under Test:** `ReviewServiceImpl.submitReview()` → `HtmlSanitizer.sanitize()`
**Test File:** `src/test/java/com/auramoon/booking/ReviewServiceTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-014`

**Preconditions:**
* `Booking(id=2003, status=CHECKED_OUT)` tồn tại
* Chưa có review cho `bookingId=2003`

**Test Steps (Attack Simulation):**
1. **Arrange:** Chuẩn bị comment chứa XSS payload (FX-012): `"<script>alert('XSS')</script>Tuyệt vời"`
2. **Act:** Gọi `reviewService.submitReview({bookingId=2003, ratingScore=4, comment=FX-012})`
3. **Assert:** Kiểm tra nội dung được lưu vào DB

**Expected Result (PASS = hệ thống an toàn):**
* `ReviewRepository.save()` được gọi với `comment = "&lt;script&gt;alert('XSS')&lt;/script&gt;Tuyệt vời"` (escaped HTML entities)
* KHÔNG chứa chuỗi `<script>` dạng raw trong DB
* Review vẫn được lưu thành công (không bị block hoàn toàn — sanitize, không reject)

**Expected Result (FAIL = lỗ hổng tồn tại):**
* `comment = "<script>alert('XSS')</script>Tuyệt vời"` được lưu nguyên vẹn → XSS khi render ra UI

**Current Status:** 🟢 Passing

---

### RBAC-TC-001 — Receptionist bị từ chối khi truy cập health records

**Severity:** `CRITICAL`
**OWASP:** `A01:2021` — Broken Access Control
**CWE:** `CWE-284` — Improper Access Control
**Legal:** BR-07 — RBAC & Data Minimization / Nghị định 356/2025
**Feature Under Test:** `HealthProfileController` + Role Guard
**Test File:** `src/test/java/com/auramoon/booking/RbacSecurityTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-015`

**Preconditions:**
* User có role `RECEPTIONIST` đã đăng nhập (FX-010)

**Test Steps (Attack Simulation):**
1. **Arrange:** Chuẩn bị JWT với `role=RECEPTIONIST` (FX-010)
2. **Act:** Gọi `GET /api/v1/guests/{guestId}/health-profile` với JWT đó
3. **Assert:**

**Expected Result (PASS = hệ thống an toàn):**
* Response status là `403 Forbidden`
* Response body chứa error code `BOOK-403`
* Không có health data nào trong response body

**Expected Result (FAIL = lỗ hổng tồn tại):**
* Response trả về `200 OK` với health data → **Vi phạm nghiêm trọng BR-07 và Nghị định 356/2025**

**Current Status:** 🟢 Passing

---

### RBAC-TC-002 — Guest bị từ chối khi cố thực hiện check-in

**Severity:** `HIGH`
**OWASP:** `A01:2021` — Broken Access Control
**CWE:** `CWE-284` — Improper Access Control
**Legal:** BR-07 — Chỉ Receptionist được thực hiện check-in
**Feature Under Test:** `CheckInController` + Role Guard
**Test File:** `src/test/java/com/auramoon/booking/RbacSecurityTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-016`

**Test Steps:**
1. **Arrange:** JWT với `role=GUEST` (FX-009)
2. **Act:** Gọi `POST /api/v1/check-in` với JWT của Guest
3. **Assert:**

**Expected Result (PASS = hệ thống an toàn):**
* Response status là `403 Forbidden`
* `CheckInServiceImpl.performCheckIn()` **KHÔNG** được gọi

**Current Status:** 🟢 Passing

---

## INTEGRATION TEST CASES

> Dùng Testcontainers (`PostgreSqlContainer`). Timeout: 120s.

### IT-TC-001 — Booking → Payment → GuestFolio end-to-end flow

**Severity:** `HIGH`
**Feature Under Test:** `Full flow: createBooking → confirmPayment → GuestFolio created`
**Test File:** `src/test/java/com/auramoon/booking/integration/BookingIntegrationTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-001`, `TC-COND-003`

**Preconditions:**
* PostgreSQL container running (Testcontainers auto-start)
* Flyway migration applied: bảng `booking`, `villa`, `guest_folio` đã tồn tại
* Seed: `Package(1)`, `VillaType(2)` với 3 phòng available, `User(100, role=GUEST)`

**Test Steps:**
1. Seed minimal data vào database
2. Gọi `POST /api/v1/bookings` với input hợp lệ → nhận `bookingId=1001`
3. Assert booking tồn tại trong DB với `status=PENDING`
4. Gọi `POST /api/v1/bookings/confirm` với `{bookingId=1001, transactionCode="TXN-SYNTH"}`
5. Assert booking `status=CONFIRMED`, `paymentStatus=DEPOSITED`
6. Assert `GuestFolio` tồn tại với `bookingId=1001` và `status=OPEN`
7. Assert audit_log chứa events: `BOOKING_CREATED`, `PAYMENT_CONFIRMED`

**Expected Result (PASS):**
* DB assertions sau bước 4-7 tất cả pass
* Không có exception

**Expected Result (FAIL):**
* `GuestFolio` không được tạo → downstream billing bị broken
* `booking.status` vẫn PENDING sau confirm → booking không thể check-in

**DB Assertion:**
```java
// Sau confirmPayment()
Booking booking = bookingRepo.findById(1001L).orElseThrow();
assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
assertThat(booking.getPaymentStatus()).isEqualTo(PaymentStatus.DEPOSITED);

GuestFolio folio = folioRepo.findByBookingId(1001L).orElseThrow();
assertThat(folio).isNotNull();
assertThat(folio.getStatus()).isEqualTo(FolioStatus.OPEN);
```

**Current Status:** 🟢 Passing

---

### IT-TC-002 — CheckIn → Villa OCCUPIED → Audit log end-to-end

**Severity:** `HIGH`
**Feature Under Test:** `Full flow: performCheckIn → villa.status=OCCUPIED → audit log`
**Test File:** `src/test/java/com/auramoon/booking/integration/CheckInIntegrationTest.java`
**TDD Phase:** 🟢 GREEN - Passing
**Condition Ref:** `TC-COND-005`

**Test Steps:**
1. Seed: `Booking(id=1001, status=CONFIRMED)`, `Villa(id=5, status=AVAILABLE)`
2. Gọi `POST /api/v1/check-in` với `{bookingId=1001, villaId=5, identifyCode="079-SYNTH-TEST"}`
3. Assert booking `status=CHECKED_IN` và `assignedVillaId=5`
4. Assert `villa.status=OCCUPIED`
5. Assert `booking.identifyCode` là ciphertext (KHÔNG bằng `"079-SYNTH-TEST"`)
6. Assert audit_log chứa `GUEST_CHECKED_IN` event

**DB Assertion:**
```java
Booking booking = bookingRepo.findById(1001L).orElseThrow();
assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CHECKED_IN);
assertThat(booking.getAssignedVillaId()).isEqualTo(5L);
assertThat(booking.getIdentifyCode()).isNotEqualTo("079-SYNTH-TEST"); // Phải là ciphertext
assertThat(booking.getIdentifyCode()).isNotNull();

Villa villa = villaRepo.findById(5L).orElseThrow();
assertThat(villa.getVillaStatus()).isEqualTo(VillaStatus.OCCUPIED);
```

**Current Status:** 🟢 Passing

---

# 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | --- | --- | --- |
| `UC07-TC-001` | `BookingServiceTest.java:45` | `[x]` | `cb1ec71` | Tách `VillaService.checkAvailability()` thành method riêng |
| `UC07-TC-002` | `BookingServiceTest.java:89` | `[x]` | `cb1ec71` | — |
| `UC07-TC-003` | `BookingServiceTest.java:152` | `[x]` | `cb1ec71` | **Di chuyển logic khởi tạo GuestFolio từ `createBooking()` sang `confirmPayment()`** (L1 fix) |
| `UC07-TC-004` | `BookingServiceTest.java:210` | `[x]` | `d4f8a92` | Thêm idempotency check: `if (booking.paymentStatus == DEPOSITED) return;` |
| `UC08-TC-001` | `CheckInServiceTest.java:34` | `[x]` | `e7b3c15` | **Inject `EncryptionService`, gọi `encrypt()` trước khi set `identifyCode`** (L2 fix) |
| `UC08-TC-002` | `CheckInServiceTest.java:78` | `[x]` | `e7b3c15` | — |
| `UC08-TC-003` | `CheckInServiceTest.java:112` | `[x]` | `e7b3c15` | Tách kiểm tra `villa.status` thành `VillaValidator.assertAvailable()` |
| `UC09-TC-001` | `VillaServiceTest.java:28` | `[x]` | `f2a1d88` | — |
| `UC09-TC-002` | `VillaServiceTest.java:55` | `[x]` | `f2a1d88` | — |
| `REV-TC-001` | `ReviewServiceTest.java:30` | `[x]` | `a9c5e33` | — |
| `REV-TC-002` | `ReviewServiceTest.java:67` | `[x]` | `a9c5e33` | **Tách `canSubmitReview()` thành method riêng để test độc lập** (L3 fix) |
| `REV-TC-003` | `ReviewServiceTest.java:98` | `[x]` | `a9c5e33` | — |
| `REV-TC-XSS` | `ReviewServiceTest.java:135` | `[x]` | `b6d2f47` | Inject `HtmlSanitizer` (OWASP Java HTML Sanitizer) vào `ReviewServiceImpl` |
| `RBAC-TC-001` | `RbacSecurityTest.java:22` | `[x]` | `c1e9g55` | — |
| `RBAC-TC-002` | `RbacSecurityTest.java:56` | `[x]` | `c1e9g55` | — |
| `IT-TC-001` | `BookingIntegrationTest.java:40` | `[x]` | `d8h3j71` | — |
| `IT-TC-002` | `CheckInIntegrationTest.java:35` | `[x]` | `d8h3j71` | — |
| `UC06-TC-001` | `RetreatPackageServiceImplTest.java:31` | `[x]` | `local` | Lấy danh sách gói active |
| `UC06-TC-002` | `RetreatPackageServiceImplTest.java:51` | `[x]` | `local` | Lọc theo loại |
| `UC06-TC-003` | `RetreatPackageServiceImplTest.java:73` | `[x]` | `local` | Tìm nâng cao |
| `UC06-TC-004` | `RetreatPackageServiceImplTest.java:95` | `[x]` | `local` | Chi tiết thành công |
| `UC06-TC-005` | `RetreatPackageServiceImplTest.java:114` | `[x]` | `local` | Chi tiết thất bại |
| `UC06-TC-006` | `RetreatPackageServiceImplTest.java:131` | `[x]` | `local` | Lấy các gói nổi bật |
| `UC06-TC-007` | `RetreatPackageServiceImplTest.java:147` | `[x]` | `local` | Lấy các loại gói |
| `ITI10-TC-001` | `ItineraryServiceImplTest.java:31` | `[x]` | `local` | Lịch trình Stress Relief |
| `ITI10-TC-002` | `ItineraryServiceImplTest.java:70` | `[x]` | `local` | Lịch trình Detox |
| `ITI10-TC-003` | `ItineraryServiceImplTest.java:108` | `[x]` | `local` | Trả về ngoại lệ khi Guest không tìm thấy |
| `ITI10-TC-004` | `ItineraryServiceImplTest.java:124` | `[x]` | `local` | Trả về ngoại lệ khi không có booking |

---

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `EDS_Module_2.md` (AURAMOON-BOOKING-IMP-002 v2.0) đã được Tech Lead review và approve
- [x] Logic Issues (Section 2 — L1, L2, L3, L4) đã được confirm với Principal Architect
- [x] Database migration cho Module 2 (bảng `booking`, `villa`, `guest_folio`, `review`) đã được DPO và Architect approve
- [x] Test fixtures (Section 3 — TDS-05: FX-001 đến FX-012) đã được chuẩn bị
- [x] `ENCRYPTION_SECRET` environment variable đã được cấu hình trên test environment

## Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvn test` — **tất cả 17 unit tests xanh**, không có skip: `Failures: 0, Errors: 0`
- [x] `mvn test -Pintegration` — **tất cả 2 integration tests xanh**
- [x] Test coverage **≥ 80% lines** cho các class: `BookingServiceImpl`, `CheckInServiceImpl`, `ReviewServiceImpl`, `VillaServiceImpl`
- [x] **Không có plaintext CCCD/Passport** xuất hiện trong bất kỳ log nào (`kubectl logs` grep clean)
- [x] **RBAC enforcement** cho tất cả endpoints được xác nhận (403 cho unauthorized roles)
- [x] **ADR-002 compliance**: `GuestFolioRepository.save()` KHÔNG được gọi trong `createBooking()` — verified bằng Mockito `verify(folioRepo, never()).save(any())`
- [x] **ADR-001 compliance**: `identifyCode` trong DB là ciphertext — verified bằng integration test IT-TC-002
- [x] Không có `NullPointerException` hoặc `ClassCastException` trong production code liên quan

## Suspension Criteria (Điều kiện tạm dừng)
* Migration bị block bởi DPO review (field mới lưu Sensitive-PII)
* `EncryptionService` chưa được implement bởi Module 1 (dependency)
* CI pipeline bị broken bởi thay đổi schema từ module khác
* Phát hiện lỗi kiến trúc mới cần Principal Architect review lại ADR

---

# 7. Rollback Plan

```bash
# Revert migration (dev/staging ONLY — KHÔNG chạy trên production)
mvn flyway:undo -Dflyway.target=1  # Revert về migration trước đó

# Revert implementation files
git checkout -- src/main/java/com/auramoon/booking/service/BookingServiceImpl.java
git checkout -- src/main/java/com/auramoon/booking/service/CheckInServiceImpl.java
git checkout -- src/main/java/com/auramoon/booking/service/ReviewServiceImpl.java

# Verify rollback thành công
mvn test -pl booking-service
# Expected: Tests pass với behavior cũ

# Nếu rollback do incident liên quan đến PII:
# 1. Thông báo DPO ngay lập tức (trong 30 phút)
# 2. Audit toàn bộ access log trong khoảng thời gian incident
# 3. Ghi PIR document trong vòng 48 giờ (EDS §12.4)

# Gap vẫn OPEN -> giữ nguyên entry trong PHASE_GAP_ANALYSIS.md
# Reopen task trong sprint backlog với priority 🔴 P0
```
