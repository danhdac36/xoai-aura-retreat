# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field | Value |
|---|---|
| Document ID | `HOS-M3-IMP-014` |
| Version | 1.0 |
| Date | 2026-06-14 |
| Status | Draft |
| Document Owner | Nhóm phát triển SE2023-G6 |
| Author | Agent (Senior Java Developer) |
| Reviewed by | DuongLD (Tech Lead) |
| Approved by | Principal Architect |
| Based on EDS | v2.0 |

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | Agent | Tạo tài liệu lần đầu cho UC14 |

## 1. Tổng quan Module
| Field | Value |
|---|---|
| Module Name | Spa & Therapy Scheduling Engine - UC14 Update Session Status |
| Bounded Context | `spa`, `billing` |
| Data Classification | Internal |
| Compliance Scope | N/A |
| Upstream Dependencies | `auth` |
| Downstream Consumers | `billing` (Ghi nhận Folio Item) |

## 2. Ma trận Truy vết (Traceability Matrix)
| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| BR-05 | Business Rule | Chỉ assigned therapist được update status. | `TherapistScheduleService.updateSessionStatus()` | N/A | ADR-001 |
| BR-11 | Business Rule | Extra services ngoài gói phải ghi nợ vào Folio. | `TherapistScheduleService.updateSessionStatus()` | N/A | ADR-002 |
| BR-15 | Business Rule | Lưu Audit Trail khi đổi trạng thái. | TBD | N/A | - |

## 3. Architecture Decision Records (ADR)
### ADR-001 — Bảo mật cập nhật trạng thái (Ownership Check)
**Bối cảnh (Context)**
Therapist chỉ được quyền cập nhật những ca làm việc do chính mình thực hiện, không được phép thay đổi trạng thái của therapist khác.
**Quyết định (Decision)**
Bắt buộc truy vấn `SCHEDULE` để kiểm tra `therapist_code` của `treatment_id` có khớp với `therapist_code` đang đăng nhập không trước khi update vào `TREATMENT_BOOKING`.

### ADR-002 — Tích hợp Billing (Folio Item)
**Bối cảnh (Context)**
Khi session chuyển sang trạng thái `Completed`, nếu session này có `folio_id` (nghĩa là gói Extra Spa do Receptionist mua thêm ở UC15), hệ thống phải tự động ghi nợ.
**Quyết định (Decision)**
Insert một bản ghi vào bảng `FOLIO_ITEM` với `amount` lấy từ `TREATMENT_SERVICE.price` trong cùng một khối `@Transactional` với việc update status.

## 4. Non-Functional Requirements & SLA
| Category | Requirement | Target SLA | Measurement Method | Compliance Basis |
|---|---|---|---|---|
| Data Integrity | ACID cho update & billing | 100% | Transaction log | Ngăn chặn thất thoát doanh thu (BR-11) |

## 5. Static Modeling (Mô hình Tĩnh)
### 5.1. Data Structure
- **Bảng `TREATMENT_BOOKING`**: Update cột `status` ('Completed', 'No-Show', 'Scheduled').
- **Bảng `FOLIO_ITEM`**: Insert record (nếu `folio_id` != null). Trường liên quan: `folio_id`, `service_category`='Extra Spa', `reference_id`=`treatment_id`, `amount`=`price`.
- **Bảng `SCHEDULE`**: Dùng để JOIN kiểm tra ownership (`therapist_code`).

## 6. Dynamic Modeling (Mô hình Động)
### 6.1. State Machine — Treatment Session Status
```text
[Scheduled] ──────────────── Therapist marks Completed ──────────────→ [Completed]
[Scheduled] ──────────────── Therapist marks No-Show ─────────────────→ [No-Show]
```
> ⚠️ **Invariant bất biến**: Không được phép chuyển ngược từ `Completed` hoặc `No-Show` về `Scheduled`.

## 8. Interface Specification
### 8.1. Service Interface
```java
// @version 1.0
public interface TherapistScheduleService {
  /**
   * Cập nhật trạng thái session
   * @throws RuntimeException khi không phải owner hoặc sai state
   */
  void updateSessionStatus(Integer treatmentId, String therapistCode, String newStatus);
}
```

## 9. API Specification
**PATCH `/therapist/schedules/{treatmentId}/status`**
**Auth Level**: Session/JWT (Role: THERAPIST)
**Request Body:**
```json
{
  "status": "Completed" 
}
```
**Response — 200 OK:**
```json
{
  "message": "Cập nhật trạng thái thành công"
}
```

## 10. Bảng mã lỗi (Error Codes)
| Code | HTTP Status | Message (EN) | Message (VI) | Trigger Condition |
|---|---|---|---|---|
| `SPA-011` | 403 | Forbidden | Bạn không có quyền cập nhật ca này | Sai therapist_code (BR-05) |
| `SPA-012` | 400 | Invalid State | Trạng thái không hợp lệ | Update từ Completed/No-Show về Scheduled |
| `SPA-013` | 404 | Not Found | Không tìm thấy ca trị liệu | ID không tồn tại |
