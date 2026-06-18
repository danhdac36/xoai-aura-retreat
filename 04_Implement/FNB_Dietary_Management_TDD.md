# TEST-DRIVEN DEVELOPMENT SPECIFICATION

## Đặc tả Kiểm thử Hướng Phát triển - Module 4: Dietary F&B Management

- **Document ID**: `HOS-FNB-TDD-001`
- **Version**: 1.0
- **Date**: 2026-06-14
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: DacHD
- **Reviewed by**: HaiPG
- **DPO Sign-off**: [x] Approved — 2026-06-14 — Hoang Danh Dac (DPO Officer)
- **Approved by**: DacHD
- **Classification**: Internal — Confidential

### References:

- `04_testing/SOFTWARE_TEST_PLAN.md` (FPT-EDU-STP-001 v2.0) — Master Test Plan
- [SWP391-HOS-03.md](file:///d:/SWP301/su26-swp391-se2023-g6/08_Document_References/Template/SWP391-HOS-03.md) — Module 4 Use Cases
- [FNB_Dietary_Management_EDS.md](file:///d:/SWP301/su26-swp391-se2023-g6/04_Implement/FNB_Dietary_Management_EDS.md) — Đặc tả kỹ thuật thực thể F&B
- [DB.sql](file:///d:/SWP301/su26-swp391-se2023-g6/03_Design/SQL/DB.sql) — Cấu trúc cơ sở dữ liệu
- [UnitTest.md](file:///d:/SWP301/su26-swp391-se2023-g6/00_Policy/UnitTest.md) — Chính sách kiểm thử dự án
- [Rule.md](file:///d:/SWP301/su26-swp391-se2023-g6/00_Policy/Rule.md) — Quy tắc phát triển Spring Boot MVC

> [!NOTE]
> **Quy ước TDD (Test-Driven Development)**:
> Tài liệu này đặc tả các ca kiểm thử (Test Cases) trước khi viết mã nguồn ứng dụng.
> Tất cả các kiểm thử trong tài liệu này hiện ở trạng thái **PENDING** và sẽ chuyển dịch qua các pha **RED** (Viết test và chạy thất bại) -> **GREEN** (Viết code logic tối thiểu để test pass) -> **REFACTOR** (Tối ưu mã nguồn).
> Quy ước đặt tên hàm test: `methodName_condition_expectedBehavior` (JUnit 5 convention).

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ. Mọi thay đổi phải ghi vào bảng này.

| Ngày      | Người thực hiện | Nội dung thay đổi                                        |
| :--------- | :------------------ | :---------------------------------------------------------- |
| 2026-06-14 | DacHD               | Khởi tạo tài liệu đặc tả kiểm thử TDD cho Module 4 |

---

## MỤC LỤC

1. [Thông tin Module](#1-thông-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Security Test Cases](#5-security-test-cases)
6. [Integration Test Cases](#6-integration-test-cases)
7. [Red-Green-Refactor Tracker](#7-red-green-refactor-tracker)
8. [Entry / Exit Criteria](#8-entry--exit-criteria)
9. [Rollback Plan](#9-rollback-plan)

---

## 1. Thông tin Module

| Field                           | Value                                                                                                            |
| :------------------------------ | :--------------------------------------------------------------------------------------------------------------- |
| **Feature / Gap ID**      | FNB-CORE-02                                                                                                      |
| **Module**                | Dietary F&B Management (com.xoai.retreat.fnb)                                                                    |
| **Spec gốc**             | [FNB_Dietary_Management_EDS.md](file:///d:/SWP301/su26-swp391-se2023-g6/04_Implement/FNB_Dietary_Management_EDS.md) |
| **Priority**              | 🔴 P0 (Core Module)                                                                                              |
| **Sprint**                | Sprint 2 (2026-06-14 -> 2026-06-28)                                                                              |
| **Milestone**             | Alpha Review — 2026-06-28                                                                                       |
| **Data Classification**   | Sensitive-PII (Dietary Profile, Food Allergies)                                                                  |
| **Compliance Scope**      | Nghị định 356/2025/NĐ-CP (Luật Bảo vệ dữ liệu cá nhân Việt Nam)                                      |
| **Upstream Dependencies** | `auth` (User), `booking` (Booking)                                                                           |
| **Downstream Consumers**  | `billing` (GuestFolio)                                                                                         |

---

## 2. Logic Issues Resolved

| #  | Spec gốc (sai / thiếu)                                     | Thực tế (schema / policy)                                                                                    | Fix áp dụng trong test                                                              |
| :- | :----------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------ |
| L1 | Đặt món không kiểm tra trạng thái Check-in            | Bảng `BOOKING` chứa trạng thái phòng. Khách chỉ được đặt món khi đã checked-in.               | Thêm test kiểm tra trạng thái Booking trước khi cho phép đặt món.           |
| L2 | Đầu bếp có thể truy cập toàn bộ hồ sơ khách hàng | Chính sách tối thiểu hóa dữ liệu (Decree 356/2025): Đầu bếp không được thấy bệnh lý vật lý. | Tách biệt kiểm thử quyền truy cập dữ liệu giữa Đầu bếp và Spa Therapist. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

Kiểm thử bao phủ toàn bộ các thành phần thuộc package `com.xoai.retreat.fnb`:

* **Controller Layer**: Validate dữ liệu đầu vào DTO, mã HTTP trả về, kiểm soát Token JWT.
* **Service Layer**: Logic nghiệp vụ, bộ lọc dị ứng thực đơn, tích hợp ghi nợ Folio.
* **Repository Layer**: So khớp thực thể DB, mã hóa/giải mã ứng dụng AES-256-GCM.

### TDS-02 — Test Basis / Cơ sở Kiểm thử

* [SWP391-HOS-03.md](file:///d:/SWP301/su26-swp391-se2023-g6/08_Document_References/Template/SWP391-HOS-03.md) §Module 4.
* [FNB_Dietary_Management_EDS.md](file:///d:/SWP301/su26-swp391-se2023-g6/04_Implement/FNB_Dietary_Management_EDS.md).
* Nghị định 356/2025/NĐ-CP Điều 4 (Dữ liệu cá nhân nhạy cảm) và Điều 32 (Biện pháp bảo vệ).

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                                     | Coverage Item                          | Test Cases                       |
| :----------- | :------------------------------------------------- | :------------------------------------- | :------------------------------- |
| COND-FNB-001 | Bộ lọc dị ứng thực đơn tự động           | `MealOrderService.getFilteredMenu()` | `FNB-TC-001`, `002`, `003` |
| COND-FNB-002 | Xem bảng chuẩn bị KDS của bếp                 | `ChefController.getDailyMealPrep()`  | `FNB-TC-004`, `005`, `006` |
| COND-FNB-003 | Cập nhật trạng thái đơn món ăn             | `MealOrder.updateStatus()`           | `FNB-TC-007`, `008`, `009` |
| COND-FNB-004 | Khách gọi đồ a-la-carte và ghi nợ Folio      | `MealOrderService.createMealOrder()` | `FNB-TC-010`, `011`, `012` |
| COND-FNB-005 | Đảm bảo tính tối thiểu hóa dữ liệu (RBAC) | `DietaryProfileRepository`           | `FNB-TC-013`, `014`, `015` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

* **Equivalence Partitioning**: Phân vùng hợp lệ/không hợp lệ đối với số lượng đặt món (`quantity` > 0).
* **State Transition Testing**: Kiểm thử sự thay đổi trạng thái đơn hàng (PENDING -> PREPARING -> READY).
* **Error Guessing / Security Scanning**: Giả lập hacker sử dụng quyền Therapist để đọc thông tin dị ứng của F&B và ngược lại.

### TDS-05 — Test Data Requirements (Fixture)

* `FX-GUEST-001`: Khách mẫu John Doe có dị ứng Đậu Phộng (`"Peanuts"`).
* `FX-BOOKING-ACTIVE`: Lượt booking trạng thái "Checked-in".
* `FX-MENU-ITEMS`: Danh sách 3 món: Súp gà (an toàn), Gỏi đu đủ đậu phộng (chứa dị ứng), và Trà đá (A-la-carte).
* `FX-JWT-GUEST`: Token JWT đại diện cho GUEST John Doe.
* `FX-JWT-CHEF`: Token JWT đại diện cho CHEF.
* `FX-JWT-THERAPIST`: Token JWT đại diện cho SPA_THERAPIST.

---

## 4. Test Case Specification

> [!IMPORTANT]
> **Test Status**: `PENDING` (Chờ viết mã nguồn để chạy pha RED).
> **Naming Rule**: `methodName_condition_expectedBehavior`

### UC16 — Chọn món dinh dưỡng có lọc dị ứng

#### `FNB-TC-001` — getFilteredMenu_guestHasDietaryProfile_returnsFilteredSafeMenu

- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.getFilteredMenu()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-001`
- **Preconditions**:
  - Seed dữ liệu khách John Doe có dị ứng Đậu Phộng (`"Peanuts"`).
  - Seed thực đơn món ăn.
- **Test Steps**:
  1. Arrange: Thiết lập mock UserRepository trả về John Doe, DietaryProfileRepository trả về dị ứng "Peanuts".
  2. Act: Gọi phương thức `getFilteredMenu(johnDoeId, activeBookingId)`.
  3. Assert: Kiểm tra danh sách trả về.
- **Expected Result (PASS)**: Danh sách món ăn trả về không chứa bất kỳ món nào có thành phần nguyên liệu `"Peanuts"`.
- **Expected Result (FAIL)**: Trả về món ăn có chứa thành phần dị ứng gây nguy hiểm sức khỏe cho khách.

#### `FNB-TC-002` — getFilteredMenu_invalidBookingId_throwsValidationException

- **Severity**: MEDIUM
- **Feature Under Test**: `MealOrderService.getFilteredMenu()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-001`
- **Preconditions**: Khách đăng nhập hợp lệ.
- **Test Steps**:
  1. Act: Gọi `getFilteredMenu(johnDoeId, -999)`.
  2. Assert: Đảm bảo ném ra ngoại lệ.
- **Expected Result (PASS)**: Ném ra ngoại lệ `IllegalArgumentException` hoặc `ValidationException` (HTTP 400).
- **Expected Result (FAIL)**: Hệ thống chạy tiếp mà không phát sinh lỗi hoặc gây ra lỗi Server-side NullPointerException.

#### `FNB-TC-003` — getFilteredMenu_bookingNotFound_throwsResourceNotFoundException

- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.getFilteredMenu()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-001`
- **Preconditions**: Booking ID không tồn tại trong DB.
- **Test Steps**:
  1. Act: Gọi `getFilteredMenu(johnDoeId, 99999)`.
  2. Assert: Kiểm tra ngoại lệ ném ra.
- **Expected Result (PASS)**: Ném ra `ResourceNotFoundException` (HTTP 404).
- **Expected Result (FAIL)**: Trả về mảng trống hoặc ném lỗi 500.

---

### UC17 — Xem bảng KDS chuẩn bị bữa ăn của bếp

#### `FNB-TC-004` — getDailyMealPrep_chefRole_returnsDailyMealPrepDashboard

- **Severity**: HIGH
- **Feature Under Test**: `ChefController.getDailyMealPrep()` / API `GET /api/v1/fnb/chef/dashboard`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-002`
- **Preconditions**: Đăng nhập bằng tài khoản Chef (`FX-JWT-CHEF`).
- **Test Steps**:
  1. Act: Gửi request `GET /api/v1/fnb/chef/dashboard` với header authorization hợp lệ.
  2. Assert: Kiểm tra cấu trúc JSON trả về.
- **Expected Result (PASS)**: HTTP 200 OK. Body chứa danh sách đơn hàng cần chế biến kèm cảnh báo dị ứng rõ ràng.
- **Expected Result (FAIL)**: Lỗi 403 Forbidden hoặc dữ liệu trả về không có cảnh báo dị ứng.

#### `FNB-TC-005` — getDailyMealPrep_invalidDateFormat_throwsValidationException

- **Severity**: LOW
- **Feature Under Test**: `ChefController.getDailyMealPrep()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-002`
- **Test Steps**:
  1. Act: Gửi request với định dạng ngày sai: `GET /api/v1/fnb/chef/dashboard?date=14-06-2026`.
- **Expected Result (PASS)**: Trả về HTTP 400 Bad Request.
- **Expected Result (FAIL)**: HTTP 200 với dữ liệu trống hoặc HTTP 500 lỗi phân tích cú pháp.

#### `FNB-TC-006` — getDailyMealPrep_noOrdersForDate_returnsEmptyDashboard

- **Severity**: MEDIUM
- **Feature Under Test**: `ChefController.getDailyMealPrep()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-002`
- **Test Steps**:
  1. Act: Gửi request lấy dữ liệu một ngày không có ai đặt phòng/món: `GET /api/v1/fnb/chef/dashboard?date=2027-01-01`.
- **Expected Result (PASS)**: Trả về HTTP 200 OK kèm danh sách đơn rỗng (`[]`).
- **Expected Result (FAIL)**: Ném lỗi ngoại lệ hệ thống hoặc trả về lỗi 404.

---

### UC18 — Cập nhật trạng thái đơn món ăn

#### `FNB-TC-007` — updatePrepStatus_validOrderAndChefRole_updatesStatusAndSaves

- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.updatePrepStatus()` / API `PATCH /api/v1/fnb/chef/orders/:id/status`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-003`
- **Preconditions**: Đơn hàng có ID 55 đang ở trạng thái `PENDING`.
- **Test Steps**:
  1. Act: Gọi `updatePrepStatus(55, "PREPARING")`.
  2. Assert: Truy vấn lại DB để đối chiếu trạng thái.
- **Expected Result (PASS)**: DB cập nhật trạng thái đơn thành `PREPARING` thành công.
- **Expected Result (FAIL)**: Trạng thái không đổi hoặc ném lỗi DB.

#### `FNB-TC-008` — updatePrepStatus_invalidStatusTransition_throwsValidationException

- **Severity**: HIGH
- **Feature Under Test**: `MealOrderService.updatePrepStatus()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-003`
- **Preconditions**: Đơn hàng có ID 55 đang ở trạng thái `READY`.
- **Test Steps**:
  1. Act: Cố gắng chuyển trạng thái ngược dòng: Gọi `updatePrepStatus(55, "PENDING")`.
- **Expected Result (PASS)**: Ném ra `IllegalStateException` (HTTP 400).
- **Expected Result (FAIL)**: Hệ thống cho phép đơn hàng lùi trạng thái về `PENDING`.

#### `FNB-TC-009` — updatePrepStatus_orderIdNotFound_throwsNotFoundException

- **Severity**: MEDIUM
- **Feature Under Test**: `MealOrderService.updatePrepStatus()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-003`
- **Test Steps**:
  1. Act: Cập nhật đơn hàng không tồn tại: Gọi `updatePrepStatus(9999, "PREPARING")`.
- **Expected Result (PASS)**: Trả về HTTP 404 Not Found.
- **Expected Result (FAIL)**: Trả về HTTP 200 hoặc ném ngoại lệ 500.

---

### UC19 — Khách gọi đồ a-la-carte tính phí vào Folio

#### `FNB-TC-010` — createAlacarteOrder_validAcarteSelection_savesOrderAndChargesFolio

- **Severity**: CRITICAL
- **Feature Under Test**: `MealOrderService.createMealOrder()` / API `POST /api/v1/fnb/meal-orders`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-004`
- **Preconditions**: Khách John Doe đang checked-in (`FX-BOOKING-ACTIVE`). Gọi món uống A-la-carte (Trà đá giá 15,000 VND).
- **Test Steps**:
  1. Arrange: Mock `FolioServiceClient.postFnbChargeToFolio()` để nhận ghi nợ thành công.
  2. Act: Thực hiện đặt món uống qua API.
  3. Assert: Verify mock được gọi đúng 1 lần với số tiền 15,000 VND.
- **Expected Result (PASS)**: Đơn hàng lưu thành công và Folio của phòng được ghi nhận nợ 15,000 VND.
- **Expected Result (FAIL)**: Đơn lưu nhưng không chuyển tiếp ghi nợ sang module thanh toán, hoặc không tích hợp được.

#### `FNB-TC-011` — createAlacarteOrder_invalidQuantity_throwsValidationException

- **Severity**: LOW
- **Feature Under Test**: `MealOrderService.createMealOrder()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-004`
- **Test Steps**:
  1. Act: Thực hiện đặt món với số lượng âm: `quantity = -5`.
- **Expected Result (PASS)**: Bị chặn bởi Validation (@Min(1)) và trả về HTTP 400 Bad Request.
- **Expected Result (FAIL)**: Tạo thành công đơn hàng số lượng âm trong DB.

#### `FNB-TC-012` — createAlacarteOrder_menuItemNotFound_throwsNotFoundException

- **Severity**: MEDIUM
- **Feature Under Test**: `MealOrderService.createMealOrder()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-004`
- **Test Steps**:
  1. Act: Đặt món ăn có ID không tồn tại: `menuItemId = 999`.
- **Expected Result (PASS)**: Trả về HTTP 404 Not Found.
- **Expected Result (FAIL)**: Lỗi 500 hoặc lưu bản ghi rỗng.

---

### UC20 — Đảm bảo Tối thiểu hóa dữ liệu (Data Minimization)

#### `FNB-TC-013` — getDietaryProfileForChef_chefRequestsProfile_returnsOnlyDietaryDetails

- **Severity**: HIGH
- **Feature Under Test**: `DietaryProfileRepository.findByUserId()`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-005`
- **Preconditions**: Khách hàng John Doe có cả hồ sơ y tế bệnh lý (đau lưng ở Spa) và hồ sơ dị ứng (đậu phộng ở F&B).
- **Test Steps**:
  1. Arrange: Đăng nhập quyền Chef.
  2. Act: Gọi API truy vấn hồ sơ ăn uống của John Doe.
  3. Assert: Kiểm tra cấu trúc dữ liệu trả về.
- **Expected Result (PASS)**: Dữ liệu trả về chỉ chứa thông tin `"Peanuts"`, tuyệt đối không chứa thông tin đau lưng (Spinal/Back pain).
- **Expected Result (FAIL)**: Dữ liệu trả về rò rỉ cả thông tin bệnh lý của module Spa cho Đầu bếp xem.

#### `FNB-TC-014` — getDietaryProfileForChef_invalidUserId_throwsValidationException

- **Severity**: LOW
- **Feature Under Test**: `DietaryProfileRepository`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-005`
- **Test Steps**:
  1. Act: Truy cập hồ sơ với ID người dùng sai định dạng (ví dụ: chuỗi thay vì số).
- **Expected Result (PASS)**: Trả về lỗi validate ở tầng Controller (HTTP 400).
- **Expected Result (FAIL)**: Không lọc được lỗi đầu vào dẫn đến lỗi cơ sở dữ liệu.

#### `FNB-TC-015` — getDietaryProfileForChef_userNotFound_throwsNotFoundException

- **Severity**: MEDIUM
- **Feature Under Test**: `DietaryProfileRepository`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-005`
- **Test Steps**:
  1. Act: Truy cập hồ sơ của ID người dùng không tồn tại (ví dụ: `userId = 99999`).
- **Expected Result (PASS)**: Trả về HTTP 404 Not Found.
- **Expected Result (FAIL)**: Trả về HTTP 200 với các giá trị rỗng/null.

---

## 5. SECURITY TEST CASES

### `FNB-TC-SEC-001` — apiEndpoint_unauthenticatedUser_returns401Unauthorized

- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-306 — Missing Authentication for Critical Function
- **Legal**: Nghị định 356/2025/NĐ-CP (Ngăn ngừa truy cập trái phép)
- **Feature Under Test**: Mọi API endpoint của F&B (Ví dụ: `POST /api/v1/fnb/meal-orders`)
- **Test File**: `com.xoai.retreat.fnb.controller.MealOrderControllerTest`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Chuẩn bị request rỗng, **không gửi** Header `Authorization: Bearer <Token>`.
  2. Act: Thực hiện gọi API `POST /api/v1/fnb/meal-orders`.
- **Expected Result (PASS)**: Trả về HTTP 401 Unauthorized.
- **Expected Result (FAIL)**: Endpoint cho phép thực thi tạo đơn đặt món mà không yêu cầu đăng nhập.

### `FNB-TC-SEC-002` — apiEndpoint_unauthorizedRoleGuestOwnMismatch_returns403Forbidden

- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: Nghị định 356/2025/NĐ-CP (Bảo vệ dữ liệu của chủ thể)
- **Feature Under Test**: API đặt món `POST /api/v1/fnb/meal-orders`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Đăng nhập tài khoản Guest A (`FX-JWT-GUEST-A`).
  2. Act: Gửi yêu cầu đặt món nhưng đặt cho ID của Guest B (`guestId = 999` của khách hàng B).
- **Expected Result (PASS)**: Hệ thống phát hiện Token và ID yêu cầu không khớp, chặn lại và trả về HTTP 403 Forbidden.
- **Expected Result (FAIL)**: Guest A có thể đặt món và tính phí nợ phòng cho Guest B dễ dàng.

### `FNB-TC-SEC-003` — apiEndpoint_therapistAccessingDietaryProfile_returns403Forbidden

- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: Nghị định 356/2025/NĐ-CP (Sử dụng đúng mục đích dữ liệu)
- **Feature Under Test**: API lấy hồ sơ ăn uống `GET /api/v1/fnb/dietary-profile/:uid`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Đăng nhập tài khoản Spa Therapist (`FX-JWT-THERAPIST`).
  2. Act: Gửi yêu cầu đọc thông tin dị ứng của khách hàng: `GET /api/v1/fnb/dietary-profile/10`.
- **Expected Result (PASS)**: Hệ thống chặn lại và trả về HTTP 403 Forbidden (Therapist không có quyền đọc thông tin ẩm thực).
- **Expected Result (FAIL)**: Trả về thông tin dị ứng ẩm thực của khách cho Therapist.

### `FNB-TC-SEC-004` — apiEndpoint_chefAccessingPhysicalHealthProfile_returns403Forbidden

- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: Nghị định 356/2025/NĐ-CP (Tách biệt dữ liệu y tế nhạy cảm)
- **Feature Under Test**: API hồ sơ sức khỏe thể chất `GET /api/v1/spa/physical-health/:uid`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Đăng nhập tài khoản Chef (`FX-JWT-CHEF`).
  2. Act: Cố tình gửi yêu cầu đọc bệnh lý đau cột sống của khách: `GET /api/v1/spa/physical-health/10`.
- **Expected Result (PASS)**: Hệ thống chặn lại và trả về HTTP 403 Forbidden hoặc ẩn thông tin.
- **Expected Result (FAIL)**: Đầu bếp đọc được toàn bộ bệnh lý của khách hàng.

### `FNB-TC-SEC-005` — convertToDatabaseColumn_sensitiveData_isEncryptedWithAesGcm

- **Severity**: CRITICAL
- **OWASP**: A02:2021 — Cryptographic Failures
- **CWE**: CWE-311 — Missing Encryption of Sensitive Data
- **Legal**: Nghị định 356/2025/NĐ-CP (Mã hóa dữ liệu nhạy cảm)
- **Feature Under Test**: `AesEncryptor.convertToDatabaseColumn()`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Chuẩn bị chuỗi dữ liệu thô: `"Peanuts, Shellfish"`.
  2. Act: Gọi phương thức mã hóa `convertToDatabaseColumn("Peanuts, Shellfish")`.
  3. Assert: Kiểm tra chuỗi trả về.
- **Expected Result (PASS)**: Chuỗi trả về là định dạng Base64 ngẫu nhiên, hoàn toàn không chứa plaintext `"Peanuts"` hay `"Shellfish"`.
- **Expected Result (FAIL)**: Trả về chuỗi nguyên thủy hoặc mã hóa yếu (ví dụ: Base64 thô không mã hóa).

### `FNB-TC-SEC-006` — convertToEntityAttribute_encryptedData_isDecryptedToPlaintext

- **Severity**: CRITICAL
- **OWASP**: A02:2021 — Cryptographic Failures
- **CWE**: CWE-311 — Cryptographic Failure
- **Feature Under Test**: `AesEncryptor.convertToEntityAttribute()`
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Lấy chuỗi đã mã hóa từ test `FNB-TC-SEC-005`.
  2. Act: Gọi `convertToEntityAttribute(encryptedString)`.
- **Expected Result (PASS)**: Chuỗi trả về phục hồi chính xác dữ liệu gốc `"Peanuts, Shellfish"`.
- **Expected Result (FAIL)**: Trả về chuỗi lỗi hoặc ném ra ngoại lệ giải mã sai khóa.

### `FNB-TC-SEC-007` — logOrQuery_sensitiveData_doesNotLeakPlaintextInLogs

- **Severity**: HIGH
- **OWASP**: A09:2021 — Security Logging and Monitoring Failures
- **CWE**: CWE-532 — Insertion of Sensitive Information into Log File
- **Legal**: Nghị định 356/2025/NĐ-CP
- **Feature Under Test**: Lớp log hoặc xuất query SQL của Hibernate
- **TDD Phase**: PENDING (RED Phase)
- **Test Steps**:
  1. Arrange: Kích hoạt ghi log SQL Hibernate.
  2. Act: Thực hiện lưu và truy vấn dữ liệu `DietaryProfile`.
  3. Assert: Quét log file kiểm tra xem có xuất hiện từ khóa `"Peanuts"` hay `"Shellfish"` không.
- **Expected Result (PASS)**: Log của ứng dụng và log SQL chỉ hiển thị các giá trị tham số mã hóa dạng băm hoặc dấu hỏi chấm (`?`), không lưu plaintext.
- **Expected Result (FAIL)**: Từ khóa nhạy cảm `"Peanuts"` bị in rõ ràng trong tệp nhật ký ứng dụng.

---

## 6. INTEGRATION TEST CASES

### `FNB-TC-INT-001` — createAlacarteOrderAndBillingIntegration_fullFlow

- **Severity**: HIGH
- **Feature Under Test**: Đặt món A-la-carte và đồng bộ hóa đơn nháp sang module Billing.
- **Test File**: `com.xoai.retreat.fnb.integration.MealOrderBillingIntegrationTest`
- **TDD Phase**: PENDING (RED Phase)
- **Condition Ref**: `COND-FNB-004`
- **Preconditions**:
  - PostgreSQL container đang chạy (sử dụng Testcontainers).
  - Schema CSDL đã được nạp đầy đủ.
- **Test Steps**:
  1. Seed thông tin một Booking trạng thái ACTIVE (Checked-in).
  2. Gửi request POST tạo đơn đặt món a-la-carte (Trà đá giá 15,000 VND).
  3. Thực hiện kiểm tra cơ sở dữ liệu của bảng `MEAL_ORDER`, `MEAL_ORDER_ITEM` và kiểm tra bảng `GUEST_FOLIO` xem số tiền nợ đã tăng thêm 15,000 VND hay chưa.
- **Expected Result (PASS)**:
  - DB chứa bản ghi `MEAL_ORDER` hợp lệ.
  - Folio tương ứng được cập nhật tăng thêm 15,000 VND.
- **Expected Result (FAIL)**: Bản ghi DB F&B lưu thành công nhưng Folio của khách không có thay đổi hoặc giao dịch bị rollback hoàn toàn.

#### DB Assertion (Java Spring Data JPA):

```java
// Kiểm tra đơn đặt món
MealOrder order = mealOrderRepository.findById(savedOrderId).orElse(null);
assertNotNull(order);
assertEquals("PENDING", order.getOrderStatus());

// Kiểm tra hóa đơn Folio thông qua RestTemplate/FeignClient mock hoặc kiểm tra trực tiếp DB folio nếu dùng chung DB
GuestFolio folio = guestFolioRepository.findByBookingId(bookingId).orElse(null);
assertNotNull(folio);
assertEquals(new BigDecimal("15000.00"), folio.getTotalExtraFb());
```

---

## 7. Red-Green-Refactor Tracker

| TC ID              | Test File                             | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :----------------- | :------------------------------------ | :--------------: | :---------------- | :--------------- |
| `FNB-TC-001`     | `MealOrderServiceTest.java`         |   [ ] Pending   |                   |                  |
| `FNB-TC-002`     | `MealOrderServiceTest.java`         |   [ ] Pending   |                   |                  |
| `FNB-TC-003`     | `MealOrderServiceTest.java`         |   [ ] Pending   |                   |                  |
| `FNB-TC-004`     | `ChefControllerTest.java`           |   [ ] Pending   |                   |                  |
| `FNB-TC-007`     | `MealOrderServiceTest.java`         |   [ ] Pending   |                   |                  |
| `FNB-TC-010`     | `MealOrderServiceTest.java`         |   [ ] Pending   |                   |                  |
| `FNB-TC-013`     | `DietaryProfileRepositoryTest.java` |   [ ] Pending   |                   |                  |
| `FNB-TC-SEC-001` | `MealOrderControllerTest.java`      |   [ ] Pending   |                   |                  |
| `FNB-TC-SEC-005` | `AesEncryptorTest.java`             |   [ ] Pending   |                   |                  |

---

## 8. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)

- [X] Tài liệu kiến trúc [FNB_Dietary_Management_EDS.md](file:///d:/SWP301/su26-swp391-se2023-g6/04_Implement/FNB_Dietary_Management_EDS.md) đã được phê duyệt.
- [X] Các cấu hình cơ sở dữ liệu `DB.sql` khớp hoàn toàn với mô tả.

### Exit Criteria (Điều kiện kết thúc - DoD)

- [ ] 100% test cases trong tài liệu này được biên dịch thành công và chạy PASS (xanh).
- [ ] Test coverage (độ bao phủ dòng lệnh) đạt tối thiểu **85%** đối với toàn bộ các class mới viết của module `fnb`.
- [ ] DPO xác nhận kết quả mã hóa dữ liệu thực tế không xảy ra rò rỉ plaintext trong file log.

---

## 9. Rollback Plan

### Revert implementation

Nếu quá trình tích hợp TDD gặp lỗi nghiêm trọng, chạy lệnh sau để đưa mã nguồn về phiên bản ổn định trước đó:

```bash
git checkout -- src/main/java/com/xoai/retreat/fnb/
git checkout -- src/test/java/com/xoai/retreat/fnb/
```
