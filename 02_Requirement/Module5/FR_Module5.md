# Đặc tả Yêu cầu Chức năng (Functional Requirements) - Module 5

Tài liệu này trích xuất và hệ thống hóa các yêu cầu chức năng (Giao diện & Logic) từ phần **3. Functional Requirements** của tài liệu SRS, dành riêng cho **Module 5: Thanh toán Gộp & Phân tích Thống kê** (Bao gồm các Use Case từ UC21 đến UC25).

*(Lưu ý: Chức năng UC23 - Gửi đánh giá (Submit Retreat Review) không có mô tả chi tiết giao diện trong Phần 3 của bản SRS gốc, do đó không được liệt kê màn hình ở đây, nhưng logic nghiệp vụ vẫn tuân theo Use Case Specification).*

---

## 1. Màn hình Thanh toán Gộp & Check-out (Consolidated Billing & Check-out Screen)
**Phục vụ Use Case:** 
- **UC21**: Tạo Hóa đơn Gộp (Consolidated Bill).
- **UC22**: Xử lý thanh toán cuối cùng và cập nhật trạng thái phòng.

### 1.1. Bố cục & Tổng quan (Layout & Description)
- **Bố cục (Layout):** Sử dụng cấu trúc chia 2 cột với tỷ lệ 7:3. 
  - Cột trái (rộng hơn): Liệt kê chi tiết toàn bộ các khoản nợ được phân loại rõ ràng theo từng phòng ban (Tiền phòng, Spa, F&B).
  - Cột phải (nhỏ hơn, sticky sidebar): Hiển thị bảng tổng kết tiền, phương thức thanh toán và nút Check-out.
- **Mô tả:** Màn hình này số hóa quy trình Kiểm toán đêm (Night Audit) và Sổ cái khách hàng (Guest Folio) của khách sạn. Hệ thống tự động gom toàn bộ các khoản nợ chưa thanh toán từ các điểm bán hàng (POS) trong toàn khu nghỉ dưỡng thành một hóa đơn cuối cùng duy nhất.

### 1.2. Chi tiết Trường dữ liệu & Ràng buộc (Fields & Constraints)
- **Các khối chi phí (Departmental Charge Blocks):**
  - Là các bảng dữ liệu (Data Grid) chỉ đọc (Read-only).
  - *Backend Logic:* Hệ thống PHẢI tự động gom (aggregate) các record này thông qua việc query chéo các Module 2, 3 và 4. Sử dụng `Room_Booking_ID` làm khóa ngoại (Foreign Key) chính để liên kết dữ liệu.
- **Tóm tắt Thanh toán (Payment Summary):**
  - Khối tính toán chỉ đọc. Tự động tính tổng đại số của số dư Tiền phòng + Spa + F&B + Thuế/Phí dịch vụ.
- **Phương thức thanh toán (Payment Method):**
  - Toggle / Radio Button (VD: Thẻ tín dụng, Chuyển khoản ngân hàng).
- **Nút "Thanh toán & Check-out" (Action Button):**
  - *Ràng buộc Nghiệp vụ (Strict Constraint):* Hệ thống phải xác thực trạng thái của tất cả các đơn hàng phụ. Khách hàng **KHÔNG THỂ** check-out nếu họ còn đơn hàng Spa hoặc F&B đang ở trạng thái chờ xử lý (pending/unprocessed). Nút bấm sẽ bị disable hoặc ném ra lỗi nếu vi phạm.
  - *Hành động sau khi thực thi (Post-execution):* Thanh toán thành công -> Đổi trạng thái Booking thành `Checked-out` -> Đổi trạng thái Villa vật lý thành `Vacant/Needs Cleaning` (Trống/Cần dọn dẹp).

---

## 2. Bảng điều khiển Doanh thu Quản lý (Manager Revenue Dashboard Screen)
**Phục vụ Use Case:** 
- **UC24**: Xem Bảng điều khiển Doanh thu (Pie/Bar charts) chia theo nguồn thu.

### 2.1. Bố cục & Tổng quan
- **Bố cục:** Cấu trúc Dashboard chuẩn với thanh điều hướng (sidebar) bên trái. Nội dung chính gồm: Thanh bộ lọc (Filter bar) trên cùng -> Khu vực giữa là 2 khối biểu đồ lớn (Biểu đồ tròn và Biểu đồ cột/đường) -> Khu vực dưới cùng là bảng chi tiết các giao dịch.
- **Mô tả:** Cung cấp cho Ban Quản lý cái nhìn tổng quan về hiệu suất tài chính. Dữ liệu được trích xuất động và tổng hợp từ các Sổ cái (Consolidated Folios) đã hoàn thành Check-out để đưa ra các chỉ số theo thời gian thực.

### 2.2. Chi tiết Trường dữ liệu & Ràng buộc
- **Bộ lọc (Filters):**
  - Gồm các Dropdown list (Khoảng thời gian: Tháng/Quý/Năm; Danh mục: Tất cả, Retreat, Spa, F&B). Khi thay đổi filter, Frontend sẽ gọi lại API để query và vẽ lại biểu đồ ngay lập tức.
- **Phân tách Doanh thu (Revenue Breakdown - Donut/Pie Chart):**
  - Thể hiện % và giá trị thực tế của từng nguồn thu chính (Gói Retreat, Dịch vụ Spa, Đồ ăn F&B).
- **Xu hướng Doanh thu (Revenue Trend - Line/Bar Chart):**
  - Thể hiện sự biến động tổng doanh thu theo dòng thời gian (VD: các tháng). Kèm theo các KPI tĩnh như "Tháng cao nhất" (Best Month) và "Trung bình tháng" (Monthly Average).
- **Bảng giao dịch gần đây (Recent Transactions Table):**
  - Bảng dữ liệu có phân trang, liệt kê các giao dịch check-out hoàn tất gần nhất (Tên khách, Dịch vụ, Ngày, Trạng thái, Số tiền).
- **Nút "Tải báo cáo CSV" (Export CSV):**
  - Kích hoạt backend xuất file .csv hoặc .xlsx từ dữ liệu của bảng trên dựa theo các filter đang kích hoạt.

---

## 3. Màn hình Báo cáo Hiệu suất & Xuất file (Performance Reports & Export Screen)
**Phục vụ Use Case:** 
- **UC25**: Xuất báo cáo tỷ lệ lấp đầy phòng và mức độ sử dụng nhân viên ra file Excel/PDF.

### 3.1. Bố cục & Tổng quan
- **Bố cục:** Layout Admin rộng rãi. Chia làm 3 vùng dọc: Cấu hình tham số báo cáo -> Tóm tắt KPI (có chỉ số tăng trưởng MoM) -> Bảng dữ liệu chi tiết có phân trang và nhãn cảnh báo trạng thái.
- **Mô tả:** Hệ thống phân tích tự động gom dữ liệu từ Front Desk, Spa, F&B để tính toán "Tỷ lệ lấp đầy (Occupancy)" và "Hiệu suất nhân sự". Quản lý có thể xem trước động hoặc xuất ra file báo cáo chính thức.

### 3.2. Chi tiết Trường dữ liệu & Ràng buộc
- **Nút "Xuất PDF" (Export PDF):**
  - *Backend Logic:* Khởi chạy luồng tạo tài liệu. Server dùng thư viện (VD: `iTextPDF` trong Java) để tạo cấu trúc bảng có chèn logo thương hiệu. Trả về Byte Stream để trình duyệt tự tải file `.pdf`.
- **Nút "Tạo báo cáo mới" (Generate Report):**
  - Gửi cấu hình Filter lên Server để tính toán lại toàn bộ KPI và Data Grid.
- **Bộ lọc Dữ liệu (Data Filters):**
  - Loại dữ liệu (Doanh thu tổng, Hiệu suất Spa, Tỷ lệ lấp đầy), Thời gian, Phân khúc.
- **Thẻ Tóm tắt KPI (KPI Summary Cards):**
  - Thể hiện các con số lõi. *Đặc biệt:* Tích hợp phân tích xu hướng so với tháng trước (Month-over-Month). Màu Xanh (Green) thể hiện tăng trưởng dương, Màu Đỏ (Red) thể hiện sự sụt giảm.
- **Bảng Xem trước Dữ liệu (Data Preview Grid):**
  - Hiển thị chi tiết từng dòng dữ liệu. Cột `Trạng thái` sử dụng mã màu (Color-coded badging) để cảnh báo nhận thức nhanh (TỐT - Xanh, CẦN CHÚ Ý - Đỏ).
- **Phân trang (Pagination Controls):**
  - *Logic:* Bắt buộc áp dụng Phân trang từ phía Server (Server-side Pagination sử dụng SQL `LIMIT/OFFSET`) để tối ưu bộ nhớ khi xử lý lượng dữ liệu lớn.
