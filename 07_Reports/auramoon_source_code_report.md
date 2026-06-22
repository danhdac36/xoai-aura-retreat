# Báo Cáo Phân Tích Toàn Bộ Source Code Dự Án Auramoon

## 1. Tổng quan dự án (Executive Summary)
Dự án **Auramoon** là một ứng dụng Web được xây dựng dựa trên kiến trúc **Spring Boot MVC** (Sử dụng Java 21) thay vì RESTful API. Dự án sử dụng cơ sở dữ liệu **Microsoft SQL Server**, kết hợp các công cụ giao diện như **Thymeleaf**, **HTML/CSS/JS** thuần và **Tailwind CSS** thông qua CDN/cấu hình nội bộ.

Dự án có sự tích hợp của các dịch vụ bên ngoài:
- **Thanh toán VNPay** (Sandbox).
- **Gửi Email** thông qua SMTP (Gmail).
- **Xác thực Google OAuth2** (Single Sign-On).

## 2. Thống kê Quy mô Source Code
Dự án có cấu trúc mã nguồn khá lớn, với các thành phần chính như sau:
- **Code Java**: ~163 file (Hơn 7.100 dòng code).
- **Template HTML**: ~43 file (Hơn 6.100 dòng code).
- **Javascript**: ~22 file (Hơn 2.100 dòng code).
- **CSS**: ~15 file (Hơn 660 dòng code).
- **Hình ảnh/Resources**: Hàng trăm file ảnh tĩnh chất lượng cao dung lượng lớn (108 file `.jpg`, `.webp`).

## 3. Kiến trúc hệ thống & Thiết kế Module
Theo **Nguyên tắc 1 và 4** của dự án, mã nguồn được tuân thủ chia tách thành các module rõ ràng trong cả Backend (`src/main/java/com/AuraMoon/auramoon`) và Frontend (`src/main/resources/`):

### Các Module chức năng chính (Java Packages & Frontend Folders):
1. **Auth (Xác thực & Phân quyền)**:
   - Xử lý Đăng nhập, Đăng ký, Phục hồi mật khẩu.
   - Hỗ trợ Role-based routing cho các vai trò: Admin, Manager, Receptionist, Therapist, Guest, Cheff.
2. **Booking (Đặt chỗ & Lịch trình)**:
   - Cho phép Guest đặt chỗ (Spa, Villas, Packages).
   - Tích hợp module Review và hiển thị Đặt lịch cho Lễ tân.
3. **Billing (Thanh toán & Hóa đơn)**:
   - Quá trình checkout thanh toán VNPay và hiển thị Dashboard thu chi.
4. **F&B (Ẩm thực - Food and Beverage)**:
   - Quản lý Menu, Dietary Management, đặt món ăn (Alacarte/Meal selection).
5. **Spa**:
   - Quản lý dịch vụ Spa, sắp xếp lịch cho Therapist.
6. **Dashboard & Report**:
   - Chức năng Báo cáo doanh thu, hiệu suất, tình trạng Booking cho Manager và Admin.
7. **Guest, Public, Reception**:
   - Các module phục vụ cho việc hiển thị thông tin công khai và lịch trình cá nhân của Guest.

## 4. Tuân thủ Nguyên Tắc Dự Án (Project Principles Compliance)
- **Nguyên tắc 1 (Kiến trúc MVC)**: Đã tuân thủ. Các controller (`@Controller`) trả về các view Thymeleaf (ví dụ: `auth/login`, `fnb/uc16-meal-selection`), sử dụng OOP, tuân thủ Java Naming Convention. Không lạm dụng `@RestController`.
- **Nguyên tắc 2 (File đầu ra dạng .md)**: Báo cáo này đang được xuất dưới dạng Markdown (.md) theo đúng yêu cầu người dùng.
- **Nguyên tắc 3 (Báo cáo trước khi thay thế code)**: Báo cáo phân tích này giúp người dùng nắm bắt tổng quan dự án hiện tại trước khi có các yêu cầu cập nhật code tiếp theo.
- **Nguyên tắc 4 (Cấu trúc Layout và Module Frontend)**: Đã tuân thủ. Các tệp tin JS và CSS được tách biệt hoàn toàn khỏi HTML, lưu trữ tại `static/js/` và `static/css/`, phân tầng thư mục rõ ràng theo từng Module chức năng (vd: `static/js/auth/login.js`, `static/css/billing/checkout.css`).

## 5. Tích hợp Testing (Kiểm thử)
Dự án được chú trọng cấu trúc Test chặt chẽ trong thư mục `src/test/java/`:
- **Unit Testing / Integration Testing**: Các module quan trọng như `billing`, `booking`, `dashboard`, `report`, `spa` đều có chứa các file test cụ thể (`DashboardIntegrationTest.java`, `BillingServiceImplTest.java`...).

## 6. Kết luận & Khuyến nghị
- Source code đã được chuẩn hóa rất tốt về mặt cấu trúc theo MVC pattern. 
- Việc phân chia resources Frontend thành từng folder module (auth, booking, fnb...) tạo điều kiện thuận lợi cho việc bảo trì.
- Sẵn sàng tiến hành các bước tối ưu hóa hoặc chỉnh sửa cụ thể theo yêu cầu tiếp theo của bạn.

## 7. Các Rủi Ro Tiềm Ẩn Dễ Gây Sai Flow
Mặc dù kiến trúc dự án khá tốt, trong quá trình chạy thực tế có thể gặp phải một số rủi ro làm gián đoạn luồng nghiệp vụ (sai flow):

1. **Rủi ro cấu hình và bên thứ ba (Third-party Integrations):**
   - **Thanh toán VNPay (Sandbox):** Các key cấu hình (`vnp_TmnCode`, `vnp_HashSecret`) bị thiếu, sai lệch, hoặc URL Sandbox bảo trì sẽ khiến luồng thanh toán (Checkout) thất bại.
   - **Xác thực Google OAuth2:** Nếu Client ID / Client Secret hết hạn, bị thu hồi, hoặc thiết lập sai `redirect-uri` (không khớp với cấu hình Google Cloud Console), người dùng không thể đăng nhập SSO.
   - **Gửi Email SMTP:** Dự án sử dụng tài khoản Gmail cá nhân (`auramoonretreat@gmail.com`) với App Password. Nếu mật khẩu ứng dụng thay đổi hoặc Gmail hạn chế gửi mail do spam limit, toàn bộ luồng đăng ký tài khoản, gửi hóa đơn và phục hồi mật khẩu sẽ bị lỗi.

2. **Rủi ro Đồng bộ Dữ liệu và Xử lý Đồng thời (Database / Concurrency):**
   - **Auto DDL bị tắt (`ddl-auto=none`):** Cấu hình cơ sở dữ liệu ngăn Hibernate tự động cập nhật bảng. Việc này tốt cho production nhưng nếu Database schema hiện tại (trong SQL Server) không khớp hoàn toàn với các class `@Entity` trong Java, sẽ sinh ra lỗi truy vấn SQL và làm chết luồng xử lý.
   - **Xung đột đặt chỗ (Concurrency Booking):** Đối với các tài nguyên giới hạn (phòng Villa, lịch Spa), nếu hệ thống thiếu cơ chế lock dữ liệu (Pessimistic/Optimistic locking), hai khách cùng lúc đặt một phòng có thể dẫn đến tình trạng *double-booking* (đặt trùng).

3. **Rủi ro Frontend và Giao diện (Thymeleaf & Cache):**
   - **Trình duyệt lưu cache (Browser Caching):** Dự án sử dụng CSS/JS thuần không qua công cụ đóng gói (asset bundler). Người dùng có thể gặp giao diện lỗi hoặc luồng xử lý JS bị sai do trình duyệt vẫn chạy file JS/CSS cũ từ cache.
   - **Lỗi Render HTML (Thymeleaf Template):** Thymeleaf parse DOM rất chặt chẽ. Nếu một template bị sai cú pháp (thiếu thẻ đóng) hoặc truy cập thuộc tính `null` từ Model, trang web sẽ lỗi 500 (Internal Server Error) toàn bộ thay vì bỏ qua lỗi.
