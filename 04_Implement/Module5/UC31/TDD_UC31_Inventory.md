# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC31 Spa Inventory Auto-Tracking

- **Document ID**: `AM-MOD5-TDD-031`
- **Version**: 1.0
- **Date**: 2026-06-27
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [x] N/A — Không xử lý PII
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `02_Requirement/Module5/SRS_Document.md` — Functional requirements (UC31)
- `04_Implement/Module5/UC31/EDS_UC31_Inventory.md` (`AM-MOD5-IMP-031`) — Technical Specification
- `ADR-031-1` — Event-Driven cho trừ kho Spa
- `BR-27` — Inventory Transaction Integrity
- `BR-15` — Audit Trail cho hành động trừ kho

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
| 2026-06-27 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC31 Spa Inventory Auto-Tracking |

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
| **Feature / Gap ID** | GAP-031 |
| **Module** | Spa Inventory Auto-Tracking (Module 5) |
| **Spec gốc** | `AM-MOD5-IMP-031` |
| **Priority** | 🔴 P0 |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | Spa Booking Module (`TreatmentBooking`, `TreatmentBookingStatus`) |
| **Downstream Consumers** | Manager Dashboard |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**
> Liệt kê mọi sai lệch giữa spec thiết kế và schema/policy/codebase thực tế.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | EDS ban đầu đề xuất tạo Event `SpaSessionCompletedEvent` mới. | Dự án đã có sẵn `TreatmentBookingStatus.COMPLETED` trong `common.enums`. | Listener phải lắng nghe thay đổi trạng thái `TreatmentBooking` thay vì event tùy chỉnh. |
| L2 | Entity `InventoryItem` không kế thừa `BaseEntity`. | Tất cả entity dự án kế thừa `BaseEntity` (có `createdAt`, `updatedAt`, `isDelete`). | Entity đổi thành `SpaInventory extends BaseEntity`. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Spa Inventory Auto-Tracking` bao gồm các layer:
- Domain (Entity `SpaInventory`, `TherapyBOM`)
- Services (`IInventoryService` mock repository bằng Mockito)
- Controller (`InventoryController` dùng `@WebMvcTest` + `MockMvc`)
- Event Listener (`InventoryListener` — kiểm tra xử lý khi `TreatmentBooking` status đổi sang `COMPLETED`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-31 | Hệ thống tự động trừ số lượng vật tư tiêu hao khi Therapist đánh dấu phiên trị liệu "Đã hoàn thành". |
| ADR-031-1 | Sử dụng Event-Driven (Spring Events) cho trừ kho Spa — Decoupled hoàn toàn. |
| BR-27 | Transaction Integrity khi trừ kho — Đảm bảo tính ACID. |
| BR-15 | Audit Trail — Mọi thao tác trừ kho phải ghi Audit Log. |
| `AM-MOD5-IMP-031` §6.1 | Sequence Diagram (SpaService → EventBus → InventoryListener → InventoryService → DB). |
| `AM-MOD5-IMP-031` §9.1 | Endpoints `/manager/inventory` (GET) và `/manager/inventory/restock` (POST). |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Trừ kho thành công khi có đủ tồn kho | `InventoryService.deductMaterials()` | `MOD5-INV-TC-001` |
| TC-COND-002 | Trừ kho khi tồn kho không đủ (âm kho) | `InventoryService.deductMaterials()` | `MOD5-INV-TC-002` |
| TC-COND-003 | Cảnh báo Low-stock khi quantity < threshold | `InventoryService.getLowStockItems()` | `MOD5-INV-TC-003` |
| TC-COND-004 | Controller Dashboard trả View cho Manager | `InventoryController.inventoryDashboard()` | `MOD5-INV-TC-004` |
| TC-COND-005 | Chặn Guest truy cập Inventory Dashboard | Spring Security Guard | `MOD5-INV-TC-005` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | Lượng tồn kho (đủ / không đủ / bằng 0) | Phân biệt các trường hợp trừ kho. |
| Boundary Value Analysis | `quantity == threshold` (ranh giới cảnh báo) | Xác minh chính xác điểm kích hoạt Low-stock Alert. |
| State Transition Testing | `TreatmentBookingStatus` (PENDING → COMPLETED) | Xác thực Listener chỉ kích hoạt khi status đúng. |
| Error Guessing | User Role (GUEST / RECEPTIONIST truy cập) | Chặn truy cập trái phép API quản lý kho. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | `SpaInventory(itemName="Tinh dầu Lavender", quantity=100.0, threshold=20.0, unit="ml")` | Tồn kho đủ — Happy path |
| FX-002 | DB seed | `SpaInventory(itemName="Kem massage", quantity=10.0, threshold=20.0, unit="gram")` | Tồn kho thiếu — Negative path |
| FX-003 | DB seed | `TherapyBOM(treatmentServiceId=1, inventoryId=1, usageAmount=20.0)` | BOM trị liệu |
| FX-004 | Auth Mock | `@WithMockUser(roles = "MANAGER")` | Giả lập quyền Manager |
| FX-005 | Auth Mock | `@WithMockUser(roles = "GUEST")` | Giả lập quyền Guest (truy cập trái phép) |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-INV-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-INV-TC-001` — Trừ kho thành công khi có đủ tồn kho
- **Severity**: CRITICAL
- **Feature Under Test**: `InventoryService.deductMaterials()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/inventory/service/impl/InventoryServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- (FX-001) Tồn kho tinh dầu Lavender = 100ml.
- (FX-003) BOM trị liệu yêu cầu 20ml tinh dầu Lavender.

#### Test Steps:
1. Mock `inventoryRepository.findById()` trả về `SpaInventory(quantity=100.0)`.
2. Mock `therapyBomRepository.findByTreatmentServiceId()` trả về `TherapyBOM(usageAmount=20.0)`.
3. Gọi `inventoryService.deductMaterials(treatmentBookingId)`.
4. Verify `inventoryRepository.save()` được gọi với `quantity = 80.0`.

#### Expected Result (PASS):
- Tồn kho tinh dầu Lavender giảm xuống `80.0ml`.
- `inventoryRepository.save()` được gọi đúng 1 lần.

#### Expected Result (FAIL):
- Quantity không thay đổi hoặc bị trừ sai số.
- `save()` không được gọi.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Method `deductMaterials()` cần fetch BOM → tính toán → save. Sử dụng `@Transactional`.

---

### `MOD5-INV-TC-002` — Kho không đủ số lượng — Exception + Critical Alert
- **Severity**: CRITICAL
- **Feature Under Test**: `InventoryService.deductMaterials()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/inventory/service/impl/InventoryServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- (FX-002) Tồn kho kem massage = 10g.
- (FX-003) BOM yêu cầu 20g.

#### Test Steps:
1. Mock `inventoryRepository.findById()` trả về `SpaInventory(quantity=10.0)`.
2. Mock `therapyBomRepository.findByTreatmentServiceId()` trả về `TherapyBOM(usageAmount=20.0)`.
3. Gọi `inventoryService.deductMaterials(treatmentBookingId)`.
4. Assert exception **HOẶC** assert quantity đã bị trừ âm (-10.0) và `auditLogService.log()` được gọi với level "CRITICAL".

#### Expected Result (PASS):
- Hệ thống vẫn cho phép trừ kho (negative balance = -10.0) theo SRS Exception E1.
- `AuditLogService` ghi log cảnh báo Critical.
- Giao dịch Spa **không** bị rollback.

#### Expected Result (FAIL):
- Hệ thống chặn trừ kho mà không ghi log → dữ liệu không nhất quán.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Theo SRS UC31 Exception E1: "System forces deduction (negative balance) and sends a Critical Alert to the Manager for manual reconciliation."

---

### `MOD5-INV-TC-003` — Lấy danh sách Low-stock Items
- **Severity**: HIGH
- **Feature Under Test**: `InventoryService.getLowStockItems()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/inventory/service/impl/InventoryServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- (FX-001) Item A: quantity=100, threshold=20 (OK).
- (FX-002) Item B: quantity=10, threshold=20 (LOW).

#### Test Steps:
1. Mock `inventoryRepository.findByQuantityLessThanEqualThreshold()` trả về list chỉ chứa Item B.
2. Gọi `inventoryService.getLowStockItems()`.
3. Assert list chứa đúng 1 item và item đó là Item B.

#### Expected Result (PASS):
- List trả về chỉ chứa các item có `quantity <= threshold`.
- Kích thước list = 1.

#### Expected Result (FAIL):
- List rỗng hoặc chứa cả Item A (không nên).

- **Current Status**: 🔴 Not written
- **Implementation Note**: Sử dụng `@Query("SELECT i FROM SpaInventory i WHERE i.quantity <= i.threshold AND i.isDelete = false")`.

---

### `MOD5-INV-TC-004` — Manager truy cập Inventory Dashboard thành công
- **Severity**: HIGH
- **Feature Under Test**: `InventoryController.inventoryDashboard()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/inventory/controller/InventoryControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- (FX-004) Auth Mock Role = `MANAGER`.

#### Test Steps:
1. Mock `inventoryService.getLowStockItems()` trả về list hợp lệ.
2. Dùng `MockMvc` perform `GET /manager/inventory`.
3. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"manager/inventory-dashboard"`.
- Model chứa attribute `"lowStockItems"`.

#### Expected Result (FAIL):
- Status `403` hoặc trả về view sai tên.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Controller cần sử dụng `@GetMapping("/manager/inventory")` và trả về đúng đường dẫn template Thymeleaf.

---

## SECURITY TEST CASES

### `MOD5-INV-TC-005` — Chặn Guest truy cập Inventory Dashboard
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Feature Under Test**: Spring Security Guard trên `/manager/inventory`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/inventory/controller/InventoryControllerSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-005) Auth Mock Role = `GUEST`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Guest.
2. Cố tình truy cập `GET /manager/inventory`.
3. Xác minh quyền truy cập.

#### Expected Result (PASS = hệ thống an toàn):
- Trả về `403 Forbidden`.
- `inventoryService.getLowStockItems()` KHÔNG ĐƯỢC GỌI (`verify(...).never()`).

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Guest thấy được trang Inventory Dashboard (Lỗ hổng leo thang đặc quyền).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** — Các test Unit (Mockito) + WebMvcTest đã cover đầy đủ Flow Event-Driven.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-INV-TC-001` | `InventoryServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INV-TC-002` | `InventoryServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INV-TC-003` | `InventoryServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-INV-TC-004` | `InventoryControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-INV-TC-005` | `InventoryControllerSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AM-MOD5-IMP-031` đã được review và approve.
- [x] Logic Issues (Section 2) đã được confirm với Team.
- [ ] SQL migration cho bảng `spa_inventory` và `therapy_bom` đã được approved.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines cho `InventoryService` và `InventoryController`.
- [ ] Không có lỗi `InventoryShortageException` không được handle.
- [ ] Audit Log ghi nhận đúng mỗi thao tác trừ kho.

### Suspension Criteria (Điều kiện tạm dừng)
- Bảng `spa_inventory` / `therapy_bom` chưa tồn tại trên Test DB.
- Module Spa chưa expose event thay đổi `TreatmentBookingStatus`.

---

## 7. Rollback Plan

### Revert Database (dev only)
- Xóa bảng `spa_inventory` và `therapy_bom` khỏi schema.

### Revert implementation files
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/inventory/
git checkout -- src/test/java/com/AuraMoon/auramoon/inventory/
```

### Gap vẫn OPEN
- Giữ nguyên entry trong Bảng theo dõi Sprint hiện tại.
