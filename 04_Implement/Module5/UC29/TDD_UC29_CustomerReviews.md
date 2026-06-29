# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC29 Customer Reviews

- **Document ID**: `XOAI-MOD5-TDD-029`
- **Version**: 1.0
- **Date**: 2026-06-26
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [ ] Pending
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `04_testing/SOFTWARE_TEST_PLAN.md` (FPT-EDU-STP-001 v2.0) — Master Test Plan
- `01_Requirements/SRS.md` — Functional requirements (UC29)
- `04_Implement/Module5/UC29/EDS_UC29_CustomerReviews.md` — Technical Specification
- `02_Design/ADR/ADR-002` — [Kiểm soát quyền Soft Delete Review]
- `Luật PDPA` — Bảo vệ dữ liệu cá nhân khách hàng.

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
| 2026-06-26 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC29 Customer Reviews |

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
| **Feature / Gap ID** | GAP-005 |
| **Module** | Customer Reviews Dashboard (Module 5) |
| **Spec gốc** | `XOAI-MOD5-IMP-029` |
| **Priority** | 🟠 P1 |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Sensitive-PII / Internal |
| **Compliance Scope** | Luật PDPA (Quyền ẩn dữ liệu/Right to be forgotten) |
| **Upstream Dependencies** | Booking Module (Tạo đánh giá), User Module (Guest Profile) |
| **Downstream Consumers** | N/A |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**
> Liệt kê mọi sai lệch giữa spec thiết kế và schema/policy/codebase thực tế.

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | Schema `DB.sql` gốc không có trường Audit cho bảng `REVIEW`. | Đã cập nhật schema thêm `create_at` và `is_delete`. | Các method tính AVG rating và findAll() phải áp dụng `@Query` có `WHERE r.isDelete = false`. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Customer Reviews Dashboard` bao gồm các layer:
- Domain (Entity `Review`)
- Repository (`IReviewRepository` dùng `@DataJpaTest` với In-memory H2)
- Services (`IReviewService` mock repository bằng Mockito)
- Controller (`ReviewController` dùng `@WebMvcTest` + `MockMvc`)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-29 | Yêu cầu hiển thị dashboard dạng Read-only cho Manager. |
| ADR-002 | Manager (Read-only), ADMIN (có quyền Soft Delete `is_delete = 1`). |
| `XOAI-MOD5-IMP-029` §6.1 | Sequence Diagram (Controller -> Service -> Repository). |
| `XOAI-MOD5-IMP-029` §9.1 | Endpoints `/admin/reviews` (GET) và `/admin/reviews/{id}/hide` (POST). |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Dashboard metrics (AVG, Count) | `ReviewService.getDashboardMetrics()` | `MOD5-REV-TC-001` |
| TC-COND-002 | Loại trừ review bị ẩn khỏi kết quả | `ReviewRepository.getAverageRating()` | `MOD5-REV-TC-002` |
| TC-COND-003 | Admin ẩn review thành công (Redirect) | `ReviewController.hideReview()` | `MOD5-REV-TC-003` |
| TC-COND-004 | Manager ẩn review bị lỗi 403 | `ReviewController.hideReview()` (Security Guard) | `MOD5-REV-TC-004` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | User Role (ADMIN vs MANAGER) | Phân biệt quyền thao tác chức năng "Ẩn Đánh giá". |
| State Transition Testing | Cờ `is_delete` (0 -> 1) | Xác thực luồng Soft delete làm thay đổi state. |
| Error Guessing | Phân quyền truy cập | Bắt lỗi 403 Forbidden nếu Manager gọi API của Admin. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DB seed | `Review(rating=5, isDelete=false)` | Dữ liệu hợp lệ để hiển thị |
| FX-002 | DB seed | `Review(rating=1, isDelete=true)` | Dữ liệu bị ẩn, dùng kiểm tra tính năng loại trừ |
| FX-003 | Auth Mock | `@WithMockUser(roles = "MANAGER")` | Giả lập quyền Manager |
| FX-004 | Auth Mock | `@WithMockUser(roles = "ADMIN")` | Giả lập quyền Admin |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-REV-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-REV-TC-001` — Dashboard trả về View thành công cho Manager
- **Severity**: HIGH
- **Feature Under Test**: `ReviewController.dashboard()`
- **Test File**: `src/test/java/com/xoai/aura/controller/ReviewControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- (FX-003) Auth Mock Role = `MANAGER`.
- (FX-001) `ReviewService` trả về đối tượng `ReviewMetricsDTO` hợp lệ.

#### Test Steps:
1. Mock `reviewService.getDashboardMetrics()` trả về `new ReviewMetricsDTO(...)`.
2. Dùng `MockMvc` perform `GET /admin/reviews`.
3. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"admin/reviews"`.
- Model chứa attribute `"metrics"`.

#### Expected Result (FAIL):
- Status `403` hoặc trả về view sai tên.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Controller cần sử dụng `@GetMapping("/admin/reviews")` và trả về đúng đường dẫn template.

---

### `MOD5-REV-TC-002` — Repository loại bỏ review có `is_delete = true`
- **Severity**: CRITICAL
- **Feature Under Test**: `ReviewRepository.getAverageRating()`
- **Test File**: `src/test/java/com/xoai/aura/repository/ReviewRepositoryTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- DataJpaTest H2 Database.
- Save (FX-001) 1 review 5 sao (`isDelete = false`).
- Save (FX-002) 1 review 1 sao (`isDelete = true`).

#### Test Steps:
1. Gọi `reviewRepository.getAverageRating()`.
2. Kiểm tra kết quả AVG.

#### Expected Result (PASS):
- Kết quả trung bình phải là `5.0` (chỉ tính review chưa xóa).

#### Expected Result (FAIL):
- Kết quả trung bình là `3.0` (tính gộp cả review đã bị ẩn).

- **Current Status**: 🔴 Not written
- **Implementation Note**: Chắc chắn sử dụng `@Query("SELECT AVG(r.rating) FROM Review r WHERE r.isDelete = false")`.

---

### `MOD5-REV-TC-003` — Admin Submit ẩn đánh giá thành công
- **Severity**: HIGH
- **Feature Under Test**: `ReviewController.hideReview()`
- **Test File**: `src/test/java/com/xoai/aura/controller/ReviewControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- (FX-004) Auth Mock Role = `ADMIN`.

#### Test Steps:
1. Dùng `MockMvc` perform `POST /admin/reviews/1/hide` kèm CSRF token.
2. Kiểm tra HTTP Status.
3. Verify `reviewService.hideReview(1)` được gọi đúng 1 lần.

#### Expected Result (PASS):
- Status: `302 Found` (Redirect).
- Redirect URL: `/admin/reviews`.
- Flash Attribute `"message"` tồn tại.

#### Expected Result (FAIL):
- Gặp lỗi HTTP `405 Method Not Allowed` hoặc redirect sai trang.

- **Current Status**: 🔴 Not written
- **Implementation Note**: API khai báo là `@PostMapping` và trả về `redirect:/admin/reviews`.

---

## SECURITY TEST CASES

### `MOD5-REV-TC-004` — Truy cập trái phép API Ẩn Review (Manager Access)
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: Vi phạm nguyên tắc bảo mật thông tin nội bộ (Chống sửa xóa trái phép).
- **Feature Under Test**: `@PreAuthorize` Guard trên `ReviewController.hideReview()`
- **Test File**: `src/test/java/com/xoai/aura/controller/ReviewControllerSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-003) Auth Mock Role = `MANAGER`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Manager.
2. Cố tình submit Form `POST /admin/reviews/1/hide` kèm CSRF hợp lệ.
3. Xác minh quyền truy cập.

#### Expected Result (PASS = hệ thống an toàn):
- Trả về `403 Forbidden`.
- `reviewService.hideReview(1)` KHÔNG ĐƯỢC GỌI (`verify(...).never()`).

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Review bị ẩn, hệ thống trả về `302 Redirect` (Lỗ hổng leo thang đặc quyền).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** - Chức năng MVC cơ bản, các test H2 Repository + MockMvc đã cover đầy đủ Flow.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-REV-TC-001` | `ReviewControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-REV-TC-002` | `ReviewRepositoryTest.java` | [ ] | `[hash]` | |
| `MOD5-REV-TC-003` | `ReviewControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-REV-TC-004` | `ReviewControllerSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `XOAI-MOD5-IMP-029` đã được review và approve.
- [x] Logic Issues (Section 2) đã được confirm với Database Architect.
- [x] Cấu trúc Table `REVIEW` trên Test DB (H2) đã sẵn sàng.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines cho `ReviewController` và `ReviewService`.
- [ ] Không có query `DELETE` vật lý nào được gọi tới bảng `REVIEW`.
- [ ] `ReviewHidden` event được emit thành công xuống Audit Log khi `hideReview()` được gọi.

### Suspension Criteria (Điều kiện tạm dừng)
- Lỗi kết nối cấu hình Spring Security khiến `@WithMockUser` không chạy được trên Test Context.

---

## 7. Rollback Plan

### Revert Database (dev only)
- Chạy file schema gốc, xóa bỏ `create_at` và `is_delete`.

### Revert implementation files
```bash
git checkout -- src/main/java/com/xoai/aura/controller/ReviewController.java
git checkout -- src/main/java/com/xoai/aura/service/ReviewService.java
git checkout -- src/test/java/com/xoai/aura/controller/ReviewControllerTest.java
```

### Gap vẫn OPEN
- Giữ nguyên entry trong Bảng theo dõi Sprint hiện tại.
