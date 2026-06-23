# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa cho UC11

| Field | Value |
|---|---|
| Document ID | `AURA-SPA-IMP-UC11` |
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
| 2026-06-14 | DuongLD | Tạo tài liệu lần đầu cho UC11: Schedule Spa/Treatment Session |

## 1. Tổng quan Module

| Field | Value |
|---|---|
| Module Name | `Spa Scheduling Module` |
| Bounded Context | `Spa Management` |
| Data Classification | Internal / Public |
| Compliance Scope | N/A |
| Upstream Dependencies | `Booking Module` |
| Downstream Consumers | `Frontend UI (schedule.html)` |

## 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| UC-11 | User Story | Đặt lịch Spa / Therapy Session | `SpaScheduleController.showSchedulePage()` | N/A | N/A |
| UC-11 | User Story | Lấy Gói Spa đang kích hoạt | `SpaScheduleController.getActivePackage()` | N/A | N/A |
| UC-11 | User Story | Lấy Khung giờ còn trống | `SpaScheduleController.getAvailableTimeSlots()` | N/A | N/A |

## 3. Architecture Decision Records (ADR)

### ADR-003 — Tính toán Khung giờ Trống (Time Slot Generation) tại Backend

**Bối cảnh (Context)**
Giao diện cần hiển thị các khung giờ có sẵn trong ngày dựa trên thời lượng (duration) của dịch vụ (VD: 45 phút, 60 phút, 90 phút).
Hệ thống phải đảm bảo khung giờ đó phòng và nhân viên vẫn đang rảnh.

**Các phương án đã xem xét (Options Considered)**

| Phương án | Mô tả | Ưu điểm | Nhược điểm |
|---|---|---|---|
| A - Trả toàn bộ lịch về Frontend | Trả về tất cả lịch đã book trong ngày, UI tự trừ giờ. | + Backend nhàn hạ. | - Rò rỉ thông tin lịch của khách khác. |
| B - Tính toán sẵn ở Backend | Backend check từng khung giờ và chỉ trả về list giờ an toàn. | + Bảo mật tuyệt đối, Logic tập trung. | - Backend phải xử lý vòng lặp nặng hơn một chút. |

**Quyết định (Decision)**
Chọn **Phương án B**. Spring Boot Controller `getAvailableTimeSlots` sẽ kiểm tra chéo với hàm `findAvailableRoomsWithLock` để chắc chắn có phòng, rồi mới cho khung giờ đó vào danh sách `availableTimes` trả về Frontend.

## 4. Non-Functional Requirements & SLA

| Category | Requirement | Target SLA | Measurement Method | Compliance Basis |
|---|---|---|---|---|
| Latency | API response (p99) | < 300ms | k6 load test | N/A |

## 5. Static Modeling (Mô hình Tĩnh)

Sơ đồ Giao diện:
* Giao diện HTML (schedule.html) chia làm 3 cụm: Sáng, Chiều, Tối.
* Data truyền qua AJAX / Fetch API dạng JSON.

## 8. Interface Specification (Đặc tả Giao diện)

### 8.1. API Controller Interface

```java
@Controller
@RequestMapping("/booking-spa")
public class SpaScheduleController {
    @GetMapping
    public String showSchedulePage();

    @GetMapping("/active-package")
    @ResponseBody
    public ResponseEntity<?> getActivePackage();

    @GetMapping("/available-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableTimeSlots(@RequestParam("date") String dateStr, @RequestParam("duration") Integer durationMinutes);
}
```

## 9. API Specification

| Method | Path | Auth Level | Required Roles | Rate Limit | Idempotent? |
|---|---|---|---|---|---|
| GET | `/booking-spa` | Optional | `GUEST` | 100/min | Yes |
| GET | `/booking-spa/active-package` | Optional | `GUEST` | 100/min | Yes |
| GET | `/booking-spa/available-slots` | Optional | `GUEST` | 300/min | Yes |

## 10. Bảng mã lỗi (Error Codes)

| Code | HTTP Status | Message (EN) | Message (VI) | Trigger Condition |
|---|---|---|---|---|
| `SPA-002` | 404 | Package not found | Không tìm thấy gói hợp lệ | Người dùng chưa mua gói hoặc gói đã đặt kín lịch. |
