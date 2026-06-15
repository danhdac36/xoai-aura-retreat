# Mẫu Prompt Khi Làm Việc Với AI (AI Prompt Templates)

Tài liệu này cung cấp các mẫu prompt (khung câu lệnh) chuẩn hóa để các lập trình viên sử dụng khi tương tác với các công cụ AI. Các mẫu này được tùy biến riêng cho dự án **Aura Moon** (sử dụng **Java 21, Spring Boot, Spring Data JPA, Spring Security, Lombok, SQL Server**).

---

## Mẫu 1: Yêu Cầu Tạo Component Mới (Controller/Service/Repository)
Sử dụng khi bạn cần tạo một tính năng mới trong dự án.

```text
Tôi đang phát triển hệ thống quản lý khu nghỉ dưỡng sức khỏe Aura Moon.
Công nghệ đang dùng: Java 21, Spring Boot, Spring Data JPA, Lombok, SQL Server.

Hãy viết code cho chức năng: [Mô tả chi tiết tính năng, ví dụ: Đặt lịch Spa cho khách]

Yêu cầu cụ thể:
1. Tạo Entity, Repository, Service (Interface & Implement), và Controller.
2. Sử dụng Lombok (@Data, @Getter, @Setter, @Builder, @RequiredArgsConstructor).
3. Đảm bảo tuân thủ tiêu chuẩn clean code, xử lý Null-pointer an toàn, sử dụng Java 21 features (như Switch Expressions, Record class cho DTO nếu phù hợp).
4. Viết các câu query JPA tối ưu, tránh lỗi N+1 Query.
5. Thiết lập mã lỗi (Error Codes) và ném Custom Exception thay vì dùng Exception chung.

Ngữ cảnh database/entity liên quan:
[Dán cấu trúc Entity hoặc bảng SQL liên quan vào đây]
```

---

## Mẫu 2: Yêu Cầu Sửa Lỗi (Troubleshooting & Debugging)
Sử dụng khi ứng dụng bị crash hoặc API trả về lỗi 500, lỗi SQL JPA.

```text
Tôi đang gặp lỗi trong dự án Spring Boot (Java 21, Spring Data JPA, SQL Server).

Đoạn code đang bị lỗi:
```java
[Dán đoạn code Java hoặc SQL tại đây]
```

Thông báo lỗi (Stacktrace):
```text
[Dán log lỗi trong Console của IntelliJ hoặc Spring Boot tại đây]
```

Hãy giúp tôi:
1. Giải thích nguyên nhân gốc rễ (Root Cause) gây ra lỗi này.
2. Đưa ra giải pháp sửa đổi code chi tiết.
3. Có lưu ý nào về cấu hình Hibernate/JPA hoặc database cần điều chỉnh để không bị lại lỗi này không?
```

---

## Mẫu 3: Viết Unit Test (Junit 5 & Mockito)
Sử dụng để yêu cầu AI viết các bộ test case cho các Service/Controller.

```text
Tôi cần viết Unit Test cho một Service class trong dự án Spring Boot.
Công nghệ sử dụng: JUnit 5, Mockito.

Hãy viết các test cases bao gồm cả trường hợp thành công (Happy Case) và các trường hợp lỗi (Edge Cases/Failure Cases) cho đoạn code dưới đây.

Yêu cầu test:
- Mock các Repositories/Services phụ thuộc bằng @Mock.
- Sử dụng @InjectMocks cho service cần test.
- Sử dụng Assertions của JUnit 5 để so sánh kết quả.
- Đảm bảo độ bao phủ mã nguồn (Code Coverage) tối đa.

Mã nguồn cần viết Test:
```java
[Dán code của Service/Controller cần viết unit test tại đây]
```
```

---

## Mẫu 4: Yêu Cầu Tối Ưu Hóa & Refactor Code
Sử dụng khi code đã chạy được nhưng trông quá phức tạp, dài dòng hoặc nghi ngờ có vấn đề hiệu năng.

```text
Tôi có đoạn code sau đang chạy trong hệ thống Spring Boot. Tôi muốn tối ưu hóa nó để tăng tốc độ xử lý và cải thiện độ sạch (clean code).

Đoạn code hiện tại:
```java
[Dán code hiện tại vào đây]
```

Yêu cầu tối ưu:
1. Refactor lại để code dễ đọc, tuân thủ nguyên tắc SOLID và DRY (Don't Repeat Yourself).
2. Tối ưu hóa hiệu năng (ví dụ: tối ưu số lượng câu lệnh SQL, tránh truy vấn database trong vòng lặp `for`).
3. Tận dụng các tính năng mới của Java 17/21 (như Stream API, Optional, Record).
4. Giải thích rõ những điểm bạn đã cải tiến và lý do tại sao.
```

---

## Mẫu 5: Thiết Kế Database & Ánh Xạ JPA Entity
Sử dụng khi bắt đầu làm một Module mới cần định nghĩa bảng dữ liệu.

```text
Tôi muốn thiết kế database và tạo các JPA Entity cho thực thể: [Tên thực thể, ví dụ: MealOrder (Đơn đặt món ăn)]

Nghiệp vụ yêu cầu:
- [Ví dụ: Một khách hàng (User) có thể đặt nhiều MealOrder. Mỗi MealOrder chứa nhiều món ăn (MenuItem) với số lượng cụ thể].
- [Ví dụ: Cần lưu thông tin dị ứng từ DietaryProfile của khách để tự động kiểm tra].

Hãy giúp tôi:
1. Viết script SQL Server tạo bảng (DML/DDL), có đầy đủ Primary Key, Foreign Key và các Ràng buộc (Constraints) như NOT NULL, UNIQUE.
2. Tạo các class JPA Entity tương ứng trong Java 21, sử dụng Lombok.
3. Thiết lập chính xác các mối quan hệ `@OneToMany`, `@ManyToOne`, hoặc `@ManyToMany`. Chỉ ra cấu hình FetchType (LAZY/EAGER) và CascadeType phù hợp để tránh rò rỉ bộ nhớ hoặc hiệu năng kém.
```
