# Báo Cáo Phân Tích Nghiệp Vụ Đặt Phòng (Booking)

**Tài liệu phân tích kiến trúc hệ thống hiện tại và đề xuất nâng cấp nghiệp vụ Đặt phòng.**

## 1. Hiện trạng hệ thống (Core Architecture)
Hiện tại, cấu trúc Database đang quy định: **1 Lượt Đặt Phòng (Booking) chỉ được liên kết với duy nhất 1 Gói Trị Liệu (RetreatPackage).**
Công thức tính tiền hiện tại: `Tổng tiền = (Giá Gói × Số người) + (Giá Villa × Số ngày)`.
- **Ưu điểm:** Code đơn giản, tính tiền tổng cơ bản là đúng theo tiêu chuẩn chung.
- **Nhược điểm:** Quá cứng nhắc. Hệ thống không có trường kiểm tra sức chứa tối đa của Biệt thự. Nghiêm trọng hơn, vé Spa (`TreatmentBooking`) đang sinh ra sai logic (chỉ sinh vé bằng với số ngày, không nhân với số lượng khách).

## 2. Nếu áp dụng kịch bản "1 Người đặt cho đúng 1 Người"
Để chặn hệ thống chỉ cho phép 1 đơn đặt phòng áp dụng cho đúng 1 người, ta cần:
- **Backend:** Ép cứng `totalGuests = 1` khi tạo `Booking`. Sửa công thức tính tiền `GuestFolio` (bỏ phép nhân số lượng khách). Thêm Validation để chặn Request từ Client nếu có số lượng khách > 1.
- **Frontend:** Ẩn ô chọn số lượng khách trên màn hình UI và cố định giá trị (hidden input) gửi về Server là 1.

## 3. Nếu áp dụng kịch bản "1 Đoàn 5 Người, Ở chung 1 Villa, Mua 5 Gói khác nhau"
Hệ thống hiện tại **hoàn toàn không hỗ trợ** kịch bản này do thiết kế giới hạn "1 Booking = 1 Package". Để đáp ứng đúng nghiệp vụ thực tế của Resort, hệ thống buộc phải được nâng cấp toàn diện (Major Refactoring) sang mô hình Cha - Con (Master - Detail).

### Danh sách các module bị "vạ lây" và bắt buộc phải đập đi xây lại:

#### 3.1. Phân hệ Đặt phòng (Booking Module)
- **Database:** Xóa liên kết Gói trị liệu ở bảng `Booking` (Cha). Tạo thêm bảng mới `BookingDetail` (Con) để lưu danh sách đích danh từng người trong phòng và Gói trị liệu họ đã chọn.
- **DTO:** Sửa `BookingRequestDTO` để nhận mảng (Array) danh sách khách thay vì 1 ID Gói duy nhất.
- **Service:** Viết lại toàn bộ logic tính tiền và lưu dữ liệu trong `BookingServiceImpl.java`.
- **Frontend:** Xây dựng lại UI màn hình Đặt phòng. Cần có chức năng "Thêm Khách" và chọn Gói cho từng khách riêng biệt trên giao diện.

#### 3.2. Phân hệ Trị liệu (Spa Module)
- Các File như `TreatmentBooking.java` (Vé Spa) phải đổi liên kết từ mã `Booking` sang mã `BookingDetail`. Kỹ thuật viên Spa phải biết chính xác vị khách tên gì trong căn Villa đó đang dùng gói dịch vụ nào.

#### 3.3. Phân hệ Ẩm thực (FnB Module)
- Các File `MealOrder.java` và `DietaryProfile.java` (Hồ sơ ăn kiêng) phải liên kết với từng cá nhân. Vì mỗi người chọn một Gói khác nhau nên tiêu chuẩn ăn uống (Vegan, Keto, Normal...) của họ trong cùng 1 Villa là hoàn toàn khác nhau.

#### 3.4. Phân hệ Hóa đơn & Kế toán (Billing Module)
- Các file `GuestFolio.java` và `FolioItem.java` phải cập nhật lại cấu trúc để in ra được hóa đơn chi tiết dịch vụ của từng người, thay vì gộp chung một cục (để phục vụ xuất hóa đơn VAT).

#### 3.5. Phân hệ Báo cáo (Report Module)
- Toàn bộ các biểu đồ thống kê doanh thu, số lượng Gói bán ra của Admin phải được viết lại câu lệnh truy vấn SQL (Query). Lý do là vì dữ liệu Gói đã dời từ bảng `Booking` sang bảng `BookingDetail`.

---
**Tổng kết:** Thay đổi cấu trúc Booking là thay đổi trái tim của hệ thống. Nó sẽ tạo ra hiệu ứng dây chuyền buộc lập trình viên phải nâng cấp đồng loạt 5 phân hệ liên quan.
