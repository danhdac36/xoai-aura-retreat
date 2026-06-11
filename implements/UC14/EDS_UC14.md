# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field | Value |
|---|---|
| Document ID | SPA-UC14-IMP-001 |
| Version | 1.0 |
| Date | 2026-06-11 |
| Status | Draft |
| Document Owner | Backend Team |
| Author | Antigravity AI |
| Reviewed by | Tech Lead |
| Approved by | Principal Architect |

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-11 | Antigravity AI | Tạo tài liệu lần đầu cho UC14 |

## 1. Tổng quan Module
| Field | Value |
|---|---|
| Module Name | Session Status Management |
| Bounded Context | Spa / Session |
| Data Classification | Internal |
| Compliance Scope | N/A |
| Upstream Dependencies | Schedule Module |
| Downstream Consumers | Notification Service, Reward System |

## 2. Ma trận Truy vết
| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code |
|---|---|---|---|
| UC14 | User Story | Đánh dấu ca trị liệu Hoàn thành/Vắng mặt | `SessionService.updateStatus` |

## 6. Dynamic Modeling
### 6.3. State Machine
`SCHEDULED` -> `COMPLETED` (Đã hoàn thành)
`SCHEDULED` -> `NO_SHOW` (Khách vắng mặt)
> ⚠️ **Invariant bất biến:** Không thể chuyển trạng thái từ `COMPLETED` về `NO_SHOW` và ngược lại.

## 7. Domain Event Catalog
| Event Name | Trigger | Publisher | Subscriber(s) |
|---|---|---|---|
| `SessionCompleted` | Update status to `COMPLETED` | `SessionService` | `NotificationModule` |
| `SessionNoShow` | Update status to `NO_SHOW` | `SessionService` | `NotificationModule` |

## 8. Interface Specification
```typescript
export interface ISessionService {
  updateSessionStatus(therapistId: string, scheduleId: string, status: 'COMPLETED' | 'NO_SHOW'): Promise<void>;
}
```

## 9. API Specification
| Method | Path | Auth Level | Required Roles |
|---|---|---|---|
| PATCH | `/api/v1/schedules/:id/status` | JWT Bearer | `THERAPIST` |

## 10. Bảng mã lỗi
| Code | HTTP Status | Message (EN) | Message (VI) |
|---|---|---|---|
| `SES-001` | 400 | Invalid status transition | Trạng thái không hợp lệ |
| `SES-002` | 403 | Forbidden | Không phải ca của bạn |

## 16. Bảng tổng hợp phân quyền
| Endpoint | GUEST | USER | THERAPIST | ADMIN |
|---|---|---|---|---|
| PATCH `/api/v1/schedules/:id/status` | ❌ | ❌ | ✅ Own | ✅ All |
