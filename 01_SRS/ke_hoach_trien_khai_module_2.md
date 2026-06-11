# KẾ HOẠCH TRIỂN KHAI CHI TIẾT MODULE 2: ĐẶT GÓI TRỊ LIỆU & PHÒNG Ở
## (Áp dụng tiêu chuẩn kỹ thuật EDS v2.0 & Đặc tả kiểm thử TDD v1.0)

| Field | Value |
| --- | --- |
| **Document ID** | `AURAMOON-BOOKING-IMP-002` |
| **Version** | 1.0 |
| **Date** | 2026-06-11 |
| **Status** | In Review |
| **Document Owner** | Sinh viên 2 (Module 2 Owner) |
| **Author** | Antigravity AI Assistant |
| **Reviewed by** | [Tech Lead / Principal Architect] |
| **DPO Sign-off** | [ ] Pending (Bắt buộc do có xử lý thông tin định danh CCCD/Passport) |
| **Approved by** | [Principal Architect] |
| **Last Review** | 2026-06-11 |
| **Based on Templates**| EDS v2.0 & TDD v1.0 |

---

# CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa lịch sử cũ. Mọi thay đổi phải ghi vào bảng này.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-11 | AI Assistant | Tạo kế hoạch triển khai tích hợp EDS & TDD cho Module 2 lần đầu |

---

# MỤC LỤC
* [PHẦN I: ĐẶC TẢ KỸ THUẬT & THIẾT KẾ CHI TIẾT (EDS)](#phần-i-đặc-tả-kỹ-thuật--thiết-kế-chi-tiết-eds)
  * [1. Tổng quan Module 2](#1-tổng-quan-module-2)
  * [2. Ma trận Truy vết (Traceability Matrix)](#2-ma-trận-truy-vết-traceability-matrix)
  * [3. Quyết định Kiến trúc (ADR)](#3-quyết-định-kiến-trúc-adr)
  * [4. Yêu cầu Phi chức năng & SLA](#4-yêu-cầu-phi-chức-năng--sla)
  * [5. Mô hình Tĩnh (Static Modeling)](#5-mô-hình-tĩnh-static-modeling)
  * [6. Mô hình Động (Dynamic Modeling)](#6-mô-hình-động-dynamic-modeling)
  * [7. Đặc tả API & Phân quyền](#7-đặc-tả-api--phân-quyền)
  * [8. Bảng mã lỗi (Error Codes)](#8-bảng-mã-lỗi-error-codes)
* [PHẦN II: ĐẶC TẢ KIỂM THỬ HƯỚNG PHÁT TRIỂN (TDD)](#phần-ii-đặc-tả-kiểm-thử-hướng-phát-triển-tdd)
  * [1. Logic Issues Resolved](#1-logic-issues-resolved)
  * [2. Test Design Specification (TDS)](#2-test-design-specification-tds)
  * [3. Kịch bản Kiểm thử Chi tiết (Test Cases)](#3-kịch-bản-kiểm-thử-chi-tiết-test-cases)
  * [4. Điều kiện Bắt đầu/Kết thúc (Entry/Exit Criteria)](#4-điều-kiện-bắt-đầukết-thúc-entryexit-criteria)
* [PHẦN III: KẾ HOẠCH TRIỂN KHAI & PHỤC HỒI RỦI RO](#phần-iii-kế-hoạch-triển-khai--phục-hồi-rủi-ro)

---

# PHẦN I: ĐẶC TẢ KỸ THUẬT & THIẾT KẾ CHI TIẾT (EDS)

## 1. Tổng quan Module 2

| Field | Value |
| --- | --- |
| **Module Name** | Đặt Gói Trị Liệu & Phòng Ở (Booking & Stay Management) |
| **Bounded Context** | BookingContext |
| **Data Classification** | `Confidential` (Thông tin đặt phòng, doanh thu) & `Sensitive-PII` (Số CCCD/Hộ chiếu của khách check-in) |
| **Compliance Scope** | Luật Cư trú 2020 (Việt Nam), Nghị định 13/2023/NĐ-CP về bảo vệ dữ liệu cá nhân |
| **Upstream Dependencies** | `AuthContext` (Thông tin tài khoản khách hàng, phân quyền nhân viên) |
| **Downstream Consumers** | `SpaContext` (Tác vụ đặt lịch trị liệu của gói), `FnbContext` (Xử lý suất ăn theo gói dinh dưỡng), `BillingContext` (Tổng hợp Folio thanh toán) |

Module 2 chịu trách nhiệm quản lý vòng đời từ lúc khách hàng duyệt, lọc, đặt gói trị liệu kèm biệt thự lưu trú (Villa), thanh toán đặt cọc trực tuyến, cho đến khi Lễ tân thực hiện thủ tục Check-in, gán phòng vật lý, thu thập và mã hóa thông tin định danh cư trú, cập nhật trạng thái phòng vật lý thời gian thực, và hiển thị dòng thời gian lịch trình trải nghiệm (Itinerary Timeline) cho khách hàng.

---

## 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại | Mô tả yêu cầu | Thành phần Code liên quan | Compliance Target | ADR liên quan |
| --- | --- | --- | --- | --- | --- |
| **UC06** | Functional | Duyệt và lọc các gói trị liệu theo mục tiêu sức khỏe | `RetreatPackageController`, `RetreatPackageService`, `RetreatPackageRepository` | GWI Standards | — |
| **UC07** | Functional | Đặt gói & Villa, thanh toán đặt cọc qua cổng thanh toán | `BookingController`, `BookingService`, `BookingRepository`, `GuestFolioRepository` | AHLEI Folio Standards | ADR-002, ADR-003 |
| **UC08** | Functional | Lễ tân Check-in, gán phòng Villa vật lý, thu thập thông tin định danh | `CheckInController`, `CheckInService`, `VillaRepository` | Luật Cư trú 2020 | ADR-001 |
| **UC09** | Functional | Quản lý trạng thái vật lý của Villa (Trống, Đang ở, Bảo trì/Dọn dẹp) | `VillaController`, `VillaService` | Operational Efficiency | — |
| **UC10** | Functional | Xem chi tiết đặt phòng và dòng thời gian lịch trình (Itinerary Timeline) | `ItineraryController`, `ItineraryService` | Guest Satisfaction | — |

---

## 3. Quyết định Kiến trúc (ADR)

### ADR-001 — Mã hóa thông tin định danh tại chỗ (Encryption at rest) cho dữ liệu định danh khách hàng
*   **Bối cảnh:** Theo Luật Cư trú 2020 và Nghị định 13/2023/NĐ-CP, việc lưu giữ thông tin cá nhân nhạy cảm như Số CCCD/Hộ chiếu bắt buộc phải được bảo vệ chống rò rỉ dữ liệu.
*   **Phương án xem xét:**
    *   *Phương án A:* Lưu plaintext trong DB (Dễ lập trình, không bảo mật).
    *   *Phương án B:* Mã hóa đối xứng AES-256 bằng khóa động do ứng dụng quản lý và lưu trữ trong DB.
*   **Quyết định:** Chọn **Phương án B**. Sử dụng thuật toán AES-256 với khóa mã hóa lưu cấu hình an toàn trên biến môi trường hệ thống.
*   **Hệ quả:** 
    *   *Tích cực:* Đảm bảo tính pháp lý và an toàn thông tin, vượt qua kiểm định an toàn dữ liệu cá nhân.
    *   *Tiêu cực:* Không thể thực hiện truy vấn tìm kiếm trực tiếp trên cột mã hóa bằng toán tử `LIKE`. Việc tìm kiếm phải khớp chính xác (`exact match`) bằng cách mã hóa dữ liệu đầu vào trước khi so khớp.

### ADR-002 — Khởi tạo tài khoản nợ trung tâm (Guest Folio) ngay khi đặt phòng thành công
*   **Bối cảnh:** Resort cung cấp dịch vụ trọn gói và các dịch vụ phát sinh ngoài gói (Spa mua thêm, ăn uống gọi thêm). Cần có cơ chế tích hợp hóa đơn để khách thanh toán một lần lúc Check-out.
*   **Quyết định:** Khi tạo mới một `Booking` thành công với trạng thái `Đã đặt cọc` (Deposited), hệ thống tự động khởi tạo một bản ghi `GuestFolio` liên kết trực tiếp với `booking_id`.
*   **Hệ quả:** Các module khác (Spa - Module 3, F&B - Module 4) chỉ cần gọi API nợ của `BillingContext` truyền kèm `booking_id` để tự động đẩy chi phí vào `GuestFolio` trung tâm.

### ADR-003 — Chống xung đột đặt phòng vật lý và cơ chế khóa bi quan (Pessimistic Locking)
*   **Bối cảnh:** Nhiều khách hàng hoặc lễ tân có thể gán phòng cùng một lúc dẫn đến tình trạng double-booking (hai khách ở cùng một phòng).
*   **Quyết định:** Sử dụng cơ chế khóa `@Lock(LockModeType.PESSIMISTIC_WRITE)` của Spring Data JPA khi truy vấn kiểm tra phòng trống vật lý trong quá trình Check-in và gán phòng.

---

## 4. Yêu cầu Phi chức năng & SLA

### 4.1. Hiệu năng & Khả dụng
*   **Thời gian phản hồi API:** `< 250ms` đối với luồng lọc gói (UC06) và xem lịch trình (UC10). `< 500ms` đối với luồng check-in và gán phòng (UC08).
*   **Độ sẵn sàng (Uptime):** `99.9%` đối với luồng đặt phòng trực tuyến.
*   **Tải đồng thời:** Hỗ trợ tối thiểu `200 concurrent transactions` tại thời điểm thanh toán đặt cọc.

### 4.2. Bảo mật dữ liệu định danh
*   **Mã hóa lưu trữ (At-rest):** Cột `identify_code` (CCCD/Passport) trong bảng `USER` được mã hóa AES-256.
*   **Mã hóa đường truyền (In-transit):** Tất cả các trang đi qua HTTPS TLS 1.2 hoặc cao hơn.
*   **Phân quyền (RBAC):** Chỉ Lễ tân (`Receptionist`) và Quản trị viên (`Admin`) được xem dữ liệu check-in và gán phòng. Khách hàng chỉ xem lịch trình cá nhân của mình.

---

## 5. Mô hình Tĩnh (Static Modeling)

### 5.1. Thiết kế Lớp (Class Diagram)
Hệ thống tuân thủ cấu trúc Spring Boot MVC:
```
com.AuraMoon.auramoon.booking
├── controller
│   ├── RetreatPackageController.java (Đã có - UC06)
│   ├── BookingController.java (Mới - UC07)
│   ├── CheckInController.java (Mới - UC08)
│   └── VillaStatusController.java (Mới - UC09)
├── service
│   ├── RetreatPackageService.java (Đã có)
│   ├── BookingService.java (Mới - UC07)
│   ├── CheckInService.java (Mới - UC08)
│   ├── VillaService.java (Mới - UC09)
│   └── ItineraryService.java (Mới - UC10)
├── repository
│   ├── RetreatPackageRepository.java (Đã có)
│   ├── BookingRepository.java (Mới)
│   ├── VillaRepository.java (Mới)
│   ├── VillaTypeRepository.java (Mới)
│   └── GuestFolioRepository.java (Mới - thuộc Billing/Booking)
└── entity
    ├── RetreatPackage.java (Đã có)
    ├── Booking.java (Đã có)
    ├── Villa.java (Đã có)
    ├── VillaType.java (Đã có)
    └── Review.java (Đã có)
```

### 5.2. Cấu trúc Database (Spring Data JPA Entities Mapping)
Dựa trên các Class Entity hiện tại, cấu trúc bảng trên SQL Server được liên kết như sau:
1.  **VILLA_TYPE**: `type_id` (PK), `type_name`, `image`, `price_per_day`, `is_delete`
2.  **VILLA**: `villa_id` (PK), `villa_type` (FK -> VILLA_TYPE), `villa_code` (Unique), `limit_person`, `villa_status` (Available, Occupied, Maintenance), `cleaning_status` (Cleaned, Dirty), `is_delete`
3.  **RETREAT_PACKAGE**: `package_id` (PK), `type_package`, `package_name`, `duration_days`, `services`, `description`, `price`, `is_active`, `is_delete`
4.  **BOOKING**: `booking_id` (PK), `guest_id` (FK -> USER), `package_id` (FK -> RETREAT_PACKAGE), `assigned_villa_id` (FK -> VILLA - Nullable cho đến check-in), `checkin_date`, `checkout_date`, `total_guests`, `booking_status` (Pending, Confirmed, Checked-In, Checked-Out, Cancelled), `payment_status` (Unpaid, Deposited, Fully-Paid), `is_delete`
5.  **GUEST_FOLIO** (Bảng quản lý nợ phát sinh): `folio_id` (PK), `booking_id` (FK -> BOOKING), `total_package_amount`, `total_extra_amount`, `final_amount`, `status` (Pending, Settled)

---

## 6. Mô hình Động (Dynamic Modeling)

### 6.1. Sequence Diagram: Đặt gói và thanh toán đặt cọc (UC07)
```
Guest -> BookingController: GET /booking/create?packageId=1
BookingController -> RetreatPackageService: getPackageById(1)
BookingController --> Guest: Trả về trang nhập thông tin đặt phòng (booking/create-form)

Guest -> BookingController: POST /booking/create (packageId, checkinDate, totalGuests, villaTypeId)
BookingController -> BookingService: createBooking(guestId, DTO)
activate BookingService
BookingService -> VillaService: checkVillaAvailability(villaTypeId, checkin, checkout)
alt Không có Villa trống
    BookingService --> BookingController: ném VillaNotAvailableException
    BookingController --> Guest: Trả về trang form kèm thông báo lỗi phòng đầy
else Có Villa trống
    BookingService -> BookingRepository: save(Booking ở trạng thái PENDING, payment UNPAID)
    BookingService -> PaymentService: generatePaymentUrl(bookingId, depositAmount)
    BookingService --> BookingController: Trả về Payment URL
    deactivate BookingService
    BookingController --> Guest: Redirect sang Cổng thanh toán (Stripe/VNPay)
end

Guest -> PaymentGateway: Tiến hành thanh toán đặt cọc
PaymentGateway -> BookingController: GET /booking/payment/callback (mã giao dịch, trạng thái)
BookingController -> BookingService: confirmPayment(bookingId, transactionCode)
activate BookingService
BookingService -> BookingRepository: Cập nhật booking_status = 'Confirmed', payment_status = 'Deposited'
BookingService -> GuestFolioRepository: save(GuestFolio mới liên kết bookingId)
BookingService -> NotificationService: sendConfirmationEmail(bookingId)
deactivate BookingService
BookingController --> Guest: Trả về trang "Đặt phòng thành công" (booking/success)
```

### 6.2. Sơ đồ trạng thái của Booking (Booking State Machine)
```
       [Khởi tạo] 
           │
           ▼
     ┌───────────┐         Thanh toán thất bại / Quá 15p
     │  PENDING  ├─────────────────────────────────────────┐
     └─────┬─────┘                                         │
           │ Thanh toán Đặt cọc thành công                 │
           ▼                                               │
     ┌───────────┐                                         │
     │ CONFIRMED ├───────── Khách hủy / Không đến            │
     └─────┬─────┘                                         ▼
           │ Lễ tân check-in & gán phòng vật lý      ┌───────────┐
           ▼                                         │ CANCELLED │
     ┌───────────┐                                   └───────────┘
     │CHECKED-IN │                                         ▲
     └─────┬─────┘                                         │
           │ Lễ tân check-out & thanh toán nợ Folio        │
           ▼                                               │
     ┌───────────┐                                         │
     │CHECKED-OUT├─────────────────────────────────────────┘
     └───────────┘
```

---

## 7. Đặc tả API & Phân quyền

Hệ thống sử dụng cơ chế Server-Side Rendering (Thymeleaf), các URL Request định nghĩa như sau:

| HTTP Method | URL Path | Vai trò yêu cầu | Mục đích |
| --- | --- | --- | --- |
| **GET** | `/packages` | ALL (Public) | Xem danh sách và lọc gói trị liệu (UC06 - Đã làm) |
| **GET** | `/booking/create` | `GUEST` | Hiển thị form điền thông tin đặt phòng (UC07) |
| **POST** | `/booking/create` | `GUEST` | Gửi yêu cầu đặt phòng và lấy URL thanh toán (UC07) |
| **GET** | `/booking/payment/callback` | `GUEST`, `SYSTEM` | Nhận kết quả thanh toán từ Gateway (UC07) |
| **GET** | `/reception/bookings` | `RECEPTIONIST`, `ADMIN` | Trang chủ lễ tân, danh sách khách đến trong ngày (UC08) |
| **POST** | `/reception/checkin/{id}` | `RECEPTIONIST` | Xác nhận check-in, gán phòng và lưu thông tin định danh (UC08) |
| **GET** | `/reception/villas` | `RECEPTIONIST`, `ADMIN` | Quản lý trạng thái các phòng Villa vật lý (UC09) |
| **POST** | `/reception/villas/{id}/status`| `RECEPTIONIST` | Cập nhật dọn phòng / bảo trì Villa (UC09) |
| **GET** | `/itinerary` | `GUEST` | Xem dòng thời gian lịch trình của chuyến đi (UC10) |

---

## 8. Bảng mã lỗi (Error Codes)

| Mã lỗi | HTTP Status | Thông điệp hiển thị | Tình huống xảy ra |
| --- | --- | --- | --- |
| `BOOK-001` | 400 Bad Request | Ngày check-in phải lớn hơn hoặc bằng ngày hiện tại. | Nhập ngày quá khứ trên form đặt. |
| `BOOK-002` | 409 Conflict | Loại biệt thự đã chọn không còn phòng trống trong thời gian này. | Trùng lịch / Hết phòng. |
| `BOOK-003` | 404 Not Found | Không tìm thấy mã đơn đặt phòng yêu cầu. | Nhập sai ID đặt phòng khi check-in. |
| `BOOK-004` | 500 Internal | Lỗi bảo mật: Không thể mã hóa dữ liệu định danh cư trú. | Lỗi hệ thống mã hóa AES. |
| `BOOK-005` | 400 Bad Request | Giao dịch đặt cọc không hợp lệ hoặc bị hủy từ cổng thanh toán. | Thanh toán thất bại. |
| `BOOK-006` | 409 Conflict | Không thể chuyển trạng thái phòng: Phòng hiện đang có khách ở. | Cố tình đổi trạng thái Villa đang `Occupied` sang `Available` |

---

# PHẦN II: ĐẶC TẢ KIỂM THỬ HƯỚNG PHÁT TRIỂN (TDD)

## 1. Logic Issues Resolved

Trước khi triển khai, các mâu thuẫn nghiệp vụ sau đây đã được làm rõ và thống nhất giải pháp xử lý:

| # | Spec / Thiết kế ban đầu | Thực tế Codebase & Luật áp dụng | Giải pháp trong mã kiểm thử |
| --- | --- | --- | --- |
| **L1** | Cho phép lễ tân gán phòng tùy ý không cần theo loại phòng khách đặt. | Cần tuân thủ đúng loại Villa (`VillaCategory` / `VillaType`) khách đã trả tiền đặt cọc. | Viết test kiểm tra: Lễ tân chỉ được gán các phòng Villa vật lý có `villa_type` khớp với loại phòng trong đơn đặt phòng. |
| **L2** | Lưu thông tin CMND/CCCD trực tiếp dưới dạng Text thuần trong DB. | Luật Cư trú 2020 & Nghị định 13/2023 yêu cầu bảo vệ dữ liệu nhạy cảm PII. | Test case kiểm tra: Cột `identify_code` lưu xuống DB phải được mã hóa dạng Ciphertext, không lưu plaintext. |

---

## 2. Test Design Specification (TDS)

### 2.1. Phạm vi kiểm thử (Scope)
*   **Unit Tests:** Kiểm thử độc lập tầng `BookingService` và `CheckInService` sử dụng Mockito để giả lập Repository.
*   **Integration Tests:** Kiểm thử sự phối hợp giữa Service, Repository và Database thực tế thông qua việc chạy test kết nối CSDL SQL Server (Hỗ trợ rollback bằng `@Transactional`).

### 2.2. Điều kiện kiểm thử và Các ca kiểm thử tương ứng (Coverage Items)

| Condition ID | Điều kiện nghiệp vụ | Các Ca kiểm thử (Test Cases) |
| --- | --- | --- |
| **COND-BOOK-001**| Đặt phòng hợp lệ (đủ phòng trống) | `createBooking_villasAvailable_savesSuccessfully` |
| **COND-BOOK-002**| Đặt phòng nhưng loại Villa đó đã hết phòng trống | `createBooking_noVillasAvailable_throwsBook002` |
| **COND-CHECK-001**| Check-in hợp lệ: Gán đúng loại phòng, gán trạng thái vật lý | `checkIn_validRequest_updatesStatusesAndEncryptsID` |
| **COND-CHECK-002**| Check-in lỗi: Nhập CCCD rỗng hoặc không mã hóa thành công | `checkIn_emptyIdentityCode_throwsValidationException` |
| **COND-VILLA-001**| Cập nhật trạng thái dọn dẹp vật lý của phòng | `updateVillaStatus_validRequest_updatesSuccessfully` |

---

## 3. Kịch bản Kiểm thử Chi tiết (Test Cases)

### 3.1. UC07: Đặt Gói & Villa
#### `createBooking_villasAvailable_savesSuccessfully`
*   **Mức độ nghiêm trọng:** HIGH (P0)
*   **Mô tả:** Khách hàng đặt gói trị liệu hợp lệ trong khoảng thời gian có sẵn biệt thự.
*   **Các bước thực hiện:**
    1.  *Arrange:* Mock `RetreatPackageRepository` trả về thông tin gói trị liệu thời lượng 3 ngày. Mock `VillaRepository` trả về danh sách có phòng trống thuộc loại Villa được chọn.
    2.  *Act:* Gọi `bookingService.createBooking(...)` truyền thông tin khách hàng, gói trị liệu và ngày check-in.
    3.  *Assert:* Kết quả trả về chứa mã đặt phòng, trạng thái phòng là `PENDING`, trạng thái thanh toán là `UNPAID`. Ngày checkout được tự động tính bằng `checkinDate + durationDays` (ví dụ: đặt 12/06 -> checkout 15/06).

#### `createBooking_noVillasAvailable_throwsBook002`
*   **Mức độ nghiêm trọng:** HIGH (P0)
*   **Mô tả:** Khách hàng cố gắng đặt phòng nhưng loại phòng đó đã kín lịch trong thời gian lưu trú.
*   **Các bước thực hiện:**
    1.  *Arrange:* Mock `VillaRepository` trả về danh sách phòng trống rỗng cho loại Villa đó trong khoảng ngày check-in -> check-out.
    2.  *Act:* Gọi `bookingService.createBooking(...)`.
    3.  *Assert:* Hệ thống ném ra ngoại lệ `VillaNotAvailableException` (mã lỗi `BOOK-002`). Không có bản ghi Booking mới nào được lưu.

---

### 3.2. UC08: Check-in Nhận Phòng & Mã Hóa Thông Tin Định Danh
#### `checkIn_validRequest_updatesStatusesAndEncryptsID`
*   **Mức độ nghiêm trọng:** CRITICAL (P0)
*   **Mục tiêu bảo mật:** Tuân thủ Luật Cư trú và Bảo vệ dữ liệu nhạy cảm (PII).
*   **Các bước thực hiện:**
    1.  *Arrange:* Mock `BookingRepository` trả về đơn đặt phòng hợp lệ ở trạng thái `Confirmed`. Mock `VillaRepository` trả về Villa vật lý số "VIL-101" đang trống. Cấu hình dịch vụ mã hóa `EncryptionService`.
    2.  *Act:* Gọi `checkInService.performCheckIn(bookingId, guestIdentityCode = "012345678901", villaId = 101)`.
    3.  *Assert:*
        *   Trạng thái Booking đổi thành `Checked-In`.
        *   Trạng thái Villa "VIL-101" đổi thành `Occupied`.
        *   Số định danh cư trú của khách hàng lưu xuống cơ sở dữ liệu phải được mã hóa (Assert rằng dữ liệu lưu vào database KHÔNG phải là chuỗi `"012345678901"` mà là chuỗi đã được mã hóa AES-256).

#### `checkIn_invalidVillaType_throwsException`
*   **Mức độ nghiêm trọng:** HIGH (P1)
*   **Mô tả:** Lễ tân cố tình gán một Villa có loại phòng không khớp với loại phòng khách đã đặt.
*   **Các bước thực hiện:**
    1.  *Arrange:* Đơn đặt phòng yêu cầu loại Villa "Ocean View". Mock Villa được chọn có loại là "Garden View".
    2.  *Act:* Gọi `checkInService.performCheckIn(bookingId, "012345678901", invalidVillaId)`.
    3.  *Assert:* Hệ thống ném ra `InvalidVillaAssignmentException` và ngăn chặn tác vụ check-in.

---

### 3.3. UC10: Dòng Thời Gian Lịch Trình (Itinerary Timeline)
#### `getItineraryTimeline_validBooking_returnsAggregatedTimeline`
*   **Mức độ nghiêm trọng:** MEDIUM (P1)
*   **Mô tả:** Khách hàng truy cập lịch trình cá nhân, hệ thống tổng hợp thông tin lưu trú, lịch spa và suất ăn tương ứng.
*   **Các bước thực hiện:**
    1.  *Arrange:* Mock thông tin booking lưu trú từ ngày 12/06 đến 14/06. Mock danh sách lịch hẹn Spa từ Module 3. Mock thực đơn ăn uống từ Module 4.
    2.  *Act:* Gọi `itineraryService.getTimelineForGuest(guestId)`.
    3.  *Assert:* Kết quả trả về là một danh sách các sự kiện được sắp xếp đúng thứ tự thời gian tăng dần:
        *   Sự kiện 1: Check-in (12/06 14:00)
        *   Sự kiện 2: Suất ăn trưa (12/06 12:00)
        *   Sự kiện 3: Trị liệu Spa (12/06 15:30)
        *   ...
        *   Sự kiện cuối: Check-out (14/06 12:00)

---

## 4. Điều kiện Bắt đầu/Kết thúc (Entry/Exit Criteria)

### Điều kiện Bắt đầu (Entry Criteria)
- [ ] Kế hoạch triển khai này được người dùng phê duyệt (Approved).
- [ ] Đã cấu hình và kiểm tra kết nối CSDL SQL Server cục bộ.
- [ ] Principal Architect phê duyệt thiết kế mã hóa thông tin cư trú cưỡng chế.

### Điều kiện Kết thúc (Exit Criteria — Definition of Done)
- [ ] Toàn bộ các API và Controller nghiệp vụ cho UC07, UC08, UC09, UC10 được cài đặt hoàn chỉnh.
- [ ] Viết đầy đủ unit test cho `BookingService`, `CheckInService` và `VillaService`.
- [ ] **Chạy test thành công 100%:** Toàn bộ các test case vượt qua kiểm thử (`mvn test` thành công).
- [ ] Đã dựng các view Thymeleaf cho Đặt phòng (`booking/create-form`), Quản lý phòng Lễ tân (`reception/villas`), và Lịch trình (`guest/itinerary`) đảm bảo thẩm mỹ (CSS Glassmorphism premium, font Outfit, micro-animations đầy đủ).

---

# PHẦN III: KẾ HOẠCH TRIỂN KHAI & PHỤC HỒI RỦI RO

## 1. Các bước thực hiện chi tiết (Step-by-Step)

### Bước 1: Cấu hình và Chuẩn bị Database
1.  Đảm bảo bảng `BOOKING`, `VILLA`, `VILLA_TYPE` đã được tạo đúng cấu trúc trong SQL Server.
2.  Chèn dữ liệu thử nghiệm (Seed data) cho các loại Villa (VILLA_TYPE) và các phòng biệt thự vật lý (VILLA).

### Bước 2: Hiện thực hóa Tầng Repository & Encryption
1.  Tạo lớp công cụ `EncryptionUtils` thực hiện mã hóa/giải mã AES-256 để bảo vệ thông tin định danh `identify_code`.
2.  Tạo các repository tương ứng cho `BookingRepository`, `VillaRepository`, `VillaTypeRepository`, `GuestFolioRepository`.

### Bước 3: Viết và Thực thi Kịch bản Kiểm thử TDD (RED phase)
1.  Tạo file kiểm thử `BookingServiceTest.java` và `CheckInServiceTest.java` trong thư mục `src/test/java/...`.
2.  Khai báo tất cả các test case ở trạng thái rỗng hoặc ném lỗi để kiểm tra trạng thái **RED (Thất bại)**.

### Bước 4: Viết Production Code (GREEN phase)
1.  Triển khai logic nghiệp vụ trong `BookingServiceImpl.java` (kiểm tra phòng trống, tính ngày checkout, lưu booking, tích hợp API thanh toán).
2.  Triển khai logic nghiệp vụ trong `CheckInServiceImpl.java` (gán số phòng vật lý, gọi hàm mã hóa số định danh, thay đổi trạng thái dọn dẹp và phòng ở).
3.  Triển khai `ItineraryServiceImpl.java` (truy vấn kết hợp để dựng timeline).
4.  Chạy lại các test case và tối ưu hóa mã nguồn cho đến khi tất cả các test case đều chuyển sang **GREEN (Thành công)**.

### Bước 5: Thiết kế Giao diện Thymeleaf & CSS tĩnh
1.  Xây dựng các giao diện HTML trong `src/main/resources/templates/booking/` và `templates/reception/`.
2.  Tách biệt các file CSS và JS vào các thư mục tĩnh tương ứng theo module (ví dụ: `static/css/booking/booking-form.css`, `static/js/booking/timeline.js`).
3.  Áp dụng thiết kế giao diện Glassmorphism cao cấp, phông chữ sang trọng và hiệu ứng động mượt mờ.

---

## 2. Kế hoạch phục hồi (Rollback Plan)

Trong trường hợp triển khai gặp lỗi nghiêm trọng hoặc xung đột mã nguồn hệ thống:
1.  **Revert Code:** Sử dụng lệnh Git để hoàn tác toàn bộ các file đã chỉnh sửa về trạng thái ổn định gần nhất:
    ```bash
    git checkout -- src/main/java/com/AuraMoon/auramoon/booking/
    git checkout -- src/main/resources/templates/booking/
    ```
2.  **Database Rollback:** Nếu có chạy các tập lệnh thay đổi cấu trúc bảng (Migration), chạy tập lệnh rollback để khôi phục cấu trúc DB cũ.
3.  Báo cáo chi tiết sự cố lên Tech Lead và DPO để đánh giá lại thiết kế trước khi tiến hành thử lại.
