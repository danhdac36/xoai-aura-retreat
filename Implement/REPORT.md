# BÁO CÁO THỰC THI & HOÀN THÀNH - PHÂN HỆ F&B (UC16 & UC19)
**Dự án**: Xoai Aura Retreat - Hệ thống quản lý khu nghỉ dưỡng trị liệu & Spa

Tài liệu này lưu trữ thông tin tổng hợp về các yêu cầu, các chỉnh sửa, cấu trúc tệp tin, cách vận hành, kiểm thử và danh sách lỗi đã được xử lý trong quá trình hiện thực hóa **UC16: Khách chọn bữa ăn dinh dưỡng** và tích hợp **UC19: Gọi món ngoài**.

---

## 1. Yêu cầu của người dùng (User Request)
1. Thay vì lọc bỏ hoàn toàn các món ăn chứa chất gây dị ứng khỏi danh sách thực đơn, hệ thống cần **trả về đầy đủ tất cả món ăn**, nhưng đánh dấu món ăn vi phạm dị ứng là **KHÔNG KHẢ DỤNG** (kèm dòng cảnh báo dị ứng cụ thể các nguyên liệu vi phạm, ví dụ: đậu phộng, tôm, hạt điều) đúng như mẫu thiết kế giao diện UI trong tệp `UC16_UC19.png`.
2. Đồng thời tích hợp thêm chức năng **UC19 (Gọi món ngoài)** trong cùng một màn hình thực đơn:
   - Khi chọn tab "Gọi món ngoài" (A-La-Carte), hiển thị đầy đủ giá và tính toán tự động: Tạm tính, Phí phục vụ (5%), Tổng cộng.
   - Ghi nhận đầy đủ doanh thu sau thuế/phí dịch vụ vào hồ sơ hóa đơn `GuestFolio` của khách hàng.

---

## 2. Các tệp tin được chỉnh sửa và tạo mới

### 📁 Tệp tin Tạo mới (New Files)
1. **[MenuItemResponse.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MenuItemResponse.java)**: DTO phản hồi chứa đầy đủ thông tin món ăn, chỉ số dinh dưỡng (Calo, Protein, Carbs, Fats, Fiber), cờ hiệu khả dụng cho khách hàng (`isAvailableForGuest`), cờ khuyến dùng (`isRecommended`), và thông báo cảnh báo dị ứng (`warningMessage`).
2. **[SWP391-FNB-IMP-UC16.md](file:///d:/SWP301/su26-swp391-se2023-g6/Implement/SWP391-FNB-IMP-UC16.md)**: Tài liệu đặc tả kỹ thuật của module F&B.
3. **[MealSelectionController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionController.java)**: REST API Endpoint phục vụ Client.
4. **[MealSelectionMvcController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionMvcController.java)**: Spring MVC Controller điều hướng giao diện Web Thymeleaf và nạp dữ liệu demo mẫu.
5. **[MealSelectionService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealSelectionService.java)**: Chứa toàn bộ nghiệp vụ lọc món ăn và ghi hóa đơn.
6. **[MealSelectionResponse.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealSelectionResponse.java)**: DTO phản hồi trạng thái giao dịch đặt bữa ăn.
7. **[UserRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/auth/repository/UserRepository.java)**: Kết nối CSDL bảng `[USER]` của module Auth.
8. **[BookingRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/BookingRepository.java)**: Kết nối CSDL bảng `BOOKING` của module Booking.
9. **[GuestFolioRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/billing/repository/GuestFolioRepository.java)**: Kết nối CSDL bảng `GUEST_FOLIO` của module Billing.
10. **[index.html](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/selection/index.html)**: Giao diện trang chủ Thymeleaf mô phỏng cổng thông tin khách hàng.
11. **[menu.html](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/selection/menu.html)**: Giao diện trang thực đơn và biểu mẫu đặt món tương tác Thymeleaf.
12. **[MealSelectionServiceTest.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/test/java/com/AuraMoon/auramoon/fnb/service/MealSelectionServiceTest.java)**: Bộ Unit Test Mockito kiểm thử tự động toàn bộ logic UC16 & UC19.
13. **Cấu trúc thư mục Resources mới**: Tạo các thư mục phục vụ lưu trữ file CSS, hình ảnh, JavaScript và giao diện Thymeleaf cho các phân hệ của resort bao gồm `auth`, `billing`, `booking`, `fnb`, `home`, `layout` và `spa` dưới thư mục `resources/static` và `resources/templates`.

### 🛠️ Tệp tin Chỉnh sửa (Modified Files)
Dưới đây là các tệp tin hiện hữu trong dự án đã được điều chỉnh bổ sung:
- **[User.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/auth/entity/User.java)**: Loại bỏ kế thừa `BaseEntity` và định nghĩa thủ công các thuộc tính `createdAt` / `updatedAt` nhằm tránh lỗi JPA schema validation khi CSDL gốc thiếu cột `is_delete` / `created_at`.
- **[MenuItem.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/entity/MenuItem.java)**: Nâng thuộc tính độ dài cột `item_name` lên 100 để hỗ trợ tên món ăn dài theo đúng thực tế thực đơn.
- **[MealSelectionController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionController.java)**: Cập nhật kiểu trả về thành danh sách chứa thông số dinh dưỡng `MenuItemResponse`.
- **[MealSelectionMvcController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionMvcController.java)**: Viết lại cơ chế Seeder tự động dữ liệu mẫu qua native query SQL Server với `SET IDENTITY_INSERT` để giữ nguyên các ràng buộc CSDL gốc.
- **[MealSelectionService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealSelectionService.java)**: Bổ sung logic làm giàu dinh dưỡng, xác thực dị ứng đa ngữ (Anh-Việt) và tự động tính 5% phí phục vụ cho đơn hàng gọi thêm ngoài (UC19).
- **[menu.html](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/selection/menu.html)**: Thiết kế lại toàn bộ giao diện dựa trên khung HTML Tailwind CSS sang trọng được cung cấp từ bản thiết kế mockup, xử lý tính toán số tiền real-time bằng JS.
- **[MealSelectionServiceTest.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/test/java/com/AuraMoon/auramoon/fnb/service/MealSelectionServiceTest.java)**: Viết thêm các bộ Unit Test bổ sung để xác nhận độ chính xác của logic dị ứng và hóa đơn.
- **Các tệp tin Repository & DTO liên quan**: Đồng bộ hóa kiểu dữ liệu khóa chính về `Integer` để khớp tuyệt đối với SQL Server.

---

## 3. Chức năng chi tiết từng tệp tin

| Tên Tệp Tin | Chức năng / Trách nhiệm chính |
| :--- | :--- |
| `MenuItemResponse` | DTO đóng gói thông tin chi tiết của món ăn gửi về cho Client, bao gồm cờ hiệu dị ứng, cờ khuyên dùng, và chỉ số dinh dưỡng. |
| `MealSelectionController` | Tiếp nhận và xử lý các RESTful request dạng JSON cho ứng dụng Client/Mobile App. |
| `MealSelectionMvcController` | Renders các view giao diện Thymeleaf Web (`/fnb/selection`), nạp dữ liệu demo mẫu để chạy thử nghiệm. |
| `MealSelectionService` | Phân tích dị ứng (peanut/tôm/hạt điều), lập cờ dị ứng/khuyên dùng chay và tự động tính toán tổng hóa đơn F&B (gồm 5% phí phục vụ cho đơn gọi thêm) ghi nợ vào Folio. |
| `MealSelectionRequest` | Chứa dữ liệu đầu vào khi khách gửi đơn đặt bữa ăn. |
| `MealSelectionResponse` | Chứa dữ liệu phản hồi, bao gồm chi tiết vi phạm nguyên liệu nếu gặp lỗi `ALLERGY_VIOLATION`. |
| `index.html` | Trang mô phỏng đăng nhập và nút bấm một chạm tự động cấu hình nạp nhanh dữ liệu demo. |
| `menu.html` | Trang thực đơn tương tác hiển thị chỉ số dinh dưỡng, nhãn khuyên dùng, vô hiệu hóa các món dị ứng, hiển thị cảnh báo dị ứng và tính tiền hóa đơn real-time. |
| `MealSelectionServiceTest` | Lớp kiểm thử tự động, giả lập (mock) các tầng CSDL để xác minh thuật toán lọc và tính toán phí phục vụ. |

---

## 4. Cách chạy dự án (How to Run)
1. **Yêu cầu hệ thống**:
   - Java Development Kit (JDK) phiên bản 21.
   - Apache Maven phiên bản 3.8+.
   - CSDL Microsoft SQL Server đang chạy ở cổng mặc định 1433 với tài khoản `sa` / mật khẩu `123` và CSDL tên là `HoS`.
2. **Khởi chạy ứng dụng**:
   Mở terminal tại thư mục `d:\SWP301\su26-swp391-se2023-g6\auramoon` và chạy lệnh sau:
   ```bash
   mvn spring-boot:run
   ```

---

## 5. Cách kiểm thử (How to Test)

### 🧪 Cách 1: Kiểm thử Giao diện Trực quan (Manual Web Test)
1. Mở trình duyệt Web và truy cập URL: **`http://localhost:8080/fnb/selection`**
2. Hệ thống sẽ tự động kiểm tra CSDL và chạy trình tự động khởi tạo dữ liệu mẫu nếu cần thiết, rồi tải trực tiếp giao diện menu cá nhân hóa 5 bước của khách hàng **Minh** (Guest ID = 1) mà không yêu cầu nhập mã số khách hàng:
   - Giao diện có tiêu đề thanh lịch **"Thực đơn cá nhân của bạn"** và dòng chào mừng **"Chào mừng bạn trở lại, Minh..."**.
   - Các món ăn chay an toàn có nhãn nổi bật **"KHUYÊN DÙNG"** màu vàng/xanh lục nhạt.
   - Món ăn vi phạm dị ứng (**Tôm Nướng Muối Hạt & Hạt Điều**) bị gán cờ dị ứng màu đỏ **"⚠️ Cảnh báo dị ứng"**, làm mờ và vô hiệu hóa nút bấm thành **"Không khả dụng"**.
3. **Thực đơn đã chọn (Sidebar bên phải)**:
   - Click chọn món ăn bất kỳ (ví dụ: **Cá Hồi Nướng Hương Thảo**). Món ăn sẽ được thêm động vào danh sách bên phải.
   - Hệ thống tự động tính toán **Tạm tính**, **Phí phục vụ (5%)**, và **Tổng cộng** thay đổi real-time.
4. **Gọi món ngoài (A-La-Carte)**:
   - Chuyển đổi tab ở góc trên bên phải sang **"Gọi món ngoài"** để chuyển sang luồng UC19.
5. Nhấn **"Xác nhận đặt bàn"** để lưu đơn hàng vào CSDL. Hệ thống sẽ hiển thị thông báo thành công màu xanh tươi mát ở đầu trang.

### ⚙️ Cách 2: Chạy bộ kiểm thử tự động (Automated Test)
Chạy lệnh kiểm thử tự động độc lập qua Maven để xác nhận tính chính xác của thuật toán:
```bash
mvn clean compile test
```
*Hệ thống sẽ thực hiện biên dịch sạch và chạy 7 ca kiểm thử. Kết quả kỳ vọng: `BUILD SUCCESS` (0 thất bại, 0 lỗi).*

---

## 6. Những lỗi đã sửa (Bugs Fixed)
* **Sửa lại cơ chế đăng nhập / Portal**: Loại bỏ hoàn toàn màn hình nhập mã số khách hàng, trực tiếp nạp thực đơn 5 bước cá nhân hóa cho Minh khi truy cập `/fnb/selection`.
* **Khắc phục lỗi Seeding với IDENTITY_INSERT**: Viết lại cơ chế Seeder tự động sử dụng truy vấn SQL Native và lệnh `SET IDENTITY_INSERT` để chèn dữ liệu demo cố định chính xác các khóa chính (`Integer`) mà không bị lỗi detached entity JPA từ Hibernate.
* **Sửa lỗi Truncation của SQL Server**: Khắc phục lỗi độ dài `item_name` trong bảng `MENU_ITEM` (độ dài gốc chỉ là 20 ký tự) bằng cách nâng cột lên `NVARCHAR(100)` và chạy lệnh biên dịch lại `sp_recompile` trong SQL Server.
* **Sửa lỗi thiếu cột audit `created_at`**: Bổ sung cột `created_at` cho các bảng thừa kế từ `BaseEntity` (`BOOKING`, `RETREAT_PACKAGE`, `GUEST_FOLIO`, `DIETARY_PROFILE`, v.v.) bị thiếu trong CSDL gốc.
* **Tích hợp giao diện Tailwind CSS cao cấp**: Tích hợp mã giao diện Tailwind CSS cao cấp theo đúng chuẩn mockup, chạy mượt mà trên nền Thymeleaf của Spring Boot.

---

## 7. Những phần chưa hoàn thiện
* **Không có**: Tính năng UC16 và UC19 đã được hoàn thiện 100% cả về mặt chức năng, CSDL, seeder, kiểm thử tự động và giao diện người dùng.
