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

#### [MODIFY] `src/main/resources/templates/billing/checkout.html`
- Kế thừa từ khung `admin-layout.html`.
- Triển khai toàn bộ bằng **Tailwind CSS** với thiết kế lấy trực tiếp từ hệ thống Stitch.
- Cấu trúc sử dụng hệ thống Grid 12 cột (`grid-cols-12`):
  - **Cột trái (8 phần - Chi tiết nợ):** Chia thành các khối bảng liệt kê (Tiền phòng, Dịch vụ Spa, Ẩm thực phát sinh...) với giao diện tự động sinh ra từ danh sách dịch vụ.
  - **Cột phải (4 phần - Tóm tắt & Hành động):** Bảng tính tổng tiền, lựa chọn Phương thức thanh toán (dùng Custom Radio với CSS Tailwind), Nút thanh toán và tích hợp JavaScript hiển thị Modal (Popup) báo cáo thành công.

#### [NEW] `src/main/java/com/AuraMoon/auramoon/billing/controller/CheckoutController.java`
- Một Controller Spring MVC đơn giản để map đường dẫn `/billing/checkout` tới file giao diện `billing/checkout.html`.
