# Yêu cầu Nâng cấp UC24 - Báo cáo Doanh thu (Advanced Revenue Dashboard)

Tài liệu này ghi nhận các tính năng và cải tiến UI/UX nhằm nâng cấp bảng điều khiển doanh thu (Dashboard) lên tiêu chuẩn chuyên nghiệp (Professional Grade) dành cho Quản lý cấp cao.

## 1. Tương tác sâu (Drill-down UX)
- **Tính năng:** Khi người quản lý click vào một phần cụ thể trên biểu đồ (ví dụ: phần "Spa & Trị liệu" trên biểu đồ tròn).
- **Hành vi (UX):** Bảng "Giao dịch gần đây" bên dưới tự động lọc và chỉ hiển thị các hóa đơn/giao dịch liên quan đến hạng mục vừa click.
- **Mục đích:** Giúp quản lý truy xuất nguyên nhân cốt lõi (root cause) của dữ liệu một cách trực quan mà không cần chuyển trang.

## 2. Bộ lọc thời gian nâng cao (Advanced Filtering)
- **Tính năng:** Bổ sung thanh công cụ lọc thời gian chi tiết (Date Range Picker nâng cao).
- **Các tùy chọn nhanh:**
  - Hôm nay (Today)
  - Tuần này (This Week)
  - Tháng này (This Month)
  - Quý này (This Quarter)
  - YTD (Year-to-Date - Từ đầu năm đến nay)
- **Tính năng so sánh:** Bổ sung checkbox "So sánh với cùng kỳ năm ngoái" (Compare to last year) để hiển thị đường xu hướng kép trên biểu đồ.

## 3. Cảnh báo và Gợi ý thông minh (Actionable Insights)
- **Tính năng:** Tích hợp một khu vực nhỏ dành riêng cho các Insight tự động (phân tích nhanh).
- **Ví dụ hoạt động:** Hệ thống tự động phân tích dữ liệu và đưa ra các câu chú ý như: *"Tỷ lệ lấp đầy tăng 5% nhưng doanh thu Ẩm thực giảm 10%. Cân nhắc chạy chương trình khuyến mãi Set Menu buổi tối để kích cầu."*
- **Mục đích:** Không chỉ hiển thị dữ liệu thô (Data) mà còn chuyển hóa thành thông tin hữu ích (Information) để ra quyết định.

## 4. Xuất Báo cáo (Export & Download)
- **Tính năng:** Nút "Xuất báo cáo" (Export) đặt ở góc phải phía trên Dashboard.
- **Định dạng hỗ trợ:**
  - Xuất dữ liệu bảng thô ra file **Excel/CSV**.
  - Xuất toàn bộ Dashboard (kèm biểu đồ) ra file **PDF** để phục vụ các buổi họp giao ban.

## 5. Logic tính toán Tỉ lệ tăng trưởng (Dynamic Growth Rate)
- **Tính năng:** Bổ sung logic tính toán phần trăm tăng/giảm của doanh thu so với kỳ trước để thay thế cho con số tĩnh trên UI.
- **Hành vi:**
  - Lấy tổng doanh thu của kỳ (tháng) đang chọn để đối chiếu với tổng doanh thu của kỳ trước đó (tháng trước).
  - Áp dụng công thức: `% Tăng trưởng = ((Kỳ này - Kỳ trước) / Kỳ trước) * 100`.
  - Hiển thị màu sắc động (Xanh/Đỏ/Xám) trên giao diện dựa trên kết quả tăng hay giảm.

## 6. Menu Hành động Giao dịch (Kebab Action Menu)
- **Tính năng:** Phát triển menu thả xuống (Dropdown) cho nút "3 chấm" tại mỗi dòng của bảng Giao dịch gần đây.
- **Các tác vụ tích hợp:**
  1. **Xem chi tiết:** Mở bảng tóm tắt Guest Folio tương ứng (Tiền phòng, Spa, F&B).
  2. **Tải Hóa đơn PDF:** Kết xuất hóa đơn điện tử cho giao dịch đã hoàn tất.
  3. **Gửi lại Email:** Kích hoạt lại email gửi biên lai thanh toán cho khách hàng nếu cần.
  4. **Tra soát:** (Nâng cao) Phục vụ giải quyết tranh chấp/khiếu nại thanh toán.

---
*Ghi chú: Bản phân tích này được lập để làm cơ sở cho đợt nâng cấp thiết kế UI/UX tiếp theo trên hệ thống Stitch và cập nhật code tương ứng.*
