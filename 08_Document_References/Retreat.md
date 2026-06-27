# 1. Đặt vấn đề (Problem Statement)

Các Hệ thống Quản lý Khách sạn (PMS) truyền thống được thiết kế cho các khách sạn tiêu chuẩn, nơi khách hàng chỉ đơn giản là đặt phòng và thỉnh thoảng ăn tại nhà hàng. Tuy nhiên, các Khu nghỉ dưỡng Trị liệu (Wellness Resorts) hiện đại hoạt động theo cách khác. Khách hàng thường đặt các "Gói Retreat" (ví dụ: Hành trình Detox 5 ngày, Retreat Yoga 3 ngày) bao gồm chỗ ở, các kế hoạch bữa ăn kiêng/y tế cụ thể và các phiên Spa, trị liệu được lên lịch trình chặt chẽ.

Việc quản lý các trải nghiệm trọn gói này là một "cơn ác mộng" về mặt hậu cần đối với nhân viên khu nghỉ dưỡng. Xoai Aura Retreat được đề xuất để giải quyết vấn đề này bằng cách cung cấp một nền tảng tích hợp xử lý việc đặt phòng Villa, xếp lịch Spa phức tạp (ghép nối chuyên viên trị liệu với phòng điều trị), quản lý chế độ ăn cá nhân hóa và lập hóa đơn gộp (thanh toán tổng hợp), đồng thời bảo vệ nghiêm ngặt dữ liệu sức khỏe và dị ứng nhạy cảm của khách hàng.

# 2. Giao diện Công cụ Bên thứ ba (Third-Party Tool Interface)

Để đảm bảo tiêu chuẩn ngành, hệ thống phải tích hợp các API bên ngoài sau (mô phỏng hoặc thực tế):

- **API Cổng thanh toán (Stripe / VNPay Sandbox / PayPal)**: Chức năng xử lý an toàn các khoản tiền đặt cọc trực tuyến cho các Gói Retreat và xử lý thanh toán cuối cùng khi trả phòng.
- **API Lịch & Thông báo (Google Calendar API / SendGrid)**: Chức năng tự động đồng bộ lịch trình Spa/Yoga vào lịch cá nhân của khách hàng và gửi email nhắc nhở 1 giờ trước phiên trị liệu.
- **API Xác thực (Google Identity / Facebook Login)**: Chức năng cung cấp tính năng Đăng nhập một lần (SSO) an toàn để khách hàng đăng ký liền mạch.

# 3. Đặc tả Thực thể Bên ngoài - Tác nhân (External Entity Specification - Actors)

- **Khách hàng (Guest)**: Đặt các gói retreat, điền hồ sơ y tế/ăn kiêng, lên lịch các liệu trình spa và xem lịch trình của họ.
- **Lễ tân (Receptionist)**: Quản lý check-in/check-out, gán phòng Villa thực tế và xử lý hóa đơn gộp.
- **Chuyên viên Spa / Huấn luyện viên Yoga (Spa Therapist / Yoga Instructor)**: Xem lịch làm việc hàng ngày, kiểm tra ghi chú sức khỏe của khách trước khi điều trị và đánh dấu các phiên đã hoàn thành.
- **Nhân viên F&B / Đầu bếp (F&B Staff / Chef)**: Xem các yêu cầu ăn kiêng hàng ngày của khách đã check-in để chuẩn bị các bữa ăn cá nhân hóa mà không được xem các dữ liệu y tế riêng tư không cần thiết.
- **Quản lý Hệ thống (System Manager / Admin)**: Quản lý dữ liệu gốc (Villas, Gói dịch vụ, Dịch vụ Spa, Nhân viên) và xem các bảng điều khiển doanh thu.

# 4. Ca Sử dụng / Câu chuyện Người dùng (Use Cases / User Stories)

Dự án này được thiết kế thành 5 module độc lập nhưng liên kết chặt chẽ với nhau. Mỗi sinh viên phải chịu trách nhiệm một module (Làm Full-stack: DB -> DAO -> Controller -> UI).

## Module 1: Xác thực & Hồ sơ Sức khỏe Nhạy cảm (Giao cho Sinh viên 1)

- **UC01**: Với tư cách là khách hàng, tôi muốn đăng ký, xác minh email của mình và đăng nhập an toàn.
- **UC02**: Với tư cách là Khách hàng, tôi muốn hoàn thiện "Hồ sơ Sức khỏe & Ăn kiêng" của mình (ví dụ: ăn chay, dị ứng đậu phộng, đau lưng). Ràng buộc: Giao diện người dùng (UI) KHÔNG ĐƯỢC tick sẵn (unchecked) vào các hộp kiểm đồng ý khi thu thập dữ liệu nhạy cảm này (theo Nghị định 356/2025).
- **UC03**: Với tư cách là Admin, tôi muốn quản lý tài khoản nhân viên và gán các Vai trò (Roles) nghiêm ngặt (Chuyên viên trị liệu, Đầu bếp, Lễ tân) để giới hạn quyền truy cập dữ liệu.
- **UC04**: Với tư cách là Admin, tôi muốn quản lý Dữ liệu gốc cho các Hạng Villa, Dịch vụ Spa và Gói Retreat.
- **UC05**: Với tư cách là Khách hàng, tôi muốn thực hiện "Quyền được xóa" của mình, xóa vĩnh viễn dữ liệu sức khỏe và dị ứng của tôi khỏi hệ thống sau khi kỳ nghỉ retreat kết thúc.

## Module 2: Đặt Gói Retreat & Chỗ ở (Giao cho Sinh viên 2)

- **UC06**: Với tư cách là Khách hàng, tôi muốn duyệt các "Gói Retreat" có sẵn và lọc chúng theo mục tiêu (ví dụ: Giảm cân, Giảm căng thẳng, Yoga).
- **UC07**: Với tư cách là Khách hàng, tôi muốn chọn một gói, chọn ngày đi, chọn loại Villa và thanh toán tiền cọc an toàn.
- **UC08**: Với tư cách là Lễ tân, tôi muốn xem bảng điều khiển các khách dự kiến đến và thực hiện Check-In (gán một số phòng Villa cụ thể).
- **UC09**: Với tư cách là Lễ tân, tôi muốn quản lý trạng thái Villa thực tế (Trống, Đang có khách, Đang bảo trì).
- **UC10**: Với tư cách là Khách hàng, tôi muốn xem toàn bộ chi tiết đặt phòng và dòng thời gian lịch trình của mình.

## Module 3: Công cụ Xếp lịch Spa & Trị liệu - LÕI 1 (Giao cho Sinh viên 3 - Độ phức tạp cao)

- **UC11**: Với tư cách là Khách hàng (người đã đặt gói), tôi muốn lên lịch các phiên Spa/Trị liệu đi kèm bằng cách chọn ngày và khung giờ.
- **UC12**: Với tư cách là Hệ thống, tôi phải tự động tìm một khung giờ trống bằng cách khớp đồng thời MỘT Chuyên viên trị liệu rảnh VÀ MỘT Phòng điều trị trống.
- **UC13**: Với tư cách là Chuyên viên Spa, tôi muốn xem lịch trình hàng ngày của mình và truy cập các ghi chú y tế cụ thể (ví dụ: đau lưng) của các khách hàng được giao.
- **UC13.1**: Với tư cách là Quản lý, tôi muốn xem danh sách nhân viên Spa, lịch làm việc của họ, và cập nhật trạng thái (Nghỉ/Bận). Nếu cập nhật thành "Nghỉ", hệ thống sẽ tự động chuyển ca cho các lịch đã đặt sang chuyên viên khác.
- **UC14**: Với tư cách là Chuyên viên Spa, tôi muốn đánh dấu một phiên là "Đã hoàn thành" hoặc "Vắng mặt (No-Show)".
- **UC15**: Với tư cách là Lễ tân, tôi muốn đặt thủ công các dịch vụ Spa bổ sung cho khách và tính phí vào tài khoản (folio) Villa của họ.

## Module 4: Quản lý Ẩm thực F&B & Ăn kiêng - LÕI 2 (Giao cho Sinh viên 4)

- **UC16**: Với tư cách là Khách hàng, tôi muốn chọn trước các bữa ăn hàng ngày từ một thực đơn được hệ thống tự động lọc dựa trên hồ sơ dị ứng/ăn kiêng của tôi.
- **UC17**: Với tư cách là Đầu bếp/Nhân viên F&B, tôi muốn xem "Bảng điều khiển Chuẩn bị Bữa ăn Hàng ngày" hiển thị tổng hợp các đơn đặt món và các cảnh báo dị ứng cụ thể trong ngày.
- **UC18**: Với tư cách là Đầu bếp, tôi muốn cập nhật trạng thái của đơn đặt món (Đang chuẩn bị -> Sẵn sàng giao).
- **UC19**: Với tư cách là Khách hàng, tôi muốn gọi thêm đồ uống/thức ăn a-la-carte ngoài gói của mình và tính phí vào Villa.
- **UC20**: Với tư cách là Hệ thống, tôi phải che giấu toàn bộ tiền sử bệnh án của Khách hàng đối với Đầu bếp, chỉ hiển thị "Dị ứng thực phẩm" có liên quan (Nguyên tắc Hạn chế dữ liệu).

## Module 5: Thanh toán Gộp, Phân tích Thống kê & Nhân sự - Buồng phòng (Giao cho Sinh viên 5)

- **UC21**: Với tư cách là Lễ tân, trong quá trình Check-Out, tôi muốn tạo một Hóa đơn Gộp tổng hợp lại Chi phí Gói còn lại, Dịch vụ Spa gọi thêm và Đơn đặt món F&B gọi thêm.
- **UC22**: Với tư cách là Lễ tân, tôi muốn xử lý thanh toán cuối cùng và chuyển trạng thái Villa thành Trống/Cần dọn dẹp.
- **UC23**: Với tư cách là Khách hàng, sau khi check-out, tôi muốn gửi bài đánh giá và xếp hạng cho trải nghiệm Retreat của mình.
- **UC24**: Với tư cách là Quản lý, tôi muốn xem Bảng điều khiển Doanh thu (Biểu đồ tròn/cột) phân tách thu nhập theo Gói retreat, Spa và F&B.
- **UC25**: Với tư cách là Quản lý, tôi muốn xuất báo cáo "Tỷ lệ Lấp đầy phòng & Mức độ sử dụng Chuyên viên trị liệu" hàng tháng ra file Excel.
- **UC26**: Với tư cách là Quản lý/Hệ thống, tôi muốn thực thi quy trình "Night Audit" (thủ công hoặc tự động lúc 12h đêm) để hệ thống tự động rà soát, chốt doanh thu các điểm bán hàng (POS) trong ngày và tự động cộng phí tiền phòng (Room Charge) vào Guest Folio Item cho ngày hôm đó
- **UC27**: Với tư cách là Hệ thống, tôi muốn tự động kết xuất Hóa đơn gộp (Consolidated Invoice) dưới định dạng file PDF và gửi tự động vào email của khách hàng ngay sau khi Lễ tân hoàn tất Check-out.
- **UC28 (Housekeeping Management)**: Với tư cách là Quản lý Buồng phòng (Housekeeping Manager), tôi muốn xem danh sách các Villa đang ở trạng thái "Cần dọn dẹp" (DIRTY) để phân công dọn dẹp, đồng thời thực hiện nghiệm thu và cập nhật trạng thái về "Sạch sẽ / Sẵn sàng" (CLEAN / AVAILABLE) ngay trên cùng một màn hình để Lễ tân có thể gán phòng cho khách mới.
- **UC29 (Customer Reviews Management)**: Với tư cách là Quản lý, tôi muốn xem chi tiết các bài đánh giá và nhận xét của khách hàng gắn liền với từng mã đặt phòng (Booking ID) để theo dõi chất lượng dịch vụ.
- **UC30 (Audit Log Management)**: Với tư cách là Quản lý, tôi muốn theo dõi và quản lý Nhật ký kiểm toán (Audit Log) để giám sát mọi hành động và thay đổi dữ liệu trên hệ thống.
- **UC31 (Time Attendance)**: Với tư cách là Nhân viên (Lễ tân, Chuyên viên Spa, Đầu bếp), tôi muốn truy cập hệ thống để bấm nút "Check-in (Vào ca)" và "Check-out (Tan ca)". Hệ thống sẽ ghi nhận vào bảng `timesheet` (gồm `id`, `user_id`, `check_in_time`, `check_out_time`, `work_date`).
- **UC32 (Commission Payroll)**: Với tư cách là Quản lý, tôi muốn hệ thống tự động tính Bảng lương cuối tháng cho Chuyên viên Spa. Công thức: Lương cứng (tính theo ngày công từ bảng `timesheet`) + Tiền Hoa hồng (Commission nhân với tổng số ca Spa đã hoàn thành được trích xuất từ báo cáo của Module 3).
- **UC33 (Staff Profile Management)**: Với tư cách là Admin/Quản lý, tôi muốn xem trang Hồ sơ Chi tiết (Profile Details) của nhân viên/quản lý cấp dưới để tra cứu thông tin (có thể được điều hướng tới thông qua deep link từ Audit Log).
- **UC34 (Guest Booking History & Itinerary Viewer)**: Với tư cách là Khách hàng HOẶC Quản lý, tôi muốn xem danh sách toàn bộ Lịch sử các gói nghỉ dưỡng (Booking History) của khách hàng. Đồng thời, tôi có thể nhấn vào từng chuyến đi để xem chi tiết Lịch trình (Timeline) thực tế của chính gói đó.

# 5. Quy tắc Nghiệp vụ - Ràng buộc Nghiêm ngặt (Strict Constraints)

- **Chống trùng lịch 2 chiều ở Spa (2-Dimensional Double-Booking Prevention)**: Một lượt đặt lịch Spa chỉ hợp lệ nếu cả Chuyên viên trị liệu VÀ Phòng điều trị đều trống vào thời gian được yêu cầu. Giao dịch cơ sở dữ liệu (database transaction) phải khóa (lock) cả hai tài nguyên này đồng thời.
- **Ràng buộc Hóa đơn Gộp (Consolidated Billing Constraint)**: Khách hàng KHÔNG THỂ check-out nếu họ có đơn đặt Spa hoặc F&B phát sinh chưa được thanh toán/đang chờ xử lý.
- **Hạn chế dữ liệu - Phân quyền RBAC (Data Minimization)**:
  - Chuyên viên trị liệu chỉ có thể xem dữ liệu sức khỏe liên quan đến điều trị vật lý (ví dụ: chấn thương, đau lưng).
  - Đầu bếp chỉ có thể xem dị ứng ăn kiêng (ví dụ: dị ứng đậu phộng, ăn chay).
  - Lễ tân không được xem cả hai thông tin trên. Điều này phải được bắt buộc thực thi trong các câu lệnh truy vấn SQL Backend và Controller.

# 6. Tài liệu Tham khảo & Kiến thức Chuyên ngành (Nghiên cứu Bắt buộc)

## A. Tuân thủ Pháp lý & Bảo vệ Dữ liệu

### 1. Nghị định 356/2025/NĐ-CP về Bảo vệ Dữ liệu Cá nhân (Hiệu lực 1/1/2026):

- **Trọng tâm**: Điều 4 (Dữ liệu Nhạy cảm) và Điều 6 (Sự đồng ý rõ ràng).
- **Áp dụng vào Dự án**: Yêu cầu ăn kiêng (tiểu đường, dị ứng) và tình trạng thể chất là dữ liệu y tế nhạy cảm. Giao diện người dùng KHÔNG ĐƯỢC sử dụng các hộp kiểm "được tick sẵn". Module 1 phải thực hiện mã hóa cơ sở dữ liệu (encryption) cho các trường dữ liệu này.

### 2. Luật Cư trú 2020:

- **Trọng tâm**: Khai báo tạm trú bắt buộc đối với khách trong và ngoài nước.
- **Áp dụng vào Dự án**: Quá trình Check-in (Module 2) phải thu thập dữ liệu CCCD/ID cần thiết để Quản lý xuất báo cáo cho công an địa phương, đồng thời đảm bảo dữ liệu ID này được mã hóa khi lưu trữ.

## B. Nghiệp vụ Khách sạn & Chăm sóc sức khỏe

### 3. Tiêu chuẩn của Viện Sức khỏe Toàn cầu (GWI Standards):

- **Áp dụng vào Dự án**: Hướng dẫn thiết lập Dữ liệu Gốc cho các "Gói Retreat". Sinh viên nên sử dụng các thuật ngữ tiêu chuẩn của ngành (ví dụ: Retreat Chánh niệm, Thải độc) thay vì thuật ngữ khách sạn chung chung.

### 4. Hồ sơ thanh toán của khách (Guest Folio) & Kiểm toán đêm (Night Audit - Tiêu chuẩn AHLEI):

- **Trọng tâm**: Cơ chế kế toán trong đó tất cả các hệ thống điểm bán hàng (Spa, F&B) đẩy các khoản nợ vào tài khoản trung tâm của khách hàng (Folio).
- **Áp dụng vào Dự án**: Module 5 phải tổng hợp dữ liệu sử dụng Room_Booking_ID làm liên kết chính để tính toán hóa đơn cuối cùng một cách tự động.
