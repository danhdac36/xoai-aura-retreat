# BÁO CÁO PHÂN TÍCH KIẾN TRÚC MVC & LUỒNG HOẠT ĐỘNG MODULE 2
**Tên phân hệ:** Đặt Gói Nghỉ Dưỡng Trị Liệu & Quản Lý Lưu Trú (Retreat Package & Accommodation Booking)  
**Tài liệu tham chiếu:** [TDD_Module_2.md](file:///d:/su26-swp391-se2023-g6/07_Reports/TDD_Module_2.md) / [Insert_Mock_Data.sql](file:///d:/su26-swp391-se2023-g6/03_Design/Insert_Mock_Data.sql) / [DB.sql](file:///d:/su26-swp391-se2023-g6/03_Design/DB.sql)

---

## 1. TỔNG QUAN VỀ MODULE 2
Module 2 xử lý toàn bộ luồng nghiệp vụ cốt lõi về lưu trú và trải nghiệm của khách hàng tại **Xoai Aura Retreat**:
*   **Phạm vi:** Từ lúc khách tìm kiếm gói trị liệu trực tuyến $\rightarrow$ thực hiện đặt phòng $\rightarrow$ thanh toán đặt cọc qua VNPay $\rightarrow$ lễ tân làm thủ tục nhận phòng (Check-in), gán số phòng vật lý $\rightarrow$ quản lý sơ đồ và trạng thái villa $\rightarrow$ tự động hiển thị lộ trình chi tiết (Itinerary) từng ngày cho khách hàng.
*   **Mục tiêu tuân thủ pháp lý:**
    *   **Khai báo tạm trú (Luật Cư trú 2020):** Thu thập số định danh cá nhân (CCCD/Passport) khi khách check-in.
    *   **Bảo vệ dữ liệu cá nhân (Nghị định 356/2025/NĐ-CP):** Mã hóa đối xứng AES-256 đối với các thông tin nhạy cảm định danh (Sensitive-PII) trước khi ghi xuống DB.

---

## 2. CHI TIẾT KIẾN TRÚC MVC TRONG MÃ NGUỒN

Hệ thống được thiết kế theo đúng mô hình 3 lớp của Spring MVC:

### 2.1. Lớp Model (Quản lý Dữ liệu & Xử lý Nghiệp vụ)
*   **Thực thể dữ liệu (JPA Entities):** 
    *   [Booking.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/Booking.java): Lưu thông tin đơn đặt phòng, ngày check-in/out, số khách, trạng thái đặt phòng (`PENDING`, `CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`), trạng thái thanh toán.
    *   [Villa.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/Villa.java): Lưu mã phòng vật lý (`GV01`, `PR01`), loại phòng, trạng thái phòng (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`) và trạng thái dọn dẹp (`CLEAN`, `DIRTY`, `CLEANING`).
    *   [RetreatPackage.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/RetreatPackage.java): Lưu danh mục gói nghỉ dưỡng, thời lượng lưu trú và đơn giá.
    *   [User.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/auth/entity/User.java): Lưu thông tin khách hàng (liên kết qua `guest_id`).
*   **Đối tượng chuyển dữ liệu (DTOs):**
    *   `BookingRequestDTO` & `BookingResponseDTO`: Đóng gói dữ liệu khi gửi yêu cầu đặt phòng.
    *   `CheckInRequestDTO`: Đóng gói dữ liệu form Check-in gồm `bookingId`, `villaId`, và `identifyCode` (CCCD).
    *   `ItineraryTimelineDTO`: Đóng gói trục thời gian lịch trình của khách hàng theo từng ngày.
*   **Tầng xử lý dữ liệu (Repositories & Services):**
    *   Các Repository JPA xử lý tương tác SQL Server.
    *   [BookingServiceImpl.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/BookingServiceImpl.java): Xử lý tạo booking và xác nhận thanh toán cọc.
    *   [CheckInServiceImpl.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/CheckInServiceImpl.java): Kiểm tra sức chứa villa, mã hóa CCCD qua `EncryptionService` và lưu thông tin.
    *   [ItineraryServiceImpl.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/ItineraryServiceImpl.java): Tổng hợp và sinh lịch trình động theo ngày.

### 2.2. Lớp Controller (Điều hướng & Tiếp nhận yêu cầu)
*   [CheckInController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/CheckInController.java):
    *   `GET /reception/bookings`: Truy vấn danh sách booking và các villa trống, gán vào Model để hiển thị lên view.
    *   `POST /reception/checkin`: Nhận dữ liệu Check-in từ form gửi lên, gọi `CheckInService` để lưu thông tin và thực hiện `redirect` về trang danh sách kèm thông báo.
*   [VillaStatusController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/VillaStatusController.java):
    *   `GET /reception/villas`: Truy vấn toàn bộ sơ đồ Villa vật lý đưa qua View.
    *   `POST /reception/villas/{id}/status`: Nhận yêu cầu thay đổi trạng thái buồng phòng từ Housekeeping/Lễ tân và cập nhật.
*   [ItineraryController.java](file:///d:/su26-swp391-se2023-g6/05_Development/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/ItineraryController.java):
    *   `GET /itinerary`: Kiểm tra thông tin người dùng hiện tại từ HTTP Session, gọi `ItineraryService` để lấy dữ liệu timeline và trả về trang lịch trình cá nhân của khách.

### 2.3. Lớp View (Giao diện người dùng)
Được viết bằng Thymeleaf HTML kết hợp CSS/JS:
*   `reception/bookings.html`: Hiển thị danh sách khách chuẩn bị đến, chứa Form Modal để lễ tân điền thông tin check-in nhanh.
*   `reception/villas.html`: Hiển thị sơ đồ biệt thự trực quan dưới dạng các ô trạng thái (Phòng trống, Phòng có khách, Phòng bảo trì).
*   `guest/itinerary.html`: Trục thời gian (Timeline) hiển thị sinh động toàn bộ lịch trình sinh hoạt, trị liệu của khách hàng.

---

## 3. LUỒNG ĐI ĐIỂN HÌNH CỦA DỮ LIỆU TRONG MVC (LIFE CYCLE)

### Luồng Đặt Gói Trị Liệu Trực Tuyến & Cọc (Online Booking Flow)
1.  **Guest** duyệt danh sách gói nghỉ dưỡng (`RetreatPackageController` $\rightarrow$ `guest/packages.html`).
2.  **Guest** nhấn đặt gói, chọn ngày và hạng biệt thự. Yêu cầu gửi đến `BookingController.createBooking()`.
3.  `BookingServiceImpl` gọi `VillaService` kiểm tra xem hạng phòng đó còn biệt thự vật lý trống hay không.
4.  Nếu còn phòng, tạo bản ghi `Booking` với trạng thái `PENDING`, `UNPAID` và tạo `GuestFolio` ở trạng thái `PENDING`. Đồng thời trả về link redirect sang cổng VNPay.
5.  Khách hàng thanh toán. **VNPay** gửi callback về `confirmPayment()`.
6.  Hệ thống chuyển trạng thái Booking thành `CONFIRMED` và `paymentStatus` thành `DEPOSITED` (Đặt cọc thành công).

### Luồng Làm Thủ Tục Check-in Tại Quầy (Check-in Flow)
1.  **Receptionist** xem danh sách booking trong ngày tại `/reception/bookings`. Giao diện nạp dữ liệu từ `CheckInController` $\rightarrow$ `reception/bookings.html`.
2.  Khách hàng đến quầy, xuất trình căn cước. Lễ tân nhấn **"Check-in"**, hệ thống mở Modal nhập thông tin.
3.  Lễ tân nhập số CCCD, chọn phòng vật lý cụ thể (ví dụ: `PR01`) $\rightarrow$ nhấn **"Xác nhận"** gửi yêu cầu `POST /reception/checkin`.
4.  `CheckInServiceImpl` thực hiện:
    *   Kiểm tra sức chứa của Villa vật lý (`limitPerson`) xem có nhỏ hơn số khách đặt phòng (`totalGuests`) hay không. Nếu nhỏ hơn, chặn lại và báo lỗi.
    *   Gọi `EncryptionService.encrypt()` mã hóa CCCD bằng thuật toán AES-256.
    *   Set CCCD đã mã hóa vào thông tin khách hàng, cập nhật Booking thành `CHECKED_IN`, gán ID phòng vật lý vào đơn đặt.
    *   Cập nhật trạng thái Villa vật lý đó thành `OCCUPIED` (Có khách lưu trú) và `CLEANED`.
    *   Ghi Audit Log kiểm toán sự kiện check-in.
5.  Sau khi Service thực thi xong, Controller thực hiện `redirect` trình duyệt tải lại trang danh sách `/reception/bookings` với dữ liệu mới đã được cập nhật động.

---

## 4. CÁC RÀNG BUỘC NGHIỆP VỤ ĐẠT ĐƯỢC (BUSINESS RULES)
*   **Chặn gán sai sức chứa:** Không cho phép xếp khách vào Villa có sức chứa nhỏ hơn số người đăng ký lưu trú.
*   **Bảo mật thông tin nhạy cảm:** Bắt buộc mã hóa một chiều/hai chiều số CCCD của khách tại tầng Backend trước khi persist vào database. Nhân viên không có quyền hạn cao sẽ không thể nhìn thấy chuỗi thô.
*   **Chặn đặt trùng phòng:** Khi Villa đã ở trạng thái `OCCUPIED` hoặc `MAINTENANCE` thì sẽ tự động bị loại khỏi danh sách gợi ý phòng trống khả dụng để gán cho các booking tiếp theo.
*   **Tránh Folio rác:** Chỉ tạo hoặc kích hoạt các nghiệp vụ hóa đơn phòng khi có xác nhận thanh toán đặt cọc thành công từ VNPay.

---

## 5. ĐÁNH GIÁ CHẤT LƯỢNG VÀ KIỂM THỬ (TESTING & DoD)
*   Độ bao phủ của Module 2 đã được đảm bảo thông qua 21/21 Unit Tests trong các file `BookingServiceTest`, `CheckInServiceTest`, `ReviewServiceTest`, `ItineraryServiceImplTest` và `RetreatPackageServiceImplTest`.
*   Tất cả các ca kiểm thử đều chạy thành công (`BUILD SUCCESS`).
