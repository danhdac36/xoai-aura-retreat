# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE

# Mẫu Đặc tả Kiểm thử Hướng Phát triển

**Document ID:** AURA-PUB-TDD-HOME
**Version:** 1.0
**Date:** 2026-06-12
**Status:** Draft
**Standard:** ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
**Author:** Sinh viên 5 – Fullstack Developer
**Reviewed by:** [ ] Tech Lead – Pending
**DPO Sign-off:** [ ] N/A
**Approved by:** [ ] Pending
**Classification:** Internal

**References:**
* `03_Implement/Home/Home_EDS_Public_Web.md` — Technical Specification
* `01_SRS/SRS_Document_SWP391_G6.md` — Screen Authorization Matrix (§1.4.2)

> **Quy ước TDD:** Test data dùng SYNTHETIC. Không dùng PII thật.

# CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-12 | Sinh viên 5 | Khởi tạo tài liệu — TDD spec cho Home & Landing Pages |

# MỤC LỤC
1. Thông tin Module
2. Logic Issues Resolved
3. Test Design Specification (TDS)
4. Test Case Specification
5. Red-Green-Refactor Tracker
6. Entry / Exit Criteria
7. Rollback Plan

# 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `HOME` |
| **Module** | `Public Web — Home & Landing Pages` |
| **Spec gốc** | `Home_EDS_Public_Web.md` |
| **Priority** | 🟠 P1 |
| **Sprint** | `S3 (2026-06-09 → 2026-06-22)` |
| **Milestone** | `M3 Alpha - 2026-07-11` |
| **Data Classification** | `Public` |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | `None` |
| **Downstream Consumers** | `Login/Register, Booking Flow` |

# 2. Logic Issues Resolved

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| L1 | ImplementationPlan không quy định rõ `@GetMapping` value cho `/` | Code thực tế: `@GetMapping({"/", "/home"})` — cả 2 path đều vào trang chủ | Test cả 2 paths |

# 3. Test Design Specification (TDS)

## TDS-01 — Scope / Phạm vi

```text
Public Web bao gồm các layer:
├── Controller (HomeController — 5 endpoints)
└── Views (5 Thymeleaf templates + 1 shared layout)
   (Không có Service/Repository — nội dung tĩnh)
```

## TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| --- | --- |
| `SRS.md` §1.4.2 | Screen Auth: Home, About, Packages → All roles |
| `Rule 4` (Nguyên tắc 4) | Tách CSS/JS theo module |

## TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| TC-COND-001 | Trang chủ load thành công | `HomeController.showHomePage()` | `HOME-TC-001` |
| TC-COND-002 | Tất cả 5 trang đều trả HTTP 200 | Tất cả 5 `@GetMapping` | `HOME-TC-002` |
| TC-COND-003 | Model chứa pageTitle đúng | `model.addAttribute("pageTitle")` | `HOME-TC-003` |

## TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique | Applied To | Rationale |
| --- | --- | --- |
| Equivalence Partitioning | 5 endpoints (valid paths) | Mỗi path là 1 partition riêng |

## TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| --- | --- | --- | --- |
| N/A | — | Không cần fixtures — Controller không có dependencies | — |

# 4. Test Case Specification

## HOME-TC-001 — Trang chủ load thành công (cả 2 paths)

**Severity:** `HIGH`
**Feature Under Test:** `HomeController.showHomePage()`
**Test File:** `HomeControllerTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-001`

**Preconditions:**
* Không cần — Controller không có dependency injection.

**Test Steps:**
1. Arrange: Không cần setup.
2. Act: `mockMvc.perform(get("/"))` và `mockMvc.perform(get("/home"))`.
3. Assert:
   - Cả 2 trả về HTTP 200 OK.
   - View name = `public/home`.
   - Model attribute `pageTitle` = "Trang Chủ - Xoai Aura Retreat".

**Expected Result (PASS):**
* Cả `/` và `/home` đều render đúng trang chủ.

**Expected Result (FAIL):**
* 404 Not Found hoặc sai view name.

**Current Status:** 🔴 Not written

## HOME-TC-002 — Tất cả 5 endpoints trả HTTP 200

**Severity:** `HIGH`
**Feature Under Test:** Tất cả 5 `@GetMapping` trong `HomeController`
**Test File:** `HomeControllerTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-002`

**Preconditions:**
* Không cần setup.

**Test Steps:**
1. Arrange: Chuẩn bị danh sách 5 paths.
2. Act: Lặp qua từng path, gọi `mockMvc.perform(get(path))`.
3. Assert: Tất cả trả về 200 OK.

| Path | Expected View |
| --- | --- |
| `/` | `public/home` |
| `/home` | `public/home` |
| `/villas` | `public/villas` |
| `/wellness` | `public/wellness` |
| `/spa` | `public/spa` |
| `/culinary` | `public/culinary` |

**Expected Result (PASS):**
* Tất cả 6 request (5 path + alias `/`) đều HTTP 200.

**Expected Result (FAIL):**
* Bất kỳ endpoint nào trả 404 → template file thiếu.

**Current Status:** 🔴 Not written

## HOME-TC-003 — Model chứa pageTitle đúng cho từng trang

**Severity:** `MEDIUM`
**Feature Under Test:** `HomeController` — Model attributes
**Test File:** `HomeControllerTest.java`
**TDD Phase:** 🔴 RED
**Condition Ref:** `TC-COND-003`

**Preconditions:**
* Không cần setup.

**Test Steps:**
1. Act: Gọi từng endpoint.
2. Assert: `model().attribute("pageTitle", expectedTitle)` cho mỗi trang.

| Path | Expected pageTitle |
| --- | --- |
| `/home` | "Trang Chủ - Xoai Aura Retreat" |
| `/villas` | "Villas - Xoai Aura Retreat" |
| `/wellness` | "Retreat & Wellness - Xoai Aura Retreat" |
| `/spa` | "Aura Spa & Therapies - Xoai Aura Retreat" |
| `/culinary` | "Aura Culinary & Dining - Xoai Aura Retreat" |

**Expected Result (PASS):**
* Mỗi trang có `pageTitle` đúng cho SEO `<title>` tag.

**Expected Result (FAIL):**
* Title sai hoặc thiếu → Ảnh hưởng SEO.

**Current Status:** 🔴 Not written

# 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | --- | --- | --- |
| `HOME-TC-001` | `HomeControllerTest.java` | `[ ]` | `[ ]` | |
| `HOME-TC-002` | `HomeControllerTest.java` | `[ ]` | `[ ]` | |
| `HOME-TC-003` | `HomeControllerTest.java` | `[ ]` | `[ ]` | |

# 6. Entry / Exit Criteria

## Entry Criteria (Điều kiện bắt đầu)
- [x] Thiết kế Stitch đã có sẵn (5 Screen IDs)
- [x] Thymeleaf Layout Dialect đã có trong `pom.xml`
- [x] Spring Security cho phép `.permitAll()` cho public paths

## Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh
- [ ] Tất cả 5 trang hiển thị đúng trên trình duyệt
- [ ] Không có `<style>` hoặc `<script>` inline trong HTML source
- [ ] Navbar điều hướng mượt mà giữa 5 trang

## Suspension Criteria (Điều kiện tạm dừng)
* Thymeleaf Layout Dialect incompatible với Spring Boot version
* Thiết kế Stitch thay đổi lớn

# 7. Rollback Plan

```bash
# Public Web không thay đổi DB, chỉ cần revert code:
git checkout -- auramoon/src/main/java/com/AuraMoon/auramoon/publicweb/
git checkout -- auramoon/src/main/resources/templates/public/
git checkout -- auramoon/src/main/resources/static/css/public/
git checkout -- auramoon/src/main/resources/static/js/public/
```
