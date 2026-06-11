# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

**Document ID:** FPT-EDU-TDD-UC14-001  
**Version:** 1.0  
**Date:** 2026-06-11  
**Status:** Draft  
**Author:** Antigravity AI  

## MỤC LỤC
1. Thông tin Module
2. Test Design Specification (TDS)
3. Test Case Specification
4. Entry / Exit Criteria

## 1. Thông tin Module
| Field | Value |
|---|---|
| Feature / Gap ID | UC14 |
| Module | Session Status Management |
| Spec gốc | SPA-UC14-IMP-001 |
| Priority | 🔴 P0 |

## 2. Test Design Specification (TDS)
### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Update trạng thái hợp lệ | `PATCH /schedules/:id/status` | `UC14-TC-001` |
| TC-COND-002 | Chặn update ca làm việc của người khác | `PATCH /schedules/:id/status` | `UC14-TC-002` |
| TC-COND-003 | Chặn đổi trạng thái nếu đã hoàn thành | `PATCH /schedules/:id/status` | `UC14-TC-003` |

## 3. Test Case Specification

### UC14-TC-001 — Cập nhật trạng thái Session
**Severity:** HIGH  
**Feature Under Test:** `SessionController.updateStatus`  
**TDD Phase:** 🔴 RED  

**Test Steps:**
1. Gọi API `PATCH /api/v1/schedules/{id}/status` với body `{"status": "COMPLETED"}` dùng Token của Therapist phụ trách.
**Expected Result (PASS):** HTTP 200, DB được cập nhật.

### UC14-TC-002 — Phân quyền cập nhật trạng thái
**Severity:** HIGH  
**Feature Under Test:** `SessionController.updateStatus` Security  
**TDD Phase:** 🔴 RED  

**Test Steps:**
1. Gọi API cập nhật trạng thái dùng Token của Therapist X cho lịch trình của Therapist Y.
**Expected Result (PASS):** HTTP 403.

### UC14-TC-003 — State Machine Invariant
**Severity:** MEDIUM  
**Feature Under Test:** `SessionController.updateStatus` Logic  
**TDD Phase:** 🔴 RED  

**Test Steps:**
1. Lấy một ca đã có status `COMPLETED`.
2. Gửi request đổi thành `NO_SHOW`.
**Expected Result (PASS):** HTTP 400 Invalid transition.

## 4. Entry / Exit Criteria
- [ ] State Machine logic xử lý đúng
