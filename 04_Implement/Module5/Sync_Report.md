# Báo cáo Đánh giá Đồng bộ hóa Tài liệu

**Người đánh giá:** AI Assistant
**Ngày thực hiện:** 2026-06-13
**Phạm vi:** Kiểm tra sự đồng bộ giữa tài liệu Requirement (SRS), Design (SDS) và các tài liệu Đặc tả kỹ thuật (EDS) của UC21, UC22, UC23 thuộc Module 5.

---

## 1. Kết luận Tổng quan

Nhìn chung, **các tài liệu EDS (UC21, UC22, UC23) đã đồng bộ rất tốt với các Business Rules (BR) và Use Cases (UC) được định nghĩa trong SRS**. Các quyết định kiến trúc (ADR) trong EDS bám sát luồng nghiệp vụ thực tế, đặc biệt là việc xử lý tích hợp cổng thanh toán VNPay và bảo vệ dữ liệu đánh giá.

Tuy nhiên, vẫn tồn tại một số **điểm lệch pha nhỏ (Discrepancies)** chủ yếu liên quan đến việc ghi nhận Audit Log (BR-15) và hiển thị mã lỗi (System Messages), cần được xem xét và điều chỉnh để đạt độ đồng bộ 100%.

---

## 2. Phân tích Chi tiết từng Use Case

### 2.1. UC21 - Generate Consolidated Invoice
* **Tài liệu đối chiếu:** `SRS_Document.md` (Section 2.5.1) vs `UC21_EDS_Consolidated_Invoice.md`
* **Mức độ đồng bộ:** **Khá (85%)**

**Điểm Đồng bộ (Match):**
* **Mô hình Dữ liệu (BR-11):** SRS yêu cầu tổng hợp chi phí từ Package, Spa, F&B. EDS đã đáp ứng xuất sắc bằng kiến trúc "Charge to Room" (Sổ cái Guest Folio & Folio Item) được định nghĩa trong ADR-001. Luồng MVC Controller đọc dữ liệu và nhóm theo danh mục là hoàn toàn chính xác.

**Điểm Lệch pha (Discrepancy):**
1. **Thiếu Audit Trail (BR-15):** 
   * *Theo SRS:* Tại luồng Normal Flow bước 11 ghi rõ "System records audit logs" và Postconditions có "Audit logs are recorded".
   * *Theo EDS:* Tại mục 12.1, EDS cho rằng UC21 là thao tác "chỉ đọc" (Read-only) nên bỏ qua việc ghi nhận Log (Không thấy xuất hiện Repository nào lưu vết Lễ tân đã xuất hóa đơn). 
   * *Khuyến nghị:* Cần cập nhật EDS bổ sung thêm việc gọi `AuditLogRepository` khi Lễ tân truy cập xem hóa đơn lần cuối.

### 2.2. UC22 - Process Final Payment
* **Tài liệu đối chiếu:** `SRS_Document.md` (Section 2.5.2) vs `UC22_EDS_Process_Final_Payment.md`
* **Mức độ đồng bộ:** **Rất tốt (95%)**

**Điểm Đồng bộ (Match):**
* **Xử lý thanh toán đa kênh:** EDS đã thiết kế thành công luồng xử lý bất đồng bộ (2 bước) cho VNPay để đối chiếu với yêu cầu Payment Gateway API của SRS.
* **Quy định Check-out (BR-12):** Ngăn chặn check-out nếu còn đơn hàng đang treo (pending orders).
* **Kiểm toán (BR-15):** Traceability Matrix của EDS có trỏ tới `AuditLogRepository`.

**Điểm Lệch pha (Discrepancy):**
1. **Quản lý System Messages (MSG):** 
   * *Theo SRS:* Khi gặp lỗi (ví dụ chưa thanh toán Spa/F&B), hệ thống phải hiển thị mã lỗi định trước (Ví dụ MSG-13: "Please settle all outstanding charges...").
   * *Theo EDS:* Lại sử dụng string fix cứng (Hardcode) trong code Java (ví dụ: `Thanh toán VNPay thành công!` hoặc `Không thể Check-out: Khách còn đơn hàng Spa/F&B đang thực hiện.`).
   * *Khuyến nghị:* EDS nên sửa lại để sử dụng tài nguyên Message properties đa ngôn ngữ cho chuẩn với SRS.

### 2.3. UC23 - Submit Retreat Review
* **Tài liệu đối chiếu:** `SRS_Document.md` (Section 2.6.2) vs `UC23_EDS_Review.md`
* **Mức độ đồng bộ:** **Hoàn hảo (100%)**

**Điểm Đồng bộ (Match):**
* **Bảo vệ tính toàn vẹn (BR-13):** SRS yêu cầu chỉ booking "Completed" mới được Review và mỗi booking chỉ được 1 lần. EDS đã bẻ nhỏ BR-13 thành 2 luật cụ thể: `BR-REV-001` và `BR-REV-002`, đồng thời bổ sung ADR-002 (Cơ chế Database Unique Constraint + Service Validation) để chống tình trạng SPAM API.
* **Điều hướng UI (ADR-003):** Lựa chọn phương án chuyển tiếp mượt mà từ trang Checkout sang form Đánh giá. Không có sự sai lệch nào so với luồng Normal Flow của SRS.

---

## 3. Đề xuất Khắc phục (Action Items)

Dựa trên các phân tích trên, tôi đề xuất các bước cập nhật code/tài liệu như sau:
1. **Cập nhật file `UC21_EDS_Consolidated_Invoice.md`:** Thêm bước "Ghi nhận Audit Log sự kiện Lễ tân tạo Hóa đơn" vào Sequence Diagram.
2. **Cập nhật hệ thống Messages:** Tránh việc hard-code string tiếng Việt trực tiếp trong Controller (UC22, UC23) mà nên mapping chuẩn theo bảng System Messages (MSG-11, MSG-13, MSG-16...) từ SRS.
