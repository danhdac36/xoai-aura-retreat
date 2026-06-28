# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC32 Loyalty & Tier Program

- **Document ID**: `AM-MOD5-TDD-032`
- **Version**: 1.0
- **Date**: 2026-06-27
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [x] N/A — Chỉ chứa điểm thưởng, không PII nhạy cảm
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `02_Requirement/Module5/SRS_Document.md` — Functional requirements (UC32)
- `04_Implement/Module5/UC32/EDS_UC32_Loyalty.md` (`AM-MOD5-IMP-032`) — Technical Specification
- `ADR-032-1` — Tính toán điểm độc lập thông qua Event-Driven
- `BR-28` — Loyalty Points Issuance (Cộng điểm khi Folio Payment Completed)
- `BR-15` — Audit Trail cho hành động thăng hạng

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
| 2026-06-27 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC32 Loyalty & Tier Program |

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
| **Feature / Gap ID** | GAP-032 |
| **Module** | Loyalty & Tier Program (Module 5) |
| **Spec gốc** | `AM-MOD5-IMP-032` |
| **Priority** | 🟠 P1 |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | Billing Module (`CheckoutCompletedEvent`, `GuestFolio`) |
| **Downstream Consumers** | Guest Dashboard |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | EDS ban đầu đề xuất event `FolioPaymentCompletedEvent` mới. | Dự án đã có sẵn `CheckoutCompletedEvent` trong `billing.dto` được `BillingServiceImpl` bắn ra. | Listener phải lắng nghe `CheckoutCompletedEvent` thay vì tạo event mới. |
| L2 | Entity `LoyaltyProfile` không kế thừa `BaseEntity`. | Tất cả entity dự án kế thừa `BaseEntity`. | Entity đổi thành `LoyaltyProfile extends BaseEntity`. |
| L3 | Tính điểm dựa trên `amountPaid` truyền trực tiếp. | Nên query `GuestFolio.finalAmount` từ `folioId` trong Event. | Test phải mock `guestFolioRepository.findById(folioId)` để lấy `finalAmount`. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Loyalty & Tier Program` bao gồm các layer:
- Domain (Entity `LoyaltyProfile`)
- Services (`ILoyaltyService` mock repository bằng Mockito)
- Controller (`LoyaltyController` dùng `@WebMvcTest` + `MockMvc`)
- Event Listener (`LoyaltyListener` — kiểm tra xử lý khi nhận `CheckoutCompletedEvent`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-32 | Tự động cộng điểm "Aura Points" cho khách sau khi thanh toán xong hóa đơn. Tự động thăng hạng. |
| ADR-032-1 | Sử dụng `@Async TransactionalEventListener(phase = AFTER_COMMIT)` từ `CheckoutCompletedEvent`. |
| BR-28 | Cộng điểm Loyalty khi Folio Balance = 0. Tỷ lệ quy đổi: 1 điểm / 100,000 VND. |
| `AM-MOD5-IMP-032` §6.1 | Sequence Diagram (BillingService → EventBus → LoyaltyListener → LoyaltyService → DB). |
| `AM-MOD5-IMP-032` §9.1 | Endpoint `/guest/loyalty` (GET). |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Cộng điểm thành công dựa trên `finalAmount` | `LoyaltyService.awardPoints()` | `MOD5-LYL-TC-001` |
| TC-COND-002 | Thăng hạng khi tổng điểm vượt ngưỡng | `LoyaltyService.checkAndUpgradeTier()` | `MOD5-LYL-TC-002` |
| TC-COND-003 | Không thăng hạng khi chưa đủ điểm | `LoyaltyService.checkAndUpgradeTier()` | `MOD5-LYL-TC-003` |
| TC-COND-004 | Guest xem trang Loyalty Dashboard | `LoyaltyController.guestLoyalty()` | `MOD5-LYL-TC-004` |
| TC-COND-005 | Chặn Receptionist truy cập trang Loyalty | Spring Security Guard | `MOD5-LYL-TC-005` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | `finalAmount` (> 100k, < 100k, = 0) | Phân biệt trường hợp tính điểm. |
| Boundary Value Analysis | Tổng điểm tại ngưỡng Tier (99, 100, 101) | Xác minh chính xác điểm kích hoạt thăng hạng. |
| State Transition Testing | Tier (MEMBER → SILVER → GOLD → PLATINUM) | Xác thực luồng thăng hạng tuần tự. |
| Error Guessing | User Role (RECEPTIONIST truy cập) | Chặn truy cập trái phép API Guest. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | `LoyaltyProfile(guestId=1, totalPoints=50, currentTier="MEMBER")` | Happy path cộng điểm |
| FX-002 | DB seed | `LoyaltyProfile(guestId=2, totalPoints=98, currentTier="MEMBER")` | Ngưỡng thăng Silver (100) |
| FX-003 | DB seed | `GuestFolio(id=10, finalAmount=200000)` | Số tiền để tính điểm (→ 2 điểm) |
| FX-004 | Event | `CheckoutCompletedEvent(bookingId=101, folioId=10)` | Event trigger từ Billing |
| FX-005 | Auth Mock | `@WithMockUser(roles = "GUEST")` | Giả lập quyền Guest |
| FX-006 | Auth Mock | `@WithMockUser(roles = "RECEPTIONIST")` | Giả lập truy cập trái phép |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-LYL-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-LYL-TC-001` — Cộng điểm Loyalty thành công
- **Severity**: CRITICAL
- **Feature Under Test**: `LoyaltyService.awardPoints()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/loyalty/service/impl/LoyaltyServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- (FX-001) Guest có 50 điểm, tier MEMBER.
- (FX-003) GuestFolio có `finalAmount = 200,000 VND`.
- Tỷ lệ quy đổi: 1 điểm / 100,000 VND.

#### Test Steps:
1. Mock `guestFolioRepository.findById(10)` trả về `GuestFolio(finalAmount=200000)`.
2. Mock `loyaltyProfileRepository.findByGuestId(1)` trả về `LoyaltyProfile(totalPoints=50)`.
3. Gọi `loyaltyService.awardPoints(bookingId=101, folioId=10)`.
4. Verify `loyaltyProfileRepository.save()` được gọi với `totalPoints = 52`.

#### Expected Result (PASS):
- Điểm mới = 50 + 2 = `52`.
- `loyaltyProfileRepository.save()` được gọi đúng 1 lần.

#### Expected Result (FAIL):
- Điểm không thay đổi hoặc tính sai.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Method cần query `GuestFolio` từ `folioId` của `CheckoutCompletedEvent`, rồi tính: `points = finalAmount / 100000`.

---

### `MOD5-LYL-TC-002` — Thăng hạng khi tổng điểm vượt ngưỡng Silver
- **Severity**: CRITICAL
- **Feature Under Test**: `LoyaltyService.checkAndUpgradeTier()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/loyalty/service/impl/LoyaltyServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- (FX-002) Guest có 98 điểm, tier MEMBER.
- Ngưỡng Silver = 100 điểm.

#### Test Steps:
1. Mock `loyaltyProfileRepository.findByGuestId(2)` trả về `LoyaltyProfile(totalPoints=98)`.
2. Cộng thêm 2 điểm (tổng 100).
3. Gọi `loyaltyService.checkAndUpgradeTier(guestId=2)`.
4. Verify `save()` được gọi với `currentTier = "SILVER"`.
5. Verify `auditLogService` ghi log `"UPGRADE_TIER"`.

#### Expected Result (PASS):
- Tier đổi từ `MEMBER` sang `SILVER`.
- Audit Log ghi nhận `UPGRADE_TIER`.

#### Expected Result (FAIL):
- Tier vẫn giữ nguyên `MEMBER` dù đủ điểm.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Cần bổ sung `UPGRADE_TIER` vào enum `AuditLogActionType`.

---

### `MOD5-LYL-TC-003` — Không thăng hạng khi chưa đủ điểm
- **Severity**: MEDIUM
- **Feature Under Test**: `LoyaltyService.checkAndUpgradeTier()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/loyalty/service/impl/LoyaltyServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- Guest có 50 điểm sau khi cộng. Tier MEMBER. Ngưỡng Silver = 100.

#### Test Steps:
1. Mock `loyaltyProfileRepository.findByGuestId()` trả về `LoyaltyProfile(totalPoints=50, currentTier="MEMBER")`.
2. Gọi `loyaltyService.checkAndUpgradeTier(guestId)`.
3. Verify `save()` KHÔNG đổi tier.

#### Expected Result (PASS):
- Tier giữ nguyên `MEMBER`.
- `auditLogService` KHÔNG được gọi với `UPGRADE_TIER`.

#### Expected Result (FAIL):
- Tier bị đổi dù chưa đủ điểm.

- **Current Status**: 🔴 Not written

---

### `MOD5-LYL-TC-004` — Guest xem trang Loyalty Dashboard thành công
- **Severity**: HIGH
- **Feature Under Test**: `LoyaltyController.guestLoyalty()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/loyalty/controller/LoyaltyControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- (FX-005) Auth Mock Role = `GUEST`.

#### Test Steps:
1. Mock `loyaltyService.getProfile(guestId)` trả về `LoyaltyProfile` hợp lệ.
2. Dùng `MockMvc` perform `GET /guest/loyalty`.
3. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"guest/loyalty-dashboard"`.
- Model chứa attribute `"profile"`.

#### Expected Result (FAIL):
- Status `403` hoặc trả về view sai tên.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Controller cần `@GetMapping("/guest/loyalty")` và trả về đúng đường dẫn template Thymeleaf.

---

## SECURITY TEST CASES

### `MOD5-LYL-TC-005` — Chặn Receptionist truy cập Loyalty Dashboard
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Feature Under Test**: Spring Security Guard trên `/guest/loyalty`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/loyalty/controller/LoyaltyControllerSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-006) Auth Mock Role = `RECEPTIONIST`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Receptionist.
2. Cố tình truy cập `GET /guest/loyalty`.
3. Xác minh quyền truy cập.

#### Expected Result (PASS = hệ thống an toàn):
- Trả về `403 Forbidden`.

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Receptionist thấy được Loyalty Dashboard của Guest (Lỗ hổng truy cập dữ liệu khách hàng).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** — Các test Unit (Mockito) + WebMvcTest đã cover đầy đủ Flow Event-Driven.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-LYL-TC-001` | `LoyaltyServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-LYL-TC-002` | `LoyaltyServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-LYL-TC-003` | `LoyaltyServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-LYL-TC-004` | `LoyaltyControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-LYL-TC-005` | `LoyaltyControllerSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AM-MOD5-IMP-032` đã được review và approve.
- [x] Logic Issues (Section 2) đã được confirm.
- [ ] SQL migration cho bảng `loyalty_profiles` đã approved.
- [x] `CheckoutCompletedEvent` đã tồn tại trong `billing.dto`.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines cho `LoyaltyService` và `LoyaltyController`.
- [ ] Audit Log ghi nhận đúng `UPGRADE_TIER` khi thăng hạng.
- [ ] Điểm tính toán chính xác từ `GuestFolio.finalAmount` (không hardcode).

### Suspension Criteria (Điều kiện tạm dừng)
- Bảng `loyalty_profiles` chưa tồn tại trên Test DB.
- `CheckoutCompletedEvent` bị thay đổi cấu trúc payload.

---

## 7. Rollback Plan

### Revert Database (dev only)
- Xóa bảng `loyalty_profiles` khỏi schema.

### Revert implementation files
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/loyalty/
git checkout -- src/test/java/com/AuraMoon/auramoon/loyalty/
```

### Gap vẫn OPEN
- Giữ nguyên entry trong Bảng theo dõi Sprint hiện tại.
