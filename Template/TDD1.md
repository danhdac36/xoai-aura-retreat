# TEST-DRIVEN DEVELOPMENT SPECIFICATION
# Đặc tả Kiểm thử Hướng Phát triển - Module 2: Đặt Gói Trị Liệu & Phòng Ở

**Document ID:** `AURAMOON-BOOKING-TDD-002`
**Version:** 1.0
**Date:** 2026-06-11
**Status:** Approved
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Antigravity AI Assistant
**Reviewed by:** Tech Lead / Principal Architect
**DPO Sign-off:** [x] Approved - 2026-06-11
**Approved by:** Principal Architect
**Classification:** Internal – Confidential

**References:**
- [EDS1.md](file:///d:/su26-swp391-se2023-g6/Template/EDS1.md) — Technical Specification cho Module 2
- [ke_hoach_trien_khai_module_2.md](file:///d:/su26-swp391-se2023-g6/01_SRS/ke_hoach_trien_khai_module_2.md) — Kế hoạch triển khai tích hợp
- Luật Cư trú 2020 & Nghị định 13/2023/NĐ-CP (Bảo vệ dữ liệu cá nhân)

> **Quy ước TDD:** Các kịch bản kiểm thử phải được viết và cấu hình dưới dạng mã kiểm thử JUnit 5 (Sử dụng Mockito cho Unit Test để tối ưu thời gian chạy) TRƯỚC khi viết mã nguồn thực hiện tính năng.

---

# CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-11 | Antigravity | Khởi tạo tài liệu TDD cho Module 2 lần đầu |

---

# MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification (Kịch bản kiểm thử chi tiết)
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

---

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `GAP-BOOKING-02` |
| **Module** | Đặt Gói Trị Liệu & Phòng Ở (Booking & Stay Management) |
| **Spec gốc** | [EDS1.md](file:///d:/su26-swp391-se2023-g6/Template/EDS1.md) |
| **Priority** | 🔴 P0 (Critical cho core booking & bảo mật định danh) |
| **Sprint** | Sprint 2 |
| **Data Classification** | `Sensitive-PII` |
| **Compliance Scope** | Luật Cư trú 2020 / Nghị định 13/2023/NĐ-CP |
| **Upstream Dependencies** | `AuthContext` (Quản lý User & Role) |
| **Downstream Consumers** | `SpaContext`, `FnbContext`, `BillingContext` |

---

# 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | Lễ tân có thể gán bất kỳ phòng nào đang trống khi Check-in. | Cần đảm bảo gán phòng vật lý trùng khớp với loại biệt thự (`villa_type`) khách đặt. | Viết test kiểm tra: Hệ thống chặn check-in và ném ngoại lệ nếu loại phòng gán không khớp loại phòng trong đơn đặt phòng. |
| **L2** | Lưu trữ thông tin số định danh (CCCD/Passport) dạng text thuần. | Nghị định 13/2023 bắt buộc bảo mật PII tại chỗ (at-rest encryption). | Kịch bản kiểm thử xác nhận thông tin CCCD lưu xuống CSDL đã được mã hóa đối xứng AES-256. |

---

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi
Tài liệu TDD này bao gồm kiểm thử tầng Service logic của Module 2 sử dụng Mockito để cô lập các phụ thuộc ngoài (Database, API ngoài).

```text
com.AuraMoon.auramoon.booking.service
├── BookingServiceImpl (SUT - Mock dependencies)
├── CheckInServiceImpl (SUT - Mock dependencies)
└── VillaServiceImpl (SUT - Mock dependencies)
```

## TDS-02 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **COND-BOOK-001** | Đặt phòng thành công (Đủ phòng trống) | `BookingService.createBooking` | `AURAMOON-TC-001` |
| **COND-BOOK-002** | Đặt phòng thất bại do hết Villa loại đó | `BookingService.createBooking` | `AURAMOON-TC-002` |
| **COND-CHECK-001**| Check-in hợp lệ, gán phòng và mã hóa CCCD | `CheckInService.performCheckIn` | `AURAMOON-TC-003` |
| **COND-CHECK-002**| Check-in lỗi do loại phòng gán không khớp đơn đặt | `CheckInService.performCheckIn` | `AURAMOON-TC-004` |
| **COND-VILLA-001**| Cập nhật trạng thái Villa vật lý | `VillaService.updateVillaStatus` | `AURAMOON-TC-005` |
| **COND-ITIN-001** | Tạo dòng thời gian trải nghiệm tổng hợp | `ItineraryService.getTimelineForGuest`| `AURAMOON-TC-006` |

---

# 4. Test Case Specification (Kịch bản kiểm thử chi tiết)

## AURAMOON-TC-001 — createBooking_villasAvailable_savesSuccessfully

- **Severity:** HIGH
- **Feature Under Test:** `BookingServiceImpl.createBooking()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/BookingServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-BOOK-001`

**Preconditions:**
- Gói trị liệu tồn tại và hoạt động (thời lượng 3 ngày).
- Còn phòng trống thuộc loại Villa được chọn.

**Test Steps (Arrange / Act / Assert):**
1. *Arrange:* Mock `RetreatPackageRepository` trả về thông tin gói. Mock `VillaRepository` trả về danh sách phòng trống của loại biệt thự đó.
2. *Act:* Gọi `bookingService.createBooking(guestId, request)`.
3. *Assert:* Đơn đặt phòng được khởi tạo có trạng thái `PENDING`, trạng thái thanh toán `UNPAID`. Ngày checkout bằng `checkinDate + durationDays` của gói.

**Expected Result (PASS):** Trả về DTO thông tin đặt phòng chính xác, không ném ngoại lệ.

---

## AURAMOON-TC-002 — createBooking_noVillasAvailable_throwsBook002

- **Severity:** HIGH
- **Feature Under Test:** `BookingServiceImpl.createBooking()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/BookingServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-BOOK-002`

**Preconditions:**
- Loại phòng đã hết phòng trống trong khoảng thời gian khách muốn đặt.

**Test Steps:**
1. *Arrange:* Mock `VillaRepository` trả về danh sách trống khi kiểm tra phòng.
2. *Act:* Gọi `bookingService.createBooking(guestId, request)`.
3. *Assert:* Kiểm tra xem hệ thống có ném ra `BusinessException` với mã lỗi `BOOK-002` hay không.

**Expected Result (PASS):** Ném lỗi `BusinessException` với mã lỗi `BOOK-002`.

---

## AURAMOON-TC-003 — checkIn_validRequest_updatesStatusesAndEncryptsID

- **Severity:** CRITICAL
- **Feature Under Test:** `CheckInServiceImpl.performCheckIn()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/CheckInServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-CHECK-001`

**Preconditions:**
- Đơn đặt phòng có trạng thái `Confirmed`.
- Phòng Villa vật lý gán vào ở trạng thái trống (`Available`) và cùng loại với Villa đã đặt.

**Test Steps:**
1. *Arrange:* Mock `BookingRepository` trả về thông tin Booking. Mock `VillaRepository` trả về Villa trống phù hợp. Cấu hình khóa AES cho môi trường kiểm thử.
2. *Act:* Gọi `checkInService.performCheckIn(bookingId, "012345678901", villaId)`.
3. *Assert:* 
   - Kiểm tra xem trạng thái của Booking chuyển sang `Checked-In`.
   - Trạng thái của Villa vật lý chuyển sang `Occupied`.
   - Số CCCD được gửi để lưu vào cơ sở dữ liệu KHÔNG phải là chuỗi thô `"012345678901"` mà là chuỗi đã được mã hóa bằng AES-256.

**Expected Result (PASS):** Trạng thái cập nhật đúng và dữ liệu CCCD được mã hóa thành công trước khi ghi DB.

---

## AURAMOON-TC-004 — checkIn_invalidVillaType_throwsException

- **Severity:** HIGH
- **Feature Under Test:** `CheckInServiceImpl.performCheckIn()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/CheckInServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-CHECK-002`

**Preconditions:**
- Villa được gán có loại phòng (`villa_type`) khác với loại phòng đã đặt cọc trong đơn đặt phòng.

**Test Steps:**
1. *Arrange:* Mock đơn đặt phòng yêu cầu loại phòng "Ocean Villa". Mock phòng gán thực tế thuộc loại "Garden Villa".
2. *Act:* Gọi `checkInService.performCheckIn(bookingId, "012345678901", invalidVillaId)`.
3. *Assert:* Kiểm tra hệ thống ném ra `BusinessException` hoặc ngoại lệ xử lý lỗi gán phòng không khớp.

**Expected Result (PASS):** Hệ thống chặn check-in và ném ra lỗi ngoại lệ thiết lập sai loại phòng.

---

## AURAMOON-TC-005 — updateVillaStatus_validRequest_updatesSuccessfully

- **Severity:** MEDIUM
- **Feature Under Test:** `VillaServiceImpl.updateVillaStatus()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/VillaServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-VILLA-001`

**Preconditions:**
- Villa tồn tại trong hệ thống.

**Test Steps:**
1. *Arrange:* Mock `VillaRepository` tìm thấy Villa hợp lệ.
2. *Act:* Gọi `villaService.updateVillaStatus(villaId, "Maintenance")`.
3. *Assert:* Trạng thái Villa được cập nhật sang `Maintenance` và lưu thành công.

---

## AURAMOON-TC-006 — getItineraryTimeline_validBooking_returnsAggregatedTimeline

- **Severity:** HIGH
- **Feature Under Test:** `ItineraryServiceImpl.getTimelineForGuest()`
- **Test File:** `src/test/java/com/AuraMoon/auramoon/booking/service/ItineraryServiceImplTest.java`
- **TDD Phase:** 🔴 RED
- **Condition Ref:** `COND-ITIN-001`

**Preconditions:**
- Khách hàng đã check-in và có lịch trị liệu Spa, ăn uống đi kèm.

**Test Steps:**
1. *Arrange:* Mock thông tin lịch trình cư trú của khách hàng, mock danh sách dịch vụ Spa của Module 3, mock thực đơn ăn uống của Module 4.
2. *Act:* Gọi `itineraryService.getTimelineForGuest(guestId)`.
3. *Assert:* Trả về danh sách các sự kiện được sắp xếp đúng thứ tự thời gian tăng dần (Check-in -> Ăn trưa -> Trị liệu -> Ăn tối...).

---

# 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | --- | --- | --- |
| `AURAMOON-TC-001` | `BookingServiceImplTest.java` | `[ ]` | `[pending]` | |
| `AURAMOON-TC-002` | `BookingServiceImplTest.java` | `[ ]` | `[pending]` | |
| `AURAMOON-TC-003` | `CheckInServiceImplTest.java` | `[ ]` | `[pending]` | |
| `AURAMOON-TC-004` | `CheckInServiceImplTest.java` | `[ ]` | `[pending]` | |
| `AURAMOON-TC-005` | `VillaServiceImplTest.java` | `[ ]` | `[pending]` | |
| `AURAMOON-TC-006` | `ItineraryServiceImplTest.java` | `[ ]` | `[pending]` | |

---

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)
- [x] Tài liệu kỹ thuật [EDS1.md](file:///d:/su26-swp391-se2023-g6/Template/EDS1.md) được duyệt.
- [x] Khai báo các class kiểm thử và test method ở trạng thái RED (chưa implement).

## Exit Criteria (Điều kiện kết thúc — Definition of Done)
- [ ] Maven test chạy thành công 100% không có lỗi (`mvn test` thành công).
- [ ] Độ bao phủ kiểm thử (Test coverage) cho code mới đạt tối thiểu 80%.
- [ ] Dữ liệu định danh CCCD/Passport được kiểm chứng mã hóa an toàn khi lưu trữ.

---

# 7. Rollback Plan

```bash
# Revert các thay đổi đối với file test trong Git
git checkout -- src/test/java/com/AuraMoon/auramoon/booking/
```
