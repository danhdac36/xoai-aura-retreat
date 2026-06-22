# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa: UC11 - Lên lịch phiên Spa/Trị liệu

| Field | Value |
|---|---|
| Document ID | `HOS-SPA-IMP-011` |
| Version | 1.0 |
| Date | 2024-06-14 |
| Status | Draft |
| Document Owner | Backend Team |
| Author | Senior Java Developer |
| Reviewed by | Tech Lead |
| DPO Sign-off | [ ] Pending |
| Approved by | Principal Architect |
| Based on EDS | v2.0 |

## CHANGELOG
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ. Mọi thay đổi phải ghi vào bảng này.

| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2024-06-14 | Senior Java Developer | Tạo tài liệu lần đầu, thiết kế logic xử lý conflict lịch Spa (UC11) |

## MỤC LỤC
1. Tổng quan Module
2. Ma trận Truy vết (Traceability Matrix)
3. Architecture Decision Records (ADR)
4. Non-Functional Requirements & SLA
5. Static Modeling (Mô hình Tĩnh)
6. Dynamic Modeling (Mô hình Động)
7. Domain Event Catalog
8. Interface Specification (Đặc tả Giao diện)
9. API Specification
10. Bảng mã lỗi (Error Codes)
11. Quy trình Triển khai (Step-by-Step)
12. Rollback & Incident Runbook
13. Kịch bản Kiểm thử Chi tiết
14. Phương pháp Xác minh
15. Mẫu thử thực tế (API Verification Samples)
16. Bảng tổng hợp phân quyền (Authorization Matrix)

---

## 1. Tổng quan Module
> Module xử lý việc khách hàng (đã đặt gói Retreat) tự lên lịch các phiên Spa/Trị liệu nằm trong gói. Yêu cầu quan trọng nhất là phải tìm kiếm, sắp xếp và khóa tài nguyên (Phòng trị liệu & Chuyên viên) đồng thời để tránh xung đột lịch (double-booking).

| Field | Value |
|---|---|
| Module Name | `Spa Scheduling Module` |
| Bounded Context | `Spa & Treatment Management` |
| Data Classification | Internal / Confidential |
| Compliance Scope | N/A |
| Upstream Dependencies | `Booking Module`, `Package Module` |
| Downstream Consumers | `Notification Service`, `Audit Service` |

## 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| BR-04 | Business Rule | Dual Resource Spa Scheduling: Cần cả Phòng và Chuyên viên trống cùng lúc. Không cho phép trùng lịch. | `SpaScheduleService.findAvailableResources()` | System Integrity | ADR-001 |
| BR-05 | Business Rule | Spa Service Eligibility: Khách chỉ được book dịch vụ có trong gói. | `SpaScheduleService.validateServiceEligibility()` | Business Logic | — |
| BR-15 | Business Rule | Audit Trail: Lưu log khi tạo lịch. | `AuditLogger.log()` | Traceability | — |
| BR-17 | Business Rule | Spa Appointment Notification | `NotificationService.sendReminder()` | User Experience | — |
| UC-11 | User Story | Guest schedules Spa Session | `SpaScheduleController.POST /schedules` | — | — |

## 3. Architecture Decision Records (ADR)

### ADR-001 — Cơ chế xử lý tranh chấp (Concurrency Control) khi book lịch Spa

| Field | Value |
|---|---|
| Status | Proposed |
| Deciders | Senior Java Developer, Tech Lead |
| Date | 2024-06-14 |

**Bối cảnh (Context)**
> Khi nhiều khách hàng cùng lên lịch Spa vào một khung giờ, có nguy cơ xảy ra Race Condition dẫn đến việc 1 chuyên viên hoặc 1 phòng bị book cho 2 khách hàng khác nhau cùng một thời điểm (Double-booking). Đây là bài toán cấp phát 2 tài nguyên đồng thời (2-Dimensional Resource Allocation).

**Các phương án đã xem xét (Options Considered)**

| Phương án | Mô tả | Ưu điểm | Nhược điểm |
|---|---|---|---|
| A. Optimistic Locking | Dùng `@Version` trên entity `THERAPIST` và `TREATMENT_ROOM`. | Hiệu năng cao nếu ít xung đột. | Khó retry logic khi phải cấp phát 2 tài nguyên rời rạc cùng lúc. Tỷ lệ lỗi trả về user cao khi tải cao. |
| B. Pessimistic Locking (Database Row Lock) | Dùng `SELECT ... FOR UPDATE` khóa bản ghi của `THERAPIST` và `TREATMENT_ROOM` cụ thể trước khi check. | Đảm bảo an toàn tuyệt đối ở tầng DB. | Dễ gây Deadlock nếu không cẩn thận thứ tự khóa, giảm throughput. |
| C. Serialized Transaction & Overlap Query | Sử dụng câu query kiểm tra Overlap trên bảng `SCHEDULE` trong một Transaction có mức độ cách ly `SERIALIZABLE` hoặc dùng advisory locks (nếu dùng Postgres). | Tránh được phantom reads, logic code đơn giản. | Lock table/range gây giảm hiệu năng hệ thống cục bộ. |

**Quyết định (Decision)**
> Chọn **Phương án B kết hợp Overlap Query trong Transaction `READ_COMMITTED` nhưng có Explicit Row Lock (`PESSIMISTIC_WRITE`) trên Booking/Guest**.
> Cụ thể logic xử lý sẽ là:
> 1. Tính toán thời gian `start_time` và `end_time = start_time + duration_minutes`.
> 2. Truy vấn tìm 1 `room_id` và 1 `therapist_code` trống (bằng cách NOT IN bảng SCHEDULE với điều kiện Overlap: `Existing.start_time < Req.end_time AND Existing.end_time > Req.start_time`).
> 3. Để tránh 2 request cùng lúc lấy được cùng 1 phòng/chuyên viên, ta sẽ áp dụng **Pessimistic Write Lock** lên record của `TREATMENT_ROOM` và `THERAPIST` vừa tìm được.
> 4. Kiểm tra lại lần 2 (Double-check) xem 2 tài nguyên này đã bị book chưa. Nếu an toàn thì mới INSERT vào bảng `SCHEDULE`.

**Hệ quả (Consequences)**
**Tích cực:** Đảm bảo 100% không xảy ra lỗi trùng lịch (Double-booking).
**Tiêu cực / Trade-offs:** Khóa bản ghi có thể làm chậm một vài ms. Cần đảm bảo thứ tự khóa (ví dụ: luôn khóa Room trước rồi tới Therapist) để tránh Deadlock.

## 4. Non-Functional Requirements & SLA

| Category | Requirement | Target SLA | Measurement Method | Compliance Basis |
|---|---|---|---|---|
| Latency | Transaction tạo lịch (p99) | < 500ms | k6 load test (with lock) | Đảm bảo UX mượt mà |
| Data Integrity| Tránh Double-booking | 0% lỗi | Multi-thread Integration Test| BR-04 |
| Consistency | Audit log & Schedule | 100% | Transactional Outbox / Sync | BR-15 |

## 5. Static Modeling (Mô hình Tĩnh)

### 5.2. Data Structure (Entity Schema Map)

```sql
-- Dựa trên Schema DB.sql

-- Bảng dịch vụ Spa
model TREATMENT_SERVICE {
  service_id       Int
  treatment_code   String
  duration_minutes Int     -- Thời gian thực hiện để tính end_time
  is_available     Boolean
}

-- Bảng lưu trữ Booking Spa cho khách (đã ánh xạ vào gói)
model TREATMENT_BOOKING {
  treatment_id     Int @id
  booking_id       Int
  service_id       Int
  status           String  -- Pending, Scheduled, Completed, No-Show
}

-- Bảng lịch thực tế (Nơi xảy ra logic Conflict)
model SCHEDULE {
  schedule_id    Int @id
  treatment_id   Int
  therapist_code String
  room_id        Int
  start_time     DateTime
  end_time       DateTime
  is_delete      Boolean @default(false)
  
  -- Index cực kỳ quan trọng cho truy vấn khoảng thời gian
  @@index([start_time, end_time, is_delete])
  @@index([therapist_code, start_time, end_time])
  @@index([room_id, start_time, end_time])
}
```

## 6. Dynamic Modeling (Mô hình Động)

### Logic check trùng lịch (Conflict Resolution Logic)

Thời gian xung đột (Overlap) được định nghĩa theo công thức:
`(B_Start < A_End) AND (B_End > A_Start)`

**Truy vấn tìm tài nguyên (Pseudocode JPA):**
```sql
-- Tìm phòng trống
SELECT r.* FROM TREATMENT_ROOM r
WHERE r.status = 'Active' AND r.is_delete = 0
AND r.room_id NOT IN (
    SELECT s.room_id FROM SCHEDULE s
    WHERE s.is_delete = 0 
    AND s.start_time < :requestEndTime 
    AND s.end_time > :requestStartTime
)
ORDER BY r.room_id ASC LIMIT 1
FOR UPDATE; -- Lock ROW để xử lý an toàn

-- Tìm chuyên viên trống
SELECT t.* FROM THERAPIST t
WHERE t.status = 'Active'
AND t.therapist_code NOT IN (
    SELECT s.therapist_code FROM SCHEDULE s
    WHERE s.is_delete = 0 
    AND s.start_time < :requestEndTime 
    AND s.end_time > :requestStartTime
)
ORDER BY t.therapist_code ASC LIMIT 1
FOR UPDATE; -- Lock ROW
```
*(Ghi chú: ORDER BY giúp đảm bảo cấp phát tuần tự, giảm phân mảnh và tránh Deadlock khi khóa)*

## 7. Domain Event Catalog

### 7.1. Events Published (Phát ra)

| Event Name | Trigger | Publisher | Subscriber(s) | Async? |
|---|---|---|---|---|
| `SpaSessionScheduled` | Khi record lưu thành công vào `SCHEDULE` | `SpaScheduleService` | `NotificationService`, `AuditService` | Yes |

## 8. Interface Specification (Đặc tả Giao diện)

### 8.1. Service Interface

```typescript
// ISpaScheduleService.java (Java format)
// @version 1.0

public interface SpaScheduleRequest {
    Integer bookingId;
    Integer serviceId;
    LocalDateTime startTime;
}

public interface SpaScheduleResponse {
    Integer scheduleId;
    String therapistCode;
    Integer roomId;
    LocalDateTime startTime;
    LocalDateTime endTime;
}

public interface ISpaScheduleService {
    /**
     * Lên lịch cho dịch vụ Spa. Xử lý đồng thời và chống double-booking.
     * @throws ResourceConflictException Khi không có Therapist hoặc Room (MSG-10)
     * @throws ValidationException Khi dịch vụ không hợp lệ (BR-05)
     */
    @Transactional
    SpaScheduleResponse scheduleSession(SpaScheduleRequest request);
}
```

## 9. API Specification

### 9.1. Endpoints Table

| Method | Path | Auth Level | Required Roles | Rate Limit | Idempotent? |
|---|---|---|---|---|---|
| POST | `/api/v1/spa/schedules` | JWT Bearer | `GUEST` | 10/min | No |

### 9.2. Request / Response Schemas

**POST `/api/v1/spa/schedules` — Đặt lịch Spa**

**Request Body:**
```json
{
  "bookingId": 1234,
  "serviceId": 5,
  "startTime": "2024-06-20T10:00:00"
}
```

**Response — 201 Created (Happy Path - MSG-09):**
```json
{
  "message": "Spa appointment booked successfully.",
  "data": {
    "scheduleId": 88,
    "therapistCode": "TH001",
    "roomId": 12,
    "startTime": "2024-06-20T10:00:00",
    "endTime": "2024-06-20T11:00:00"
  }
}
```

**Response — 409 Conflict (Không có phòng/chuyên viên - MSG-10):**
```json
{
  "error": {
    "code": "SPA-010",
    "message": "No available Therapist or Therapy Room could be found."
  }
}
```

## 10. Bảng mã lỗi (Error Codes)

| Code | HTTP Status | Message (EN) | Trigger Condition |
|---|---|---|---|
| `SPA-001` | 400 | Service not found or not in package | Dịch vụ chọn không nằm trong gói Retreat của khách (BR-05) |
| `SPA-010` | 409 | No available Therapist or Therapy Room could be found. | Trùng lịch (BR-04), query tìm room/therapist trống trả về null |
| `SPA-011` | 403 | Unauthorized booking modification | Cố tình book lịch cho bookingId không thuộc về Guest hiện tại |

## 11. Quy trình Triển khai (Step-by-Step)
- **Pre-Migration:** Đảm bảo bảng `SCHEDULE` đã được đánh Index đầy đủ trên các trường `start_time` và `end_time` để tối ưu hóa câu truy vấn Overlap.

## 12. Rollback & Incident Runbook

| Điều kiện | Ngưỡng | Người quyết định |
|---|---|---|
| Lỗi Deadlock ở DB tăng vọt | > 10 lỗi/phút | On-call Engineer |
| Khách bị Double-booking | Bất kỳ case nào | Tech Lead |

**Rollback:** Fix lại câu lệnh `ORDER BY` khi lock record để giải quyết Deadlock, hoặc tạm thời giới hạn số lượng request song song (Rate limiting) tại API Gateway.

## 13. Kịch bản Kiểm thử Chi tiết (TDD Focus)

### 13.1. Integration Tests (Multi-threading)
**TC-INT-SPA-001 — Kiểm thử Race Condition (Double-booking)**
```gherkin
  Scenario: Hai khách hàng cố gắng book cùng một dịch vụ vào cùng một giờ khi chỉ còn 1 phòng duy nhất
    Given test data classification: SYNTHETIC
    And hệ thống chỉ còn đúng 1 TREATMENT_ROOM trống lúc 10:00
    When 2 requests POST /api/v1/spa/schedules được gửi lên ĐỒNG THỜI (Multi-threading)
    Then 1 request trả về status 201 (MSG-09)
    And 1 request trả về status 409 (MSG-10: No available Therapist or Therapy Room)
    And database chỉ có đúng 1 record được tạo ra trong bảng SCHEDULE
```

## 16. Bảng tổng hợp phân quyền (Authorization Matrix)

| Endpoint | GUEST | RECEPTIONIST | THERAPIST | ADMIN |
|---|---|---|---|---|
| POST `/api/v1/spa/schedules` | ✅ Own | ✅ All | ❌ | ✅ All |
