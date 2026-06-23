# Báo Cáo Phân Tích Lỗi 500: Invalid column name 'create_at'

## 1. Nguyên nhân gốc rễ (Root Cause)
Lỗi `InvalidDataAccessResourceUsageException: JDBC exception executing SQL [Invalid column name 'create_at']` xảy ra khi truy cập endpoint `/profile/me`. 

Nguyên nhân trực tiếp là do Hibernate (ORM) đang cố gắng sinh ra câu lệnh SQL `SELECT` có chứa cột `create_at` từ bảng `PHYSICAL_HEALTH_PROFILE` (và tương tự với bảng `CONSENT`). Tuy nhiên, trong cơ sở dữ liệu thực tế (SQL Server), các bảng này **không hề có cột `create_at`**.

## 2. Phân tích mã nguồn hiện tại
Mặc dù bạn có đề cập rằng đã sửa entity, nhưng theo trạng thái mã nguồn hiện tại tôi vừa rà soát, cấu trúc của các lớp vẫn đang kế thừa từ `BaseEntity`:

**Tại file `PhysicalHealthProfile.java`**:
```java
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class PhysicalHealthProfile extends BaseEntity { // VẪN ĐANG KẾ THỪA BaseEntity
```

**Tại file `Consent.java`**:
```java
@EqualsAndHashCode(callSuper = true)
public class Consent extends BaseEntity { // VẪN ĐANG KẾ THỪA BaseEntity
```

Lớp `BaseEntity` (nằm trong `com.AuraMoon.auramoon.common.entity.BaseEntity`) có chứa trường `@CreatedDate private LocalDateTime createdAt;`. Do đó, bất kỳ entity nào `extends BaseEntity` đều sẽ tự động bị Hibernate ánh xạ thêm cột `create_at` và `is_delete` vào các truy vấn SQL.

## 3. Cách khắc phục (Hướng dẫn thao tác tay)
Vì yêu cầu **"Cấm sửa code"**, tôi sẽ hướng dẫn chi tiết để bạn tự tay sửa các file này:

### Bước 1: Sửa file `PhysicalHealthProfile.java`
- Xóa `extends BaseEntity`.
- Xóa `@EqualsAndHashCode(callSuper = true)`.
- Xóa khối `@AttributeOverrides(...)`.
- Thêm thuộc tính `updatedAt` trực tiếp vào class.

**Code sau khi sửa nên giống thế này:**
```java
// Thêm import nếu cần
import java.time.LocalDateTime;

@Entity
@Table(name = "PHYSICAL_HEALTH_PROFILE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhysicalHealthProfile {
    // ... giữ nguyên các trường khác (id, userId, medicalConditions, injuries)

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
```

### Bước 2: Sửa file `Consent.java` (Tương tự)
- Xóa `extends BaseEntity`.
- Xóa `@EqualsAndHashCode(callSuper = true)`.
- Thêm thuộc tính `updatedAt` trực tiếp vào class.

**Code sau khi sửa nên giống thế này:**
```java
// Thêm import
import java.time.LocalDateTime;

@Entity
@Table(name = "CONSENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {
    // ... giữ nguyên các trường khác (id, user, consentStatus, consentVersion)

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
```

## 4. Kết luận
Lỗi xảy ra hoàn toàn do sự bất đồng bộ giữa cấu trúc Entity trong code (có kế thừa cột `create_at` thông qua `BaseEntity`) và cấu trúc bảng thực tế dưới Database (không có cột `create_at`). 

Bạn chỉ cần loại bỏ sự kế thừa này và tự định nghĩa cột `update_at` trong các Entity tương ứng, sau đó build lại ứng dụng (`mvn compile`) là lỗi 500 sẽ biến mất.
