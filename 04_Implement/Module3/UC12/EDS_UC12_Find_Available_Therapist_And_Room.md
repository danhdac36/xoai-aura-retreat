# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa cho UC12

| Field | Value |
|---|---|
| Document ID | `AURA-SPA-IMP-UC12` |
| Version | 1.0 |
| Date | 2026-06-14 |
| Status | Approved |
| Document Owner | Backend Team |
| Author | DuongLD |
| Reviewed by | Tech Lead |
| DPO Sign-off | [x] Approved - 2026-06-14 - DPO |
| Approved by | Principal Architect |
| Last Review | 2026-06-14 |
| Based on EDS | v2.0 |

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | DuongLD | Tạo tài liệu lần đầu cho UC12: Find Available Therapist and Treatment Room |

## 1. Tổng quan Module

| Field | Value |
|---|---|
| Module Name | `Spa Scheduling Module` |
| Bounded Context | `Spa Management` |
| Data Classification | Internal / Public |
| Compliance Scope | N/A |
| Upstream Dependencies | `Booking Module` |
| Downstream Consumers | `Frontend UI` |

## 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| UC-12 | User Story | Tìm Phòng và Chuyên viên rảnh | `SpaScheduleServiceImpl.scheduleSession()` | N/A | ADR-001 |
| BR-SPA-001 | Business Rule | Chống đặt trùng lịch cùng lúc | `TherapistRepository.findAvailableTherapistsWithLock()` | N/A | ADR-001 |
| BR-SPA-002 | Business Rule | Chống SQL Null Trap | `TreatmentRoomRepository.findAvailableRoomsWithLock()` | N/A | ADR-002 |

## 3. Architecture Decision Records (ADR)

### ADR-001 — Sử dụng Pessimistic Locking để chống Race Condition

| Field | Value |
|---|---|
| Status | Accepted |
| Deciders | Principal Architect, Tech Lead |
| Date | 2026-06-14 |

**Bối cảnh (Context)**
Hệ thống Booking Spa dễ gặp tình trạng Double Booking (đặt trùng) khi có nhiều khách hàng cùng lúc chọn vào một khung giờ. Việc chỉ dùng `SELECT` thông thường không đảm bảo tính độc quyền dữ liệu.

**Các phương án đã xem xét (Options Considered)**

| Phương án | Mô tả | Ưu điểm | Nhược điểm |
|---|---|---|---|
| A - Optimistic Lock | Dùng `@Version` trong JPA | + Tốc độ cao, không block DB | - Hay sinh lỗi `OptimisticLockException` ép UI phải retry. |
| B - Pessimistic Lock | Dùng `@Lock(PESSIMISTIC_WRITE)` | + Đảm bảo an toàn 100% dữ liệu | - Khóa DB một khoảng thời gian cực ngắn (vài ms). |

**Quyết định (Decision)**
Chọn **Phương án B** vì trong nghiệp vụ Spa, số lượng người đặt cùng lúc không quá khổng lồ nhưng độ chính xác và tránh phiền hà cho khách hàng phải đặt lên hàng đầu. Lệnh `SELECT ... FOR UPDATE` sẽ ép các Request khác phải xếp hàng đợi.

**Hệ quả (Consequences)**
**Tích cực:**
* Giải quyết triệt để vấn đề quá tải và đụng độ lịch.

**Tiêu cực / Trade-offs:**
* Hiệu suất chậm đi vài mili-giây do cơ chế Lock của Database.

### ADR-002 — Sử dụng NOT EXISTS thay cho NOT IN để tránh SQL NULL Trap

**Bối cảnh (Context)**
Lệnh `NOT IN` kết hợp với Subquery trong SQL có nguy cơ gây lỗi `NULL Trap` khi Subquery trả về giá trị NULL (làm toàn bộ kết quả mệnh đề NOT IN thành UNKNOWN và trả về Rỗng). 
Quyết định: Sử dụng `NOT EXISTS` để tối ưu hóa hiệu suất và loại bỏ rủi ro SQL NULL Trap.

## 4. Non-Functional Requirements & SLA

| Category | Requirement | Target SLA | Measurement Method | Compliance Basis |
|---|---|---|---|---|
| Latency | API response (p99) | < 500ms | k6 load test | N/A |
| Data Integrity | Double Booking | 0% | Concurrency Testing | Business Rule |

## 5. Static Modeling (Mô hình Tĩnh)

Sơ đồ Entity Relationship:
* `TreatmentBooking` (1-N) `TreatmentService`
* `Schedule` (N-1) `TreatmentRoom`
* `Schedule` (N-1) `Therapist`

## 8. Interface Specification (Đặc tả Giao diện)

### 8.1. Service Interface

```java
public interface SpaScheduleService {
    SpaScheduleResponse scheduleSession(SpaScheduleRequest request);
}
```

### 8.2. Repository Interface

```java
public interface TreatmentRoomRepository extends JpaRepository<TreatmentRoom, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM TreatmentRoom r WHERE r.status = 'Active' AND r.isDelete = false AND NOT EXISTS (SELECT 1 FROM Schedule s WHERE s.room = r AND s.isDelete = false AND s.startTime < :reqEnd AND s.endTime > :reqStart) ORDER BY r.id ASC")
    List<TreatmentRoom> findAvailableRoomsWithLock(@Param("reqStart") LocalDateTime reqStart, @Param("reqEnd") LocalDateTime reqEnd);
}
```

## 9. API Specification

| Method | Path | Auth Level | Required Roles | Rate Limit | Idempotent? |
|---|---|---|---|---|---|
| POST | `/booking-spa` | Optional | `GUEST` | 100/min | No |

## 10. Bảng mã lỗi (Error Codes)

| Code | HTTP Status | Message (EN) | Message (VI) | Trigger Condition |
|---|---|---|---|---|
| `SPA-001` | 400 | Service not found | Không tìm thấy dịch vụ | Service không tồn tại hoặc đã được đặt hết. |
| `SPA-010` | 409 | No available Therapist or Room | Hết Phòng hoặc Chuyên viên | Khung giờ hiện tại không còn tài nguyên trống. |
