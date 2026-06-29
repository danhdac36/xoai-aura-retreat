# Giải pháp thiết kế Nghiệp vụ Lịch trình Retreat Package

## Bối cảnh (Context)
Dự án yêu cầu hiển thị thông tin chi tiết lịch trình theo từng ngày (Ngày 1 làm gì, Ngày 2 có hoạt động gì...) cho một Gói nghỉ dưỡng (Retreat Package). 
Ràng buộc đưa ra: 
1. Không được phép thay đổi cấu trúc Database hiện tại (`DB.sql`).
2. Không sử dụng định dạng dữ liệu JSON thuần túy dưới Database.

## Quyết định Thiết kế (Design Decision)
Sử dụng cột `description NVARCHAR(MAX)` có sẵn trong bảng `RETREAT_PACKAGE` để lưu trữ **toàn bộ văn bản** của lịch trình. Kết hợp với việc quy ước một chuỗi phân cách (Delimiter) đặc biệt để tầng Service (Java) có thể bóc tách (parse) chuỗi dài này thành cấu trúc phân tầng (Mô tả chung và Lịch trình từng ngày).

Chuỗi quy ước được thống nhất sử dụng là: `[DAY]`

## Hướng dẫn Hiện thực hóa (Implementation Guide)

### 1. Quy ước nhập liệu (Input Format) cho Admin
Admin sẽ nhập toàn bộ nội dung vào một ô Textarea duy nhất (cột `description`) theo định dạng sau:
```text
Tổng quan: Gói nghỉ dưỡng chữa lành tâm hồn 3 ngày 2 đêm, tập trung vào thanh lọc cơ thể.
[DAY]
Ngày 1: Nhận phòng lúc 14h. Nghỉ ngơi tự do tại Villa. Thưởng thức trà chiều và ngâm chân thảo mộc.
[DAY]
Ngày 2: 6h sáng tập Yoga Đón Bình Minh. 9h sáng sử dụng dịch vụ Massage trị liệu toàn thân.
[DAY]
Ngày 3: Ăn sáng Detox. Trả phòng lúc 12h và nhận quà lưu niệm.
```

### 2. Xử lý Logic tại Backend (Spring Boot)
Tạo một DTO trung gian để chứa dữ liệu đã được bóc tách:
```java
public class RetreatItineraryDto {
    private String generalDescription;    // Chứa đoạn văn mô tả tổng quan
    private List<String> dailyActivities; // Chứa danh sách hoạt động từng ngày

    // getters, setters...
}
```

Viết hàm phụ trợ (Utility/Service) để tách chuỗi bằng Java String Regex:
```java
public RetreatItineraryDto parseDescription(String rawDescription) {
    RetreatItineraryDto dto = new RetreatItineraryDto();
    
    // Xử lý an toàn nếu description bị Null
    if (rawDescription == null || rawDescription.isEmpty()) {
        dto.setGeneralDescription("");
        dto.setDailyActivities(new ArrayList<>());
        return dto;
    }

    // Tách chuỗi bằng regex, thoát (escape) các ký hiệu ngoặc vuông
    String[] parts = rawDescription.split("\\[DAY\\]");
    
    // Phần đầu tiên (trước chữ [DAY] đầu tiên) luôn là mô tả chung
    dto.setGeneralDescription(parts[0].trim());

    // Các phần từ index 1 trở đi tương ứng với từng ngày
    List<String> activities = new ArrayList<>();
    for (int i = 1; i < parts.length; i++) {
        if (!parts[i].trim().isEmpty()) {
            activities.add(parts[i].trim());
        }
    }
    
    dto.setDailyActivities(activities);
    return dto;
}
```

### 3. Đẩy dữ liệu ra View (Thymeleaf MVC)
Tầng Controller gọi hàm parse ở trên và đẩy đối tượng DTO xuống View (`Model`). Tầng View sẽ sử dụng vòng lặp `th:each` để render giao diện dạng Timeline (Dòng thời gian):

```html
<!-- 1. Render đoạn mô tả tổng quan -->
<div class="package-summary">
    <p th:text="${itinerary.generalDescription}"></p>
</div>

<!-- 2. Render danh sách các ngày -->
<div class="package-timeline mt-5">
    <h3>Lịch trình chi tiết</h3>
    
    <!-- Vòng lặp duyệt qua List activities -->
    <div class="day-card" th:each="activity, iterStat : ${itinerary.dailyActivities}">
        <!-- Dùng iterStat.count để in ra tự động: Ngày 1, Ngày 2... -->
        <h4 th:text="'Ngày ' + ${iterStat.count}"></h4> 
        <p th:text="${activity}"></p>
    </div>
</div>
```

---
*Ghi chú: Tài liệu này lưu trữ ý tưởng xử lý chuỗi động để đáp ứng bài toán nghiệp vụ linh hoạt mà không vi phạm cấu trúc CSDL lõi của hệ thống.*
