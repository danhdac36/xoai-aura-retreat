# Kế hoạch Triển khai Mã nguồn: UC22 (Process Final Payment) Toàn diện

Dưới đây là kế hoạch code chi tiết, kết hợp luồng xử lý 2 bước của VNPay vào kiến trúc các Layer chuẩn xác (Repository, Service, Controller, UI) mà không làm mất đi các thành phần nghiệp vụ cốt lõi của UC22.

---

## 1. Kế hoạch triển khai Backend

### Bước 1. Data Access Layer (Repositories)
Đảm bảo khai báo đủ các hàm JPA cần thiết để kiểm tra trạng thái và lưu trữ giao dịch:
*   **`PaymentRepository`**: Sử dụng `save(Payment payment)` để tạo hoặc cập nhật bản ghi thanh toán.
*   **`FolioItemRepository`**: Viết hàm `boolean existsByGuestFolioAndStatusIn(GuestFolio folio, List<String> statuses)` để kiểm tra nhanh xem có đơn hàng nào đang `PENDING` hoặc `PREPARING` hay không (Phục vụ chặn lỗi BR-12).
*   Các repository khác (`BookingRepository`, `VillaRepository`, `GuestFolioRepository`) sẽ sử dụng JPA mặc định (như `findById()`) để tự động cập nhật (Dirty Checking) thông qua hàm `@Transactional` của Service.

### Bước 2. Business Logic Layer (Services)
Chia làm 2 service tách biệt để đảm bảo tính Single Responsibility:

**1. `CheckoutService` (hoặc `BillingService`):**
*   **`initiatePayment(Integer bookingId, String paymentMethod, String paymentGateway)`**: 
    * Lấy `GuestFolio` để tính tổng tiền còn nợ (Balance Due).
    * Quét BR-12 bằng `FolioItemRepository`. Nếu vi phạm, ném `PendingOrdersExistException`.
    * Tạo Entity `Payment` với trạng thái `PENDING`. Lưu vào DB.
    * Trả về `Payment` (hoặc `paymentId`).
*   **`completePaymentAndCheckout(Integer paymentId, String transactionCode)` (Gắn `@Transactional`)**:
    * Lấy `Payment` từ DB. Sửa status thành `SUCCESS`.
    * Lấy `GuestFolio`, sửa status thành `PAID`.
    * Lấy `Booking`, sửa status thành `COMPLETED`.
    * Lấy `Villa`, sửa status thành `VACANT_NEEDS_CLEANING`.
*   **`markPaymentAsFailed(Integer paymentId)`**:
    * Sửa Payment status thành `FAILED`.

**2. `VNPayService` và cấu hình hệ thống:**
*   **`application-secret.properties`**: Yêu cầu khai báo cấu hình môi trường Sandbox: `vnp_TmnCode` và `vnp_HashSecret` do VNPay cấp vào file bí mật này. Phải sử dụng `.gitignore` để tránh lộ API Key lên git. File `application.properties` sẽ dùng `spring.config.import` để nạp các biến môi trường này.
*   **`VNPayConfig.java`**: Lớp tiện ích (Utility) độc lập, chứa thuật toán mã hóa dữ liệu `hmacSHA512` theo chuẩn VNPay, và các hàm sắp xếp mảng dữ liệu (Alphabetical Sort) để đảm bảo chữ ký tạo ra chính xác.
*   **`VNPayService.java`**: 
    *   `createPaymentUrl(...)`: Khởi tạo Map dữ liệu với ít nhất 12 tham số bắt buộc của hệ thống (`vnp_Version`, `vnp_Command`, `vnp_TmnCode`, `vnp_Amount`, `vnp_CreateDate`, `vnp_TxnRef`,...). Mã hóa toàn bộ chuỗi này để sinh ra trường `vnp_SecureHash` hợp lệ và nối vào cuối URL.
    *   `verifySignature(...)`: Băm lại (Re-hash) toàn bộ chuỗi query param do IPN/Return trả về và đem so sánh với chữ ký `vnp_SecureHash` trong request để xác nhận tính toàn vẹn.

### Bước 3. Exceptions & Error Handling
Tạo Custom Exception:
*   `PendingOrdersExistException` (kế thừa `RuntimeException`): Ném ra khi vấp phải BR-12.

### Bước 4. Presentation Layer (Controller)
Trong `CheckoutController`, chúng ta xử lý riêng luồng Khởi tạo và luồng Trả kết quả:

**Endpoint 1: Xử lý Form Submit**
```java
@PostMapping("/checkout/{bookingId}/pay")
public String processPayment(@PathVariable Integer bookingId, @RequestParam String paymentMethod, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    try {
        String gateway = "CASH".equals(paymentMethod) ? "CASH" : "VNPAY";
        Payment payment = checkoutService.initiatePayment(bookingId, paymentMethod, gateway);
        
        if ("CASH".equals(paymentMethod)) {
            // Tiền mặt: Chốt luôn
            checkoutService.completePaymentAndCheckout(payment.getId(), null);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán Tiền mặt thành công!");
            return "redirect:/checkout/" + bookingId + "/success";
        } else {
            // VNPay: Sinh URL và Redirect (Hỗ trợ Reverse Proxy / Ngrok)
            String scheme = request.getHeader("X-Forwarded-Proto") != null ? request.getHeader("X-Forwarded-Proto") : request.getScheme();
            String host = request.getHeader("X-Forwarded-Host") != null ? request.getHeader("X-Forwarded-Host") : request.getServerName();
            String port = "";
            if (request.getHeader("X-Forwarded-Host") == null && request.getServerPort() != 80 && request.getServerPort() != 443) {
                port = ":" + request.getServerPort();
            }
            String baseUrl = scheme + "://" + host + port;
            String returnUrl = baseUrl + "/checkout/vnpay-return";
            String vnpayUrl = vnPayService.createPaymentUrl(payment.getAmount(), payment.getId(), returnUrl);
            return "redirect:" + vnpayUrl;
        }
    } catch (PendingOrdersExistException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Không thể Check-out: Khách còn đơn hàng Spa/F&B đang thực hiện.");
        return "redirect:/checkout?bookingId=" + bookingId;
    }
}
```

**Endpoint 2: Xử lý VNPay Callback**
```java
@GetMapping("/checkout/vnpay-return")
public String vnpayReturn(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
    if (vnPayService.verifySignature(params)) {
        Integer paymentId = Integer.parseInt(params.get("vnp_TxnRef"));
        if ("00".equals(params.get("vnp_ResponseCode"))) {
            checkoutService.completePaymentAndCheckout(paymentId, params.get("vnp_TransactionNo"));
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán VNPay thành công!");
            // Redirect về một trang hóa đơn hoàn tất
            return "redirect:/checkout/success?paymentId=" + paymentId; 
        } else {
            checkoutService.markPaymentAsFailed(paymentId);
            redirectAttributes.addFlashAttribute("errorMessage", "Khách hàng hủy giao dịch hoặc thẻ lỗi.");
        }
    } else {
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi bảo mật chữ ký VNPay!");
    }
    // Trở về trang checkout cũ để khách làm lại
    return "redirect:/checkout?bookingId=" + lấy_từ_paymentId; 
}
```

---

## 2. Kế hoạch triển khai Frontend

Tại file `src/main/resources/templates/billing/checkout.html` (được thừa hưởng từ UC21), chúng ta bổ sung các yếu tố sau:

### Gắn Flash Messages (Báo cáo lỗi/thành công)
Ngay dưới thanh Header, thêm khối thẻ Thymeleaf:
```html
<div th:if="${successMessage}" class="bg-green-100 border-green-400 text-green-700 p-4 rounded mb-4" role="alert">
    <span th:text="${successMessage}"></span>
</div>
<div th:if="${errorMessage}" class="bg-red-100 border-red-400 text-red-700 p-4 rounded mb-4" role="alert">
    <span th:text="${errorMessage}"></span>
</div>
```

### Chuyển đổi khối Thanh toán thành Form Submit hợp lệ
Bao bọc khu vực chọn phương thức và nút bấm ở Cột Phải bằng một `<form>` POST:
```html
<form th:action="@{/checkout/{id}/pay(id=${data.folio.booking.id})}" method="POST">
    <!-- CSRF Token -->
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />

    <div class="mt-4 space-y-2">
        <label class="flex items-center space-x-2">
            <input type="radio" name="paymentMethod" value="CASH" required />
            <span>Tiền mặt (Thu tại quầy)</span>
        </label>
        <label class="flex items-center space-x-2">
            <input type="radio" name="paymentMethod" value="VNPAY" required />
            <span>Thanh toán VNPay (Quét mã QR/Thẻ ATM)</span>
        </label>
    </div>

    <!-- Nút Submit -->
    <button type="submit" class="w-full bg-blue-600 text-white rounded py-2 mt-4 hover:bg-blue-700">
        Hoàn tất Thanh toán & Check-out
    </button>
</form>
```
