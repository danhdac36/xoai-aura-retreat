# Tài Liệu Đặc Tả Use Case (Use Case Specification)

## Module 1: Authentication & Sensitive Health Profile

---

### 1. Thông tin chung

| Thuộc tính                            | Chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| :-------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Mã Use Case (ID)**             | UC04                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Tên Use Case**                 | Manage Master Data (Quản lý Dữ liệu Danh mục gốc)                                                                                                                                                                                                                                                                                                                                                                                               |
| **Actor chính (Primary Actor)**  | Administrator (Quản trị viên)                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Actor phụ (Secondary Actors)** | System (Hệ thống), Database (Cơ sở dữ liệu)                                                                                                                                                                                                                                                                                                                                                                                               |
| **Mô tả ngắn gọn**            | Use case này cho phép Administrator thực hiện các thao tác quản lý dữ liệu danh mục gốc bao gồm Hạng Villa (Villa Categories), Dịch vụ Trị liệu (Spa Services), Gói Nghỉ dưỡng (Retreat Packages) và Hồ sơ Nhân viên (Staff Records) để phục vụ cho các hoạt động vận hành, đặt phòng, đặt lịch trị liệu và thanh toán của resort. Mọi thay đổi về dữ liệu danh mục này phải được lưu vết lịch sử (Audit Log) và áp dụng Soft Delete để bảo vệ tính toàn vẹn của dữ liệu lịch sử đặt chỗ. |

---

### 2. Các điều kiện (Conditions)

#### 2.1 Điều kiện tiên quyết (Preconditions)

* Tài khoản Administrator đã được đăng nhập và xác thực thành công vào trang quản trị (Admin Portal).
* Trạng thái tài khoản của Administrator là Active (Hoạt động) và có vai trò (Role) tương ứng là ADMIN.

#### 2.2 Điều kiện sau (Postconditions)

* Các thông tin thêm mới, cập nhật hoặc xóa mềm đối với Hạng Villa, Dịch vụ Trị liệu, Gói Nghỉ dưỡng và Hồ sơ Nhân viên được lưu trữ thành công vào Database.
* Thuộc tính `update_at` (nếu có) của bản ghi tương ứng được tự động cập nhật thời gian thay đổi.
* Hệ thống tạo bản ghi log trong bảng `AUDIT_LOG` để ghi nhận thông tin tài khoản admin thực hiện và chi tiết thay đổi.
* Dữ liệu danh mục mới cập nhật được đồng bộ tức thì lên giao diện đặt phòng (Module 2), đặt lịch Spa (Module 3) và menu bữa ăn (Module 4).

---

### 3. Luồng sự kiện (Flow of Events)

#### 3.1 Luồng cơ bản (Normal Flow)

1. Administrator chọn menu **"Master Data Management"** trên Admin Portal.
2. Hệ thống hiển thị các tùy chọn danh mục cần quản trị:
   * **Villa Categories (Hạng Villa)**
   * **Spa Services (Dịch vụ Trị liệu)**
   * **Retreat Packages (Gói Nghỉ dưỡng)**
   * **Staff Records (Hồ sơ Nhân viên)**
3. Administrator chọn một danh mục cụ thể (ví dụ: *Spa Services*).
4. Hệ thống truy xuất cơ sở dữ liệu và hiển thị danh sách các bản ghi hiện tại của danh mục đó có trạng thái chưa xóa (`is_delete = 0`).
5. Administrator có thể thực hiện một trong các thao tác: **Thêm mới (Create)**, **Cập nhật (Update)**, hoặc **Xóa mềm (Soft Delete)**.

##### 3.1.1 Thao tác Thêm mới (Create)
1. Administrator click chọn **"Add New"**.
2. Hệ thống hiển thị Form nhập thông tin tương ứng:
   * **Đối với Hạng Villa:** Tên hạng villa, ảnh đại diện, giá phòng mỗi ngày.
   * **Đối với Dịch vụ Trị liệu:** Mã dịch vụ (Unique Code), tên dịch vụ, thời lượng (phút), đơn giá, trạng thái khả dụng.
   * **Đối với Gói Nghỉ dưỡng:** Tên gói, loại gói, số ngày nghỉ, mô tả chi tiết, giá trọn gói, danh sách dịch vụ đi kèm (tuân thủ tiêu chuẩn GWI).
   * **Đối với Hồ sơ Nhân viên:** Mã nhân viên, họ tên, vai trò (Therapist, Chef, Receptionist), trạng thái làm việc.
3. Administrator điền đầy đủ thông tin và nhấn **"Save"**.
4. Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào (Validation).
5. Hệ thống lưu bản ghi mới vào Database với giá trị mặc định của `is_delete = 0`.
6. Hệ thống tự động ghi nhận hành động thêm mới vào `AUDIT_LOG` (Mã hành động: `CREATE_MASTER_DATA`, lưu actor_id, details).
7. Hệ thống hiển thị thông báo thêm mới thành công và cập nhật lại danh sách hiển thị.

##### 3.1.2 Thao tác Cập nhật (Update)
1. Administrator click vào nút **"Edit"** của bản ghi cần chỉnh sửa trên danh sách.
2. Hệ thống hiển thị Form chứa thông tin hiện tại của bản ghi.
3. Administrator thay đổi các thông tin cần chỉnh sửa (không được phép sửa mã định danh duy nhất/Unique Code nếu quy tắc hệ thống cấm) và nhấn **"Save"**.
4. Hệ thống kiểm tra tính hợp lệ của dữ liệu mới nhập.
5. Hệ thống cập nhật các giá trị thay đổi vào cơ sở dữ liệu và lưu thời gian thay đổi vào cột `update_at`.
6. Hệ thống tự động ghi nhận hành động cập nhật vào `AUDIT_LOG` (Mã hành động: `UPDATE_MASTER_DATA`, lưu thông tin trước/sau thay đổi ở cột `details`).
7. Hệ thống hiển thị thông báo cập nhật thành công và làm mới danh sách.

##### 3.1.3 Thao tác Xóa mềm (Soft Delete)
1. Administrator click vào nút **"Delete"** của bản ghi cần xóa.
2. Hệ thống hiển thị popup xác nhận: *"Bạn có chắc chắn muốn xóa danh mục này? Hành động này sẽ không hiển thị danh mục trong hệ thống đặt chỗ nhưng vẫn giữ dữ liệu lịch sử."*
3. Administrator xác nhận đồng ý xóa.
4. Hệ thống thực hiện cập nhật cột `is_delete = 1` của bản ghi trong Database (không xóa vật lý để đảm bảo không lỗi khóa ngoại đối với các booking/lịch trình cũ đã liên kết).
5. Hệ thống tự động ghi nhận hành động xóa vào `AUDIT_LOG` (Mã hành động: `DELETE_MASTER_DATA`).
6. Hệ thống hiển thị thông báo xóa thành công và loại bỏ bản ghi ra khỏi danh sách hiển thị.

---

#### 3.2 Luồng thay thế (Alternative Flows)

* **A1. Thay đổi trạng thái khả dụng (is_available / is_active):**
  * Tại màn hình danh sách, Administrator có thể click nhanh vào nút bật/tắt (Toggle) để kích hoạt hoặc tạm dừng dịch vụ/gói nghỉ dưỡng mà không cần vào Form chỉnh sửa chi tiết.
  * Hệ thống cập nhật trường `is_available` hoặc `is_active` tương ứng trong DB, lưu log hành động và phản hồi thành công nhanh trên giao diện.

---

#### 3.3 Luồng ngoại lệ (Exceptions)

* **E1. Trùng mã danh mục duy nhất (Duplicate Unique Code):**
  * Tại bước kiểm tra tính hợp lệ khi Thêm mới/Cập nhật, nếu mã dịch vụ (`treatment_code`) hoặc thông tin ràng buộc duy nhất đã tồn tại cho một bản ghi khác (kể cả bản ghi đã bị soft-delete trước đó tùy theo thiết kế).
  * Hệ thống từ chối lưu dữ liệu, báo đỏ tại trường nhập liệu và hiển thị thông báo lỗi (ví dụ: *"Mã dịch vụ này đã tồn tại trên hệ thống. Vui lòng nhập mã khác"*).
* **E2. Dữ liệu số không hợp lệ (Invalid Numeric Data):**
  * Administrator nhập giá trị âm (`< 0`) cho đơn giá (`price`, `price_per_day`) hoặc thời gian (`duration_minutes`, `duration_days`).
  * Hệ thống ngăn chặn việc gửi form và hiển thị cảnh báo lỗi định dạng tương ứng.
* **E3. Lỗi ràng buộc hệ thống khi xóa (Soft delete safety check):**
  * Trong trường hợp hệ thống phát hiện hạng villa hoặc dịch vụ đang nằm trong một Booking/Schedule đang hoạt động ở trạng thái kích hoạt (chưa checkout/chưa hoàn thành).
  * Hệ thống có thể đưa ra cảnh báo bổ sung trước khi ẩn danh mục đó đi để tránh ảnh hưởng tới vận hành thời gian thực.

---

### 4. Quy tắc nghiệp vụ (Business Rules)

* **BR-13 (Soft Delete Integration):** Tuyệt đối không sử dụng lệnh `DELETE` vật lý trên cơ sở dữ liệu đối với ba bảng `VILLA_TYPE`, `TREATMENT_SERVICE`, và `RETREAT_PACKAGE`. Bắt buộc dùng cờ `is_delete = 1` để đảm bảo báo cáo doanh thu lịch sử (Module 5) và hóa đơn cũ (Guest Folio) không bị mất liên kết dữ liệu.
* **BR-15 (Audit Trail Management):** Mọi hành động thêm, sửa, xóa trên Master Data đều phải được ghi vết hoạt động vào bảng `AUDIT_LOG`. Log phải ghi rõ ai thực hiện, loại hành động và mốc thời gian thực hiện.
* **BR-16 (Master Data Validation):**
  * Đơn giá dịch vụ và giá gói phải lớn hơn hoặc bằng 0.
  * Thời gian trị liệu (`duration_minutes`) và thời gian lưu trú gói (`duration_days`) phải lớn hơn 0.
  * Mã code dịch vụ trị liệu (`treatment_code`) phải là duy nhất và không được có khoảng trắng.
* **BR-17 (Global Wellness Institute - GWI Standards Compliance):**
  * Theo chuẩn GWI (Taxonomy of Wellness Tourism), danh mục Gói Nghỉ dưỡng (Retreat Packages) bắt buộc phải sử dụng các thuật ngữ chuyên ngành chuẩn quốc tế (ví dụ: *Mindfulness Retreat, Detoxification, Ayurveda*) thay vì các thuật ngữ khách sạn thông thường.

---

### 5. Yêu cầu giao diện người dùng (UI/UX Requirements)

* Giao diện Admin Portal cần cung cấp các tab/menu chuyển đổi mượt mà giữa 4 danh mục con.
* Có thanh tìm kiếm theo Tên hoặc Mã code và bộ lọc trạng thái (Active/Inactive) để dễ dàng quản trị lượng danh mục lớn.
* Các nút hành động (Edit, Delete, Add) cần được hiển thị rõ ràng và có xác nhận popup đối với các hành động nguy hiểm như Xóa.

---

### 6. Cấu trúc Database liên quan

* `VILLA_TYPE`: Quản lý các thuộc tính `type_name`, `image`, `price_per_day`, `is_delete`.
* `TREATMENT_SERVICE`: Quản lý các thuộc tính `treatment_code`, `service_name`, `duration_minutes`, `price`, `is_available`, `is_delete`.
* `RETREAT_PACKAGE`: Quản lý các thuộc tính `type_package`, `package_name`, `duration_days`, `services`, `description`, `is_active`, `is_delete`, `price`, `create_at`, `update_at`.
* `[USER]` / `THERAPIST`: Quản lý hồ sơ nhân viên và trạng thái làm việc.
* `AUDIT_LOG`: Ghi vết các hành động quản trị hệ thống.
