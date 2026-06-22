# ĐẶC TẢ YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS SPECIFICATION)

# Module 3 — Spa & Therapy Scheduling Engine

---

| Trường       | Giá trị                                                       |
| -------------- | --------------------------------------------------------------- |
| Document ID    | `HOS-M3-FRS-001`                                              |
| Version        | 1.0                                                             |
| Ngày tạo     | 2026-06-14                                                      |
| Trạng thái   | Draft                                                           |
| Document Owner | Nhóm phát triển SE2023-G6                                    |
| Author         | DuongLD                                                         |
| Reviewed by    | Tech Lead                                                       |
| DPO Sign-off   | [ ] Pending*(bắt buộc — module xử lý Physical Health PII)* |
| Approved by    | Principal Architect                                             |
| Based on SRS   | SRS_Document.md (HOS-03)                                        |
| Based on EDS   | EDS_TEMPLATE_V2.0                                               |

---

## CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                    |
| ---------- | ------------------- | --------------------------------------- |
| 2026-06-14 | SE2023-G6           | Khởi tạo tài liệu FRS Module 3 v1.0 |

---

## MỤC LỤC

1. [Tổng quan Module](#1-tổng-quan-module)
2. [Actors &amp; Phạm vi](#2-actors--phạm-vi)
3. [Danh sách Use Cases của Module 3](#3-danh-sách-use-cases-của-module-3)
4. [Use Case Specifications (Đặc tả Chi tiết)](#4-use-case-specifications-đặc-tả-chi-tiết)
   - 4.1 UC11 — Schedule Spa/Therapy Session
   - 4.2 UC12 — Find Available Therapist and Treatment Room (Auto-Matching)
   - 4.3 UC13 — View Daily Work Schedule (Therapist)
   - 4.4 UC14 — Update Treatment Session Status
   - 4.5 UC15 — Book Additional Spa Service (Receptionist)
5. [Functional Requirements — Đặc tả màn hình](#5-functional-requirements--đặc-tả-màn-hình)
   - 5.1 Guest Spa Scheduler Screen
   - 5.2 Therapist Daily Schedule Screen
   - 5.3 Manual Spa Booking Modal (Receptionist)
6. [Business Rules áp dụng](#6-business-rules-áp-dụng)
7. [Data Model — Bảng dữ liệu liên quan](#7-data-model--bảng-dữ-liệu-liên-quan)
8. [RBAC &amp; Data Minimization](#8-rbac--data-minimization)
9. [Non-Functional Requirements](#9-non-functional-requirements)
10. [System Messages](#10-system-messages)
11. [Traceability Matrix](#11-traceability-matrix)

---

## 1. Tổng quan Module

### 1.1 Mô tả

**Module 3 — Spa & Therapy Scheduling Engine** quản lý toàn bộ vòng đời đặt lịch và thực hiện dịch vụ Spa / Liệu pháp tại Xoai Aura Retreat. Module này là cốt lõi nghiệp vụ của resort vì giải quyết bài toán phân bổ tài nguyên 2 chiều đồng thời (Therapist + Treatment Room) nhằm ngăn chặn tuyệt đối tình trạng đặt chồng (double-booking).

Module 3 phục vụ ba nhóm actor chính:

- **Guest**: Tự đặt lịch các session Spa được bao gồm trong gói Retreat.
- **Receptionist**: Đặt thêm dịch vụ Spa ngoài gói cho Guest đang lưu trú và ghi nợ vào Guest Folio.
- **Spa Therapist / Yoga Trainer**: Xem lịch làm việc hàng ngày, đọc ghi chú sức khỏe vật lý (chỉ phần liên quan điều trị), cập nhật trạng thái session.

| Trường              | Giá trị                                                                    |
| --------------------- | ---------------------------------------------------------------------------- |
| Module Name           | Spa & Therapy Scheduling Engine                                              |
| Bounded Context       | `spa`                                                                      |
| Data Classification   | **Sensitive-PII** (Physical Health Profile — injuries, medical notes) |
| Compliance Scope      | Decree 356/2025 — Personal Data Protection                                  |
| Upstream Dependencies | `auth` (RBAC/JWT), `booking` (booking_id, villa_id)                      |
| Downstream Consumers  | `billing` (folio charge), `fnb` (itinerary timeline)                     |

### 1.2 Phạm vi nghiệp vụ

Module 3 bao gồm:

| Chức năng                                          | Actor        | UC   |
| ---------------------------------------------------- | ------------ | ---- |
| Đặt lịch Spa session (trong gói Retreat)         | Guest        | UC11 |
| Tự động tìm Therapist & Phòng khả dụng        | System       | UC12 |
| Xem lịch làm việc hàng ngày                     | Therapist    | UC13 |
| Cập nhật trạng thái session                      | Therapist    | UC14 |
| Đặt thêm dịch vụ Spa ngoài gói (Receptionist) | Receptionist | UC15 |

**Ngoài phạm vi Module 3:**

- Quản lý Master Data Spa services → Module Admin
- Thanh toán & hóa đơn → Module 5 (Billing)
- Đặt lịch F&B → Module 4

---

## 2. Actors & Phạm vi

| # | Actor                                   | Vai trò trong Module 3                                                              |
| - | --------------------------------------- | ------------------------------------------------------------------------------------ |
| 1 | **Guest**                         | Đặt lịch Spa session từ gói đã mua; Xem lịch trên Itinerary Timeline        |
| 2 | **Receptionist**                  | Đặt thêm dịch vụ Spa ngoài gói cho Guest; Ghi nợ vào Folio                  |
| 3 | **Spa Therapist / Yoga Trainer**  | Xem lịch ngày; Đọc ghi chú sức khỏe vật lý; Cập nhật trạng thái session |
| 4 | **System**                        | Tự động matching Therapist + Room; Gửi thông báo/reminder; Ghi Audit log       |
| 5 | **Calendar/Notification Service** | Nhận event để gửi email xác nhận và reminder trước session                  |

---

## 3. Danh sách Use Cases của Module 3

| UC ID | Tên Use Case                             | Actor chính  | Ưu tiên |
| ----- | ----------------------------------------- | ------------- | --------- |
| UC11  | Schedule Spa/Therapy Session              | Guest         | High      |
| UC12  | Find Available Therapist & Treatment Room | System (Auto) | High      |
| UC13  | View Daily Work Schedule                  | Therapist     | High      |
| UC14  | Update Treatment Session Status           | Therapist     | High      |
| UC15  | Book Additional Spa Service               | Receptionist  | Medium    |

---

## 4. Use Case Specifications (Đặc tả Chi tiết)

### 4.1 UC11 — Schedule Spa/Therapy Session

| Trường                   | Nội dung                                                                                                                                                                                                                                   |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC11 — Schedule Spa/Therapy Session                                                                                                                                                                                                        |
| **Primary Actor**    | Guest                                                                                                                                                                                                                                       |
| **Secondary Actors** | System, Calendar/Notification Service, Spa Therapist                                                                                                                                                                                        |
| **Mô tả**          | Guest có booking Retreat Package đang active đặt lịch cho các session Spa/liệu pháp được bao gồm trong gói. Hệ thống tự động phân bổ Therapist và phòng điều trị phù hợp, ngăn chặn đặt chồng tài nguyên. |
| **Trigger**          | Guest chọn "Schedule Therapy Session" từ Guest Dashboard hoặc Itinerary Timeline.                                                                                                                                                        |

#### Preconditions (Điều kiện tiên quyết)

- [PRE-1] Guest đã xác thực và có JWT hợp lệ với role `GUEST`.
- [PRE-2] Guest có ít nhất một Booking đang ở trạng thái `Confirmed` hoặc `Checked-In`.
- [PRE-3] Gói Retreat của Guest còn session Spa chưa được đặt lịch.
- [PRE-4] Có ít nhất một Therapist và một Treatment Room được khai báo trong hệ thống.

#### Postconditions (Kết quả sau thực hiện)

- [POST-1] Một bản ghi `TREATMENT_BOOKING` mới được tạo với `status = Scheduled`.
- [POST-2] Một bản ghi `SCHEDULE` mới được tạo, liên kết `therapist_code` và `room_id` với `start_time` và `end_time` cụ thể.
- [POST-3] Notification/reminder được gửi đến Guest qua Calendar Service.
- [POST-4] Audit log ghi nhận hành động đặt lịch (BR-15).

#### Normal Flow (Luồng chính)

| Bước | Actor  | Hành động                                                                                                                |
| ------ | ------ | --------------------------------------------------------------------------------------------------------------------------- |
| 1      | Guest  | Mở màn hình Spa Scheduler.                                                                                               |
| 2      | System | Hiển thị danh sách dịch vụ Spa có trong gói Retreat của Guest.                                                      |
| 3      | Guest  | Chọn loại dịch vụ Spa.                                                                                                  |
| 4      | Guest  | Chọn ngày mong muốn từ Date Picker.                                                                                     |
| 5      | System | Gọi API kiểm tra tính đủ điều kiện theo gói (BR-05).                                                               |
| 6      | System | Thực hiện truy vấn 2 chiều: tìm Therapist khả dụng VÀ Treatment Room khả dụng cho ngày đã chọn (UC12, BR-04). |
| 7      | System | Render danh sách Time Slot: slot khả dụng = nút bấm; slot hết = Disabled/Grayed-out.                                  |
| 8      | Guest  | Chọn Time Slot mong muốn.                                                                                                 |
| 9      | System | Hiển thị Booking Summary (dịch vụ, ngày, giờ, phòng auto-assigned, therapist).                                       |
| 10     | Guest  | Nhấn nút "Xác nhận đặt lịch".                                                                                        |
| 11     | System | Thực hiện Database Transaction: Lock Therapist + Room → Insert `TREATMENT_BOOKING` + `SCHEDULE`.                     |
| 12     | System | Gọi Calendar/Notification Service gửi xác nhận và reminder 1 giờ trước session (BR-17).                             |
| 13     | System | Ghi Audit Log (BR-15).                                                                                                      |
| 14     | System | Hiển thị MSG-09 (Đặt lịch thành công).                                                                               |

#### Alternative Flows (Luồng thay thế)

| ID | Điều kiện                         | Xử lý                                                                                            |
| -- | ------------------------------------ | -------------------------------------------------------------------------------------------------- |
| A1 | Guest muốn chọn Therapist cụ thể | Giao diện cho phép filter theo Therapist; Algorithm kiểm tra availability riêng.               |
| A2 | Guest thay đổi ngày đã chọn    | Hệ thống tái tính availability và refresh Time Slot Grid.                                     |
| A3 | Guest mua thêm session ngoài gói  | Hệ thống cho phép đặt lịch; Phí được ghi vào Folio thông qua UC15 (bởi Receptionist). |

#### Exception Flows (Luồng ngoại lệ)

| ID | Điều kiện                                                    | Xử lý                                                                       |
| -- | --------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| E1 | Không có Therapist hoặc Room nào khả dụng                 | Hiển thị MSG-10; Đề xuất ngày/giờ khác.                               |
| E2 | Guest không đủ điều kiện (gói không bao gồm dịch vụ) | Hiển thị thông báo lỗi eligibility; Tắt nút xác nhận.                |
| E3 | Transaction thất bại (concurrent booking conflict)            | Rollback toàn bộ transaction; Hiển thị MSG-10; Yêu cầu chọn lại slot. |
| E4 | Notification Service thất bại                                 | Log lỗi; Session vẫn được xác nhận (BR-17); Không rollback.           |

#### Business Rules áp dụng

- **BR-04**: Dual Resource Spa Scheduling — bắt buộc có cả Therapist VÀ Room khả dụng.
- **BR-05**: Spa service eligibility — chỉ dịch vụ trong gói.
- **BR-15**: Audit Trail.
- **BR-17**: Spa Appointment Notification.

---

### 4.2 UC12 — Find Available Therapist and Treatment Room (Auto-Matching Engine)

| Trường                   | Nội dung                                                                                                                                                                                          |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC12 — Find Available Therapist and Treatment Room                                                                                                                                                |
| **Primary Actor**    | System (Automated)                                                                                                                                                                                 |
| **Secondary Actors** | UC11 (trigger), UC15 (trigger)                                                                                                                                                                     |
| **Mô tả**          | Hệ thống tự động kiểm tra đồng thời tính khả dụng của Therapist VÀ Treatment Room cho một khoảng thời gian xác định. Đây là lõi logic ngăn chặn double-booking 2 chiều. |
| **Trigger**          | Được gọi nội bộ khi Guest hoặc Receptionist chọn ngày trên màn hình đặt lịch Spa.                                                                                                   |

#### Preconditions

- [PRE-1] `booking_id` hợp lệ được cung cấp.
- [PRE-2] `service_id` hợp lệ với `duration_minutes` xác định.
- [PRE-3] Ngày/giờ được yêu cầu (`start_time`) ở trong tương lai.

#### Postconditions

- [POST-1] Trả về danh sách Time Slot với trạng thái AVAILABLE hoặc UNAVAILABLE.
- [POST-2] Nếu xác nhận booking: một cặp (`therapist_code`, `room_id`) được lock và lưu vào `SCHEDULE`.

#### Normal Flow — Query Availability

| Bước | Hành động                                                                                                                                                                                                        |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Nhận tham số:`service_id`, `date`, tùy chọn `requested_therapist_id` (nullable).                                                                                                                          |
| 2      | Tính `end_time = start_time + duration_minutes` cho từng slot trong ngày làm việc.                                                                                                                           |
| 3      | **Branch A — Auto assign**: Nếu `requested_therapist_id = null`, truy vấn tất cả Therapist có `status = Available` và không có `SCHEDULE` record nào overlap với `[start_time, end_time]`. |
| 4      | **Branch B — Specific Therapist**: Nếu `requested_therapist_id` được cung cấp, kiểm tra availability chỉ cho Therapist đó.                                                                        |
| 5      | Song song: Truy vấn Treatment Room có `status = Available` và không có `SCHEDULE` record nào overlap.                                                                                                     |
| 6      | **Điều kiện AVAILABLE**: `COUNT(available_therapist) >= 1` **AND** `COUNT(available_room) >= 1`.                                                                                                 |
| 7      | Trả về danh sách slot với trạng thái tương ứng.                                                                                                                                                            |

#### Normal Flow — Confirm & Lock (khi Guest xác nhận)

| Bước | Hành động                                                                                                                              |
| ------ | ----------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Bắt đầu Database Transaction với mức isolation `SERIALIZABLE` (hoặc dùng `SELECT ... FOR UPDATE`).                             |
| 2      | Re-query availability (lần 2) trong transaction để ngăn race condition.                                                               |
| 3      | Nếu vẫn available: Insert `TREATMENT_BOOKING` → lấy `treatment_id`.                                                               |
| 4      | Insert `SCHEDULE` với `treatment_id`, `therapist_code` (auto-selected hoặc requested), `room_id`, `start_time`, `end_time`. |
| 5      | Commit transaction.                                                                                                                       |
| 6      | Nếu conflict được phát hiện trong bước 2: Rollback và trả lỗi E3.                                                              |

#### Algorithm — Auto Therapist Selection

```
FUNCTION findAvailableTherapist(service_id, start_time, end_time, requested_therapist_id):
  IF requested_therapist_id IS NOT NULL:
    therapist_list = [requested_therapist_id]
  ELSE:
    therapist_list = ALL therapists WHERE status = 'Available'
  
  FOR EACH therapist IN therapist_list:
    conflict = SCHEDULE WHERE therapist_code = therapist.therapist_code
                          AND NOT (end_time <= start_time OR start_time >= end_time)
                          AND is_delete = 0
    IF conflict IS EMPTY:
      RETURN therapist  // first available
  
  RETURN NULL  // no therapist available
```

#### Business Rules áp dụng

- **BR-04**: Dual Resource Constraint — cả 2 chiều phải thỏa mãn đồng thời.

---

### 4.3 UC13 — View Daily Work Schedule

| Trường                | Nội dung                                                                                                                                                                                                                                    |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**     | UC13 — View Daily Work Schedule                                                                                                                                                                                                             |
| **Primary Actor** | Spa Therapist / Yoga Trainer                                                                                                                                                                                                                 |
| **Mô tả**       | Therapist xem toàn bộ danh sách session được phân công trong ngày làm việc, bao gồm thông tin Guest, loại dịch vụ, phòng điều trị và thời gian. Có thể truy cập ghi chú sức khỏe vật lý giới hạn của Guest. |
| **Trigger**       | Therapist mở màn hình "Daily Schedule" sau khi đăng nhập.                                                                                                                                                                              |

#### Preconditions

- [PRE-1] Therapist đã xác thực với JWT hợp lệ và role `THERAPIST`.
- [PRE-2] `therapist_code` được extract từ JWT/Security Context.

#### Postconditions

- [POST-1] Màn hình hiển thị danh sách session theo thứ tự thời gian.
- [POST-2] Dữ liệu nhạy cảm ngoài phạm vi điều trị bị ẩn hoàn toàn (BR-07).

#### Normal Flow

| Bước | Actor     | Hành động                                                                                                                                                                                                    |
| ------ | --------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Therapist | Mở màn hình Daily Schedule.                                                                                                                                                                                  |
| 2      | System    | Tự động extract `therapist_code` từ Security Context (JWT).                                                                                                                                               |
| 3      | System    | Truy vấn `SCHEDULE JOIN TREATMENT_BOOKING` WHERE `therapist_code = current_therapist` AND `start_time` thuộc ngày hiện tại.                                                                          |
| 4      | System    | Hiển thị Summary Cards: Tổng sessions, Sessions đã hoàn thành, Phòng trống.                                                                                                                            |
| 5      | System    | Render bảng Schedule Data với: Khung giờ, Tên Guest, Tên dịch vụ, Phòng, Trạng thái.                                                                                                                  |
| 6      | Therapist | (Tùy chọn) Nhấn "Xem Ghi Chú" để mở Modal ghi chú sức khỏe của Guest đó.                                                                                                                           |
| 7      | System    | **RBAC Enforcement**: Truy vấn `PHYSICAL_HEALTH_PROFILE` chỉ lấy `injuries` và `medical_conditions` liên quan điều trị. **Tuyệt đối không trả về** `DIETARY_PROFILE` (BR-07). |
| 8      | System    | Hiển thị ghi chú sức khỏe trong Modal.                                                                                                                                                                     |

#### Alternative Flows

| ID | Điều kiện                     | Xử lý                                                  |
| -- | -------------------------------- | -------------------------------------------------------- |
| A1 | Therapist xem ngày khác        | Date Picker cho phép chọn; System re-query theo ngày. |
| A2 | Không có session nào hôm nay | Hiển thị thông báo "Không có lịch hôm nay".      |

#### Exception Flows

| ID | Điều kiện                               | Xử lý                                              |
| -- | ------------------------------------------ | ---------------------------------------------------- |
| E1 | Attempt truy cập dietary data             | Backend trả 403; Log cảnh báo RBAC violation.     |
| E2 | Therapist truy cập schedule người khác | Backend kiểm tra ownership; Trả 403 nếu vi phạm. |

#### Business Rules áp dụng

- **BR-07**: RBAC — Therapist chỉ thấy physical health notes liên quan điều trị.
- **BR-05**: Chỉ assigned therapist mới có thể update session status.

---

### 4.4 UC14 — Update Treatment Session Status

| Trường                | Nội dung                                                                                                               |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**     | UC14 — Update Treatment Session Status                                                                                 |
| **Primary Actor** | Spa Therapist / Yoga Trainer                                                                                            |
| **Mô tả**       | Therapist cập nhật trạng thái của session sau khi thực hiện. Trạng thái "Completed" kích hoạt logic billing. |
| **Trigger**       | Therapist thay đổi Status Dropdown trên màn hình Daily Schedule.                                                   |

#### Preconditions

- [PRE-1] Therapist đã đăng nhập với role `THERAPIST`.
- [PRE-2] Session thuộc `therapist_code` hiện tại (ownership check).
- [PRE-3] Session đang ở trạng thái `Scheduled`.

#### Postconditions

- [POST-1] `TREATMENT_BOOKING.status` được cập nhật thành `Completed` hoặc `No-Show`.
- [POST-2] Nếu `Completed` VÀ session là dịch vụ ngoài gói: `FOLIO_ITEM` được tạo, ghi nợ vào `GUEST_FOLIO` của Guest (kết nối với billing - BR-11).
- [POST-3] Audit Log ghi nhận hành động cập nhật.

#### Normal Flow

| Bước | Actor     | Hành động                                                                                                                                                        |
| ------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Therapist | Chọn giá trị mới trong Status Dropdown:`Completed` hoặc `No-Show`.                                                                                         |
| 2      | System    | Validate ownership: kiểm tra `therapist_code` khớp với session.                                                                                                |
| 3      | System    | Validate state transition hợp lệ:`Scheduled → Completed` hoặc `Scheduled → No-Show`.                                                                       |
| 4      | System    | Update `TREATMENT_BOOKING.status` trong database.                                                                                                                 |
| 5      | System    | **Nếu `Completed` và `folio_id` tồn tại (extra service)**: Insert `FOLIO_ITEM` với `service_category = 'Extra Spa'`, `amount = service.price`. |
| 6      | System    | Ghi Audit Log.                                                                                                                                                      |
| 7      | System    | UI cập nhật hiển thị badge trạng thái mới.                                                                                                                   |

#### State Machine — Treatment Session Status

```
[Scheduled] ──────────────── Therapist marks Completed ──────────────→ [Completed]
[Scheduled] ──────────────── Therapist marks No-Show ─────────────────→ [No-Show]
[Scheduled] ──────────────── System/Admin cancels ────────────────────→ [Cancelled]
```

> ⚠️ **Invariant bất biến**: Không được phép chuyển ngược từ `Completed` hoặc `No-Show` về `Scheduled`.

#### Exception Flows

| ID | Điều kiện                               | Xử lý                                                             |
| -- | ------------------------------------------ | ------------------------------------------------------------------- |
| E1 | Therapist không phải owner của session  | Backend trả 403; Hiển thị MSG-18.                                |
| E2 | Session đã ở trạng thái `Completed` | Backend từ chối; Trả lỗi "Trạng thái không thể thay đổi". |

#### Business Rules áp dụng

- **BR-05**: Chỉ assigned therapist được update status.
- **BR-11**: Extra service charges → Folio.
- **BR-15**: Audit Trail.

---

### 4.5 UC15 — Book Additional Spa Service (Receptionist)

| Trường                   | Nội dung                                                                                                                                                                   |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC15 — Book Additional Spa Service                                                                                                                                         |
| **Primary Actor**    | Receptionist                                                                                                                                                                |
| **Secondary Actors** | System (Auto-Matching, UC12)                                                                                                                                                |
| **Mô tả**          | Receptionist đặt thêm dịch vụ Spa ngoài gói cho Guest đang lưu trú (Checked-In). Chi phí được ghi nợ trực tiếp vào Guest Folio mà không thu tiền ngay. |
| **Trigger**          | Receptionist mở Manual Spa Booking Modal từ Receptionist Dashboard.                                                                                                       |

#### Preconditions

- [PRE-1] Receptionist đã xác thực với role `RECEPTIONIST`.
- [PRE-2] Guest được chọn có Booking status = `Checked-In`.
- [PRE-3] Guest Folio (`GUEST_FOLIO`) đã tồn tại cho Booking đó.

#### Postconditions

- [POST-1] `TREATMENT_BOOKING` mới với `folio_id` được tạo.
- [POST-2] `SCHEDULE` record được tạo với Therapist + Room được phân bổ.
- [POST-3] `FOLIO_ITEM` được insert: `service_category = 'Extra Spa'`, `status = 'Pending'`.
- [POST-4] Audit Log ghi nhận hành động.

#### Normal Flow

| Bước | Actor        | Hành động                                                                                                                                                                                                                |
| ------ | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Receptionist | Mở Manual Spa Booking Modal.                                                                                                                                                                                               |
| 2      | Receptionist | Nhập Guest/Room ID vào Autocomplete Search Input.                                                                                                                                                                         |
| 3      | System       | Truy vấn chỉ Guest có `booking_status = 'Checked-In'`.                                                                                                                                                                 |
| 4      | Receptionist | Chọn Guest từ kết quả tìm kiếm.                                                                                                                                                                                       |
| 5      | Receptionist | Chọn Spa Service từ Dropdown.                                                                                                                                                                                             |
| 6      | Receptionist | Chọn ngày (default: ngày hiện tại).                                                                                                                                                                                    |
| 7      | System       | Gọi UC12 để tính availability và render Time Slot Grid.                                                                                                                                                                |
| 8      | Receptionist | Chọn Time Slot.                                                                                                                                                                                                            |
| 9      | System       | Hiển thị Price Summary: Base Price + Tax + Grand Total.                                                                                                                                                                   |
| 10     | Receptionist | Nhấn "Xác nhận & Ghi nợ vào Folio".                                                                                                                                                                                    |
| 11     | System       | **Dual-Write Transaction**: ``① Insert `TREATMENT_BOOKING` (with `folio_id` linkage) + `SCHEDULE`. ``② Insert `FOLIO_ITEM` với `status = 'Pending'`, `create_by = receptionist_user_id`. |
| 12     | System       | Ghi Audit Log.                                                                                                                                                                                                              |
| 13     | System       | Đóng Modal; Hiển thị MSG-09.                                                                                                                                                                                            |

#### Exception Flows

| ID | Điều kiện                         | Xử lý                                                           |
| -- | ------------------------------------ | ----------------------------------------------------------------- |
| E1 | Guest không có booking Checked-In  | Search trả về rỗng; Hiển thị thông báo lỗi.               |
| E2 | Không có Therapist/Room khả dụng | Hiển thị MSG-10; Đề xuất chọn slot khác.                   |
| E3 | Transaction thất bại               | Rollback toàn bộ (cả booking + folio item); Hiển thị MSG-19. |

#### Business Rules áp dụng

- **BR-04**: Dual Resource Scheduling.
- **BR-05**: Extra services ngoài gói chỉ Receptionist thêm được.
- **BR-11**: Ghi nợ vào Guest Folio.
- **BR-15**: Audit Trail.

---

## 5. Functional Requirements — Đặc tả màn hình

### 5.1 Guest Spa Scheduler Screen

**[Mô tả]** Giao diện cho phép Guest đặt lịch Spa session. Hệ thống đóng vai trò orchestrator phân bổ tài nguyên nền, render các time slot hợp lệ theo kết quả kiểm tra tài nguyên 2 chiều.

**Mapped Use Cases**: UC11, UC12

**Screen Authorization**: Chỉ `GUEST` có quyền truy cập.

#### Field Descriptions

| # | Field Name                               | Mô tả chi tiết                                                                                                                                                                                                                                                                     |
| - | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|   | **Field Group: Scheduling Inputs** |                                                                                                                                                                                                                                                                                       |
| 1 | Service Info                             | Read-only. Hiển thị: Service Name, Duration (phút), Price. Dữ liệu từ `TREATMENT_SERVICE`.                                                                                                                                                                                    |
| 2 | Date Picker                              | Data type:`LocalDate`. **Constraint**: Ngày trong quá khứ bị disable. Chọn ngày hợp lệ → trigger API `/api/v1/spa/availability?date=&service_id=` để fetch Time Slot Grid.                                                                                       |
| 3 | Time Slot Grid                           | Data type:`LocalTime`. **Business Rule**: Slot CHỈ AVAILABLE khi backend xác nhận `COUNT(available_therapist) >= 1 AND COUNT(available_room) >= 1` cho khoảng thời gian đó. Slot không đáp ứng → Render Disabled, label "Hết chỗ". Áp dụng **BR-04**. |
|   | **Field Group: Confirmation**      |                                                                                                                                                                                                                                                                                       |
| 4 | Booking Summary                          | Read-only. Hiển thị: Dịch vụ, Ngày, Giờ, Phòng (auto-assigned bởi UC12 — Guest không được chọn), Therapist (có thể ẩn tên nếu Auto). Cập nhật động theo input (2) và (3).                                                                                     |
| 5 | Nút "Xác nhận đặt lịch"            | Action: Submit booking request.**Backend Constraint**: Toàn bộ execution phải trong Database Transaction (`@Transactional` + `SELECT FOR UPDATE` trên Therapist và Room) để ngăn concurrent double-booking.                                                         |

#### Validation Rules

| Trường    | Rule                                         | Error Message                               |
| ----------- | -------------------------------------------- | ------------------------------------------- |
| Date Picker | Không được chọn ngày quá khứ         | "Ngày không hợp lệ"                     |
| Time Slot   | Phải chọn ít nhất một slot              | "Vui lòng chọn thời gian"                |
| Eligibility | Dịch vụ phải thuộc gói Retreat đã mua | "Dịch vụ không có trong gói của bạn" |

---

### 5.2 Therapist Daily Schedule Screen

**[Mô tả]** Dashboard chuyên dụng cho Therapist quản lý ca làm việc hàng ngày. Cung cấp xem lịch sessions, truy cập an toàn ghi chú sức khỏe vật lý, và cập nhật trạng thái thời gian thực.

**Mapped Use Cases**: UC13, UC14

**Screen Authorization**: Chỉ `THERAPIST` có quyền truy cập.

#### Field Descriptions

| # | Field Name                                 | Mô tả chi tiết                                                                                                                                                                                                                                                                                                                               |
| - | ------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|   | **Field Group: Dashboard Summary**   |                                                                                                                                                                                                                                                                                                                                                 |
| 1 | Date Header                                | Data type:`LocalDate`. Default = Ngày hệ thống hiện tại. Hiển thị format "Tuesday, June 2, 2026".                                                                                                                                                                                                                                      |
| 2 | Summary Cards                              | Read-only metrics: (a) Tổng sessions hôm nay, (b) Sessions đã hoàn thành, (c) Phòng điều trị đang trống. Tính toán động từ dataset của ngày.                                                                                                                                                                                 |
|   | **Field Group: Schedule Data Table** |                                                                                                                                                                                                                                                                                                                                                 |
| 3 | Session Details                            | Read-only. Hiển thị: Khung giờ (HH:mm - HH:mm), Tên Guest, Tên dịch vụ, Phòng điều trị. Dữ liệu từ `SCHEDULE JOIN TREATMENT_BOOKING JOIN TREATMENT_SERVICE`.                                                                                                                                                                    |
| 4 | Nút "Xem Ghi Chú"                        | Action: Mở Modal hiển thị ghi chú sức khỏe của Guest.**RBAC Constraint**: API `/api/v1/spa/health-notes/{treatment_id}` chỉ trả về `injuries` và `medical_conditions` từ `PHYSICAL_HEALTH_PROFILE`. **Tuyệt đối không trả về** bất kỳ trường nào từ `DIETARY_PROFILE`. Áp dụng **BR-07**. |
| 5 | Status Dropdown                            | Data type: Enum. Giá trị:`Đang chờ (Scheduled)`, `Hoàn thành (Completed)`, `Vắng (No-Show)`. Action: Thay đổi giá trị → commit state change ngay lập tức. Marking `Completed` → trigger billing logic cho extra-package session (BR-11).                                                                                 |

#### RBAC — Data Minimization Matrix cho Màn hình này

| Dữ liệu                                | Therapist | Hiển thị?               |
| ---------------------------------------- | --------- | ------------------------- |
| Guest Name (để nhận diện)            | ✅        | Có                       |
| Session Time & Room                      | ✅        | Có                       |
| Injuries (chấn thương cơ thể)       | ✅        | Có — Qua Modal          |
| Medical Conditions (bệnh nền vật lý) | ✅        | Có — Qua Modal          |
| Food Allergies                           | ❌        | **Không bao giờ** |
| Dietary Preferences                      | ❌        | **Không bao giờ** |
| CCCD/Passport                            | ❌        | **Không bao giờ** |
| Payment Info                             | ❌        | **Không bao giờ** |

---

### 5.3 Manual Spa Booking Modal (Receptionist)

**[Mô tả]** Overlay dialog cho phép Receptionist đặt thêm dịch vụ Spa ngoài gói cho Guest đang Checked-In. Phí được route trực tiếp vào Guest Folio, bỏ qua POS thanh toán ngay.

**Mapped Use Cases**: UC15

**Screen Authorization**: Chỉ `RECEPTIONIST` có quyền truy cập.

#### Field Descriptions

| # | Field Name                                       | Mô tả chi tiết                                                                                                                                                                                                                                                                                                          |
| - | ------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|   | **Field Group: Guest & Service Selection** |                                                                                                                                                                                                                                                                                                                            |
| 1 | Guest/Room Search Input                          | Data type: String (Autocomplete).**Constraint**: Backend chỉ trả về Guest có `booking_status = 'Checked-In'`. Kết quả hiển thị: Tên Guest + Số Villa.                                                                                                                                                    |
| 2 | Spa Service Dropdown                             | Data type: Dropdown List. Dữ liệu từ `TREATMENT_SERVICE WHERE is_available = 1 AND is_delete = 0`.                                                                                                                                                                                                                    |
|   | **Field Group: Scheduling**                |                                                                                                                                                                                                                                                                                                                            |
| 3 | Date Picker                                      | Data type:`LocalDate`. Default = Ngày hệ thống hiện tại.                                                                                                                                                                                                                                                            |
| 4 | Available Time Slots Grid                        | Data type:`LocalTime` Grid. **Logic**: Tái sử dụng toàn bộ UC12 availability algorithm. Slot Available ↔ có Therapist VÀ Room đồng thời trống.                                                                                                                                                         |
|   | **Field Group: Folio Integration**         |                                                                                                                                                                                                                                                                                                                            |
| 5 | Price Summary                                    | Read-only. Hiển thị: Base Price, Service Fee (VAT 10%), Grand Total.                                                                                                                                                                                                                                                     |
| 6 | Nút "Xác nhận & Ghi nợ vào Folio"           | Action: Form submission.**Strict Backend Constraint**: Dual-write transaction nguyên tử: ① Insert `TREATMENT_BOOKING` + `SCHEDULE` → ② Insert `FOLIO_ITEM` (`service_category='Extra Spa'`, `status='Pending'`, `create_by=receptionist_id`). Nếu một trong hai thất bại → Rollback toàn bộ. |

---

## 6. Business Rules áp dụng

| BR ID | Tên Rule                                | Mô tả chi tiết áp dụng cho Module 3                                                                                                                                                                                                        | UC tham chiếu         |
| ----- | ---------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- |
| BR-04 | Dual Resource Spa Scheduling             | Một Spa appointment CHỈ hợp lệ khi HỆ THỐNG xác nhận đồng thời tồn tại ít nhất 1 Therapist khả dụng VÀ ít nhất 1 Treatment Room khả dụng tại thời điểm đó. Logic được implement qua DB transaction với locking. | UC11, UC12, UC15       |
| BR-05 | Spa Service Eligibility & Update Control | Guest chỉ được đặt các dịch vụ Spa có trong gói Retreat đã mua. Chỉ Therapist được phân công mới có quyền cập nhật session status. Dịch vụ ngoài gói chỉ Receptionist được thêm.                               | UC11, UC14, UC15       |
| BR-07 | RBAC & Data Minimization                 | Therapist CHỈ được truy cập physical health notes (injuries, medical conditions). KHÔNG bao giờ được truy cập dietary/allergy information. Enforce tại backend API layer, không phải frontend.                                    | UC13                   |
| BR-11 | Guest Folio & Consolidated Billing       | Mọi phí Spa ngoài gói (extra service) phải được ghi vào `GUEST_FOLIO` dưới dạng `FOLIO_ITEM` với `service_category = 'Extra Spa'` và `status = 'Pending'`. Thanh toán thực hiện khi Checkout.                          | UC14, UC15             |
| BR-15 | Audit Trail Management                   | Hệ thống ghi audit log cho mọi hành động: đặt lịch, xem ghi chú sức khỏe, cập nhật status, ghi nợ Folio. Log tối thiểu gồm: actor_id, action_type, entity_id, timestamp.                                                      | UC11, UC13, UC14, UC15 |
| BR-17 | Spa Notification & Synchronization       | Sau khi đặt lịch thành công: gửi email xác nhận ngay. Gửi reminder 1 giờ trước session. Notification failure KHÔNG rollback booking đã confirmed.                                                                                | UC11                   |

---

## 7. Data Model — Bảng dữ liệu liên quan

### 7.1 TREATMENT_SERVICE

| # | Field            | PK | FK | NN | Mô tả                                                      |
| - | ---------------- | -- | -- | -- | ------------------------------------------------------------ |
| 1 | service_id       | X  |    | X  | Unique identifier (auto-increment)                           |
| 2 | treatment_code   |    |    | X  | Mã dịch vụ duy nhất (tối đa 10 ký tự)                |
| 3 | service_name     |    |    |    | Tên hiển thị dịch vụ (vd: Swedish Massage)              |
| 4 | duration_minutes |    |    |    | Thời lượng dịch vụ (phút) — dùng để tính end_time |
| 5 | price            |    |    |    | Giá niêm yết (VND/USD)                                    |
| 6 | is_available     |    |    |    | Trạng thái khả dụng (1: Available, 0: Tạm ngừng)       |
| 7 | is_delete        |    |    |    | Soft delete flag                                             |

### 7.2 TREATMENT_ROOM

| # | Field     | PK | FK | NN | Mô tả                                                      |
| - | --------- | -- | -- | -- | ------------------------------------------------------------ |
| 1 | room_id   | X  |    | X  | Unique identifier (auto-increment)                           |
| 2 | room_code |    |    | X  | Mã phòng duy nhất                                         |
| 3 | image     |    |    |    | Đường dẫn ảnh thực tế của phòng                     |
| 4 | room_name |    |    |    | Tên hiển thị phòng Spa                                   |
| 5 | status    |    |    |    | Trạng thái vật lý:`Available / Occupied / Maintenance` |
| 6 | is_delete |    |    |    | Soft delete flag                                             |

### 7.3 THERAPIST

| # | Field          | PK | FK   | NN | Mô tả                                                             |
| - | -------------- | -- | ---- | -- | ------------------------------------------------------------------- |
| 1 | therapist_id   | X  | USER | X  | FK → USER(user_id) — cũng là identifier                         |
| 2 | therapist_code |    |      | X  | Mã Therapist duy nhất (6 ký tự) — dùng làm FK trong SCHEDULE |
| 3 | status         |    |      |    | Trạng thái làm việc:`Available / Busy / Leave`                |

### 7.4 TREATMENT_BOOKING

| # | Field        | PK | FK                            | NN | Mô tả                                                       |
| - | ------------ | -- | ----------------------------- | -- | ------------------------------------------------------------- |
| 1 | treatment_id | X  |                               | X  | Unique identifier cho Spa appointment (auto-increment)        |
| 2 | booking_id   |    | BOOKING(booking_id)           | X  | Liên kết với Resort Booking gốc để xác nhận lưu trú |
| 3 | folio_id     |    | GUEST_FOLIO(folio_id)         |    | Nullable: chỉ điền khi là extra service (ghi nợ Folio)   |
| 4 | service_id   |    | TREATMENT_SERVICE(service_id) | X  | Dịch vụ Spa được chọn                                   |
| 5 | note         |    |                               |    | Ghi chú đặc biệt từ Guest hoặc chỉ dẫn điều trị    |
| 6 | status       |    |                               |    | Trạng thái:`Scheduled / Completed / No-Show / Cancelled`  |
| 7 | is_delete    |    |                               |    | Soft delete flag                                              |
| 8 | create_at    |    |                               |    | Thời điểm tạo appointment                                 |
| 9 | update_at    |    |                               |    | Thời điểm cập nhật gần nhất                            |

### 7.5 SCHEDULE

| # | Field          | PK | FK                              | NN | Mô tả                                                               |
| - | -------------- | -- | ------------------------------- | -- | --------------------------------------------------------------------- |
| 1 | schedule_id    | X  |                                 | X  | Unique identifier (auto-increment)                                    |
| 2 | treatment_id   |    | TREATMENT_BOOKING(treatment_id) | X  | Liên kết với appointment                                           |
| 3 | therapist_code |    | THERAPIST(therapist_code)       |    | Therapist được phân công — FK để kiểm tra conflict           |
| 4 | room_id        |    | TREATMENT_ROOM(room_id)         |    | Phòng điều trị được phân công — FK để kiểm tra conflict  |
| 5 | start_time     |    |                                 |    | Giờ bắt đầu điều trị thực tế                                 |
| 6 | end_time       |    |                                 |    | Giờ kết thúc ước tính (= start_time + service.duration_minutes) |
| 7 | is_delete      |    |                                 |    | Soft delete flag — dùng để giải phóng slot khi cancel           |

> **Lưu ý thiết kế quan trọng**: Double-booking prevention được thực thi bằng cách kiểm tra overlap trên bảng `SCHEDULE` theo điều kiện:
> `NOT (end_time <= :requested_start OR start_time >= :requested_end) AND is_delete = 0`
> cho cả `therapist_code` lẫn `room_id` trong cùng một transaction với isolation `SERIALIZABLE`.

---

## 8. RBAC & Data Minimization

### 8.1 Authorization Matrix — Module 3

| Endpoint / Action                                        | GUEST | RECEPTIONIST | THERAPIST | ADMIN |
| -------------------------------------------------------- | ----- | ------------ | --------- | ----- |
| GET `/api/v1/spa/services` (list available services)   | ✅    | ✅           | ✅        | ✅    |
| GET `/api/v1/spa/availability`                         | ✅    | ✅           | ❌        | ✅    |
| POST `/api/v1/spa/bookings` (self-booking)             | ✅    | ❌           | ❌        | ❌    |
| POST `/api/v1/spa/bookings/manual` (receptionist book) | ❌    | ✅           | ❌        | ❌    |
| GET `/api/v1/spa/schedule/my` (therapist daily)        | ❌    | ❌           | ✅ (own)  | ✅    |
| GET `/api/v1/spa/health-notes/{id}` (physical only)    | ❌    | ❌           | ✅ (own)  | ✅    |
| PATCH `/api/v1/spa/bookings/{id}/status`               | ❌    | ❌           | ✅ (own)  | ✅    |

**Chú thích:**

- ✅ = Được phép
- ❌ = Bị từ chối (HTTP 403 + MSG-18)
- `(own)` = Chỉ được phép với resource mình được phân công

### 8.2 Data Access Rules — Physical Health Data

> **CRITICAL — Compliance Requirement (Decree 356/2025)**

| Endpoint                          | Trả về                                                                            | KHÔNG trả về                                                              |
| --------------------------------- | ----------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| `/api/v1/spa/health-notes/{id}` | `medical_conditions`, `injuries` (PHYSICAL_HEALTH_PROFILE chỉ relevant fields) | `food_allergies`, `diatary_preference` (DIETARY_PROFILE — TUYỆT ĐỐI) |

Cơ chế enforcement:

1. Backend API sử dụng DTO riêng biệt cho từng role.
2. Không bao giờ trả về entity đầy đủ — luôn map sang DTO với projection cụ thể.
3. Unit test bắt buộc xác nhận response không chứa dietary fields.

---

## 9. Non-Functional Requirements

### 9.1 Performance

| ID       | Yêu cầu                         | Target     | Đo lường          |
| -------- | --------------------------------- | ---------- | -------------------- |
| PF-M3-01 | Availability check response time  | ≤ 500ms   | k6 load test         |
| PF-M3-02 | Spa booking confirmation (UC11)   | ≤ 3 giây | APM monitoring       |
| PF-M3-03 | Therapist daily schedule load     | ≤ 1 giây | Lighthouse           |
| PF-M3-04 | Manual booking (UC15) transaction | ≤ 3 giây | DB query plan review |

### 9.2 Security & Data Integrity

| ID        | Yêu cầu                             | Target                      | Compliance Basis |
| --------- | ------------------------------------- | --------------------------- | ---------------- |
| SEC-M3-01 | Mã hóa Physical Health Data tại DB | AES-256 hoặc DB encryption | Decree 356/2025  |
| SEC-M3-02 | Audit log cho mọi health data access | 100% coverage               | BR-15            |
| SEC-M3-03 | RBAC enforcement tại API layer       | Zero bypass                 | BR-07            |
| SEC-M3-04 | Transaction isolation cho booking     | SERIALIZABLE                | BR-04            |

### 9.3 Usability

| ID       | Yêu cầu                                        | Measurement |
| -------- | ------------------------------------------------ | ----------- |
| US-M3-01 | Guest đặt lịch Spa từ đầu đến xác nhận | ≤ 3 phút  |
| US-M3-02 | Therapist update session status                  | ≤ 30 giây |
| US-M3-03 | Receptionist hoàn thành manual booking         | ≤ 2 phút  |

---

## 10. System Messages

| Code   | Type    | Trigger Context                               | Message                                                                                              |
| ------ | ------- | --------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| MSG-09 | Success | Spa appointment booked successfully (UC11/15) | "Đặt lịch Spa thành công!"                                                                      |
| MSG-10 | Error   | No therapist/room available (UC11/12)         | "Không tìm thấy Therapist hoặc Phòng điều trị khả dụng. Vui lòng chọn thời gian khác." |
| MSG-18 | Error   | Unauthorized access attempt                   | "Bạn không có quyền thực hiện hành động này."                                              |
| MSG-19 | Error   | Unexpected system error during transaction    | "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."                                             |

---

## 11. Traceability Matrix

| Requirement ID | Loại    | Mô tả ngắn                                | Thành phần Code (dự kiến)                                                                  | Business Rule | UC liên quan    |
| -------------- | -------- | -------------------------------------------- | ---------------------------------------------------------------------------------------------- | ------------- | ---------------- |
| FRS-M3-001     | Func     | Guest đặt lịch Spa session trong gói     | `SpaController.POST /bookings`, `SpaSchedulingService.bookSession()`                       | BR-04, BR-05  | UC11, UC12       |
| FRS-M3-002     | Func     | Auto-matching Therapist + Room               | `ResourceMatchingService.findAvailable()`, `ScheduleRepository`                            | BR-04         | UC12             |
| FRS-M3-003     | Func     | Therapist xem lịch ngày                    | `TherapistController.GET /schedule/my`, `ScheduleService.getDailySchedule()`               | BR-07         | UC13             |
| FRS-M3-004     | Func     | Therapist đọc ghi chú sức khỏe vật lý | `HealthNoteController.GET /health-notes/{id}`, `PhysicalHealthRepository` (DTO projection) | BR-07         | UC13             |
| FRS-M3-005     | Func     | Therapist cập nhật trạng thái session    | `SpaController.PATCH /bookings/{id}/status`, `SessionStatusService.update()`               | BR-05, BR-11  | UC14             |
| FRS-M3-006     | Func     | Receptionist đặt Spa ngoài gói           | `SpaController.POST /bookings/manual`, `FolioService.postCharge()`                         | BR-04, BR-11  | UC15             |
| FRS-M3-007     | Security | RBAC dietary data exclusion tại API         | `HealthNoteMapper.toDto()` (whitelist projection)                                            | BR-07         | UC13             |
| FRS-M3-008     | Security | DB Transaction isolation cho booking         | `@Transactional(isolation=SERIALIZABLE)` trên `SpaSchedulingService`                      | BR-04         | UC11, UC12, UC15 |
| FRS-M3-009     | Audit    | Audit log cho mọi spa action                | `AuditLogService.log()` — gọi trong mọi service method của module                        | BR-15         | UC11–UC15       |
| FRS-M3-010     | Func     | Gửi notification sau booking thành công   | `NotificationService.sendSpaConfirmation()`, async via Calendar API                          | BR-17         | UC11             |

---

## PHỤ LỤC

### A. Glossary

| Thuật ngữ              | Định nghĩa                                                                                                                      |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| Double-booking           | Tình trạng cùng một Therapist hoặc Treatment Room được phân công cho 2 session khác nhau trong cùng khoảng thời gian |
| Dual Resource Constraint | Quy tắc BR-04: Cả Therapist VÀ Room phải đồng thời khả dụng mới được xác nhận booking                               |
| Guest Folio              | Tài khoản tổng hợp chi phí của Guest trong thời gian lưu trú, liên kết với Booking_ID                                  |
| Extra Spa Service        | Dịch vụ Spa ngoài phạm vi gói Retreat đã mua, được Receptionist thêm và ghi vào Folio                                 |
| Physical Health Notes    | Dữ liệu sức khỏe vật lý (chấn thương, bệnh nền) từ PHYSICAL_HEALTH_PROFILE — chỉ Therapist được xem               |
| RBAC                     | Role-Based Access Control — kiểm soát quyền truy cập dựa trên vai trò người dùng                                        |
| Data Minimization        | Nguyên tắc chỉ cung cấp dữ liệu tối thiểu cần thiết cho từng role                                                       |
| PII                      | Personally Identifiable Information — Thông tin cá nhân có thể nhận dạng                                                   |
| Session Status           | Trạng thái của Spa session: Scheduled → Completed / No-Show / Cancelled                                                        |

### B. Tài liệu tham chiếu

| Tài liệu               | Đường dẫn / Link                                                         |
| ------------------------ | ---------------------------------------------------------------------------- |
| SRS Document (HOS-03)    | `c:\Workspace\SWP391\02_Requirement\SRS_Document.md`                       |
| SDS Document             | `c:\Workspace\SWP391\03_Design\SDS_Document.md`                            |
| Database Schema (DB.sql) | `c:\Workspace\SWP391\03_Design\Database\DB.sql`                            |
| EDS Template v2.0        | `c:\Workspace\SWP391\08_Document-Reference\Templates\EDS_TEMPLATE_V2.0.md` |
| TDD Template v1.0        | `c:\Workspace\SWP391\08_Document-Reference\Templates\TDD_TEMPLATE_V1.md`   |
| Decree 356/2025          | Personal Data Protection — Vietnam                                          |
| Residence Law 2020       | Accommodation registration requirements                                      |

---

*FRS Module 3 v1.0 — Spa & Therapy Scheduling Engine — Xoai Aura Retreat (HOS-03)*
*Nhóm SE2023-G6 — SWP391*
