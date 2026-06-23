# Tài Liệu Đặc Tả Use Case (Use Case Specification)

## Module 1: Authentication & Sensitive Health Profile

---

### 1. Thông tin chung

| Thuộc tính                            | Chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| :-------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Mã Use Case (ID)**             | UC02                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Tên Use Case**                 | Complete Health & Dietary Profile (Cập nhật Hồ sơ Sức khỏe & Dinh dưỡng)                                                                                                                                                                                                                                                                                                                                                                |
| **Actor chính (Primary Actor)**  | Guest (Khách hàng)                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Actor phụ (Secondary Actors)** | System (Hệ thống), Database (Cơ sở dữ liệu)                                                                                                                                                                                                                                                                                                                                                                                               |
| **Mô tả ngắn gọn**            | Use case này cho phép Guest tạo và cập nhật Hồ sơ Sức khỏe & Dinh dưỡng nhạy cảm (bao gồm tình trạng y tế, chấn thương, dị ứng, chế độ ăn). Dữ liệu này được sử dụng để cá nhân hóa trải nghiệm tại Khu nghỉ dưỡng, đồng thời phải đảm bảo tính riêng tư, quản lý sự đồng ý (explicit consent) theo Nghị định 356/2025/NĐ-CP và phân quyền truy cập nghiêm ngặt (RBAC). |

---

### 2. Các điều kiện (Conditions)

#### 2.1 Điều kiện tiên quyết (Preconditions)

* Tài khoản Guest đã được đăng ký và xác thực thành công (đang trong trạng thái đăng nhập).
* Trạng thái tài khoản của Guest là Active (Hoạt động).
* Guest đã chấp nhận Chính sách Quyền riêng tư chung của hệ thống.

#### 2.2 Điều kiện sau (Postconditions)

* Hồ sơ sức khỏe và dinh dưỡng của Guest được **mã hóa (AES-128)** và lưu trữ an toàn trong Database.
* Chỉ các nhân viên được phân quyền hợp lệ (Spa Therapist, Chef) mới được phép truy cập vào các phần thông tin tương ứng.
* Mọi thao tác cập nhật/lưu trữ được update date,time vào thuộc tính update_at của bảng tương ứng.

---

### 3. Luồng sự kiện (Flow of Events)

#### 3.1 Luồng cơ bản (Normal Flow)

1. Guest chọn menu/chức năng **"Health & Dietary Profile"** trên hệ thống.
2. hệ thống hiển thị biểu mẫu (Form) điền thông tin hồ sơ.
3. Hệ thống hiển thị **Tuyên bố đồng ý (Consent statement)** với hộp kiểm (checkbox) ở trạng thái **không được chọn sẵn (unchecked mặc định)** (Quy tắc BR-08).
4. Guest nhập các thông tin về **Chế độ ăn uống** (Dietary preferences).
5. Guest nhập thông tin về **Dị ứng thực phẩm** (Food allergies).
6. Guest nhập thông tin về **Tình trạng sức khỏe / Chấn thương vật lý** (Physical health conditions).
7. Guest tick chọn hộp kiểm để cấp quyền đồng ý rõ ràng (Explicit consent).
8. Guest nhấn nút **Gửi (Submit)**.
9. Hệ thống kiểm tra tính hợp lệ của dữ liệu (Validation).
10. Hệ thống thực hiện **Mã hóa (Encryption)** các trường dữ liệu nhạy cảm (Quy tắc BR-09).
11. Hệ thống lưu hồ sơ xuống Database (vào bảng `PHYSICAL_HEALTH_PROFILE`, `DIETARY_PROFILE` và lưu trạng thái vào bảng `CONSENT`).
12. Hệ thống ghi lại ngày, giờ cập nhật vào cột update_at
13. Hệ thống hiển thị thông báo cập nhật hồ sơ thành công.

#### 3.2 Luồng thay thế (Alternative Flows)

* **A1. Guest chỉ cung cấp hồ sơ dinh dưỡng:**
  * Guest bỏ trống phần thông tin sức khỏe vật lý, chỉ điền dị ứng và chế độ ăn.
  * Hệ thống vẫn tiến hành lưu thông tin dinh dưỡng, các trường sức khỏe vật lý được lưu rỗng (null/empty).
* **A2. Guest cập nhật hồ sơ đã có:**
  * Tại bước 2, hệ thống truy xuất hồ sơ hiện tại, giải mã dữ liệu và hiển thị lên Form.
  * Guest chỉnh sửa thông tin.
  * Hệ thống ghi đè giá trị mới, mã hóa lại và lưu lịch sử cập nhật (Update history).

#### 3.3 Luồng ngoại lệ (Exceptions)

* **E1. Không cấp quyền đồng ý (Consent not granted):**
  * Tại bước 8, nếu Guest nhấn Submit nhưng chưa tick vào hộp kiểm đồng ý.
  * Hệ thống từ chối lưu dữ liệu và hiển thị thông báo lỗi (MSG-03: "Bạn phải đồng ý với các điều khoản bảo mật dữ liệu y tế để tiếp tục").
* **E2. Lỗi lưu trữ hệ thống (Unexpected storage failure):**
  * Trong quá trình mã hóa hoặc kết nối Database bị gián đoạn.
  * Hệ thống hiển thị thông báo lỗi chung (MSG-15: "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau").

---

### 4. Quy tắc nghiệp vụ (Business Rules)

* **BR-07 (Role-Based Access Control and Data Minimization):** Phân quyền truy cập tối thiểu. Chef chỉ được xem dữ liệu Dinh dưỡng/Dị ứng. Spa Therapist chỉ được xem dữ liệu Sức khỏe vật lý. Lễ tân không được xem cả hai. (đã thực hiện)
* **BR-08 (Explicit Consent):** Sự đồng ý phải rõ ràng. Hộp kiểm (checkbox) xác nhận thu thập dữ liệu y tế tuyệt đối **không được tick sẵn**. (Tuân thủ Nghị định 356/2025).
* **BR-09 (Data Encryption):** Dữ liệu nhạy cảm phải được mã hóa tại Database (Encryption at rest).
* **BR-10 (Right to Deletion):** Guest có quyền yêu cầu xóa vĩnh viễn dữ liệu sức khỏe và dị ứng của họ ra khỏi hệ thống sau khi kết thúc kỳ nghỉ.
* **BR-15 (Audit Trail):** Mọi hành động lưu/cập nhật hồ sơ đều phải được lưu log hệ thống.

---

### 5. Yêu cầu giao diện người dùng (UI/UX Requirements)

* Giao diện Form chia làm 2 phần rõ rệt: **"Diet & Allergies"** và **"Physical Health Status"**.
* (Tùy chọn) Có nút để tải xuống dữ liệu cá nhân nếu Guest yêu cầu.
* (Tùy chọn) Giao diện phần Consent cần được làm nổi bật để Guest chú ý.

### 6. Cấu trúc Database liên quan

* `PHYSICAL_HEALTH_PROFILE`: Lưu `medical_conditions`, `injuries` (Mã hóa). update_at
* `DIETARY_PROFILE`: Lưu `food_allergies`, `diatary_preference` (Mã hóa). update_at
* `CONSENT`: Lưu `consent_status`, `consent_version`.
