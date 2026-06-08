# BÁO CÁO PHÂN TÍCH ĐẶC TẢ DỰ ÁN: XOAI AURA RETREAT

* **Tên dự án:** Xoai Aura Retreat - A Wellness Resort & Spa Management System (Hệ thống quản lý khu nghỉ dưỡng & Spa trị liệu)
* **Mã dự án:** SWP391-HOS-03
* **Môn học:** Software Development Project (SWP391)
* **Số lượng thành viên:** 5 sinh viên

---

## I. Tổng Quan Dự Án & Bối Cảnh (Problem Statement)
Hệ thống quản lý khách sạn truyền thống (PMS) thường chỉ giải quyết các nghiệp vụ cơ bản như đặt phòng và dịch vụ ăn uống đơn giản. Đối với **Khu nghỉ dưỡng trị liệu (Wellness Resort)**, quy trình phức tạp hơn nhiều do khách hàng thường mua các **Gói trị liệu (Retreat Packages)** (ví dụ: Detox 5 ngày, Yoga 3 ngày) bao gồm phòng ở, thực đơn ăn uống/y khoa chuyên biệt, lịch trình Spa/trị liệu dày đặc.

**Xoai Aura Retreat** ra đời nhằm giải quyết bài toán vận hành logistics phức tạp này bằng cách cung cấp một nền tảng tích hợp:
* Quản lý đặt phòng Villa.
* Sắp xếp lịch Spa phức tạp (khớp lịch Kỹ thuật viên & Phòng trị liệu).
* Quản lý chế độ ăn uống cá nhân hóa.
* Hóa đơn thanh toán tổng hợp (Consolidated Billing).
* Bảo vệ nghiêm ngặt dữ liệu sức khỏe nhạy cảm và dị ứng của khách hàng.

---

## II. Các Tích Hợp Bên Thứ Ba (Third-Party APIs)
Để đảm bảo tiêu chuẩn ngành, hệ thống cần tích hợp các API sau (giả lập hoặc thực tế):
1. **Cổng thanh toán (Payment Gateway):** Stripe / VNPay Sandbox / PayPal.
   * *Nhiệm vụ:* Xử lý đặt cọc gói trị liệu trực tuyến và thanh toán hóa đơn khi checkout.
2. **Lịch biểu & Thông báo (Calendar & Notification):** Google Calendar API / SendGrid.
   * *Nhiệm vụ:* Đồng bộ lịch Spa/Yoga vào lịch cá nhân của khách và gửi email nhắc nhở trước giờ trị liệu 1 tiếng.
3. **Xác thực (Authentication):** Google Identity / Facebook Login (SSO).

---

## III. Các Tác Nhân Trong Hệ Thống (Actors)
Hệ thống phục vụ 5 nhóm đối tượng sử dụng chính:
1. **Khách hàng (Guest):** Đặt gói, khai báo hồ sơ sức khỏe/ăn uống, đặt lịch trị liệu, xem lịch trình cá nhân.
2. **Lễ tân (Receptionist):** Quản lý Check-in/Check-out, gán phòng Villa, quản lý tình trạng Villa, thanh toán hóa đơn tổng hợp.
3. **Kỹ thuật viên Spa / Huấn luyện viên Yoga (Spa Therapist / Yoga Instructor):** Xem lịch làm việc hàng ngày, xem ghi chú sức khỏe của khách được phân công, xác nhận trạng thái buổi trị liệu.
4. **Nhân viên bếp / Đầu bếp (F&B Staff / Chef):** Xem danh sách thực đơn cá nhân hóa của khách lưu trú hàng ngày (không được xem dữ liệu bệnh lý khác).
5. **Quản lý hệ thống (Admin/Manager):** Quản lý dữ liệu danh mục (Villas, Gói trị liệu, Dịch vụ Spa, Staff), xem báo cáo doanh thu và công suất sử dụng.

---

## IV. Cấu Trúc Module & Phân Chia Thành Viên (5 Modules)
Dự án được thiết kế thành 5 module độc lập nhưng kết nối chặt chẽ. Mỗi sinh viên phụ trách toàn diện một module (Full-stack từ DB -> DAO -> Controller -> UI).

### Module 1: Xác Thực & Hồ Sơ Sức Khỏe Nhạy Cảm (Sinh viên 1)
* **UC01:** Đăng ký, xác thực email, đăng nhập bảo mật cho khách.
* **UC02:** Khách khai báo "Hồ sơ sức khỏe & Chế độ ăn" (ví dụ: dị ứng đậu phộng, đau lưng, ăn chay). *Ràng buộc: Giao diện phải dùng hộp kiểm (checkbox) trống, không được chọn sẵn mặc định để lấy sự đồng ý rõ ràng (theo Nghị định 356/2025).*
* **UC03:** Quản lý tài khoản nhân viên và phân quyền chặt chẽ (RBAC) để hạn chế truy cập dữ liệu nhạy cảm.
* **UC04:** Quản lý dữ liệu danh mục (Hạng Villa, Dịch vụ Spa, Gói trị liệu).
* **UC05:** Khách hàng thực hiện quyền yêu cầu xóa dữ liệu nhạy cảm (Right to Deletion) sau khi kỳ nghỉ kết thúc.

### Module 2: Đặt Gói Trị Liệu & Phòng Ở (Sinh viên 2)
* **UC06:** Khách duyệt và lọc các "Gói trị liệu" theo mục tiêu sức khỏe (Giảm cân, Giảm stress, Yoga...).
* **UC07:** Chọn gói, chọn ngày, chọn loại Villa và thanh toán tiền cọc.
* **UC08:** Lễ tân xem danh sách khách sắp đến để thực hiện Check-in (gán số phòng cụ thể).
* **UC09:** Quản lý trạng thái Villa vật lý (Trống, Đang ở, Bảo trì).
* **UC10:** Khách xem chi tiết đặt phòng và dòng thời gian lịch trình (Itinerary Timeline).

### Module 3: Công Cụ Sắp Lịch Spa & Trị Liệu - CORE 1 (Sinh viên 3 - Độ phức tạp cao)
* **UC11:** Khách đã đặt gói tự chọn ngày/giờ cho các buổi trị liệu đi kèm.
* **UC12:** Hệ thống tự động tìm khung giờ trống bằng cách khớp đồng thời cả **Kỹ thuật viên trống** VÀ **Phòng trị liệu trống**.
* **UC13:** Kỹ thuật viên xem lịch làm việc và truy cập ghi chú sức khỏe liên quan (ví dụ: đau lưng).
* **UC14:** Kỹ thuật viên xác nhận trạng thái buổi trị liệu ("Hoàn thành" hoặc "Khách không đến").
* **UC15:** Lễ tân đặt thêm dịch vụ Spa ngoài gói cho khách và tính vào hóa đơn phòng.

### Module 4: Quản Lý Ăn Uống (Dietary F&B) - CORE 2 (Sinh viên 4)
* **UC16:** Khách chọn món ăn hàng ngày từ thực đơn đã được lọc tự động dựa trên hồ sơ dị ứng/ăn kiêng của họ.
* **UC17:** Đầu bếp xem bảng điều khiển chuẩn bị món ăn hàng ngày (tổng hợp món ăn và cảnh báo dị ứng tương ứng).
* **UC18:** Đầu bếp cập nhật trạng thái món ăn (Đang chuẩn bị -> Sẵn sàng giao).
* **UC19:** Khách gọi thêm món a-la-carte (ngoài gói) và ghi nợ vào phòng Villa.
* **UC20:** Hệ thống tự động ẩn thông tin bệnh lý không liên quan đối với Đầu bếp, chỉ hiển thị "Dị ứng thực phẩm" để tuân thủ nguyên tắc giảm thiểu dữ liệu (Data Minimization).

### Module 5: Hóa Đơn Tổng Hợp & Phân Tích (Sinh viên 5)
* **UC21:** Lễ tân xuất hóa đơn tổng hợp (Consolidated Invoice) khi Checkout, bao gồm: Tiền gói trị liệu còn lại + Dịch vụ Spa phát sinh + Món ăn F&B phát sinh.
* **UC22:** Xử lý thanh toán cuối cùng và chuyển trạng thái phòng sang "Trống/Cần dọn dẹp".
* **UC23:** Khách gửi đánh giá/phản hồi sau khi hoàn thành kỳ nghỉ.
* **UC24:** Quản lý xem biểu đồ doanh thu (Pie/Bar charts) phân tích theo gói, spa và ẩm thực.
* **UC25:** Xuất báo cáo công suất phòng và hiệu suất làm việc của Kỹ thuật viên ra file Excel hàng tháng.

---

## V. Quy Tắc Nghiệp Vụ Nghiêm Ngặt (Business Rules)
1. **Chống trùng lịch 2 chiều (Spa Double-Booking Prevention):** Một lịch hẹn chỉ hợp lệ khi cả kỹ thuật viên VÀ phòng trị liệu đều trống tại thời điểm đó. Giao dịch database phải khóa đồng thời cả hai tài nguyên này để tránh tranh chấp dữ liệu (race condition).
2. **Ràng buộc hóa đơn tổng hợp (Consolidated Billing):** Không cho phép khách Checkout nếu vẫn còn các hóa đơn phát sinh chưa thanh toán tại quầy Spa hoặc F&B.
3. **Giảm thiểu dữ liệu & Phân quyền (Data Minimization - RBAC):**
   * *Kỹ thuật viên trị liệu:* Chỉ được xem dữ liệu thể trạng (chấn thương, đau khớp...).
   * *Đầu bếp:* Chỉ được xem dị ứng thực phẩm (ví dụ: dị ứng lạc, ăn chay...).
   * *Lễ tân:* Không được xem cả hai loại dữ liệu trên.
   * Phải được thực thi trực tiếp tại tầng truy vấn cơ sở dữ liệu (SQL queries) và Controller.

---

## VI. Tuân Thủ Pháp Lý & Tiêu Chuẩn Ngành (Mandatory Research)
Hệ thống phải tuân thủ nghiêm ngặt các quy định thực tế sau:
* **Nghị định 356/2025/NĐ-CP về Bảo vệ dữ liệu cá nhân (Hiệu lực từ 01/01/2026):**
  * Dữ liệu sức khỏe/chỉ số sinh trắc học là dữ liệu nhạy cảm (Điều 4).
  * Yêu cầu đồng ý tường minh (Điều 6) -> UI không được tích sẵn checkbox.
  * Phải mã hóa dữ liệu nhạy cảm này trong database.
* **Luật Cư trú 2020:**
  * Quy định khai báo tạm trú bắt buộc cho khách lưu trú.
  * Module 2 phải thu thập thông tin định danh (ID/Passport) để xuất báo cáo gửi công an địa phương hàng ngày (dữ liệu này phải được mã hóa khi lưu trữ).
* **Tiêu chuẩn GWI (Global Wellness Institute) về du lịch chăm sóc sức khỏe:**
  * Phải sử dụng đúng thuật ngữ chuyên ngành khi tạo gói trị liệu (*Mindfulness*, *Detoxification*, *Ayurveda*) thay vì các từ ngữ khách sạn chung chung.
* **Tiêu chuẩn Folio & Night Audit (AHLEI):**
  * Mọi giao dịch từ các điểm bán lẻ (Spa, F&B) phải ghi nhận trực tiếp vào Folio (hồ sơ thanh toán phòng) của khách thông qua mã `Room_Booking_ID` để tính toán hóa đơn động.
