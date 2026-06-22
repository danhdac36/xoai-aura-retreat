# Báo cáo Kiểm toán Mã nguồn UC08 (Check-in Guest)

Dưới đây là báo cáo chi tiết về các điểm sai lệch giữa Mã nguồn thực tế và Tài liệu Thiết kế (EDS) đối với luồng nghiệp vụ UC08.

## 🔴 1. Lỗi logic nghiêm trọng: Mã hóa 2 lần (Double Encryption)

> [!CAUTION]
> Dữ liệu CCCD/Passport đang bị mã hóa 2 lần, làm hỏng hoàn toàn dữ liệu gốc.

- **Yêu cầu Tài liệu (ADR-UC08-001)**: CCCD/Passport phải được mã hóa AES-256 trước khi lưu xuống DB.
- **Thực tế trong Code**:
  1. Trong file `CheckInServiceImpl.java`, hệ thống đang gọi hàm mã hóa thủ công: `encryptionService.encrypt(request.getIdentifyCode())`.
  2. Tuy nhiên, trong file Entity `User.java`, cột `identifyCode` đã được khai báo tự động mã hóa bằng JPA Converter: `@Convert(converter = AesDataEncryptor.class)`.
- **Hậu quả**: Chuỗi đã mã hóa từ Service bị JPA mã hóa thêm lần nữa trước khi lưu vào DB. Dữ liệu khi đọc lên sẽ bị hỏng, không thể giải mã để lấy ra số CCCD thực.

## 🟡 2. Thiếu trường dữ liệu `identifyType` (Lý do cần sửa Entity)

> [!WARNING]
> Không thể lưu được loại giấy tờ do thiếu cột trong Database.

- **Yêu cầu Tài liệu (Mục 5.2 - Data Structure)**: Bảng `User` phải lưu trữ 2 thông tin:
  - `identifyCode`: Số giấy tờ (bị mã hoá).
  - `identifyType`: Loại giấy tờ (để phân biệt CCCD hay PASSPORT).
- **Thực tế trong Code**: Cả `CheckInRequestDTO.java` và Entity `User.java` đều **không có trường identifyType**.
- **Hậu quả**: Khi lễ tân nhập số "079...", hệ thống lưu chuỗi này vào DB nhưng không có cột nào đánh dấu đây là CCCD. Về sau khi trích xuất danh sách lưu trú cho Cơ quan Công an, hệ thống không thể phân loại khách dùng CCCD (nội địa) và khách dùng Passport (quốc tế).
- **Giải pháp bắt buộc**: Phải thêm trường `private String identifyType;` vào file Entity `User.java` để JPA tự động tạo thêm cột này dưới Database.

## 🟡 3. Lỗ hổng Phân quyền & Sai lệch API Endpoint

> [!IMPORTANT]
> API Check-in đang bị cấu hình sai đường dẫn, dẫn đến việc không được bảo vệ bởi SecurityConfig.

- **Yêu cầu Tài liệu**: Phân quyền Role `RECEPTIONIST` qua endpoint `POST /receptionist/check-in`.
- **Thực tế trong Code**:
  - `SecurityConfig.java` đang chặn quyền theo chuỗi `"/receptionist/**"`.
  - Nhưng `CheckInController.java` lại đang khai báo API với path là `"/reception/checkin"`.
- **Hậu quả**: Chữ "reception" khác với "receptionist", do đó API Check-in hiện tại đang **vượt qua màng lọc phân quyền của Receptionist** trong `SecurityConfig.java`. 

---

## 🎯 Tổng hợp Đề xuất Sửa đổi (Implementation Plan)

Để Code chuẩn 100% theo Tài liệu EDS và vá lỗ hổng bảo mật, cần thực hiện 3 bước:

1. **Sửa Entity `User.java` & `CheckInRequestDTO.java`**: Thêm biến `identifyType` (String).
2. **Sửa `CheckInServiceImpl.java`**: 
   - Xóa bỏ dòng gọi thủ công `encryptionService.encrypt(...)`.
   - Cập nhật hàm để nhận thêm tham số `identifyType` từ DTO và set vào User.
3. **Sửa `CheckInController.java`**: Đổi `@RequestMapping("/reception")` thành `@RequestMapping("/receptionist")` và sửa các link redirect tương ứng.
