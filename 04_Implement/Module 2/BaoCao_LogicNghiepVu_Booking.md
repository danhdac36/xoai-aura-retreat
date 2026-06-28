# Báo Cáo Phân Tích & Đề Xuất Nâng Cấp Logic Nghiệp Vụ Booking
**Dự án:** AuraMoon Retreat
**Module:** Booking (Module 2)

---

## 1. TÌNH TRẠNG HIỆN TẠI (The Problem)
Hệ thống Booking hiện tại xử lý luồng cơ bản rất tốt, nhưng đang thiếu các rào cản kiểm soát cho chức năng **"Đặt nhóm đông người" (Group Booking)**. 
Cụ thể, giao diện đang cho phép nhập số lượng khách lên tới 10 người (`max="10"`), nhưng Backend lại xử lý đơn 10 người y hệt như đơn 1 người (chỉ gán 1 căn Villa và chỉ thu tiền phụ thu của 1 căn).

---

## 2. TẠI SAO PHẢI SỬA? (Hậu quả kinh doanh)
Nếu giữ nguyên hiện trạng và đưa dự án vào thực tế (Go-Live), AuraMoon sẽ đối mặt với 3 thảm họa vận hành sau:

1. **Overcrowding (Quá tải phòng):** Khách hàng cố tình đi 10 người, chọn căn "Biệt thự Canopy" (chỉ thiết kế cho 2 người). Hệ thống vẫn chấp nhận. Khách đến nơi không có đủ chỗ ngủ, gây khủng hoảng truyền thông và phàn nàn.
2. **Thất thoát doanh thu trầm trọng:** Đi 10 người đáng lẽ phải thuê 5 căn Villa. Nhưng hệ thống hiện tại chỉ cộng `(Giá 1 căn × Số ngày)`. AuraMoon bị mất doanh thu của 4 căn Villa còn lại.
3. **Lỗi "Bán khống" (Overbooking):** Thuật toán tìm phòng trống hiện tại chỉ kiểm tra "Loại Villa này còn trống không?". Nó thấy còn 1 căn trống thì nó báo OK. Nó không biết rằng đoàn 10 người này cần tới 5 căn trống cùng lúc. Hậu quả là nhận tiền của khách xong nhưng không có đủ phòng để giao.

---

## 3. CẦN SỬA NHỮNG GÌ? (Giải pháp đề xuất)

Team phát triển có thể chọn 1 trong 2 phương án sau để vá lỗ hổng này tùy thuộc vào thời gian cho phép của dự án.

### PHƯƠNG ÁN 1: Vá Nhanh (Dễ nhất - Đề xuất dùng cho đồ án SWP391)
Nếu dự án đang gấp rút và không muốn đập đi xây lại Database, hãy chặn đứng vấn đề ngay từ "cửa gửi xe" (Frontend).

*   **Chỉ sửa 1 file duy nhất:** `booking-form.html` (Hoặc file JS tương ứng).
*   **Hành động:** 
    *   Đổi thuộc tính ô nhập số khách thành `max="2"`.
    *   Thêm dòng ghi chú: *"Hệ thống tự động hiện chỉ hỗ trợ đặt gói cho nhóm tối đa 2 người. Quý khách đi theo đoàn lớn vui lòng liên hệ Hotline."*
*   **Kết quả:** Giải quyết triệt để mọi lỗ hổng trên mà không tốn công code thêm logic phức tạp. Hợp lý với mô hình Retreat tĩnh lặng.

### PHƯƠNG ÁN 2: Xây Trọn Vẹn (Phức tạp - Chuẩn thực tế)
Nếu muốn hệ thống thông minh, tự động phân bổ nhiều Villa cho đoàn đông người, team cần thực hiện phẫu thuật lớn ở 3 nơi:

**A. Ở Database & Entity (`VillaType.java`)**
*   Thêm cột `maxGuests` (Số người tối đa / phòng).

**B. Ở Backend (`BookingServiceImpl.java`)**
*   **Tính số phòng cần thuê:** `int neededVillas = Math.ceil(totalGuests / villaType.getMaxGuests());` (Ví dụ 5 người, phòng sức chứa 2 -> Cần 3 phòng).
*   **Sửa logic kiểm tra phòng trống:** Thay vì kiểm tra xem "có trống không", phải đếm xem "có đủ `neededVillas` trống không?".
*   **Sửa logic tính tiền phụ thu:** Thay vì `(Giá phụ thu × Số ngày)`, phải đổi thành `(Giá phụ thu × Số ngày × neededVillas)`.

**C. Ở Giao diện (`booking-form.js`)**
*   Cập nhật lại công thức Javascript tương đương như Backend để màn hình hiển thị đúng số tiền cho khách xem.

---
*Báo cáo kết thúc.*
