# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC30 Audit Log

- **Document ID**: `XOAI-MOD5-TDD-030`
- **Version**: 1.0
- **Date**: 2026-06-26
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [ ] Pending
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential / Sensitive-PII

### References:
- `04_testing/SOFTWARE_TEST_PLAN.md` (FPT-EDU-STP-001 v2.0) — Master Test Plan
- `01_Requirements/SRS.md` — Functional requirements (UC30)
- `04_Implement/Module5/UC30/EDS_UC30_AuditLog.md` — Technical Specification
- `02_Design/ADR/ADR-001` — [Chiến lược Append-only tuyệt đối cho Audit Log]
- `Luật PDPA / PCI-DSS` — Bảo mật và lưu vết tài chính.

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`@Test`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Không mark test là ✅ nếu `mvn test` chưa xanh.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-26 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC30 Audit Log |

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
| **Feature / Gap ID** | GAP-006 |
| **Module** | System Audit Log (Module 5) |
| **Spec gốc** | `XOAI-MOD5-IMP-030` |
| **Priority** | 🔴 P0 (Bắt buộc cho Compliance) |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Sensitive-PII / Confidential |
| **Compliance Scope** | PCI-DSS (Lưu vết tài chính), GDPR Art. 5.1(f) |
| **Upstream Dependencies** | Mọi Event Publisher trong hệ thống |
| **Downstream Consumers** | Admin Dashboard UI |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Kế hoạch lưu trữ là 5 năm (NFR 4.2). | Đã thỏa thuận giảm xuống **6 tháng** để tối ưu DB. | Test case không ảnh hưởng, nhưng policy Retention phải tuân thủ 6 tháng. |
| L2 | Bảng Audit Log có thể bị xóa bằng `deleteAll()`. | Áp dụng ADR-001 (Append-only). | Phải có Unit Test Reflection kiểm tra không tồn tại các method `delete` trong Repository. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`System Audit Log` bao gồm các layer:
- Domain (Entity `AuditLog`)
- Repository (`IAuditLogRepository` với H2 Database)
- Services (`AuditLogService` xử lý lưu vết và truy vấn)
- Controller (`AuditLogController` cung cấp UI và API chi tiết)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-30 | Hệ thống lưu vết phải truy xuất được bởi Admin. |
| ADR-001 | Append-only: Cấm mọi thao tác Delete/Update trên code base. |
| ADR-002 | UI Modal cho Details API (`GET /admin/audit/details/{id}`) trả về JSON. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Tính bất biến của Repository (Append-only) | `IAuditLogRepository` Methods | `MOD5-AUD-TC-001` |
| TC-COND-002 | Dashboard truy vấn thành công | `AuditLogController.dashboard()` | `MOD5-AUD-TC-002` |
| TC-COND-003 | API Details trả về đúng JSON | `AuditLogController.getLogDetails()` | `MOD5-AUD-TC-003` |
| TC-COND-004 | Admin role enforcement | Lớp Security config | `MOD5-AUD-TC-004` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | Role (`ADMIN` vs `MANAGER`) | Chỉ Admin mới được xem Audit Log. |
| Boundary Value Analysis | Phân trang (Pagination) | Test query với trang không tồn tại. |
| Error Guessing | Reflection API | Đoán các hàm xóa được kế thừa từ JpaRepository. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | `AuditLog(action="PAYMENT", actor=1)` | Data hợp lệ cho Dashboard |
| FX-002 | DB seed | `AuditLog(details="{...}")` | Chuỗi JSON để test API Modal |
| FX-003 | Auth Mock | `@WithMockUser(roles = "ADMIN")` | Test truy cập của Admin |
| FX-004 | Auth Mock | `@WithMockUser(roles = "MANAGER")` | Test truy cập trái phép |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-AUD-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-AUD-TC-001` — Đảm bảo Repository tuân thủ Append-Only (Không có hàm Delete)
- **Severity**: CRITICAL
- **Feature Under Test**: `IAuditLogRepository`
- **Test File**: `src/test/java/com/xoai/aura/repository/AuditLogRepositoryTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- Biên dịch thành công Interface `IAuditLogRepository`.

#### Test Steps:
1. Dùng Java Reflection quét tất cả các phương thức của `IAuditLogRepository`.
2. Kiểm tra xem có phương thức nào chứa tên `delete`, `remove`, `update` hay không.

#### Expected Result (PASS):
- Test xác nhận KHÔNG tồn tại bất kỳ method nào như `deleteById`, `deleteAll`. (Dev phải cẩn thận chỉ implements `Repository` hoặc chặn các phương thức này nếu extends `JpaRepository`).

#### Expected Result (FAIL):
- Tồn tại method `delete()` (Vi phạm Compliance PCI-DSS).

- **Current Status**: 🔴 Not written
- **Implementation Note**: Tốt nhất `IAuditLogRepository` chỉ extends `Repository<AuditLog, Integer>` và khai báo tay `save()` và `findAll()`, thay vì extends `JpaRepository` (vì nó bao gồm cả đống hàm delete).

---

### `MOD5-AUD-TC-002` — Dashboard render HTML thành công cho Admin
- **Severity**: HIGH
- **Feature Under Test**: `AuditLogController.dashboard()`
- **Test File**: `src/test/java/com/xoai/aura/controller/AuditLogControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- (FX-003) Auth Mock Role = `ADMIN`.

#### Test Steps:
1. Mock `auditLogService.getLogs(...)`.
2. Dùng `MockMvc` perform `GET /admin/audit`.
3. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"admin/audit"`.
- Model chứa attribute `"logs"`.

#### Expected Result (FAIL):
- Gặp lỗi template parse hoặc HTTP 500.

- **Current Status**: 🔴 Not written

---

### `MOD5-AUD-TC-003` — API Lấy chi tiết Log (Modal Popup) trả về JSON
- **Severity**: HIGH
- **Feature Under Test**: `AuditLogController.getLogDetails()`
- **Test File**: `src/test/java/com/xoai/aura/controller/AuditLogControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- (FX-003) Auth Mock Role = `ADMIN`.
- (FX-002) Dữ liệu Details là chuỗi `{"amount": 100}`.

#### Test Steps:
1. Mock `auditLogService.getDetailsById(1)` trả về `{"amount": 100}`.
2. Dùng `MockMvc` perform `GET /admin/audit/details/1`.
3. Kiểm tra HTTP Status và Body.

#### Expected Result (PASS):
- Status: `200 OK`.
- Content-Type: `application/json`.
- Response Body: `{"amount": 100}`.

#### Expected Result (FAIL):
- Trả về nguyên trang HTML (lỗi không gắn `@ResponseBody`).

- **Current Status**: 🔴 Not written
- **Implementation Note**: Endpoint này phải trả về dữ liệu thô (JSON) chứ không phải Thymeleaf View.

---

## SECURITY TEST CASES

### `MOD5-AUD-TC-004` — Ngăn chặn Manager và người dùng khác truy cập Audit Log
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: Vi phạm bảo mật thông tin nội bộ.
- **Feature Under Test**: Spring Security Rules cho `/admin/audit/**`
- **Test File**: `src/test/java/com/xoai/aura/controller/AuditLogSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-004) Auth Mock Role = `MANAGER`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Manager.
2. Thực hiện `GET /admin/audit` và `GET /admin/audit/details/1`.
3. Xác minh quyền truy cập bị chặn.

#### Expected Result (PASS = hệ thống an toàn):
- Cả hai request đều trả về `403 Forbidden`.

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Manager nhìn thấy Audit Log của toàn hệ thống (Rò rỉ PII diện rộng).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** - Các Test Database H2 và WebMvcTest đã đảm bảo bao phủ luồng MVC và logic Append-only.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-AUD-TC-001` | `AuditLogRepositoryTest.java` | [ ] | `[hash]` | |
| `MOD5-AUD-TC-002` | `AuditLogControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-AUD-TC-003` | `AuditLogControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-AUD-TC-004` | `AuditLogSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `XOAI-MOD5-IMP-030` đã được review và approve.
- [x] Bảng `AUDIT_LOG` được thiết lập không có Cascade Delete từ các bảng khác.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines.
- [ ] Khẳng định 100% không thể gọi lệnh DELETE vào bảng `AUDIT_LOG` qua ORM.

### Suspension Criteria (Điều kiện tạm dừng)
- Lỗi thiết kế Base Entity khiến `AuditLog` vô tình bị di truyền cờ `is_delete`.

---

## 7. Rollback Plan

### Revert Database (dev only)
- Xóa bỏ bảng `AUDIT_LOG` và các bảng liên quan.

### Revert implementation files
```bash
git checkout -- src/main/java/com/xoai/aura/controller/AuditLogController.java
git checkout -- src/main/java/com/xoai/aura/service/AuditLogService.java
git checkout -- src/main/java/com/xoai/aura/repository/IAuditLogRepository.java
```

### Gap vẫn OPEN
- Báo cáo với Security Team về tiến độ tích hợp Log.
