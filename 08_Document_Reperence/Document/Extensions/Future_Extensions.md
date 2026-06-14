# Hồ sơ Mở rộng Dự án (Future Extensions)

Tài liệu này lưu trữ các phân tích và ý tưởng kiến trúc nâng cao (Edge Cases & Extensions) được phát hiện trong quá trình thảo luận thiết kế với Product Owner. Những tính năng này nằm ngoài phạm vi cốt lõi hiện tại (Happy Path) của tài liệu SRS gốc, nhưng rất có giá trị thực tiễn và có thể được tích hợp trong các Giai đoạn (Phases) tiếp theo của dự án.

## 1. Thanh toán lẻ tẻ qua API (Standalone API Payments)

**Bối cảnh:** 
Trong mô hình chuẩn của dự án (Charge to Room), khách hàng chỉ thanh toán 2 lần: Đặt cọc (Booking) và Thanh toán gộp (Check-out). Tuy nhiên, trong thực tế vận hành, có những trường hợp khách hàng muốn thanh toán ngay lập tức cho một dịch vụ phát sinh (ví dụ: Ly nước cam ở nhà hàng, vé mời bạn bè vào Spa) bằng Chuyển khoản ngân hàng / VNPay QR thay vì ghi nợ vào phòng.

**Giải pháp Kiến trúc đề xuất:**
- **Không phá vỡ cấu trúc Folio:** Mọi dịch vụ vẫn phải sinh ra một bản ghi trong `FOLIO_ITEM`.
- **Sử dụng IPN/Webhook:** Bổ sung trường `payment_type` vào bảng `PAYMENT` (ví dụ: `FNB_PAYMENT`, `SPA_PAYMENT`).
- **Luồng hoạt động:** 
  1. Nhân viên nhấn nút "Thanh toán VNPay" trên POS cho đơn hàng lẻ. Hệ thống gọi API VNPay tạo QR code với mã giao dịch (OrderInfo) chỉ định rõ thuộc Folio nào và loại thanh toán là gì.
  2. Khi khách quét mã thành công, VNPay gọi ngầm về Webhook của hệ thống (Server-to-Server).
  3. Webhook tạo tự động một bản ghi vào bảng `PAYMENT` với `payment_type` tương ứng và `status='SUCCESS'`.
  4. Lúc Check-out (UC21), nhờ cơ chế Sổ cái kép (Double-entry Ledger), số tiền này tự động được cộng vào `Total Paid` và trừ đi ở `Balance Due`, đảm bảo không bao giờ tính trùng tiền của khách.

## 2. Quản lý linh hoạt "Dịch vụ phát sinh khác"

**Bối cảnh:**
Ngoài Spa và F&B (như liệt kê trong UC21), một khách sạn thực tế có vô vàn các khoản phí phát sinh khác:
- Khách book thêm Villa cho bạn bè (Extra Room).
- Đồ uống trong tủ lạnh phòng (Minibar).
- Đền bù hư hỏng tài sản (Damage Fee).
- Phụ thu trả phòng trễ (Late Checkout Fee).
- Dịch vụ giặt ủi (Laundry).

**Giải pháp Kiến trúc đề xuất:**
- **UI & DTO Động (Dynamic UI Binding):** Thay vì hardcode các thuộc tính List tĩnh (như `spaItems`, `fnbItems`) trong các đối tượng DTO hiển thị, sử dụng cấu trúc `Map<String, List<FolioItem>> groupedExtraServices`.
- **Xử lý Stream:** Backend sẽ lấy toàn bộ `FolioItem` thuộc về `GuestFolio` đó, sử dụng Java 8 Stream `Collectors.groupingBy(FolioItem::getServiceCategory)` để tự động gom nhóm.
- **Render linh hoạt:** Frontend (Thymeleaf/React/Vue) chỉ việc duyệt qua Map này. Nếu có hạng mục mới (ví dụ 'MINIBAR') được đẩy vào DB, hệ thống sẽ tự động in ra thêm 1 bảng Minibar trên Hóa đơn mà không cần phải sửa đổi hay deploy lại code.
