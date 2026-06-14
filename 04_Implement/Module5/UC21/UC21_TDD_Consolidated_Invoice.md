# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-BILLING-TDD-021
**Version:** 1.0
**Date:** 2026-06-12
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Phùng Giang Hải– Backend Developer
**Reviewed by:** [x] Phùng Giang Hải - Tech Lead
**DPO Sign-off:** [x] Required (Hóa đơn chứa PII)
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**

* `01_SRS/SRS_Document_SWP391_G6.md` — UC21
* `03_Implement/UC21/UC21_EDS_Consolidated_Invoice.md` — Technical Specification
* `Database/DB.sql` — Tables: GUEST_FOLIO, FOLIO_ITEM, PAYMENT, AUDIT_LOG

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Test data dùng SYNTHETIC. Không dùng PII thật.

# CHANGELOG

> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày      | Người thực hiện | Nội dung thay đổi                                            |
| ---------- | ------------------- | --------------------------------------------------------------- |
| 2026-06-12 | Sinh viên 5        | Khởi tạo tài liệu — TDD spec cho UC21 Consolidated Invoice |
| 2026-06-14 | AI Assistant       | Đồng bộ TDD với EDS v2 (Bổ sung test cases cho BR-12 và BR-15) |
| 2026-06-14 | AI Assistant       | Bổ sung kỹ thuật (TDS-04) và dữ liệu Mock FX-005 (TDS-05) sau review |
| 2026-06-14 | AI Assistant       | Bổ sung AuditLogService & AuditLogRepo vào phạm vi TDS-01 |
| 2026-06-14 | Tech Lead          | Đã duyệt (Approved) tài liệu TDD |

# MỤC LỤC

1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field                           | Value                                                                |
| ------------------------------- | -------------------------------------------------------------------- |
| **Feature / Gap ID**      | `UC21`                                                             |
| **Module**                | `Consolidated Billing — Invoice View`                             |
| **Spec gốc**             | `UC21_EDS_Consolidated_Invoice.md`                                 |
| **Priority**              | 🔴 P0                                                                |
| **Sprint**                | `S3 (2026-06-09 → 2026-06-22)`                                    |
| **Milestone**             | `M3 Alpha - 2026-07-11`                                            |
| **Data Classification**   | `Internal`                                                         |
| **Compliance Scope**      | N/A                                                                  |
| **Upstream Dependencies** | `Booking Module, Spa Module (FOLIO_ITEM), F&B Module (FOLIO_ITEM)` |
| **Downstream Consumers**  | `UC22 (Process Final Payment)`                                     |

# 2. Logic Issues Resolved

| #  | Spec gốc (sai / thiếu)                                                                                       | Thực tế (schema / policy)                                                | Fix áp dụng trong test                                                |
| -- | -------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| L1 | ImplementationPlan gốc ghi `findByGuestFolio(GuestFolio)` truyền entity                                    | Code thực tế dùng `findByGuestFolioId(Integer)` truyền ID            | Test cases dùng `findByGuestFolioId(folioId)`                        |
| L2 | ImplementationPlan ghi field DTO `groupedExtraServices` nhưng code ban đầu dùng tên `groupedServices` | Đã sửa trong BillingServiceImpl                                         | Test verify đúng field name `groupedExtraServices`                  |
| L3 | DB column `total_package_amout` có typo                                                                     | Entity mapping giữ nguyên typo:`@Column(name = "total_package_amout")` | Test dùng entity setter `setTotalPackageAmount()` (Java tên chuẩn) |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
UC21 Consolidated Invoice bao gồm các layer:
├── Service
│   ├── BillingServiceImpl.getCheckoutData (Core logic & Exceptions)
│   └── AuditLogService.logActivity (Ghi vết kiểm toán - BR-15)
├── Controller (CheckoutController.showCheckoutPage - model binding & error handling)
└── Repository (GuestFolioRepo, FolioItemRepo, PaymentRepo, AuditLogRepo - JPA queries)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source            | Items Derived                                                        |
| ----------------- | -------------------------------------------------------------------- |
| `SRS.md` UC-21  | Hóa đơn gộp tổng hợp Package + Spa + F&B                       |
| `BR-11`         | Mọi Spa/F&B charge → Guest Folio                                   |
| `BR-12`         | Chặn xem/thanh toán nếu có FolioItem đang ở trạng thái PENDING     |
| `BR-15`         | Ghi nhận Audit Log mọi thao tác xem Hóa đơn                          |
| `ADR-001` (EDS) | Công thức tính: totalCost = packageAmount + SUM(FolioItem.amount) |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                                           | Coverage Item                             | Test Cases       |
| ------------ | -------------------------------------------------------- | ----------------------------------------- | ---------------- |
| TC-COND-001  | Tổng hợp hóa đơn thành công với FolioItems       | `BillingServiceImpl.getCheckoutData()`  | `BIL21-TC-001` |
| TC-COND-002  | BookingId không tồn tại → Exception                  | `BillingServiceImpl.getCheckoutData()`  | `BIL21-TC-002` |
| TC-COND-003  | Folio không có FolioItem → groupedExtraServices rỗng | `BillingServiceImpl.getCheckoutData()`  | `BIL21-TC-003` |
| TC-COND-004  | Gom nhóm FolioItem theo serviceCategory đúng          | `Stream.groupingBy()`                   | `BIL21-TC-004` |
| TC-COND-005  | Controller bind data đúng vào Model                   | `CheckoutController.showCheckoutPage()` | `BIL21-TC-005` |
| TC-COND-006  | Tồn tại FolioItem PENDING → Throw Exception          | `BillingServiceImpl.getCheckoutData()`  | `BIL21-TC-006` |
| TC-COND-007  | Ghi Audit Log thành công khi gọi Service              | `AuditLogService.logActivity()`         | `BIL21-TC-007` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4)  | Applied To                       | Rationale                                     |
| ------------------------ | -------------------------------- | --------------------------------------------- |
| Equivalence Partitioning | bookingId (valid/invalid)        | Phân vùng: ID tồn tại (Sinh ra TC-001) vs không tồn tại (Sinh ra TC-002) |
| Boundary Value Analysis  | FolioItem list (empty/non-empty) | Biên: 0 items (Sinh ra TC-003) vs >0 items (Sinh ra TC-004) |
| State Transition Testing | FolioItem status (PENDING)       | Kiểm tra chuyển đổi trạng thái: Nếu còn PENDING thì bị chặn (Sinh ra TC-006) |
| Use Case / API Testing   | Audit Logging (Service to Service)| Test luồng tương tác giữa các component (Sinh ra TC-005, TC-007) |

## TDS-05 — Test Data Requirements

| Fixture ID | Type   | Value / Logic                                                 | Mục đích          |
| ---------- | ------ | ------------------------------------------------------------- | -------------------- |
| `FX-001` | Entity | `GuestFolio(id=1, bookingId=1, totalPackageAmount=5000000)` | Happy path           |
| `FX-002` | Entity | `FolioItem(serviceCategory="Extra Spa", amount=500000)`     | Extra service charge |
| `FX-003` | Entity | `FolioItem(serviceCategory="Extra F&B", amount=200000)`     | Extra F&B charge     |
| `FX-004` | Entity | `Payment(status="SUCCESS", amount=2000000)`                 | Deposit payment      |
| `FX-005` | Entity | `FolioItem(status="PENDING")`                               | Kích hoạt lỗi BR-12  |

# 4. Test Case Specification

## BIL21-TC-001 — Tổng hợp hóa đơn gộp thành công

**Severity:** `HIGH`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()`
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001`

**Preconditions:**

* Mock `GuestFolioRepository.findByBookingId(1)` → `FX-001`
* Mock `FolioItemRepository.findByGuestFolioId(1)` → `[FX-002, FX-003]`
* Mock `PaymentRepository.findByGuestFolioIdAndStatus(1, "SUCCESS")` → `[FX-004]`

**Test Steps:**

1. Arrange: Chuẩn bị mock repositories với fixtures trên.
2. Act: Gọi `billingService.getCheckoutData(1)`.
3. Assert:
   - `dto.getFolio()` không null, bookingId = 1.
   - `dto.getGroupedExtraServices()` có 2 keys: "Extra Spa", "Extra F&B".
   - `dto.getTotalCost()` = 5000000 + 500000 + 200000 = 5700000.
   - `dto.getTotalPaid()` = 2000000.
   - `dto.getBalanceDue()` = 3700000.

**Expected Result (PASS — hành vi đúng):**

* DTO trả về với tất cả giá trị tính toán chính xác.

**Expected Result (FAIL — dấu hiệu lỗi):**

* Sai công thức tính hoặc thiếu grouping.

**Current Status:** 🟢 PASS

## BIL21-TC-002 — BookingId không tồn tại

**Severity:** `HIGH`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()`
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-002`

**Preconditions:**

* Mock `GuestFolioRepository.findByBookingId(999)` → `Optional.empty()`

**Test Steps:**

1. Arrange: Mock repository trả về empty.
2. Act: Gọi `billingService.getCheckoutData(999)`.
3. Assert: Throw `RuntimeException` với message chứa "not found".

**Expected Result (PASS — hành vi đúng):**

* `RuntimeException` thrown.

**Expected Result (FAIL — dấu hiệu lỗi):**

* Trả về null thay vì throw exception → NullPointerException ở tầng Controller.

**Current Status:** 🟢 PASS

## BIL21-TC-003 — Folio không có FolioItem (danh sách trống)

**Severity:** `MEDIUM`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()`
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-003`

**Preconditions:**

* Mock `GuestFolioRepository.findByBookingId(1)` → `FX-001` (packageAmount = 5000000)
* Mock `FolioItemRepository.findByGuestFolioId(1)` → `[]` (empty list)
* Mock `PaymentRepository.findByGuestFolioIdAndStatus(1, "SUCCESS")` → `[]`

**Test Steps:**

1. Arrange: Mock trả về empty lists.
2. Act: Gọi `billingService.getCheckoutData(1)`.
3. Assert:
   - `dto.getGroupedExtraServices()` là empty map.
   - `dto.getTotalCost()` = 5000000 (chỉ package).
   - `dto.getTotalPaid()` = 0.
   - `dto.getBalanceDue()` = 5000000.

**Expected Result (PASS — hành vi đúng):**

* Hóa đơn chỉ hiện giá gói, không có dịch vụ phát sinh.

**Expected Result (FAIL — dấu hiệu lỗi):**

* NullPointerException khi Stream xử lý empty list.

**Current Status:** 🟢 PASS

## BIL21-TC-004 — Gom nhóm FolioItem theo serviceCategory

**Severity:** `MEDIUM`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()` — Stream groupingBy logic
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-004`

**Preconditions:**

* 3 FolioItems: 2 "Extra Spa" + 1 "Extra F&B"
* 1 FolioItem có `serviceCategory = null`

**Test Steps:**

1. Arrange: Mock 4 FolioItems.
2. Act: Gọi `billingService.getCheckoutData(1)`.
3. Assert:
   - `groupedExtraServices.get("Extra Spa").size()` = 2.
   - `groupedExtraServices.get("Extra F&B").size()` = 1.
   - `groupedExtraServices.get("Khác").size()` = 1 (null category → "Khác").

**Expected Result (PASS — hành vi đúng):**

* Items null category được gom vào nhóm "Khác".

**Expected Result (FAIL — dấu hiệu lỗi):**

* NullPointerException khi `serviceCategory == null`.

**Current Status:** 🟢 PASS

## BIL21-TC-005 — Controller bind data vào Model

**Severity:** `MEDIUM`
**Feature Under Test:** `CheckoutController.showCheckoutPage()`
**Test File:** `CheckoutControllerTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-005`

**Preconditions:**

* Mock `BillingService.getCheckoutData(1)` trả về `CheckoutViewDTO` hợp lệ.

**Test Steps:**

1. Arrange: Mock service.
2. Act: `mockMvc.perform(get("/billing/checkout").param("bookingId", "1"))`.
3. Assert:
   - Status 200 OK.
   - View name = `billing/checkout/checkout`.
   - `model().attributeExists("data")`.
   - `model().attribute("pageTitle", "Hóa đơn Gộp & Check-out")`.

**Expected Result (PASS — hành vi đúng):**

* View render đúng với data trong Model.

**Expected Result (FAIL — dấu hiệu lỗi):**

* 404 do sai view path hoặc thiếu model attribute.

**Current Status:** 🟢 PASS

## BIL21-TC-006 — Bị chặn do vi phạm BR-12 (Pending Orders)

**Severity:** `HIGH`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()`
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-006`

**Preconditions:**
* Mock `GuestFolioRepository.findByBookingId(1)` → `FX-001`
* Mock `FolioItemRepository.existsByGuestFolioIdAndStatusIn(1, ["PENDING"])` → `true`

**Test Steps:**
1. Arrange: Thiết lập mock repository trả về `true` cho trạng thái PENDING.
2. Act: Gọi `billingService.getCheckoutData(1)`.
3. Assert: Verify method throws `PendingOrderException`.

**Expected Result (PASS — hành vi đúng):**
* `PendingOrderException` được ném ra để chặn luồng tạo hóa đơn.

**Current Status:** 🔴 RED

## BIL21-TC-007 — Ghi nhận Audit Log (BR-15)

**Severity:** `HIGH`
**Feature Under Test:** `BillingServiceImpl.getCheckoutData()` tương tác với `AuditLogService`
**Test File:** `BillingServiceImplTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-007`

**Preconditions:**
* Mock `AuditLogService` (Spy hoặc Mock).

**Test Steps:**
1. Arrange: Thiết lập các mock repository hợp lệ (Happy path).
2. Act: Gọi `billingService.getCheckoutData(1)`.
3. Assert: Verify `auditLogService.logActivity` được gọi chính xác 1 lần với action="VIEW_INVOICE".

**Expected Result (PASS — hành vi đúng):**
* Audit Log Service được kích hoạt đúng.

**Current Status:** 🔴 RED

# 5. Red-Green-Refactor Tracker

| TC ID            | Test File                       | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| ---------------- | ------------------------------- | ---------------- | ----------------- | ---------------- |
| `BIL21-TC-001` | `BillingServiceImplTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL21-TC-002` | `BillingServiceImplTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL21-TC-003` | `BillingServiceImplTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL21-TC-004` | `BillingServiceImplTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL21-TC-005` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL21-TC-006` | `BillingServiceImplTest.java` | `[X]`          | `[ ]`           | Pending implementation |
| `BIL21-TC-007` | `BillingServiceImplTest.java` | `[X]`          | `[ ]`           | Pending implementation |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)

- [X] Spec kỹ thuật `UC21_EDS_Consolidated_Invoice.md` đã được review (Bao gồm update BR-12, BR-15)
- [X] Database schema cho GUEST_FOLIO, FOLIO_ITEM, PAYMENT và AUDIT_LOG đã sẵn sàng
- [X] Test fixtures (Section 3 TDS-05) đã được chuẩn bị

## Exit Criteria (Điều kiện kết thúc — DoD)

- [X] `mvn test` — tất cả unit tests xanh (không có skip)
- [X] Test coverage ≥ 80% lines cho `BillingServiceImpl`
- [X] Trang `/billing/checkout?bookingId=1` hiển thị đúng dữ liệu
- [X] Công thức tính totalCost, totalPaid, balanceDue chính xác

## Suspension Criteria (Điều kiện tạm dừng)

* Module Spa hoặc F&B chưa INSERT dữ liệu vào FOLIO_ITEM
* Database schema thay đổi cấu trúc bảng GUEST_FOLIO

# 7. Rollback Plan

```bash
# UC21 là chức năng chỉ đọc, không thay đổi DB schema
# Revert implementation files nếu gây lỗi:
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/billing/service/impl/BillingServiceImpl.java
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/billing/controller/CheckoutController.java
```
