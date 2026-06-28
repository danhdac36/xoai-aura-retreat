# Tài Liệu Đặc Tả Use Case (Use Case Specification)

## Module 1: Authentication & Sensitive Health Profile

---

### 1. Thông tin chung

| Thuộc tính                            | Chi tiết                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| :-------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Mã Use Case (ID)**             | UC03                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Tên Use Case**                 | Manage Staff Accounts & Roles (Quản lý Tài khoản & Phân quyền Nhân viên)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **Actor chính (Primary Actor)**  | Administrator (Quản trị viên)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **Actor phụ (Secondary Actors)** | System (Hệ thống), Database (Cơ sở dữ liệu)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **Mô tả ngắn gọn**            | Use case này cho phép Administrator thực hiện các thao tác quản lý hồ sơ và tài khoản của nhân sự trong Resort (Therapist, Chef, Receptionist) (Tất cả thông tin có ở bảng [USER] + Role). Mục đích chính là thiết lập định danh và phân quyền chặt chẽ (RBAC) làm cơ sở để thực thi nguyên tắc Thu hẹp dữ liệu (Data Minimization) ở các module khác.  Và quả lý tài khoản (account) của khách hàng.Mọi thao tác đều được lưu Audit Log và chỉ sử dụng vô hiệu hóa (Deactivate/Soft Delete) thay vì xóa vật lý. |

---

### 2. Các điều kiện (Conditions)

#### 2.1 Điều kiện tiên quyết (Preconditions)

* Tài khoản Administrator đã được đăng nhập và xác thực thành công vào trang quản trị (Admin Portal).
* Trạng thái tài khoản của Administrator là Active (Hoạt động) và có Role là ADMIN.

#### 2.2 Điều kiện sau (Postconditions)

* Tài khoản nhân viên mới hoặc thông tin cập nhật được lưu thành công vào cơ sở dữ liệu.
* Quyền (Role) được gắn chính xác cho tài khoản để áp dụng bảo mật phân quyền.
* Hệ thống tạo bản ghi log trong bảng `AUDIT_LOG` để ghi nhận thông tin admin thực hiện, loại hành động và chi tiết.

---

### 3. Luồng sự kiện (Flow of Events)

#### 3.1 Luồng cơ bản (Normal Flow)

1. Administrator chọn menu **"Staff Management"** (Quản lý nhân viên) trên Admin Portal.
2. Hệ thống truy xuất cơ sở dữ liệu và hiển thị danh sách nhân viên hiện tại, bao gồm các thông tin: Họ tên, Email, Chức vụ/Role, Trạng thái (Active/Inactive).
3. Administrator có thể thực hiện một trong các thao tác: **Thêm mới (Create)**, **Cập nhật (Update)**, hoặc **Vô hiệu hóa (Deactivate)**.

##### 3.1.1 Thao tác Thêm mới (Create Staff)

1. Administrator click chọn **"Add New Staff"**.
2. Hệ thống hiển thị Form nhập thông tin:
   * **Thông tin cá nhân:** Họ tên, Số điện thoại, Căn cước công dân (CCCD).
   * **Thông tin tài khoản:** Email (sử dụng làm tài khoản đăng nhập), Mật khẩu khởi tạo.
   * **Phân quyền (Role):** Dropdown chọn 1 trong các quyền: `Therapist` (Chuyên viên trị liệu), `Chef` (Đầu bếp / F&B), `Receptionist` (Lễ tân), `Admin` (Quản trị).
3. Administrator điền đầy đủ thông tin và nhấn **"Save"**.
4. Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào (Ví dụ: Email không được trùng lặp).
5. Hệ thống mã hóa mật khẩu (Password Hashing) và lưu bản ghi vào Database với trạng thái mặc định `status = Active`.
6. Hệ thống ghi nhận hành động vào `AUDIT_LOG`.
7. Hệ thống hiển thị thông báo thành công và cập nhật danh sách.

##### 3.1.2 Thao tác Cập nhật (Update Staff)

1. Administrator click vào nút **"Edit"** của một nhân viên trên danh sách.
2. Hệ thống hiển thị Form chứa thông tin hiện tại (không hiển thị mật khẩu).
3. Administrator thay đổi thông tin cần thiết (VD: Đổi Role từ Receptionist sang Admin) và nhấn **"Save"**.
4. Hệ thống cập nhật bản ghi vào cơ sở dữ liệu, ghi lại thời gian `update_at`.
5. Hệ thống ghi nhận hành động thay đổi (so sánh dữ liệu cũ - mới) vào `AUDIT_LOG`.
6. Hệ thống hiển thị thông báo cập nhật thành công.

##### 3.1.3 Thao tác Vô hiệu hóa (Deactivate / Soft Delete)

1. Administrator click vào nút **"Deactivate"** (hoặc Toggle trạng thái Active -> Inactive) của nhân viên đã nghỉ việc.
2. Hệ thống hiển thị popup xác nhận để tránh click nhầm.
3. Administrator xác nhận.
4. Hệ thống chuyển đổi trạng thái tài khoản thành Inactive (không xóa vật lý khỏi DB).
5. (Tuỳ chọn) Hệ thống tự động vô hiệu hóa (revoke) các session đăng nhập hiện tại của nhân viên đó.
6. Hệ thống hiển thị thông báo thành công.

*3.1.3* Thao tác Xem chi tiết thông tin  (đối với nhân sự của hệ thống)

1. Admin click vào nút "Detail" của 1 nhân viên trên danh sách
2. Hệ thống hiển thị toàn bộ thông tin của nhân viên đó (Toàn bộ thông tin trong entity User)

---

#### 3.2 Luồng ngoại lệ (Exceptions)

* **E1. Trùng lặp Email (Duplicate Email):**
  * Tại bước kiểm tra tính hợp lệ, nếu Email nhập vào đã tồn tại trong hệ thống (dành cho một user/staff khác).
  * Hệ thống từ chối lưu, bôi đỏ ô Email và thông báo: *"Email này đã tồn tại trong hệ thống."*
* **E2. Cập nhật trạng thái Admin cuối cùng:**
  * Nếu Administrator đang cố gắng Vô hiệu hóa (Deactivate) chính tài khoản Admin duy nhất còn lại của hệ thống.
  * Hệ thống chặn hành động này để tránh việc hệ thống bị vô chủ (locked out).

---

### 4. Quy tắc nghiệp vụ (Business Rules)

* **BR-01 (Strict Role Binding):** Mỗi tài khoản nhân viên bắt buộc phải được gán ít nhất một Role (`Role_ID`). Role này là cơ sở duy nhất để hệ thống thực thi Data Minimization (RBAC) theo yêu cầu của HOS-03:
  * *Therapist*: Chỉ được cấp quyền truy cập tính năng xem lịch sử trị liệu/vật lý.
  * *Chef*: Chỉ được cấp quyền truy cập tính năng xem dị ứng thực phẩm.
  * *Receptionist*: Không được xem dữ liệu sức khỏe, chỉ xem booking và hóa đơn.
* **BR-02 (Historical Data Integrity):** Không được phép Hard Delete (xóa vật lý bằng lệnh `DELETE`) tài khoản nhân viên. Trị liệu viên (Therapist) có thể đã thực hiện các ca Spa trong quá khứ, nếu xóa sẽ làm hỏng dữ liệu báo cáo "Therapist Utilization" (UC25). Bắt buộc dùng cờ `is_active = 0` (hoặc `is_delete = 1`).
* **BR-03 (Audit Trail):** Mọi hành động Create/Update/Deactivate hồ sơ nhân sự phải được ghi vào `AUDIT_LOG` để truy vết bảo mật, nhất là khi phân quyền (Role) bị thay đổi.
* **BR-04 (Credential Security):** Mật khẩu của nhân viên phải được băm (hash) bằng thuật toán mạnh (VD: BCrypt/Argon2) trước khi lưu vào cơ sở dữ liệu.

---

### 5. Yêu cầu giao diện người dùng (UI/UX Requirements)

* Có bộ lọc (Filter) tìm kiếm nhân viên theo **Role** và **Trạng thái (Active/Inactive)** để dễ quản lý.
* Hiển thị rõ ràng (Badge color) cho các Role khác nhau trên danh sách (Ví dụ: Therapist màu Xanh, Chef màu Cam, Admin màu Đỏ).
* Các action nguy hiểm như "Deactivate" cần dùng màu đỏ và bắt buộc có confirm popup.

---

### 6. Cấu trúc Database liên quan (Dự kiến)

* Bảng `USER` / `ACCOUNT`: Lưu trữ thông tin đăng nhập (Email, Password Hash), thông tin cá nhân cơ bản và `is_active`.
* Bảng `ROLE`: Chứa danh mục các quyền hạn (Therapist, Chef, Receptionist, Admin).
* Bảng `USER_ROLE` (nếu N-N) hoặc cột `role_id` trong bảng `USER` (nếu 1-N).
* Bảng `AUDIT_LOG`: Ghi lại toàn bộ hành động (Action, Actor_ID, Timestamp, Details).
