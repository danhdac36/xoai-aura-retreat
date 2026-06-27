# Báo cáo Triển khai: Giao diện Quick View Modal cho Villa

## 1. Mục đích tính năng
- Cho phép khách hàng "Xem nhanh" không gian Villa ngay tại Bước 2 của Form Đặt phòng (`create-form.html`).
- Tăng trải nghiệm người dùng (UX): Khách hàng không cần mở tab mới hay chuyển trang (điều này dễ làm đứt đoạn luồng thanh toán và giảm tỷ lệ chốt đơn).

## 2. Vấn đề hiện tại (Nghiệp vụ & Database)
Dựa trên phân tích mã nguồn file `VillaType.java`, bảng dữ liệu hiện tại đang có giới hạn:
- **Đã có:** `Tên Villa (typeName)`, `Ảnh (image)`, `Giá phụ thu (pricePerDay)`.
- **Chưa có:** Các cột lưu trữ `Mô tả chi tiết (Description)`, `Tiện ích phòng (Amenities)`, hay `Diện tích (Size)`.

## 3. Hai phương án giải quyết đề xuất

### Phương án 1: Triển khai ngay (Giao diện Frontend)
- **Cách làm:** Code ngay giao diện Pop-up bằng HTML/JS/Tailwind. Phần "Tên, Ảnh, Giá" lấy dữ liệu động từ Database. Phần "Mô tả và Tiện ích" sẽ code cứng (hardcode) bằng một đoạn văn bản giới thiệu chung chung của Resort.
- **Ưu điểm:** Có ngay tính năng cho luồng UI/UX Đặt phòng hoàn chỉnh mượt mà.
- **Nhược điểm:** Nội dung mô tả chưa phản ánh được sự khác biệt giữa từng loại Villa. Sau này khi Backend có thêm cột Description, Frontend cần thay lại biến số.

### Phương án 2: Chờ đồng bộ (Backend)
- **Cách làm:** Yêu cầu đội Backend bổ sung các cột `description`, `amenities` vào bảng `VillaType`. Sau khi API được cập nhật đẩy đủ dữ liệu thì Frontend mới tiến hành ráp giao diện Pop-up.
- **Ưu điểm:** Dữ liệu chân thực và đồng bộ 100%.
- **Nhược điểm:** Mất thời gian chờ đợi Backend, làm gián đoạn tiến độ code giao diện.

*Khuyến nghị: Nên làm theo Phương án 1 để hoàn thiện trải nghiệm người dùng trước.*

## 4. Cấu trúc kỹ thuật dự kiến (`create-form.html`)
- **Nút bấm:** Thêm nút `[🔍 Khám phá không gian]` trên Card.
- **Modal Container:** Khối div nền kính mờ `backdrop-blur-md bg-black/60` ẩn đi bằng CSS `opacity-0 invisible`.
- **JS Logic:** Viết hàm `openVillaModal(id, name, img, price)` để nhận dữ liệu từ Card được chọn và hiển thị lên Modal. Hàm `selectVillaFromModal(id)` để đánh dấu chọn (Checked) vào Radio Button tương ứng khi khách bấm chọn ngay trong Pop-up.
