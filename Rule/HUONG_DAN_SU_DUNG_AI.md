# Hướng Dẫn Sử Dụng AI Trong Project SWP391 — Ngũ Sơn Resort & Spa

> **Tài liệu này tổng hợp từ thực tế sử dụng AI (Antigravity / Gemini) trong dự án.**
> Mục tiêu: giúp mọi thành viên biết cách prompt AI hiệu quả — từ đọc tài liệu, viết EDS/TDD, đến sinh code và chạy kiểm thử.

---

## Mục Lục

1. [Tổng quan quy trình](#1-tổng-quan-quy-trình)
2. [Bước 0 — Chuẩn bị trước khi giao task](#2-bước-0--chuẩn-bị-trước-khi-giao-task)
3. [Bước 1 — Yêu cầu AI đọc tài liệu đúng thứ tự](#3-bước-1--yêu-cầu-ai-đọc-tài-liệu-đúng-thứ-tự)
4. [Bước 2 — Yêu cầu AI viết EDS và TDD](#4-bước-2--yêu-cầu-ai-viết-eds-và-tdd)
5. [Bước 3 — Yêu cầu AI viết code và unit test](#5-bước-3--yêu-cầu-ai-viết-code-và-unit-test)
6. [Bước 4 — Yêu cầu AI chạy kiểm thử](#6-bước-4--yêu-cầu-ai-chạy-kiểm-thử)
7. [Prompt mẫu hoàn chỉnh](#7-prompt-mẫu-hoàn-chỉnh)
8. [Lỗi thường gặp và cách xử lý](#8-lỗi-thường-gặp-và-cách-xử-lý)
9. [Checklist Definition of Done](#9-checklist-definition-of-done)

---

## 1. Tổng Quan Quy Trình

Quy trình làm việc với AI trong project được chia làm **4 bước tuần tự**:

```
[Tài liệu dự án]
       │
       ▼
  BƯỚC 1: AI Đọc tài liệu
  (Module spec, SRS, SDS, DB SQL, Quy tắc AI)
       │
       ▼
  BƯỚC 2: AI Viết EDS + TDD
  (Dùng Template/EDS_TEMPLATE_V2.0.md + TDD_TEMPLATE_V1.md)
       │
       ▼
  BƯỚC 3: AI Viết Production Code + Unit Test
  (Service, Repository, DTO, Test class với Mockito)
       │
       ▼
  BƯỚC 4: AI Chạy kiểm thử + Báo cáo kết quả
  (mvn test, ghi Pass/Fail, ghi lệnh đã chạy)
```

> **Nguyên tắc cốt lõi:** AI **PHẢI** đọc tài liệu trước khi code. Không được đoán nghiệp vụ.

---

## 2. Bước 0 — Chuẩn Bị Trước Khi Giao Task

Trước khi giao bất kỳ task nào cho AI, thành viên cần chuẩn bị:

| Cần có | File / Vị trí |
|--------|---------------|
| Đề bài module | `DE_BAI_MODULE/Module_X.md` |
| Schema database | `SQL_DB_RESORT_SPA/resort_spa_db.sql` |
| SRS | `SRS/` folder |
| SDS / Architecture | `SDS/` folder |
| Quy tắc AI | `Quy_tac_AI_Test/` folder |
| Template EDS | `Template/EDS_TEMPLATE_V2.0.md` |
| Template TDD | `Template/TDD_TEMPLATE_V1.md` |

---

## 3. Bước 1 — Yêu Cầu AI Đọc Tài Liệu Đúng Thứ Tự

### Thứ tự bắt buộc AI phải đọc (theo AI_TEAM_RULES.md)

1. `Quy_tac_AI_Test/AI_TEAM_RULES.md` — Quy tắc chung
2. `Quy_tac_AI_Test/AI_RULES.md` — Quy tắc kỹ thuật
3. `Quy_tac_AI_Test/TESTING_GUIDELINES.md` — Tiêu chuẩn kiểm thử
4. `README.md` — Overview project
5. `SRS/SRS_Summary.md` — Actors, module, business requirement
6. `SDS/BACKEND_ARCHITECTURE.md` — Nếu sửa backend
7. `SQL_DB_RESORT_SPA/resort_spa_db.sql` — Nếu đụng entity/DB
8. `DE_BAI_MODULE/Module_X.md` — Đề bài module cụ thể

### Prompt mẫu cho Bước 1

```text
@[Quy_tac_AI_Test/AI_TEAM_RULES.md] @[Quy_tac_AI_Test/AI_RULES.md]
@[Quy_tac_AI_Test/TESTING_GUIDELINES.md]

Hãy đọc các file quy tắc trên. Sau đó đọc:
- @[DE_BAI_MODULE/Module_1.md] — đề bài module
- @[SQL_DB_RESORT_SPA/resort_spa_db.sql] — schema database thật
- @[SRS/SRS_Summary.md] — business requirements

Xác nhận đã hiểu yêu cầu và tóm tắt lại:
1. Module này gồm những Use Case nào?
2. Các entity nào liên quan?
3. Business rules quan trọng nhất là gì?
```

---

## 4. Bước 2 — Yêu Cầu AI Viết EDS Và TDD

### EDS (Engineering Design Specification)

EDS mô tả **thiết kế kỹ thuật** — architecture, data model, API design, security.

```text
@[DE_BAI_MODULE/Module_1.md]
@[Template/EDS_TEMPLATE_V2.0.md]
@[Quy_tac_AI_Test/AI_TEAM_RULES.md]

Đọc lại Module 1 và bộ Quy tắc AI.
Sử dụng Template EDS_TEMPLATE_V2.0.md để viết EDS cho Module 1.
Lưu file vào: Backend/docs/AUTH_EDS.md

Yêu cầu:
- Phải map đúng với schema trong resort_spa_db.sql
- Phải ghi rõ AES-256 encryption cho trường nhạy cảm
- Phải có RBAC matrix cho từng role
- Phải có ADR (Architecture Decision Record) cho quyết định quan trọng
```

### TDD (Test-Driven Development Specification)

TDD mô tả **test cases** — viết TRƯỚC khi code production.

```text
@[Backend/docs/AUTH_EDS.md]
@[Template/TDD_TEMPLATE_V1.md]
@[Quy_tac_AI_Test/TESTING_GUIDELINES.md]

Dựa trên EDS vừa tạo, viết TDD Specification cho Module 1.
Lưu file vào: Backend/docs/AUTH_TDD.md

Yêu cầu:
- Mỗi Use Case phải có ít nhất: happy path, validation error, not found
- Mọi API có auth/role phải có test: 401 unauthenticated, 403 forbidden
- Dữ liệu nhạy cảm phải có test: không leak, không plaintext
- Format: methodName_condition_expectedBehavior (JUnit 5 convention)
- Trạng thái test: PENDING (chờ implement)
```

> **Lưu ý thực tế:** Sau khi AI tạo xong, thành viên review và cập nhật status sang **"Approved"** + ghi tên tác giả trước khi chuyển sang bước tiếp theo.

---

## 5. Bước 3 — Yêu Cầu AI Viết Code Và Unit Test

### Prompt để yêu cầu AI viết implementation + test cùng lúc

```text
@[Backend/docs/AUTH_EDS.md] @[Backend/docs/AUTH_TDD.md]

Đọc cả 2 file này và viết phần implementation trước, sau đó viết unit test và chạy kiểm thử.

Yêu cầu production code:
- Tạo đủ: Repository, Service interface, ServiceImpl, DTOs
- Implement đúng business rules trong EDS (BR-01, BR-02, BR-20, BR-21, BR-22...)
- Dùng BusinessException với error code chuẩn (AUTH-001, AUTH-003, AUTH-404, AUTH-005...)
- Không dùng Lombok cho entity nếu có vấn đề annotation processing

Yêu cầu test:
- File: src/test/java/fu/se/smms/service/AuthModule1ServiceTest.java
- Dùng @ExtendWith(MockitoExtension.class) — KHÔNG load Spring context
- Dùng @Mock cho dependencies, @InjectMocks cho class được test
- Mỗi test case phải có: Arrange / Act / Assert comment
- @DisplayName bằng tiếng Việt, mô tả nghiệp vụ rõ ràng
- Đặt tên theo: methodName_condition_expectedBehavior
```

### Cấu trúc test class chuẩn

```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AuthModule1ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("AUTH-TC-001: Email trùng → ném AUTH-001")
    void register_duplicateEmail_throwsAuth001() {
        // Arrange
        // Act
        // Assert
    }
}
```

---

## 6. Bước 4 — Yêu Cầu AI Chạy Kiểm Thử

### Prompt để AI tự chạy test

```text
Sau khi viết xong code và test, hãy chạy kiểm thử và báo cáo kết quả.

Lệnh chạy (Windows):
cd Backend
run_tests.bat   ← hoặc Maven trực tiếp

Báo cáo phải gồm:
1. Tổng số test: X passed / Y failed / Z skipped
2. Danh sách test case với trạng thái PASS ✅ / FAIL ❌
3. Nếu fail: stack trace ngắn gọn và nguyên nhân
4. Lệnh đã chạy chính xác
```

### File `run_tests.bat` chuẩn cho dự án

```bat
@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "MAVEN_HOME=C:\Users\Administrator\Videos\FontendFor_SWP391\03.SourceCode\maven-extracted\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

"%MAVEN_HOME%\bin\mvn.cmd" test -Dtest=AuthModule1ServiceTest --no-transfer-progress
```

> **Lưu ý:** File `run_tests.bat` đã được tạo tại `Backend/run_tests.bat`

---

## 7. Prompt Mẫu Hoàn Chỉnh

### 7.1 Prompt giao task từ đầu (toàn bộ chu trình)

```text
Trước khi code, hãy đọc theo thứ tự:
1. Quy_tac_AI_Test/AI_TEAM_RULES.md
2. Quy_tac_AI_Test/AI_RULES.md
3. Quy_tac_AI_Test/TESTING_GUIDELINES.md
4. DE_BAI_MODULE/Module_1.md
5. SQL_DB_RESORT_SPA/resort_spa_db.sql
6. Template/EDS_TEMPLATE_V2.0.md
7. Template/TDD_TEMPLATE_V1.md

Sau khi đọc xong:
BƯỚC 1: Viết AUTH_EDS.md cho Module 1 → lưu vào Backend/docs/
BƯỚC 2: Viết AUTH_TDD.md cho Module 1 → lưu vào Backend/docs/
BƯỚC 3: Viết production code (Repository, Service, DTO, ServiceImpl)
BƯỚC 4: Viết unit test class AuthModule1ServiceTest.java
BƯỚC 5: Chạy mvn test và báo cáo kết quả

Chỉ sửa đúng phạm vi Module 1. Báo cáo file đã sửa + lệnh đã chạy.
```

### 7.2 Prompt chỉ viết và chạy test (khi EDS/TDD đã có)

```text
@[Backend/docs/AUTH_EDS.md] @[Backend/docs/AUTH_TDD.md]

Đọc cả 2 file này và viết phần testing trước và chạy kiểm thử.

Yêu cầu:
- Tạo đủ production classes cần thiết để test compile được
- File test: src/test/java/fu/se/smms/service/AuthModule1ServiceTest.java
- Không dùng @SpringBootTest — chỉ dùng Mockito (@ExtendWith(MockitoExtension.class))
- Mỗi TC trong TDD phải có 1 method test tương ứng
- Sau khi viết xong, chạy: mvn test -Dtest=AuthModule1ServiceTest
- Báo cáo số test pass/fail và danh sách cụ thể
```

### 7.3 Prompt kiểm tra kết quả (khi test chạy xong)

```text
Tổng hợp kết quả kiểm thử vào walkthrough.md:
- Số test: X/Y PASSED
- Danh sách 13 test case với TDD reference và Business Rule tương ứng
- Infrastructure issues đã giải quyết (nếu có)
- Cách chạy lại test để reviewer tái hiện
```

---

## 8. Lỗi Thường Gặp Và Cách Xử Lý

### 8.1 Lỗi Maven không chạy được với Java 24

**Triệu chứng:**
```
Error: -classpath requires class path specification
```

**Nguyên nhân:** Maven 3.9.6 có bug với Java 24 — biến `CLASSWORLDS_JAR` được set với embedded quotes.

**Fix:** Patch file `03.SourceCode/apache-maven-3.9.6/bin/mvn.cmd` tại dòng `CLASSWORLDS_JAR`:

```bat
❌ Sai:  for %%i in ("%MAVEN_HOME%"\boot\plexus-classworlds-*) do set CLASSWORLDS_JAR="%%i"
✅ Đúng: for %%i in ("%MAVEN_HOME%"\boot\plexus-classworlds-*) do set "CLASSWORLDS_JAR=%%~i"

❌ Sai:  -classpath %CLASSWORLDS_JAR% ^
✅ Đúng: -classpath "%CLASSWORLDS_JAR%" ^
```

---

### 8.2 Maven thiếu JAR files trong boot/

**Triệu chứng:**
```
Error: Could not find or load main class org.codehaus.plexus.classworlds.launcher.Launcher
```

**Nguyên nhân:** Thư mục `boot/` chỉ có `.license`, thiếu `plexus-classworlds-*.jar`.

**Fix:** Download lại Maven từ archive:
```powershell
$progressPreference = 'silentlyContinue'
Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" `
  -OutFile "03.SourceCode\apache-maven-3.9.6-bin.zip" -UseBasicParsing

Expand-Archive -Path "03.SourceCode\apache-maven-3.9.6-bin.zip" `
  -DestinationPath "03.SourceCode\maven-extracted" -Force
```

Sau đó dùng Maven ở `03.SourceCode\maven-extracted\apache-maven-3.9.6\`.

---

### 8.3 Lombok annotation không hoạt động khi compile

**Triệu chứng:**
```
cannot find symbol: method getPhysicalConditionEncrypted()
```

**Nguyên nhân:** Lombok `@Getter/@Setter` không được annotation processor xử lý với Java 24.

**Fix:** Thay Lombok bằng explicit Java trong entity:

```java
// ❌ Tránh dùng khi có vấn đề môi trường
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicalProfile { ... }

// ✅ Dùng explicit — luôn hoạt động
public class MedicalProfile {
    private String physicalConditionEncrypted;

    public String getPhysicalConditionEncrypted() { return physicalConditionEncrypted; }
    public void setPhysicalConditionEncrypted(String v) { this.physicalConditionEncrypted = v; }
}
```

---

### 8.4 JAVA_HOME không được nhận trong cmd

**Triệu chứng:** `mvn.cmd` không thấy JAVA_HOME dù đã set trong PowerShell.

**Fix:** Tạo file `.bat` để set và chạy trong cùng 1 process:

```bat
@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "MAVEN_HOME=C:\...\maven-extracted\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"
"%MAVEN_HOME%\bin\mvn.cmd" test -Dtest=AuthModule1ServiceTest
```

Chạy bằng: `cmd /C run_tests.bat`

---

## 9. Checklist Definition of Done

Một task với AI chỉ được coi là **DONE** khi đáp ứng đủ tất cả:

### Tài liệu
- [ ] `AUTH_EDS.md` đã viết theo template EDS_TEMPLATE_V2.0
- [ ] `AUTH_TDD.md` đã viết theo template TDD_TEMPLATE_V1
- [ ] Status được cập nhật thành **"Approved"** sau khi review
- [ ] Author được ghi đúng tên thành viên

### Code
- [ ] Repository interface đầy đủ method cần thiết
- [ ] Service interface định nghĩa đúng business operations
- [ ] ServiceImpl implement đúng business rules (BR-XX)
- [ ] DTOs đầy đủ, không leak sensitive field
- [ ] BusinessException dùng đúng error code prefix (`AUTH-`, `INV-`, `BOOK-`...)

### Test
- [ ] Unit test file tạo đúng package `fu.se.smms.service`
- [ ] Mỗi TC trong TDD có ≥ 1 method test tương ứng
- [ ] Test KHÔNG dùng @SpringBootTest (chạy thuần Mockito)
- [ ] Tên test đúng convention: `method_condition_expected`
- [ ] `@DisplayName` bằng tiếng Việt mô tả nghiệp vụ
- [ ] **Build pass: `Tests run: N, Failures: 0, Errors: 0`**

### Báo cáo
- [ ] Ghi số test pass/fail/total
- [ ] Ghi lệnh đã chạy (`mvn test -Dtest=...`)
- [ ] Ghi file đã tạo/sửa với đường dẫn cụ thể
- [ ] Ghi infrastructure issues đã giải quyết (nếu có)

---

## Phụ Lục — Kết Quả Thực Tế Module 1

Dưới đây là kết quả chạy kiểm thử thực tế của Module 1 (Authentication):

```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  11.394 s
[INFO] Finished at: 2026-06-11T11:56:38+07:00
```

### 13 test cases đã pass:

| # | Test Case | Business Rule |
|---|-----------|---------------|
| 1 | `register_duplicateEmail_throwsAuth001` | BR-01: email unique |
| 2 | `register_newEmail_returnsUserWithInactiveStatus` | BR-02: INACTIVE khi đăng ký |
| 3 | `updateStaffStatus_staffNotFound_throwsAuth404` | BR-22: RBAC staff lock |
| 4 | `updateStaffStatus_lockStaff_setsInactive` | BR-22: Admin lock nhân viên |
| 5 | `saveProfile_consentFalse_throwsAuth003` | Decree 13/2023 Art.6 consent |
| 6 | `saveProfile_consentNull_throwsAuth003` | Decree 13/2023 Art.6 consent |
| 7 | `saveProfile_consentTrue_savesSuccessfully` | UC02: lưu hồ sơ y tế |
| 8 | `getForRole_chefRole_onlyFoodAllergiesReturned` | BR-21: CHEF filter |
| 9 | `getForRole_therapistRole_onlyPhysicalConditionReturned` | BR-21: THERAPIST filter |
| 10 | `getForRole_receptionistRole_throwsAuth005` | BR-21: RECEPTIONIST forbidden |
| 11 | `getForRole_managerRole_allFieldsReturned` | BR-21: MANAGER full access |
| 12 | `deleteSensitiveProfile_existingProfile_wipesData` | BR-20: Right to Deletion |
| 13 | `deleteSensitiveProfile_profileNotFound_throwsAuth404` | BR-20: profile not found |

---

*Tài liệu được tổng hợp từ thực tế sử dụng AI trong dự án SWP391 — Ngũ Sơn Resort & Spa Management System.*
*Cập nhật lần cuối: 2026-06-11 bởi nhóm G3 SE2023.*
