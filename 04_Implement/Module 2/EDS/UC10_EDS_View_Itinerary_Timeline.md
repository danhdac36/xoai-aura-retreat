# ENGINEERING DESIGN SPECIFICATION (EDS) v2.0

# UC10 — View Booking Details & Itinerary Timeline

| Field                    | Value                                                                       |
| ------------------------ | --------------------------------------------------------------------------- |
| **Document ID**    | `AURAMOON-BOOKING-EDS-UC10`                                               |
| **Version**        | 1.0                                                                         |
| **Date**           | 2026-06-19                                                                  |
| **Status**         | Approved                                                                    |
| **Document Owner** | Lê Trà My — Module 2 Lead                                               |
| **Author**         | Lê Trà My — Full-stack Developer                                        |
| **Reviewed by**    | Phùng Giang Hải                                                           |
| **DPO Sign-off**   | `[x] Approved` — Chỉ hiển thị dữ liệu của chính Guest (IDOR-safe) |
| **Approved by**    | Phùng Giang Hải — Tech Lead                                              |
| **Last Review**    | 2026-06-20                                                                  |
| **Based on EDS**   | v2.0                                                                        |

# CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                                                         |
| ---------- | ------------------- | ---------------------------------------------------------------------------- |
| 2026-06-19 | Student 2           | Tạo tài liệu lần đầu — UC10 View Booking Details & Itinerary Timeline |

---

# 1. Tổng quan UC

> **UC10** cho phép Guest xem toàn bộ thông tin booking của mình và lịch trình kỳ nghỉ (Itinerary Timeline) được generate tự động dựa trên gói nghỉ dưỡng và loại gói. Timeline tổng hợp sự kiện theo từng ngày: Check-in, Yoga/Thiền, Bữa ăn, Spa, Check-out.

| Field                           | Value                                                                     |
| ------------------------------- | ------------------------------------------------------------------------- |
| **UC ID**                 | `UC10`                                                                  |
| **UC Name**               | View Booking Details & Itinerary Timeline                                 |
| **Module**                | `booking`                                                               |
| **Bounded Context**       | `booking`                                                               |
| **Data Classification**   | `PII` (guestName, bookingId — của chính Guest)                       |
| **Compliance Scope**      | IDOR Prevention — Guest chỉ xem booking của chính mình               |
| **Upstream Dependencies** | UC07 (Booking phải tồn tại),`auth` (UserRepository — lấy fullName) |
| **Downstream Consumers**  | Không có downstream — Read-only view                                   |

---

# 2. Ma trận Truy vết

| Requirement ID    | Loại      | Mô tả yêu cầu                        | Thành phần Code                                   | Compliance Target | ADR liên quan |
| ----------------- | ---------- | ---------------------------------------- | --------------------------------------------------- | ----------------- | -------------- |
| **UC10**    | User Story | Guest xem booking và itinerary timeline | `ItineraryServiceImpl.getTimelineForGuest()`      | IDOR Prevention   | ADR-UC10-001   |
| **BR-IDOR** | Security   | Guest chỉ xem booking của chính mình | Controller lấy guestId từ JWT, không từ request | OWASP API-01      | ADR-UC10-001   |

---

# 3. Architecture Decision Records (ADR)

## ADR-UC10-001 — Itinerary được generate server-side dựa trên packageType

| Field            | Value      |
| ---------------- | ---------- |
| **Status** | Accepted   |
| **Date**   | 2026-06-19 |

**Bối cảnh:**
Itinerary không được lưu trực tiếp trong DB mà được tạo động tại runtime bởi `ItineraryServiceImpl`. Logic tạo event dựa trên `typePackage` của gói đã book.

**Quyết định:** Generate itinerary on-the-fly dựa trên logic template theo `typePackage`:

- `yoga` → Yoga sáng + Bữa ăn + Spa phục hồi
- `stress` → Thiền định + Bữa trà + Spa Aromatherapy
- `detox` / `weight` → Cardio + Bữa Detox + Spa thải độc
- Default → Yoga sáng + Bữa dinh dưỡng + Spa massage

**Hệ quả:**

- Linh hoạt, không cần lưu itinerary vào DB
- Dễ mở rộng thêm packageType mới
- Trade-off: Nếu thay đổi logic template, tất cả booking cũ cũng thay đổi itinerary (không phản ánh đúng thời điểm book)

---

## ADR-UC10-002 — IDOR Prevention: guestId từ JWT, không từ request param

| Field            | Value      |
| ---------------- | ---------- |
| **Status** | Accepted   |
| **Date**   | 2026-06-19 |

**Quyết định:** Controller lấy `guestId` từ `@AuthenticationPrincipal` (JWT), không nhận `guestId` từ query param hay request body. Điều này ngăn Guest A xem itinerary của Guest B.

```java
// Controller:
@GetMapping("/itinerary")
public String showItinerary(@AuthenticationPrincipal UserDetailsResponse currentUser, Model model) {
    Integer guestId = currentUser.getId(); // Từ JWT — không phải từ request param
    ItineraryTimelineDTO dto = itineraryService.getTimelineForGuest(guestId);
    model.addAttribute("itinerary", dto);
    return "booking/itinerary";
}
```

---

# 4. Non-Functional Requirements & SLA

## 4.1. Performance

| Category       | Requirement                  | Target SLA                              | Measurement Method            | Compliance Basis  |
| ------------- | ---------------------------- | --------------------------------------- | ----------------------------- | ----------------- |
| Latency       | `getTimelineForGuest()`    | `< 300ms` (bao gồm event generation) | Stopwatch / k6                | Hotel Operations  |
| Timeline size | Số events cho 7-day package | ~28 events (4 events/day × 7 ngày)    | Unit test đếm events          | Hotel Operations  |

## 4.2. Security

| Category       | Requirement                                                | Target                 | Verification Method     | Compliance Basis |
| -------------- | ---------------------------------------------------------- | ---------------------- | ----------------------- | ---------------- |
| IDOR           | guestId từ JWT — không từ request param (ADR-UC10-002) | 0 IDOR incidents       | Security test (§13.3)   | OWASP API-01     |
| Authentication | JWT Bearer, role = GUEST                                   | 403 nếu sai role       | Security test (§13.3)   | Hotel SOP        |
| PII isolation  | Không hiển thị CCCD, Health Profile trong itinerary     | 0 PII leak             | Manual review (§14.1)   | PDPA Art. 26     |

---

# 5. Static Modeling

## 5.1. Class Diagram

```plantuml
@startuml
package "Controller Layer" {
  class ItineraryController <<Controller>> {
    +showItinerary(currentUser: UserDetailsResponse, model: Model): String
  }
}

package "Service Layer" {
  interface IItineraryService <<interface>> {
    +getTimelineForGuest(guestId: Integer): ItineraryTimelineDTO
  }
  class ItineraryServiceImpl {
    -bookingRepo: BookingRepository
    -userRepo: UserRepository
    +getTimelineForGuest(guestId: Integer): ItineraryTimelineDTO
    -generateDailyEvents(date: LocalDate, packageType: String, isStart: boolean, isEnd: boolean): List<TimelineEvent>
  }
}

package "DTO" {
  class ItineraryTimelineDTO {
    +bookingId: Integer
    +guestName: String
    +events: List<TimelineEvent>
  }
  class TimelineEvent {
    +eventName: String
    +time: LocalDateTime
    +description: String
  }
}

package "Repository Layer" {
  interface BookingRepository <<JpaRepository>> {
    +findByGuestId(guestId: Integer): List<Booking>
  }
}

IItineraryService <|.. ItineraryServiceImpl
ItineraryServiceImpl --> BookingRepository : uses
ItineraryController --> IItineraryService : uses
ItineraryTimelineDTO *-- TimelineEvent
@enduml
```

## 5.2. DTO Structure

```java
// ItineraryTimelineDTO.java
@Data @Builder
public class ItineraryTimelineDTO {
    private Integer bookingId;
    private String guestName;
    private List<TimelineEvent> events; // Sorted by time ASC

    @Data @Builder
    public static class TimelineEvent {
        private String eventName;           // Tên sự kiện
        private LocalDateTime time;         // Thời điểm (ngày + giờ)
        private String description;         // Mô tả chi tiết
    }
}
```

---

# 6. Dynamic Modeling

## 6.1. Sequence Diagram — Happy Path

```plantuml
@startuml
actor Guest
participant "ItineraryController" as Ctrl
participant "ItineraryServiceImpl" as Svc
participant "UserRepository" as UserRepo
participant "BookingRepository" as BookRepo
database "PostgreSQL" as DB

Guest -> Ctrl: GET /booking/itinerary\n[JWT: guestId=100]
activate Ctrl

Ctrl -> Ctrl: guestId = currentUser.getId() (từ JWT)

Ctrl -> Svc: getTimelineForGuest(100)
activate Svc

Svc -> UserRepo: findById(100)
UserRepo -> DB: SELECT * FROM USERS WHERE user_id=100
DB --> UserRepo: User{fullName="Nguyễn Văn A"}
UserRepo --> Svc: User

Svc -> BookRepo: findByGuestId(100)
BookRepo -> DB: SELECT * FROM BOOKING WHERE guest_id=100
DB --> BookRepo: [Booking{id=1001, status=CONFIRMED, checkinDate=2026-07-01, checkoutDate=2026-07-06}]
BookRepo --> Svc: List<Booking>

Svc -> Svc: activeBooking = booking có status CONFIRMED hoặc CHECKED-IN
Svc -> Svc: packageType = "Detox" → nhóm logic detox/weight

loop Mỗi ngày từ checkinDate đến checkoutDate
  Svc -> Svc: generateDailyEvents(date, packageType, isStart, isEnd)
  note over Svc
    Ngày đầu (isStart): Chỉ Check-in + Bữa tối
    Ngày cuối (isEnd): Chỉ Checkout
    Ngày giữa: Yoga/Thiền 06:30, Bữa trưa 12:00, Spa 15:30, Bữa tối 18:30
  end note
end

Svc -> Svc: events.sort(Comparator.comparing(time))
Svc --> Ctrl: ItineraryTimelineDTO{bookingId=1001, guestName="Nguyễn Văn A", events=[...28 events]}
deactivate Svc

Ctrl -> Ctrl: model.addAttribute("itinerary", dto)
Ctrl --> Guest: HTTP 200 — Thymeleaf: booking/itinerary.html
deactivate Ctrl
@enduml
```

## 6.2. Sequence Diagram — Error Path (Không có booking)

```plantuml
@startuml
actor Guest
participant "ItineraryController" as Ctrl
participant "ItineraryServiceImpl" as Svc
participant "BookingRepository" as BookRepo

Guest -> Ctrl: GET /booking/itinerary [JWT: guestId=200]
Ctrl -> Svc: getTimelineForGuest(200)
Svc -> BookRepo: findByGuestId(200)
BookRepo --> Svc: [] (empty list)
Svc --> Ctrl: throw IllegalArgumentException("Khách hàng chưa có bất kỳ lịch đặt phòng nào")
Ctrl --> Guest: HTTP 302 redirect:/guest/dashboard?error=no_booking
@enduml
```

## 6.3. State Machine — Booking Status Lifecycle (dùng bởi UC10)

```plantuml
@startuml
[*] --> PENDING : UC07 createBooking()\n[Guest submit form — paymentStatus=UNPAID]

PENDING --> CONFIRMED : UC07 confirmPayment()\n[VNPay callback — paymentStatus=PARTIAL]

PENDING --> [*] : Timeout / Hủy\n[Cleanup job]

CONFIRMED --> CHECKED_IN : UC08 performCheckIn()\n[Receptionist gán villa]

CHECKED_IN --> CHECKED_OUT : UC22 processCheckout()\n[Module 5 — paymentStatus=PAID]

note right of CONFIRMED
  UC10 hiển thị itinerary
  cho booking ở trạng thái này.
  assignedVilla = NULL
  (chưa gán villa cụ thể)
end note

note right of CHECKED_IN
  UC10 hiển thị itinerary
  cho booking ở trạng thái này.
  assignedVilla = Villa cụ thể
end note

note bottom of PENDING
  activeBooking selection (UC10):
  Ưu tiên CONFIRMED > CHECKED-IN
  Fallback: booking mới nhất trong list
end note
@enduml
```

**Quy tắc chọn `activeBooking` trong `ItineraryServiceImpl`:**

| Ưu tiên    | Điều kiện                       | Kết quả                           |
| ------------ | ---------------------------------- | ----------------------------------- |
| 1            | Có booking status =`CHECKED-IN` | Lấy booking đó                   |
| 2            | Có booking status =`CONFIRMED`  | Lấy booking đó                   |
| 3 (fallback) | Không có booking active          | Lấy booking cuối cùng trong list |

## 6.4. Itinerary Template Logic

```
packageType matching (case-insensitive, contains):
├── "stress" → Stress Relief template
│   ├── 06:30 → "Thiền định & Thở chánh niệm"
│   ├── 12:00 → "Bữa trưa thanh đạm giải tỏa căng thẳng"
│   ├── 15:30 → "Trị liệu Spa giấc ngủ sâu (Aromatherapy)"
│   └── 18:30 → "Thưởng trà trị liệu & Thư giãn"
├── "detox" || "weight" || "béo" || "cân" → Detox/Weight template
│   ├── 06:30 → "Vận động Cardio nhẹ nhàng"
│   ├── 12:00 → "Bữa trưa Detox & Ít calorie"
│   ├── 15:30 → "Trị liệu Spa thải độc chuyên sâu"
│   └── 18:30 → "Nước ép thanh lọc & Soup nhẹ"
└── default (Yoga, Ayurveda, ...) → Default template
    ├── 06:30 → "Luyện tập Yoga sáng"
    ├── 12:00 → "Bữa trưa dinh dưỡng"
    ├── 15:30 → "Trị liệu Spa phục hồi"
    └── 18:30 → "Bữa tối dinh dưỡng"
```

---

# 7. Domain Event Catalog

## 7.1. Events Published

> UC10 là **read-only**. Không phát ra event nào.

## 7.2. Events Consumed

> UC10 không tiêu thụ event. Nó đọc trực tiếp từ BookingRepository và UserRepository.

---

# 8. Interface Specification

## 8.1. Service Interface

```java
// ItineraryService.java
// @version 1.0
public interface ItineraryService {
    /**
     * Lấy Itinerary Timeline của Guest.
     * Tự động chọn booking active (CONFIRMED hoặc CHECKED-IN).
     * Generate events dựa trên packageType.
     *
     * @param guestId ID của Guest (từ JWT — IDOR-safe)
     * @throws IllegalArgumentException nếu guestId không tồn tại
     * @throws IllegalArgumentException nếu Guest chưa có booking nào
     * @return ItineraryTimelineDTO với danh sách events sắp xếp theo thời gian
     */
    ItineraryTimelineDTO getTimelineForGuest(Integer guestId);
}
```

## 8.2. Repository Interface

```java
// BookingRepository.java (mở rộng liên quan UC10)
// @version 1.0
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /**
     * Tìm tất cả booking thuộc về một Guest.
     * Dùng bởi: getTimelineForGuest() để lấy danh sách booking rồi chọn activeBooking.
     * @param guestId  ID của Guest (từ JWT — IDOR-safe)
     * @return Danh sách booking theo thứ tự tạo (mới nhất cuối)
     */
    List<Booking> findByGuestId(Integer guestId);
}

// UserRepository.java (dùng bởi UC10 để lấy fullName)
// @version 1.0
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Tìm User theo ID.
     * Dùng bởi: getTimelineForGuest() để lấy fullName hiển thị trên itinerary.
     * @param id  User ID (= guestId từ JWT)
     */
    Optional<User> findById(Integer id);
}
```

---

# 9. API Specification

## 9.1. Endpoints Table

| Method | Path                   | Auth Level | Required Roles | Rate Limit | Idempotent? |
| ------ | ---------------------- | ---------- | -------------- | ---------- | ----------- |
| GET    | `/booking/itinerary` | JWT Bearer | `GUEST`      | 100/min    | Yes         |
| GET    | `/guest/dashboard`   | JWT Bearer | `GUEST`      | 100/min    | Yes         |

## 9.2. Response Schema

### GET `/booking/itinerary` — Itinerary Timeline

**Response — 200 OK (Thymeleaf Model):**

```json
{
  "itinerary": {
    "bookingId": 1001,
    "guestName": "Nguyễn Văn A",
    "events": [
      {
        "eventName": "Nhận phòng (Check-in)",
        "time": "2026-07-01T14:00:00",
        "description": "Nhận Villa và bắt đầu kỳ nghỉ dưỡng."
      },
      {
        "eventName": "Vận động Cardio nhẹ nhàng",
        "time": "2026-07-02T06:30:00",
        "description": "Hoạt động đi bộ nhanh hoặc các bài tập vận động..."
      },
      {
        "eventName": "Bữa trưa Detox & Ít calorie",
        "time": "2026-07-02T12:00:00",
        "description": "Bữa trưa dinh dưỡng chuyên biệt..."
      },
      "...",
      {
        "eventName": "Trả phòng (Check-out)",
        "time": "2026-07-06T12:00:00",
        "description": "Hoàn tất thủ tục thanh toán Folio và check-out phòng."
      }
    ]
  }
}
```

**Response — 302 Redirect (No booking):**

```
Location: /guest/dashboard?error=no_booking
```

---

# 10. Bảng mã lỗi

| Code         | HTTP Status | Message (VI)                  | Trigger Condition                             |
| ------------ | ----------- | ----------------------------- | --------------------------------------------- |
| `ITIN-001` | 404         | Khách hàng không tồn tại | guestId từ JWT không tìm thấy trong DB    |
| `ITIN-002` | 404         | Chưa có đặt phòng nào   | guestId có trong DB nhưng chưa có booking |
| `ITIN-403` | 403         | Không đủ quyền            | Không phải GUEST role                       |

---

# 11. Quy trình Triển khai

## 11.1. Prerequisites

- [X] `ItineraryServiceImpl` đã implement
- [X] `BookingRepository.findByGuestId()` đã implement
- [X] Booking có `retreatPackage.typePackage` được set đúng
- [X] `ItineraryController` lấy `guestId` từ `@AuthenticationPrincipal` (ADR-UC10-002)
- [ ] ADR-UC10-001 và ADR-UC10-002 đã được Tech Lead review và Accepted

## 11.2. Pre-Migration Checklist

> UC10 là **read-only** — không có migration schema. Kiểm tra dữ liệu trước khi chạy:

- [X] Bảng `BOOKING` có cột `guest_id`, `package_id`, `booking_status`, `checkin_date`, `checkout_date`
- [X] Bảng `RETREAT_PACKAGE` có cột `type_package`
- [ ] Kiểm tra dữ liệu test có nhật giá trị `type_package` hợp lệ:

```sql
-- Verify các typePackage hiện tại trong DB
SELECT DISTINCT type_package FROM RETREAT_PACKAGE;
-- Expected: các giá trị như 'Stress Relief', 'Detox', 'Yoga', ...
```

## 11.2. Core Implementation (đã implement)

```java
// ItineraryServiceImpl.getTimelineForGuest()
@Transactional(readOnly = true)
public ItineraryTimelineDTO getTimelineForGuest(Integer guestId) {
    // 1. Load user
    User guest = userRepository.findById(guestId).orElseThrow(...);

    // 2. Load bookings — chọn booking active
    List<Booking> bookings = bookingRepository.findByGuestId(guestId);
    if (bookings.isEmpty()) throw new IllegalArgumentException("Chưa có đặt phòng");

    Booking activeBooking = bookings.stream()
        .filter(b -> "CHECKED-IN".equals(b.getBookingStatus()) || "CONFIRMED".equals(b.getBookingStatus()))
        .findFirst()
        .orElse(bookings.get(bookings.size() - 1));

    // 3. Generate events theo packageType
    String packageType = activeBooking.getRetreatPackage().getTypePackage().toLowerCase();
    List<TimelineEvent> events = new ArrayList<>();

    // 4. Check-in event (Day 1, 14:00)
    events.add(TimelineEvent.builder()
        .eventName("Nhận phòng (Check-in)")
        .time(start.atTime(14, 0))
        .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
        .build());

    // 5. Loop daily events
    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
        // Morning activity (06:30) — skip ngày đầu
        // Lunch (12:00) — skip ngày cuối
        // Spa (15:30) — skip ngày cuối
        // Dinner (18:30) — skip ngày cuối
    }

    // 6. Check-out event (Last day, 12:00)
    events.add(...checkout event...);

    // 7. Sort và return
    events.sort(Comparator.comparing(TimelineEvent::getTime));
    return ItineraryTimelineDTO.builder()
        .bookingId(activeBooking.getId())
        .guestName(guest.getFullName())
        .events(events)
        .build();
}
```

## 11.3. IDOR-Safe Controller Pattern

```java
// ItineraryController.java
@GetMapping("/booking/itinerary")
public String showItinerary(
    @AuthenticationPrincipal UserDetailsResponse currentUser,
    Model model
) {
    if (currentUser == null) return "redirect:/login";
    // ✅ guestId từ JWT — không từ request param (ADR-UC10-002)
    Integer guestId = currentUser.getId();
    try {
        ItineraryTimelineDTO dto = itineraryService.getTimelineForGuest(guestId);
        model.addAttribute("itinerary", dto);
        return "booking/itinerary";
    } catch (IllegalArgumentException e) {
        return "redirect:/guest/dashboard?error=no_booking";
    }
}
```

## 11.4. Deployment Checklist

- [ ] `ItineraryController` mapping đúng `/booking/itinerary`
- [ ] `booking/itinerary.html` Thymeleaf template tồn tại và render đúng
- [ ] SecurityConfig cho phép `GUEST` access `/booking/itinerary`
- [ ] Test IDOR: Guest A không xem được itinerary của Guest B
- [ ] Test No-Booking: Guest chưa đặt phòng nhận redirect đúng
- [ ] Kiểm tra các packageType khác nhau generate đúng template event

---



> UC10 là **read-only** — không có write operations, không cần rollback DB.
> Nếu có lỗi, chỉ cần restart service.

---

# 13. Kịch bản Kiểm thử

## 13.1. Unit Tests

### TC-UC10-001 — getTimelineForGuest với Detox package

```text
Feature: View Itinerary Timeline
  Background:
    Given test data classification: SYNTHETIC

  Scenario: Guest có booking Detox 5 ngày
    Given Booking(guestId=100, status=CONFIRMED, checkinDate=2026-07-01, checkoutDate=2026-07-06)
    And package.typePackage = "Detox"
    When getTimelineForGuest(100)
    Then kết quả có bookingId = 1001
    And guestName = "Nguyễn Văn A"
    And events có item đầu tiên là "Nhận phòng (Check-in)" lúc 14:00 ngày 2026-07-01
    And events có item cuối là "Trả phòng (Check-out)" lúc 12:00 ngày 2026-07-06
    And events chứa "Vận động Cardio nhẹ nhàng" (không phải "Yoga")
    And events chứa "Bữa trưa Detox & Ít calorie"
    And events được sắp xếp tăng dần theo time

  Scenario: packageType = "Stress Relief"
    Given package.typePackage = "Stress Relief"
    When getTimelineForGuest(100)
    Then events chứa "Thiền định & Thở chánh niệm"
    And events KHÔNG chứa "Luyện tập Yoga sáng"
```

### TC-UC10-002 — IDOR Prevention

```text
  Scenario: Guest A không thể xem itinerary của Guest B
    Given GuestA (id=100) và GuestB (id=200)
    And Booking(guestId=200, id=9999) tồn tại
    When GuestA gọi getTimelineForGuest(100)
    Then kết quả chỉ trả về booking của GuestA (guestId=100)
    And bookingId=9999 KHÔNG xuất hiện trong kết quả
```

### TC-UC10-003 — No booking

```text
  Scenario: Guest chưa có booking
    Given bookingRepository.findByGuestId(300) trả về []
    When getTimelineForGuest(300)
    Then throw IllegalArgumentException("Khách hàng chưa có bất kỳ lịch đặt phòng nào")
```

### TC-UC10-004 — Active booking selection

```text
  Scenario: Chọn booking active khi có nhiều booking
    Given Guest có 2 bookings: Booking-A(CHECKED_OUT) và Booking-B(CONFIRMED)
    When getTimelineForGuest(guestId)
    Then activeBooking = Booking-B (CONFIRMED)
    And itinerary dựa trên Booking-B
```

---

# 14. Phương pháp Xác minh

## 14.1. Manual UI Verification

1. Đăng nhập với account Guest đã có booking CONFIRMED
2. Mở `http://localhost:8080/booking/itinerary`
3. Kiểm tra:
   - ✅ Hiển thị đúng tên Guest
   - ✅ Sự kiện đầu tiên: "Nhận phòng" lúc 14:00 ngày check-in
   - ✅ Sự kiện cuối: "Trả phòng" lúc 12:00 ngày checkout
   - ✅ Events được sắp xếp tăng dần theo thời gian
   - ✅ Template đúng với packageType (Detox/Stress/Default)

## 14.2. IDOR Test

```bash
# Đăng nhập với Guest A (guestId=100)
# Thử xem itinerary (phải chỉ thấy booking của mình)
curl -X GET "http://localhost:8080/booking/itinerary" \
  -H "Cookie: JSESSIONID=[guestA-session]"
# Expected: Itinerary của guestId=100, không phải 200
```

## 14.3. Log / Audit Verification

```bash
# Kiểm tra không có PII (CCCD, healthProfile) trong log
grep -i "identifyCode\|cccd\|health" logs/application.log
# Expected: No output — UC10 không được ghi PII ra log

# Kiểm tra request log có guestId hợp lệ
grep "GET /booking/itinerary" logs/access.log | tail -20
# Expected: chỉ có GUEST role mới thành công (200), các role khác nhận 403
```

## 14.4. Tool-based Verification

```bash
# Kiểm tra JWT claim có guestId đúng
# Decode JWT token:
echo "[JWT_TOKEN]" | cut -d'.' -f2 | base64 -d
# Expected: { "sub": "[email]", "id": 100, "role": "GUEST" }

# Kiểm tra IDOR bằng Burp Suite / OWASP ZAP:
# 1. Đăng nhập GuestA → lấy session cookie
# 2. Intercept request GET /booking/itinerary
# 3. Thay session cookie của GuestB vào
# Expected: Trả về itinerary của GuestB (nếu có lỗi IDOR)
#           Hoặc trả về itinerary của GuestA (nếu bảo mật đúng)
```

---

# 15. Mẫu thử thực tế

```bash
# Xem itinerary của Guest đã đăng nhập
curl -X GET http://localhost:8080/booking/itinerary \
  -H "Cookie: JSESSIONID=[guest-session]"
# Expected: 200 — Thymeleaf render itinerary timeline

# Xem guest dashboard
curl -X GET http://localhost:8080/guest/dashboard \
  -H "Cookie: JSESSIONID=[guest-session]"
# Expected: 200 — Danh sách bookings của Guest
```

---

# 16. Bảng tổng hợp phân quyền

| Endpoint                   | GUEST       | RECEPTIONIST | THERAPIST | CHEF | ADMIN |
| -------------------------- | ----------- | ------------ | --------- | ---- | ----- |
| `GET /booking/itinerary` | ✅ Own only | ❌           | ❌        | ❌   | ❌    |
| `GET /guest/dashboard`   | ✅ Own only | ❌           | ❌        | ❌   | ❌    |

> ⚠️ **IDOR Constraint:** `Own only` nghĩa là Guest A chỉ được xem data của guestId bằng chính mình (lấy từ JWT). Không bao giờ nhận guestId từ URL param hay request body.

---

# PHỤ LỤC

## A. Glossary

| Thuật ngữ       | Định nghĩa                                                                           |
| ----------------- | --------------------------------------------------------------------------------------- |
| `Itinerary`     | Lịch trình kỳ nghỉ — danh sách sự kiện theo thứ tự thời gian                 |
| `TimelineEvent` | Một sự kiện trong itinerary: tên, thời điểm, mô tả                             |
| `IDOR`          | Insecure Direct Object Reference — lỗ hổng cho phép xem resource của người khác |
| `packageType`   | Loại gói: Yoga, Stress Relief, Detox, Weight Loss, Ayurveda...                        |
| `activeBooking` | Booking có status CONFIRMED hoặc CHECKED-IN                                           |

## B. Tài liệu tham chiếu

| Document                                 | Path                                                                                    |
| ---------------------------------------- | --------------------------------------------------------------------------------------- |
| SRS Module 2 Analysis                    | `01_Planning/Module 2/SRS_Module2_Analysis.md`                                        |
| ItineraryServiceImpl                     | `05_Development/.../booking/service/impl/ItineraryServiceImpl.java`                   |
| ItineraryService Interface               | `05_Development/.../booking/service/ItineraryService.java`                            |
| BookingRepository                        | `05_Development/.../booking/repository/BookingRepository.java`                        |
| OWASP API Security Top 10 — API-01 IDOR | https://owasp.org/API-Security/editions/2023/en/0xa1-broken-object-level-authorization/ |
