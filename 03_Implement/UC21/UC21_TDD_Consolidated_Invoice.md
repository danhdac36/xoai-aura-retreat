# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-BILLING-TDD-021
**Version:** 1.0
**Date:** 2026-06-12
**Status:** Draft
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Sinh viên 5 – Backend Developer
**Reviewed by:** [ ] Tech Lead – Pending
**DPO Sign-off:** [ ] N/A
**Approved by:** [ ] Pending
**Classification:** Internal – Confidential

**References:**
* `01_SRS/SRS_Document_SWP391_G6.md` — UC21
* `03_Implement/UC21/UC21_EDS_Consolidated_Invoice.md` — Technical Specification
* `Database/DB.sql` — Tables: GUEST_FOLIO, FOLIO_ITEM, PAYMENT

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.
> Thứ tự bắt buộc: viết test → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Test data dùng SYNTHETIC. Không dùng PII thật.

# CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-12 | Sinh viên 5 | Khởi tạo tài liệu — TDD spec cho UC21 Consolidated Invoice |

# MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `UC21` |
| **Module** | `Consolidated Billing — Invoice View` |
| **Spec gốc** | `UC21_EDS_Consolidated_Invoice.md` |
| **Priority** | 🔴 P0 |
| **Sprint** | `S3 (2026-06-09 → 2026-06-22)` |
| **Milestone** | `M3 Alpha - 2026-07-11` |
| **Data Classification** | `Internal` |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | `Booking Module, Spa Module (FOLIO_ITEM), F&B Module (FOLIO_ITEM)` |
| **Downstream Consumers** | `UC22 (Process Final Payment)` |

# 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| L1 | ImplementationPlan gốc ghi `findByGuestFolio(GuestFolio)` truyền entity | Code thực tế dùng `findByGuestFolioId(Integer)` truyền ID | Test cases dùng `findByGuestFolioId(folioId)` |
| L2 | ImplementationPlan ghi field DTO `groupedExtraServices` nhưng code ban đầu dùng tên `groupedServices` | Đã sửa trong BillingServiceImpl | Test verify đúng field name `groupedExtraServices` |
| L3 | DB column `total_package_amout` có typo | Entity mapping giữ nguyên typo: `@Column(name = "total_package_amout")` | Test dùng entity setter `setTotalPackageAmount()` (Java tên chuẩn) |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
UC21 Consolidated Invoice bao gồm các layer:
├── Service (BillingServiceImpl.getCheckoutData - core logic)
├── Controller (CheckoutController.showCheckoutPage - model binding)
└── Repository (GuestFolioRepo, FolioItemRepo, PaymentRepo - JPA queries)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| --- | --- |
| `SRS.md` UC-21 | Hóa đơn gộp tổng hợp Package + Spa + F&B |
| `BR-11` | Mọi Spa/F&B charge → Guest Folio |
| `ADR-001` (EDS) | Công thức tính: totalCost = packageAmount + SUM(FolioItem.amount) |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| TC-COND-001 | Tổng hợp hóa đơn thành công với FolioItems | `BillingServiceImpl.getCheckoutData()` | `BIL21-TC-001` |
| TC-COND-002 | BookingId không tồn tại → Exception | `BillingServiceImpl.getCheckoutData()` | `BIL21-TC-002` |
| TC-COND-003 | Folio không có FolioItem → groupedExtraServices rỗng | `BillingServiceImpl.getCheckoutData()` | `BIL21-TC-003` |
| TC-COND-004 | Gom nhóm FolioItem theo serviceCategory đúng | `Stream.groupingBy()` | `BIL21-TC-004` |
| TC-COND-005 | Controller bind data đúng vào Model | `CheckoutController.showCheckoutPage()` | `BIL21-TC-005` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| --- | --- | --- |
| Equivalence Partitioning | bookingId (valid/invalid) | Phân vùng: ID tồn tại vs không tồn tại |
| Boundary Value Analysis | FolioItem list (empty/non-empty) | Biên: 0 items vs nhiều items |

## TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| --- | --- | --- | --- |
| `FX-001` | Entity | `GuestFolio(id=1, bookingId=1, totalPackageAmount=5000000)` | Happy path |
| `FX-002` | Entity | `FolioItem(serviceCategory="Extra Spa", amount=500000)` | Extra service charge |
| `FX-003` | Entity | `FolioItem(serviceCategory="Extra F&B", amount=200000)` | Extra F&B charge |
| `FX-004` | Entity | `Payment(status="SUCCESS", amount=2000000)` | Deposit payment |

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

**Current Status:** 🔴 Not written

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

**Current Status:** 🔴 Not written

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

**Current Status:** 🔴 Not written

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

**Current Status:** 🔴 Not written

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

**Current Status:** 🔴 Not written

# 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | --- | --- | --- |
| `BIL21-TC-001` | `BillingServiceImplTest.java` | `[ ]` | `[ ]` | |
| `BIL21-TC-002` | `BillingServiceImplTest.java` | `[ ]` | `[ ]` | |
| `BIL21-TC-003` | `BillingServiceImplTest.java` | `[ ]` | `[ ]` | |
| `BIL21-TC-004` | `BillingServiceImplTest.java` | `[ ]` | `[ ]` | |
| `BIL21-TC-005` | `CheckoutControllerTest.java` | `[ ]` | `[ ]` | |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `UC21_EDS_Consolidated_Invoice.md` đã được review
- [x] Database schema cho GUEST_FOLIO, FOLIO_ITEM, PAYMENT đã sẵn sàng
- [x] Test fixtures (Section 3 TDS-05) đã được chuẩn bị

## Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip)
- [ ] Test coverage ≥ 80% lines cho `BillingServiceImpl`
- [ ] Trang `/billing/checkout?bookingId=1` hiển thị đúng dữ liệu
- [ ] Công thức tính totalCost, totalPaid, balanceDue chính xác

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
