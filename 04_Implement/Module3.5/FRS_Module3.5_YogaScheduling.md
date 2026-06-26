# ĐẶC TẢ YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS SPECIFICATION)

# Module 3.5 — Yoga & Mindfulness Scheduling Engine

---

| Trường       | Giá trị                                                       |
| -------------- | --------------------------------------------------------------- |
| Document ID    | `HOS-M3.5-FRS-001`                                            |
| Version        | 1.0                                                             |
| Ngày tạo     | 2026-06-26                                                      |
| Trạng thái   | Draft                                                           |
| Document Owner | Nhóm phát triển SE2023-G6                                    |
| Author         | DuongLD / AI Assistant                                          |
| Reviewed by    | Tech Lead                                                       |
| DPO Sign-off   | [ ] Pending*(bắt buộc — module xử lý Physical Health PII)* |
| Approved by    | Principal Architect                                             |
| Based on SRS   | SRS_Document.md (HOS-03)                                        |
| Based on EDS   | EDS_TEMPLATE_V2.0                                               |

---

## CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                    |
| ---------- | ------------------- | --------------------------------------- |
| 2026-06-26 | SE2023-G6 / AI      | Khởi tạo tài liệu FRS Module 3.5 v1.0 |

---

## MỤC LỤC

1. [Tổng quan Module](#1-tổng-quan-module)
2. [Actors &amp; Phạm vi](#2-actors--phạm-vi)
3. [Danh sách Use Cases của Module 3.5](#3- danh-sách-use-cases-của-module-35)
4. [Use Case Specifications (Đặc tả Chi tiết)](#4-use-case-specifications-đặc-tả-chi-tiết)
   - 4.1 UC_YOGA_01 — View Schedule & Register Yoga Class
   - 4.2 UC_YOGA_02 — Cancel Yoga Class Registration
   - 4.3 UC_YOGA_03 — View Class Participant List & Health Notes (Instructor)
   - 4.4 UC_YOGA_04 — Manage Classes & Schedules (Manager)
5. [Functional Requirements — Đặc tả màn hình](#5-functional-requirements--đặc-tả-màn-hình)
   - 5.1 Guest Yoga Booking Screen
   - 5.2 Instructor Class Details Screen
6. [Business Rules áp dụng](#6-business-rules-áp-dụng)
7. [Data Model — Bảng dữ liệu liên quan](#7-data-model--bảng-dữ-liệu-liên-quan)
8. [RBAC &amp; Data Minimization](#8-rbac--data-minimization)
9. [Non-Functional Requirements](#9-non-functional-requirements)
10. [System Messages](#10-system-messages)

---

## 1. Tổng quan Module

### 1.1 Mô tả

**Module 3.5 — Yoga & Mindfulness Scheduling Engine** quản lý các hoạt động lớp học nhóm (1-nhiều) thuộc về chương trình Vận động & Tâm trí (Mindfulness) tại Xoai Aura Retreat. Khác với phân hệ Spa (mô hình 1-1 riêng tư), phân hệ Yoga xử lý việc đăng ký tham gia các lớp học nhóm có giới hạn sĩ số theo lịch cố định của resort.

Module này tích hợp sâu với hồ sơ sức khỏe vật lý (`PHYSICAL_HEALTH_PROFILE`) của khách hàng để đưa ra các cảnh báo an toàn khớp gối, tim mạch, cột sống trước khi tập, đồng thời hiển thị thông tin cảnh báo này cho Huấn luyện viên (Instructor) để hướng dẫn điều chỉnh tư thế.

| Trường              | Giá trị                                                                    |
| --------------------- | ---------------------------------------------------------------------------- |
| Module Name           | Yoga & Mindfulness Scheduling Engine                                         |
| Bounded Context       | `yoga`                                                                      |
| Data Classification   | **Sensitive-PII** (Physical Health Profile — injuries, medical notes) |
| Compliance Scope      | Decree 356/2025 — Personal Data Protection                                  |
| Upstream Dependencies | `auth` (RBAC/JWT), `booking` (booking_id, status)                           |
| Downstream Consumers  | `fnb`/`spa` (itinerary timeline)                                            |

### 1.2 Phạm vi nghiệp vụ

| Chức năng                                           | Actor           | UC          |
| --------------------------------------------------- | --------------- | ----------- |
| Xem lịch học & đăng ký lớp Yoga (trong gói/miễn phí)| Guest           | UC_YOGA_01  |
| Hủy đăng ký lớp học Yoga                            | Guest           | UC_YOGA_02  |
| Xem danh sách học viên lớp & ghi chú sức khỏe       | Instructor      | UC_YOGA_03  |
| Quản lý danh mục lớp và lịch học hàng tuần          | Manager         | UC_YOGA_04  |

---

## 2. Actors & Phạm vi

| # | Actor                                   | Vai trò trong Module 3.5                                                            |
| - | --------------------------------------- | ------------------------------------------------------------------------------------ |
| 1 | **Guest**                         | Xem lịch Yoga trong ngày; Đăng ký/Hủy đăng ký lớp; Xác nhận cảnh báo sức khỏe      |
| 2 | **Yoga Instructor**               | Xem danh sách học viên đăng ký lớp; Đọc ghi chú sức khỏe để chỉnh sửa tư thế         |
| 3 | **Manager**                       | Tạo lớp Yoga, phân công giáo viên, gán địa điểm học và số học viên tối đa             |
| 4 | **System**                        | Tự động kiểm tra chấn thương/bệnh lý trùng khớp; Xác thực sĩ số tối đa; Ghi log     |

---

## 3. Danh sách Use Cases của Module 3.5

| UC ID       | Tên Use Case                                      | Actor chính | Ưu tiên |
| ----------- | ------------------------------------------------- | ----------- | ------- |
| UC_YOGA_01  | View Schedule & Register Yoga Class               | Guest       | High    |
| UC_YOGA_02  | Cancel Yoga Class Registration                    | Guest       | High    |
| UC_YOGA_03  | View Class Participant List & Health Notes        | Instructor  | Medium  |
| UC_YOGA_04  | Manage Classes & Schedules                        | Manager     | Medium  |

---

## 4. Use Case Specifications (Đặc tả Chi tiết)

### 4.1 UC_YOGA_01 — View Schedule & Register Yoga Class

| Trường                   | Nội dung                                                                                                                                                                                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **ID & Tên**        | UC_YOGA_01 — View Schedule & Register Yoga Class                                                                                                                                                                                                                   |
| **Primary Actor**    | Guest                                                                                                                                                                                                                                                              |
| **Secondary Actors** | System, Yoga Instructor                                                                                                                                                                                                                                            |
| **Mô tả**          | Guest có đơn đặt phòng đang Checked-In thực hiện đăng ký tham gia lớp học Yoga theo lịch cố định của resort. Hệ thống đối chiếu thông tin chấn thương/bệnh nền để đưa ra cảnh báo an toàn và đảm bảo lớp học không vượt quá sĩ số tối đa. |
| **Trigger**          | Guest chọn "Lịch học Yoga" từ Guest Dashboard.                                                                                                                                                                                                                     |

#### Preconditions
- [PRE-1] Guest đã đăng nhập với vai trò `GUEST`.
- [PRE-2] Guest có đơn đặt phòng với trạng thái `Checked-In` hoặc `Checked-out` (chưa checkout hoàn toàn).
- [PRE-3] Lớp học Yoga có `is_delete = 0` và thời gian bắt đầu trong tương lai.

#### Postconditions
- [POST-1] Bản ghi `YOGA_REGISTRATION` được lưu với trạng thái `REGISTERED`.
- [POST-2] Số lượng chỗ trống của lịch học giảm đi 1.
- [POST-3] Hiển thị thông báo đăng ký lớp học thành công.

#### Normal Flow
1. Guest mở màn hình "Lịch học Yoga".
2. System hiển thị lịch học Yoga của ngày hôm nay/ngày được chọn, bao gồm: tên lớp, giờ học, địa điểm, giáo viên phụ trách, sĩ số tối đa và số chỗ còn trống.
3. Guest nhấn nút **"Đăng ký"** vào lớp học mong muốn.
4. System kiểm tra sĩ số lớp (`count_registered < max_capacity`).
5. System kiểm tra trùng lịch (Guest không được đăng ký hai lớp Yoga chồng chéo thời gian).
6. System kiểm tra `PHYSICAL_HEALTH_PROFILE` của Guest:
   - *Nếu không có chấn thương/bệnh lý*: System tiến hành lưu đăng ký và thông báo thành công.
   - *Nếu phát hiện chấn thương/bệnh lý (ví dụ: đau khớp gối, tim mạch)*: System chuyển sang luồng A1.
7. System tạo bản ghi trong bảng `YOGA_REGISTRATION` với trạng thái `REGISTERED`.
8. Trả về thông báo thành công.

#### Alternative Flows
- **A1: Xuất hiện cảnh báo chấn thương/sức khỏe**:
  1. System phát hiện từ khóa chấn thương hoặc bệnh nền trong hồ sơ sức khỏe.
  2. System hiển thị Popup: *"Cảnh báo: Bạn có ghi nhận bệnh lý/chấn thương. Bạn có đồng ý tự chịu trách nhiệm về thể chất khi thực hiện bài tập lớp này không?"*.
  3. Guest nhấn "Đồng ý" -> System tiếp tục thực hiện lưu ở bước 7 (Luồng chính).
  4. Guest nhấn "Hủy bỏ" -> Hủy giao dịch, không lưu.

#### Exception Flows
- **E1: Lớp học đã đầy (Over capacity)**:
  - System thông báo lớp học đã đủ sĩ số tối đa và không nhận thêm học viên. Nút đăng ký bị vô hiệu hóa.
- **E2: Trùng lịch tập**:
  - System thông báo: *"Bạn đã đăng ký một hoạt động khác trong khung giờ này"*.

---

### 4.2 UC_YOGA_02 — Cancel Yoga Class Registration

| Trường                   | Nội dung                                                                                                                                                     |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC_YOGA_02 — Cancel Yoga Class Registration                                                                                                                  |
| **Primary Actor**    | Guest                                                                                                                                                        |
| **Secondary Actors** | System                                                                                                                                                       |
| **Mô tả**          | Guest hủy lượt đăng ký tham gia lớp học Yoga trước khi lớp diễn ra ít nhất 30 phút.                                                                          |
| **Trigger**          | Guest bấm "Hủy lớp" trên màn hình lịch tập Yoga hoặc Hành trình cá nhân (Itinerary).                                                                           |

#### Preconditions
- [PRE-1] Guest có bản ghi `YOGA_REGISTRATION` ở trạng thái `REGISTERED`.
- [PRE-2] Thời gian hiện tại cách thời gian bắt đầu lớp học tối thiểu 30 phút (BR-YOGA-03).

#### Postconditions
- [POST-1] Trạng thái bản ghi `YOGA_REGISTRATION` cập nhật thành `CANCELLED`.
- [POST-2] Giải phóng 1 chỗ trống cho lớp học đó.

---

### 4.3 UC_YOGA_03 — View Class Participant List & Health Notes (Instructor)

| Trường                | Nội dung                                                                                                                                                                                                                                                      |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**     | UC_YOGA_03 — View Class Participant List & Health Notes                                                                                                                                                                                                        |
| **Primary Actor** | Yoga Instructor                                                                                                                                                                                                                                                |
| **Mô tả**       | Huấn luyện viên xem danh sách học viên đăng ký lớp học do mình phụ trách để điểm danh, đặc biệt đọc ghi chú chấn thương/bệnh lý của học viên (đã giải mã) để hướng dẫn sửa tư thế an toàn. |
| **Trigger**       | Giáo viên đăng nhập và truy cập lớp học Yoga cụ thể trên Dashboard.                                                                                                                                                                                        |

#### Preconditions
- [PRE-1] Người dùng đăng nhập có vai trò `YOGA_INSTRUCTOR`.
- [PRE-2] Lớp học được truy vấn được gán cho giáo viên tương ứng (`instructor_id` khớp).

#### Normal Flow
1. Giáo viên mở danh sách học viên của lớp học cụ thể.
2. System tải danh sách học viên đang ở trạng thái đăng ký `REGISTERED`.
3. System giải mã trường thông tin `medical_conditions` và `injuries` từ hồ sơ `PHYSICAL_HEALTH_PROFILE` của từng khách hàng.
4. Hiển thị danh sách học viên gồm: Họ tên, Số phòng Villa, và các lưu ý chấn thương/bệnh nền bên cạnh (chỉ hiển thị các thông tin liên quan vận động).
5. Giáo viên ghi nhận tình hình lớp.

---

### 4.4 UC_YOGA_04 — Manage Classes & Schedules (Manager)

- **Mô tả**: Cho phép Quản lý (Manager) tạo danh mục lớp học (ví dụ: Yoga Vô cực, Hatha Yoga trị liệu), quản lý tài khoản giáo viên Yoga và tạo lịch học hàng tuần (gán lớp, gán địa điểm, gán giáo viên và giới hạn số học viên).

---

## 5. Functional Requirements — Đặc tả màn hình

### 5.1 Guest Yoga Booking Screen

Giao diện cho phép khách hàng tự đặt lịch các lớp Yoga hàng ngày.

#### Field Descriptions
* **Date Selection**: Cho phép chọn ngày học (chỉ cho phép chọn ngày hôm nay hoặc tương lai).
* **Schedule Cards**:
  * Tên lớp Yoga (ví dụ: *Hatha Yoga cho cột sống*) & Giáo viên phụ trách.
  * Địa điểm (ví dụ: *Yoga Studio ngoài trời*).
  * Thời gian (ví dụ: *08:00 - 09:00*).
  * Chỗ trống: Số chỗ trống hiển thị động (ví dụ: *Còn 5 / 15 chỗ*).
  * Trạng thái nút:
    * "Đăng ký": Nếu khách chưa đăng ký và lớp còn chỗ.
    * "Hủy đăng ký": Nếu khách đã đăng ký lớp đó.
    * "Đầy chỗ" (Disabled): Nếu lớp đã hết chỗ.

#### Quy tắc hiển thị Cảnh báo Sức khỏe (Modal Warning)
Khi khách nhấn "Đăng ký" và API trả về phát hiện chấn thương:
* Hiển thị Modal nổi giữa màn hình với nền mờ.
* Nội dung: *"Hệ thống ghi nhận bạn đang có tình trạng: [Nội dung bệnh lý/chấn thương của khách]. Bạn có chắc chắn tình trạng thể chất của mình phù hợp với bài tập của lớp học này không?"*.
* Nút hành động: "Xác nhận tham gia" (lưu đăng ký) và "Hủy bỏ" (không đăng ký).

---

### 5.2 Instructor Class Details Screen

Màn hình chi tiết lớp học dành cho Giáo viên.

#### Data Minimization Matrix cho Giáo viên Yoga
Áp dụng nguyên tắc tối thiểu hóa thông tin để bảo vệ dữ liệu PII nhạy cảm:

| Dữ liệu sức khỏe / Thông tin cá nhân | Hiển thị? | Mục đích sử dụng |
| ------------------------------------ | --------- | ---------------- |
| Họ tên khách hàng                    | ✅ Có      | Nhận diện học viên |
| Số phòng Villa                       | ✅ Có      | Xác thực khách lưu trú |
| Chấn thương khớp/xương (Injuries)     | ✅ Có      | Để tránh tư thế gây hại khớp |
| Bệnh lý tim mạch/Huyết áp            | ✅ Có      | Để tránh bài tập gắng sức nặng |
| Dị ứng thực phẩm (Food Allergies)    | ❌ Không   | Không liên quan đến hoạt động vận động |
| Thông tin thanh toán (Payment)       | ❌ Không   | Không thuộc quyền hạn của giáo viên |

---

## 6. Business Rules áp dụng

* **BR-YOGA-01: Checked-In Requirement**: Chỉ cho phép khách hàng thuộc đơn đặt phòng có trạng thái `Checked-In` đăng ký tham gia lớp học Yoga.
* **BR-YOGA-02: Class Capacity Limit**: Lượng đăng ký thực tế không được vượt quá `max_capacity` của lịch học Yoga.
* **BR-YOGA-03: Cancellation Deadline**: Khách chỉ được phép tự hủy đăng ký trực tuyến trước giờ bắt đầu tối thiểu 30 phút. Quá thời gian này, nút hủy sẽ bị vô hiệu hóa.
* **BR-YOGA-04: No Overlap Booking**: Khách không thể đăng ký hai lớp Yoga (hoặc một lớp Yoga và một buổi trị liệu Spa) trùng khung thời gian với nhau.

---

## 7. Data Model — Bảng dữ liệu liên quan

Phân hệ sử dụng các bảng dữ liệu sau trong cơ sở dữ liệu (Schema chi tiết tại kế hoạch triển khai):
1. `YOGA_CLASS` (Lưu thông tin lớp)
2. `YOGA_INSTRUCTOR` (Thông tin giáo viên liên kết với tài khoản `[USER]`)
3. `YOGA_SCHEDULE` (Lịch dạy)
4. `YOGA_REGISTRATION` (Thông tin đăng ký của khách hàng)
5. `PHYSICAL_HEALTH_PROFILE` (Hồ sơ sức khỏe của khách)

---

## 8. RBAC & Data Minimization

* Vai trò **`YOGA_INSTRUCTOR`**:
  * Chỉ được truy cập xem lịch dạy của chính bản thân mình (cấm xem lịch giáo viên khác).
  * Chỉ được giải mã thông tin chấn thương (`injuries`) và bệnh lý (`medical_conditions`) của học viên đã đăng ký lớp của mình. Cấm truy cập dữ liệu dị ứng ăn uống (`DIETARY_PROFILE`).
* Vai trò **`GUEST`**:
  * Chỉ được xem lịch học của bản thân và thực hiện đăng ký/hủy đăng ký lớp Yoga cho chính đơn đặt phòng của mình.

---

## 9. Non-Functional Requirements

* **Performance**: API tính toán chỗ trống và kiểm tra chấn thương phải phản hồi dưới 500ms dưới tải bình thường.
* **Security**: Dữ liệu sức khỏe từ `PHYSICAL_HEALTH_PROFILE` phải được giải mã tại RAM và không bao giờ được ghi ra file log dạng thô (Clear-text).

---

## 10. System Messages

* **MSG-YOGA-001** (Success): *"Đăng ký tham gia lớp học Yoga thành công."*
* **MSG-YOGA-002** (Error): *"Lớp học đã đủ sĩ số tối đa. Vui lòng chọn lớp học hoặc giờ học khác."*
* **MSG-YOGA-003** (Error): *"Bạn đã đăng ký một hoạt động khác trùng vào khung giờ này."*
* **MSG-YOGA-004** (Error): *"Không thể hủy lớp học vì thời gian bắt đầu đã dưới 30 phút."*
