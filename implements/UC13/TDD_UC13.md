# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

**Document ID:** FPT-EDU-TDD-UC13-001  
**Version:** 1.0  
**Date:** 2026-06-11  
**Status:** Draft  
**Author:** Antigravity AI  

## MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria

## 1. Thông tin Module
| Field | Value |
|---|---|
| Feature / Gap ID | UC13 |
| Module | Therapist Schedule Viewing |
| Spec gốc | SPA-UC13-IMP-001 |
| Priority | 🔴 P0 |

## 3. Test Design Specification (TDS)
### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Lấy danh sách ca làm việc | `GET /schedules/daily` | `UC13-TC-001` |
| TC-COND-002 | Trả về Health Notes chuẩn quyền | `GET /schedules/daily` | `UC13-TC-002` |

## 4. Test Case Specification

### UC13-TC-001 — Lấy danh sách lịch trình hàng ngày
**Severity:** HIGH  
**Feature Under Test:** `ScheduleController.getDailySchedule`  
**TDD Phase:** 🔴 RED  

**Test Steps:**
1. Tạo 2 ca trị liệu trong ngày cho Therapist A, 1 ca cho Therapist B.
2. Gọi API `GET /api/v1/schedules/daily` với Token của Therapist A.
3. Assert kết quả trả về đúng 2 ca của Therapist A.

### UC13-TC-002 — Bảo mật PII Health Notes
**Severity:** CRITICAL  
**Feature Under Test:** `ScheduleController.getDailySchedule` Security  
**TDD Phase:** 🔴 RED  

**Test Steps:**
1. Gọi API `GET /api/v1/schedules/daily`.
2. Kiểm tra Payload trả về xem có lộ Health Notes của các Booking không thuộc về chuyên viên đó không.
**Expected Result (PASS):** Chỉ thấy health notes của ca mình được gán.

## 6. Entry / Exit Criteria
- [ ] Tất cả tests pass
- [ ] PII được đảm bảo an toàn
