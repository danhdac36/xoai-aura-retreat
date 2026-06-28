# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC36 Incident & Service Recovery Management

- **Document ID**: `AM-MOD5-TDD-036`
- **Version**: 1.0
- **Date**: 2026-06-27
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [x] N/A — Chỉ chứa thông tin dịch vụ, không PII nhạy cảm
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `02_Requirement/Module5/SRS_Document.md` — Functional requirements (UC36)
- `04_Implement/Module5/UC36/EDS_UC36_Incident.md` (`AM-MOD5-IMP-036`) — Technical Specification
- `ADR-036-1` — Quản lý State Machine cho Ticket
- `BR-15` — Audit Trail mọi thao tác đổi trạng thái Ticket
- `booking.entity.Review` — Entity Review có sẵn (FK liên kết)

> [!NOTE]
> **Quy ước TDD**: Tài liệu này mô tả test cases **TRƯỚC** khi viết production code.
> Thứ tự bắt buộc: viết test (`@Test` method) → chạy → xác nhận FAIL 🔴 → implement → PASS 🟢 → refactor 🔵.
> Không mark test là ✅ nếu `mvn test` chưa xanh.
> Test data dùng In-memory H2 database. Không dùng PII thật.

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| :--- | :--- | :--- |
| 2026-06-27 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC36 Incident Management |

---

## MỤC LỤC
1. [Thông tin Module](#1-thông-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)
7. [Rollback Plan](#7-rollback-plan)

---

## 1. Thông tin Module

| Field | Value |
| :--- | :--- |
| **Feature / Gap ID** | GAP-036 |
| **Module** | Incident & Service Recovery Management (Module 5) |
| **Spec gốc** | `AM-MOD5-IMP-036` |
| **Priority** | 🔴 P0 |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | Review Module (`booking.entity.Review`) |
| **Downstream Consumers** | Manager Dashboard |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | EDS ban đầu dùng `sourceReviewId: Long` (Soft reference). | Dự án đã có Entity `Review` trong `booking.entity` với ID kiểu `Integer`. | Entity `IncidentTicket` sử dụng `@ManyToOne` liên kết trực tiếp với `Review`. |
| L2 | Entity không kế thừa `BaseEntity`. | Convention dự án. | Entity đổi thành `IncidentTicket extends BaseEntity`. |
| L3 | State Machine cho phép nhảy cóc OPEN → RESOLVED. | ADR-036-1 quy định chặn chuyển trạng thái không hợp lệ. | Test phải xác nhận chặn transition OPEN → RESOLVED. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Incident & Service Recovery Management` bao gồm các layer:
- Domain (Entity `IncidentTicket`, Enum `TicketStatus`)
- Services (`IIncidentService` mock repository bằng Mockito)
- Controller (`IncidentController` dùng `@WebMvcTest` + `MockMvc`)
- State Machine (Kiểm tra chuyển trạng thái Ticket hợp lệ/không hợp lệ)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-36 | Manager nhận diện sự cố từ đánh giá in-stay, tạo/xử lý Ticket (OPEN → IN_PROGRESS → RESOLVED). |
| ADR-036-1 | State Machine đơn giản bằng Java Enum + validation logic ở Service layer. |
| BR-15 | Audit Trail mọi thao tác đổi trạng thái Ticket. |
| `AM-MOD5-IMP-036` §6.1 | State Diagram (OPEN → IN_PROGRESS → RESOLVED). |
| `AM-MOD5-IMP-036` §6.2 | Sequence Diagram (Manager → Controller → Service → DB + AuditLog). |
| `AM-MOD5-IMP-036` §9.1 | Endpoints `/manager/incidents`, `/manager/incidents/{id}/assign`, `/manager/incidents/{id}/resolve`. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Assign ticket OPEN → IN_PROGRESS thành công | `IncidentService.assignTicket()` | `MOD5-INC-TC-001` |
| TC-COND-002 | Resolve ticket IN_PROGRESS → RESOLVED thành công | `IncidentService.resolveTicket()` | `MOD5-INC-TC-002` |
| TC-COND-003 | Chặn nhảy cóc OPEN → RESOLVED | `IncidentService.resolveTicket()` | `MOD5-INC-TC-003` |
| TC-COND-004 | Dashboard trả View danh sách OPEN tickets | `IncidentController.dashboard()` | `MOD5-INC-TC-004` |
| TC-COND-005 | Ticket liên kết trực tiếp với Entity Review | `IncidentTicket.review` (ManyToOne) | `MOD5-INC-TC-005` |
| TC-COND-006 | Chặn Guest truy cập Incident Dashboard | Spring Security Guard | `MOD5-INC-TC-006` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| State Transition Testing | `TicketStatus` (OPEN → IN_PROGRESS → RESOLVED) | Xác thực mọi transition hợp lệ và chặn transition bất hợp lệ. |
| Equivalence Partitioning | User Role (MANAGER vs GUEST) | Phân biệt quyền thao tác Incident Dashboard. |
| Boundary Value Analysis | Ticket ID (tồn tại / không tồn tại) | Xác minh xử lý 404. |
| Error Guessing | Nhảy cóc state, Ticket ID không tồn tại | Bắt lỗi nghiệp vụ và dữ liệu. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | `IncidentTicket(id=1, status=OPEN, title="Phòng ồn", review=Review(rating=1))` | Ticket mới — Happy path assign |
| FX-002 | DB seed | `IncidentTicket(id=2, status=IN_PROGRESS, assignedDepartment="Maintenance")` | Ticket đang xử lý — Happy path resolve |
| FX-003 | DB seed | `Review(id=10, rating=1, comment="Quá tệ")` | Đánh giá xấu liên kết với Ticket |
| FX-004 | Auth Mock | `@WithMockUser(roles = "MANAGER")` | Giả lập quyền Manager |
| FX-005 | Auth Mock | `@WithMockUser(roles = "GUEST")` | Giả lập truy cập trái phép |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-INC-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-INC-TC-001` — Assign Ticket OPEN → IN_PROGRESS thành công
- **Severity**: CRITICAL
- **Feature Under Test**: `IncidentService.assignTicket()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/service/impl/IncidentServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- (FX-001) Ticket `id=1`, trạng thái `OPEN`.

#### Test Steps:
1. Mock `incidentTicketRepository.findById(1)` trả về `IncidentTicket(status=OPEN)`.
2. Gọi `incidentService.assignTicket(1, "Spa")`.
3. Verify `save()` được gọi với `status = IN_PROGRESS` và `assignedDepartment = "Spa"`.
4. Verify `auditLogService.log()` ghi nhận "Ticket Assigned".

#### Expected Result (PASS):
- Trạng thái đổi sang `IN_PROGRESS`.
- `assignedDepartment` = `"Spa"`.
- Audit Log ghi nhận thao tác.

#### Expected Result (FAIL):
- Trạng thái không đổi hoặc department không được lưu.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Validate trạng thái hiện tại phải là `OPEN` trước khi assign.

---

### `MOD5-INC-TC-002` — Resolve Ticket IN_PROGRESS → RESOLVED thành công
- **Severity**: CRITICAL
- **Feature Under Test**: `IncidentService.resolveTicket()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/service/impl/IncidentServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- (FX-002) Ticket `id=2`, trạng thái `IN_PROGRESS`.

#### Test Steps:
1. Mock `incidentTicketRepository.findById(2)` trả về `IncidentTicket(status=IN_PROGRESS)`.
2. Gọi `incidentService.resolveTicket(2, "Đã sửa chữa xong thiết bị")`.
3. Verify `save()` được gọi với `status = RESOLVED` và `resolutionAction = "Đã sửa chữa xong thiết bị"`.
4. Verify `auditLogService.log()` ghi nhận "Ticket Resolved".

#### Expected Result (PASS):
- Trạng thái đổi sang `RESOLVED`.
- `resolutionAction` được ghi nhận.
- Audit Log ghi nhận thao tác.

#### Expected Result (FAIL):
- Exception hoặc trạng thái không đổi.

- **Current Status**: 🔴 Not written

---

### `MOD5-INC-TC-003` — Chặn nhảy cóc OPEN → RESOLVED (Invalid State Transition)
- **Severity**: CRITICAL
- **Feature Under Test**: `IncidentService.resolveTicket()` — State Machine Guard
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/service/impl/IncidentServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- (FX-001) Ticket `id=1`, trạng thái `OPEN`.

#### Test Steps:
1. Mock `incidentTicketRepository.findById(1)` trả về `IncidentTicket(status=OPEN)`.
2. Gọi `incidentService.resolveTicket(1, "Shortcut fix")`.
3. Assert ném `InvalidStateTransitionException`.

#### Expected Result (PASS):
- Ném `InvalidStateTransitionException` với mã lỗi `INC-001`.
- `save()` KHÔNG ĐƯỢC GỌI (`verify(...).never()`).

#### Expected Result (FAIL):
- Ticket bị đổi sang RESOLVED mà bỏ qua bước IN_PROGRESS → mất kiểm soát quy trình.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Đây là test CỐT LÕI của ADR-036-1. State Machine PHẢI chặn transition bất hợp lệ.

---

### `MOD5-INC-TC-004` — Manager truy cập Incident Dashboard thành công
- **Severity**: HIGH
- **Feature Under Test**: `IncidentController.dashboard()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/controller/IncidentControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- (FX-004) Auth Mock Role = `MANAGER`.

#### Test Steps:
1. Mock `incidentService.findAllOpenTickets()` trả về list hợp lệ.
2. Dùng `MockMvc` perform `GET /manager/incidents`.
3. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"manager/incident-dashboard"`.
- Model chứa attribute `"tickets"`.

#### Expected Result (FAIL):
- Status `403` hoặc trả về view sai tên.

- **Current Status**: 🔴 Not written

---

### `MOD5-INC-TC-005` — Ticket liên kết trực tiếp với Entity Review
- **Severity**: HIGH
- **Feature Under Test**: `IncidentTicket.review` (JPA `@ManyToOne` FK)
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/service/impl/IncidentServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-005`

#### Preconditions:
- (FX-003) Có `Review(id=10, rating=1, comment="Quá tệ")`.

#### Test Steps:
1. Tạo `IncidentTicket` với `review = Review(id=10)`.
2. Mock `incidentService.createTicketFromReview(10)` thành công.
3. Verify entity `IncidentTicket` lưu xuống có `review.id = 10`.

#### Expected Result (PASS):
- `incidentTicket.getReview().getId()` = `10`.
- `incidentTicket.getReview().getRating()` = `1`.

#### Expected Result (FAIL):
- `review` là `null` (liên kết FK bị hỏng).

- **Current Status**: 🔴 Not written
- **Implementation Note**: Entity phải sử dụng `@ManyToOne(fetch = FetchType.LAZY)` với `@JoinColumn(name = "review_id")`.

---

## SECURITY TEST CASES

### `MOD5-INC-TC-006` — Chặn Guest truy cập Incident Dashboard
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Feature Under Test**: Spring Security Guard trên `/manager/incidents`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/incident/controller/IncidentControllerSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-005) Auth Mock Role = `GUEST`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Guest.
2. Cố tình truy cập `GET /manager/incidents`.
3. Xác minh quyền truy cập.

#### Expected Result (PASS = hệ thống an toàn):
- Trả về `403 Forbidden`.

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Guest thấy được danh sách Incident Tickets (Lỗ hổng leo thang đặc quyền — Guest xem được dữ liệu vận hành nội bộ).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** — Các test Unit (Mockito) + WebMvcTest + State Transition đã cover đầy đủ Flow.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-INC-TC-001` | `IncidentServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INC-TC-002` | `IncidentServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INC-TC-003` | `IncidentServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INC-TC-004` | `IncidentControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-INC-TC-005` | `IncidentServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INC-TC-006` | `IncidentControllerSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AM-MOD5-IMP-036` đã được review và approve.
- [x] Logic Issues (Section 2) đã được confirm.
- [ ] SQL migration cho bảng `incident_tickets` đã approved.
- [x] Entity `Review` đã tồn tại trong `booking.entity`.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines cho `IncidentService` và `IncidentController`.
- [ ] State Machine chặn thành công mọi transition bất hợp lệ (TC-003 phải PASS).
- [ ] Audit Log ghi nhận đúng mỗi thao tác đổi trạng thái.
- [ ] FK giữa `IncidentTicket` và `Review` hoạt động chính xác.

### Suspension Criteria (Điều kiện tạm dừng)
- Bảng `incident_tickets` chưa tồn tại trên Test DB.
- Entity `Review` bị thay đổi cấu trúc ID.

---

## 7. Rollback Plan

### Revert Database (dev only)
- Xóa bảng `incident_tickets` khỏi schema.

### Revert implementation files
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/incident/
git checkout -- src/test/java/com/AuraMoon/auramoon/incident/
```

### Gap vẫn OPEN
- Giữ nguyên entry trong Bảng theo dõi Sprint hiện tại.
