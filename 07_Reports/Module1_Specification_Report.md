# BÁO CÁO ĐẶC TẢ CHI TIẾT MODULE 1: AUTHENTICATION & SENSITIVE HEALTH PROFILE

**Dự án:** Xoai Aura Retreat - Wellness Resort & Spa Management System
**Mã dự án:** SWP391-HOS-03
**Tác giả:** Nhóm Phát Triển G6
**Vai trò báo cáo:** Senior Software Engineer

---

## I. Tổng Quan Module 1

Module **Authentication & Sensitive Health Profile** đóng vai trò là "Trọng tâm Bảo mật & Dữ liệu Nhạy cảm" của toàn bộ hệ thống Xoai Aura Retreat. Module này chịu trách nhiệm xác thực người dùng, quản lý phân quyền dựa trên vai trò (RBAC), thu thập hồ sơ y tế/ăn kiêng nhạy cảm và đảm bảo tính tuân thủ pháp lý theo các quy định nghiêm ngặt về bảo vệ dữ liệu cá nhân tại Việt Nam.

### 1. Phạm vi Nghiệp vụ (Business Scope)

* Đăng ký tài khoản khách hàng mới, gửi mã xác thực email và đăng nhập (sử dụng mật khẩu truyền thống hoặc Google OAuth2 SSO).
* Khôi phục mật khẩu thông qua cơ chế gửi token bảo mật dùng một lần qua email.
* Thiết lập và cập nhật Hồ sơ Sức khỏe & Ăn kiêng (Health & Dietary Profile).
* Quản lý danh sách tài khoản nhân viên (Therapist, Chef, Receptionist) và phân công vai trò (Admin).
* Hỗ trợ quyền yêu cầu xóa dữ liệu vĩnh viễn (Right to Erasure / Right to be forgotten) của khách hàng sau khi kỳ nghỉ kết thúc.

### 2. Nguyên Tắc Thiết Kế Kỹ Thuật (Tuân thủ Principles.md)

* **Kiến trúc:** Sử dụng mô hình **Spring Boot MVC**, nơi Controller chịu trách nhiệm định tuyến, xử lý nghiệp vụ thông qua `@ModelAttribute` và trả về giao diện trực tiếp bằng **Thymeleaf HTML** (Không thiết kế theo dạng REST API thuần túy).
* **Tách biệt tài nguyên tĩnh:** Không viết code JavaScript (`<script>`) hoặc CSS (`<style>`) nội tuyến trực tiếp trong các file HTML của giao diện. Mọi file bổ trợ phải được tách riêng thành các file độc lập và lưu trữ trong thư mục tĩnh theo đúng phân hệ của Module 1:
  - CSS: `src/main/resources/static/css/auth/`
  - JS: `src/main/resources/static/js/auth/`

---

## II. Các Quy Định Pháp Lý & Tuân Thủ (Compliance)

Hệ thống phải tích hợp các quy định pháp lý quốc gia vào thiết kế giao diện và cơ sở dữ liệu:

### 1. Nghị định số 356/2025/NĐ-CP về Bảo vệ Dữ liệu Cá nhân

* **Phân loại dữ liệu nhạy cảm:** Thông tin về tình trạng sức khỏe (bệnh lý, chấn thương cũ) và thông tin ăn kiêng/dị ứng nghiêm trọng được phân loại là dữ liệu cá nhân nhạy cảm.
* **Cơ chế thu thập (Consent Management):** Giao diện khi khách hàng điền form thông tin sức khỏe bắt buộc phải hiển thị tuyên bố đồng ý (Consent statement) với **hộp kiểm (Checkbox) để trống mặc định**. Hệ thống chỉ cho phép lưu khi khách hàng chủ động tích chọn. Nút "Lưu" sẽ bị vô hiệu hóa (Disabled) cho đến khi hộp kiểm được tích chọn.
* **Mã hóa dữ liệu tại chỗ (Encryption at rest):** Toàn bộ dữ liệu nhạy cảm phải được mã hóa trước khi lưu xuống Database. Khi truy vấn lên Java để xử lý, dữ liệu mới được giải mã tự động.

### 2. Luật Cư trú năm 2020

* Nhằm phục vụ công tác khai báo tạm trú bắt buộc cho chính quyền địa phương, hệ thống thu thập số CCCD/Hộ chiếu (`Identify_code`) của khách hàng lúc check-in.
* Thông tin CCCD/Hộ chiếu này cũng được coi là thông tin định danh cá nhân quan trọng (PII) và phải được mã hóa bảo mật khi lưu trữ trong cơ sở dữ liệu bảng `[USER]`.

---

## III. Phân Quyền & Giảm Thiểu Dữ Liệu (RBAC & Data Minimization)

Mô hình phân quyền dựa trên vai trò (Role-Based Access Control) được triển khai chặt chẽ ở cả tầng Giao diện (Thymeleaf Security) và tầng Cơ sở dữ liệu để thực thi nguyên tắc giảm thiểu dữ liệu (Data Minimization):

| Vai Trò (Role)            |            Chức năng Xác thực & Quản trị            |                         Xem CCCD/Hộ chiếu                         |         Xem Ghi chú Bệnh lý (Physical notes)         |       Xem Thông tin Dị ứng (Allergies)       |
| -------------------------- | :--------------------------------------------------------: | :------------------------------------------------------------------: | :------------------------------------------------------: | :----------------------------------------------: |
| **Guest**            |            Tự quản lý tài khoản cá nhân            |                        Có (chỉ của mình)                        |                  Có (chỉ của mình)                  |              Có (chỉ của mình)              |
| **Receptionist**     |              Check-in, Check-out, Ghi folio              | **Không** (chỉ hiển thị dưới dạng che khuất `*****`) |                     **Không**                     |                 **Không**                 |
| **Spa Therapist**    |                  Xem lịch làm việc spa                  |                                Không                                | **Có** (để chuẩn bị bài trị liệu vật lý) |                      Không                      |
| **Chef / F&B Staff** |             Xem danh sách chuẩn bị món ăn             |                                Không                                |                          Không                          | **Có** (để lọc món ăn gây dị ứng) |
| **Admin**            | Quản lý tài khoản, Master Data,Xem báo cáo doanh thu |                                Không                                |                          Không                          |                      Không                      |
|                            |                                                            |                                                                      |                                                          |                                                  |

> [!IMPORTANT]
> Việc lọc dữ liệu phải được thực hiện ở mức truy vấn DB (SQL/JPA) hoặc che khuất (Masking) ở tầng DTO trước khi gửi lên View, tránh việc gửi toàn bộ Object User về trình duyệt rồi ẩn bằng CSS/JS.

---

## IV. Đặc Tả Chi Tiết Các Use Case (UC) thuộc Module 1

### 1. UC01: Đăng ký & Đăng nhập Hệ thống

* **Luồng truyền thống:** Khách hàng nhập Email, Họ tên, Mật khẩu. Mật khẩu được mã hóa một chiều qua thuật toán **BCrypt** trước khi thực hiện câu lệnh `INSERT` vào bảng `[USER]`. Hệ thống sinh ra một `verify_token` ngẫu nhiên và gửi link xác thực qua mail của khách.  Khách bấm vào link và tài khoản được kích hoạt. Tài khoản chỉ được kích hoạt (Status = `Active`) sau khi người dùng click vào link xác thực.
* **Luồng SSO (Single Sign-On):** Tích hợp Google Identity API. Khi người dùng click nút đăng nhập bằng Google, hệ thống xác thực mã Token trả về từ Google, lấy ra địa chỉ email. Nếu email chưa tồn tại trong hệ thống, tự động tạo mới tài khoản với vai trò `GUEST`.

### 2. UC02: Hoàn thiện Hồ sơ Sức khỏe & Ăn kiêng

* **Giao diện Form:** Chia làm 2 khu vực rõ ràng:
  - Bên trái: **Dietary Profile** (Dị ứng thực phẩm: Lạc, Hải sản, Bơ sữa, Gluten; Lựa chọn chế độ ăn: Chay, Keto, Halal...).
  - Bên phải: **Physical Health Profile** (Bệnh lý nền: Cao huyết áp, tiểu đường; Các chấn thương gần đây: Đau lưng, chấn thương khớp...).
* **Ràng buộc Nghiệp vụ (Business Rule):**
  - Phải tích chọn "Tôi đồng ý cung cấp thông tin y tế nhằm cá nhân hóa dịch vụ nghỉ dưỡng..." (Checkbox này mặc định `false`).
  - Nút "Lưu & Tiếp tục" chỉ kích hoạt khi Checkbox trên được chọn.
  - Khi ấn lưu, dữ liệu nhạy cảm được đưa qua bộ chuyển đổi mã hóa đối xứng AES-256 để lưu trữ an toàn trong DB.

### 3. UC03: Quản lý Tài khoản Nhân viên & Phân Vai trò (Cho Admin)

* Admin có quyền tạo mới tài khoản nhân viên (gán email, mật khẩu tạm thời).
* Gán vai trò cụ thể (`ROLE`) cho nhân viên: `RECEPTIONIST`, `THERAPIST`, `CHEF`, `MANAGER`, `ADMIN`.
* Riêng đối với vai trò kỹ thuật viên trị liệu, hệ thống tự động chèn một bản ghi tương ứng vào bảng `THERAPIST` để quản lý trạng thái sẵn sàng làm việc của họ.

### 4. UC04: Quản lý Master Data, Villa Categories, spa service, retreat package

* Admin có thể xem, thêm, sửa, xóa các dữ liệu của Villa, spa service, retreat package, Meal-Item
* Khi xóa thì trạng thái record đó là isDelete = true.(xóa mềm)

### 5. UC05: Yêu cầu Xóa Dữ liệu Nhạy cảm (Right to Erasure)

* Theo quy định bảo mật, khách hàng có quyền yêu cầu xóa vĩnh viễn dữ liệu sức khỏe sau khi hoàn tất kỳ nghỉ dưỡng.
* Khách hàng click "Xóa vĩnh viễn dữ liệu nhạy cảm" tại trang cá nhân.
* **Xác thực:** Yêu cầu khách hàng nhập lại mật khẩu hiện tại để xác minh danh tính.
* **Thực thi:** Hệ thống thực hiện câu lệnh **xóa cứng** (`DELETE`) hoặc cập nhật trắng (`SET NULL` / `UPDATE` chuỗi rỗng) các trường thông tin trong bảng `PHYSICAL_HEALTH_PROFILE` và `DIETARY_PROFILE`. Đồng thời ghi nhận vào log lịch sử và tự động đăng xuất người dùng để hủy phiên làm việc (Session Invalidation).

---

## V. Ánh Xạ Cơ Sở Dữ Liệu (Database Mapping)

Dựa trên cấu trúc file thiết kế cơ sở dữ liệu `HoS.sql`, các bảng do Module 1 trực tiếp quản lý và các lưu ý kỹ thuật bảo mật bao gồm:

```mermaid
erDiagram
    ROLE ||--o{ USER : "has"
    USER ||--o| THERAPIST : "is a"
    USER ||--o{ CONSENT : "provides"
    USER ||--o| PHYSICAL_HEALTH_PROFILE : "has"
    USER ||--o| DIETARY_PROFILE : "has"

    USER {
        int user_id PK
        int role_id FK
        string email UNIQUE
        string password_hash
        string full_name
        string Identify_code "PII - Encrypted"
        string verify_token "Reset/Verify Token"
        string status
    }

    PHYSICAL_HEALTH_PROFILE {
        int profile_id PK
        int user_id FK
        string medical_conditions "Sensitive - Encrypted"
        string injuries "Sensitive - Encrypted"
    }

    DIETARY_PROFILE {
        int dietary_id PK
        int user_id FK
        string food_allergies "Sensitive - Encrypted"
        string diatary_preference "Sensitive - Encrypted"
    }
```

### Chi tiết ánh xạ và yêu cầu mã hóa ở tầng DB:

1. **Bảng `[USER]`**:
   * Trường `password_hash` bắt buộc lưu trữ chuỗi băm BCrypt.
   * Trường `Identify_code` (CCCD/Hộ chiếu) lưu trữ thông tin định danh cá nhân, bắt buộc mã hóa AES-256 trước khi lưu xuống.
2. **Bảng `PHYSICAL_HEALTH_PROFILE`**:
   * Cả hai trường `medical_conditions` và `injuries` bắt buộc được mã hóa AES-256.
3. **Bảng `DIETARY_PROFILE`**:
   * Trường `food_allergies` và `diatary_preference` (dietary_preference) bắt buộc được mã hóa AES-256.
4. **Bảng `CONSENT`**:
   * Lưu trữ vết đồng ý của khách hàng (`consent_status = 1`, thời gian đồng ý `update_at`, và phiên bản chính sách bảo mật `consent_version`).

---

## VI. Định Hướng Thiết Kế Mã Nguồn (Spring Boot MVC Blueprint)

### 1. Cấu trúc Lớp (Layered Architecture)

Mã nguồn Java trong thư mục `auramoon/src/main/java/com/AuraMoon/auramoon` sẽ được tổ chức theo các phân lớp nghiệp vụ rõ ràng:

* **`entity/`**: Chứa các JPA Entities (Ví dụ: `User.java`, `Role.java`, `PhysicalHealthProfile.java`, `DietaryProfile.java`).
* **`repository/`**: Chứa các Spring Data JPA Repositories (Ví dụ: `UserRepository.java`, `PhysicalHealthProfileRepository.java`).
* **`service/`**: Chứa các Interface định nghĩa logic nghiệp vụ.
* **`service/impl/`**: Lớp thực thi của Service.
* **`controller/`**: Chứa Spring MVC Controllers trả về Thymeleaf views.
* **`dto/`**: Các đối tượng truyền tải dữ liệu (ví dụ: `UserRegistrationDto.java`, `HealthProfileForm.java`).
* **`security/`**: Cấu hình Spring Security, mã hóa mật khẩu, cấu hình OAuth2.
* **`converter/`**: Chứa các lớp JPA AttributeConverter phục vụ mã hóa tự động ở tầng thực thể.

### 2. Thiết kế Bộ mã hóa thuộc tính JPA (JPA Attribute Converter)

Để mã hóa tự động các trường dữ liệu nhạy cảm mà không cần viết mã hóa thủ công trong từng hàm Service, chúng ta sẽ áp dụng cơ chế chuyển đổi tự động của JPA:

```java
package com.AuraMoon.auramoon.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AesDataEncryptor implements AttributeConverter<String, String> {

    private final String SECRET_KEY = "YourSymmetricAESKeyHere"; // Lưu cấu hình này ở application.properties

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        // Thực hiện mã hóa AES-256 cho chuỗi attribute bằng SECRET_KEY
        return encryptedString;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // Thực hiện giải mã AES-256 cho chuỗi dbData bằng SECRET_KEY
        return decryptedString;
    }
}
```

Tại các Class Entity nhạy cảm, chỉ cần gắn annotation `@Convert`:

```java
@Entity
@Table(name = "PHYSICAL_HEALTH_PROFILE")
@Data
public class PhysicalHealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;

    private Integer userId;

    @Convert(converter = AesDataEncryptor.class)
    private String medicalConditions;

    @Convert(converter = AesDataEncryptor.class)
    private String injuries;
}
```

---

## VII. Kế Hoạch Kiểm Thử Module 1 (Verification Plan)

### 1. Kiểm thử tự động (Automated Testing)

* **Unit Test cho Service:** Sử dụng JUnit 5 và Mockito để kiểm tra logic đăng ký, cập nhật mật khẩu, kiểm tra trùng lặp email.
* **Security Configuration Test:** Viết các bài test giả lập các vai trò người dùng truy cập vào các đường dẫn nhạy cảm để xác minh tính đúng đắn của phân quyền:
  - Guest truy cập `/admin/**` -> Mong đợi lỗi HTTP 403 Forbidden.
  - Chef truy cập `/health-profile` của khách hàng -> Mong đợi lỗi HTTP 403 Forbidden.
  - Đăng nhập sai mật khẩu -> Hệ thống hiển thị thông điệp cảnh báo phù hợp.

### 2. Kiểm thử thủ công (Manual Verification)

* **Kiểm tra Giao diện Consent Box:** Truy cập form điền thông tin sức khỏe của Khách hàng, kiểm tra xem checkbox đồng ý có bị tích chọn sẵn hay không. Thử nhấn nút Lưu khi chưa tích checkbox để đảm bảo hệ thống chặn thành công.
* **Kiểm tra Cơ sở dữ liệu (Database Inspection):** Thực hiện đăng ký tài khoản khách hàng mới, khai báo CCCD và hồ sơ bệnh án. Sau đó vào cơ sở dữ liệu SQL Server chạy lệnh `SELECT * FROM [USER]` và `SELECT * FROM PHYSICAL_HEALTH_PROFILE` để trực tiếp đối chứng xem các trường dữ liệu nhạy cảm đã hiển thị ở dạng mã hóa (chuỗi ký tự nhị phân hoặc Base64 vô nghĩa) hay chưa.
* **Thử nghiệm Xóa dữ liệu (Erasure Test):** Thực hiện quy trình xóa hồ sơ y tế, sau đó truy vấn database để xác minh chắc chắn các dòng dữ liệu liên quan đã bị dọn sạch khỏi bảng hoặc đã được đặt lại thành giá trị rỗng/NULL.
