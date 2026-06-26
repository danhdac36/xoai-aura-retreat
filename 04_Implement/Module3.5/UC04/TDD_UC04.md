# TEST-DRIVEN DEVELOPMENT SPECIFICATION
# Mẫu Đặc tả Kiểm thử Hướng Phát triển: UC04 - Quản lý danh mục lớp và lịch học (Quản lý)

**Document ID:** `HOS-YOGA-TDD-004`  
**Version:** 1.0  
**Date:** 2026-06-26  
**Status:** Draft  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** Lê Đức Dương - Backend Developer  
**Reviewed by:** Tech Lead  
**Approved by:** Principal Architect  
**Classification:** Internal - Confidential  

**References:**
* `01_Requirements/SRS_Document.md` (UC_YOGA_04)
* `04_Implement/Module3.5/UC04/EDS_UC04.md` (HOS-YOGA-IMP-004)

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test (`*Test.java`) -> chạy -> xác nhận FAIL 🔴 -> implement -> PASS 🟢 -> refactor 🔵.

---

## CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-26 | SE2023-G6 / AI | Khởi tạo tài liệu - TDD spec cho UC04 |

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
| **Feature / Gap ID** | UC04 - Manage Classes & Schedules (Manager) |
| **Module** | Yoga & Mindfulness Scheduling Engine |
| **Spec gốc** | `HOS-YOGA-IMP-004` |
| **Priority** | 🟡 P2 |
| **Data Classification** | Internal (Dữ liệu vận hành nội bộ) |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | `auth` Module (Quản lý User/Roles) |
| **Downstream Consumers** | Giao diện Quản lý Yoga |

---

## 2. Logic Issues Resolved
> Bắt buộc điền trước khi viết test.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | Xóa lịch học trực tiếp ra khỏi DB. | Chính sách bảo toàn dữ liệu (Soft Delete): Chỉ cập nhật trường `is_delete = true`. | Viết test kiểm tra xem sau khi gọi hàm xóa lịch học, trường `isDelete` của `YogaSchedule` được cập nhật thành `true` thay vì bản ghi bị biến mất khỏi DB. |
| L2 | Cho phép xóa lịch học bất cứ lúc nào. | Tính toàn vẹn dữ liệu: Không thể xóa lịch học nếu đã có học viên đăng ký tham gia lớp đó (`count_registered > 0`). | Viết test kiểm tra xem hệ thống có ném ra lỗi `YOGA-012` khi xóa lịch học đã có người đăng ký hay không. |
| L3 | Xếp lịch học không cần khớp với thời lượng lớp. | Ràng buộc nghiệp vụ: Thời gian bắt đầu và kết thúc của một ca học phải khớp đúng với trường `durationMinutes` định nghĩa trong lớp học đó. | Viết test đảm bảo ném lỗi `YOGA-011` nếu khoảng cách giữa `startTime` và `endTime` trong yêu cầu xếp lịch không trùng khớp với thời lượng của lớp học chỉ định. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
```
Yoga Management Module bao gồm các layer:
├── Service Interface (IYogaManagerService)
├── Service Implementation (YogaManagerServiceImpl - xử lý ràng buộc thời gian, xung đột giáo viên và địa điểm)
└── Controller (YogaManagerController - xử lý endpoints quản lý và xác thực JWT với vai trò MANAGER/ADMIN)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| `SRS.md` UC_YOGA_04 | Quản lý danh mục lớp và lịch học hàng tuần |
| `BR-YOGA-05` | Cấm xóa lịch học khi đã có người đăng ký |
| `BR-YOGA-06` | Trùng lịch đứng lớp của giáo viên |
| `BR-YOGA-07` | Trùng phòng học / địa điểm |
| `BR-YOGA-08` | Ràng buộc thời gian bằng thời lượng lớp |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-012 | Happy path: Tạo lớp học Yoga mới thành công | `IYogaManagerService.createClass` | `YOGA-TC-020` |
| TC-COND-013 | Lỗi dữ liệu đầu vào khi tạo lớp học (tên rỗng, duration nhỏ hơn 15) | `IYogaManagerService.createClass` | `YOGA-TC-021` |
| TC-COND-014 | Soft-delete lớp học thành công | `IYogaManagerService.deleteClass` | `YOGA-TC-022` |
| TC-COND-015 | Happy path: Tạo lịch học Yoga mới thành công | `IYogaManagerService.createSchedule` | `YOGA-TC-023` |
| TC-COND-016 | Tạo lịch học thất bại do lớp học không tồn tại | `IYogaManagerService.createSchedule` | `YOGA-TC-024` |
| TC-COND-017 | Tạo lịch học thất bại do giáo viên không tồn tại hoặc không ACTIVE | `IYogaManagerService.createSchedule` | `YOGA-TC-025` |
| TC-COND-018 | Tạo lịch học thất bại do thời gian học không khớp thời lượng lớp học | `IYogaManagerService.createSchedule` | `YOGA-TC-026` |
| TC-COND-019 | Tạo lịch học thất bại do trùng lịch của giáo viên | `IYogaManagerService.createSchedule` | `YOGA-TC-027` |
| TC-COND-020 | Tạo lịch học thất bại do trùng phòng học / địa điểm | `IYogaManagerService.createSchedule` | `YOGA-TC-028` |
| TC-COND-021 | Xóa lịch học thành công khi chưa có ai đăng ký | `IYogaManagerService.deleteSchedule` | `YOGA-TC-029` |
| TC-COND-022 | Xóa lịch học thất bại khi đã có học viên đăng ký | `IYogaManagerService.deleteSchedule` | `YOGA-TC-030` |

---

## 4. Test Case Specification

### YOGA-TC-020 — Tạo lớp học Yoga mới thành công (Happy Path)
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.createClass()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Dữ liệu yêu cầu hợp lệ: tên lớp không trống, thời lượng hợp lệ (>= 15).

**Test Steps:**
1. Gọi `yogaManagerService.createClass(request)`.

**Expected Result (PASS):**
1. Lớp học mới được lưu trong DB, trả về thông tin lớp kèm `classId` tự tăng, `isDelete = false`.

---

### YOGA-TC-021 — Tạo lớp học thất bại do dữ liệu đầu vào không hợp lệ
* **Severity:** MEDIUM
* **Feature Under Test:** `IYogaManagerService.createClass()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Dữ liệu request không hợp lệ (Ví dụ: `className = ""`, hoặc `durationMinutes = 10`).

**Test Steps:**
1. Thực hiện gọi API `POST /manager/yoga/classes` với request body lỗi.

**Expected Result (PASS):**
1. Hệ thống trả về `400 Bad Request` cùng với chi tiết lỗi xác thực dữ liệu đầu vào.

---

### YOGA-TC-022 — Soft-delete lớp học thành công
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.deleteClass()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Có lớp học với ID chỉ định tồn tại trong DB.

**Test Steps:**
1. Gọi `yogaManagerService.deleteClass(classId)`.

**Expected Result (PASS):**
1. Bản ghi lớp học đó không bị xóa vật lý, nhưng trường `isDelete` được cập nhật thành `true`.

---

### YOGA-TC-023 — Tạo lịch học Yoga mới thành công (Happy Path)
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Lớp học tồn tại, Giáo viên tồn tại & sẵn sàng.
* Thời gian bắt đầu ở tương lai.
* Khoảng cách thời gian bắt đầu và kết thúc bằng thời lượng lớp học.
* Giáo viên và phòng học không bị trùng lịch nào khác.

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request)`.

**Expected Result (PASS):**
1. Bản ghi lịch học được thêm mới vào bảng `YOGA_SCHEDULE` với trạng thái `isDelete = false`.

---

### YOGA-TC-024 — Tạo lịch học thất bại do lớp học không tồn tại
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Request truyền vào `classId = 9999` không tồn tại trong DB.

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request)`.

**Expected Result (PASS):**
1. Ném ra ngoại lệ với mã lỗi `YOGA-009` (Không tìm thấy lớp học).

---

### YOGA-TC-025 — Tạo lịch học thất bại do giáo viên không tồn tại hoặc không ACTIVE
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Giáo viên chỉ định có trạng thái không khả dụng (status != 'AVAILABLE').

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request)`.

**Expected Result (PASS):**
1. Ném ra ngoại lệ với mã lỗi `YOGA-010` (Giáo viên không tồn tại hoặc không sẵn sàng).

---

### YOGA-TC-026 — Tạo lịch học thất bại do thời gian học không khớp thời lượng lớp học
* **Severity:** MEDIUM
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Lớp học có `durationMinutes = 60`.
* Request xếp lịch có `startTime` lúc 08:00 và `endTime` lúc 09:30 (khoảng cách 90 phút).

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request)`.

**Expected Result (PASS):**
1. Ném ra ngoại lệ với mã lỗi `YOGA-011` (Logic thời gian học không hợp lệ).

---

### YOGA-TC-027 — Tạo lịch học thất bại do trùng lịch của giáo viên (Double Booking)
* **Severity:** CRITICAL
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Giáo viên A đã có lịch dạy từ 08:00 - 09:00 tại Phòng Yoga 1.
* Manager tạo lịch mới gán cho Giáo viên A dạy lúc 08:30 - 09:30 ở Phòng Yoga 2.

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request_moi)`.

**Expected Result (PASS):**
1. Hệ thống phát hiện xung đột thời gian và ném lỗi `YOGA-013` (Huấn luyện viên bị trùng lịch).

---

### YOGA-TC-028 — Tạo lịch học thất bại do trùng phòng học / địa điểm
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.createSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Tại địa điểm "Phòng Yoga Aura 1" đã xếp lịch học từ 08:00 - 09:00.
* Manager tạo lịch học mới tại đúng địa điểm này từ 08:45 - 09:45.

**Test Steps:**
1. Gọi `yogaManagerService.createSchedule(request_moi)`.

**Expected Result (PASS):**
1. Hệ thống phát hiện xung đột và ném lỗi `YOGA-014` (Địa điểm bị trùng lịch sử dụng).

---

### YOGA-TC-029 — Xóa lịch học thành công khi chưa có ai đăng ký
* **Severity:** HIGH
* **Feature Under Test:** `IYogaManagerService.deleteSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Lịch học tồn tại và chưa có bất kỳ học viên nào đăng ký tham gia (`count_registered = 0`).

**Test Steps:**
1. Gọi `yogaManagerService.deleteSchedule(scheduleId)`.

**Expected Result (PASS):**
1. Bản ghi được cập nhật trường `isDelete = true`.

---

### YOGA-TC-030 — Xóa lịch học thất bại khi đã có học viên đăng ký
* **Severity:** CRITICAL
* **Feature Under Test:** `IYogaManagerService.deleteSchedule()`
* **TDD Phase:** 🔴 RED

**Preconditions:**
* Lịch học đã có 2 khách hàng đặt lịch thành công (`count_registered = 2`).

**Test Steps:**
1. Gọi `yogaManagerService.deleteSchedule(scheduleId)`.

**Expected Result (PASS):**
1. Hệ thống chặn thao tác và ném ra lỗi `YOGA-012` (Không thể xóa lịch đã có học viên).

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `YOGA-TC-020` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-021` | `YogaManagerControllerTest.java` | [ ] | [ ] | |
| `YOGA-TC-022` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-023` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-024` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-025` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-026` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-027` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-028` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-029` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |
| `YOGA-TC-030` | `YogaManagerServiceImplTest.java` | [ ] | [ ] | |

---

## 6. Entry / Exit Criteria

### Entry Criteria
- [x] Tài liệu Đặc tả Kỹ thuật `EDS_UC04.md` đã được hoàn thành.

### Exit Criteria (DoD)
- [ ] Tất cả các bài kiểm thử unit tests `YOGA-TC-020` đến `YOGA-TC-030` đều vượt qua thành công (màu xanh lá 🟢).
- [ ] Test coverage của code dịch vụ quản lý đạt từ 80% trở lên.

---

## 7. Rollback Plan

```bash
# Revert file code dev
git checkout -- src/main/java/com/AuraMoon/auramoon/yoga/
git checkout -- src/test/java/com/AuraMoon/auramoon/yoga/
```
