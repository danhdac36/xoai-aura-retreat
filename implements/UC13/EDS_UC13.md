# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
# Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field | Value |
|---|---|
| Document ID | SPA-UC13-IMP-001 |
| Version | 1.0 |
| Date | 2026-06-11 |
| Status | Draft |
| Document Owner | Backend Team |
| Author | Antigravity AI |
| Reviewed by | Tech Lead |
| DPO Sign-off | [ ] Pending / [x] Approved - 2026-06-11 - DPO |
| Approved by | Principal Architect |
| Last Review | 2026-06-11 |
| Based on EDS | v2.0 |

## CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-11 | Antigravity AI | Tạo tài liệu lần đầu cho UC13 |

## MỤC LỤC
1. Tổng quan Module
2. Ma trận Truy vết
3. Architecture Decision Records (ADR)
4. Non-Functional Requirements & SLA
5. Static Modeling
6. Dynamic Modeling
7. Domain Event Catalog
8. Interface Specification
9. API Specification
10. Bảng mã lỗi
11. Quy trình Triển khai
12. Rollback & Incident Runbook
13. Kịch bản Kiểm thử Chi tiết
14. Phương pháp Xác minh
15. Mẫu thử thực tế
16. Bảng tổng hợp phân quyền

## 1. Tổng quan Module
| Field | Value |
|---|---|
| Module Name | Therapist Schedule Viewing & PII Access |
| Bounded Context | Spa / Scheduling |
| Data Classification | Sensitive-PII (Health Notes) |
| Compliance Scope | PDPA |
| Upstream Dependencies | IAM Module (Auth), Booking Module |
| Downstream Consumers | UI / Mobile App |

## 2. Ma trận Truy vết
| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code | Compliance Target | ADR liên quan |
|---|---|---|---|---|---|
| UC13 | User Story | Xem lịch trình & ghi chú sức khỏe | `ScheduleService.getDailySchedule` | Access Control | ADR-001 |

## 3. Architecture Decision Records (ADR)
### ADR-001 — Cơ chế truy cập Health Notes (PII)
| Field | Value |
|---|---|
| Status | Proposed |
| Deciders | Tech Lead |
| Date | 2026-06-11 |

**Bối cảnh (Context)**
Ghi chú sức khỏe khách hàng là dữ liệu nhạy cảm. Cần đảm bảo chỉ Chuyên viên được phân công mới đọc được.

**Quyết định (Decision)**
Thực hiện truy vấn lấy Health Notes thông qua Row-level filtering. Chuyên viên gọi API lấy lịch trình sẽ chỉ nhận được Health Notes của các Booking được gán trực tiếp cho ID của họ. 

## 4. Non-Functional Requirements & SLA
| Category | Requirement | Target SLA | Measurement Method |
|---|---|---|---|
| Latency | Lấy lịch trình hàng ngày (p99) | < 200ms | k6 load test |
| Security | Access control (Row-level) | Least privilege | Auth Matrix |

## 5. Static Modeling
### 5.2. Data Structure (Prisma Schema)
```prisma
model Schedule {
  id          String   @id @default(uuid()) @db.Uuid
  therapistId String   @db.Uuid
  bookingId   String   @db.Uuid
  date        DateTime @db.Date
  startTime   DateTime @db.Time
  endTime     DateTime @db.Time
  status      String   
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt

  @@index([therapistId, date])
  @@map("schedules")
}
```

## 8. Interface Specification
```typescript
export interface ScheduleOutput {
  scheduleId: string;
  timeRange: string;
  clientName: string;
  healthNotes: string | null; 
  status: string;
}

export interface IScheduleService {
  getDailySchedule(therapistId: string, date: string): Promise<ScheduleOutput[]>;
}
```

## 9. API Specification
| Method | Path | Auth Level | Required Roles | Rate Limit | Idempotent? |
|---|---|---|---|---|---|
| GET | `/api/v1/schedules/daily` | JWT Bearer | `THERAPIST` | 100/min | Yes |

## 10. Bảng mã lỗi
| Code | HTTP Status | Message (EN) | Message (VI) | Trigger Condition |
|---|---|---|---|---|
| `SCH-001` | 403 | Unauthorized access | Không có quyền truy cập | Therapist cố xem lịch người khác |

## 16. Bảng tổng hợp phân quyền
| Endpoint | GUEST | USER | THERAPIST | ADMIN |
|---|---|---|---|---|
| GET `/api/v1/schedules/daily` | ❌ | ❌ | ✅ Own | ✅ All |
