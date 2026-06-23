# ĐỀ XUẤT TÍNH NĂNG AI CHO HỆ THỐNG QUẢN LÝ (MANAGER DASHBOARD)

**Mục tiêu:** 
Nâng cấp bảng điều khiển (Dashboard) của Manager từ dạng báo cáo thống kê dữ liệu quá khứ (Descriptive Analytics) lên mức độ phân tích chuyên sâu, dự báo tương lai và đưa ra khuyến nghị hành động (Predictive & Prescriptive Analytics) bằng công nghệ Trí tuệ Nhân tạo (AI). Điều này giúp hệ thống tiệm cận với các chuẩn mực PMS cao cấp trên thế giới, đồng thời đáp ứng xuất sắc yêu cầu đổi mới từ giảng viên hướng dẫn.

---

## 5 CẤP ĐỘ (LEVEL) TÍCH HỢP AI CHO HỆ THỐNG

### Level 1: AI Data Insight (Trợ lý Phân tích Dữ liệu tự động)
Hỗ trợ Manager đọc hiểu số liệu nhanh chóng thông qua việc AI tự động tổng hợp và đưa ra nhận xét bằng ngôn ngữ tự nhiên.
*   **Mô tả:** AI đọc dữ liệu doanh thu, công suất phòng của chu kỳ hiện tại và đưa ra báo cáo tóm tắt "Executive Summary" kèm theo khuyến nghị hành động.
*   **Ví dụ hiển thị:** *"Doanh thu Spa tuần này tăng 15% nhưng công suất phòng lưu trú chỉ đạt 60%. Đề xuất: Triển khai chiến dịch gửi email tặng voucher Spa 30% cho khách hàng book phòng cuối tuần để kích cầu lưu trú."*
*   **Độ khó:** Dễ - Gọi API LLM truyền thống.

### Level 2: Predictive Forecasting (Dự báo Doanh thu / Công suất tương lai)
Dự báo tương lai để có kế hoạch chuẩn bị nguồn lực thay vì chỉ nhìn vào dữ liệu quá khứ.
*   **Mô tả:** Dựa vào dữ liệu lịch sử và tính chu kỳ (Seasonality), hệ thống vẽ tiếp đường dự báo (Trend Line) trên biểu đồ doanh thu/công suất cho 7 - 14 ngày tiếp theo.
*   **Ví dụ hiển thị:** Một đường nét đứt (dashed line) trên biểu đồ Chart.js nối tiếp chuỗi dữ liệu thực tế, kèm theo vùng đổ bóng (confidence interval) biểu thị rủi ro/sai số.
*   **Độ khó:** Trung bình - Có thể dùng thuật toán Linear Regression hoặc dùng AI API để nội suy dữ liệu.

### Level 3: Sentiment & Quality Trend (Phân tích Xu hướng Cảm xúc Khách hàng)
Lượng hóa chất lượng dịch vụ dựa trên phản hồi của khách hàng.
*   **Mô tả:** Hệ thống sử dụng NLP (Natural Language Processing) phân tích tất cả đánh giá của khách hàng (Feedback/Review), phân loại thành Tiêu cực (Negative), Tích cực (Positive), Trung tính (Neutral).
*   **Ví dụ hiển thị:** Đồ thị dạng cột xếp chồng (Stacked Bar Chart). Nếu AI phát hiện cột "Phàn nàn" tăng đột biến, hệ thống sẽ chắt lọc từ khóa: *"Có 15 phàn nàn về 'nước hồ bơi lạnh' vào sáng thứ 4. Đề xuất: Rà soát lại bộ gia nhiệt hồ bơi."*
*   **Độ khó:** Khá.

### Level 4: AI Dynamic Pricing (Gợi ý Chiến lược Giá Động - Yield Management)
Tối ưu hóa lợi nhuận kinh doanh bằng cách áp dụng giá linh hoạt tùy theo cung-cầu.
*   **Mô tả:** AI theo dõi Tốc độ đặt phòng (Booking Velocity). Khi một ngày trong tương lai (ví dụ dịp Lễ 2/9) có tỷ lệ cháy phòng nhanh hơn bình thường, AI sẽ ra cảnh báo.
*   **Ví dụ hiển thị:** *"Cảnh báo: Dịp lễ 2/9 còn 1 tháng nữa nhưng số phòng trống chỉ còn 10%. Tốc độ đặt phòng đang nhanh gấp 3 lần bình thường. Đề xuất AI: Đóng các kênh bán qua OTA (như Booking, Agoda) để tránh mất phí hoa hồng, và TĂNG GIÁ phòng bán trực tiếp lên 25%."*
*   **Độ khó:** Nâng cao.

### Level 5: Anomaly Detection (Phát hiện Bất thường Vận hành)
Công cụ quản trị rủi ro tự động, chống thất thoát.
*   **Mô tả:** AI phân tích các điểm dữ liệu và tự động khoanh đỏ (Highlight) các Outliers (Điểm bất thường) phá vỡ quy luật thống kê bình thường.
*   **Ví dụ hiển thị:** *"Cảnh báo Anomaly: Công suất phòng ngày 15/06 đạt 90% nhưng Doanh thu Nhà hàng (F&B) lại giảm 50% so với định mức trung bình. Có dấu hiệu thất thoát hoặc sự cố vận hành F&B. Yêu cầu kiểm tra."*
*   **Độ khó:** Khó.

---

## KIẾN TRÚC VÀ GIẢI PHÁP KỸ THUẬT (DÀNH CHO ĐỒ ÁN SWP391)

Để đảm bảo tính khả thi trong khuôn khổ thời gian của đồ án môn học, nhóm đề xuất tập trung triển khai **Level 1, Level 2 và Level 4**.

**1. Kiến trúc Backend (Spring Boot):**
*   Tạo ra một service chuyên biệt: `AIAnalyticsService.java`.
*   Sử dụng Spring Data JPA để tổng hợp số liệu thực tế (VD: Doanh thu 30 ngày qua, tỷ lệ lấp đầy).
*   Đóng gói số liệu thành định dạng JSON.

**2. Tích hợp AI (LLM API):**
*   Hệ thống không cần tự xây dựng model (tốn kém thời gian và tài nguyên máy chủ).
*   Sử dụng **Open Feign** hoặc **RestTemplate** trong Spring Boot để gọi REST API sang các mô hình ngôn ngữ lớn (LLM) như **Google Gemini API** hoặc **OpenAI API**.
*   **Prompt Engineering:** Xây dựng câu lệnh (Prompt) chuẩn hóa để LLM hiểu được nó đang đóng vai trò là "Chuyên gia phân tích dữ liệu Resort", truyền kèm mảng JSON chứa data.

**3. Frontend (Giao diện hiển thị):**
*   Nhận JSON Data do AI trả về.
*   Sử dụng thư viện **Chart.js** để vẽ biểu đồ đường (Line Chart) dự báo và biểu đồ cột.
*   Dùng HTML/CSS theo chuẩn bảng màu của hệ thống thiết kế "Aura Moon" để render box văn bản "AI Insights" ngay bên cạnh các biểu đồ lớn.

> **Kết luận:** Việc ứng dụng AI dưới dạng "Cố vấn dữ liệu" qua API LLM không chỉ giải quyết triệt để yêu cầu đổi mới của Giảng viên mà còn mang lại tính hiện đại, thực tiễn và dễ dàng áp dụng ngay trên kiến trúc Spring Boot hiện có của đồ án.
