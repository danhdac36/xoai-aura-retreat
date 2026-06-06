# Kế hoạch Triển khai Frontend: Cấu trúc Layout & UC21, UC22

## Đánh giá thiết kế
1. **Kiến trúc Layout (Sidebar/Dashboard chung):** Sử dụng cơ chế **Thymeleaf Layout/Fragments** (chia sẻ component). Nghĩa là ta chỉ viết code thanh sidebar bên trái một lần duy nhất, sau đó nhúng (include) nó vào tất cả các trang quản trị (Admin/Staff Portal). Điều này giúp đồng bộ giao diện và dễ dàng áp dụng Phân quyền (RBAC) bằng Spring Security (ẩn/hiện các menu trên sidebar dựa theo Role).
2. **Tập trung UC21 & UC22:** Màn hình "Hóa đơn gộp & Check-out" là quan trọng nhất của Module 5. Nó đòi hỏi cấu trúc UI phức tạp (Grid 7:3) và logic tổng hợp dữ liệu chéo từ nhiều module (Booking, Spa, F&B). 

## Các thay đổi và triển khai

### 1. Kiến trúc Layout dùng chung (Thymeleaf Fragments)
Chúng ta sẽ tạo một bộ khung (shell) chuẩn cho toàn bộ phân hệ quản lý.

#### [NEW] `src/main/resources/templates/fragments/sidebar.html`
- Chứa code HTML của thanh Navigation dọc (Sidebar) bên trái. 
- Sẽ chứa các menu: Quản lý Phòng, Quản lý Spa, F&B, Thanh toán (Billing), Báo cáo (Dashboard).

#### [NEW] `src/main/resources/templates/fragments/header.html`
- Chứa Navbar phía trên (Profile người dùng, Nút Đăng xuất).

#### [NEW] `src/main/resources/templates/layout/admin-layout.html`
- Đóng vai trò là Trang gốc (Base Layout). Nó sẽ `th:replace` sidebar và header vào, đồng thời dùng `layout:fragment="content"` để nhường không gian ở giữa cho các trang con "đổ" nội dung vào.

### 2. Triển khai Giao diện UC21 & UC22 (Module Billing)

#### [NEW] `src/main/resources/templates/billing/checkout.html`
- Kế thừa từ khung `admin-layout.html`.
- Triển khai cấu trúc chia 2 cột (Tỷ lệ 7:3) giống bản vẽ UC21_22:
  - **Cột trái (Chi tiết nợ):** Chia thành 3 khối bảng liệt kê (Tiền phòng, Dịch vụ Spa phát sinh, Ẩm thực phát sinh).
  - **Cột phải (Tóm tắt & Hành động):** Bảng tính tổng tiền, tùy chọn Phương thức thanh toán (Radio button), và Nút bấm kích hoạt "Thanh toán & Check-out".

#### [NEW] `src/main/java/com/AuraMoon/auramoon/billing/controller/CheckoutController.java`
- Một Controller Spring MVC đơn giản để map đường dẫn `/billing/checkout` tới file giao diện `billing/checkout.html`.
