# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: SU26-MOD5-TDD-UC33
- **Version**: 1.0
- **Date**: 2026-06-26
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [x] Tech Lead — Approved
- **DPO Sign-off**: [x] Approved
- **Approved by**: [x] Principal Architect
- **Classification**: Internal — Confidential

### References:
- `01_Requirements/Module5/SRS_Document.md` — Functional requirements
- `04_Implement/Module5/UC33/EDS_UC33_StaffProfileDetails.md` — Technical Specification (ADR-001, ADR-002)
- PDPA / GDPR Art. 5.1(c) — Data Minimization

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`.spec.ts` / `.java`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-26 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho View Staff Profile Details (UC33) |

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
| **Feature / Gap ID** | UC33 |
| **Module** | HR Management (Module 5) |
| **Spec gốc** | EDS_UC33_StaffProfileDetails.md |
| **Priority** | 🔴 P0 |
| **Sprint** | S3 (2026-06-20 -> 2026-07-04) |
| **Milestone** | M3 Alpha |
| **Data Classification** | Sensitive-PII (Email, Phone) |
| **Compliance Scope** | PDPA / GDPR |
| **Upstream Dependencies** | UserService, TherapistService, AuditLogService |
| **Downstream Consumers** | Frontend (Thymeleaf UI) |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Truy vấn thông tin nhân sự chỉ cần query bảng `USER`. | Hồ sơ được chia cắt theo Role. VD: Therapist có thêm `therapist_code` và performance KPIs ở bảng `THERAPIST`. | Test phải rẽ nhánh (Polymorphic Fetching): verify Aggregator gọi đúng `TherapistService` nếu Role == THERAPIST. |
| L2 | View Thymeleaf trực tiếp nhận `UserEntity`. | `UserEntity` chứa `password_hash` làm rò rỉ dữ liệu. | Test bắt buộc map qua `FullStaffProfileDTO` và assert KHÔNG chứa password. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`View Staff Profile Details` bao gồm các layer:
- Services / Aggregator (Logic tổng hợp từ nhiều Repository)
- Controller (Mock các Service)
- Integration (Testcontainers MySQL)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS_Document.md` UC33 | Hiển thị Profile, Rẽ nhánh logic cho Therapist. |
| EDS ADR-001 | Sử dụng `StaffProfileAggregator`. |
| EDS ADR-002 (BR-07) | Data Minimization: Ẩn Password Hash. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Aggregator lấy đủ data nếu là Therapist | `StaffProfileAggregator.getAggregatedProfile()` | `HR-TC-001` |
| TC-COND-002 | Aggregator bỏ qua Therapist API nếu Role khác | `StaffProfileAggregator.getAggregatedProfile()` | `HR-TC-002` |
| TC-COND-003 | Staff Not Found | `StaffProfileAggregator.getAggregatedProfile()` | `HR-TC-003` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | `staffId` tồn tại vs Không tồn tại | Cover Happy path & 404 Exception |
| State Transition Testing | Role Enum (`THERAPIST` vs `RECEPTIONIST`) | Đảm bảo rẽ nhánh gọi API tương ứng |
| Error Guessing | Security Test | Truy cập bằng Role không có quyền (RBAC) |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | Object Mock | `{ id: 1, role: 'THERAPIST', name: 'Spa A' }` | Lấy Therapist Data |
| FX-002 | Object Mock | `{ id: 2, role: 'RECEPTIONIST', name: 'Le B' }` | Lấy Non-Therapist Data |
| FX-003 | Context Mock | `ROLE_RECEPTIONIST` | Auth Deny 403 Test |

---

## 4. Test Case Specification

### `HR-TC-001` — Lấy hồ sơ thành công cho Therapist
- **Severity**: HIGH
- **CWE**: N/A
- **Feature Under Test**: `StaffProfileAggregator.getAggregatedProfile()`
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileAggregatorTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Mock `UserService` trả về User có role `THERAPIST`.
- Mock `TherapistService` trả về `TherapistDTO`.

#### Test Steps:
1. Arrange: Setup Mock trả về FX-001.
2. Act: Gọi `aggregator.getAggregatedProfile(1L)`.
3. Assert: Kiểm tra DTO trả về.

#### Expected Result (PASS):
- DTO không null.
- `roleName` == "THERAPIST".
- `therapistCode` không null (chứng tỏ đã gọi TherapistService).

#### Expected Result (FAIL):
- Bị NullPointerException hoặc thiếu `therapistCode`.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Verify `TherapistService` được gọi đúng 1 lần.

---

### `HR-TC-002` — Lấy hồ sơ thành công cho Non-Therapist
- **Severity**: HIGH
- **Feature Under Test**: `StaffProfileAggregator.getAggregatedProfile()`
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileAggregatorTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- Mock `UserService` trả về User có role `RECEPTIONIST`.

#### Test Steps:
1. Arrange: Setup Mock trả về FX-002.
2. Act: Gọi `aggregator.getAggregatedProfile(2L)`.
3. Assert: Kiểm tra DTO trả về.

#### Expected Result (PASS):
- DTO không null, `roleName` == "RECEPTIONIST".
- `therapistCode` == null.
- `TherapistService` KHÔNG ĐƯỢC GỌI.

#### Expected Result (FAIL):
- Exception do gọi `TherapistService` nhầm người.

- **Current Status**: 🔴 Not written

---

### `HR-TC-003` — Báo lỗi 404 khi nhân sự không tồn tại
- **Severity**: MEDIUM
- **Feature Under Test**: `StaffProfileAggregator.getAggregatedProfile()`
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileAggregatorTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- Mock `UserService` ném ra `EntityNotFoundException`.

#### Test Steps:
1. Arrange: Mock `userService.getUserAndRole(99L)` ném lỗi.
2. Act & Assert: Gọi `aggregator.getAggregatedProfile(99L)` và bắt Exception.

#### Expected Result (PASS):
- Ném ra `ResourceNotFoundException`.

- **Current Status**: 🔴 Not written

---

## SECURITY TEST CASES

### `HR-TC-SEC-001` — Data Minimization (Không chứa Password)
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control / Data Leak
- **CWE**: CWE-200 — Exposure of Sensitive Information to an Unauthorized Actor
- **Legal**: PDPA / GDPR Art 5.1(c)
- **Feature Under Test**: `StaffProfileAggregator` Mapping
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileAggregatorTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- `User` Entity Mock có chứa `password_hash = "bcrypt_hash_secret"`.

#### Test Steps:
1. Gọi API hoặc function Aggregator.
2. Lấy `FullStaffProfileDTO`.
3. Dùng Java Reflection hoặc getter để tìm trường liên quan đến password.

#### Expected Result (PASS):
- Hoàn toàn KHÔNG CÓ trường `password` hoặc `passwordHash` trong DTO trả về.
- Nếu cố tình map, giá trị phải là `null`.

#### Expected Result (FAIL):
- DTO chứa field `password_hash` và bị render ra View/API.

- **Current Status**: 🔴 Not written

---

### `HR-TC-SEC-002` — Ngăn chặn Unauthorized Access
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: GDPR Art 25
- **Feature Under Test**: `StaffProfileController` Security Guards
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileControllerTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Khởi chạy `@WebMvcTest` kết hợp Spring Security.
- Mock session user với Role `RECEPTIONIST` (FX-003).

#### Test Steps:
1. `mockMvc.perform(get("/manager/staff/profile/1").with(user("test").roles("RECEPTIONIST")))`

#### Expected Result (PASS):
- Trả về HTTP Status `403 Forbidden`.

#### Expected Result (FAIL):
- Request đi lọt qua Controller và trả về HTTP 200.

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES

### `HR-TC-INT-001` — Full Flow Lấy Profile Therapist
- **Severity**: HIGH
- **Feature Under Test**: End-to-end `Controller -> Service -> DB`
- **Test File**: `src/test/java/com/harmony/hr/StaffProfileIntegrationTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Testcontainers MySQL Database đang chạy.
- Schema được tự động migrate.
- Seed data: 1 User (role_id=Therapist), 1 bảng THERAPIST tương ứng, 1 AUDIT_LOG.

#### Test Steps:
1. Dùng `MockMvc` hoặc `RestTemplate` gọi `GET /manager/staff/profile/1` với Security Context hợp lệ (`MANAGER`).
2. Capture Model Attributes.

#### Expected Result (PASS):
- Trả về 200 OK.
- View name là `manager/staff-profile-details`.
- Biến Model `profile` có `fullName` đúng DB, `therapistCode` đúng, và list `recentActivities` size == 1.

#### Expected Result (FAIL):
- 500 Internal Server Error do config query JPA sai.

- **Current Status**: 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `HR-TC-001` | `StaffProfileAggregatorTest.java` | [ ] | | |
| `HR-TC-002` | `StaffProfileAggregatorTest.java` | [ ] | | |
| `HR-TC-003` | `StaffProfileAggregatorTest.java` | [ ] | | |
| `HR-TC-SEC-001`| `StaffProfileAggregatorTest.java` | [ ] | | |
| `HR-TC-SEC-002`| `StaffProfileControllerTest.java` | [ ] | | |
| `HR-TC-INT-001`| `StaffProfileIntegrationTest.java` | [ ] | | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `EDS_UC33_StaffProfileDetails.md` đã được review và approve.
- [x] Logic rẽ nhánh Role & Data Minimization đã thống nhất.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn clean test` — tất cả Unit Tests xanh.
- [ ] `mvn verify` — tất cả Integration Tests xanh.
- [ ] Test coverage >= 80% cho `StaffProfileAggregator` và `StaffProfileController`.
- [ ] `password_hash` không xuất hiện trong bất kỳ DTO nào.

---

## 7. Rollback Plan

### Revert mã nguồn (Dev only)
```bash
git checkout -- src/test/java/com/harmony/hr/
git checkout -- src/main/java/com/harmony/hr/
```

### Gap vẫn OPEN
- Giữ nguyên trạng thái `In Progress` trên Jira. Không merge Pull Request.
