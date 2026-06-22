# UC11 — Schedule Spa/Therapy Session
## Implementation Plan + TDD Specification
### (Spring Boot 4 · Spring MVC · Thymeleaf · SQL Server)

> **Dựa trên:** SRS_Document.md (UC11/UC12), EDS_TEMPLATE_V2.0, TDD_TEMPLATE_V1, codebase hiện tại
> **Tech Stack:** Spring Boot 4.0.6 · Spring MVC · Thymeleaf · Spring Data JPA · SQL Server · JUnit 5 · Mockito

---

## Kiến trúc tổng thể

```
Browser (Thymeleaf)
    │
    ├─ GET  /spa/schedule?bookingId=&serviceId=   → SpaController → render spa/scheduler.html
    │                                               (Thymeleaf + Model)
    ├─ GET  /spa/slots  (AJAX fetch)              → SpaController @ResponseBody → JSON slots
    │                                               (slot grid render bằng JS thuần)
    └─ POST /spa/bookings                         → SpaController → PRG redirect
           └─ success → redirect:/spa/bookings/{id}/success
           └─ error   → redirect:/spa/schedule?error=SPA-001
```

**Pattern chính:** `POST / Redirect / GET` — tránh double-submit khi refresh.  
**Slot grid:** Hybrid — trang load bằng Thymeleaf, slot fetch bằng AJAX `fetch()` nhỏ → `@ResponseBody` JSON endpoint (không cần SPA framework).

---

## Hiện trạng Codebase

### ✅ Đã có
| File | Package | Trạng thái |
|------|---------|-----------|
| `TreatmentBooking.java` | `spa.entity` | ✅ Entity đủ fields |
| `Schedule.java` | `spa.entity` | ✅ startTime, endTime, therapist, room |
| `Therapist.java` | `spa.entity` | ✅ therapistCode, status |
| `TreatmentRoom.java` | `spa.entity` | ✅ status, isDelete |
| `TreatmentService.java` | `spa.entity` | ✅ durationMinutes, isAvailable |
| `Booking.java` | `booking.entity` | ✅ bookingStatus, guestId |
| `BaseEntity.java` | `common.entity` | ✅ audit fields |

### ❌ Chưa có — Cần tạo mới
Toàn bộ business layer + view layer của module `spa`:

```
spa/
├── controller/     ← chưa có
├── dto/            ← chưa có
├── exception/      ← chưa có
├── repository/     ← chưa có
└── service/        ← chưa có

resources/templates/spa/   ← chưa có
```

---

## Open Questions

> [!IMPORTANT]
> **Q1 — Session / Auth**: Module `auth` chưa có security layer. UC11 cần `guestId` từ session đăng nhập. **Workaround**: Đọc từ `HttpSession.getAttribute("userId")`. Cần confirm với auth team khi họ implement login.

> [!IMPORTANT]
> **Q2 — BR-05 Eligibility**: `RETREAT_PACKAGE.services` là `NVARCHAR(MAX)` dạng text mô tả, không có FK mapping Package ↔ TreatmentService. **Plan tạm thời**: Skip hard eligibility check — chỉ check Booking đang active (`Confirmed` / `Checked-In`).

> [!NOTE]
> **Q3 — Notification (BR-17)**: `NotificationService` sẽ là stub `@Async` + log. Không integrate Calendar API thật ở sprint này.

---

## Proposed Changes

---

### Layer 1 — Repository

#### [NEW] `ScheduleRepository.java`
```java
// spa/repository/ScheduleRepository.java
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // Tìm therapist_code nào đang bị conflict trong khoảng [startTime, endTime)
    @Query("""
        SELECT s.therapist.therapistCode FROM Schedule s
        WHERE s.isDelete = false
          AND s.therapist.therapistCode IN :therapistCodes
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
    """)
    List<String> findConflictingTherapistCodes(
        List<String> therapistCodes,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    // Tìm room_id nào đang bị conflict trong khoảng [startTime, endTime)
    @Query("""
        SELECT s.room.id FROM Schedule s
        WHERE s.isDelete = false
          AND s.room.id IN :roomIds
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
    """)
    List<Integer> findConflictingRoomIds(
        List<Integer> roomIds,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    // Kiểm tra conflict của 1 therapist cụ thể (Branch B — requested therapist)
    @Query("""
        SELECT COUNT(s) > 0 FROM Schedule s
        WHERE s.isDelete = false
          AND s.therapist.therapistCode = :therapistCode
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
    """)
    boolean existsConflictForTherapist(
        String therapistCode,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
}
```

#### [NEW] `TherapistRepository.java`
```java
public interface TherapistRepository extends JpaRepository<Therapist, Integer> {
    List<Therapist> findByStatus(String status);
    Optional<Therapist> findByTherapistCode(String code);
}
```

#### [NEW] `TreatmentRoomRepository.java`
```java
public interface TreatmentRoomRepository extends JpaRepository<TreatmentRoom, Integer> {
    List<TreatmentRoom> findByStatusAndIsDeleteFalse(String status);
}
```

#### [NEW] `TreatmentServiceRepository.java`
```java
public interface TreatmentServiceRepository extends JpaRepository<TreatmentService, Integer> {
    Optional<TreatmentService> findByIdAndIsAvailableTrueAndIsDeleteFalse(Integer id);
}
```

#### [NEW] `TreatmentBookingRepository.java`
```java
public interface TreatmentBookingRepository extends JpaRepository<TreatmentBooking, Integer> {
}
```

#### Sử dụng lại `BookingRepository` (từ `booking` module)
```java
// booking/repository/BookingRepository.java  ← tạo nếu chưa có
Optional<Booking> findByIdAndIsDeleteFalse(Integer id);
```

---

### Layer 2 — DTOs (POJO — không dùng Java record vì cần no-arg constructor cho @ModelAttribute)

#### [NEW] `SpaBookingForm.java` — Form data từ Thymeleaf
```java
// spa/dto/SpaBookingForm.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaBookingForm {
    private Integer bookingId;           // hidden field
    private Integer serviceId;           // hidden field
    private String startTime;            // "2026-06-20T14:00" — từ hidden input JS fill
    private String requestedTherapistCode; // nullable — null = auto-assign
    private String note;                 // optional
}
```

#### [NEW] `TimeSlotDto.java` — JSON trả về cho AJAX slot grid
```java
// spa/dto/TimeSlotDto.java
@Data
@AllArgsConstructor
public class TimeSlotDto {
    private String startTime;   // "14:00"
    private String endTime;     // "15:00"
    private boolean available;
    private String label;       // "14:00 - 15:00" hoặc "Hết chỗ"
}
```

#### [NEW] `SpaBookingConfirmDto.java` — Dữ liệu hiển thị trang success
```java
// spa/dto/SpaBookingConfirmDto.java
@Data
@AllArgsConstructor
public class SpaBookingConfirmDto {
    private Integer treatmentId;
    private String  serviceName;
    private String  startTime;
    private String  endTime;
    private String  therapistCode;
    private String  roomName;
}
```

---

### Layer 3 — Exception Classes

#### [NEW] `ResourceUnavailableException.java`
```java
// spa/exception/ResourceUnavailableException.java
// → redirect về form với param ?error=SPA-001
public class ResourceUnavailableException extends RuntimeException {
    public ResourceUnavailableException() {
        super("Không tìm thấy Therapist hoặc Phòng điều trị khả dụng");
    }
}
```

#### [NEW] `BookingNotFoundException.java`  → 404 page hoặc redirect với error
#### [NEW] `BookingOwnershipException.java` → redirect với ?error=SPA-003
#### [NEW] `InvalidBookingStateException.java` → redirect với ?error=SPA-004

---

### Layer 4 — Service (Logic không đổi so với plan cũ)

#### [NEW] `MatchResult.java` (record nội bộ)
```java
// spa/service/MatchResult.java
public record MatchResult(Therapist therapist, TreatmentRoom room) {}
```

#### [NEW] `ResourceMatchingService.java` (interface)
```java
// spa/service/ResourceMatchingService.java
public interface ResourceMatchingService {

    /** Trả về danh sách TimeSlot cho một ngày — gọi bởi AJAX endpoint */
    List<TimeSlotDto> getAvailableSlots(Integer serviceId, LocalDate date);

    /**
     * Tìm và LOCK therapist + room trong @Transactional SERIALIZABLE.
     * Gọi trong bookSession().
     * @throws ResourceUnavailableException nếu không còn slot
     */
    MatchResult matchAndLock(
        Integer serviceId,
        LocalDateTime startTime,
        String requestedTherapistCode   // null = auto-assign
    );
}
```

#### [NEW] `ResourceMatchingServiceImpl.java`

**Algorithm `matchAndLock()` — double-booking prevention:**
```
INPUT: serviceId, startTime, requestedTherapistCode

1. Lấy service → tính endTime = startTime + durationMinutes
2. [Branch A] IF requestedTherapistCode == null:
       allAvailable = therapistRepo.findByStatus("Available")
       conflicted   = scheduleRepo.findConflictingTherapistCodes(allCodes, start, end)
       therapist    = first(allAvailable WHERE code NOT IN conflicted)
   [Branch B] IF requestedTherapistCode != null:
       IF scheduleRepo.existsConflictForTherapist(code, start, end) → throw
       therapist = therapistRepo.findByTherapistCode(code)

3. allRooms   = roomRepo.findByStatusAndIsDeleteFalse("Available")
   conflicted = scheduleRepo.findConflictingRoomIds(allRoomIds, start, end)
   room       = first(allRooms WHERE id NOT IN conflicted)

4. IF therapist == null OR room == null → throw ResourceUnavailableException
5. RETURN MatchResult(therapist, room)
```

**Algorithm `getAvailableSlots()` — cho AJAX:**
```
INPUT: serviceId, date

1. Lấy service.durationMinutes
2. Tạo các slot từ 08:00 đến 20:00, bước = durationMinutes
3. Với mỗi slot:
     available = (findAvailableTherapist(slot) != null)
              AND (findAvailableRoom(slot) != null)
4. RETURN List<TimeSlotDto> với available = true/false
```

#### [NEW] `SpaSchedulingService.java` (interface)
```java
public interface SpaSchedulingService {
    List<TimeSlotDto> getAvailability(Integer serviceId, LocalDate date);

    /**
     * Đặt lịch UC11. Gọi trong @Transactional SERIALIZABLE.
     * @return treatmentId vừa tạo
     * @throws BookingNotFoundException nếu bookingId không tồn tại
     * @throws BookingOwnershipException nếu guestId không khớp
     * @throws InvalidBookingStateException nếu booking không active
     * @throws ResourceUnavailableException nếu không còn slot
     */
    Integer bookSession(Integer guestId, SpaBookingForm form);
}
```

#### [NEW] `SpaSchedulingServiceImpl.java`

**`bookSession()` flow:**
```
1. Validate booking tồn tại (BookingNotFoundException)
2. Validate guestId == booking.guestId (BookingOwnershipException)
3. Validate bookingStatus IN ['Confirmed','Checked-In'] (InvalidBookingStateException)
4. Validate startTime > LocalDateTime.now() (InvalidBookingStateException)
5. Parse startTime String → LocalDateTime
6. [TRONG @Transactional SERIALIZABLE]:
     MatchResult match = resourceMatchingService.matchAndLock(...)
     TreatmentBooking tb = save(TreatmentBooking{..., status="Scheduled"})
     Schedule sc = save(Schedule{therapist, room, startTime, endTime})
7. [ASYNC] notificationService.sendSpaConfirmation(tb.getId())
8. RETURN tb.getId()
```

#### [NEW] `NotificationService.java` (stub)
```java
@Service
public class NotificationService {
    @Async
    public void sendSpaConfirmation(Integer treatmentId) {
        // TODO: integrate Calendar API
        log.info("[NOTIFICATION] Spa confirmation sent for treatmentId={}", treatmentId);
    }
}
```

---

### Layer 5 — Controller (Spring MVC)

#### [NEW] `SpaController.java`

```java
@Controller
@RequestMapping("/spa")
@RequiredArgsConstructor
public class SpaController {

    private final SpaSchedulingService spaService;
    private final TreatmentServiceRepository serviceRepo;

    // ─────────────────────────────────────────────
    // GET /spa/schedule?bookingId=1&serviceId=1
    // Render trang đặt lịch
    // ─────────────────────────────────────────────
    @GetMapping("/schedule")
    public String showScheduler(
            @RequestParam Integer bookingId,
            @RequestParam Integer serviceId,
            @RequestParam(required = false) String error,
            Model model,
            HttpSession session) {

        // Auth check
        Integer guestId = (Integer) session.getAttribute("userId");
        if (guestId == null) return "redirect:/auth/login";

        TreatmentService svc = serviceRepo
            .findByIdAndIsAvailableTrueAndIsDeleteFalse(serviceId)
            .orElseThrow(() -> new BookingNotFoundException("Service not found"));

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("service", svc);
        model.addAttribute("spaBookingForm", new SpaBookingForm());
        model.addAttribute("error", error);  // hiển thị error từ redirect
        return "spa/scheduler"; // → templates/spa/scheduler.html
    }

    // ─────────────────────────────────────────────
    // GET /spa/slots?serviceId=1&date=2026-06-20
    // AJAX endpoint — trả JSON slot grid
    // ─────────────────────────────────────────────
    @GetMapping("/slots")
    @ResponseBody
    public List<TimeSlotDto> getSlots(
            @RequestParam Integer serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return spaService.getAvailability(serviceId, date);
    }

    // ─────────────────────────────────────────────
    // POST /spa/bookings  (PRG pattern)
    // Xử lý form đặt lịch
    // ─────────────────────────────────────────────
    @PostMapping("/bookings")
    public String submitBooking(
            @ModelAttribute SpaBookingForm form,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Integer guestId = (Integer) session.getAttribute("userId");
        if (guestId == null) return "redirect:/auth/login";

        try {
            Integer treatmentId = spaService.bookSession(guestId, form);
            redirectAttrs.addFlashAttribute("treatmentId", treatmentId);
            return "redirect:/spa/bookings/" + treatmentId + "/success";

        } catch (ResourceUnavailableException e) {
            return "redirect:/spa/schedule?bookingId=" + form.getBookingId()
                + "&serviceId=" + form.getServiceId()
                + "&error=SPA-001";
        } catch (BookingOwnershipException e) {
            return "redirect:/spa/schedule?bookingId=" + form.getBookingId()
                + "&serviceId=" + form.getServiceId()
                + "&error=SPA-003";
        } catch (InvalidBookingStateException e) {
            return "redirect:/spa/schedule?bookingId=" + form.getBookingId()
                + "&serviceId=" + form.getServiceId()
                + "&error=SPA-004";
        }
    }

    // ─────────────────────────────────────────────
    // GET /spa/bookings/{id}/success
    // Trang xác nhận thành công
    // ─────────────────────────────────────────────
    @GetMapping("/bookings/{id}/success")
    public String successPage(
            @PathVariable Integer id,
            @ModelAttribute("treatmentId") Integer treatmentId,
            Model model) {
        // Lấy chi tiết từ DB để hiển thị
        // ...
        return "spa/booking-success"; // → templates/spa/booking-success.html
    }
}
```

---

### Layer 6 — Thymeleaf Templates

#### [NEW] `templates/spa/scheduler.html` — skeleton
```html
<!-- resources/templates/spa/scheduler.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><title>Đặt lịch Spa</title></head>
<body>

  <!-- Error banner -->
  <div th:if="${error}" class="alert alert-danger">
    <span th:switch="${error}">
      <span th:case="'SPA-001'">Không có Therapist hoặc Phòng khả dụng. Vui lòng chọn thời gian khác.</span>
      <span th:case="'SPA-003'">Bạn không có quyền thực hiện thao tác này.</span>
      <span th:case="'SPA-004'">Thông tin đặt lịch không hợp lệ.</span>
    </span>
  </div>

  <!-- Service Info (read-only) -->
  <div>
    <h3 th:text="${service.serviceName}"></h3>
    <p th:text="${service.durationMinutes} + ' phút'"></p>
  </div>

  <!-- Date Picker -->
  <input type="date" id="datePicker" min="[[${#temporals.format(#temporals.createNow(), 'yyyy-MM-dd')}]]" />

  <!-- Slot Grid (populated by JS) -->
  <div id="slotGrid"></div>

  <!-- Hidden Form (JS fills startTime trước khi submit) -->
  <form th:action="@{/spa/bookings}" th:object="${spaBookingForm}" method="post">
    <input type="hidden" th:field="*{bookingId}" th:value="${bookingId}" />
    <input type="hidden" th:field="*{serviceId}" th:value="${service.id}" />
    <input type="hidden" id="startTimeInput" th:field="*{startTime}" />
    <input type="hidden" th:field="*{requestedTherapistCode}" />
    <textarea th:field="*{note}" placeholder="Ghi chú (tùy chọn)"></textarea>
    <button type="submit" id="confirmBtn" disabled>Xác nhận đặt lịch</button>
  </form>

  <script th:inline="javascript">
    const serviceId = [[${service.id}]];

    document.getElementById('datePicker').addEventListener('change', function() {
      const date = this.value;
      fetch(`/spa/slots?serviceId=${serviceId}&date=${date}`)
        .then(r => r.json())
        .then(slots => renderSlots(slots));
    });

    function renderSlots(slots) {
      const grid = document.getElementById('slotGrid');
      grid.innerHTML = '';
      slots.forEach(slot => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = slot.label;
        btn.disabled = !slot.available;
        if (slot.available) {
          btn.addEventListener('click', () => selectSlot(slot.startTime, btn));
        }
        grid.appendChild(btn);
      });
    }

    function selectSlot(startTime, btn) {
      document.getElementById('startTimeInput').value = startTime;
      document.getElementById('confirmBtn').disabled = false;
      // highlight selected
      document.querySelectorAll('#slotGrid button').forEach(b => b.classList.remove('selected'));
      btn.classList.add('selected');
    }
  </script>
</body>
</html>
```

#### [NEW] `templates/spa/booking-success.html` — trang xác nhận (skeleton)

---

### Layer 7 — Error Code Mapping

| Code | Trigger | Redirect |
|------|---------|---------|
| `SPA-001` | Không có Therapist/Room | `?error=SPA-001` → MSG-10 |
| `SPA-002` | Booking không tồn tại | `/error/404` |
| `SPA-003` | Guest không sở hữu booking | `?error=SPA-003` → MSG-18 |
| `SPA-004` | Booking không active / thời gian quá khứ | `?error=SPA-004` |
| `SPA-005` | Lỗi hệ thống | `/error/500` |

---

## TDD Specification — Test Cases

> **Quy ước TDD**: Viết test TRƯỚC → 🔴 FAIL → implement → 🟢 PASS → 🔵 refactor  
> **Test Data**: Chỉ dùng `SYNTHETIC` — KHÔNG dùng production PII  
> **Framework**: JUnit 5 + Mockito (Unit) · `@WebMvcTest` (Controller) · H2 + `@SpringBootTest` (Integration)

### Test Fixtures

| ID | Type | Value |
|----|------|-------|
| FX-001 | Object | `Booking { id=1, guestId=100, bookingStatus="Checked-In" }` |
| FX-002 | Object | `Booking { id=2, guestId=100, bookingStatus="Cancelled" }` |
| FX-003 | Object | `TreatmentService { id=1, durationMinutes=60, isAvailable=true }` |
| FX-004 | Object | `Therapist { therapistCode="TH001", status="Available" }` |
| FX-005 | Object | `TreatmentRoom { id=1, status="Available", isDelete=false }` |
| FX-006 | Object | `Schedule { therapist=TH001, room=1, start=10:00, end=11:00 }` — conflict |
| FX-007 | Input | `startTime = ngày mai 14:00` |
| FX-008 | Input | `startTime = hôm qua 14:00` — invalid |
| FX-009 | Session | `HttpSession { "userId" = 100 }` |

---

### Unit Tests — `ResourceMatchingServiceImpl`

#### SPA-TC-001 — Auto-assign: Có Therapist + Room → trả MatchResult

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@ExtendWith(MockitoExtension.class)
class ResourceMatchingServiceImplTest {

    @Mock ScheduleRepository scheduleRepo;
    @Mock TherapistRepository therapistRepo;
    @Mock TreatmentRoomRepository roomRepo;
    @Mock TreatmentServiceRepository serviceRepo;
    @InjectMocks ResourceMatchingServiceImpl service;

    @Test
    void matchAndLock_autoAssign_success() {
        // Arrange
        var svc = TreatmentService.builder().id(1).durationMinutes(60).build();
        when(serviceRepo.findByIdAndIsAvailableTrueAndIsDeleteFalse(1))
            .thenReturn(Optional.of(svc));
        when(therapistRepo.findByStatus("Available"))
            .thenReturn(List.of(therapist("TH001")));
        when(scheduleRepo.findConflictingTherapistCodes(any(), any(), any()))
            .thenReturn(List.of());                        // không conflict
        when(roomRepo.findByStatusAndIsDeleteFalse("Available"))
            .thenReturn(List.of(room(1)));
        when(scheduleRepo.findConflictingRoomIds(any(), any(), any()))
            .thenReturn(List.of());                        // không conflict

        // Act
        MatchResult result = service.matchAndLock(1, tomorrowAt14(), null);

        // Assert
        assertNotNull(result);
        assertEquals("TH001", result.therapist().getTherapistCode());
        assertEquals(1, result.room().getId());
    }
}
```

---

#### SPA-TC-002 — Tất cả Therapist bị conflict → `ResourceUnavailableException`

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void matchAndLock_allTherapistsBusy_throwsException() {
    when(serviceRepo.findByIdAndIsAvailableTrueAndIsDeleteFalse(1))
        .thenReturn(Optional.of(svc60min));
    when(therapistRepo.findByStatus("Available"))
        .thenReturn(List.of(therapist("TH001")));
    when(scheduleRepo.findConflictingTherapistCodes(List.of("TH001"), any(), any()))
        .thenReturn(List.of("TH001"));           // TH001 conflict

    assertThrows(ResourceUnavailableException.class,
        () -> service.matchAndLock(1, tomorrowAt14(), null));
}
```

---

#### SPA-TC-003 — Có Therapist nhưng tất cả Room bị conflict → `ResourceUnavailableException`

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void matchAndLock_allRoomsBusy_throwsException() {
    // therapist available
    when(scheduleRepo.findConflictingTherapistCodes(any(), any(), any()))
        .thenReturn(List.of());
    // room conflict
    when(roomRepo.findByStatusAndIsDeleteFalse("Available"))
        .thenReturn(List.of(room(1)));
    when(scheduleRepo.findConflictingRoomIds(List.of(1), any(), any()))
        .thenReturn(List.of(1));

    assertThrows(ResourceUnavailableException.class,
        () -> service.matchAndLock(1, tomorrowAt14(), null));
}
```

---

#### SPA-TC-004 — Requested Therapist cụ thể, không conflict → dùng therapist đó

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void matchAndLock_specificTherapist_noConflict_usesRequestedTherapist() {
    when(scheduleRepo.existsConflictForTherapist("TH001", any(), any()))
        .thenReturn(false);
    when(therapistRepo.findByTherapistCode("TH001"))
        .thenReturn(Optional.of(therapist("TH001")));
    // room available
    when(scheduleRepo.findConflictingRoomIds(any(), any(), any()))
        .thenReturn(List.of());

    MatchResult result = service.matchAndLock(1, tomorrowAt14(), "TH001");

    assertEquals("TH001", result.therapist().getTherapistCode());
    // Verify không gọi findByStatus (không auto-search therapist)
    verify(therapistRepo, never()).findByStatus(any());
}
```

---

#### SPA-TC-005 — Requested Therapist bị conflict → `ResourceUnavailableException`

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void matchAndLock_specificTherapist_conflict_throwsException() {
    when(scheduleRepo.existsConflictForTherapist("TH001", any(), any()))
        .thenReturn(true);   // conflict!

    assertThrows(ResourceUnavailableException.class,
        () -> service.matchAndLock(1, tomorrowAt14(), "TH001"));
}
```

---

### Unit Tests — `SpaSchedulingServiceImpl`

#### SPA-TC-006 — Happy Path: bookSession thành công, trả treatmentId

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void bookSession_validRequest_returnsTreatmentId() {
    when(bookingRepo.findByIdAndIsDeleteFalse(1))
        .thenReturn(Optional.of(booking(1, 100, "Checked-In")));   // FX-001
    when(serviceRepo.findByIdAndIsAvailableTrueAndIsDeleteFalse(1))
        .thenReturn(Optional.of(svc60min));
    when(resourceMatchingService.matchAndLock(any(), any(), any()))
        .thenReturn(new MatchResult(therapist("TH001"), room(1)));
    when(treatmentBookingRepo.save(any()))
        .thenReturn(TreatmentBooking.builder().id(99).status("Scheduled").build());
    when(scheduleRepo.save(any()))
        .thenReturn(Schedule.builder().id(10).build());

    SpaBookingForm form = new SpaBookingForm(1, 1, tomorrowAt14Str(), null, null);
    Integer id = service.bookSession(100, form);

    assertEquals(99, id);
    verify(notificationService, times(1)).sendSpaConfirmation(99);
}
```

---

#### SPA-TC-007 — Booking không tồn tại → `BookingNotFoundException`

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void bookSession_bookingNotFound_throwsException() {
    when(bookingRepo.findByIdAndIsDeleteFalse(999))
        .thenReturn(Optional.empty());

    assertThrows(BookingNotFoundException.class,
        () -> service.bookSession(100,
            new SpaBookingForm(999, 1, tomorrowAt14Str(), null, null)));
}
```

---

#### SPA-TC-008 — guestId không khớp Booking → `BookingOwnershipException`

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void bookSession_guestNotOwner_throwsException() {
    // Booking thuộc guestId=200, nhưng caller guestId=100
    when(bookingRepo.findByIdAndIsDeleteFalse(1))
        .thenReturn(Optional.of(booking(1, 200, "Checked-In")));

    assertThrows(BookingOwnershipException.class,
        () -> service.bookSession(100,
            new SpaBookingForm(1, 1, tomorrowAt14Str(), null, null)));
}
```

---

#### SPA-TC-009 — Booking status Cancelled → `InvalidBookingStateException`

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void bookSession_bookingCancelled_throwsException() {
    when(bookingRepo.findByIdAndIsDeleteFalse(2))
        .thenReturn(Optional.of(booking(2, 100, "Cancelled")));  // FX-002

    assertThrows(InvalidBookingStateException.class,
        () -> service.bookSession(100,
            new SpaBookingForm(2, 1, tomorrowAt14Str(), null, null)));
}
```

---

#### SPA-TC-010 — startTime trong quá khứ → `InvalidBookingStateException`

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void bookSession_pastStartTime_throwsException() {
    when(bookingRepo.findByIdAndIsDeleteFalse(1))
        .thenReturn(Optional.of(booking(1, 100, "Checked-In")));

    // FX-008: startTime = hôm qua
    assertThrows(InvalidBookingStateException.class,
        () -> service.bookSession(100,
            new SpaBookingForm(1, 1, yesterdayAt14Str(), null, null)));
}
```

---

#### SPA-TC-011 — NotificationService fail → Booking VẪN thành công (BR-17)

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void bookSession_notificationFails_bookingStillSucceeds() {
    // Arrange — setup đầy đủ như TC-006
    // Notification throws
    doThrow(RuntimeException.class)
        .when(notificationService).sendSpaConfirmation(any());

    // Act — KHÔNG throw exception
    assertDoesNotThrow(() -> service.bookSession(100, validForm));

    // Booking vẫn được tạo
    verify(treatmentBookingRepo, times(1)).save(any());
}
```

> ⚠️ **Implementation note**: `notificationService.sendSpaConfirmation()` phải `@Async` + wrapped trong try-catch để exception không bubble lên `bookSession()`.

---

### Controller Tests — `@WebMvcTest`

#### SPA-TC-012 — GET /spa/schedule: Guest chưa login → redirect /auth/login

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@WebMvcTest(SpaController.class)
class SpaControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean SpaSchedulingService spaService;
    @MockBean TreatmentServiceRepository serviceRepo;

    @Test
    void showScheduler_notLoggedIn_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/spa/schedule")
                .param("bookingId", "1")
                .param("serviceId", "1"))
               // không có session userId
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"));
    }
}
```

---

#### SPA-TC-013 — GET /spa/schedule: Guest đã login → render scheduler.html với model đúng

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void showScheduler_loggedIn_rendersTemplate() throws Exception {
    var svc = TreatmentService.builder().id(1).serviceName("Massage").durationMinutes(60).build();
    when(serviceRepo.findByIdAndIsAvailableTrueAndIsDeleteFalse(1))
        .thenReturn(Optional.of(svc));

    mockMvc.perform(get("/spa/schedule")
                .param("bookingId", "1")
                .param("serviceId", "1")
                .sessionAttr("userId", 100))
           .andExpect(status().isOk())
           .andExpect(view().name("spa/scheduler"))
           .andExpect(model().attributeExists("service"))
           .andExpect(model().attribute("bookingId", 1))
           .andExpect(model().attributeExists("spaBookingForm"));
}
```

---

#### SPA-TC-014 — GET /spa/slots: Trả JSON danh sách slot

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@Test
void getSlots_returnsJsonList() throws Exception {
    var slots = List.of(
        new TimeSlotDto("2026-06-20T14:00", "2026-06-20T15:00", true,  "14:00 - 15:00"),
        new TimeSlotDto("2026-06-20T15:00", "2026-06-20T16:00", false, "Hết chỗ")
    );
    when(spaService.getAvailability(1, LocalDate.of(2026,6,20))).thenReturn(slots);

    mockMvc.perform(get("/spa/slots")
                .param("serviceId", "1")
                .param("date", "2026-06-20"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].available").value(true))
           .andExpect(jsonPath("$[1].label").value("Hết chỗ"));
}
```

---

#### SPA-TC-015 — POST /spa/bookings: Thành công → redirect success page

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void submitBooking_success_redirectsToSuccessPage() throws Exception {
    when(spaService.bookSession(eq(100), any())).thenReturn(99);

    mockMvc.perform(post("/spa/bookings")
                .sessionAttr("userId", 100)
                .param("bookingId", "1")
                .param("serviceId", "1")
                .param("startTime", tomorrowAt14Str()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/spa/bookings/99/success"));
}
```

---

#### SPA-TC-016 — POST /spa/bookings: ResourceUnavailableException → redirect với error=SPA-001

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void submitBooking_noResource_redirectsWithError() throws Exception {
    when(spaService.bookSession(any(), any()))
        .thenThrow(new ResourceUnavailableException());

    mockMvc.perform(post("/spa/bookings")
                .sessionAttr("userId", 100)
                .param("bookingId", "1")
                .param("serviceId", "1")
                .param("startTime", tomorrowAt14Str()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("/spa/schedule*error=SPA-001*"));
}
```

---

#### SPA-TC-017 — POST /spa/bookings: BookingOwnershipException → redirect với error=SPA-003

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void submitBooking_ownershipViolation_redirectsWithError() throws Exception {
    when(spaService.bookSession(any(), any()))
        .thenThrow(new BookingOwnershipException());

    mockMvc.perform(post("/spa/bookings")
                .sessionAttr("userId", 100)
                .param("bookingId", "1")
                .param("serviceId", "1")
                .param("startTime", tomorrowAt14Str()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("/spa/schedule*error=SPA-003*"));
}
```

---

### Integration Test

#### SPA-TC-INT-001 — Full Flow: bookSession tạo TreatmentBooking + Schedule trong H2

**Severity:** HIGH · **TDD Phase:** 🔴

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")   // application-test.properties → H2
class SpaSchedulingIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired TreatmentBookingRepository bookingRepo;
    @Autowired ScheduleRepository scheduleRepo;

    @BeforeEach
    void seed(@Autowired EntityManager em) {
        // Insert: Role, User(guestId=100), Booking(id=1, guestId=100, status=Checked-In)
        // Insert: TreatmentService(id=1, duration=60)
        // Insert: Therapist(TH001, Available, therapist_id=101)
        // Insert: TreatmentRoom(id=1, Available)
    }

    @Test
    void fullBookingFlow_createsRecordsInDb() throws Exception {
        mockMvc.perform(post("/spa/bookings")
                    .sessionAttr("userId", 100)
                    .param("bookingId", "1")
                    .param("serviceId", "1")
                    .param("startTime", "2026-06-20T14:00"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("/spa/bookings/*/success"));

        // DB assertions
        List<TreatmentBooking> tbs = bookingRepo.findAll();
        assertEquals(1, tbs.size());
        assertEquals("Scheduled", tbs.get(0).getStatus());

        List<Schedule> schedules = scheduleRepo.findAll();
        assertEquals(1, schedules.size());
        assertNotNull(schedules.get(0).getTherapist());
        assertNotNull(schedules.get(0).getRoom());
        // endTime = startTime + 60 phút
        assertEquals(
            LocalDateTime.of(2026, 6, 20, 15, 0),
            schedules.get(0).getEndTime()
        );
    }
}
```

---

#### SPA-TC-INT-002 — Concurrent booking: 2 request cùng slot → chỉ 1 thành công

**Severity:** CRITICAL · **TDD Phase:** 🔴

```java
@Test
void concurrentBooking_onlyOneSucceeds() throws InterruptedException {
    // Chỉ có 1 Therapist + 1 Room khả dụng (đã seed)
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger successCount   = new AtomicInteger(0);
    AtomicInteger redirectErrors = new AtomicInteger(0);

    Runnable task = () -> {
        try {
            latch.await();
            var response = mockMvc.perform(post("/spa/bookings")
                    .sessionAttr("userId", 100)
                    .param("bookingId", "1")
                    .param("serviceId", "1")
                    .param("startTime", "2026-06-20T14:00"))
                .andReturn().getResponse();

            String location = response.getHeader("Location");
            if (location != null && location.contains("/success")) successCount.incrementAndGet();
            if (location != null && location.contains("error=SPA-001")) redirectErrors.incrementAndGet();
        } catch (Exception ignored) {}
    };

    Thread t1 = new Thread(task), t2 = new Thread(task);
    t1.start(); t2.start();
    latch.countDown();
    t1.join(); t2.join();

    assertEquals(1, successCount.get(),   "Chỉ 1 booking được tạo");
    assertEquals(1, redirectErrors.get(), "1 request bị conflict → SPA-001");
    assertEquals(1, scheduleRepo.count(), "DB chỉ có 1 Schedule record");
}
```

---

## Red-Green-Refactor Tracker

| TC ID | Class được test | 🔴 RED | 🟢 GREEN (commit) | 🔵 REFACTOR |
|-------|-----------------|--------|--------------------|-------------|
| SPA-TC-001 | `ResourceMatchingServiceImpl` | [ ] | — | — |
| SPA-TC-002 | `ResourceMatchingServiceImpl` | [ ] | — | — |
| SPA-TC-003 | `ResourceMatchingServiceImpl` | [ ] | — | — |
| SPA-TC-004 | `ResourceMatchingServiceImpl` | [ ] | — | — |
| SPA-TC-005 | `ResourceMatchingServiceImpl` | [ ] | — | — |
| SPA-TC-006 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-007 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-008 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-009 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-010 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-011 | `SpaSchedulingServiceImpl`    | [ ] | — | — |
| SPA-TC-012 | `SpaController`               | [ ] | — | — |
| SPA-TC-013 | `SpaController`               | [ ] | — | — |
| SPA-TC-014 | `SpaController`               | [ ] | — | — |
| SPA-TC-015 | `SpaController`               | [ ] | — | — |
| SPA-TC-016 | `SpaController`               | [ ] | — | — |
| SPA-TC-017 | `SpaController`               | [ ] | — | — |
| SPA-TC-INT-001 | Full Integration          | [ ] | — | — |
| SPA-TC-INT-002 | Concurrency               | [ ] | — | — |

---

## Thứ tự Implementation

```
Phase 1 — Repository Layer
  ├── BookingRepository.java         (nếu chưa có trong booking module)
  ├── ScheduleRepository.java        (JPQL overlap queries — critical)
  ├── TherapistRepository.java
  ├── TreatmentRoomRepository.java
  ├── TreatmentServiceRepository.java
  └── TreatmentBookingRepository.java

Phase 2 — Exception + DTO
  ├── ResourceUnavailableException.java
  ├── BookingNotFoundException.java
  ├── BookingOwnershipException.java
  ├── InvalidBookingStateException.java
  ├── SpaBookingForm.java
  ├── TimeSlotDto.java
  └── SpaBookingConfirmDto.java

Phase 3 — Service (TDD: viết test TRƯỚC khi implement)
  ├── [TEST 🔴] ResourceMatchingServiceTest     → TC-001–005
  ├── ResourceMatchingServiceImpl.java          → implement
  ├── [TEST 🔴] SpaSchedulingServiceTest        → TC-006–011
  ├── SpaSchedulingServiceImpl.java             → implement
  └── NotificationService.java                  (stub @Async)

Phase 4 — Controller + Templates
  ├── [TEST 🔴] SpaControllerTest               → TC-012–017
  ├── SpaController.java                        → implement
  ├── templates/spa/scheduler.html
  └── templates/spa/booking-success.html

Phase 5 — Integration Tests
  ├── application-test.properties               (H2 config)
  └── [TEST 🔴] SpaSchedulingIntegrationTest    → TC-INT-001–002
```

---

## File Structure Hoàn chỉnh

```
spa/
├── controller/
│   └── SpaController.java
├── dto/
│   ├── SpaBookingForm.java
│   ├── TimeSlotDto.java
│   └── SpaBookingConfirmDto.java
├── entity/                    ← đã có, không sửa
├── exception/
│   ├── ResourceUnavailableException.java
│   ├── BookingNotFoundException.java
│   ├── BookingOwnershipException.java
│   └── InvalidBookingStateException.java
├── repository/
│   ├── ScheduleRepository.java
│   ├── TherapistRepository.java
│   ├── TreatmentRoomRepository.java
│   ├── TreatmentServiceRepository.java
│   └── TreatmentBookingRepository.java
└── service/
    ├── ResourceMatchingService.java    (interface)
    ├── ResourceMatchingServiceImpl.java
    ├── SpaSchedulingService.java       (interface)
    ├── SpaSchedulingServiceImpl.java
    └── NotificationService.java        (stub)

resources/
└── templates/
    └── spa/
        ├── scheduler.html
        └── booking-success.html

test/
└── spa/
    ├── service/
    │   ├── ResourceMatchingServiceImplTest.java
    │   └── SpaSchedulingServiceImplTest.java
    ├── controller/
    │   └── SpaControllerTest.java
    └── integration/
        └── SpaSchedulingIntegrationTest.java
```

---

## Verification Plan

### Automated Tests
```bash
# Unit tests — Service layer
./mvnw test -Dtest="ResourceMatchingServiceImplTest,SpaSchedulingServiceImplTest"

# Controller tests
./mvnw test -Dtest="SpaControllerTest"

# Integration tests (H2)
./mvnw test -Dtest="SpaSchedulingIntegrationTest" -Dspring.profiles.active=test

# All spa tests
./mvnw test -Dtest="*Spa*,*ResourceMatching*"
```

### Manual Verification
```bash
# Khởi động app
./mvnw spring-boot:run

# 1. Mở trang đặt lịch (sau khi login)
# http://localhost:8080/spa/schedule?bookingId=1&serviceId=1

# 2. Kiểm tra AJAX slot endpoint
curl "http://localhost:8080/spa/slots?serviceId=1&date=2026-06-20"
# Expected: JSON array với available=true/false

# 3. Submit form → kiểm tra redirect đến /spa/bookings/{id}/success
```

### DB Verification
```sql
-- TreatmentBooking được tạo đúng
SELECT treatment_id, booking_id, service_id, status, create_at
FROM TREATMENT_BOOKING WHERE booking_id = 1;

-- Schedule gắn Therapist + Room đúng, endTime = startTime + 60 phút
SELECT sc.schedule_id, sc.therapist_code, sc.room_id, sc.start_time, sc.end_time
FROM SCHEDULE sc WHERE sc.is_delete = 0;

-- Anti-double-booking check
SELECT therapist_code, COUNT(*) cnt
FROM SCHEDULE
WHERE is_delete = 0
  AND start_time < '2026-06-20 15:00:00'
  AND end_time   > '2026-06-20 14:00:00'
GROUP BY therapist_code HAVING cnt > 1;
-- Expected: 0 rows
```

---

## Entry / Exit Criteria

**Entry Criteria**
- [x] Entities đã có (`spa.entity.*`)
- [x] DB schema tồn tại (`DB.sql`)
- [ ] `application-test.properties` với H2 được tạo
- [ ] Q1–Q3 đã confirm với team (đặc biệt Q1 — session auth)

**Exit Criteria — DoD**
- [ ] 19 test cases đều 🟢 GREEN
- [ ] `@Transactional(isolation=SERIALIZABLE)` trên `bookSession()`
- [ ] SPA-TC-INT-002 (concurrency) pass ≥ 3 lần liên tiếp
- [ ] Trang `scheduler.html` render đúng, slot grid load qua AJAX
- [ ] Redirect đúng sau submit (PRG pattern)
- [ ] Audit log được ghi mỗi lần `bookSession()`
