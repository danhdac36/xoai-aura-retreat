# PHÂN TÍCH YÊU CẦU CHI TIẾT - MODULE 2
## RETREAT PACKAGE & ACCOMMODATION BOOKING

---

## I. Tổng quan Module 2
Module 2 tập trung vào luồng nghiệp vụ cốt lõi về đặt gói nghỉ dưỡng trị liệu và quản lý lưu trú tại khu nghỉ dưỡng **Xoai Aura Retreat**. 

*   **Phạm vi nghiệp vụ:** Từ lúc khách hàng tìm kiếm gói trị liệu trực tuyến, thực hiện đặt chỗ, thanh toán đặt cọc qua cổng VNPay, cho đến khi lễ tân làm thủ tục nhận phòng (Check-in), gán số phòng vật lý, quản lý trạng thái dọn dẹp phòng, và hiển thị lộ trình chi tiết (Itinerary) của khách hàng.
*   **Mục tiêu tuân thủ pháp luật:** Thu thập thông tin CCCD/Passport khi khách làm thủ tục Check-in nhằm khai báo tạm trú theo **Luật Cư trú 2020**, đồng thời áp dụng mã hóa dữ liệu nhạy cảm này khi lưu trữ để bảo mật thông tin cá nhân.

---

## II. Danh sách Use Cases (UC06 - UC10)

### 1. UC06: Browse Wellness Packages (Duyệt tìm gói trị liệu)
*   **Mô tả:** Khách hàng duyệt danh sách các gói trị liệu chăm sóc sức khỏe theo tiêu chuẩn GWI (Global Wellness Institute).
*   **Các tính năng lọc (Filter):** Lọc theo mục tiêu sức khỏe (Weight Loss - Giảm cân, Stress Relief - Giảm căng thẳng, Yoga - Thiền hành, Detoxification - Thải độc), thời gian lưu trú (số ngày) và khoảng giá.
*   **Kết quả đầu ra:** Trang chi tiết gói trị liệu hiển thị lộ trình (itinerary) mẫu, lợi ích sức khỏe và giá niêm yết.

### 2. UC07: Book Retreat Package & Pay Deposit (Đặt gói & Thanh toán đặt cọc)
*   **Mô tả:** Khách hàng chọn một gói trị liệu cụ thể, nhập ngày Check-in mong muốn, chọn hạng Villa lưu trú (Villa Type), và thực hiện thanh toán đặt cọc.
*   **Ràng buộc nghiệp vụ:**
    *   **BR-02 (Villa Selection):** Khách hàng chỉ được chọn **Loại biệt thự (Villa Type)** khi đặt chỗ, không được chọn số phòng vật lý cụ thể.
    *   **Hạn chế ngày đặt:** Không cho phép chọn ngày Check-in trong quá khứ. Hệ thống tự động tính ngày Check-out dựa trên thời lượng (số ngày) của gói nghỉ dưỡng đã chọn.
    *   **BR-01 (Booking Confirmation):** Trạng thái đơn đặt chỗ ban đầu là `PENDING` và `UNPAID`. Chỉ sau khi cổng thanh toán (VNPay Sandbox) xác nhận thanh toán đặt cọc thành công, đơn hàng mới chuyển thành `CONFIRMED` và `DEPOSITED`, đồng thời hệ thống tự động mở hồ sơ nợ phòng (**Guest Folio**).

### 3. UC08: Check In Guest (Làm thủ tục nhận phòng)
*   **Mô tả:** Lễ tân tiếp đón khách hàng vào ngày Check-in, tiến hành gán số phòng biệt thự thực tế và thu thập thông tin định danh để đăng ký tạm trú.
*   **Ràng buộc nghiệp vụ:**
    *   **BR-03 (Villa Allocation):** Chỉ được gán những biệt thự thuộc đúng loại Villa Type đã đặt và đang có trạng thái vật lý là **Trống & Sạch sẽ (Vacant/Cleaned)**. Hệ thống ngăn chặn gán phòng đang bảo trì hoặc đang có khách lưu trú.
    *   **BR-14 (Stay Registration):** Lễ tân bắt buộc phải nhập số định danh CCCD/Passport của khách.
    *   **Mã hóa dữ liệu:** Số CCCD/Passport phải được mã hóa trước khi lưu vào DB để đảm bảo an toàn thông tin.
    *   **BR-07 (Data Minimization):** Màn hình Check-in của lễ tân **tuyệt đối không hiển thị** các thông tin sức khỏe nhạy cảm (như bệnh lý, chấn thương hoặc dị ứng) của khách hàng.

### 4. UC09: Manage Villa Status (Quản lý trạng thái Villa)
*   **Mô tả:** Lễ tân hoặc bộ phận buồng phòng cập nhật trạng thái hoạt động thực tế của biệt thự.
*   **Các trạng thái trạng thái:**
    *   Trạng thái đặt phòng: `Available` (Trống), `Occupied` (Có khách ở), `Reserved` (Đã được giữ trước).
    *   Trạng thái dọn dẹp: `Cleaned` (Đã dọn dẹp), `Dirty` (Bẩn/Chưa dọn dẹp), `Needs Cleaning` (Cần dọn dẹp sau khi khách check-out hoặc dọn định kỳ).
    *   Trạng thái kỹ thuật: `Maintenance` (Bảo trì/Hỏng hóc).

### 5. UC10: View Booking Details & Itinerary Timeline (Xem lộ trình cá nhân)
*   **Mô tả:** Khách hàng truy cập tài khoản để xem lộ trình cá nhân được tổng hợp dưới dạng dòng thời gian (Timeline).
*   **Dữ liệu tổng hợp:** Hệ thống tự động truy vấn chéo từ các module khác để hiển thị đầy đủ lịch trình mỗi ngày của khách bao gồm:
    *   Lịch lưu trú Villa (Module 2).
    *   Lịch các buổi trị liệu/Spa đã đặt thành công (Module 3).
    *   Lịch các bữa ăn dinh dưỡng đã đặt dựa trên thực đơn đã lọc dị ứng (Module 4).

---

## III. Các ràng buộc nghiệp vụ chính (Business Rules Matrix)

| Mã Quy tắc | Tên Quy tắc | Nội dung kiểm tra & Xử lý nghiệp vụ |
| :--- | :--- | :--- |
| **BR-01** | Xác nhận đặt phòng | Trạng thái đặt phòng chuyển sang `CONFIRMED` chỉ sau khi nhận kết quả thanh toán cọc thành công từ VNPay. |
| **BR-02** | Chọn loại biệt thự | Khách hàng chỉ chọn `VillaType` trực tuyến. Số phòng vật lý cụ thể chỉ được gán tại quầy lễ tân khi Check-in. |
| **BR-03** | Hạn chế gán phòng | Không gán phòng đang có khách ở (`Occupied`) hoặc đang hỏng hóc/bảo trì (`Maintenance`). |
| **BR-09** | Mã hóa thông tin | Số CCCD/Passport của khách hàng bắt buộc phải mã hóa một chiều/hai chiều an toàn (`AES-256`) tại tầng Backend trước khi persist vào database. |
| **BR-14** | Khai báo tạm trú | Bắt buộc thu thập dữ liệu định danh khi làm thủ tục Check-in để phục vụ báo cáo lưu trú hằng ngày. |
| **BR-15** | Ghi nhật ký hệ thống | Mọi hành động tạo Booking, Check-in, đổi trạng thái phòng đều phải ghi nhận lại trong bảng Nhật ký kiểm toán (Audit Log). |

---

## IV. Cấu trúc bảng Database liên quan

1.  **BOOKING (Đơn đặt phòng):** Lưu trữ thông tin khách hàng (`guest_id`), gói trị liệu (`package_id`), Villa được gán (`assigned_villa_id`), ngày Check-in/Check-out, số khách (`total_guests`), trạng thái đặt phòng (`booking_status`) và trạng thái thanh toán (`payment_status`).
2.  **RETREAT_PACKAGE (Gói trị liệu):** Lưu trữ thông tin danh mục gói nghỉ dưỡng, thời lượng (ngày) và giá trọn gói.
3.  **VILLA (Biệt thự vật lý):** Lưu mã số phòng (`villa_code`), liên kết loại phòng (`type_id`), trạng thái đặt phòng (`villa_status`) và trạng thái dọn dẹp (`cleaning_status`).
4.  **VILLA_TYPE (Loại biệt thự):** Lưu thông tin cấu hình loại phòng như tên hạng phòng, giá phòng một ngày, ảnh minh họa và sức chứa tối đa.
