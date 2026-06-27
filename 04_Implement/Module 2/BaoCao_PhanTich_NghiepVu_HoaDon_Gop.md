# Báo Cáo Phân Tích Nghiệp Vụ Hóa Đơn, F&B và Spa (Guest Folio & Services)

**Tài liệu phân tích nghiệp vụ gộp hóa đơn (Folio Consolidation) và bóc tách dịch vụ Ẩm thực (F&B), Trị liệu (Spa) đối với nhóm khách hàng.**

## 1. Bản chất của việc "Gộp thành 1" trong nghiệp vụ Resort (PMS)

Trong ngành khách sạn/resort, việc tách chi tiết dịch vụ ra từng người ở bước đặt phòng hoàn toàn không mâu thuẫn với việc gộp chung lại thành 1 hóa đơn tổng (Master Folio) để khách thanh toán một lần lúc Check-out.

- **GuestFolio (Tầng Cha - Hóa đơn tổng):** Hệ thống sinh ra **DUY NHẤT 1 `GuestFolio`** gắn liền với `Booking` gốc. Hóa đơn mang tên người đại diện thanh toán.
- **FolioItem (Tầng Con - Dòng chi tiết):** Bên trong 1 `GuestFolio`, hệ thống sẽ gom tất cả dịch vụ mà từng người đã sử dụng (từ dữ liệu `BookingDetail`) đổ vào thành các dòng `FolioItem` chi tiết.

## 2. Nghiệp vụ vận hành phân hệ Trị liệu (Spa Module)

- **Tạo vé Spa (TreatmentBooking):** Sinh ra dựa trên `BookingDetail` (của từng người).
- **Vận hành:** Kỹ thuật viên Spa nhận lịch phải biết rõ vị khách tên gì, thuộc phòng nào, đang dùng gói nào để làm đúng liệu trình.
- **Tính phí phát sinh:** Mua thêm dịch vụ ngoài gói sẽ tạo `FolioItem` mới gắn vào tên vị khách đó.

## 3. Nghiệp vụ vận hành phân hệ Ẩm thực (F&B Module)

- **Hồ sơ ăn kiêng (Dietary Profile):** F&B phải nhìn thấy `Dietary Profile` của từng cá nhân để chuẩn bị suất ăn (VD: Khách A ăn Detox, Khách B ăn mặn).
- **Tính phí phát sinh:** Các món gọi ngoài (rượu, nước ngọt) được ghi nhận thành `FolioItem` F&B, cộng dồn vào hóa đơn tổng.

## 4. Thiết Kế Mẫu Hóa Đơn Thanh Toán Chuẩn (Guest Folio Design)

Dưới đây là bản thiết kế giao diện Hóa đơn thanh toán (Guest Folio) chuẩn nghiệp vụ ngành Khách sạn/Resort (theo chuẩn các PMS quốc tế như Opera, Smile). Mẫu này thể hiện rõ việc gộp tổng thanh toán nhưng tách bạch chi tiết từng cá nhân và tính thuế riêng biệt.

```text
================================================================================
                            AURAMOON RETREAT RESORT
                      123 Wellness Street, Da Lat, Vietnam
                              Tel: +84 263 123 456
                              VAT No: 0123456789
================================================================================

GUEST FOLIO / HÓA ĐƠN THANH TOÁN

Tên khách đại diện (Guest Name)  : Nguyễn Văn A          Số Folio (Folio No.) : F202606-1025
Số phòng (Room No.)              : Villa VIP 01          Ngày in (Date)       : 26/06/2026
Ngày đến (Arrival)               : 24/06/2026            Thu ngân (Cashier)   : LeTan_01
Ngày đi (Departure)              : 26/06/2026            Trang (Page)         : 1 of 1
Số lượng khách (No. of Guests)   : 3 Người lớn

--------------------------------------------------------------------------------
Ngày       | Mã DV   | Diễn giải (Description)                     | Phát sinh (VND)| Thanh toán
--------------------------------------------------------------------------------
24/06/2026 | RM      | Tiền phòng Villa VIP (Đêm 1)                |   7,500,000 | 
25/06/2026 | RM      | Tiền phòng Villa VIP (Đêm 2)                |   7,500,000 | 
24/06/2026 | PKG-DTX | Gói Detox 3 ngày (Khách: Nguyễn Văn A)      |   3,000,000 |
24/06/2026 | PKG-YGA | Gói Yoga Thiền (Khách: Trần Thị B)          |   2,500,000 |
24/06/2026 | PKG-HRB | Gói Trị liệu Thảo dược (Khách: Lê Văn C)    |   3,500,000 |
25/06/2026 | FB-RES  | Nhà hàng: 1 x Rượu vang (Khách: Trần Thị B) |   1,000,000 |
26/06/2026 | SPA-EX  | Spa: 60p Massage thêm (Khách: Nguyễn Văn A) |     500,000 |
26/06/2026 | PMT-CC  | Thanh toán qua Thẻ Tín Dụng (Visa - 42** )  |             |  25,500,000
--------------------------------------------------------------------------------
                                           TỔNG PHÁT SINH (Total Charges):  25,500,000
                                           TỔNG THANH TOÁN (Total Credits): 25,500,000
                                           SỐ DƯ CẦN THANH TOÁN (Balance):           0

Phân tích Thuế (Tax Breakdown):
- Doanh thu không chịu thuế (Non-Taxable)    :           0
- Doanh thu Dịch vụ Spa & F&B (VAT 10%)      :  10,500,000  -> Thuế:  1,050,000
- Doanh thu Tiền phòng (VAT 8%)              :  15,000,000  -> Thuế:  1,200,000

Ký tên Khách hàng (Guest Signature)                       Ký tên Thu ngân (Cashier Signature)

___________________________________                       ___________________________________
```

## 5. Tại sao phải làm theo cấu trúc "Gộp tổng nhưng tách chi tiết"?

1. **Vận hành chuẩn xác (Operation):** Đảm bảo nhà bếp (F&B) nấu đúng món, Kỹ thuật viên (Spa) làm đúng liệu trình cho từng người trong cùng 1 căn Villa.
2. **Dễ dàng xuất hóa đơn VAT (Hóa đơn đỏ):** Như trên mẫu thiết kế, hệ thống bóc tách được dòng nào chịu thuế 8% (Tiền phòng), dòng nào chịu thuế 10% (Spa, F&B) để xuất VAT chính xác cho cục Thuế.
3. **Minh bạch khách hàng:** Khách cầm hóa đơn lên thấy rõ ai dùng Gói gì, ai phát sinh thêm chi phí gì, tránh tranh cãi lúc Check-out.
4. **Hỗ trợ tách bill (Split Bill) khi cần:** Nếu Khách B muốn tự trả tiền riêng, Lễ tân chỉ việc chọn các dòng `FolioItem` có tên Khách B và chuyển sang một `GuestFolio` mới.
