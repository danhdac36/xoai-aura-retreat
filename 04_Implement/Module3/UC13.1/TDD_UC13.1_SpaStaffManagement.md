# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: FPT-EDU-TDD-SPA-131
- **Version**: 1.0
- **Date**: 2026-06-21
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Lê Đức Dương
- **Reviewed by**: [x] Tech Lead — Approved
- **DPO Sign-off**: [x] Approved
- **Approved by**: Lê Đức Dương
- **Classification**: Internal

### References:
- `04_Implement/Module3/FRS_Module3_SpaScheduling.md`
- `EDS_UC13.1_SpaStaffManagement.md`

---

## CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-21 | Lê Đức Dương | Khởi tạo tài liệu — TDD spec cho UC13.1 Spa Staff Management |
| 2026-06-21 | Lê Đức Dương | Phê duyệt tài liệu TDD |

---

## MỤC LỤC
1. [Thông tin Module](#1-thông-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)

---

## 1. Thông tin Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | UC13.1 |
| **Module** | Spa & Therapy Scheduling Engine |
| **Spec gốc** | EDS_UC13.1_SpaStaffManagement.md |
| **Priority** | 🟠 P1 |
| **Milestone** | M3 |
| **Data Classification** | Internal / PII |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

Module Spa Staff Management bao gồm:
- Domain/Entity: `Therapist`
- Repository: `TherapistRepository` (truy vấn danh sách kèm `fullName`)
- Service: `SpaManagerService` (xử lý logic lấy danh sách, cập nhật trạng thái)
- Controller: `SpaManagerController` (xử lý HTTP request từ Manager)

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Danh sách Therapist hiển thị đúng | `SpaManagerService.getAllTherapistsWithDetails` | `SPA-131-TC-001` |
| TC-COND-002 | Cập nhật trạng thái thành công | `SpaManagerService.updateTherapistStatus` | `SPA-131-TC-002` |
| TC-COND-003 | Báo lỗi khi cập nhật trạng thái không hợp lệ | `SpaManagerService.updateTherapistStatus` | `SPA-131-TC-003` |
| TC-COND-004 | Manager được phép truy cập | `SpaManagerController` auth guards | `SPA-131-TC-004` |
| TC-COND-005 | Guest/Therapist bị cấm truy cập | `SpaManagerController` auth guards | `SPA-131-TC-SEC-001` |

---

## 4. Test Case Specification

### `SPA-131-TC-001` — Lấy danh sách Therapist
- **Severity**: HIGH
- **Feature Under Test**: `SpaManagerService.getAllTherapistsWithDetails()`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Có sẵn 2 Therapist trong DB (T1: Available, T2: Busy).
- T1 có 2 session trong ngày hôm nay.

#### Test Steps:
1. Gọi hàm `getAllTherapistsWithDetails()`.
2. Kiểm tra kết quả trả về.

#### Expected Result (PASS):
- Trả về list 2 object.
- Object T1 có status `Available`, `todaySessionCount = 2`.
- Object T2 có status `Busy`, `todaySessionCount = 0`.

---

### `SPA-131-TC-002` — Cập nhật trạng thái Therapist thành công
- **Severity**: HIGH
- **Feature Under Test**: `SpaManagerService.updateTherapistStatus()`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Therapist `TH001` đang ở trạng thái `Available`.

#### Test Steps:
1. Gọi `updateTherapistStatus("TH001", "OFF_DUTY", 1)`.
2. Truy vấn lại Therapist `TH001`.

#### Expected Result (PASS):
- Trạng thái cập nhật thành `OFF_DUTY`.
- `AuditLogService.log` được gọi 1 lần với action `SPA_STATUS_UPDATED`.

---

### `SPA-131-TC-003` — Cập nhật trạng thái không hợp lệ
- **Severity**: MEDIUM
- **Feature Under Test**: `SpaManagerService.updateTherapistStatus()`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Therapist `TH001` tồn tại.

#### Test Steps:
1. Gọi `updateTherapistStatus("TH001", "InvalidStatus", 1)`.

#### Expected Result (PASS):
- Ném ra ngoại lệ `SpaBusinessException`.
- Báo lỗi `SPA-131-01` (Trạng thái không hợp lệ).
- Database không thay đổi.

---

## SECURITY TEST CASES

### `SPA-131-TC-SEC-001` — RBAC Access Control
- **Severity**: CRITICAL
- **OWASP**: A01:2021-Broken Access Control
- **Feature Under Test**: `SpaManagerController` endpoints
- **TDD Phase**: 🔴 RED

#### Preconditions:
- Môi trường Spring Security được mock.

#### Test Steps:
1. Mock user đăng nhập với role `GUEST` hoặc `THERAPIST`.
2. Thực hiện HTTP GET `/manager/spa/therapists`.
3. Thực hiện HTTP POST `/manager/spa/therapists/status`.

#### Expected Result (PASS):
- HTTP Response trả về `403 Forbidden` cho mọi request.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `SPA-131-TC-001` | `SpaManagerServiceTest.java` | [ ] | | |
| `SPA-131-TC-002` | `SpaManagerServiceTest.java` | [ ] | | |
| `SPA-131-TC-003` | `SpaManagerServiceTest.java` | [ ] | | |
| `SPA-131-TC-SEC-001` | `SpaManagerControllerTest.java` | [ ] | | |

---

## 6. Entry / Exit Criteria

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] Tất cả unit tests trong tài liệu này pass (`GREEN`).
- [ ] Giao diện `manager-therapists.html` hiển thị chính xác trạng thái và form đổi trạng thái.
- [ ] Các logic authorization (`@PreAuthorize`) được áp dụng đúng.
