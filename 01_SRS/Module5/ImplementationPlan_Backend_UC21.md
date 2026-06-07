# Kế hoạch Triển khai Backend: Logic Xử lý Hóa đơn gộp (UC21)

## 1. Phân tích Dữ liệu & Nghiệp vụ (Tiêu chuẩn "Charge to Room")
1. **Kiểm soát dòng tiền (Giữ nguyên DB):** Dựa theo tài liệu phân tích hệ thống (SRS), luồng tiền được thiết kế tối giản: Khách hàng thanh toán **tối đa 2 lần** (Lúc đặt cọc và Lúc Check-out). Do đó, tổng tiền trong bảng `PAYMENT` tại thời điểm trước khi Check-out được mặc định hiểu là **Tiền cọc**. Không cần thêm trường `payment_type`.
2. **Dịch vụ phát sinh (FOLIO_ITEM):** Bảng `FOLIO_ITEM` là nơi duy nhất ghi nhận toàn bộ "trải nghiệm/dịch vụ" khách đã sử dụng (Ghi nợ phòng). Giao diện sẽ hiển thị tất cả các item này chia theo `service_category` để khách hàng đối soát.
3. **Công thức tính Nợ cuối (Sổ cái kép):** Việc quy về một vài bảng cố định giúp truy vấn đơn giản và chính xác:
   - **Tổng chi phí (Total Cost):** = `Tiền gói (GUEST_FOLIO)` + `SUM(TẤT CẢ Folio_Item.amount)`.
   - **Tiền cọc / Đã thanh toán (Deposit/Total Paid):** = `SUM(TẤT CẢ Payment.amount WHERE status = 'SUCCESS')`.
   - **Cần thanh toán nốt (Balance Due):** = `Tổng chi phí` - `Tiền cọc`.

---

## 2. Luồng Triển khai Code (Execution Flow)

Dưới đây là sơ đồ luồng dữ liệu và thiết kế các layer mã nguồn từ Database lên đến UI.

### Bước 1: Data Access Layer (Repository)
Tạo 3 interface kế thừa `JpaRepository` trong package `billing/repository`:
- **`GuestFolioRepository`**: Viết hàm `Optional<GuestFolio> findByBookingId(Integer bookingId);`
- **`FolioItemRepository`**: Viết hàm `List<FolioItem> findByGuestFolio(GuestFolio guestFolio);`
- **`PaymentRepository`**: Viết hàm `List<Payment> findByGuestFolioAndStatus(GuestFolio guestFolio, String status);` (Để query các khoản đã đặt cọc thành công).

### Bước 2: Data Transfer Object (DTO)
Tạo một DTO `CheckoutViewDTO` ở package `billing/dto` để đóng gói gọn gàng toàn bộ dữ liệu trả về cho Frontend, tránh nhồi nhét quá nhiều Object lẻ tẻ vào `Model`. Thiết kế này sử dụng Map để dễ dàng mở rộng các loại dịch vụ phát sinh.

```java
public class CheckoutViewDTO {
    private GuestFolio folio;
    
    // Lịch sử thanh toán
    private List<Payment> payments; 
    private BigDecimal totalPaid; // TỔNG ĐÃ THANH TOÁN (Từ bảng Payment)
    
    // Dịch vụ phát sinh (Gom nhóm tự động linh hoạt)
    private Map<String, List<FolioItem>> groupedExtraServices; 
    
    private BigDecimal totalCost; // TỔNG CHI PHÍ = Tiền gói + Tổng Folio Items
    private BigDecimal balanceDue; // CẦN THANH TOÁN THÊM = Total Cost - Total Paid
}
```

### Bước 3: Business Logic Layer (Service)
Tạo `BillingService` và `BillingServiceImpl`. Viết hàm `getCheckoutData(Integer bookingId)` thực hiện luồng sau:
1. Lấy `GuestFolio` bằng `bookingId`.
2. Lấy toàn bộ `FolioItem` thuộc Folio. Dùng Java Stream `Collectors.groupingBy(FolioItem::getServiceCategory)` để tự động gom nhóm thành `groupedExtraServices`. Tính tổng tiền các FolioItem này.
3. Lấy toàn bộ `Payment` có `status == 'SUCCESS'` thuộc Folio. Tính tổng `totalPaid`.
4. Tính `totalCost` = `folio.totalPackageAmount` + Tổng tiền dịch vụ phát sinh.
5. Tính `balanceDue` = `totalCost` - `totalPaid`.
6. Đóng gói tất cả vào `CheckoutViewDTO` và `return`.

### Bước 4: Presentation Layer (Controller)
Cập nhật `CheckoutController`:
```java
@GetMapping("/checkout")
public String showCheckoutPage(@RequestParam Integer bookingId, Model model) {
    CheckoutViewDTO data = billingService.getCheckoutData(bookingId);
    model.addAttribute("data", data);
    model.addAttribute("pageTitle", "Hóa đơn Gộp & Check-out");
    return "billing/checkout";
}
```

### Bước 5: UI Layer (Thymeleaf - `checkout.html`)
Sửa HTML tĩnh thành thẻ động (Dynamic Binding) truy xuất từ object `data`:
- **Hiển thị Dịch vụ động:** `<th:block th:each="entry : ${data.groupedExtraServices}">`
  - Tiêu đề nhóm: `<h3 th:text="${entry.key}"></h3>`
  - In giá: `<td th:text="${item.amount} + ' VND'"></td>`
  - Kiểm tra đã thanh toán chưa: `<span th:if="${item.status == 'PAID'}" class="badge">Đã thanh toán</span>`
- **Tóm tắt (Cột phải):** Hiển thị các biến `data.totalCost`, `data.totalPaid`, `data.balanceDue` bằng `th:text`.
