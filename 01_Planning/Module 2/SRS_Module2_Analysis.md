# PHÂN TÍCH MODULE 2 — Retreat Package & Accommodation Booking

**Document ID:** SWP391-HOS-03 / MODULE-2-SRS-ANALYSIS
**Version:** 1.0
**Date:** 2026-06-19
**Dựa trên:**
- `02_Requirement/SRS_Document.md`
- `08_Document_References/Template/SWP391-HOS-03.md`
**Phụ trách:** Student 2

---

## 1. Tổng quan Module

| Mục | Nội dung |
|---|---|
| **Tên Module** | Retreat Package & Accommodation Booking |
| **Project Code** | SWP391-HOS-03 |
| **Bounded Context** | `booking` |
| **Assigned to** | Student 2 (Full-stack: DB → DAO → Service → Controller → UI) |
| **Upstream (phụ thuộc vào)** | Module 1 — Authentication (JWT, Session, Role) |
| **Downstream (cung cấp dữ liệu cho)** | Module 3 (Spa), Module 4 (F&B), Module 5 (Billing/Checkout) |
| **Data Classification** | PII / Sensitive-PII (CCCD/Passport) |

---

## 2. Use Cases thuộc Module 2

### 2.1 Danh sách Use Cases

| UC ID | Tên Use Case | Actor chính | Feature | Độ phức tạp |
|:---:|---|---|---|:---:|
| **UC06** | Browse Wellness Packages | Guest | Package Browsing | Thấp |
| **UC07** | Book Wellness Package & Pay Deposit | Guest, Payment Gateway | Booking & Payment | **Cao** |
| **UC08** | Check In Guest | Receptionist | Reception Management | **Cao** |
| **UC09** | Manage Villa Status | Receptionist | Villa Management | Trung bình |
| **UC10** | View Booking Details & Itinerary Timeline | Guest | Booking Tracking | Trung bình |

> ⚠️ **Lưu ý:** UC23 (Submit Review) **KHÔNG** thuộc Module 2. UC23 thuộc **Module 5** (Student 5).

---

### 2.2 UC06 — Browse Wellness Packages

| Mục | Nội dung |
|---|---|
| **Primary Actor** | Guest |
| **Trigger** | Guest vào trang danh sách gói nghỉ dưỡng |
| **Preconditions** | Gói nghỉ dưỡng tồn tại và đang Active |
| **Postconditions** | Guest thấy danh sách gói phù hợp |

**Normal Flow:**
1. Guest mở trang Retreat Packages
2. System hiển thị danh sách gói đang Active
3. Guest lọc theo mục tiêu: `Weight Loss`, `Stress Relief`, `Yoga`, `Detox`, `Mindfulness`
4. System cập nhật danh sách theo bộ lọc
5. Guest xem chi tiết từng gói

**Màn hình liên quan:**
- `Packages List` (Guest)
- `Package Detail` (Guest)

---

### 2.3 UC07 — Book Wellness Package & Pay Deposit ⭐ CORE

| Mục | Nội dung |
|---|---|
| **Primary Actor** | Guest |
| **Secondary Actor** | Payment Gateway (VNPay) |
| **Trigger** | Guest nhấn "Book Now" |
| **Preconditions** | Guest đã authenticated, Gói Active, Villa inventory tồn tại |
| **Postconditions** | Booking tạo thành công, Deposit lưu, GuestFolio khởi tạo, Audit log ghi |

**Normal Flow:**
1. Guest mở trang Retreat Package
2. System hiển thị danh sách gói khả dụng
3. Guest chọn gói
4. Guest chọn ngày check-in
5. Guest chọn **Villa Type** — chỉ chọn *Type*, KHÔNG gán villa cụ thể *(BR-02)*
6. System kiểm tra tồn kho villa type trong khoảng ngày
7. System tính chi phí và tiền đặt cọc
8. Guest xác nhận booking
9. System redirect sang Payment Gateway (VNPay)
10. Guest hoàn tất thanh toán đặt cọc
11. VNPay trả kết quả thành công về callback
12. System xác nhận booking: `bookingStatus = CONFIRMED`, `paymentStatus = DEPOSITED` *(BR-01)*
13. System tạo Itinerary cơ bản
14. System ghi audit log *(BR-15)*
15. System hiển thị thông báo thành công (MSG-04)

**Alternative Flows:**
- A1: Guest thay đổi gói → System tính lại giá
- A2: Inventory thay đổi → System refresh availability

**Exception Flows:**
- E1: Thanh toán thất bại → System hiển thị MSG-05, booking vẫn PENDING
- E2: Timeout → Reservation giữ ở trạng thái PENDING

**Business Rules áp dụng:**
- `BR-01`: Booking chỉ CONFIRMED sau khi deposit thành công
- `BR-02`: Guest chỉ chọn VillaType; Receptionist gán villa cụ thể lúc check-in
- `BR-15`: Audit log bắt buộc

---

### 2.4 UC08 — Check In Guest ⭐ CORE + LEGAL

| Mục | Nội dung |
|---|---|
| **Primary Actor** | Receptionist |
| **Trigger** | Receptionist xử lý check-in cho khách đến |
| **Preconditions** | Booking tồn tại và `CONFIRMED`, Payment đã DEPOSITED, Villa available |
| **Postconditions** | `bookingStatus = CHECKED_IN`, Villa `OCCUPIED`, CCCD mã hóa và lưu |

**Normal Flow:**
1. Receptionist mở Receptionist Dashboard
2. System hiển thị danh sách Expected Arrivals hôm nay
3. Receptionist chọn booking cần check-in
4. System hiển thị Check-in Form
5. Receptionist gán một Villa vật lý cụ thể (có sẵn và cùng VillaType đã book)
6. Receptionist thu thập CCCD/Passport của khách
7. System **mã hóa** thông tin CCCD/Passport trước khi lưu DB *(BR-09, ADR-001)*
8. System cập nhật `booking.assignedVilla = villa`, `bookingStatus = CHECKED_IN`
9. System cập nhật `villa.status = OCCUPIED` *(BR-03)*
10. System ghi audit log *(BR-15)*

**Constraint (Ràng buộc pháp lý):**
> Thông tin CCCD/Passport phải được **mã hóa AES-256** trước khi lưu, phục vụ khai báo tạm trú theo **Luật Cư trú 2020**.
> Receptionist **KHÔNG ĐƯỢC** xem thông tin sức khỏe (health profile) của khách *(BR-07)*.

**Business Rules áp dụng:**
- `BR-03`: Villa MAINTENANCE không được gán cho khách
- `BR-07`: Receptionist chỉ xem thông tin cần thiết, không xem health records
- `BR-09`: Mã hóa Sensitive-PII bắt buộc
- `BR-14`: Thu thập CCCD để khai báo tạm trú
- `BR-15`: Audit log bắt buộc

---

### 2.5 UC09 — Manage Villa Status

| Mục | Nội dung |
|---|---|
| **Primary Actor** | Receptionist |
| **Trigger** | Cần thay đổi trạng thái villa |

**Villa Status FSM (Finite State Machine):**
```
AVAILABLE → OCCUPIED        (sau Check-in)
OCCUPIED  → NEEDS_CLEANING  (sau Check-out)
NEEDS_CLEANING → AVAILABLE  (sau khi dọn dẹp xong)
AVAILABLE → MAINTENANCE     (báo hỏng)
MAINTENANCE → AVAILABLE     (sửa xong)
```

**Constraint:**
- Villa đang `MAINTENANCE` hoặc `NEEDS_CLEANING` không được gán cho booking mới *(BR-03)*

---

### 2.6 UC10 — View Booking Details & Itinerary Timeline

| Mục | Nội dung |
|---|---|
| **Primary Actor** | Guest |
| **Trigger** | Guest mở trang booking của mình |

**Dữ liệu hiển thị (cross-module aggregation):**
| Nguồn | Dữ liệu |
|---|---|
| Module 2 (own) | Booking info, Villa info, Check-in/out dates |
| Module 3 | Spa/Therapy schedule |
| Module 4 | Meal plan (F&B) |
| Module 5 | Billing timeline, GuestFolio |

> **Lưu ý:** Module 2 chỉ cần expose API lấy booking cơ bản. Itinerary tổng hợp do Module 5 xử lý.

---

## 3. Business Rules (Module 2)

| BR ID | Nội dung | Áp dụng trong |
|:---:|---|---|
| **BR-01** | Booking chỉ được CONFIRMED sau khi deposit thành công | UC07 |
| **BR-02** | Guest chọn VillaType; Receptionist gán Villa cụ thể lúc check-in | UC07, UC08 |
| **BR-03** | Không double-booking villa; MAINTENANCE không được gán | UC08, UC09 |
| **BR-07** | RBAC — Receptionist không xem health records | UC08 |
| **BR-09** | Mã hóa AES-256 CCCD/Passport trước khi lưu DB | UC08 |
| **BR-12** | Khách không thể checkout khi còn nợ Spa/F&B | UC10 (liên kết) |
| **BR-14** | Thu thập CCCD/Passport để khai báo tạm trú (Luật Cư trú 2020) | UC08 |
| **BR-15** | Audit log bắt buộc cho mọi hành động quan trọng | UC07, UC08 |

---

## 4. Compliance (Tuân thủ pháp lý)

### 4.1 Luật Cư trú 2020 (Luật số 68/2020/QH14)
- **Áp dụng tại:** UC08 (Check-in)
- **Yêu cầu:** Phải thu thập và lưu trữ thông tin định danh (CCCD/Passport) của tất cả khách lưu trú
- **Mục đích:** Cho phép xuất báo cáo khai báo tạm trú hàng ngày cho cơ quan chức năng
- **Implementation:** `identifyCode` được mã hóa bằng `EncryptionService.encrypt()` trước khi lưu vào `User.identifyCode`

### 4.2 Nghị định 356/2025/NĐ-CP — Bảo vệ dữ liệu cá nhân
- **Áp dụng tại:** UC08
- **CCCD/Passport là Sensitive-PII** theo Điều 4
- **Yêu cầu:** Mã hóa bắt buộc khi lưu trữ (AES-256)
- **Implementation:** `ADR-001` — EncryptionServiceImpl

### 4.3 GWI (Global Wellness Institute) Standards
- **Áp dụng tại:** UC06
- **Yêu cầu:** Dùng thuật ngữ chuẩn ngành wellness cho tên gói
- **Ví dụ:** Mindfulness Retreat, Detoxification Journey, Ayurveda, Stress Relief

---

## 5. Database Entities liên quan

| Entity | Mô tả | Module chủ |
|---|---|---|
| `Booking` | Bản ghi đặt phòng chính | Module 2 |
| `RetreatPackage` | Catalog gói nghỉ dưỡng | Module 1/Admin |
| `VillaType` | Phân loại biệt thự (Deluxe, Suite...) | Module 1/Admin |
| `Villa` | Biệt thự vật lý cụ thể | Module 1/Admin |
| `GuestFolio` | Tài khoản thanh toán của booking | Module 2 (tạo) / Module 5 (aggregate) |
| `Payment` | Giao dịch đặt cọc | Module 2 |

**Key relationship:**
```
Guest (User) ──< Booking >── RetreatPackage
                    │
                    ├──── VillaType (selected at booking)
                    ├──── Villa (assigned at check-in)
                    └──── GuestFolio (created at booking confirmation)
```

---

## 6. API Endpoints (Module 2)

| Method | Endpoint | Actor | Mô tả |
|---|---|---|---|
| `GET` | `/api/v1/packages` | Guest | Lấy danh sách gói (UC06) |
| `GET` | `/api/v1/packages/{id}` | Guest | Chi tiết gói |
| `POST` | `/api/v1/bookings` | Guest | Tạo booking + GuestFolio (UC07) |
| `POST` | `/api/v1/bookings/confirm` | System (VNPay callback) | Xác nhận thanh toán deposit |
| `GET` | `/api/v1/bookings/{id}` | Guest | Xem chi tiết booking (UC10) |
| `GET` | `/api/v1/bookings/{id}/itinerary` | Guest | Xem itinerary (UC10) |
| `POST` | `/api/v1/check-in` | Receptionist | Thực hiện check-in (UC08) |
| `GET` | `/api/v1/villas/available` | Receptionist | Danh sách villa khả dụng |
| `PATCH` | `/api/v1/villas/{id}/status` | Receptionist | Cập nhật villa status (UC09) |

---

## 7. Screen Authorization (Module 2)

| Màn hình | Guest | Receptionist | Therapist | Chef | Admin |
|---|:---:|:---:|:---:|:---:|:---:|
| Packages List | ✅ | ✅ | ✅ | ✅ | ✅ |
| Package Detail | ✅ | ✅ | ✅ | ✅ | ✅ |
| Book Now | ✅ | — | — | — | — |
| Payment | ✅ | — | — | — | — |
| Success Page | ✅ | — | — | — | — |
| Guest Dashboard (Booking info) | ✅ | — | — | — | — |
| Itinerary Timeline | ✅ | — | — | — | — |
| Receptionist Dashboard | — | ✅ | — | — | — |
| Expected Arrival List | — | ✅ | — | — | — |
| Check-in Form | — | ✅ | — | — | — |
| Villa Status Management | — | ✅ | — | — | — |

---

## 8. Third-party Integration (Module 2)

| Dịch vụ | Mục đích | UC |
|---|---|---|
| **VNPay Sandbox** | Xử lý thanh toán đặt cọc | UC07 |
| **VNPay Callback** | Nhận kết quả thanh toán, confirm booking | UC07 |

---

## 9. Quan hệ với các Module khác

```
Module 1 ──────────► Module 2 ──────────► Module 3
(Auth/JWT)           (Booking)            (Spa Schedule)
(RetreatPackage)     └──GuestFolio────►  Module 4
(VillaType/Villa)        (bookingId)      (F&B)
                                     └──► Module 5
                                          (Invoice/Checkout)
```

| Dữ liệu gửi đi | Nhận bởi |
|---|---|
| `bookingId` → làm khóa chính trong GuestFolio | Module 5 aggregate billing |
| `guestId` + `bookingId` → phục vụ check Spa eligibility | Module 3 |
| `guestId` → phục vụ check Dietary profile filter | Module 4 |

---

## 10. Checklist Triển khai (Implementation Checklist)

### Database
- [ ] Bảng `booking` với các trường: `id`, `guestId`, `retreatPackageId`, `villaTypeId`, `assignedVillaId`, `checkinDate`, `checkoutDate`, `totalGuests`, `bookingStatus`, `paymentStatus`
- [ ] Bảng `guest_folio` với FK `bookingId`
- [ ] Constraint: Villa không được double-booking trong cùng khoảng ngày

### Service Layer
- [ ] `BookingServiceImpl.createBooking()` → tạo Booking + GuestFolio(PENDING)
- [ ] `BookingServiceImpl.confirmPayment()` → update CONFIRMED + DEPOSITED
- [ ] `CheckInServiceImpl.performCheckIn()` → gán villa + mã hóa CCCD + CHECKED_IN
- [ ] `VillaServiceImpl.checkAvailability()` → kiểm tra phòng trống
- [ ] `VillaServiceImpl.updateVillaStatus()` → cập nhật trạng thái villa

### Security / Compliance
- [ ] `EncryptionService.encrypt(identifyCode)` phải được gọi tại check-in
- [ ] Endpoint `/check-in` chỉ RECEPTIONIST mới access được
- [ ] Endpoint `/bookings` (POST) chỉ GUEST mới access được
- [ ] Audit log cho createBooking, confirmPayment, performCheckIn

### UI Screens
- [ ] Packages List + Filter by goal
- [ ] Package Detail + Book Now form
- [ ] Payment redirect sang VNPay
- [ ] Booking Confirmation / Success Page
- [ ] Guest Dashboard — Itinerary Timeline
- [ ] Receptionist Dashboard — Expected Arrivals
- [ ] Check-in Form (gán villa + nhập CCCD)
- [ ] Villa Status Management

---

*Document generated: 2026-06-19 | Based on SRS_Document.md + SWP391-HOS-03.md*
