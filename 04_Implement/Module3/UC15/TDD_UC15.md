# TEST-DRIVEN DEVELOPMENT SPECIFICATION: UC15

**Document ID:** HOS-SPA-TDD-015  
**Version:** 1.0  
**Date:** 2026-06-14  
**Status:** Approved  
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation  
**Author:** DuongLD - Senior Java Developer  
**Reviewed by:** [x] Tech Lead - Approved  
**DPO Sign-off:** [ ] Pending  
**Approved by:** [x] Principal Architect - Approved  
**Classification:** Internal - Confidential

**References:**
* `02_Requirement/Module3/FRS_Module3_SpaScheduling.md` — Functional requirements
* `04_Implement/Module3/UC15/EDS.md` — Technical Specification (EDS)
* `03_Design/Database/DB.sql` — Database Schema

---

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | DuongLD | Khởi tạo tài liệu - TDD spec cho UC15: Book Additional Spa Service |

---

## MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

---

## 1. Thông tin Module

| Field | Value |
|---|---|
| Feature / Gap ID | UC15 |
| Module | Spa & Therapy Scheduling Engine (`spa`) |
| Spec gốc | `HOS-SPA-IMP-015` (EDS) |
| Priority | 🔴 P0 / 🟠 P1 (Medium-High) |
| Sprint | S1 |
| Data Classification | Sensitive-PII (Booking & Billing Linkage) |
| Compliance Scope | Decree 356/2025 (Personal Data Protection) |
| Upstream Dependencies | `booking` (Booking status validation), `auth` (Receptionist session check) |
| Downstream Consumers | `billing` (Folio Item pending charge) |

---

## 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
|---|---|---|---|
| L1 | API sử dụng JSON `@RequestBody` để nhận dữ liệu booking | Dự án sử dụng Spring MVC Form POST truyền thống (`application/x-www-form-urlencoded`) | Test Controller sẽ sử dụng form parameters thay vì JSON body |
| L2 | Ghi nợ trực tiếp qua `FolioItemRepository` | Luật Vùng Cấm Bay cấm chạm vào repository của `billing` | Mock `BillingIntegrationService` để kiểm tra luồng nợ mà không gọi trực tiếp database của Billing |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi
Tài liệu kiểm thử hướng phát triển này bao phủ:
- **Unit Tests:** Kiểm thử độc lập `SpaManualBookingServiceImpl` bằng cách mock tất cả repositories và `BillingIntegrationService`.
- **Controller Tests:** Kiểm thử Spring MVC controller mapping cho endpoints GET và POST bằng MockMvc.
- **Integration Tests:** Kiểm thử tính nguyên tử của `@Transactional` đảm bảo rollback thành công cả dữ liệu Spa và Billing khi xảy ra lỗi đột ngột.

```
spa (Module) Test Coverage:
├── Service Layer: SpaManualBookingServiceImplTest.java (JUnit 5 + Mockito)
├── Controller Layer: SpaReceptionistControllerTest.java (MockMvc)
└── Integration Layer: SpaManualBookingIntegrationTest.java (Spring Boot Test + Transaction rollback verification)
```

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
|---|---|
| FRS Section 4.5 (UC15) | Toàn bộ luồng nghiệp vụ đặt Spa ngoài gói bởi Lễ tân |
| BR-04 | Logic đồng thời Therapist + Room trống tại thời điểm đặt (Pessimistic Lock) |
| BR-11 | Ghi nợ Folio Item dưới dạng 'Pending', danh mục 'Extra Spa' |
| BR-VAL-01 | Chỉ chấp nhận đặt lịch cho Booking có trạng thái 'Checked-In' |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-015-01 | Đặt lịch thành công khi Guest Checked-In & tài nguyên trống | `SpaManualBookingService.bookAdditionalService` | `SPA-TC-015-01` |
| TC-COND-015-02 | Từ chối đặt lịch khi Guest chưa checked-in | `SpaManualBookingService.bookAdditionalService` | `SPA-TC-015-02` |
| TC-COND-015-03 | Từ chối đặt lịch khi không có Therapist hoặc Room trống | `SpaManualBookingService.bookAdditionalService` | `SPA-TC-015-03` |
| TC-COND-015-04 | Đảm bảo Rollback toàn bộ khi Billing Integration lỗi | `@Transactional SpaManualBookingService.bookAdditionalService` | `SPA-TC-015-04` |
| TC-COND-015-05 | Phân quyền: Từ chối GUEST và THERAPIST truy cập | `SpaReceptionistController` endpoints | `SPA-TC-015-SEC-01` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử
- **Equivalence Partitioning:** Chia trạng thái booking của Guest thành 2 phân vùng tương đương: Checked-In (Hợp lệ) và Confirmed/Checked-Out/Cancelled (Không hợp lệ).
- **State Transition Testing:** Kiểm thử chuyển đổi trạng thái: một Spa Booking tạo mới sẽ chuyển ngay sang trạng thái `Scheduled`.
- **Error Guessing:** Mô phỏng lỗi ngắt kết nối mạng hoặc treo DB của Billing Integration Service giữa chừng để xác nhận rollback.

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
|---|---|---|---|
| FX-001 | Booking | `{ id: 10, guestId: 1, bookingStatus: 'Checked-In' }` | Guest hợp lệ để đặt thêm Spa |
| FX-002 | Booking | `{ id: 11, guestId: 1, bookingStatus: 'Confirmed' }` | Guest chưa check-in (lỗi) |
| FX-003 | Service | `{ id: 5, treatmentCode: 'SW01', price: 500000, durationMinutes: 60 }` | Dịch vụ Spa ngoài gói |
| FX-004 | Session | `HttpSession` chứa attribute `"role" = "RECEPTIONIST"`, `"userId" = 2` | Quyền Lễ tân hợp lệ |
| FX-005 | Session | `HttpSession` chứa attribute `"role" = "GUEST"`, `"userId" = 1` | Quyền Khách hàng (không hợp lệ) |

---

## 4. Test Case Specification

### SPA-TC-015-01 — Happy Path: Đặt dịch vụ thành công cho Guest Checked-In, tài nguyên trống

*   **Severity:** CRITICAL
*   **Feature Under Test:** `SpaManualBookingServiceImpl.bookAdditionalService(SpaScheduleRequest, Integer)`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/service/SpaManualBookingServiceTest.java`
*   **TDD Phase:** 🟢 GREEN — đã pass
*   **Condition Ref:** TC-COND-015-01

**Preconditions:**
*   Booking FX-001 trạng thái 'Checked-In' tồn tại trong DB.
*   Service FX-003 tồn tại trong DB.
*   Tài khoản GuestFolio liên kết với Booking FX-001 có `folioId = 100`.
*   Có ít nhất 1 Room và 1 Therapist khả dụng trong khoảng thời gian mong muốn.

**Test Steps (Arrange - Act - Assert):**
1.  **Arrange:**
    *   Mock `bookingRepository.findById(10)` trả về `Booking` (status = 'Checked-In').
    *   Mock `treatmentServiceRepository.findById(5)` trả về `TreatmentService` (price = 500000, duration = 60).
    *   Mock `billingService.findFolioIdByBookingId(10)` trả về `Optional.of(100)`.
    *   Mock `roomRepository.findAvailableRoomsWithLock(startTime, endTime)` trả về danh sách có chứa `TreatmentRoom`.
    *   Mock `therapistRepository.findAvailableTherapistsWithLock(startTime, endTime)` trả về danh sách có chứa `Therapist`.
    *   Mock `treatmentBookingRepository.save(any(TreatmentBooking.class))` trả về saved entity với ID = 50.
    *   Mock `scheduleRepository.save(any(Schedule.class))` trả về saved entity.
2.  **Act:**
    *   Gọi `spaManualBookingService.bookAdditionalService(request, receptionistUserId)`.
3.  **Assert:**
    *   Xác nhận trả về `SpaScheduleResponse` không null, chứa `scheduleId` và các mã tài nguyên chính xác.
    *   Xác nhận `billingService.createFolioItem` được gọi đúng 1 lần với: `folioId = 100`, `referenceId = 50`, `serviceCategory = "Extra Spa"`, `amount = 500000`.
    *   Xác nhận `treatmentBookingRepository.save` lưu một booking có `status = "Scheduled"`.

**Expected Result (PASS):**
*   Trả về response thành công, các repository và service tích hợp được kích hoạt đúng tham số.

**Expected Result (FAIL):**
*   Không ghi nợ Folio thành công hoặc không gán đúng Room/Therapist.

**Current Status:** 🟢 Passing

---

### SPA-TC-015-02 — Error Path 1: Từ chối đặt lịch do Guest chưa Checked-In (Ví dụ: Confirmed)

*   **Severity:** HIGH
*   **Feature Under Test:** `SpaManualBookingServiceImpl.bookAdditionalService(SpaScheduleRequest, Integer)`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/service/SpaManualBookingServiceTest.java`
*   **TDD Phase:** 🟢 GREEN — đã pass
*   **Condition Ref:** TC-COND-015-02

**Preconditions:**
*   Booking FX-002 trạng thái 'Confirmed' tồn tại trong DB.

**Test Steps:**
1.  **Arrange:**
    *   Mock `bookingRepository.findById(11)` trả về `Booking` (status = 'Confirmed').
    *   Mock `treatmentServiceRepository.findById(5)` trả về `TreatmentService`.
2.  **Act & Assert:**
    *   Thực thi hàm và xác nhận ném ra ngoại lệ `SpaBusinessException` có `errorCode` là `"SPA-002"`.
    *   Verify `treatmentBookingRepository.save` và `scheduleRepository.save` **không bao giờ** được gọi.
    *   Verify `billingService.createFolioItem` **không bao giờ** được gọi.

**Expected Result (PASS):**
*   Exception `SpaBusinessException("SPA-002", ...)` được ném ra. Không có thay đổi dữ liệu nào.

**Current Status:** 🟢 Passing

---

### SPA-TC-015-03 — Error Path 2: Từ chối đặt lịch do Hết Therapist hoặc Hết Phòng khả dụng

*   **Severity:** HIGH
*   **Feature Under Test:** `SpaManualBookingServiceImpl.bookAdditionalService(SpaScheduleRequest, Integer)`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/service/SpaManualBookingServiceTest.java`
*   **TDD Phase:** 🟢 GREEN — đã pass
*   **Condition Ref:** TC-COND-015-03

**Preconditions:**
*   Booking FX-001 'Checked-In' tồn tại trong DB.
*   Không có Therapist rảnh trong khung giờ yêu cầu.

**Test Steps:**
1.  **Arrange:**
    *   Mock `bookingRepository.findById(10)` trả về `Booking` (status = 'Checked-In').
    *   Mock `treatmentServiceRepository.findById(5)` trả về `TreatmentService`.
    *   Mock `billingService.findFolioIdByBookingId(10)` trả về `Optional.of(100)`.
    *   Mock `roomRepository.findAvailableRoomsWithLock(startTime, endTime)` trả về danh sách chứa phòng trống.
    *   Mock `therapistRepository.findAvailableTherapistsWithLock(startTime, endTime)` trả về **List rỗng** `Collections.emptyList()` (Hết chuyên viên).
2.  **Act & Assert:**
    *   Thực thi hàm và xác nhận ném ra ngoại lệ `SpaBusinessException` với `errorCode` là `"SPA-010"`.
    *   Verify `treatmentBookingRepository.save` và `billingService.createFolioItem` **không bao giờ** được gọi (chặn sớm).

**Expected Result (PASS):**
*   Hệ thống ném ngoại lệ `"SPA-010"`. Không lưu dữ liệu rác.

**Current Status:** 🟢 Passing

---

### SPA-TC-015-04 — Error Path 3: Rollback toàn bộ giao dịch khi ghi nợ Billing thất bại

*   **Severity:** CRITICAL
*   **Feature Under Test:** `@Transactional SpaManualBookingServiceImpl.bookAdditionalService`
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/service/SpaManualBookingServiceTest.java` (Sử dụng Spring `@SpringBootTest` để xác nhận rollback giao dịch trên thực tế)
*   **TDD Phase:** 🟢 GREEN — đã pass
*   **Condition Ref:** TC-COND-015-04

**Preconditions:**
*   Guest đã checked-in, Therapist và Room trống.
*   `BillingIntegrationService` được mock để cố ý ném lỗi `RuntimeException("Database Connection Failed")` khi tạo Folio Item.

**Test Steps:**
1.  **Arrange:**
    *   Thiết lập mock cho các điều kiện ban đầu thành công.
    *   Thiết lập `doThrow(new RuntimeException("Billing Fail")).when(billingService).createFolioItem(any(), any(), any(), any(), any(), any());`
2.  **Act & Assert:**
    *   Gọi hàm `bookAdditionalService` và bắt ngoại lệ trả về.
    *   Kiểm tra Database thực tế: Xác nhận **không tồn tại** bản ghi `TREATMENT_BOOKING` hay `SCHEDULE` nào được lưu cho yêu cầu này (toàn bộ transaction được rollback tự động).

**Expected Result (PASS):**
*   Giao dịch Spa bị rollback hoàn toàn, không lưu thông tin lịch nửa chừng, ném ra ngoại lệ nghiệp vụ hệ thống `"SPA-019"`.

**Current Status:** 🟢 Passing

---

### SECURITY TEST CASES

#### SPA-TC-015-SEC-01 — Kiểm tra phân quyền truy cập endpoint manual booking

*   **Severity:** HIGH
*   **Feature Under Test:** `SpaReceptionistController` endpoints (`GET /booking-spa/manual`, `POST /booking-spa/manual`)
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/controller/SpaReceptionistControllerTest.java`
*   **TDD Phase:** 🔴 RED — chưa implement
*   **Condition Ref:** TC-COND-015-05

**Preconditions:**
*   Sử dụng Spring MockMvc để giả lập session.

**Test Steps (Attack Simulation):**
1.  Thiết lập MockMvc session giả lập user có role là `GUEST` (FX-005) hoặc `THERAPIST`.
2.  Thực hiện request `GET /booking-spa/manual` và `POST /booking-spa/manual`.
3.  Kiểm tra response status code.

**Expected Result (PASS):**
*   Trả về mã lỗi HTTP `403 Forbidden` hoặc chuyển hướng về trang báo lỗi quyền truy cập.

**Expected Result (FAIL):**
*   Trả về HTTP `200 OK` hoặc `302 Redirect` thực hiện nghiệp vụ, vi phạm phân quyền RBAC.

**Current Status:** 🔴 Not written

---

### INTEGRATION TEST CASES

#### SPA-TC-015-INT-01 — Integration Test: Luồng nguyên tử trọn vẹn của Lễ tân đặt dịch vụ ngoài gói

*   **Severity:** HIGH
*   **Feature Under Test:** Đặt phòng Checked-In -> Xem slots khả dụng -> Lễ tân POST Form -> Ghi nhận Spa Booking, Schedule & Folio Item
*   **Test File:** `src/test/java/com/AuraMoon/auramoon/spa/controller/SpaManualBookingIntegrationTest.java`
*   **TDD Phase:** 🔴 RED — chưa implement
*   **Condition Ref:** TC-COND-015-01, TC-COND-015-04

**Preconditions:**
*   Database H2/Real SQL test instance đang hoạt động.
*   Đã nạp sẵn dữ liệu seed (Checked-In Booking, Guest Folio, 1 Room hoạt động, 1 Therapist hoạt động).

**Test Steps:**
1.  Gửi yêu cầu HTTP GET `/booking-spa/available-slots` để đảm bảo tìm thấy khung giờ 15:00 rảnh.
2.  Gửi yêu cầu HTTP POST `/booking-spa/manual` với các tham số form-urlencoded:
    *   `bookingId = 1`, `serviceId = 2`, `startTime = 2026-06-15T15:00:00`, `note = Integration Test`
    *   Sử dụng session có quyền `RECEPTIONIST`.
3.  Xác nhận response trả về mã `302 Found`, chuyển hướng về trang `/booking-spa/manual` kèm Flash Attribute `successMessage`.
4.  Truy vấn trực tiếp Database để xác nhận:
    *   Một bản ghi `TREATMENT_BOOKING` có `status = 'Scheduled'` và `folio_id` khớp với Folio hiện tại.
    *   Một bản ghi `SCHEDULE` có giờ bắt đầu = 15:00 và gán đúng Room + Therapist trống.
    *   Một bản ghi `FOLIO_ITEM` có `service_category = 'Extra Spa'`, `amount` khớp giá dịch vụ, và `status = 'Pending'`.

**Expected Result (PASS):**
*   Tất cả dữ liệu được lưu đúng ở cả 3 bảng trong DB thực tế, response chuyển hướng thành công.

**Current Status:** 🔴 Not written

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
|---|---|---|---|---|
| `SPA-TC-015-01` | `SpaManualBookingServiceTest.java` | [x] | [x] | |
| `SPA-TC-015-02` | `SpaManualBookingServiceTest.java` | [x] | [x] | |
| `SPA-TC-015-03` | `SpaManualBookingServiceTest.java` | [x] | [x] | |
| `SPA-TC-015-04` | `SpaManualBookingServiceTest.java` | [x] | [x] | |
| `SPA-TC-015-SEC-01`| `SpaReceptionistControllerTest.java` | [ ] | [ ] | (Controller layer) |
| `SPA-TC-015-INT-01`| `SpaManualBookingIntegrationTest.java` | [ ] | [ ] | (Integration layer) |

---

## 6. Entry / Exit Criteria

**Entry Criteria (Điều kiện bắt đầu)**
- [ ] Bản thiết kế kỹ thuật (EDS) cho UC15 đã được phê duyệt.
- [ ] Interface `BillingIntegrationService` và `SpaManualBookingService` được định nghĩa.
- [ ] Test template đã được review.

**Exit Criteria (Điều kiện kết thúc — DoD)**
- [ ] Chạy thành công toàn bộ Unit/Integration tests cho UC15 với trạng thái xanh (Passed 100%).
- [ ] Test coverage cho các file service/controller mới tạo đạt tối thiểu 85% lines.
- [ ] Đảm bảo transaction rollback hoạt động hoàn hảo, không có record rác sinh ra khi lỗi billing.
- [ ] Quy tắc phân quyền RBAC được thực thi chặt chẽ, Lễ tân đặt lịch được và Khách hàng bị chặn.

---

## 7. Rollback Plan

```bash
# Revert toàn bộ file code thay đổi về trạng thái ban đầu của Git
git checkout -- src/main/java/com/AuraMoon/auramoon/spa/
git checkout -- src/test/java/com/AuraMoon/auramoon/spa/

# Làm sạch thư mục build
mvn clean
```
