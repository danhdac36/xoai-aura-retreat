# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0

## Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field                    | Value                                                                                              |
| :----------------------- | :------------------------------------------------------------------------------------------------- |
| **Document ID**    | `XOA-MOD1-IMP-004`                                                                               |
| **Version**        | 1.2                                                                                                |
| **Date**           | 2026-06-30                                                                                         |
| **Status**         | Approve                                                                                            |
| **Document Owner** | System Administrator                                                                               |
| **Author**         | NgocNM (Senior Software Engineer)                                                                  |
| **Reviewed by**    | Tech Lead                                                                                          |
| **DPO Sign-off**   | N/A (Dữ liệu danh mục không chứa PII của khách hàng) |
| **Approved by**    | Principal Architect                                                                                |
| **Last Review**    | 2026-06-30                                                                                         |
| **Based on EDS**   | v2.0                                                                                               |

---

## CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ. Mọi thay đổi phải ghi vào bảng này.

| Ngày      | Người thực hiện | Nội dung thay đổi                                                             |
| :--------- | :------------------ | :------------------------------------------------------------------------------- |
| 2026-06-25 | NgocNM              | Tạo tài liệu lần đầu dựa trên đặc tả UC04                             |
| 2026-06-25 | NgocNM              | Chỉnh sửa kiến trúc hoàn toàn sang Spring Boot MVC (Thymeleaf) theo Policy |
| 2026-06-30 | NgocNM              | Cập nhật phạm vi Master Data (Retreat, Villa, Menu, Yoga, Spa) và logic Parse chuỗi mô tả Retreat Package |

---

## MỤC LỤC

1. [Tổng quan Module](#1-tổng-quan-module)
2. [Ma trận Truy vết (Traceability Matrix)](#2-ma-trận-truy-vết-traceability-matrix)
3. [Architecture Decision Records (ADR)](#3-architecture-decision-records-adr)
4. [Non-Functional Requirements & SLA](#4-non-functional-requirements--sla)
5. [Static Modeling (Mô hình Tĩnh)](#5-static-modeling-mô-hình-tĩnh)
6. [Dynamic Modeling (Mô hình Hướng Động)](#6-dynamic-modeling-mô-hình-hướng-động)
7. [Domain Event Catalog](#7-domain-event-catalog)
8. [Interface Specification (Đặc tả Giao diện)](#8-interface-specification-đặc-tả-giao-diện)
9. [API Specification](#9-api-specification)
10. [Bảng mã lỗi (Error Codes)](#10-bảng-mã-lỗi-error-codes)
11. [Quy trình Triển khai (Step-by-Step)](#11-quy-trình-triển-khai-step-by-step)
12. [Rollback & Incident Runbook](#12-rollback--incident-runbook)
13. [Kịch bản Kiểm thử Chi tiết](#13-kịch-bản-kiểm-thử-chi-tiết)
14. [Phương pháp Xác minh](#14-phương-pháp-xác-minh)
15. [Mẫu thử thực tế (API Verification Samples)](#15-mẫu-thử-thực-tế-api-verification-samples)
16. [Bảng tổng hợp phân quyền (Authorization Matrix)](#16-bảng-tổng-hợp-phân-quyền-authorization-matrix)

---

## 1. Tổng quan Module

> [!NOTE]
> Mô tả ngắn gọn mục đích của module, phạm vi nghiệp vụ và lý do tồn tại.

| Field                           | Value                                                                                |
| :------------------------------ | :----------------------------------------------------------------------------------- |
| **Module Name**           | Master Data Management (UC04)                                                        |
| **Bounded Context**       | Core Configuration & System Setup                                                    |
| **Data Classification**   | Internal / Public (Các gói dịch vụ, menu được publish rộng rãi)                                |
| **Compliance Scope**      | Internal Auditing                                                                    |
| **Upstream Dependencies** | Identity & Access Management (Module 1 - Admin Auth)                                 |
| **Downstream Consumers**  | Booking (Module 2), Spa Schedule & Yoga (Module 3), F&B Menu (Module 4), Reports (Module 5) |

---

## 2. Ma trận Truy vết (Traceability Matrix)

> [!NOTE]
> Ánh xạ trực tiếp: `[Mã yêu cầu]` → `[Thành phần Code]` → `[Mục tiêu Tuân thủ]`.

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu                                            | Thành phần Code                  | Compliance Target | ADR liên quan |
| :------------- | :---------------- | :----------------------------------------------------------- | :--------------------------------- | :---------------- | :------------- |
| BR-13          | Business Rule     | Yêu cầu sử dụng Soft Delete thay vì xóa cứng vật lý cho Master Data (Retreat, Villa, Menu, Yoga, Spa) | `MasterDataService.softDelete()` | Data Integrity    | ADR-001        |
| BR-15          | Business Rule     | Ghi lại toàn bộ thao tác CRUD vào `AUDIT_LOG`          | `AuditLogAspect`                 | Accountability    | —             |
| BR-16          | Business Rule     | Ràng buộc validation (giá >= 0, code duy nhất)           | `@Valid` trên `MasterDataDto` | Data Consistency  | —             |
| BR-17          | Business Rule     | Xử lý cấu trúc chuỗi mô tả Retreat Package bằng ký hiệu `[DAY]` để render chi tiết lịch trình mà không thay đổi DB | `RetreatPackageService.parseDescription()` | Flexibility       | ADR-003        |

---

## 3. Architecture Decision Records (ADR)

### `ADR-001` — Áp dụng Cơ chế Soft Delete cho toàn bộ Master Data

| Field              | Value                       |
| :----------------- | :-------------------------- |
| **Status**   | Accepted                    |
| **Deciders** | System Architect, Tech Lead |
| **Date**     | 2026-06-25                  |

#### Bối cảnh (Context)
> [!NOTE]
> Master Data (Gói Nghỉ Dưỡng, Hạng Villa, Thực Đơn, Lớp Yoga, Dịch vụ Spa) được liên kết chặt chẽ với các giao dịch vận hành như Booking, Hóa đơn. Việc xóa vật lý sẽ làm đứt gãy khóa ngoại và thất thoát dữ liệu thống kê lịch sử (Guest Folio).

#### Các phương án đã xem xét (Options Considered)
| Phương án | Mô tả                                    | Ưu điểm                                        | Nhược điểm                                                         |
| :----------- | :----------------------------------------- | :------------------------------------------------ | :--------------------------------------------------------------------- |
| A            | Xóa cứng (Hard Delete)                   | CSDL sạch sẽ, nhỏ gọn.                        | Lỗi rác dữ liệu, vi phạm báo cáo tài chính.                   |
| B            | Cờ đánh dấu `is_delete` (Soft Delete) | Dữ liệu lịch sử an toàn, dễ dàng rollback. | Câu lệnh SQL lấy dữ liệu phải luôn gắn `WHERE is_delete = 0`. |

#### Quyết định (Decision)
> [!NOTE]
> Chọn Phương án `[B]` vì đảm bảo tính toàn vẹn của dữ liệu Booking và kế toán lịch sử. 

#### Hệ quả (Consequences)
**Tích cực**: Báo cáo tài chính và Guest Folio luôn khớp số liệu và thông tin dịch vụ.
**Tiêu cực / Trade-offs**: Các Repository layer phải tự động lọc bản ghi có `is_delete = 0` bằng annotation Hibernate `@SQLRestriction`.

---

### `ADR-002` — Spring Boot MVC kết hợp AJAX trả về Thymeleaf Fragment

| Field              | Value          |
| :----------------- | :------------- |
| **Status**   | Accepted       |
| **Deciders** | Front-end Lead |
| **Date**     | 2026-06-25     |

#### Quyết định (Decision)
> [!NOTE]
> Sử dụng các AJAX Requests (ví dụ thông qua Fetch API) gửi dữ liệu Form data (`@ModelAttribute`) về `@Controller`. Controller xử lý nghiệp vụ và trả về các file mẫu **HTML Fragment (Thymeleaf)** để Javascript tự động hoán đổi (swap) phần DOM thay vì phải render lại toàn trang.

---

### `ADR-003` — Xử lý Lịch trình Retreat Package bằng Kỹ thuật Parse Chuỗi

| Field              | Value          |
| :----------------- | :------------- |
| **Status**   | Accepted       |
| **Deciders** | System Architect |
| **Date**     | 2026-06-30     |

#### Quyết định (Decision)
> [!NOTE]
> Để tránh việc phải tạo thêm bảng phụ để lưu lịch trình từng ngày của Retreat Package, toàn bộ nội dung lịch trình sẽ được nhập vào trường `description` dưới định dạng chuỗi phân cách bởi tag `[DAY]`. Tầng Service trong Spring Boot sẽ parse chuỗi này thành đối tượng DTO gồm mô tả chung và mảng các hoạt động từng ngày để Thymeleaf dễ dàng render.

---

## 4. Non-Functional Requirements & SLA

### 4.1. Performance & Availability

| Category     | Requirement                    | Target SLA | Measurement Method | Compliance Basis |
| :----------- | :----------------------------- | :--------- | :----------------- | :--------------- |
| Latency      | Page load time / Fragment load | < 500ms    | k6 load test       | UX Standard      |
| Availability | Uptime (monthly)               | 99.9%      | Uptime monitor     | Core Setup Req   |

---

## 5. Static Modeling (Mô hình Tĩnh)

### 5.1. Class Diagram (PlantUML)

```mermaid
classDiagram
    class MasterDataController {
        +viewMasterData(model: Model): String
        +createData(category: String, form: Object, model: Model): String
        +updateData(category: String, id: Long, form: Object, model: Model): String
        +deleteData(category: String, id: Long, model: Model): String
    }
    class RetreatPackageService {
        +parseDescription(raw: String): RetreatItineraryDto
    }
    class RetreatItineraryDto {
        -String generalDescription
        -List~String~ dailyActivities
    }

    MasterDataController --> RetreatPackageService : uses
    RetreatPackageService ..> RetreatItineraryDto : creates
```

### 5.2. Data Structure (JPA Entities)

```java
// === [RETREAT_PACKAGE] SCHEMA ===
@Entity
@Table(name = "RETREAT_PACKAGE")
@SQLRestriction("is_delete = 0")
public class RetreatPackage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;
    private String typePackage;
    private String packageName;
    private Integer durationDays;
    private BigDecimal price;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description; // Lưu trữ chuỗi chứa tag [DAY]
    
    private boolean isDelete = false;
    private boolean isActive = true;
}
```

---

## 6. Dynamic Modeling (Mô hình Hướng Động)

### 6.1. Sequence Diagram — Happy Path (PlantUML)

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrator
    participant Browser as Browser (Thymeleaf/JS)
    participant Controller as MasterDataController (@Controller)
    participant Service as MasterDataService
    participant Repo as JpaRepository
    participant DB as MS SQL

    Admin->>Browser: Nhập Form & Bấm Save (Thêm mới)
    Browser->>Controller: POST (AJAX) /admin/master-data/{category}/create<br/>(Content-Type: application/x-www-form-urlencoded)
    activate Controller
    Controller->>Controller: Validate Data (@ModelAttribute)
    Controller->>Service: processCreate()
    activate Service
    Service->>Repo: save()
    activate Repo
    Repo->>DB: INSERT
    DB-->>Repo: result
    deactivate Repo
    Service-->>Controller: Success
    deactivate Service
    Controller-->>Browser: Trả về HTML Fragment (ví dụ: dòng table mới)
    deactivate Controller
    Browser-->>Admin: Chèn Fragment vào DOM (Hiển thị ngay, không load trang)
```

---

## 7. Domain Event Catalog

### 7.1. Events Published (Phát ra)

| Event Name            | Trigger                       | Publisher             | Subscriber(s)       | Payload Schema    | Async? |
| :-------------------- | :---------------------------- | :-------------------- | :------------------ | :---------------- | :----- |
| `MasterDataUpdated` | Thêm, Sửa, Xóa Master Data | `MasterDataService` | `AuditLogService` | `AuditLogEvent` | Yes    |

---

## 8. Interface Specification (Đặc tả Giao diện)

### 8.1. Service Interface

```java
public interface IMasterDataService<T, ID> {
  /**
   * Cập nhật danh mục
   * @throws IllegalArgumentException Khi ID không tồn tại
   */
  T update(ID id, T dto);

  /**
   * Xóa mềm danh mục.
   */
  void softDelete(ID id);
}
```

---

## 9. API Specification (Spring MVC Endpoints)

### 9.1. Endpoints Table

| Method | Path                                          | Auth Level | Required Roles | Rate Limit | Return Type                                           |
| :----- | :-------------------------------------------- | :--------- | :------------- | :--------- | :---------------------------------------------------- |
| GET    | `/admin/master-data`                        | Session    | `ADMIN`      | 100/min    | View (`master-data/index.html`)                     |
| GET    | `/admin/master-data/{category}`             | Session    | `ADMIN`      | 100/min    | Fragment (`master-data/fragments :: tabContent`)    |
| POST   | `/admin/master-data/{category}/create`      | Session    | `ADMIN`      | 60/min     | Fragment (`master-data/fragments :: dataRow`)       |
| POST   | `/admin/master-data/{category}/{id}/update` | Session    | `ADMIN`      | 60/min     | Fragment (`master-data/fragments :: dataRow`)       |
| POST   | `/admin/master-data/{category}/{id}/delete` | Session    | `ADMIN`      | 60/min     | Fragment (`master-data/fragments :: emptyResponse`) |

### 9.2. Request / Response Schemas (Spring MVC)

#### POST `/admin/master-data/retreat-packages/create`

**Request Body (`application/x-www-form-urlencoded`)**:

```text
packageName=Detox+Retreat&durationDays=3&price=5000000&description=Mô+tả+chung[DAY]Ngày+1...+[DAY]Ngày+2...
```

**Response — 200 OK (Thymeleaf HTML Fragment)**:

```html
<tr id="row-pkg-1">
  <td>Detox Retreat</td>
  <td>3 Ngày</td>
  <td>5,000,000 VND</td>
  <td><span class="badge bg-success">Active</span></td>
  <td>
      <button class="btn btn-sm btn-primary" onclick="editData(1)">Sửa</button>
      <button class="btn btn-sm btn-danger" onclick="deleteData(1)">Xóa</button>
  </td>
</tr>
```

---

## 10. Bảng mã lỗi (Error Codes)

| Code            | HTTP Status | Message (VI)                              | Trigger Condition                      |
| :-------------- | :---------- | :---------------------------------------- | :------------------------------------- |
| `MST-001`     | 400         | Dữ liệu Form không hợp lệ            | `BindingResult.hasErrors()`          |
| `MST-002`     | 409         | Mã định danh đã tồn tại            | Trùng lặp code                     |
| `MST-003`     | 404         | Không tìm thấy                         | Sửa/Xóa ID không tồn tại          |
| `MST-004`     | 400         | Định dạng ngày trong mô tả không hợp lệ | Thiếu `[DAY]` separator trong package |
| `MST-WARN-01` | 200         | Cảnh báo: Danh mục đang được dùng | Xóa mềm danh mục đang hoạt động |

---

## 11. Quy trình Triển khai (Step-by-Step)

### 11.1. Prerequisites

- [X] Đã thiết kế xong Database Schema (`DB.sql`) với cờ `is_delete` đầy đủ.
- [X] Đã tạo thư mục `static/js/module1` và `static/css/module1` theo đúng Layout Principle.

### 11.2. Implementation Steps

- **Bước 1:** Khởi tạo `MasterDataController` với annotation `@Controller`.
- **Bước 2:** Cấu hình `@SQLRestriction("is_delete = 0")` trên các JPA Entity (RetreatPackage, Villa, Menu, Yoga, Spa).
- **Bước 3:** Tách Javascript (xử lý gọi AJAX hoán đổi nội dung Fragment) ra file riêng: `static/js/module1/master-data.js`.
- **Bước 4:** Xây dựng tính năng `parseDescription` cho RetreatPackage Service.
- **Bước 5:** Thiết kế giao diện Thymeleaf bao gồm 1 file tổng (`index.html`) và 1 file chứa các components (`fragments.html`).

---

## 12. Rollback & Incident Runbook

### 12.1. Điều kiện kích hoạt Rollback (Trigger Conditions)

| Điều kiện                             | Ngưỡng                        | Người quyết định |
| :--------------------------------------- | :------------------------------ | :-------------------- |
| Exception liên tục khi render template | > 5 lỗi TemplateInputException | Tech Lead             |

### 12.2. Rollback Procedure

- Git Revert commit gần nhất, redeploy ứng dụng. Không cần thao tác Database.

---

## 13. Kịch bản Kiểm thử Chi tiết

### 13.1. Unit Tests

#### `TC-MST-001` — Test validate MVC Form

- **Feature**: `MasterData Validation`
- **Scenario**: Nhập giá trị âm cho đơn giá.
  - **Given** Form data truyền vào `price = -100`
  - **When** POST tới `/admin/master-data/...`
  - **Then** Hàm xử lý trả về Fragment chứa div `<div class="error">Giá tiền phải lớn hơn 0</div>`

#### `TC-MST-002` — Test chuỗi Retreat Package

- **Feature**: `Retreat Itinerary Parsing`
- **Scenario**: Chuỗi chứa `[DAY]` đúng định dạng.
  - **Given** Chuỗi `Tổng quan [DAY] Hoạt động 1 [DAY] Hoạt động 2`
  - **When** Parse tại service
  - **Then** Trả về DTO với `generalDescription = Tổng quan`, và mảng `dailyActivities` có size = 2.

---

## 14. Phương pháp Xác minh

### 14.1. Database Inspection

- *Verify Soft Delete hoạt động*:

```sql
SELECT status, is_delete FROM RETREAT_PACKAGE WHERE package_id = 5;
-- Expected: 1
```

### 14.2. Javascript/CSS Asset Verification

- *Kiểm tra không có Script/Style rác trên HTML file*:

```bash
grep -n "<script>" master-data/index.html
# Expected: Trống rỗng (Tất cả script phải nằm ở file JS riêng).
```

---

## 15. Mẫu thử thực tế (API Verification Samples)
(N/A cho Spring MVC returning View Fragments)

---

## 16. Bảng tổng hợp phân quyền (Authorization Matrix)

| Endpoint                            | GUEST | RECEPTIONIST | THERAPIST | CHEF | ADMIN |
| :---------------------------------- | :---: | :----------: | :-------: | :--: | :---: |
| GET `/admin/master-data/*`         |  ❌  |      ❌      |    ❌    |  ❌  |  ✅  |
| POST `/admin/master-data/*/create` |  ❌  |      ❌      |    ❌    |  ❌  |  ✅  |
| POST `/admin/master-data/*/update` |  ❌  |      ❌      |    ❌    |  ❌  |  ✅  |
| POST `/admin/master-data/*/delete` |  ❌  |      ❌      |    ❌    |  ❌  |  ✅  |

**Chú thích**:

- ✅ = Được phép xem và sử dụng tính năng.
- ❌ = Bị từ chối, chuyển hướng tới trang 403.
