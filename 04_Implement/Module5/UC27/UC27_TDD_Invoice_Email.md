# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

## Mẫu Đặc tả Kiểm thử Hướng Phát triển

- **Document ID**: AM-MOD5-TDD-027
- **Version**: 1.0
- **Date**: 2026-06-17
- **Status**: Approved
- **Standard**: ISO/IEC/IEEE 29119-3:2021
- **Author**: Phùng Giang Hải
- **Reviewed by**: [X] Tech Lead — Approved
- **DPO Sign-off**: [X] Approved (PDPA scope)
- **Approved by**: [X] Principal Architect — Approved
- **Classification**: Internal — Confidential

### References:

- `01_Requirement/SRS_Document.md` — Functional requirements (UC27, BR-21, BR-15)
- `04_Implement/Module5/UC27/UC27_EDS_Invoice_Email.md` — Technical Specification
- `Nghị định 356/2025` — Bảo vệ Dữ liệu Cá nhân (PDPA)

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Bắt buộc: viết test (`@Test`) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Test data dùng Java Object Mocking (`Mockito`). KHÔNG dùng email thật của khách.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày      | Người thực hiện | Nội dung thay đổi                                                                                                                                                                                                                 |
| :--------- | :------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2026-06-17 | Phùng Giang Hải   | Khởi tạo tài liệu TDD spec cho UC27 (Hóa đơn PDF & Gửi Email)                                                                                                                                                                |
| 2026-06-17 | Phùng Giang Hải   | Đồng bộ TDD với EDS v1.3: (1) Thêm TC-005 kiểm tra nội dung PDF chuẩn BR-21, (2) Bổ sung assert Audit Log vào TC-001/TC-INT-001, (3) Thêm TC-006 kiểm tra nguồn dữ liệu gộp, (4) Cập nhật Test Basis và Fixtures. |
| 2026-06-17 | Phùng Giang Hải   | Hoàn tất Implement và Fix các lỗi liên quan. Đã Pass 22/22 Tests. Đổi trạng thái sang 🟢 GREEN. |
| 2026-06-17 | Phùng Giang Hải   | Cập nhật Status sang Approved sau khi Tech Lead nghiệm thu toàn bộ Test Cases. |

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

| Field                           | Value                                                         |
| :------------------------------ | :------------------------------------------------------------ |
| **Feature / Gap ID**      | UC27                                                          |
| **Module**                | Billing & Communication (Module 5)                            |
| **Spec gốc**             | AM-MOD5-IMP-027 (EDS)                                         |
| **Priority**              | 🔴 P0                                                         |
| **Sprint**                | Sprint hiện tại                                             |
| **Data Classification**   | Sensitive-PII / PII (Email khách hàng, Chi phí Folio)      |
| **Compliance Scope**      | NĐ 356/2025 Điều khoản hạn chế lưu trữ dữ liệu rác |
| **Upstream Dependencies** | `GuestFolioService`, `CheckoutCompletedEvent`             |
| **Downstream Consumers**  | `JavaMailSender`                                            |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| #  | Spec gốc (sai / thiếu)                              | Thực tế (schema / policy)                                            | Fix áp dụng trong test                                                     |
| :- | :---------------------------------------------------- | :--------------------------------------------------------------------- | :--------------------------------------------------------------------------- |
| L1 | Chưa rõ (Tính năng phát triển mới hoàn toàn) | Gửi Email & PDF In-Memory                                             | Viết Test bám sát giao diện `byte[]` thay vì lưu file.               |
| L2 | EDS v1.2 thiếu validate nội dung PDF theo BR-21     | BR-21 yêu cầu Logo, MST, VAT 10%, tổng tiền bằng chữ             | Thêm TC-005 kiểm tra nội dung byte stream chứa các trường bắt buộc. |
| L3 | EDS v1.2 Happy Path thiếu bước ghi Audit Log       | SRS Normal Flow Bước 6 bắt buộc ghi Audit Log cả khi thành công | Bổ sung assert `auditService.logEvent()` vào TC-001 và TC-INT-001.      |
| L4 | EDS v1.2 không ghi rõ nguồn dữ liệu Consolidated | EDS v1.3 liệt kê: Room_Booking, Folio_Item, Guest, Villa             | Thêm TC-006 kiểm tra Service phải query đúng 4 bảng nguồn.            |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Billing & Communication (UC27)` bao gồm các layer:

- Domain/Services (`PdfGeneratorService`, `EmailNotificationService` — test bằng `Mockito`)
- Event Listener (`InvoiceEmailListener` — mock Services)
- Integration (Test Spring `@Async` và `@SpringBootTest` kết nối với `GreenMail`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source                   | Items Derived                                                                                   |
| :----------------------- | :---------------------------------------------------------------------------------------------- |
| `SRS_Document.md` UC27 | (Trigger ngầm sau Checkout, luồng Alternative E1, A1)                                         |
| ADR-005                  | (In-Memory PDF Generation -`byte[]`)                                                          |
| BR-21                    | (Nội dung PDF: Logo, MST, VAT 10% tách riêng, tổng tiền bằng chữ. Không ghi file tĩnh) |
| BR-15                    | (Ghi Audit Log cho mọi trường hợp: thành công VÀ thất bại)                             |
| EDS §8.1                | (Nguồn dữ liệu gộp: Room_Booking, Folio_Item, Guest, Villa)                                 |
| NĐ 356/2025             | (Mã hóa, bảo mật Data PII)                                                                  |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition                           | Coverage Item                | Test Cases          |
| :----------- | :--------------------------------------- | :--------------------------- | :------------------ |
| TC-COND-001  | Tạo PDF thành công                    | `PdfGeneratorService`      | `MOD5-TC-001`     |
| TC-COND-002  | Gửi email đính kèm thành công      | `EmailNotificationService` | `MOD5-TC-002`     |
| TC-COND-003  | Khách không có email                  | `InvoiceEmailListener`     | `MOD5-TC-003`     |
| TC-COND-004  | Lỗi SMTP (Exception)                    | `InvoiceEmailListener`     | `MOD5-TC-004`     |
| TC-COND-005  | Nội dung PDF chuẩn BR-21               | `PdfGeneratorService`      | `MOD5-TC-005`     |
| TC-COND-006  | Query đủ 4 bảng nguồn dữ liệu gộp | `PdfGeneratorService`      | `MOD5-TC-006`     |
| TC-COND-007  | Checkout Event Trigger (E2E)             | Toàn bộ luồng Tích hợp  | `MOD5-TC-INT-001` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4)  | Applied To                                  | Rationale                                                                       |
| :----------------------- | :------------------------------------------ | :------------------------------------------------------------------------------ |
| Equivalence Partitioning | `guestEmail` (Null/Rỗng vs Có hợp lệ) | Test luồng Alternative A1 (MSG-22).                                            |
| Error Guessing           | Ngoại lệ SMTP / iTextPDF                  | Test luồng Exception E1 (MSG-23). Đảm bảo không văng lỗi làm chết app. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type            | Value / Logic                                                                     | Mục đích                      |
| :--------- | :-------------- | :-------------------------------------------------------------------------------- | :------------------------------- |
| FX-001     | Mock Object     | `FolioDTO(id: 1001, items: [RoomCharge, SpaCharge, FnbCharge], total: 5500000)` | Test PDF Generator               |
| FX-002     | Mock SMTP       | Khởi chạy `GreenMail` server                                                  | Test JavaMailSender              |
| FX-003     | Mock Event      | `CheckoutCompletedEvent(id:1001, "test@qa.com", folioId:1001)`                  | Test Listener trigger            |
| FX-004     | Mock Event      | `CheckoutCompletedEvent(id:1001, null, folioId:1001)`                           | Test luồng thiếu Email         |
| FX-005     | Mock Object     | `Guest(name: "Nguyen Van A", email: "test@qa.com")`                             | Test thông tin khách trên PDF |
| FX-006     | Mock Repository | `Room_Booking`, `Folio_Item`, `Guest`, `Villa` (Mockito stubs)            | Test query data sources          |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-TC-001` — Kết xuất PDF In-Memory thành công

- **Severity**: CRITICAL
- **Feature Under Test**: `PdfGeneratorService.generateConsolidatedInvoice()`
- **Test File**: `PdfGeneratorServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:

- (Mock) `GuestFolioService` trả về một `FolioDTO` hợp lệ (FX-001).

#### Test Steps:

1. `when(folioService.getFolio(1001)).thenReturn(mockFolio)`
2. Gọi hàm `byte[] result = pdfService.generateConsolidatedInvoice(1001)`
3. Kiểm tra kết quả trả về.

#### Expected Result (PASS):

- `result` không Null và độ dài `result.length > 0`.
- Nội dung luồng byte có chứa signature của PDF (ví dụ: `%PDF-`).

#### Expected Result (FAIL):

- Exception ném ra do template HTML bị rỗng.
- **Current Status**: 🟢 GREEN

---

### `MOD5-TC-002` — Gửi Email đính kèm mảng byte thành công

- **Severity**: CRITICAL
- **Feature Under Test**: `EmailNotificationService.sendInvoiceEmail()`
- **Test File**: `EmailNotificationServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:

- Cấu hình Mock `JavaMailSender`.

#### Test Steps:

1. Tạo một mảng `byte[] dummyPdf = new byte[]{1, 2, 3}`.
2. Gọi `emailService.sendInvoiceEmail("test@qa.com", dummyPdf)`.
3. Kiểm tra hành vi gọi tới `JavaMailSender.send()`.

#### Expected Result (PASS):

- Hàm `send()` của `JavaMailSender` được kích hoạt 1 lần (verify).
- Thuộc tính Attachment được gán thông qua `InputStreamSource` từ `dummyPdf`.
- **Current Status**: 🟢 GREEN

---

### `MOD5-TC-003` — Dừng gửi Email khi khách không có Email (MSG-22)

- **Severity**: MEDIUM
- **Feature Under Test**: `InvoiceEmailListener.handleCheckoutEvent()`
- **Test File**: `InvoiceEmailListenerTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:

- (Mock) Cả PDF Generator và Email Sender đều sẵn sàng.
- (FX-004) Event gửi đến chứa `email = null`.

#### Test Steps:

1. Gọi `listener.handleCheckoutEvent(new CheckoutCompletedEvent(1001, null, 1001))`.
2. Kiểm tra log hoặc Audit Log gọi `save(MSG-22)`.
3. Xác minh hàm `sendInvoiceEmail` KHÔNG BỊ GỌI.

#### Expected Result (PASS):

- `verify(emailService, never()).sendInvoiceEmail(...)`.
- DB lưu cảnh báo.
- **Current Status**: 🟢 GREEN

---

### `MOD5-TC-004` — Xử lý an toàn khi SMTP lỗi (MSG-23)

- **Severity**: HIGH
- **Feature Under Test**: `InvoiceEmailListener.handleCheckoutEvent()`
- **Test File**: `InvoiceEmailListenerTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:

- (Mock) `emailService.sendInvoiceEmail()` ép quăng lỗi `MailException`.

#### Test Steps:

1. Phát `CheckoutCompletedEvent` bình thường.
2. Kiểm tra luồng bắt lỗi `try-catch`.

#### Expected Result (PASS):

- Exception BỊ BẮT (Bị nuốt) và KHÔNG quăng ngược ra ngoài (để không làm sập ứng dụng).
- Audit Log ghi nhận `MSG-23`.
- **Current Status**: 🟢 GREEN

---

### `MOD5-TC-005` — Nội dung PDF phải chứa đủ trường chuẩn Bộ Tài chính (BR-21)

- **Severity**: HIGH
- **Legal**: BR-21 (Chuẩn biểu mẫu hóa đơn Bộ Tài chính)
- **Feature Under Test**: `PdfGeneratorService.generateConsolidatedInvoice()`
- **Test File**: `PdfGeneratorServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-005`

#### Preconditions:

- (Mock) `GuestFolioService` trả về FolioDTO có đầy đủ items (FX-001).
- (Mock) `GuestRepository` trả về Guest info (FX-005).

#### Test Steps:

1. Gọi `byte[] result = pdfService.generateConsolidatedInvoice(1001)`.
2. Chuyển `byte[]` thành String (extract text từ PDF bằng `PDFBox`).
3. Assert nội dung text chứa các trường bắt buộc.

#### Expected Result (PASS):

- Nội dung PDF chứa chuỗi `"Xoai Aura Retreat"` (Tên công ty).
- Nội dung PDF chứa chuỗi mã số thuế (ví dụ: `"MST: XXXXXXXXXX"`).
- Nội dung PDF chứa chuỗi `"VAT 10%"` hoặc `"Thuế GTGT"` tách riêng dòng.
- Nội dung PDF chứa bảng chi tiết dịch vụ (Room, Spa, F&B).

#### Expected Result (FAIL):

- Thiếu bất kỳ trường nào ở trên → vi phạm BR-21.
- **Current Status**: 🟢 GREEN

---

### `MOD5-TC-006` — PDF Generator phải query đủ 4 bảng nguồn dữ liệu gộp

- **Severity**: HIGH
- **Feature Under Test**: `PdfGeneratorService.generateConsolidatedInvoice()`
- **Test File**: `PdfGeneratorServiceTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-006`

#### Preconditions:

- (Mock) Tất cả Repository: `RoomBookingRepository`, `FolioItemRepository`, `GuestRepository`, `VillaRepository` (FX-006).

#### Test Steps:

1. Gọi `pdfService.generateConsolidatedInvoice(1001)`.
2. Dùng `verify()` kiểm tra Service có gọi đủ 4 Repository hay không.

#### Expected Result (PASS):

- `verify(roomBookingRepo, atLeastOnce()).findByFolioId(1001)` — Lấy thông tin đặt phòng.
- `verify(folioItemRepo, atLeastOnce()).findByFolioId(1001)` — Lấy chi tiết dịch vụ phát sinh (Room Charge, Spa, F&B).
- `verify(guestRepo, atLeastOnce()).findById(...)` — Lấy tên khách.
- `verify(villaRepo, atLeastOnce()).findById(...)` — Lấy số phòng, hạng phòng.

#### Expected Result (FAIL):

- Thiếu lời gọi đến bất kỳ Repository nào → Hóa đơn sẽ in thiếu thông tin gộp.
- **Current Status**: 🟢 GREEN

---

## INTEGRATION TEST CASES

### `MOD5-TC-INT-001` — Luồng end-to-end Checkout Trigger

- **Severity**: CRITICAL
- **Feature Under Test**: Full flow từ Event đến Ghi Log
- **Test File**: `InvoiceEmailIntegrationTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-005`

#### Preconditions:

- Spring Boot khởi chạy toàn bộ Context (`@SpringBootTest`).
- Mail Server giả lập (GreenMail) chạy port 3025.
- Cấu hình `@EnableAsync`.

#### Test Steps:

1. Spring ApplicationEventPublisher gọi `.publishEvent(new CheckoutCompletedEvent(1001, "test@local", 1001))`.
2. Dùng `Awaitility` chờ tối đa 5 giây (vì chạy bất đồng bộ).
3. Kiểm tra DB `audit_log` và Mock MailBox.

#### Expected Result (PASS):

- Mock Mailbox của `test@local` có 1 email mới.
- Email có Attachment tên `Invoice_1001.pdf` (hoặc tương đương).
- Bảng `audit_log` có 1 dòng `action_type = 'EMAIL_INVOICE'` và `status = 'SUCCESS'` cho Booking 1001.

#### Expected Result (FAIL):

- Không có email trong Mailbox → Listener không trigger.
- Không có dòng Audit Log → Bước ghi log bị bỏ sót (vi phạm BR-15, SRS Bước 6).
- **Current Status**: 🟢 GREEN

---

## 5. Red-Green-Refactor Tracker

| TC ID               | Test File                             | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :------------------ | :------------------------------------ | :--------------: | :---------------- | :--------------- |
| `MOD5-TC-001`     | `PdfGeneratorServiceTest.java`      |       [x]       | `DONE`        |                  |
| `MOD5-TC-002`     | `EmailNotificationServiceTest.java` |       [x]       | `DONE`        |                  |
| `MOD5-TC-003`     | `InvoiceEmailListenerTest.java`     |       [x]       | `DONE`        |                  |
| `MOD5-TC-004`     | `InvoiceEmailListenerTest.java`     |       [x]       | `DONE`        |                  |
| `MOD5-TC-005`     | `PdfGeneratorServiceTest.java`      |       [x]       | `DONE`        |                  |
| `MOD5-TC-006`     | `PdfGeneratorServiceTest.java`      |       [x]       | `DONE`        |                  |
| `MOD5-TC-INT-001` | `InvoiceEmailIntegrationTest.java`  |       [x]       | `DONE`        |                  |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)

- [X] Spec kỹ thuật `AM-MOD5-IMP-027` đã được review và approve.
- [X] Test fixtures (Section 3 TDS-05) đã được chuẩn bị.

### Exit Criteria (Điều kiện kết thúc — DoD)

- [x] `mvn test` — tất cả unit tests xanh (7/7 TC).
- [x] Có luồng kiểm tra Audit Log cả Happy Path và Error Path (BR-15).
- [x] Không có file PII lưu rác trên hệ thống (BR-21).
- [x] PDF output chứa đủ 6 trường bắt buộc BR-21 (Logo, MST, VAT, tổng bằng chữ...).

---

## 7. Rollback Plan

### Revert implementation files

```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/billing
git checkout -- src/test/java/com/AuraMoon/auramoon/billing
```
