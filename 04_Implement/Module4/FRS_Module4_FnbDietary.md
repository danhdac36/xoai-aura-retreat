# ĐẶC TẢ YÊU CẦU CHỨC NĂNG (FUNCTIONAL REQUIREMENTS SPECIFICATION)

# Module 4 — Quản lý Ẩm thực F&B & Ăn kiêng (F&B Culinary & Dietary Management)

---

| Trường       | Giá trị                                                       |
| -------------- | --------------------------------------------------------------- |
| Document ID    | `HOS-M4-FRS-001`                                              |
| Version        | 1.0                                                             |
| Ngày tạo     | 2026-06-23                                                      |
| Trạng thái   | Draft                                                           |
| Document Owner | Nhóm phát triển SE2023-G6                                    |
| Author         | Lê Đức Dương                                                    |
| Reviewed by    | Tech Lead                                                       |
| DPO Sign-off   | [x] Approved *(bắt buộc — module xử lý Food Allergies PII)*  |
| Approved by    | Principal Architect                                             |
| Based on SRS   | SRS_Document.md (HOS-04)                                        |
| Based on EDS   | EDS_TEMPLATE_V2.0                                               |

---

## CHANGELOG

| Ngày      | Người thực hiện | Nội dung thay đổi                    |
| ---------- | ------------------- | --------------------------------------- |
| 2026-06-23 | Lê Đức Dương         | Khởi tạo tài liệu FRS Module 4 v1.0    |

---

## MỤC LỤC

1. [Tổng quan Module](#1-tổng-quan-module)
2. [Actors &amp; Phạm vi](#2-actors--phạm-vi)
3. [Danh sách Use Cases của Module 4](#3-danh-sách-use-cases-của-module-4)
4. [Use Case Specifications (Đặc tả Chi tiết)](#4-use-case-specifications-đặc-tả-chi-tiết)
   - 4.1 UC16 — Pre-select Daily Meals (Guest)
   - 4.2 UC17 — View Daily Meal Preparation Dashboard (Chef/F&B Staff)
   - 4.3 UC18 — Update Meal Order Status (Chef)
   - 4.4 UC19 — Book Additional A-la-carte Meal/Drink (Guest)
   - 4.5 UC20 — Data Minimization & Privacy Protection (System Constraint)
5. [Functional Requirements — Đặc tả màn hình](#5-functional-requirements--đặc-tả-màn-hình)
   - 5.1 Guest Meal Selection Screen
   - 5.2 Chef Daily Meal Prep Dashboard Screen
   - 5.3 Guest A-la-carte Ordering Screen
6. [Business Rules áp dụng](#6-business-rules-áp-dụng)
7. [Data Model — Bảng dữ liệu liên quan](#7-data-model--bảng-dữ-liệu-liên-quan)
8. [RBAC &amp; Data Minimization](#8-rbac--data-minimization)
9. [Non-Functional Requirements](#9-non-functional-requirements)
10. [System Messages](#10-system-messages)
11. [Traceability Matrix](#11-traceability-matrix)

---

## 1. Tổng quan Module

### 1.1 Mô tả

**Module 4 — Quản lý Ẩm thực F&B & Ăn kiêng** quản lý việc chọn món ăn hàng ngày trong gói Retreat của khách hàng (Guest) và gọi thêm đồ ăn thức uống ngoài gói (A-la-carte), đồng thời cung cấp bảng điều khiển (Dashboard) cho Đầu bếp để chuẩn bị món ăn an toàn dựa trên hồ sơ dị ứng thực phẩm của khách. Module này giải quyết bài toán cốt lõi: bảo vệ sức khỏe của khách hàng thông qua kiểm tra dị ứng tự động 100% realtime và tuân thủ nghiêm ngặt nguyên tắc hạn chế dữ liệu (Data Minimization) theo Nghị định 356/2025/NĐ-CP (che giấu hoàn toàn tiền sử bệnh án vật lý với Đầu bếp, chỉ hiển thị thông tin dị ứng thực phẩm cần thiết để chế biến món ăn).

Module 4 phục vụ ba nhóm actor chính:

- **Guest**: Chọn trước bữa ăn hàng ngày nằm trong gói Retreat và gọi thêm đồ ăn thức uống A-la-carte ngoài gói.
- **Chef / F&B Staff**: Xem danh sách chuẩn bị món ăn tổng hợp trong ngày kèm cảnh báo dị ứng thực phẩm, cập nhật trạng thái chuẩn bị món ăn.
- **System**: Tự động lọc thực đơn an toàn, đồng bộ hóa hóa đơn gọi món ngoài gói vào Guest Folio tức thời.

| Trường              | Giá trị                                                                    |
| --------------------- | ---------------------------------------------------------------------------- |
| Module Name           | Quản lý Ẩm thực F&B & Ăn kiêng (F&B Culinary & Dietary Management)          |
| Bounded Context       | `fnb`                                                                      |
| Data Classification   | **Sensitive-PII** (Food Allergies)                                         |
| Compliance Scope      | Nghị định 356/2025/NĐ-CP — Bảo vệ Dữ liệu Cá nhân                           |
| Upstream Dependencies | `auth` (RBAC/JWT), `booking` (booking_id, guest_id), `dietary` (food allergies) |
| Downstream Consumers  | `billing` (Guest Folio, Folio Item charges)                                 |

### 1.2 Phạm vi nghiệp vụ

Module 4 bao gồm:

| Chức năng                                          | Actor        | UC   |
| ---------------------------------------------------- | ------------ | ---- |
| Chọn trước bữa ăn hàng ngày (trong gói Retreat)     | Guest        | UC16 |
| Xem bảng điều khiển chuẩn bị bữa ăn hàng ngày       | Chef / F&B   | UC17 |
| Cập nhật trạng thái chuẩn bị món ăn                 | Chef         | UC18 |
| Đặt thêm đồ ăn/thức uống A-la-carte ngoài gói       | Guest        | UC19 |
| Che giấu bệnh án, chỉ hiển thị dị ứng thực phẩm     | System (Constraint) | UC20 |

**Ngoài phạm vi Module 4:**

- Quản lý Master Data Menu items → Module Admin (Module 1)
- Xử lý thanh toán hóa đơn gộp cuối kỳ → Module 5 (Billing)
- Quản lý sức khỏe vật lý và trị liệu → Module 3 (Spa)

---

## 2. Actors & Phạm vi

| # | Actor                                   | Vai trò trong Module 4                                                              |
| - | --------------------------------------- | ------------------------------------------------------------------------------------ |
| 1 | **Guest**                         | Đặt món ăn hàng ngày từ gói Retreat; Đặt món ngoài gói A-la-carte; Xem thông báo. |
| 2 | **Chef / F&B Staff**              | Xem dashboard chuẩn bị món; Xem cảnh báo dị ứng thực phẩm; Cập nhật trạng thái đơn. |
| 3 | **System**                        | Tự động so khớp dị ứng để lọc menu; Đồng bộ phí phụ thu a-la-carte vào Guest Folio.|

---

## 3. Danh sách Use Cases của Module 4

| UC ID | Tên Use Case                             | Actor chính  | Ưu tiên | Tài liệu EDS tham chiếu |
| ----- | ----------------------------------------- | ------------- | --------- | ----------------------- |
| UC16  | Pre-select Daily Meals                    | Guest         | High      | [EDS_FNB_UC16.md](file:///c:/Workspace/SWP391/04_Implement/Module4/UC16/EDS_FNB_UC16.md) |
| UC17  | View Daily Meal Prep Dashboard            | Chef          | High      | [EDS_FNB_UC17.md](file:///c:/Workspace/SWP391/04_Implement/Module4/UC17/EDS_FNB_UC17.md) |
| UC18  | Update Meal Order Status                  | Chef          | High      | [EDS_FNB_UC18.md](file:///c:/Workspace/SWP391/04_Implement/Module4/UC18/EDS_FNB_UC18.md) |
| UC19  | Book Additional A-la-carte Meal/Drink     | Guest         | Medium    | [EDS_FNB_UC19.md](file:///c:/Workspace/SWP391/04_Implement/Module4/UC19/EDS_FNB_UC19.md) |
| UC20  | Data Minimization (F&B)                   | System (Auto) | High      | [EDS_FNB_UC20.md](file:///c:/Workspace/SWP391/04_Implement/Module4/UC20/EDS_FNB_UC20.md) |

---

## 4. Use Case Specifications (Đặc tả Chi tiết)

### 4.1 UC16 — Pre-select Daily Meals

| Trường                   | Nội dung                                                                                                                                                                                                                                   |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC16 — Pre-select Daily Meals                                                                                                                                                                                                              |
| **Primary Actor**    | Guest                                                                                                                                                                                                                                       |
| **Secondary Actors** | System, Chef                                                                                                                                                                                                                                |
| **Mô tả**          | Guest có booking Retreat Package đang ở trạng thái `Checked-In` hoặc `ACTIVE` thực hiện chọn trước các món ăn miễn phí hàng ngày từ thực đơn đã được hệ thống tự động lọc các thành phần gây dị ứng dựa trên hồ sơ dị ứng thực phẩm của họ.  |
| **Trigger**          | Guest chọn "Chọn món ăn hàng ngày" từ Guest Dashboard.                                                                                                                                                                                      |

#### Preconditions (Điều kiện tiên quyết)

- [PRE-1] Guest đã xác thực và có JWT/Session hợp lệ với role `GUEST`.
- [PRE-2] Guest có ít nhất một Booking đang ở trạng thái `Checked-In` hoặc `ACTIVE`.
- [PRE-3] Gói Retreat của Guest còn số lượng phần ăn miễn phí trong ngày (Giới hạn tối đa = `totalGuests * 3` phần ăn/ngày).

#### Postconditions (Kết quả sau thực hiện)

- [POST-1] Một bản ghi `MEAL_ORDER` mới được tạo với trạng thái `PENDING`, cờ `isExtraCharge = false`.
- [POST-2] Các bản ghi `MEAL_ORDER_ITEM` được tạo để lưu thông tin chi tiết các món ăn đã chọn.
- [POST-3] Số lượng phần ăn miễn phí khả dụng trong ngày của booking được cập nhật giảm đi tương ứng.
- [POST-4] Audit log ghi nhận hành động đặt món.

#### Normal Flow (Luồng chính)

| Bước | Actor  | Hành động                                                                                                                |
| ------ | ------ | --------------------------------------------------------------------------------------------------------------------------- |
| 1      | Guest  | Mở màn hình Chọn món ăn hàng ngày (`/fnb/meal-selection`).                                                                 |
| 2      | System | Gọi API lấy thông tin Booking, đếm số lượng phần ăn miễn phí đã đặt hôm nay để tính số phần ăn còn lại (`maxMeals`).       |
| 3      | System | Truy vấn thực đơn khả dụng (`menuitem.is_available = 1`) và tự động so khớp nguyên liệu với trường `food_allergies` của Guest (BR-FNB-001). |
| 4      | System | Hiển thị danh sách món ăn an toàn (Safe Menu), che giấu hoặc lọc bỏ hoàn toàn các món có thành phần chứa nguyên liệu dị ứng. |
| 5      | Guest  | Lựa chọn món ăn và nhập số lượng mong muốn.                                                                                |
| 6      | Guest  | Nhấn nút "Xác nhận đặt món ăn".                                                                                             |
| 7      | System | Backend kiểm tra lại tính hợp lệ: `alreadyOrdered + requestedQuantity <= totalGuests * 3` (BR-FNB-007).                     |
| 8      | System | Re-check allergen conflict một lần nữa tại Backend để tránh Bypass dữ liệu dị ứng thực phẩm.                                |
| 9      | System | Lưu `MEAL_ORDER` (trạng thái `PENDING`) và các `MEAL_ORDER_ITEM` tương ứng.                                                 |
| 10     | System | Ghi Audit Log hành động đặt món thành công.                                                                                 |
| 11     | System | Hiển thị thông báo thành công và chuyển hướng về màn hình chọn món (MSG-FNB-001).                                           |

#### Alternative Flows (Luồng thay thế)

- Không có luồng thay thế.

#### Exception Flows (Luồng ngoại lệ)

| ID | Điều kiện                                                    | Xử lý                                                                       |
| -- | --------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| E1 | Guest cố tình gửi ID món ăn có nguyên liệu trùng dị ứng thực phẩm | Hệ thống từ chối lưu, ném ngoại lệ `FNB-001` kèm thông báo lỗi cụ thể (MSG-FNB-002). |
| E2 | Vượt quá số lượng phần ăn miễn phí tối đa trong ngày của phòng   | Hệ thống từ chối lưu, trả lỗi `FNB-005` yêu cầu giảm số lượng hoặc đặt A-la-carte.|
| E3 | Trạng thái Booking của khách không phải là `Checked-In` hoặc `ACTIVE` | Hệ thống từ chối truy cập và báo lỗi "Lượt đặt phòng không ở trạng thái ACTIVE". |

---

### 4.2 UC17 — View Daily Meal Preparation Dashboard

| Trường                   | Nội dung                                                                                                                                                                                          |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC17 — View Daily Meal Preparation Dashboard                                                                                                                                                      |
| **Primary Actor**    | Chef / F&B Staff                                                                                                                                                                                   |
| **Secondary Actors** | System                                                                                                                                                                                             |
| **Mô tả**          | Đầu bếp truy cập Dashboard để xem danh sách tổng hợp các đơn đặt món ăn cần chuẩn bị trong ngày, kèm theo cảnh báo dị ứng thực phẩm tương ứng (không bao gồm thông tin bệnh lý khác).              |
| **Trigger**          | Đầu bếp mở màn hình "Daily Preparation Dashboard" sau khi đăng nhập.                                                                                                                               |

#### Preconditions

- [PRE-1] Đầu bếp đã đăng nhập với JWT/Session hợp lệ có role `CHEF`.

#### Postconditions

- [POST-1] Hiển thị danh sách đơn đặt món trong ngày theo thời gian đặt.
- [POST-2] Hiển thị rõ các nhãn cảnh báo dị ứng cho từng món ăn/đơn hàng nếu có conflict.
- [POST-3] Tuyệt đối không hiển thị tiền sử bệnh án hay thông tin sức khỏe vật lý của khách hàng (BR-FNB-002).

#### Normal Flow

| Bước | Actor  | Hành động                                                                                                                                                                                                        |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Chef   | Truy cập màn hình Dashboard (`/fnb/chef/dashboard`).                                                                                                                                                               |
| 2      | System | Tự động xác thực quyền `CHEF` và lấy ngày làm việc hiện tại (hoặc ngày chọn từ Date Picker).                                                                                                                       |
| 3      | System | Truy vấn danh sách `MEAL_ORDER` trong ngày đã chọn.                                                                                                                                                                 |
| 4      | System | Với mỗi đơn hàng, hệ thống lấy `foodAllergies` của khách hàng thông qua `DietaryProfileRepository`.                                                                                                                |
| 5      | System | So khớp thành phần món ăn với `foodAllergies`. Nếu phát hiện trùng, đặt nhãn cảnh báo `isAllergic = true` và `hasAllergyWarning = true`.                                                                           |
| 6      | System | Tạo DTO `ChefDashboardOrderResponse` **chỉ chứa thông tin dị ứng thực phẩm** (bỏ qua các trường bệnh lý của Module 1 & Module 3).                                                                                  |
| 7      | System | Render giao diện Dashboard hiển thị danh sách chi tiết món ăn cần nấu, số lượng, số phòng Villa, trạng thái chuẩn bị, và cảnh báo dị ứng màu đỏ nổi bật (nếu có).                                                  |

#### Exception Flows

| ID | Điều kiện                               | Xử lý                                              |
| -- | ------------------------------------------ | ---------------------------------------------------- |
| E1 | Nhân viên không có vai trò CHEF truy cập | Hệ thống chặn và trả lỗi HTTP 403 Forbidden.         |

---

### 4.3 UC18 — Update Meal Order Status

| Trường                | Nội dung                                                                                                               |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**     | UC18 — Update Meal Order Status                                                                                        |
| **Primary Actor** | Chef / F&B Staff                                                                                                       |
| **Mô tả**       | Đầu bếp cập nhật trạng thái chuẩn bị của đơn hàng theo chu trình tịnh tiến (State Machine), ngăn chặn việc cập nhật ngược trạng thái gây nhầm lẫn trong bếp. |
| **Trigger**       | Đầu bếp bấm nút chuyển trạng thái của đơn hàng trên Dashboard.                                                         |

#### Preconditions

- [PRE-1] Đầu bếp đã đăng nhập với role `CHEF`.
- [PRE-2] Đơn hàng được chọn tồn tại trong hệ thống và chưa hoàn thành hoàn toàn.

#### Postconditions

- [POST-1] Trạng thái `orderStatus` của đơn hàng `MEAL_ORDER` được cập nhật thành công.
- [POST-2] Nếu đơn hàng bị `CANCELLED`, hệ thống tự động void (hủy bỏ) hóa đơn phụ thu tương ứng trong Guest Folio (chỉ áp dụng với đơn A-la-carte).
- [POST-3] Audit Log ghi nhận hành động thay đổi trạng thái của Chef.

#### Normal Flow

| Bước | Actor     | Hành động                                                                                                                                                        |
| ------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Chef      | Chọn cập nhật trạng thái mới của đơn hàng từ Dashboard.                                                                                                            |
| 2      | System    | Kiểm tra tính hợp lệ của việc chuyển đổi trạng thái dựa theo State Machine (BR-FNB-003).                                                                           |
| 3      | System    | Cập nhật trường `orderStatus` của đơn hàng trong Database.                                                                                                         |
| 4      | System    | **Nếu chuyển sang `CANCELLED`**: Tìm kiếm `FolioItem` liên kết với đơn F&B này (Category = `Extra F&B`, ReferenceId = `orderId`).                                    |
| 5      | System    | Nếu tìm thấy: Chuyển trạng thái `FolioItem` thành `VOIDED`, thực hiện trừ tiền tương ứng khỏi `totalExtraFb` và `finalAmount` của `GuestFolio`.                      |
| 6      | System    | Ghi Audit Log.                                                                                                                                                      |
| 7      | System    | Làm mới Dashboard và hiển thị thông báo thành công.                                                                                                                 |

#### State Machine — Meal Order Status

```
[PENDING] ──── (Chef nhận đơn) ────→ [PREPARING] ──── (Nấu xong) ────→ [READY] ──── (Phục vụ) ────→ [DELIVERED]
    │
 (Chef/Admin Hủy)
    ▼
[CANCELLED]
```

> ⚠️ **Quy tắc bất biến**: Trạng thái chỉ được tịnh tiến theo chiều tiến, không được phép chuyển ngược lại (ví dụ: từ `READY` quay lại `PREPARING` là không hợp lệ).

#### Exception Flows

| ID | Điều kiện                               | Xử lý                                                             |
| -- | ------------------------------------------ | ------------------------------------------------------------------- |
| E1 | Cố tình lùi trạng thái (ví dụ: `READY -> PREPARING`) | Backend từ chối, ném lỗi `FNB-001` (Trạng thái chuyển đổi không hợp lệ). |
| E2 | Đơn hàng không tồn tại                  | Trả lỗi `FNB-003` (Không tìm thấy đơn hàng).                        |

---

### 4.4 UC19 — Book Additional A-la-carte Meal/Drink

| Trường                   | Nội dung                                                                                                                                                                   |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC19 — Book Additional A-la-carte Meal/Drink                                                                                                                               |
| **Primary Actor**    | Guest                                                                                                                                                                       |
| **Secondary Actors** | System, Guest Folio Service                                                                                                                                                 |
| **Mô tả**          | Guest đặt thêm các món ăn/thức uống ngoài gói tiêu chuẩn của Retreat (A-la-carte). Chi phí phát sinh được tính trực tiếp và cập nhật realtime vào Guest Folio của phòng.   |
| **Trigger**          | Guest chọn "Đặt món ăn A-la-carte" từ Guest Dashboard.                                                                                                                      |

#### Preconditions

- [PRE-1] Guest đã đăng nhập với role `GUEST`.
- [PRE-2] Booking của phòng có trạng thái `Checked-In` hoặc `ACTIVE`.
- [PRE-3] `GuestFolio` đã được khởi tạo cho Booking đó.

#### Postconditions

- [POST-1] Đơn hàng `MEAL_ORDER` được lưu thành công với trạng thái `PENDING`, cờ `isExtraCharge = true`.
- [POST-2] Một `FolioItem` mới với category = `Extra F&B` được tạo để lưu chi phí phụ thu.
- [POST-3] Cập nhật tăng `totalExtraFb` và `finalAmount` trong `GuestFolio` của phòng tương ứng.
- [POST-4] Audit Log ghi nhận.

#### Normal Flow

| Bước | Actor  | Hành động                                                                                                                                                                                                                |
| ------ | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1      | Guest        | Mở màn hình Đặt món A-la-carte (`/fnb/alacarte-order`).                                                                                                                                                                    |
| 2      | System       | Hiển thị thực đơn A-la-carte đã được lọc bỏ các món chứa nguyên liệu trùng hồ sơ dị ứng thực phẩm của Guest (BR-FNB-001).                                                                                                  |
| 3      | Guest        | Lựa chọn món ăn, số lượng và bấm xác nhận đặt món.                                                                                                                                                                         |
| 4      | System       | **Database Transaction nguyên tử (Atomic Transaction)**:<br>① Re-verify dị ứng thực phẩm.<br>② Lưu `MEAL_ORDER` + `MEAL_ORDER_ITEM`.<br>③ Tạo `FolioItem` phụ thu (`status = UNPAID`).<br>④ Cộng dồn chi phí vào `GuestFolio`. |
| 5      | System       | Ghi Audit Log giao dịch.                                                                                                                                                                                                    |
| 6      | System       | Hiển thị MSG-FNB-003 (Đặt món A-La-Carte thành công!).                                                                                                                                                                      |

#### Exception Flows

| ID | Điều kiện                         | Xử lý                                                           |
| -- | ------------------------------------ | ----------------------------------------------------------------- |
| E1 | Phát hiện allergen conflict ở bước submit | Backend từ chối lưu, ném lỗi `FNB-001` và hủy bỏ transaction.   |
| E2 | Một trong các bước lưu DB thất bại | Hệ thống rollback toàn bộ transaction để tránh tình trạng đơn hàng lưu nhưng Folio chưa cập nhật tiền. |

---

### 4.5 UC20 — Data Minimization & Privacy Protection

| Trường                   | Nội dung                                                                                                                                                                   |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID & Tên**        | UC20 — Data Minimization & Privacy Protection                                                                                                                               |
| **Primary Actor**    | System (Automated Constraint)                                                                                                                                               |
| **Mô tả**          | Hệ thống tự động che giấu toàn bộ thông tin bệnh sử y tế (Medical History/Conditions) và chấn thương của khách đối với các Chef/nhân viên F&B. Chỉ chuyển duy nhất trường `foodAllergies` của `DIETARY_PROFILE` xuống tầng xử lý F&B. |

#### Normal Flow

- [FLOW-1] Khi Chef truy cập API `/fnb/chef/dashboard`, Backend chỉ truy vấn thông tin dị ứng từ `DietaryProfileRepository`.
- [FLOW-2] Dữ liệu trả về cho Frontend sử dụng DTO `ChefDashboardOrderResponse` chuyên biệt, hoàn toàn không chứa thuộc tính liên quan đến Medical History hay Physical Conditions.
- [FLOW-3] Nếu bất kỳ API nào phục vụ vai trò CHEF cố gắng truy cập dữ liệu y tế, hệ thống sẽ ném lỗi `SEC-002` (Unauthorized Field Access) và từ chối xử lý.

---

## 5. Functional Requirements — Đặc tả màn hình

### 5.1 Guest Meal Selection Screen (UC16)

**[Mô tả]** Màn hình cho phép khách hàng tự chọn thực đơn miễn phí trong ngày đi kèm gói Retreat.

- **URL**: `/fnb/meal-selection`
- **Authorization**: Chỉ `GUEST` có booking đang active.

#### Mô tả các trường (Field Descriptions)

| # | Tên trường | Loại dữ liệu | Mô tả chi tiết |
| - | ---------- | ------------ | -------------- |
| 1 | Số phần ăn còn lại | Số nguyên (Read-only) | Hiển thị số phần ăn miễn phí tối đa còn được đặt trong ngày hôm nay. |
| 2 | Danh sách thực đơn | Bảng/Grid | Danh sách các món ăn đã được hệ thống tự động lọc (loại bỏ các món chứa nguyên liệu dị ứng của Guest). |
| 3 | Ô chọn số lượng | Dropdown/Number | Lựa chọn số lượng cho món ăn muốn đặt (chỉ hiển thị nếu số phần ăn khả dụng > 0). |
| 4 | Nút "Xác nhận đặt món" | Action Button | Gửi request đặt món ăn. Gọi API `/fnb/meal-selection` (POST). |

#### Luật Validation

- Không cho phép chọn số lượng lớn hơn số phần ăn miễn phí còn lại trong ngày.
- Nút "Xác nhận đặt món" sẽ bị disable nếu số phần ăn còn lại của ngày bằng 0.

---

### 5.2 Chef Daily Meal Prep Dashboard Screen (UC17, UC18)

**[Mô tả]** Màn hình quản lý chế biến món ăn hàng ngày dành riêng cho Đầu bếp và nhân viên bếp.

- **URL**: `/fnb/chef/dashboard`
- **Authorization**: `CHEF`, `ADMIN`.

#### Mô tả các trường (Field Descriptions)

| # | Tên trường | Loại dữ liệu | Mô tả chi tiết |
| - | ---------- | ------------ | -------------- |
| 1 | Bộ lọc ngày | Date Picker | Cho phép Đầu bếp xem danh sách món ăn của ngày khác. Mặc định là ngày hiện tại. |
| 2 | Danh sách đơn hàng | Table | Hiển thị: Mã đơn, Phòng Villa, Giờ đặt, Món ăn, Số lượng, Trạng thái. |
| 3 | Nhãn dị ứng (Allergen Label) | Badge (Màu đỏ) | Hiển thị nổi bật các thành phần dị ứng cụ thể của khách nếu món ăn trùng thành phần dị ứng (ví dụ: **CẢNH BÁO DỊ ỨNG: ĐẬU PHỘNG**). |
| 4 | Trạng thái chuẩn bị | Dropdown | Các trạng thái tịnh tiến: `PENDING`, `PREPARING`, `READY`, `DELIVERED`. Thay đổi trạng thái sẽ submit lên Backend. |

#### Quy tắc hiển thị PII (Data Minimization)

- Chỉ hiển thị trường `foodAllergies`. Cấm hiển thị các trường liên quan đến bệnh nền, chấn thương hay thông tin cá nhân nhạy cảm khác như CCCD/Hộ chiếu.

---

### 5.3 Guest A-la-carte Ordering Screen (UC19)

**[Mô tả]** Màn hình cho phép khách hàng đặt thêm đồ ăn thức uống phụ thu ngoài gói Retreat.

- **URL**: `/fnb/alacarte-order`
- **Authorization**: Chỉ `GUEST` có booking đang active.

#### Mô tả các trường (Field Descriptions)

| # | Tên trường | Loại dữ liệu | Mô tả chi tiết |
| - | ---------- | ------------ | -------------- |
| 1 | Thực đơn A-la-carte | Grid | Danh sách món ăn kèm giá niêm yết (đã tự động lọc dị ứng). |
| 2 | Giá tạm tính | Số thập phân | Tổng tiền món ăn đã chọn. |
| 3 | Thuế dịch vụ | Số thập phân | Thuế/Phí dịch vụ đi kèm. |
| 4 | Tổng cộng phụ thu | Số thập phân | Tổng tiền sẽ được ghi nợ vào phòng khi xác nhận. |
| 5 | Nút "Xác nhận & Ghi nợ vào phòng" | Action Button | Xác nhận đặt món ăn. Kích hoạt transaction ghi nhận đơn hàng và cộng dồn nợ vào Guest Folio. |

---

## 6. Business Rules áp dụng

| BR ID | Tên Rule                                | Mô tả chi tiết áp dụng cho Module 4 | UC tham chiếu |
| ----- | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| **BR-FNB-001** | Allergen-Safe Filter | Thực đơn hiển thị cho Guest bắt buộc phải lọc bỏ các món chứa nguyên liệu nằm trong danh sách dị ứng (`food_allergies`) của họ. | UC16, UC19 |
| **BR-FNB-002** | F&B Data Minimization | Đầu bếp và nhân viên bếp tuyệt đối không được phép tiếp cận dữ liệu bệnh lý vật lý hay chấn thương của khách hàng. Chỉ hiển thị thông tin dị ứng thực phẩm. | UC17, UC20 |
| **BR-FNB-003** | Order Status State Machine | Trạng thái chuẩn bị món ăn chỉ được tịnh tiến theo một chiều: `PENDING` -> `PREPARING` -> `READY` -> `DELIVERED`. Không được phép quay ngược trạng thái. | UC18 |
| **BR-FNB-004** | Backend Allergen Validation | Ngăn chặn tuyệt đối việc đặt món chứa chất gây dị ứng thông qua API Bypass. Nếu phát hiện món chứa chất dị ứng của User, Backend lập tức từ chối và Rollback giao dịch. | UC16, UC19 |
| **BR-FNB-005** | Realtime Folio Synchronization | Phí món ăn A-la-carte phải được ghi nhận lập tức vào `GUEST_FOLIO` dưới dạng `FOLIO_ITEM` (`status = UNPAID`). Khi đơn hàng bị hủy (`CANCELLED`), phí này tự động chuyển thành `VOIDED` và giảm trừ công nợ của Folio. | UC18, UC19 |
| **BR-FNB-006** | DTO Boundary Enforcement | Toàn bộ dữ liệu truyền từ Backend xuống Chef Dashboard phải đi qua DTO whitelisted (`ChefDashboardOrderResponse`), chặn đứng hoàn toàn việc serialization đối tượng Entity đầy đủ chứa dữ liệu PII y tế nhạy cảm. | UC17, UC20 |
| **BR-FNB-007** | Package Free Meals Limit | Số lượng phần ăn miễn phí trong gói Retreat bị giới hạn ở mức `totalGuests * 3` mỗi ngày. Hệ thống tính toán động số phần ăn còn lại dựa trên các đơn hàng không phụ thu (`isExtraCharge = false`) trong ngày. | UC16 |

---

## 7. Data Model — Bảng dữ liệu liên quan

### 7.1 MENU_ITEM

| # | Field | Kiểu dữ liệu | PK | FK | NN | Mô tả |
| - | ----- | ------------ | -- | -- | -- | ----- |
| 1 | menu_item_id | INT | X | | X | Mã định danh tự tăng (Primary Key) |
| 2 | item_name | NVARCHAR(20) | | | X | Tên món ăn hiển thị |
| 3 | price | DECIMAL(18,2) | | | | Đơn giá món ăn |
| 4 | ingredient | NVARCHAR(MAX) | | | | Thành phần nguyên liệu món ăn (để check dị ứng) |
| 5 | is_available | BIT | | | | Trạng thái phục vụ (1: Có sẵn, 0: Ngừng bán) |
| 6 | create_at | DATETIME | | | | Thời gian tạo món ăn |
| 7 | update_at | DATETIME | | | | Thời gian cập nhật món ăn gần nhất |

### 7.2 MEAL_ORDER

| # | Field | Kiểu dữ liệu | PK | FK | NN | Mô tả |
| - | ----- | ------------ | -- | -- | -- | ----- |
| 1 | meal_order_id | INT | X | | X | Mã định danh đơn hàng tự tăng |
| 2 | booking_id | INT | | BOOKING(booking_id) | X | Mã lượt đặt phòng của khách hàng |
| 3 | folio_id | INT | | GUEST_FOLIO(folio_id) | X | Mã Guest Folio để ghi nợ khi đặt A-la-carte |
| 4 | guest_id | INT | | USER(user_id) | X | Mã khách hàng nhận món |
| 5 | ordered_at | DATETIME | | | | Thời điểm đặt món |
| 6 | ordered_by | INT | | USER(user_id) | | Người đặt món |
| 7 | place_order | VARCHAR(100) | | | | Địa điểm giao món (Vd: Room number, Restaurant) |
| 8 | note | NVARCHAR(MAX) | | | | Ghi chú thêm từ khách hàng |
| 9 | order_status | VARCHAR(10) | | | | Trạng thái:`PENDING / PREPARING / READY / DELIVERED / CANCELLED` |

### 7.3 MEAL_ORDER_ITEM

| # | Field | Kiểu dữ liệu | PK | FK | NN | Mô tả |
| - | ----- | ------------ | -- | -- | -- | ----- |
| 1 | order_item_id | INT | X | | X | Mã định danh dòng đơn hàng tự tăng |
| 2 | meal_order_id | INT | | MEAL_ORDER(meal_order_id) | X | Liên kết với đơn hàng F&B chính |
| 3 | menu_item_id | INT | | MENU_ITEM(menu_item_id) | X | Món ăn được đặt |
| 4 | quantity | INT | | | | Số lượng đặt món |
| 5 | price | DECIMAL(18,2) | | | | Giá bán thực tế tại thời điểm đặt |

### 7.4 DIETARY_PROFILE

| # | Field | Kiểu dữ liệu | PK | FK | NN | Mô tả |
| - | ----- | ------------ | -- | -- | -- | ----- |
| 1 | dietary_id | INT | X | | X | Mã định danh tự tăng |
| 2 | user_id | INT | | USER(user_id) | X | Liên kết tài khoản khách hàng |
| 3 | food_allergies | NVARCHAR(MAX)| | | | Danh sách chất dị ứng (phân cách bằng dấu phẩy) |
| 4 | diatary_preference | NVARCHAR(MAX)| | | | Sở thích ăn uống (chay, mặn, ít đường,...) |
| 5 | update_at | DATETIME | | | | Thời điểm cập nhật cuối cùng |

---

## 8. RBAC & Data Minimization

### 8.1 Authorization Matrix — Module 4

| Endpoint / Action | GUEST | CHEF | RECEPTIONIST | ADMIN |
| ----------------- | ----- | ---- | ------------ | ----- |
| GET `/fnb/meal-selection` | ✅ (Own) | ❌ | ❌ | ✅ |
| POST `/fnb/meal-selection` | ✅ (Own) | ❌ | ❌ | ✅ |
| GET `/fnb/alacarte-order` | ✅ (Own) | ❌ | ❌ | ✅ |
| POST `/fnb/alacarte-order` | ✅ (Own) | ❌ | ❌ | ✅ |
| GET `/fnb/chef/dashboard` | ❌ | ✅ | ❌ | ✅ |
| POST `/fnb/chef/orders/{id}/status` | ❌ | ✅ | ❌ | ✅ |

### 8.2 Data Minimization Rules — Medical vs Dietary Profiles

> [!IMPORTANT]
> **Quy định bảo mật (Decree 356/2025/NĐ-CP)**:
> Chef và F&B staff KHÔNG ĐƯỢC PHÉP xem thông tin trong bảng `PHYSICAL_HEALTH_PROFILE` (medical_conditions, injuries). Chỉ có quyền xem cột `food_allergies` của bảng `DIETARY_PROFILE` để lọc món ăn.

---

## 9. Non-Functional Requirements

### 9.1 Performance

- **Response Time**: API kiểm tra và lọc thực đơn dựa trên dị ứng phải phản hồi dưới 300ms.
- **Transaction Concurrency**: Đơn hàng A-la-carte ghi nợ Folio phải xử lý đồng thời (Concurrent transactions) chính xác số tiền, không bị lệch dữ liệu (Race Conditions).

### 9.2 Security & Data Integrity

- **PII Encryption**: Dữ liệu dị ứng thực phẩm (`food_allergies`) của khách hàng phải được mã hóa khi lưu trữ trong Database.
- **Audit Logging**: Ghi nhận toàn bộ vết hệ thống khi có bất kỳ truy vấn nào vào dữ liệu dị ứng hoặc cập nhật trạng thái đơn hàng ẩm thực.

### 9.3 Usability

- Giao diện đặt món ăn tự động làm ẩn đi các món ăn có thành phần dị ứng một cách trực quan, tránh hiển thị nút đặt món rồi báo lỗi.

---

## 10. System Messages

| Code | Type | Trigger Context | Message (VI) |
| ---- | ---- | --------------- | ------------ |
| `MSG-FNB-001` | Success | Đặt món ăn miễn phí thành công | "Đặt món ăn cá nhân thành công!" |
| `MSG-FNB-002` | Error | Món ăn chứa chất gây dị ứng | "Không thể gọi món: '{itemName}' có chứa nguyên liệu gây dị ứng trong hồ sơ sức khỏe của bạn." |
| `MSG-FNB-003` | Success | Đặt món A-la-carte thành công | "Đặt món A-La-Carte thành công!" |
| `MSG-FNB-004` | Error | Trạng thái chuyển đổi sai quy tắc | "Trạng thái chuyển đổi không hợp lệ." |
| `MSG-FNB-005` | Error | Vượt giới hạn bữa ăn miễn phí | "Quý khách đã sử dụng hết phần ăn miễn phí của ngày hôm nay. Vui lòng đặt qua Thực đơn A-La-Carte." |

---

## 11. Traceability Matrix

| Requirement ID | Loại | Mô tả ngắn | Thành phần Code liên quan | Business Rule | Use Case |
| -------------- | ---- | ----------- | ------------------------- | ------------- | -------- |
| FRS-FNB-001 | Func | Tự động lọc thực đơn theo dị ứng | `IMealOrderService.getFilteredMenu()` | BR-FNB-001 | UC16 |
| FRS-FNB-002 | Func | Đặt món ăn trong gói Retreat | `GuestFnbController.createMealOrderMvc()` | BR-FNB-007 | UC16 |
| FRS-FNB-003 | Func | Hiển thị Dashboard chuẩn bị món của Chef | `ChefFnbController.getChefDashboard()` | BR-FNB-002 | UC17 |
| FRS-FNB-004 | Func | Cập nhật trạng thái chuẩn bị đơn hàng | `ChefFnbController.updateOrderStatus()` | BR-FNB-003 | UC18 |
| FRS-FNB-005 | Func | Đặt đồ ăn thức uống A-la-carte phụ thu | `GuestAlacarteController.createAlacarteOrder()`| BR-FNB-005 | UC19 |
| FRS-FNB-006 | Security | Che giấu tiền sử bệnh án vật lý với Chef | `MealOrderServiceImpl.getChefDashboardOrders()`| BR-FNB-002 | UC20 |
| FRS-FNB-007 | Security | Rollback giao dịch khi phát hiện lỗi đặt món | `@Transactional` trên `MealOrderServiceImpl` | BR-FNB-004 | UC16, UC19 |

---

*FRS Module 4 v1.0 — Quản lý Ẩm thực F&B & Ăn kiêng — Xoai Aura Retreat (HOS-04)*
*Nhóm SE2023-G6 — SWP391*
