# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển - UC35 Post-Retreat Wellness Assessment

- **Document ID**: `AM-MOD5-TDD-035`
- **Version**: 1.0
- **Date**: 2026-06-27
- **Status**: Draft
- **Standard**: ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation
- **Author**: Phùng Giang Hải — Backend Developer
- **Reviewed by**: [ ] [Tên Tech Lead] — Pending
- **DPO Sign-off**: [ ] Pending — CẦN DUYỆT (Chứa thông tin Sức khỏe / Sensitive PII)
- **Approved by**: [ ] Pending
- **Classification**: Internal — Confidential

### References:
- `02_Requirement/Module5/SRS_Document.md` — Functional requirements (UC35)
- `04_Implement/Module5/UC35/EDS_UC35_Wellness.md` (`AM-MOD5-IMP-035`) — Technical Specification
- `ADR-035-1` — Attribute-Level Encryption cho Dữ liệu Sức khỏe
- `BR-08` — Yêu cầu Consent cho dữ liệu Sức khỏe (GDPR Art. 9)
- `BR-09` — Mã hóa dữ liệu sức khỏe (GDPR Art. 32)
- `auth.config.AesDataEncryptor` — Class mã hóa AES có sẵn trong dự án

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
| 2026-06-27 | Phùng Giang Hải | Khởi tạo tài liệu — TDD spec cho UC35 Wellness Assessment |

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
| **Feature / Gap ID** | GAP-035 |
| **Module** | Post-Retreat Wellness Assessment (Module 5) |
| **Spec gốc** | `AM-MOD5-IMP-035` |
| **Priority** | 🟠 P1 |
| **Sprint** | S3 (2026-06-15 -> 2026-06-30) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Sensitive-PII |
| **Compliance Scope** | PDPA / GDPR (Health Data) |
| **Upstream Dependencies** | Identity Module (Auth), `AesDataEncryptor`, bảng `Consent` |
| **Downstream Consumers** | Guest Dashboard (Radar Chart) |

---

## 2. Logic Issues Resolved

> [!IMPORTANT]
> **Bắt buộc điền trước khi viết test.**

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| :--- | :--- | :--- | :--- |
| L1 | EDS ban đầu đề xuất tạo `AssessmentCryptoConverter` mới. | Dự án đã có sẵn `AesDataEncryptor` trong `auth.config` (AES/ECB/PKCS5Padding). | Test phải xác minh data được mã hóa bằng `AesDataEncryptor`, KHÔNG tạo Converter mới. |
| L2 | Consent check dựa trên Boolean từ Frontend (`consentGranted`). | Dự án đã có bảng `CONSENT` trong `auth.entity`. | Test phải mock/query bảng `Consent` thay vì dựa vào trường DTO. |
| L3 | Entity không kế thừa `BaseEntity`. | Convention dự án. | Entity đổi thành `WellnessAssessment extends BaseEntity`. |

---

## 3. Test Design Specification (TDS)

### TDS-01 — Scope / Phạm vi

`Post-Retreat Wellness Assessment` bao gồm các layer:
- Domain (Entity `WellnessAssessment`, Enum `AssessmentPeriod`)
- Services (`IWellnessService` mock repository bằng Mockito)
- Controller (`WellnessController` dùng `@WebMvcTest` + `MockMvc`)
- Security (Kiểm tra Consent trước khi lưu dữ liệu sức khỏe)

### TDS-02 — Test Basis / Cơ sở Kiểm thử

| Source | Items Derived |
| :--- | :--- |
| `SRS.md` UC-35 | Khách hàng thực hiện bài kiểm tra sức khỏe đầu vào/đầu ra. Hệ thống vẽ Radar Chart. |
| ADR-035-1 | Sử dụng `AesDataEncryptor` (JPA `@Convert`) để mã hóa điểm sức khỏe trước khi lưu DB. |
| BR-08 | Yêu cầu kiểm tra bảng `Consent` trước khi xử lý dữ liệu sức khỏe. |
| BR-09 | Mã hóa AES-256 tại cấp field. |
| `AM-MOD5-IMP-035` §6.1 | Sequence Diagram (Guest → Controller → Service → DB + AuditLog). |
| `AM-MOD5-IMP-035` §9.1 | Endpoints `/guest/wellness/form`, `/guest/wellness/submit`, `/guest/wellness/chart`. |

### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| :--- | :--- | :--- | :--- |
| TC-COND-001 | Submit assessment thành công khi có Consent | `WellnessService.submitAssessment()` | `MOD5-WEL-TC-001` |
| TC-COND-002 | Reject submit khi chưa có Consent | `WellnessService.submitAssessment()` | `MOD5-WEL-TC-002` |
| TC-COND-003 | Dữ liệu sức khỏe được mã hóa trong DB | `AesDataEncryptor` trên Entity | `MOD5-WEL-TC-003` |
| TC-COND-004 | Radar Chart hiển thị dữ liệu Pre/Post chính xác | `WellnessService.getRadarChartData()` | `MOD5-WEL-TC-004` |
| TC-COND-005 | Controller trả View form đánh giá cho Guest | `WellnessController.assessmentForm()` | `MOD5-WEL-TC-005` |
| TC-COND-006 | Chặn Manager truy cập dữ liệu sức khỏe | Spring Security Guard | `MOD5-WEL-TC-006` |

### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

| Technique (ISO 29119-4) | Applied To | Rationale |
| :--- | :--- | :--- |
| Equivalence Partitioning | Score range (1-10 hợp lệ, 0 hoặc 11 không hợp lệ) | Phân biệt giá trị đầu vào hợp lệ/không hợp lệ. |
| Boundary Value Analysis | Score = 0, 1, 10, 11 | Xác minh ranh giới giá trị được chấp nhận. |
| State Transition Testing | `AssessmentPeriod` (PRE_RETREAT → POST_RETREAT) | Xác thực 2 giai đoạn đánh giá. |
| Error Guessing | Thiếu Consent, Score vượt quá range | Bắt lỗi nghiệp vụ. |

### TDS-05 — Test Data Requirements

| Fixture ID | Type | Value / Logic | Mục đích |
| :--- | :--- | :--- | :--- |
| FX-001 | DTO | `AssessmentDTO(guestId=1, period=PRE_RETREAT, stress=8, sleep=6, muscleTension=7)` | Happy path submit |
| FX-002 | DB seed | `Consent(userId=1, type="HEALTH_DATA", consentStatus=true)` | Consent hợp lệ |
| FX-003 | DB seed | `Consent(userId=2, type="HEALTH_DATA", consentStatus=false)` | Consent bị từ chối |
| FX-004 | DB seed | `WellnessAssessment(guestId=1, period=PRE_RETREAT, stress="encrypted", ...)` | Dữ liệu Pre để so sánh |
| FX-005 | Auth Mock | `@WithMockUser(roles = "GUEST")` | Giả lập quyền Guest |
| FX-006 | Auth Mock | `@WithMockUser(roles = "MANAGER")` | Giả lập truy cập trái phép |

---

## 4. Test Case Specification

- **TC ID format**: `MOD5-WEL-TC-[NNN]`
- **Severity**: CRITICAL / HIGH / MEDIUM / LOW

### `MOD5-WEL-TC-001` — Submit Assessment thành công khi có Consent
- **Severity**: CRITICAL
- **Feature Under Test**: `WellnessService.submitAssessment()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/service/impl/WellnessServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-001`

#### Preconditions:
- (FX-001) DTO với scores hợp lệ.
- (FX-002) Bảng `Consent` ghi nhận user đã đồng ý (`consentStatus = true`).

#### Test Steps:
1. Mock `consentRepository.findByUserIdAndType(1, "HEALTH_DATA")` trả về `Consent(consentStatus=true)`.
2. Gọi `wellnessService.submitAssessment(dto)`.
3. Verify `wellnessAssessmentRepository.save()` được gọi đúng 1 lần.
4. Verify `auditLogService.log()` ghi nhận "Wellness Assessment Submitted".

#### Expected Result (PASS):
- `WellnessAssessment` được lưu thành công.
- Audit Log ghi nhận sự kiện.

#### Expected Result (FAIL):
- `save()` không được gọi hoặc ném exception không mong đợi.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Phải query bảng `Consent` trước khi lưu, không chỉ dựa vào trường Boolean từ Frontend.

---

### `MOD5-WEL-TC-002` — Reject Submit khi chưa có Consent
- **Severity**: CRITICAL
- **CWE**: CWE-862 — Missing Authorization
- **Legal**: GDPR Art. 9 — Cấm xử lý dữ liệu sức khỏe nếu chưa có sự đồng ý.
- **Feature Under Test**: `WellnessService.submitAssessment()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/service/impl/WellnessServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-002`

#### Preconditions:
- (FX-003) Bảng `Consent` ghi nhận user chưa đồng ý (`consentStatus = false`).

#### Test Steps:
1. Mock `consentRepository.findByUserIdAndType(2, "HEALTH_DATA")` trả về `Consent(consentStatus=false)`.
2. Gọi `wellnessService.submitAssessment(dto)`.
3. Assert ném `ConsentRequiredException`.

#### Expected Result (PASS):
- Ném `ConsentRequiredException` với mã lỗi `WEL-001`.
- `wellnessAssessmentRepository.save()` KHÔNG ĐƯỢC GỌI (`verify(...).never()`).

#### Expected Result (FAIL):
- Dữ liệu sức khỏe được lưu mà không có Consent → Vi phạm GDPR.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Đây là test BẮT BUỘC phải pass trước khi deploy. Vi phạm GDPR Art. 9 là rủi ro pháp lý.

---

### `MOD5-WEL-TC-003` — Dữ liệu sức khỏe được mã hóa trong DB
- **Severity**: CRITICAL
- **CWE**: CWE-311 — Missing Encryption of Sensitive Data
- **Legal**: BR-09, GDPR Art. 32
- **Feature Under Test**: `AesDataEncryptor` (JPA `@Convert`) trên Entity `WellnessAssessment`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/repository/WellnessAssessmentRepositoryTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-003`

#### Preconditions:
- DataJpaTest H2 Database.
- `AesDataEncryptor` configured đúng trong Test Context.

#### Test Steps:
1. Tạo `WellnessAssessment(stressScore="8", sleepScore="6", muscleTensionScore="7")`.
2. Gọi `repository.save(entity)`.
3. Dùng `EntityManager.createNativeQuery("SELECT stress_score FROM wellness_assessments WHERE id = ?")` để đọc raw data.
4. Assert giá trị trong DB **KHÔNG** phải "8" (phải là chuỗi mã hóa Base64).

#### Expected Result (PASS):
- Native SQL trả về chuỗi mã hóa (vd: `pQ+v3A==`), tuyệt đối không hiển thị plaintext `"8"`.

#### Expected Result (FAIL):
- Native SQL trả về `"8"` → dữ liệu sức khỏe bị lộ dưới dạng plaintext.

- **Current Status**: 🔴 Not written
- **Implementation Note**: Entity PHẢI sử dụng `@Convert(converter = AesDataEncryptor.class)` — class đã có sẵn tại `auth.config`.

---

### `MOD5-WEL-TC-004` — Radar Chart trả về dữ liệu Pre/Post chính xác
- **Severity**: HIGH
- **Feature Under Test**: `WellnessService.getRadarChartData()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/service/impl/WellnessServiceImplTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-004`

#### Preconditions:
- (FX-004) Có 1 record PRE_RETREAT (stress=8, sleep=6, muscleTension=7).
- Có 1 record POST_RETREAT (stress=3, sleep=9, muscleTension=4).

#### Test Steps:
1. Mock `wellnessAssessmentRepository.findByGuestIdOrderByCreatedAtDesc(1)` trả về list 2 assessment.
2. Gọi `wellnessService.getRadarChartData(guestId=1)`.
3. Assert `RadarDataDTO` chứa đúng 2 dataset (Pre & Post).

#### Expected Result (PASS):
- `RadarDataDTO.preScores` = [8, 6, 7].
- `RadarDataDTO.postScores` = [3, 9, 4].

#### Expected Result (FAIL):
- Scores bị hoán đổi hoặc thiếu dataset.

- **Current Status**: 🔴 Not written

---

### `MOD5-WEL-TC-005` — Controller trả View form đánh giá cho Guest
- **Severity**: HIGH
- **Feature Under Test**: `WellnessController.assessmentForm()`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/controller/WellnessControllerTest.java`
- **TDD Phase**: 🔴 RED
- **Condition Ref**: `TC-COND-005`

#### Preconditions:
- (FX-005) Auth Mock Role = `GUEST`.

#### Test Steps:
1. Dùng `MockMvc` perform `GET /guest/wellness/form`.
2. Kiểm tra status HTTP và view name.

#### Expected Result (PASS):
- Status: `200 OK`.
- View name: `"guest/wellness-form"`.

#### Expected Result (FAIL):
- Status `403` hoặc trả về view sai tên.

- **Current Status**: 🔴 Not written

---

## SECURITY TEST CASES

### `MOD5-WEL-TC-006` — Chặn Manager truy cập dữ liệu sức khỏe nhạy cảm
- **Severity**: CRITICAL
- **OWASP**: A01:2021 — Broken Access Control
- **CWE**: CWE-285 — Improper Authorization
- **Legal**: GDPR Art. 9 — Dữ liệu sức khỏe chỉ chủ sở hữu được xem.
- **Feature Under Test**: Spring Security Guard trên `/guest/wellness/*`
- **Test File**: `src/test/java/com/AuraMoon/auramoon/wellness/controller/WellnessControllerSecurityTest.java`
- **TDD Phase**: 🔴 RED

#### Preconditions:
- (FX-006) Auth Mock Role = `MANAGER`.

#### Test Steps (Attack Simulation):
1. Đóng giả là Manager.
2. Cố tình truy cập `GET /guest/wellness/chart`.
3. Xác minh quyền truy cập.

#### Expected Result (PASS = hệ thống an toàn):
- Trả về `403 Forbidden`.

#### Expected Result (FAIL = lỗ hổng tồn tại):
- Manager truy cập được dữ liệu sức khỏe của Guest (Vi phạm GDPR Art. 9 — Dữ liệu y tế bị tiết lộ trái phép).

- **Current Status**: 🔴 Not written

---

## INTEGRATION TEST CASES
**N/A** — Các test Unit (Mockito) + DataJpaTest (Encryption) + WebMvcTest đã cover đầy đủ Flow.

---

## 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| :--- | :--- | :---: | :--- | :--- |
| `MOD5-WEL-TC-001` | `WellnessServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-WEL-TC-002` | `WellnessServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-WEL-TC-003` | `WellnessAssessmentRepositoryTest.java` | [ ] | `[hash]` | |
| `MOD5-WEL-TC-004` | `WellnessServiceImplTest.java` | [ ] | `[hash]` | |
| `MOD5-WEL-TC-005` | `WellnessControllerTest.java` | [ ] | `[hash]` | |
| `MOD5-WEL-TC-006` | `WellnessControllerSecurityTest.java` | [ ] | `[hash]` | |

---

## 6. Entry / Exit Criteria

### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật `AM-MOD5-IMP-035` đã được review và approve.
- [x] Logic Issues (Section 2) đã được confirm.
- [ ] DPO đã sign-off phương pháp mã hóa AES-256 cho dữ liệu sức khỏe.
- [x] `AesDataEncryptor` đã tồn tại trong `auth.config`.
- [x] Bảng `Consent` đã tồn tại trong `auth.entity`.

### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] `mvn test` — tất cả unit tests xanh (không có skip).
- [ ] Test coverage (Jacoco) >= 80% lines cho `WellnessService` và `WellnessController`.
- [ ] Native SQL query trả về chuỗi mã hóa cho tất cả fields sức khỏe (TC-003 phải PASS).
- [ ] Consent check (TC-002) phải PASS trước khi deploy.
- [ ] Không có điểm sức khỏe (stress, sleep, muscleTension) xuất hiện plaintext trong logs.

### Suspension Criteria (Điều kiện tạm dừng)
- DPO chưa sign-off phương pháp mã hóa.
- `AesDataEncryptor` bị thay đổi algorithm (từ AES/ECB sang mode khác).

---

## 7. Rollback Plan

### Revert Database (dev only)
- Xóa bảng `wellness_assessments` khỏi schema.

### Revert implementation files
```bash
git checkout -- src/main/java/com/AuraMoon/auramoon/wellness/
git checkout -- src/test/java/com/AuraMoon/auramoon/wellness/
```

### Gap vẫn OPEN
- Giữ nguyên entry trong Bảng theo dõi Sprint hiện tại.
