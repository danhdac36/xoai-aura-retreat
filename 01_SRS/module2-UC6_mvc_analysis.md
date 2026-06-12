# MODULE 2 — UC06: Duyệt & Lọc Gói Trị Liệu
## Giải thích từ Giao diện → Backend (dễ hiểu)

---

## 🖥️ KỊCH BẢN 1: Khách mở trang danh sách gói lần đầu

### Bước 1 — Màn hình khách đang thấy gì?

Khách truy cập `http://localhost:8080/packages`

Màn hình hiển thị gồm 2 phần:
- **Sidebar bên trái**: Bộ lọc (radio chọn loại, slider ngày, slider giá)
- **Nội dung bên phải**: 18 card gói trị liệu dạng lưới 3 cột

---

### Bước 2 — Khi gõ URL đó, backend làm gì?

Trình duyệt gửi: `GET /packages` → vào **Controller**

📁 `RetreatPackageController.java` — hàm `listPackages()`

```java
// Nhận request GET /packages
// type = null (không có ?type=... trong URL)
public String listPackages(type=null, minDays=null, ..., Model model) {

    // Gọi Service lấy tất cả gói
    List<RetreatPackageDTO> packages = retreatPackageService.searchPackages(
        null, null, null, null, null  // không có bộ lọc nào
    );

    // Nhồi dữ liệu vào "túi" gửi cho View
    model.addAttribute("packages", packages);      // 18 gói trị liệu
    model.addAttribute("types", [...]);            // ["Detox","Yoga","Stress Relief",...]
    model.addAttribute("selectedType", "All");     // đang chọn "Tất cả"
    model.addAttribute("minDays", 2);
    model.addAttribute("maxDays", 7);

    return "booking/packages";  // → render file packages.html
}
```

---

### Bước 3 — Controller gọi Service, Service làm gì?

📁 `RetreatPackageServiceImpl.java` — hàm `searchPackages()`

```java
public List<RetreatPackageDTO> searchPackages(null, null, null, null, null) {

    // BƯỚC A: Xuống Repository lấy dữ liệu thô từ DB
    List<RetreatPackage> entities = repository.searchPackages(null,null,null,null,null);
    //  → SQL chạy: SELECT * FROM RETREAT_PACKAGE WHERE is_active=1 AND is_delete=0
    //  → Kết quả: 18 dòng Entity (dữ liệu thô từ bảng DB)

    // BƯỚC B: Chuyển từng Entity → DTO (loại bỏ dữ liệu nội bộ)
    return entities.stream()
        .map(this::convertToDTO)  // gọi hàm chuyển đổi cho từng phần tử
        .toList();
    // → Trả về 18 DTO sạch cho Controller
}
```

**Hàm `convertToDTO()` — lý do phải dùng:**
```
Entity (dữ liệu thô từ DB)     DTO (dữ liệu sạch cho View)
─────────────────────────      ──────────────────────────
id = 1                    ✅→  id = 1
packageName = "Mindful."  ✅→  packageName = "Mindful."
typePackage = "Yoga"      ✅→  typePackage = "Yoga"
durationDays = 5          ✅→  durationDays = 5
services = "Yoga,Spa,..."  ✅→  services = "Yoga,Spa,..."
description = "..."        ✅→  description = "..."
price = 25000000          ✅→  price = 25000000
isActive = true           ❌   (ẩn - khách không cần biết)
isDelete = false          ❌   (ẩn - dữ liệu nội bộ)
createdAt = 2026-01-01   ❌   (ẩn)
updatedAt = 2026-06-12   ❌   (ẩn)
```
> **Tại sao phải ẩn?** Bảo mật — không để lộ cấu trúc database ra ngoài.

---

### Bước 4 — Repository chạy SQL gì xuống DB?

📁 `RetreatPackageRepository.java` — câu query `searchPackages()`

```sql
-- Khi không có bộ lọc nào (tất cả = null)
SELECT * FROM RETREAT_PACKAGE
WHERE is_active = 1          -- chỉ gói đang hoạt động
  AND is_delete = 0          -- chưa bị xóa (soft delete)
  -- các điều kiện IS NULL → bỏ qua hết
```
**Kết quả:** DB trả về 18 dòng → Service nhận → convert DTO → Controller nhận → View hiển thị

---

### Bước 5 — View (packages.html) hiển thị ra màn hình thế nào?

```html
<!-- Vòng lặp: tạo 1 card cho mỗi gói trong danh sách -->
<div th:each="pkg : ${packages}">

    <!-- pkg.typePackage = "Yoga" → hiện tag "Yoga" -->
    <span th:text="${pkg.typePackage}">Yoga</span>

    <!-- pkg.packageName = "Mindfulness Retreat 5 Days" -->
    <h2 th:text="${pkg.packageName}">Tên gói</h2>

    <!-- pkg.durationDays = 5 → hiện "5 Ngày" -->
    <span th:text="${pkg.durationDays} + ' Ngày'">5 Ngày</span>

    <!-- pkg.price = 25000000 → format → "25.000.000 đ" -->
    <span th:text="${#numbers.formatDecimal(pkg.price,...)} + ' đ'">25.000.000 đ</span>

    <!-- pkg.services = "Yoga,Spa,Meditation" → tách ra → 3 pills -->
    <span th:each="service : ${#strings.arraySplit(pkg.services, ',')}"
          th:text="${#strings.trim(service)}">Yoga</span>

    <!-- Nút Đặt ngay → link sang UC07 với packageId -->
    <a th:href="@{/booking/create(packageId=${pkg.id})}">Tiến hành đặt gói ngay</a>
</div>
```

---

## 🔍 KỊCH BẢN 2: Khách bấm lọc "Yoga"

### Màn hình — Khách tích vào radio "Yoga"

Khi bấm radio → JavaScript tự động submit form → Browser gửi:
```
GET /packages?type=Yoga&minDays=2&maxDays=7&minPrice=10000000&maxPrice=50000000
```

### Backend xử lý:

**Controller** nhận `type = "Yoga"` → gọi:
```java
searchPackages("Yoga", 2, 7, 10000000.0, 50000000.0)
```

**Repository** chạy SQL:
```sql
SELECT * FROM RETREAT_PACKAGE
WHERE is_active = 1
  AND is_delete = 0
  AND type_package = 'Yoga'    ← thêm điều kiện lọc
  AND duration_days >= 2
  AND duration_days <= 7
  AND price >= 10000000
  AND price <= 50000000
```

**Kết quả:** Chỉ trả về các gói Yoga → View chỉ hiển thị card Yoga.

**Sidebar tự highlight "Yoga":**
```html
<!-- th:classappend thêm class "active" nếu đang chọn loại đó -->
<input th:checked="${type == selectedType}" />
<!-- selectedType = "Yoga" → radio Yoga được tích sẵn -->
```

---

## 📄 KỊCH BẢN 3: Khách bấm xem chi tiết gói (ví dụ gói ID=7)

### Màn hình — Khách bấm vào card → hiện trang chi tiết

Trình duyệt gửi: `GET /packages/7`

**Controller** — hàm `packageDetail()`:
```java
@GetMapping("/{id}")                 // lắng nghe /packages/7
public String packageDetail(id=7, Model model) {

    RetreatPackageDTO pkg = retreatPackageService.getPackageById(7);
    // Nếu gói ID=7 không tồn tại, bị ẩn, hoặc bị xóa → throw Exception → trang lỗi

    model.addAttribute("pkg", pkg);
    return "booking/package-detail";  // → render package-detail.html
}
```

**Service** — `getPackageById(7)`:
```java
RetreatPackage entity = repository
    .findByIdAndIsActiveTrueAndIsDeleteFalse(7)
    .orElseThrow(() -> new RuntimeException("Retreat package not found"));
//  ↑ tìm gói có id=7, đang active=true, chưa bị xóa

return convertToDTO(entity);  // chuyển sang DTO rồi trả về
```

**SQL chạy:**
```sql
SELECT * FROM RETREAT_PACKAGE
WHERE package_id = 7
  AND is_active = 1
  AND is_delete = 0
```

---

### Màn hình chi tiết (package-detail.html) có gì?

**Phần trên — thông tin gói:**
- Ảnh tự chọn theo loại: Yoga → `yoga.jpg`, Detox → `detox.jpg`, Spa → `spa-retreat.jpg`
- Tag loại (`pkg.typePackage`), tên gói (`pkg.packageName`)
- 3 ô stats: Thời lượng (`pkg.durationDays`), Giá (`pkg.price`), Loại trị liệu
- Pills dịch vụ: tách `pkg.services` bằng dấu phẩy → hiện từng pill

**Phần dưới — 3 tab (JavaScript show/hide, KHÔNG gọi backend):**

```
[ Lịch trình mẫu ★ ] [ Dịch vụ Spa ]  [ Thực đơn dinh dưỡng ]
          ↓ (đang hiện)
  Ngày 1: 14:00 Check-in & Trà Chào Mừng
          15:30 Tư Vấn Sức Khỏe & Thiết Lập Lộ Trình
          18:30 Bữa Tối Dinh Dưỡng Hữu Cơ
  Ngày 2: 06:30 Yoga Đón Bình Minh  ← thay đổi theo typePackage!
          08:00 Bữa Sáng Dinh Dưỡng
          15:30 Spa & Massage Thảo Dược
  ...
  Ngày N: 06:30 Thiền Sớm & Tắm Rừng
          12:00 Check-out
```

**Lịch trình tự generate theo `pkg.durationDays`:**
```html
<!-- Loop từ Ngày 1 đến Ngày N (N = durationDays từ DB) -->
<div th:each="dayNum : ${#numbers.sequence(1, pkg.durationDays)}">

    <!-- Ngày đầu tiên: check-in -->
    <div th:if="${dayNum == 1}">14:00 Check-in... 18:30 Bữa tối...</div>

    <!-- Ngày giữa: nội dung khác nhau theo loại gói -->
    <div th:if="${dayNum > 1 && dayNum < pkg.durationDays}">
        <!-- Yoga? → hiện "Yoga Đón Bình Minh" -->
        <!-- Stress Relief? → hiện "Thiền Chánh Niệm" -->
        <!-- Detox? → hiện "Vận Động Nhẹ & Cardio" -->
    </div>

    <!-- Ngày cuối: check-out -->
    <div th:if="${dayNum == pkg.durationDays}">06:30 Thiền... 12:00 Check-out</div>
</div>
```

**Nút "Tiến hành đặt gói ngay"** → liên kết sang UC07:
```html
<a th:href="@{/booking/create(packageId=${pkg.id})}">
    Tiến hành đặt gói ngay
</a>
<!-- → GET /booking/create?packageId=7 → UC07 xử lý tiếp -->
```

---

## 📊 TÓM TẮT TOÀN BỘ

| Hành động của khách | URL | Hàm Controller | Hàm Service | SQL |
|---|---|---|---|---|
| Xem tất cả gói | `GET /packages` | `listPackages()` | `searchPackages(null,...)` | `SELECT * WHERE active=1` |
| Lọc theo loại | `GET /packages?type=Yoga` | `listPackages()` | `searchPackages("Yoga",...)` | `+ AND type_package='Yoga'` |
| Lọc theo ngày+giá | `GET /packages?minDays=3&maxPrice=30tr` | `listPackages()` | `searchPackages(...,3,...,30tr)` | `+ AND days>=3 AND price<=30tr` |
| Xem chi tiết | `GET /packages/7` | `packageDetail()` | `getPackageById(7)` | `WHERE id=7 AND active=1` |
| Bấm "Đặt ngay" | `GET /booking/create?packageId=7` | (UC07 tiếp nhận) | (UC07) | (UC07) |
