# Đặc tả Yêu cầu Chức năng - Module 4: F&B (Food & Beverage)
**Dự án:** Xoai Aura Retreat Management System (HOS-03)  
**Tài liệu gốc:** SRS_Document_SWP391_G6.md — Phần `3. Functional Requirements`  
**Người tổng hợp:** AI Assistant  
**Cập nhật lần cuối:** 2026-06-12  

---

## Mục lục

1. [Tổng quan Module 4](#1-tổng-quan-module-4)
2. [Danh sách Use Case (UC)](#2-danh-sách-use-case-uc)
3. [Màn hình 3.1.10 – Personalized Menu & A-la-carte Screen (Guest)](#3-màn-hình-3110--personalized-menu--a-la-carte-screen-guest)
4. [Màn hình 3.1.11 – Chef Dashboard / Kitchen Display System (Chef & F&B Staff)](#4-màn-hình-3111--chef-dashboard--kitchen-display-system-chef--fb-staff)
5. [Business Rules áp dụng cho Module 4](#5-business-rules-áp-dụng-cho-module-4)
6. [System Messages liên quan đến Module 4](#6-system-messages-liên-quan-đến-module-4)
7. [Thực thể CSDL liên quan](#7-thực-thể-csdl-liên-quan)
8. [Phân quyền màn hình (Screen Authorization)](#8-phân-quyền-màn-hình-screen-authorization)

---

## 1. Tổng quan Module 4

Module 4 quản lý toàn bộ luồng Ẩm thực (Food & Beverage) trong hệ thống resort, bao gồm hai luồng nghiệp vụ song song:

| Luồng | Vai trò | Mô tả |
| :--- | :--- | :--- |
| **Guest → Kitchen** | Guest | Khách lựa chọn bữa ăn từ thực đơn được lọc tự động theo hồ sơ dị ứng; có thể gọi thêm món ngoài gói (A-la-carte) |
| **Kitchen → Guest** | Chef / F&B Staff | Đầu bếp nhận đơn hàng tổng hợp qua bảng Kanban, chuẩn bị món và cập nhật trạng thái đơn theo workflow bắt buộc |

### Nguyên tắc cốt lõi của Module 4

- **Data Minimization (BR-07):** Chef/F&B Staff **CHỈ** được phép xem thông tin dị ứng thực phẩm (`Food Allergies`). Hệ thống **phải ẩn hoàn toàn** hồ sơ bệnh lý vật lý (`Physical Medical Records`) như huyết áp cao, đau lưng, v.v.
- **Automatic Filtering (BR-06):** Hệ thống **tự động** loại trừ các món ăn có thành phần trùng với danh sách dị ứng của khách, không cần thao tác thủ công.
- **Folio Integration (BR-11):** Mọi đơn A-la-carte phải được ghi nợ tự động vào `Guest Folio` thông qua `Room_Booking_ID` để tích hợp vào hóa đơn tổng kết khi check-out.
- **Status Workflow (BR-16):** Trạng thái đơn hàng chỉ được phép tiến theo một chiều: `Pending → Preparing → Ready for Delivery`. **Không cho phép đảo ngược trạng thái.**

---

## 2. Danh sách Use Case (UC)

Module 4 bao gồm 5 Use Case từ **UC16 đến UC20**:

| ID | Use Case | Vai trò chính | Mô tả ngắn gọn |
| :---: | :--- | :---: | :--- |
| **UC16** | Pre-select Daily Meals | Guest | Khách chọn bữa ăn hàng ngày từ thực đơn đã được hệ thống lọc tự động theo dị ứng và khẩu phần ăn |
| **UC17** | View Daily Meal Preparation Dashboard | Chef / F&B Staff | Đầu bếp xem bảng tổng hợp đơn hàng và cảnh báo dị ứng thực phẩm của ngày |
| **UC18** | Update Meal Order Status | Chef / F&B Staff | Đầu bếp cập nhật trạng thái đơn hàng từ Preparing → Ready for Delivery |
| **UC19** | Order A-la-carte Food and Beverage | Guest | Khách gọi thêm món ngoài gói, phí tự động ghi vào Folio |
| **UC20** | Enforce Data Minimization for F&B Staff | System | Hệ thống đảm bảo RBAC — Chef không thấy hồ sơ bệnh lý, chỉ thấy dị ứng ăn uống |

---

## 3. Màn hình 3.1.10 – Personalized Menu & A-la-carte Screen (Guest)

### 3.1 Mô tả tổng quan màn hình

> **Tên màn hình:** Thực đơn cá nhân & Gọi món ngoài  
> **Vai trò truy cập:** Guest (Khách hàng)  
> **Use Cases ánh xạ:** UC16, UC19  

Giao diện này cung cấp trải nghiệm ẩm thực thông minh. Hệ thống tự động đối chiếu thành phần món ăn với hồ sơ sức khỏe của khách để đưa ra cảnh báo và ngăn chặn việc đặt món chứa chất gây dị ứng. Ngoài ra, khách có thể gọi thêm các món cao cấp ngoài gói tiêu chuẩn thông qua tab A-la-carte.

### 3.2 Layout & Cấu trúc giao diện

```
┌──────────────────────────────────────────────────────────────┐
│  [Tab] Thực đơn của bạn  |  [Tab] Gọi món ngoài             │
├──────────────────────────────────────────┬───────────────────┤
│                                          │                   │
│  ┌────────────────────────────────────┐  │  🛒 Giỏ hàng     │
│  │  🍽️ Standard Food Card            │  │  ─────────────── │
│  │  - Ảnh, Tên món, Mô tả thành phần │  │  Danh sách món   │
│  │  - Chỉ số dinh dưỡng (Calo, Macro) │  │  đã chọn        │
│  │  - Nút [Chọn món]                  │  │                  │
│  └────────────────────────────────────┘  │  Tạm tính:  XXXk │
│                                          │  Phí DV (5%): XXk │
│  ┌────────────────────────────────────┐  │  Tổng cộng: XXXk │
│  │  ⚠️ Allergy Warning Card          │  │  ─────────────── │
│  │  - Overlay cảnh báo đỏ            │  │  [Xác nhận đặt]  │
│  │  - Nút [Chọn món] bị DISABLED     │  │                  │
│  └────────────────────────────────────┘  └───────────────────┘
└──────────────────────────────────────────────────────────────┘
```

### 3.3 Đặc tả các thành phần giao diện (Field Specification)

#### 🔹 Nhóm trường: Menu Navigation

| # | Tên thành phần | Kiểu dữ liệu | Mô tả & Logic |
| :---: | :--- | :--- | :--- |
| (1) | **Category Tabs** (Chuyển đổi tab) | Action Button | Chuyển đổi giữa hai tập dữ liệu:<br>• **"Thực đơn của bạn"**: Hiển thị các món trong gói Retreat Package (Giá = 0 VND).<br>• **"Gọi món ngoài (A-la-carte)"**: Hiển thị thực đơn cao cấp với giá tiền thực tế. |

#### 🔹 Nhóm trường: Food Items (Danh sách món ăn)

| # | Tên thành phần | Kiểu dữ liệu | Mô tả & Logic |
| :---: | :--- | :--- | :--- |
| (2) | **Standard Food Card** (Thẻ món ăn bình thường) | Read-only display | Hiển thị: Hình ảnh, Tên món, Mô tả thành phần, Chỉ số dinh dưỡng (Calo, Protein, Carbs, Fats, Fiber) và nút **"Chọn món"** ở trạng thái **active**. |
| (3) | **Allergy Warning Card** (Thẻ cảnh báo dị ứng) | Read-only display | **⚠️ Ràng buộc logic nghiêm ngặt (BR-06):**<br>Backend phải thực hiện kiểm tra giao điểm (`intersection check`) giữa mảng `Ingredients[]` của món ăn và mảng `Food_Allergies[]` của khách.<br>• **Nếu có giao điểm:** Frontend phải render overlay cảnh báo (ví dụ: "⚠️ Cảnh báo dị ứng") và **vô hiệu hóa hoàn toàn** nút "Chọn món" để ngăn khách thêm món vào giỏ hàng.<br>• **Nếu không có giao điểm:** Hiển thị card bình thường (2). |

#### 🔹 Nhóm trường: Cart & Checkout (Giỏ hàng & Xác nhận đơn)

| # | Tên thành phần | Kiểu dữ liệu | Mô tả & Logic |
| :---: | :--- | :--- | :--- |
| (4) | **Cart Summary** (Tóm tắt giỏ hàng) | Dynamic calculation | Tính toán tự động, thời gian thực:<br>• Đơn từ tab **"Thực đơn của bạn"**: Tổng tiền = **0 VND**.<br>• Đơn từ tab **"Gọi món ngoài"**: Tổng = Giá gốc + Phí dịch vụ (5%). |
| (5) | **"Xác nhận đặt bàn"** (Confirm Order Button) | Action | Submit toàn bộ payload đơn hàng xuống database.<br>**Ràng buộc Folio (BR-11):** Với bất kỳ món nào từ tab A-la-carte, hệ thống phải **tự động ghi nợ** khoản phí đã tính vào tài khoản `Guest Folio` của khách, liên kết thông qua `Room_Booking_ID`. |

### 3.4 Business Logic tóm tắt (UC16 & UC19)

```
[Guest mở trang thực đơn]
         │
         ▼
[Backend: Query toàn bộ MenuItem]
         │
         ▼
[Backend: Với mỗi món → giao(Ingredients[], Food_Allergies[])]
         │
    ┌────┴────────┐
    │ Có giao điểm│          │ Không có giao điểm │
    ▼             ▼
[isAvailable=false]    [isAvailable=true]
[Render: Allergy       [Render: Standard Card
 Warning Card,          + Nút "Chọn món" active]
 Nút disabled]
         │
         ▼ (Khách chọn món)
[Frontend: Thêm vào giỏ hàng, tính tiền real-time]
         │
         ▼ (Khách bấm Xác nhận)
[Backend: INSERT Meal_Order + Meal_Order_Item]
         │
         ▼ (Nếu là A-la-carte)
[Backend: POST charge → Guest_Folio (linked by Room_Booking_ID)]
```

---

## 4. Màn hình 3.1.11 – Chef Dashboard / Kitchen Display System (Chef & F&B Staff)

### 4.1 Mô tả tổng quan màn hình

> **Tên màn hình:** Bảng điều phối bếp (Kitchen Display System - KDS)  
> **Vai trò truy cập:** Chef / F&B Staff  
> **Use Cases ánh xạ:** UC17, UC18, UC20  

Dashboard vận hành thời gian thực được sử dụng bởi đầu bếp và nhân viên F&B. Hệ thống số hóa quy trình phân luồng phiếu đặt món, cho phép nhà bếp theo dõi workflow chuẩn bị và tuân thủ nghiêm ngặt các ràng buộc an toàn thực phẩm mà không vi phạm quyền riêng tư y tế của khách hàng.

### 4.2 Layout & Cấu trúc giao diện

```
┌──────────────────────────────────────────────────────────────┐
│  🍳 Kitchen Display System  |  Ngày: Thứ Năm, 12/06/2026    │
├──────────────────┬────────────────────┬──────────────────────┤
│   📋 PENDING     │  🔥 IN PROGRESS    │   ✅ COMPLETED       │
│   (Chờ xử lý)   │  (Đang chuẩn bị)   │   (Hoàn thành)       │
├──────────────────┼────────────────────┼──────────────────────┤
│ ┌──────────────┐ │ ┌────────────────┐ │ ┌──────────────────┐ │
│ │⚠️ DỊ ỨNG: Tôm│ │ │ Phòng: Villa 3 │ │ │   Phòng: Villa 1 │ │
│ │ Phòng: V.02  │ │ │ ⏱️ Trễ 5p      │ │ │   [Hoàn thành]   │ │
│ │ ⏱️ 10p       │ │ │ • Cá hồi x1    │ │ └──────────────────┘ │
│ │ • Tôm nướng  │ │ │ • Salad x2     │ │                      │
│ │   (cảnh báo) │ │ │ [Hoàn thành ✓] │ │                      │
│ │ [Bắt đầu →]  │ │ └────────────────┘ │                      │
│ └──────────────┘ │                    │                      │
└──────────────────┴────────────────────┴──────────────────────┘
```

### 4.3 Đặc tả các thành phần giao diện (Field Specification)

#### 🔹 Nhóm trường: Kanban Columns (Cột Kanban)

| # | Tên thành phần | Kiểu dữ liệu | Mô tả & Logic |
| :---: | :--- | :--- | :--- |
| (1) | **Order Status Columns** (Cột trạng thái đơn) | Layout containers | Ba cột phân loại phiếu theo trạng thái hiện tại:<br>• **Pending** (Chờ xử lý)<br>• **In_Progress** (Đang chuẩn bị)<br>• **Completed** (Hoàn thành) |

#### 🔹 Nhóm trường: Order Ticket Details (Chi tiết phiếu đơn hàng)

| # | Tên thành phần | Kiểu dữ liệu | Mô tả & Logic |
| :---: | :--- | :--- | :--- |
| (2) | **Allergy Alert Label** (Nhãn cảnh báo dị ứng) | String / Alert Banner | **⚠️ Ràng buộc RBAC & Data Minimization nghiêm ngặt (BR-07, UC20):**<br>API Backend phục vụ UI này **BẮT BUỘC phải giới hạn** payload chỉ gồm `"Dietary Allergies"` (ví dụ: đậu phộng, hải sản, sữa).<br>**Hồ sơ y tế vật lý** (ví dụ: đau lưng, tăng huyết áp) **PHẢI bị ẩn hoàn toàn** và loại khỏi response trả về view này. |
| (3) | **Order Meta Info** (Thông tin tổng quan đơn) | Read-only text | Hiển thị: Số phòng (Room Number), Tên khách (Guest Name), và đồng hồ đếm thời gian chờ động (ví dụ: "⏱️ Trễ 5p") theo dõi thời gian aging của phiếu. |
| (4) | **Itemized List** (Danh sách món chi tiết) | Array of Objects | Hiển thị: Tên món, Số lượng, và các ghi chú khẩu phần ăn riêng (dietary modifications) liên quan đến từng món. |
| (5) | **State Mutation Buttons** (Nút thay đổi trạng thái) | Action Buttons | Hai nút hành động:<br>• **"Bắt đầu"** (Start): Chuyển ticket từ `Pending → In_Progress`.<br>• **"Hoàn thành"** (Complete): Chuyển ticket từ `In_Progress → Completed`.<br>**Logic:** Mỗi lần nhấn sẽ kích hoạt truy vấn UPDATE trạng thái trong database và tự động dịch chuyển ticket sang cột Kanban tiếp theo trên UI. |

### 4.4 Business Logic tóm tắt (UC17, UC18, UC20)

```
[Chef mở Chef Dashboard]
         │
         ▼
[Backend: Query Meal_Order JOIN Meal_Order_Item JOIN Menu_Item]
         │
         ▼
[Backend: Với mỗi đơn → Query Dietary_Profile của khách]
         │
         ▼
[Backend: RBAC Filter (BR-07, UC20)]
    ┌────┴──────────────────────────────────────────────────┐
    │   Chỉ trả về: food_allergies (peanut, seafood...)    │
    │   Ẩn hoàn toàn: physical_health (back pain, BP...)   │
    └───────────────────────────────────────────────────────┘
         │
         ▼
[Frontend: Render Kanban Board]
    • Pending Column   → Các đơn mới
    • In_Progress Col  → Đơn đang nấu
    • Completed Column → Đơn xong
         │
         ▼ (Chef bấm "Bắt đầu" / "Hoàn thành")
[Backend: UPDATE Meal_Order SET status = 'Preparing' / 'Ready']
         │
[Ràng buộc BR-16: CHẶN đảo ngược trạng thái]
    Chỉ cho phép: Pending → Preparing → Ready for Delivery
```

---

## 5. Business Rules áp dụng cho Module 4

### BR-06 — Automatic F&B Menu Filtering (Tự động lọc thực đơn)

| Thuộc tính | Giá trị |
| :--- | :--- |
| **Áp dụng cho UC** | UC16, UC17, UC20 |
| **Mô tả đầy đủ** | Hệ thống phải **tự động loại trừ** các món ăn chứa chất gây dị ứng hoặc xung đột với khẩu phần ăn đã khai báo của khách. Đầu bếp và nhân viên F&B chỉ được phép truy cập thông tin dị ứng và sở thích ăn uống cần thiết cho công việc của họ. |
| **Logic kỹ thuật** | `intersection(dish.Ingredients[], guest.Food_Allergies[]) ≠ ∅` → Đánh dấu món là không khả dụng (`isAvailable = false`). |
| **Màn hình** | 3.1.10 (Guest), 3.1.11 (Chef) |

### BR-07 — Role-Based Access Control & Data Minimization (RBAC)

| Thuộc tính | Giá trị |
| :--- | :--- |
| **Áp dụng cho UC** | UC03, UC13, UC17, UC20 |
| **Mô tả đầy đủ** | Đầu bếp **chỉ** được phép truy cập thông tin dị ứng thực phẩm và sở thích ăn uống. Thông tin bệnh lý vật lý, hồ sơ y tế phải bị ẩn hoàn toàn. Kiểm soát truy cập phải được thực thi ở **tầng Backend** (không chỉ là Frontend ẩn trường). |
| **Ràng buộc kỹ thuật** | API endpoint phục vụ Chef Dashboard phải dùng truy vấn SELECT chỉ lấy cột `dietary_allergies`, KHÔNG trả về cột `medical_conditions`, `medications`, `injuries`. |
| **Màn hình** | 3.1.11 (Chef Dashboard) |

### BR-11 — Guest Folio & Consolidated Billing (Tích hợp hóa đơn)

| Thuộc tính | Giá trị |
| :--- | :--- |
| **Áp dụng cho UC** | UC15, UC19, UC21 |
| **Mô tả đầy đủ** | Mọi phí Spa và F&B phải được ghi vào `Guest Folio` sử dụng `Booking_ID` tương ứng và đưa vào hóa đơn tổng kết khi Check-out. |
| **Logic kỹ thuật** | Khi khách xác nhận đơn A-la-carte: `INSERT INTO Guest_Folio_Item (folio_id, description, amount, room_booking_id)`. Trạng thái charge là `"Pending"` cho đến khi UC21 (Check-out) xử lý. |
| **Màn hình** | 3.1.10 (Guest — nút "Xác nhận đặt bàn") |

### BR-16 — Meal Order Status Workflow (Quy trình trạng thái đơn hàng)

| Thuộc tính | Giá trị |
| :--- | :--- |
| **Áp dụng cho UC** | UC18 |
| **Mô tả đầy đủ** | Trạng thái đơn hàng chỉ được phép tiến theo thứ tự một chiều đã định. Đảo ngược trạng thái bị nghiêm cấm. Chỉ đầu bếp hoặc nhân viên F&B mới được phép cập nhật trạng thái đơn hàng. |
| **Workflow hợp lệ** | `Pending` → `Preparing` → `Ready for Delivery` |
| **Ràng buộc kỹ thuật** | Backend phải validate trạng thái hiện tại trước khi cho phép UPDATE. Nếu trạng thái hiện tại là `Completed` hoặc `Ready`, phải từ chối yêu cầu cập nhật. |
| **Màn hình** | 3.1.11 (Chef Dashboard — nút State Mutation) |

---

## 6. System Messages liên quan đến Module 4

| Mã | Loại | Ngữ cảnh kích hoạt | Thông điệp |
| :---: | :---: | :--- | :--- |
| **MSG-11** | ✅ Success | Đặt món thành công | `"Meal order has been recorded successfully."` |
| **MSG-12** | ⚠️ Warning | Món ăn chứa chất gây dị ứng | `"Warning: This meal contains ingredients listed in the guest's allergy profile."` |
| **MSG-18** | ❌ Error | Truy cập trái phép vào dữ liệu | `"You do not have permission to access this function."` |
| **MSG-19** | ❌ Error | Lỗi hệ thống không xác định | `"An unexpected system error has occurred. Please try again later."` |

---

## 7. Thực thể CSDL liên quan

### Sơ đồ quan hệ (Module 4)

```
┌──────────┐     ┌───────────────┐     ┌──────────────────┐
│  USER    │────▶│  Dietary_     │────▶│    Menu_Item     │
│  (Guest) │     │  Profile      │     │  (item_name,     │
└──────────┘     │ (food_allergies│    │   ingredients,   │
                 │  diet_type)   │     │   calories...)   │
                 └───────────────┘     └──────────────────┘
                                              │
┌──────────┐     ┌───────────────┐     ┌──────▼───────────┐
│  BOOKING │────▶│  Meal_Order   │────▶│ Meal_Order_Item  │
└──────────┘     │  (status,     │     │ (menu_item_id,   │
     │           │   meal_date,  │     │  quantity)       │
     │           │   meal_type)  │     └──────────────────┘
     │           └───────────────┘
     │
     ▼
┌──────────────┐     ┌──────────────────┐
│  Guest_Folio │────▶│  Folio_Item      │
│              │     │ (description,    │
└──────────────┘     │  amount, type)   │
                     └──────────────────┘
```

### Mô tả thực thể

| # | Thực thể | Thuộc tính quan trọng | Vai trò trong Module 4 |
| :---: | :--- | :--- | :--- |
| 1 | **Dietary_Profile** | `food_allergies` (array), `diet_type` (Enum: Vegan/Vegetarian/Keto/Halal), `other_allergies` (text) | Nguồn dữ liệu để lọc thực đơn (BR-06) và hiển thị cảnh báo dị ứng cho Chef (BR-07) |
| 2 | **Menu_Item** | `item_name`, `ingredients` (array/text), `price`, `calories`, `protein`, `carbs`, `fats`, `fiber` | Catalog thực đơn được hệ thống dùng để kiểm tra giao điểm với `food_allergies` |
| 3 | **Meal_Order** | `booking_id` (FK), `meal_date`, `meal_type` (Enum: Standard/A-La-Carte), `status` (Enum: Pending/Preparing/Ready) | Đơn hàng chính liên kết với booking, theo dõi trạng thái theo BR-16 |
| 4 | **Meal_Order_Item** | `meal_order_id` (FK), `menu_item_id` (FK), `quantity` | Chi tiết từng món trong đơn hàng |
| 5 | **Guest_Folio** | `booking_id` (FK) | Tài khoản hóa đơn tổng hợp của khách |
| 6 | **Folio_Item** | `folio_id` (FK), `description`, `amount`, `item_type` (FNB/SPA/ROOM) | Dòng ghi nợ phí A-la-carte F&B vào hóa đơn tổng (BR-11) |

---

## 8. Phân quyền màn hình (Screen Authorization)

| Màn hình | Guest | Receptionist | Therapist | Chef / F&B Staff | Admin |
| :--- | :---: | :---: | :---: | :---: | :---: |
| A-la-carte Menu (Thực đơn gọi thêm) | ✅ | ❌ | ❌ | ❌ | ❌ |
| Dietary Menu (Thực đơn cá nhân hóa) | ✅ | ❌ | ❌ | ❌ | ❌ |
| F&B / Chef Dashboard | ❌ | ❌ | ❌ | ✅ | ❌ |
| Daily Meal Prep Board | ❌ | ❌ | ❌ | ✅ | ❌ |
| A-la-carte Orders Board | ❌ | ❌ | ❌ | ✅ | ❌ |
| Update Prep Status (Action) | ❌ | ❌ | ❌ | ✅ | ❌ |

> **Lưu ý:** Kiểm soát phân quyền phải được thực thi ở tầng **Backend** (Spring Security / Role-based interceptor), không chỉ ẩn/hiện ở tầng giao diện Frontend.

---

## Phụ lục: Liên kết tài liệu gốc

| Tài liệu | Đường dẫn | Nội dung liên quan |
| :--- | :--- | :--- |
| SRS chính | [SRS_Document_SWP391_G6.md](./SRS_Document_SWP391_G6.md) | Phần `3. Functional Requirements` — mục 3.1.10, 3.1.11 |
| SRS tóm tắt | [SRS_Document.md](./SRS_Document.md) | Tổng quan FEAT-10, FEAT-11 |
| Báo cáo thực thi | [REPORT.md](../Implement/REPORT.md) | Chi tiết cài đặt UC16 & UC19 đã hoàn thành |
