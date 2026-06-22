# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa cho UC13

| Field | Value |
|---|---|
| Document ID | `AURA-SPA-IMP-UC13` |
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
| 2026-06-14 | DuongLD | Tạo tài liệu lần đầu cho UC13: View Daily Work Schedule |

## 1. Tổng quan Module

| Field | Value |
|---|---|
| Module Name | `Therapist Portal Module` |
| Bounded Context | `Spa Management` |
| Data Classification | Internal / Sensitive (Health Notes) |
| Compliance Scope | N/A |
| Upstream Dependencies | `Spa Scheduling Module` |
| Downstream Consumers | `Therapist Dashboard UI` |

## 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| UC-13 | User Story | Xem lịch làm việc hàng ngày của Therapist | `TherapistScheduleController.getDailySchedule()` | N/A | ADR-004 |
| BR-TH-001 | Business Rule | Chỉ xem lịch của cá nhân | `ScheduleRepository.findDailyScheduleForTherapist()` | RBAC | N/A |
| BR-TH-002 | Business Rule | Tối ưu truy vấn tránh N+1 | `JOIN FETCH` trong JPA | Performance | ADR-004 |

## 3. Architecture Decision Records (ADR)

### ADR-004 — Tối ưu hóa truy vấn (Tránh N+1 Query) bằng JOIN FETCH

**Bối cảnh (Context)**
Bảng `SCHEDULE` liên kết với nhiều bảng khác bao gồm `TREATMENT_BOOKING`, `TREATMENT_SERVICE`, và `TREATMENT_ROOM`. Nếu dùng `findById` hoặc `findByTherapistCode` thông thường của Spring Data JPA với `FetchType.LAZY`, hệ thống sẽ gặp lỗi N+1 Query (1 query lấy danh sách lịch, N query phụ lấy thông tin phòng và dịch vụ). Điều này làm sập Database nếu lượng lịch trong ngày lớn.

**Các phương án đã xem xét (Options Considered)**

| Phương án | Mô tả | Ưu điểm | Nhược điểm |
|---|---|---|---|
| A - Dùng EntityGraph | Dùng `@EntityGraph` để định nghĩa các node cần fetch | + Code sạch, dễ nhìn | - Khó customize điều kiện Join sâu. |
| B - Dùng JOIN FETCH (JPQL) | Viết `SELECT s FROM Schedule s JOIN FETCH ...` | + Tốc độ cực nhanh, kiểm soát hoàn toàn Query | - Câu lệnh JPQL hơi dài. |

**Quyết định (Decision)**
Chọn **Phương án B**. Vì ta cần JOIN qua nhiều cấp (`Schedule` -> `TreatmentBooking` -> `TreatmentService`) nên việc viết JPQL rõ ràng sẽ giúp dễ maintain hơn.

## 4. Non-Functional Requirements & SLA

| Category | Requirement | Target SLA | Measurement Method | Compliance Basis |
|---|---|---|---|---|
| Latency | API response (p99) | < 200ms | k6 load test | N/A |
| Performance | Query Count | Exactly 1 Query | Hibernate Logs (show-sql) | ADR-004 |

## 5. Static Modeling (Mô hình Tĩnh)

**Câu lệnh Query Tối ưu (Dự kiến):**
```java
@Query("SELECT s FROM Schedule s " +
       "JOIN FETCH s.treatmentBooking tb " +
       "JOIN FETCH tb.treatmentService ts " +
       "JOIN FETCH s.room r " +
       "WHERE s.therapist.therapistCode = :therapistCode " +
       "AND s.startTime >= :startDate AND s.startTime < :endDate " +
       "AND s.isDelete = false " +
       "ORDER BY s.startTime ASC")
List<Schedule> findDailyScheduleForTherapist(
    @Param("therapistCode") String therapistCode, 
    @Param("startDate") LocalDateTime startDate, 
    @Param("endDate") LocalDateTime endDate
);
```

## 8. Interface Specification (Đặc tả Giao diện)

### 8.1. Spring MVC Controller Interface

```java
@Controller
@RequestMapping("/therapist/schedules")
public class TherapistScheduleController {
    
    @GetMapping("/daily")
    public String getDailySchedule(@RequestParam(value = "date", required = false) String dateStr, HttpSession session, Model model);
}
```

## 9. Routing Specification (Thymeleaf)

| Method | Path | Auth Level | Required Roles | View Name (Thymeleaf) |
|---|---|---|---|---|
| GET | `/therapist/schedules/daily` | Session | `THERAPIST` | `spa/therapist-schedule` |

## 16. Bảng tổng hợp phân quyền (Authorization Matrix)

| Endpoint | GUEST | THERAPIST | ADMIN |
|---|---|---|---|
| GET `/therapist/schedules/daily` | ❌ | ✅ Own | ✅ All |
