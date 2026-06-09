# BÁO CÁO THỰC THI & HOÀN THÀNH - PHÂN HỆ F&B (UC16)
**Dự án**: Xoai Aura Retreat - Hệ thống quản lý khu nghỉ dưỡng trị liệu & Spa

Tài liệu này lưu trữ thông tin tổng hợp về các yêu cầu, các chỉnh sửa, cấu trúc tệp tin, cách vận hành, kiểm thử và danh sách lỗi đã được xử lý trong quá trình hiện thực hóa **UC16: Khách hàng chọn trước bữa ăn hàng ngày từ thực đơn đã lọc tự động**.

---

## 1. Yêu cầu của người dùng (User Request)
1. Chuyển đổi 3 tài liệu đặc tả định dạng PDF mẫu (`TDD_TEMPLATE_V1.pdf`, `EDS_TEMPLATE_V2.0.pdf`, `SWP391-HOS-03.pdf`) sang định dạng Markdown (`.md`) trong thư mục `Template`.
2. Lập báo cáo kế hoạch thực thi và kiểm thử cho **UC16** theo cấu trúc chuẩn đặc tả kỹ thuật `EDS_TEMPLATE_V2.0` và lưu vào thư mục `Implement`.
3. Hiện thực hóa mã nguồn (code) cho chức năng **UC16** (Module F&B) viết bằng Spring Boot theo mô hình chuẩn MVC (Model-View-Controller) cho phép:
   - Tự động lọc thực đơn món ăn phù hợp với hồ sơ dị ứng thực phẩm và sở thích chay của khách.
   - Cho phép khách chọn trước bữa ăn hàng ngày (theo ngày, bữa sáng/trưa/tối).
   - Tự động kiểm tra chéo dị ứng ở phía Backend và ghi nhận giao dịch hóa đơn vào Folio của khách hàng.

---

## 2. Các tệp tin được chỉnh sửa và tạo mới

### 📁 Tệp tin Tạo mới (New Files)
1. **[SWP391-FNB-IMP-UC16.md](file:///d:/SWP301/su26-swp391-se2023-g6/Implement/SWP391-FNB-IMP-UC16.md)**: Đặc tả kỹ thuật chuẩn EDS v2.0 của UC16 (lưu tại thư mục `Implement`).
2. **[MealSelectionController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionController.java)**: REST API Endpoint phục vụ gọi dữ liệu từ Client.
3. **[MealSelectionMvcController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealSelectionMvcController.java)**: Spring MVC Controller điều hướng giao diện Web Thymeleaf và nạp dữ liệu demo mẫu.
4. **[MealSelectionService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealSelectionService.java)**: Chứa toàn bộ nghiệp vụ lọc món ăn và ghi hóa đơn.
5. **[MealSelectionResponse.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealSelectionResponse.java)**: DTO phản hồi trạng thái giao dịch đặt bữa ăn.
6. **[UserRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/auth/repository/UserRepository.java)**: Kết nối CSDL bảng `[USER]` của module Auth.
7. **[BookingRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/BookingRepository.java)**: Kết nối CSDL bảng `BOOKING` của module Booking.
8. **[GuestFolioRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/billing/repository/GuestFolioRepository.java)**: Kết nối CSDL bảng `GUEST_FOLIO` của module Billing.
9. **[index.html](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/selection/index.html)**: Giao diện trang chủ Thymeleaf mô phỏng cổng thông tin khách hàng.
10. **[menu.html](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/selection/menu.html)**: Giao diện trang thực đơn và biểu mẫu đặt món tương tác Thymeleaf.
11. **[MealSelectionServiceTest.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/test/java/com/AuraMoon/auramoon/fnb/service/MealSelectionServiceTest.java)**: Bộ Unit Test Mockito kiểm thử tự động toàn bộ logic UC16.

### 🛠️ Tệp tin Chỉnh sửa (Modified Files)
1. **[DietaryProfileRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/DietaryProfileRepository.java)**: Đổi kiểu ID sang `Integer`, thêm phương thức `findByUserId`.
2. **[MealOrderRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/MealOrderRepository.java)**: Chuyển đổi từ `class` rỗng thành `interface extends JpaRepository<MealOrder, Integer>`.
3. **[MealOrderItemRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/MealOrderItemRepository.java)**: Đổi tên interface từ khai báo sai `MealOrderRepository` thành `MealOrderItemRepository` và chỉnh kiểu ID thành `Integer`.
4. **[MenuItemRepository.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/repository/MenuItemRepository.java)**: Đồng bộ kiểu ID từ `Long` sang `Integer`.
5. **[MenuService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MenuService.java)**: Sửa tham số tìm kiếm món ăn từ `Long` sang `Integer`.
6. **[MenuController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MenuController.java)**: Sửa `@PathVariable Long id` sang `Integer id`.
7. **[MealOrderService.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/service/MealOrderService.java)** & **[MealOrderController.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/controller/MealOrderController.java)**: Đồng bộ kiểu tham số ID từ `Long` sang `Integer`.
8. **[MealOrderRequest.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealOrderRequest.java)** & **[MealSelectionRequest.java](file:///d:/SWP301/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/fnb/dto/MealSelectionRequest.java)**: Đồng bộ kiểu trường dữ liệu từ `Long` sang `Integer`.

---

## 3. Chức năng chi tiết từng tệp tin

| Tên Tệp Tin | Chức năng / Trách nhiệm chính |
| :--- | :--- |
| `MealSelectionController` | Tiếp nhận và xử lý RESTful request dạng JSON cho ứng dụng Client/Mobile App. |
| `MealSelectionMvcController` | Renders các view giao diện Thymeleaf Web (`/fnb/selection`), nạp dữ liệu mẫu chạy thử nghiệm. |
| `MealSelectionService` | Lọc thực đơn dựa trên thuật toán so khớp từ khóa dị ứng/sở thích ăn uống và tính toán công nợ hóa đơn F&B ghi vào Folio. |
| `MealSelectionRequest` | Chứa dữ liệu đầu vào khi khách gửi đơn đặt bữa ăn. |
| `MealSelectionResponse` | Chứa dữ liệu phản hồi, bao gồm chi tiết vi phạm nguyên liệu nếu gặp lỗi `ALLERGY_VIOLATION`. |
| `DietaryProfileRepository` | Cung cấp phương thức tìm kiếm hồ sơ ăn uống của khách theo ID tài khoản (`userId`). |
| `MealOrderRepository` | Cung cấp các thao tác CRUD dữ liệu bảng `MEAL_ORDER` trong CSDL. |
| `MealOrderItemRepository` | Cung cấp các thao tác CRUD dữ liệu bảng `MEAL_ORDER_ITEM` trong CSDL. |
| `UserRepository` | Cung cấp truy xuất thông tin khách hàng từ bảng `[USER]` của module Auth. |
| `BookingRepository` | Cung cấp truy xuất thông tin đặt phòng/liệu trình từ bảng `BOOKING`. |
| `GuestFolioRepository` | Truy xuất và cập nhật số tiền chi tiêu F&B thực tế vào folio thanh toán của khách hàng. |
| `index.html` | Trang mô phỏng đăng nhập và nút bấm một chạm tự động cấu hình nạp nhanh dữ liệu demo. |
| `menu.html` | Trang hiển thị thực đơn đã lọc an toàn, cho phép khách tick chọn món tương tác trực quan. |
| `MealSelectionServiceTest` | Lớp kiểm thử tự động, giả lập (mock) các tầng CSDL để xác minh thuật toán lọc và bảo vệ dị ứng. |

---

## 4. Cách chạy dự án (How to Run)
1. **Yêu cầu hệ thống**:
   - Java Development Kit (JDK) phiên bản 21.
   - Apache Maven phiên bản 3.8+.
   - CSDL Microsoft SQL Server đang chạy ở cổng mặc định 1433 với tài khoản `sa` / mật khẩu `123` và CSDL tên là `HoS` (như cấu hình trong `application.properties`).
2. **Khởi chạy ứng dụng**:
   Mở terminal tại thư mục `d:\SWP301\su26-swp391-se2023-g6\auramoon` và chạy lệnh sau:
   ```bash
   mvn spring-boot:run
   ```

---

## 5. Cách kiểm thử (How to Test)

### 🧪 Cách 1: Kiểm thử Giao diện Trực quan (Manual Web Test)
1. Mở trình duyệt Web và truy cập URL: **`http://localhost:8080/fnb/selection`**
2. Nhấn nút **`Cài Đặt Dữ Liệu Demo Nhanh`** để hệ thống tự động khởi tạo dữ liệu cần thiết.
3. Nhập mã số khách hàng là **`1`** và nhấn **`Xem Thực Đơn Đã Lọc`**. Giao diện sẽ hiển thị:
   - Thẻ thông tin khách hàng Nguyễn Văn A, có thông báo dị ứng: Đậu phộng, Tôm; sở thích: Ăn chay.
   - Thực đơn lọc tự động: Các món chứa thịt bò, tôm, đậu phộng đều đã bị loại bỏ một cách an toàn.
4. Chọn một số món và nhấn **`Xác Nhận Đặt Bữa Ăn`** để gửi yêu cầu đặt món và hoàn tất.

### ⚙️ Cách 2: Chạy bộ kiểm thử tự động (Automated Test)
Chạy lệnh kiểm thử tự động độc lập qua Maven để xác nhận tính chính xác của thuật toán:
```bash
mvn clean compile test
```
*Hệ thống sẽ thực hiện biên dịch sạch và chạy 6 ca kiểm thử. Kết quả kỳ vọng: `BUILD SUCCESS` (0 thất bại, 0 lỗi).*

---

## 6. Những lỗi đã sửa (Bugs Fixed)
* **Lỗi biên dịch trùng lặp lớp `MealOrderRepository`**: Tệp tin `MealOrderItemRepository.java` khai báo sai tên interface thành `MealOrderRepository` dẫn đến xung đột với tệp `MealOrderRepository.java` chính gốc. Agent đã đổi tên interface trong `MealOrderItemRepository.java` về đúng chuẩn.
* **Lỗi sai lệch kiểu dữ liệu ID**: Cấu trúc database SQL Server thiết lập các khóa chính dưới dạng cột số nguyên `INT IDENTITY`. Tuy nhiên, các interface Repository ban đầu khai báo JpaRepository với khóa chính kiểu `Long`. Agent đã sửa toàn bộ kiểu dữ liệu khóa chính của các Repository này thành `Integer` để tránh lỗi kiểu dữ liệu khi truy vấn.
* **Đồng bộ kiểu tham số DTO và Service**: Do các ID bảng là `Integer`, các biến ID trong Request DTO, Service, và Controller trước đó mang kiểu `Long` đều đã được Agent đồng bộ lại thành `Integer` để mã nguồn khớp hoàn toàn.

---

## 7. Những phần chưa hoàn thiện
* **Không có**: Tính năng UC16 đã được hoàn thiện 100% về cả REST API, mô hình MVC Thymeleaf Web, cơ chế Seeder hỗ trợ kiểm thử một chạm, và bộ kiểm thử tự động Unit Test Mockito.
