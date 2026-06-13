# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-BILLING-TDD-022
**Version:** 1.2
**Date:** 2026-06-12
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Phùng Giang Hải
**Reviewed by:** [x] Phùng Giang Hải
**DPO Sign-off:** [ ] N/A
**Approved by:** [x] Principal Architect
**Classification:** Internal – Confidential

**References:**

* `01_SRS/SRS_Document_SWP391_G6.md` — UC22
* `03_Implement/UC22/UC22_EDS_Process_Final_Payment.md` — Technical Specification

> **Quy ước TDD:** Tài liệu này mô tả test cases TRƯỚC khi viết production code.

# CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                            |
| ---------- | ------------------- | ----------------------------------------------- |
| 2026-06-08 | AI Assistant        | Tạo tài liệu lần đầu                      |
| 2026-06-12 | AI Assistant        | Bổ sung đầy đủ format theo template chuẩn |

# MỤC LỤC

1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field                           | Value                                                      |
| ------------------------------- | ---------------------------------------------------------- |
| **Feature / Gap ID**      | `UC22`                                                   |
| **Module**                | `Consolidated Billing & Statistical Analysis - Checkout` |
| **Spec gốc**             | `UC22_EDS_Process_Final_Payment.md`                      |
| **Priority**              | 🔴 P0                                                      |
| **Sprint**                | `S3 (2026-06-09 → 2026-06-22)`                          |
| **Milestone**             | `M3 Alpha - 2026-07-11`                                  |
| **Data Classification**   | `Internal`                                               |
| **Compliance Scope**      | N/A                                                        |
| **Upstream Dependencies** | `Booking Module, Spa Module, F&B Module`                 |
| **Downstream Consumers**  | `Analytics Module`                                       |

# 2. Logic Issues Resolved

| #  | Spec gốc (sai / thiếu)                             | Thực tế (schema / policy)            | Fix áp dụng trong test                                                                 |
| -- | ---------------------------------------------------- | -------------------------------------- | ---------------------------------------------------------------------------------------- |
| L1 | Kiến trúc REST API (json)                          | Kiến trúc Spring MVC Controller      | Đã điều chỉnh sang Spring MVC (Redirect, View, Model)                               |
| L2 | Thanh toán xử lý thành công ngay trong 1 bước | VNPay tích hợp qua Redirect 2 bước | Test cases cập nhật lại để verify luồng 2 bước (Sinh URL VNPay + Nhận Callback) |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
UC22 Checkout bao gồm các layer:
├── Service (BillingServiceImpl - initiate, complete, fail)
├── Controller (CheckoutController - submit form, vnpay return)
└── Repository (PaymentRepo, FolioItemRepo)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source           | Items Derived                                                |
| ---------------- | ------------------------------------------------------------ |
| `SRS.md` UC-22 | Hành vi xử lý thanh toán                                 |
| `BR-12`        | Checkout Constraint: Chặn check-out nếu còn đơn pending |
| `VNPay Docs`   | Yêu cầu verify `vnp_SecureHash` và `vnp_ResponseCode` |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                               | Coverage Item       | Test Cases     |
| ------------ | -------------------------------------------- | ------------------- | -------------- |
| TC-COND-001  | Thanh toán CASH thành công                | `processPayment`  | `BIL-TC-001` |
| TC-COND-002  | Khách còn đơn hàng Pending -> Báo lỗi | `initiatePayment` | `BIL-TC-002` |
| TC-COND-003  | Thanh toán VNPAY -> Sinh URL Redirect       | `processPayment`  | `BIL-TC-003` |
| TC-COND-004  | VNPAY Callback: Thành công (`00`)        | `vnpayReturn`     | `BIL-TC-004` |
| TC-COND-005  | VNPAY Callback: Thất bại / Hủy (`24`)   | `vnpayReturn`     | `BIL-TC-005` |
| TC-COND-006  | VNPAY Callback: Sai chữ ký                 | `vnpayReturn`     | `BIL-TC-006` |
| TC-COND-007  | Nợ 0 đồng -> Bypass thanh toán           | `processPayment`  | `BIL-TC-007` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique                | Applied To     | Rationale                       |
| ------------------------ | -------------- | ------------------------------- |
| Equivalence Partitioning | Payment Method | Phân vùng: CASH vs VNPAY      |
| State Transition         | Payment Status | PENDING -> SUCCESS hoặc FAILED |

## TDS-05 — Test Data Requirements

| Fixture ID | Type   | Value / Logic                                | Mục đích               |
| ---------- | ------ | -------------------------------------------- | ------------------------- |
| `FX-001` | Entity | `GuestFolio(bookingId=1, balanceDue=1000)` | Data thanh toán hợp lệ |
| `FX-002` | Entity | `FolioItem(status="PENDING")`              | Vi phạm BR-12            |

# 4. Test Case Specification

## BIL-TC-001 — Xử lý thanh toán Tiền mặt (CASH)

**Severity:** `HIGH`
**Feature Under Test:** `CheckoutController.processPayment()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001`

**Preconditions:** Khởi tạo DB không có Pending orders, balanceDue > 0.
**Test Steps:** Gọi `mockMvc.perform(post("/checkout/1/pay").param("paymentMethod", "CASH"))`.
**Expected Result (PASS):** HTTP 302 tới `/checkout/success`, DB Booking là COMPLETED, Folio là PAID.
**Expected Result (FAIL):** 500 lỗi logic hoặc không cập nhật trạng thái phòng.

## BIL-TC-002 — Chặn thanh toán nếu còn đơn hàng Pending (BR-12)

**Severity:** `CRITICAL`
**Feature Under Test:** `BillingServiceImpl.initiatePayment()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-002`

**Preconditions:** Khởi tạo DB có 1 Spa order trạng thái PENDING.
**Test Steps:** Gọi `mockMvc.perform(post("/checkout/1/pay").param("paymentMethod", "VNPAY"))`.
**Expected Result (PASS):** HTTP 302 quay lại checkout page, `flash().attributeExists("errorMessage")`.
**Expected Result (FAIL):** Vẫn sinh URL VNPay mặc dù có đơn nợ.

## BIL-TC-003 — Sinh URL VNPay & Redirect (Giai đoạn 1)

**Severity:** `HIGH`
**Feature Under Test:** `CheckoutController.processPayment()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-003`

**Preconditions:** Mock VNPayService trả về URL hợp lệ.
**Test Steps:** Gọi POST với `paymentMethod=VNPAY`.
**Expected Result (PASS):** HTTP 302, redirectUrl chứa `sandbox.vnpayment.vn`, Payment ở DB là PENDING.

## BIL-TC-004 — VNPay Callback Thành công (Giai đoạn 2)

**Severity:** `CRITICAL`
**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-004`

**Preconditions:** Payment ID 100 PENDING. Mock `verifySignature` = true.
**Test Steps:** GET `/vnpay-return` với `vnp_ResponseCode=00`.
**Expected Result (PASS):** Redirect tới `/success`, Payment=SUCCESS, Booking=COMPLETED.

## BIL-TC-005 — VNPay Callback Thất bại / Bị hủy (Giai đoạn 2)

**Severity:** `HIGH`
**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-005`

**Preconditions:** Payment ID 101 PENDING. Mock `verifySignature` = true.
**Test Steps:** GET `/vnpay-return` với `vnp_ResponseCode=24`.
**Expected Result (PASS):** Redirect về báo lỗi, Payment=FAILED, Booking không đổi.

## BIL-TC-006 — VNPay Callback Sai Chữ Ký (Security Test)

**Severity:** `CRITICAL`
**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-006`

**Preconditions:** Mock `verifySignature` = false.
**Test Steps:** GET `/vnpay-return` với `vnp_ResponseCode=00` (giả mạo thành công).
**Expected Result (PASS):** Redirect báo "Lỗi bảo mật", Payment VẪN PENDING.
**Expected Result (FAIL):** Bị bypass chữ ký, cho phép check-out chùa.

## BIL-TC-007 — Bypass Thanh toán khi Nợ 0 Đồng

**Severity:** `CRITICAL`
**Feature Under Test:** `CheckoutController.processPayment()`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-007`

**Preconditions:** Khởi tạo DB không có Pending orders, balanceDue = 0.
**Test Steps:** Gọi `mockMvc.perform(post("/checkout/1/pay"))` (không truyền tham số paymentMethod).
**Expected Result (PASS):** HTTP 302 tới `/checkout/success`, không sinh thêm Payment record, DB Booking là COMPLETED, Folio là PAID.
**Expected Result (FAIL):** Quăng lỗi 400 Bad Request hoặc 500 Internal Server Error.

# 5. Red-Green-Refactor Tracker

| TC ID          | Test File                       | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| -------------- | ------------------------------- | ---------------- | ----------------- | ---------------- |
| `BIL-TC-001` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-002` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-003` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-004` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-005` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-006` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |
| `BIL-TC-007` | `CheckoutControllerTest.java` | `[X]`          | `[X]`           | Passed           |

# 6. Entry / Exit Criteria

## Entry Criteria

- [X] Đã hoàn thiện thiết kế Payment Entity
- [X] Đã tích hợp API VNPay mẫu

## Exit Criteria

- [X] Tất cả các test cases qua (Green)
- [X] VNPay callback signature verify 100% không thể bypass

## Suspension Criteria (Điều kiện tạm dừng)

* Cổng thanh toán VNPay Sandbox gặp sự cố mạng (timeout liên tục)
* Database lock timeout khi xử lý concurrency thanh toán

# 7. Rollback Plan

Nếu VNPayService gặp sự cố kết nối liên tục, vô hiệu hóa nút VNPay trên UI, chỉ sử dụng thanh toán tiền mặt. Revert code về nhánh trước khi apply VNPay.
