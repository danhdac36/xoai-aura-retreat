# Báo Cáo Phân Quyền (Authorization Report)
**Dự án:** Xoai Aura Retreat (Aura Moon)
**Phân hệ:** Security & Authorization
**Ngày thực hiện:** 26/06/2026

Theo Nguyên tắc số 3 và 4 trong `principles.md`, tài liệu này tóm tắt kết quả phân tích và đồng bộ phân quyền giữa giao diện hiển thị (`sidebar.html`) và chốt chặn bảo mật ở backend (`SecurityConfig.java`).

## 1. Tình trạng trước khi đồng bộ
- **Frontend (`sidebar.html`):** Rất chặt chẽ. Thanh menu bên trái ẩn hiện các nút bấm rất chuẩn chỉ theo từng role (Sử dụng `sec:authorize="hasRole(...)"`).
- **Backend (`SecurityConfig.java`):** Khá sơ sài và "mở toang". Đa số các mảng `xxx_ENDPOINTS` đều bị comment trống rỗng. Mặc dù giao diện đã giấu nút bấm, nhưng nếu người dùng đoán được URL và gõ trực tiếp lên thanh địa chỉ (ví dụ: gõ `/manager/housekeeping`), họ vẫn có thể truy cập được vì Backend chưa thực sự chặn.

## 2. Kết quả đồng bộ (Chi tiết chỉnh sửa)
Chúng tôi đã xây dựng lại toàn bộ các mảng Endpoint trong `SecurityConfig.java` để ánh xạ 1:1 với giao diện:

| Role (Quyền) | Các URL được bảo vệ (Chỉ cho phép Role tương ứng truy cập) |
| :--- | :--- |
| **GUEST** | `/booking/itinerary/**`, `/guest/booking-spa/**`, `/fnb/meal-selection/**`, `/fnb/alacarte-order/**`, `/profile/health/**` |
| **ADMIN** | `/admin/**`, `/manager/dashboard/**` |
| **RECEPTIONIST** | `/receptionist/bookings/**`, `/receptionist/booking-spa/**` |
| **THERAPIST** | `/therapist/schedules/**` |
| **CHEFF** | `/fnb/chef/**` |
| **MANAGER** | `/manager/housekeeping/**`, `/billing/night-audit/**`, `/manager/spa/**` |
| **MANAGER / ADMIN** (Dùng chung) | `/manager/report/**`, `/packages/**`, `/manager/reviews/**` |
| **RECEPTIONIST / ADMIN** (Dùng chung) | `/receptionist/villa/**` |

*Lưu ý: URL `/packages/**` đã được đưa vào diện quản lý của `MANAGER / ADMIN` để khớp tuyệt đối với cấu hình hiển thị trong `sidebar.html`. Khách vãng lai và Guest sẽ nhận lỗi 403 Forbidden nếu cố truy cập trang này.*

## 3. Xác minh tính năng
Các bộ lọc URL của Spring Security hiện đã được gán cứng các rule `.hasRole()` và `.hasAnyRole()` tương ứng tại phương thức `securityFilterChain`. 

Hệ thống đã loại bỏ triệt để rủi ro "vượt rào" bằng cách gõ URL trực tiếp. Mọi truy cập trái phép hoặc cố tình dò tìm đường dẫn giờ đây sẽ bị Spring Security chặn đứng bằng mã lỗi **403 Forbidden** (hoặc bị đá văng về trang Login 302 nếu chưa đăng nhập).
