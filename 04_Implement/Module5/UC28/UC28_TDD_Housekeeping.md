# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## Đặc tả Kiểm thử Hướng Phát triển — UC28 Housekeeping Management

- **Document ID**: AURA-BILLING-TDD-028
- **Version**: 1.0
- **Date**: 2026-06-20
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Tech Lead & Module 5 Owner
- **Reviewed by**: [ ] Phùng Giang Hải — Pending
- **DPO Sign-off**: [ ] N/A — Module không xử lý PII
- **Approved by**: [ ] Pending
- **Classification**: Internal

### References:
- `04_Implement/Module5/UC28/UC28_EDS_Housekeeping.md` (AURA-BILLING-IMP-028) — Technical Specification
- `02_Requirement/Module5/SRS_Document.md` — Section 2.7.1 UC28
- `08_Document_References/Retreat.md` — UC28 Business Rules
- `03_Design/Database/DB.sql` — Table VILLA (line 104)

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases song song với production code.
> Thứ tự: viết test → chạy → xác nhận → ghi nhận kết quả.
> Test data dùng SYNTHETIC. Không dùng PII thật.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày       | Người thực hiện    | Nội dung thay đổi                                           |
| ---------- | ------------------- | -------------------------------------------------------------- |
| 2026-06-20 | Phùng Giang Hải        | Khởi tạo tài liệu TDD cho UC28 - Housekeeping Management. Viết 8 test cases, chạy và ghi nhận kết quả. |

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
| :--- | :--- |
| **Feature / Gap ID** | UC28 |
| **Module** | `Housekeeping Management — Operations / Room Management` |
| **Spec gốc** | `AURA-BILLING-IMP-028` (UC28_EDS_Housekeeping.md) |
| **Priority** | 🟠 P1 |
| **Sprint** | S4 (2026-06-20 -> 2026-07-04) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | `Booking Module (VILLA, VillaRepository)`, `Billing Module (AuditLogRepository)` |
| **Downstream Consumers** | `UC08 (Check-in & Room Assignment)` |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**
> Liệt kê mọi sai lệch giữa spec thiết kế và schema/codebase thực tế.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / code) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | EDS §5.2 khai báo `cleaning_status VARCHAR(10)` | DB.sql thực tế: `cleaning_status VARCHAR(10) CHECK (cleaning_status IN ('CLEAN', 'DIRTY', 'CLEANING'))` — Đã khớp 100%. | Không cần fix. Test sử dụng đúng 3 giá trị CHECK constraint. |
| L2 | EDS §8.1 khai báo method `getDirtyAndCleaningVillas()` | VillaRepository ban đầu chưa có method `findByCleaningStatusInAndIsDeleteFalse()`. | Đã bổ sung method vào `VillaRepository.java` trước khi viết test. |
| L3 | EDS §6.3 State Machine cho phép Approve trực tiếp từ DIRTY | Thực tế nên cho phép cả DIRTY → CLEAN (Manager dọn tự tay rồi duyệt luôn) lẫn CLEANING → CLEAN. | Test `TC-003` verify từ CLEANING. `approveAndUpdateToClean()` chấp nhận cả DIRTY và CLEANING (chỉ reject khi đã CLEAN). |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

> [!NOTE]
> `UC28 Housekeeping Management` bao gồm các layer được kiểm thử:

- **Service Layer** (core business logic — mock Repository) ← **Phạm vi chính của TDD này**
- Domain Entity (`Villa.java`) — sử dụng trực tiếp, không mock
- Repository Layer (`VillaRepository`) — mock bằng Mockito
- Audit Log (`AuditLogRepository`) — mock bằng Mockito

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC28 (§2.7.1) | Normal Flow: Xem danh sách DIRTY → Phân công → Nghiệm thu → Approve/Reject |
| `SRS.md` UC28 A1 | Alternative Flow: Từ chối nghiệm thu → Gán lại DIRTY |
| `SRS.md` UC28 E1 | Exception Flow: Database error khi update |
| BR-22 | Chỉ Manager/Admin mới được đổi DIRTY → CLEAN |
| BR-15 | Mọi thao tác phải ghi Audit Log |
| ADR-028 | Single-page Dashboard (không ảnh hưởng unit test) |
| EDS §6.3 | State Machine: AVAILABLE → OCCUPIED → DIRTY → CLEANING → CLEAN |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Lấy danh sách Villa DIRTY/CLEANING | `HousekeepingServiceImpl.getDirtyAndCleaningVillas()` | `UC28-TC-001`, `UC28-TC-008` |
| TC-COND-002 | Phân công nhân viên (DIRTY → CLEANING) | `HousekeepingServiceImpl.assignHousekeeper()` | `UC28-TC-002`, `UC28-TC-007` |
| TC-COND-003 | Duyệt sạch (CLEANING → CLEAN + AVAILABLE) | `HousekeepingServiceImpl.approveAndUpdateToClean()` | `UC28-TC-003`, `UC28-TC-005`, `UC28-TC-006` |
| TC-COND-004 | Từ chối (CLEANING → DIRTY) | `HousekeepingServiceImpl.rejectCleaning()` | `UC28-TC-004` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | `cleaning_status` input domain: {CLEAN, DIRTY, CLEANING} | 3 phân vùng tương đương cho trạng thái Villa |
| State Transition Testing | Villa cleaning lifecycle (DIRTY → CLEANING → CLEAN) | Kiểm tra mọi chuyển tiếp hợp lệ và bất hợp lệ trong State Machine EDS §6.3 |
| Boundary Value Analysis | `villaId` = giá trị không tồn tại (999) | Kiểm tra xử lý khi không tìm thấy entity |
| Error Guessing | Approve villa đã CLEAN, Assign villa đã CLEANING | Dự đoán lỗi logic người dùng bấm nút sai thứ tự |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | Mock Entity | `Villa(id=2, villaCode="V-002", cleaningStatus="DIRTY", villaStatus="OCCUPIED")` | Happy path — Assign, Approve |
| FX-002 | Mock Entity | `Villa(id=3, villaCode="V-003", cleaningStatus="CLEANING", villaStatus="OCCUPIED")` | Happy path — Approve, Reject |
| FX-003 | Mock Entity | `Villa(id=1, villaCode="V-001", cleaningStatus="CLEAN", villaStatus="AVAILABLE")` | Error path — Already clean |
| FX-004 | Mock Return | `Optional.empty()` for `villaId=999` | Error path — Not found |

---

## 4. Test Case Specification

- **TC ID format**: `UC28-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

> [!NOTE]
> **Status**: 🔴 Not written / 🟡 Written-failing / 🟢 Passing

---

### `UC28-TC-001` — Lấy danh sách Villa DIRTY/CLEANING thành công
- **Severity**: HIGH
- **Feature Under Test**: `HousekeepingServiceImpl.getDirtyAndCleaningVillas()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- FX-001 (DIRTY), FX-002 (CLEANING), FX-003 (CLEAN) đã được mock.
- `VillaRepository.findByCleaningStatusInAndIsDeleteFalse()` trả về [FX-001, FX-002].

#### Test Steps:
1. **Arrange**: Mock repository trả về 2 Villa (DIRTY + CLEANING).
2. **Act**: Gọi `getDirtyAndCleaningVillas()`.
3. **Assert**: Kết quả có 2 phần tử. V-001 (CLEAN) KHÔNG nằm trong danh sách.

#### Expected Result (PASS):
- `result.size() == 2`
- Chứa V-002 (DIRTY) và V-003 (CLEANING).
- KHÔNG chứa V-001 (CLEAN).

#### Expected Result (FAIL):
- Danh sách trả về chứa Villa CLEAN → lỗi filter logic.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-002` — Phân công nhân viên dọn dẹp thành công
- **Severity**: HIGH
- **Feature Under Test**: `HousekeepingServiceImpl.assignHousekeeper()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- FX-001: Villa V-002 có `cleaning_status = 'DIRTY'`.

#### Test Steps:
1. **Arrange**: Mock `findById(2)` trả về FX-001.
2. **Act**: Gọi `assignHousekeeper(2, "Nguyễn Văn A", 1)`.
3. **Assert**: `cleaning_status` chuyển thành `"CLEANING"`. Audit Log ghi nhận `HOUSEKEEPING_ASSIGN`.

#### Expected Result (PASS):
- `dirtyVilla.getCleaningStatus() == "CLEANING"`
- `villaRepository.save()` được gọi 1 lần.
- `auditLogRepository.saveAuditLog("HOUSEKEEPING_ASSIGN", 1, ...)` được gọi 1 lần.

#### Expected Result (FAIL):
- `cleaning_status` không thay đổi → thiếu `villa.setCleaningStatus()`.
- Audit log không ghi nhận → vi phạm BR-15.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-003` — Duyệt sạch thành công (Approve)
- **Severity**: CRITICAL
- **Feature Under Test**: `HousekeepingServiceImpl.approveAndUpdateToClean()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- FX-002: Villa V-003 có `cleaning_status = 'CLEANING'`.

#### Test Steps:
1. **Arrange**: Mock `findById(3)` trả về FX-002.
2. **Act**: Gọi `approveAndUpdateToClean(3, 1)`.
3. **Assert**: `cleaning_status = "CLEAN"` VÀ `villa_status = "AVAILABLE"`. Audit Log ghi nhận.

#### Expected Result (PASS):
- `cleaningVilla.getCleaningStatus() == "CLEAN"`
- `cleaningVilla.getVillaStatus() == "AVAILABLE"`
- `auditLogRepository.saveAuditLog("HOUSEKEEPING_APPROVE", 1, ...)` được gọi 1 lần.

#### Expected Result (FAIL):
- `villa_status` không chuyển sang `AVAILABLE` → Lễ tân không thể gán phòng cho khách mới.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-004` — Từ chối nghiệm thu (Reject)
- **Severity**: HIGH
- **Feature Under Test**: `HousekeepingServiceImpl.rejectCleaning()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- FX-002: Villa V-003 có `cleaning_status = 'CLEANING'`.

#### Test Steps:
1. **Arrange**: Mock `findById(3)` trả về FX-002.
2. **Act**: Gọi `rejectCleaning(3, 1)`.
3. **Assert**: `cleaning_status` quay về `"DIRTY"`. Audit Log ghi nhận `HOUSEKEEPING_REJECT`.

#### Expected Result (PASS):
- `cleaningVilla.getCleaningStatus() == "DIRTY"`
- Audit log chứa `"HOUSEKEEPING_REJECT"` và `"V-003"`.

#### Expected Result (FAIL):
- `cleaning_status` giữ nguyên `CLEANING` → Nhân viên không biết phải dọn lại.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-005` — Duyệt Villa đã sạch rồi (Error - HK-002)
- **Severity**: MEDIUM
- **Feature Under Test**: `HousekeepingServiceImpl.approveAndUpdateToClean()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- FX-003: Villa V-001 có `cleaning_status = 'CLEAN'`.

#### Test Steps:
1. **Arrange**: Mock `findById(1)` trả về FX-003 (đã CLEAN).
2. **Act**: Gọi `approveAndUpdateToClean(1, 1)`.
3. **Assert**: Throw `IllegalStateException("Villa is already clean.")`. Villa KHÔNG bị save. Audit log KHÔNG ghi.

#### Expected Result (PASS):
- Exception `IllegalStateException` được throw với message chính xác.
- `villaRepository.save()` KHÔNG bao giờ được gọi.
- `auditLogRepository.saveAuditLog()` KHÔNG bao giờ được gọi.

#### Expected Result (FAIL):
- Không throw exception → Villa bị ghi đè trạng thái vô ích, gây duplicate audit log.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-006` — Villa không tồn tại (Error - HK-003)
- **Severity**: MEDIUM
- **Feature Under Test**: `HousekeepingServiceImpl.approveAndUpdateToClean()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- FX-004: `villaId = 999` không tồn tại trong DB. Mock trả về `Optional.empty()`.

#### Test Steps:
1. **Arrange**: Mock `findById(999)` trả về `Optional.empty()`.
2. **Act**: Gọi `approveAndUpdateToClean(999, 1)`.
3. **Assert**: Throw `RuntimeException("Villa not found")`.

#### Expected Result (PASS):
- Exception `RuntimeException` được throw chứa message `"Villa not found"`.
- `villaRepository.save()` KHÔNG bao giờ được gọi.

#### Expected Result (FAIL):
- `NullPointerException` → thiếu xử lý `Optional.empty()`.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-007` — Assign Villa không phải DIRTY (Error - Invalid State Transition)
- **Severity**: MEDIUM
- **Feature Under Test**: `HousekeepingServiceImpl.assignHousekeeper()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- FX-002: Villa V-003 có `cleaning_status = 'CLEANING'` (đã phân công rồi).

#### Test Steps:
1. **Arrange**: Mock `findById(3)` trả về FX-002 (CLEANING).
2. **Act**: Gọi `assignHousekeeper(3, "Trần B", 1)`.
3. **Assert**: Throw `IllegalStateException` chứa message `"DIRTY"`.

#### Expected Result (PASS):
- Exception `IllegalStateException` được throw.
- Villa KHÔNG bị save.
- Audit log KHÔNG ghi nhận.

#### Expected Result (FAIL):
- Cho phép assign lại khi đang CLEANING → gây nhầm lẫn nhân viên.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

### `UC28-TC-008` — Danh sách trống khi tất cả Villa đều sạch
- **Severity**: LOW
- **Feature Under Test**: `HousekeepingServiceImpl.getDirtyAndCleaningVillas()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java`
- **TDD Phase**: 🟢 GREEN — Đã PASS
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Tất cả Villa trong DB đều có `cleaning_status = 'CLEAN'`. Mock trả về `Collections.emptyList()`.

#### Test Steps:
1. **Arrange**: Mock repository trả về danh sách rỗng.
2. **Act**: Gọi `getDirtyAndCleaningVillas()`.
3. **Assert**: Kết quả `not null` và `isEmpty()`.

#### Expected Result (PASS):
- `result != null` VÀ `result.isEmpty() == true`.
- Dashboard hiển thị trạng thái "Không có phòng nào cần dọn".

#### Expected Result (FAIL):
- `result == null` → `NullPointerException` khi Thymeleaf render View.

- **Current Status**: 🟢 Passing
- **Run Result**: `Tests run: 1, Failures: 0, Errors: 0` (2026-06-20 22:45:05)

---

## SECURITY TEST CASES

> [!NOTE]
> Test cases kiểm tra attack vectors — Phân quyền RBAC (BR-22).
> Security test cho UC28 chủ yếu ở tầng Controller (`@PreAuthorize`), sẽ bổ sung khi implement Controller.

### `UC28-SEC-001` — Receptionist không được phép truy cập Housekeeping Dashboard
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Feature Under Test**: `HousekeepingController` — `@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")`
- **Test File**: *(Sẽ tạo khi implement Controller)*
- **TDD Phase**: 🔴 RED — chưa implement Controller

#### Preconditions:
- User đăng nhập với role `RECEPTIONIST`.

#### Test Steps (Attack Simulation):
1. Đăng nhập với tài khoản có role RECEPTIONIST.
2. Truy cập GET `/housekeeping`.
3. Kiểm tra response.

#### Expected Result (PASS = hệ thống an toàn):
- HTTP 403 Forbidden.

#### Expected Result (FAIL = lỗ hổng tồn tại):
- HTTP 200 OK → Lễ tân có thể tự ý chuyển trạng thái phòng, vi phạm BR-22.

- **Current Status**: 🔴 Not written (chờ implement Controller)

---

## INTEGRATION TEST CASES

> [!NOTE]
> Integration test cho full flow: Assign → Approve / Assign → Reject → Re-Assign → Approve.
> Sẽ bổ sung khi có DB test environment.

### `UC28-TC-INT-001` — Luồng hoàn chỉnh: Assign → Approve
- **Severity**: HIGH
- **Feature Under Test**: Full flow: `assignHousekeeper()` → `approveAndUpdateToClean()`
- **Test File**: *(Sẽ tạo)*
- **TDD Phase**: 🔴 RED — chưa implement
- **Condition Ref**: `TC-COND-002, TC-COND-003`

#### Preconditions:
- DB có Villa V-002 với `cleaning_status = 'DIRTY'`.

#### Test Steps:
1. Gọi `assignHousekeeper(2, "Trần B", 1)`.
2. Verify `cleaning_status = 'CLEANING'`.
3. Gọi `approveAndUpdateToClean(2, 1)`.
4. Verify `cleaning_status = 'CLEAN'`, `villa_status = 'AVAILABLE'`.
5. Verify `AUDIT_LOG` có 2 dòng: `HOUSEKEEPING_ASSIGN` và `HOUSEKEEPING_APPROVE`.

#### Expected Result (PASS):
- Villa kết thúc ở trạng thái `CLEAN / AVAILABLE`.
- Audit log có đúng 2 entries theo đúng thứ tự thời gian.

#### Expected Result (FAIL):
- Villa bị kẹt ở trạng thái trung gian.
- Audit log thiếu entries.

- **Current Status**: 🔴 Not written (chờ DB test environment)

---

## 5. Red-Green-Refactor Tracker

> [!NOTE]
> Kết quả chạy test lúc: **2026-06-20 22:45:05 (GMT+7)**
> Build tool: Maven Surefire 3.5.5, JUnit 5, Mockito
> Thời gian chạy: **1.684 giây**

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (timestamp) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `UC28-TC-001` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | Khởi tạo logic getDirtyAndCleaningVillas |
| `UC28-TC-002` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | Thêm validation check DIRTY state |
| `UC28-TC-003` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | Bổ sung audit log HOUSEKEEPING_APPROVE |
| `UC28-TC-004` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | Set lại trạng thái DIRTY |
| `UC28-TC-005` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | — |
| `UC28-TC-006` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | — |
| `UC28-TC-007` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | — |
| `UC28-TC-008` | `HousekeepingServiceTest.java` | [x] | `2026-06-20 22:51:56` | — |
| `UC28-SEC-001` | *(chưa tạo)* | [ ] | — | Chờ implement Controller |
| `UC28-TC-INT-001` | *(chưa tạo)* | [ ] | — | Chờ DB test environment |

### Test Execution Summary (RED Phase)

```
[ERROR] Failures: 7, Errors: 1
[INFO] BUILD FAILURE
[INFO] Finished at: 2026-06-20T22:51:33+07:00
```

### Test Execution Summary (GREEN Phase)

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.AuraMoon.auramoon.housekeeping.service.HousekeepingServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.332 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
[INFO] Finished at: 2026-06-20T22:51:56+07:00
```

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AURA-BILLING-IMP-028` (EDS UC28) đã được tạo
- [x] Logic Issues (Section 2) đã được phân tích và confirm
- [x] Database schema cho bảng VILLA đã có cột `cleaning_status` (đã tồn tại trong DB.sql)
- [x] Test fixtures (Section 3 TDS-05) đã được chuẩn bị (SYNTHETIC data)
- [x] VillaRepository đã được bổ sung method `findByCleaningStatusInAndIsDeleteFalse()`

### Exit Criteria (Điều kiện kết thúc — DoD)
- [x] `mvnw test` — tất cả 8 unit tests xanh (không có skip)
- [ ] Integration tests xanh *(chưa viết — chờ DB test environment)*
- [x] Không có lỗi compile trong production code liên quan
- [x] Test data classification: SYNTHETIC — không dùng PII thật
- [ ] Controller + Security test *(chưa implement — chờ giai đoạn tiếp theo)*

### Suspension Criteria (Điều kiện tạm dừng)
- Spring Security chưa cấu hình role MANAGER → chặn việc viết Security test
- DB test environment (H2 hoặc Testcontainers) chưa setup → chặn Integration test

---

## 7. Rollback Plan

### Revert production code (nếu cần)
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/housekeeping/
git checkout -- src/test/java/com/AuraMoon/auramoon/housekeeping/
```

### Revert VillaRepository change
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/booking/repository/VillaRepository.java
```

### Files tạo mới bởi UC28
- `src/main/java/com/AuraMoon/auramoon/housekeeping/service/IHousekeepingService.java` — [NEW]
- `src/main/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceImpl.java` — [NEW]
- `src/test/java/com/AuraMoon/auramoon/housekeeping/service/HousekeepingServiceTest.java` — [NEW]
- `src/main/java/com/AuraMoon/auramoon/booking/repository/VillaRepository.java` — [MODIFIED] (thêm 1 method)
