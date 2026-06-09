# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-BILLING-TDD-022
**Version:** 1.2
**Date:** 2026-06-09
**Status:** Draft
**Author:** AI Assistant

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `UC22` |
| **Module** | `Consolidated Billing & Statistical Analysis - Checkout` |
| **Spec gốc** | `SRS_Document_SWP391_G6.md - UC22` |
| **Priority** | 🔴 P0 |

# 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Fix áp dụng trong test |
| --- | --- | --- |
| L1 | *Kiến trúc REST API (json)* | *Đã điều chỉnh sang Spring MVC (Redirect, View, Model)* |
| L2 | *Thanh toán xử lý thành công ngay trong 1 bước* | *Test cases cập nhật lại để verify luồng 2 bước (Sinh URL VNPay + Nhận Callback)* |

# 3. Test Design Specification (TDS)

## TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| --- | --- |
| `SRS.md` UC-22 | Hành vi xử lý thanh toán |
| `BR-12` | Checkout Constraint: Chặn check-out nếu còn đơn pending |
| `VNPay Docs` | Yêu cầu verify `vnp_SecureHash` và `vnp_ResponseCode` |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| TC-COND-001 | Thanh toán CASH thành công | `processPayment` | `BIL-TC-001` |
| TC-COND-002 | Khách còn đơn hàng Pending -> Báo lỗi | `initiatePayment` | `BIL-TC-002` |
| TC-COND-003 | Thanh toán VNPAY -> Sinh URL Redirect chứa `vnp_SecureHash` | `processPayment` | `BIL-TC-003` |
| TC-COND-004 | VNPAY Callback: Thành công (`00`) với chữ ký hợp lệ | `vnpayReturn` | `BIL-TC-004` |
| TC-COND-005 | VNPAY Callback: Thất bại / Hủy (`24`) | `vnpayReturn` | `BIL-TC-005` |
| TC-COND-006 | VNPAY Callback: Sai chữ ký (Hash Mismatch) | `vnpayReturn` | `BIL-TC-006` |

# 4. Test Case Specification

## BIL-TC-001 — Xử lý thanh toán Tiền mặt (CASH)

**Feature Under Test:** `CheckoutController.processPayment()`
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Khởi tạo DB không có Pending orders.
2. Act: Gọi `mockMvc.perform(post("/checkout/{bookingId}/pay").param("paymentMethod", "CASH"))`.
3. Assert: 
   - HTTP Status `302 Found`.
   - `redirectedUrl` là `/checkout/{bookingId}/success`.
   - DB: Booking là COMPLETED, Folio là PAID.

## BIL-TC-002 — Chặn thanh toán nếu còn đơn hàng Pending (BR-12)

**Feature Under Test:** `CheckoutService.initiatePayment()`
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Khởi tạo DB có 1 Spa order trạng thái PENDING.
2. Act: Gọi `mockMvc.perform(post("/checkout/{bookingId}/pay").param("paymentMethod", "VNPAY"))`.
3. Assert:
   - HTTP Status `302 Found`, redirect quay lại trang hiện tại.
   - `flash().attributeExists("errorMessage")` chứa nội dung vi phạm.

## BIL-TC-003 — Sinh URL VNPay & Redirect (Giai đoạn 1)

**Feature Under Test:** `CheckoutController.processPayment()` (Nhánh VNPAY)
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Mock `vnPayService.createPaymentUrl` trả về `https://sandbox.vnpayment.vn/paymentv2/...`
2. Act: Gọi `mockMvc.perform(post("/checkout/{bookingId}/pay").param("paymentMethod", "VNPAY"))`.
3. Assert:
   - HTTP Status `302 Found`.
   - `redirectedUrl` bắt đầu bằng `https://sandbox.vnpayment.vn`.
   - DB: Payment record được sinh ra với trạng thái `PENDING`.

## BIL-TC-004 — VNPay Callback Thành công (Giai đoạn 2)

**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Tạo Payment có id=100 đang `PENDING`. Mock `verifySignature` trả về `true`.
2. Act: Gọi `mockMvc.perform(get("/checkout/vnpay-return").param("vnp_ResponseCode", "00").param("vnp_TxnRef", "100"))`.
3. Assert:
   - HTTP Status `302 Found` tới trang `/success`.
   - DB: Payment ID 100 cập nhật thành `SUCCESS`.
   - DB: Booking và Villa cập nhật trạng thái chốt.

## BIL-TC-005 — VNPay Callback Thất bại / Bị hủy (Giai đoạn 2)

**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Tạo Payment có id=101 đang `PENDING`. Mock `verifySignature` trả về `true`.
2. Act: Gọi `mockMvc.perform(get("/checkout/vnpay-return").param("vnp_ResponseCode", "24").param("vnp_TxnRef", "101"))`.
3. Assert:
   - HTTP Status `302 Found` tới trang báo lỗi.
   - `flash().attributeExists("errorMessage")`.
   - DB: Payment ID 101 cập nhật thành `FAILED`.
   - DB: Trạng thái Booking/Villa/Folio KHÔNG ĐỔI.

## BIL-TC-006 — VNPay Callback Sai Chữ Ký (Hash Mismatch)

**Feature Under Test:** `CheckoutController.vnpayReturn()`
**TDD Phase:** 🔴 RED

**Test Steps:**
1. Arrange: Tạo Payment có id=102 đang `PENDING`. Mock `verifySignature` trả về `false`.
2. Act: Gọi `mockMvc.perform(get("/checkout/vnpay-return").param("vnp_ResponseCode", "00").param("vnp_TxnRef", "102"))`.
3. Assert:
   - HTTP Status `302 Found` tới trang checkout cũ.
   - `flash().attribute("errorMessage", "Lỗi bảo mật chữ ký VNPay!")`.
   - DB: Trạng thái Payment/Booking KHÔNG ĐỔI (Vẫn PENDING).
