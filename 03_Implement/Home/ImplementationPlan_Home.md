# Kế hoạch Thực hiện: Phân hệ Public Web (Home & Landing Pages)

**Dự án:** Xoai Aura Retreat
**Căn cứ:** Thiết kế từ Stitch cho 5 màn hình chính:
1. Home Page: `9d69eebce0e14a1785e3ee4b86740c51`
2. Retreat & Wellness: `5c0320f2f8a843a1ae823d4ccd57021e`
3. Villas: `f5ea14e0aebd47b7bc2e2b5596e68c6d`
4. Spa: `746bbe47ddf04a96bb083ac1e0452826`
5. Culinary: `49616f005474494a841eb537c4858f8e`

**Mục tiêu:** Tích hợp bộ giao diện toàn diện cho người dùng vãng lai. Xây dựng một Layout dùng chung (`public-layout`) để có thể điều hướng (navigate) qua lại giữa 5 trang này mượt mà, đồng thời tuân thủ Nguyên tắc 4 (tách biệt Module, không dùng CSS/JS nội tuyến).

## User Review Required

> [!IMPORTANT]
> - Do số lượng màn hình lên tới 5 trang và khối lượng HTML/CSS rất lớn, tôi sẽ ưu tiên việc **nhúng HTML tĩnh chuẩn xác 100%** từ bản thiết kế của bạn trước để đảm bảo cấu trúc và hiệu ứng cuộn mượt mà. Việc đổ dữ liệu động (nếu có) sẽ được tính sau. Bạn đồng ý với cách tiếp cận này chứ?
> - Các menu trên thanh Navbar (`Retreats`, `Villas`, `Spa`, `Wellness`, `Culinary`) sẽ được gắn link trực tiếp sang các trang tương ứng. Nút "Book Now" sẽ trỏ về `/login` hoặc `/packages`, bạn muốn ưu tiên trỏ đi đâu?

## Proposed Changes

### 1. Module Public Web (Controller)
#### [NEW] `src/main/java/com/AuraMoon/auramoon/publicweb/controller/HomeController.java`
- Tạo Controller xử lý các request vãng lai.
- Chứa các Mapping:
  - `@GetMapping(value = {"/", "/home"})` -> trả về `public/home`
  - `@GetMapping("/villas")` -> trả về `public/villas`
  - `@GetMapping("/wellness")` -> trả về `public/wellness`
  - `@GetMapping("/spa")` -> trả về `public/spa`
  - `@GetMapping("/culinary")` -> trả về `public/culinary`

---

### 2. Layout & Fragments (UI Template)
> [!NOTE]
> Cả 5 trang đều có chung thanh Navbar và Footer. Tôi sẽ tạo một bộ khung xương (`public-layout.html`) để nhúng nội dung riêng biệt của từng trang vào.

#### [NEW] `src/main/resources/templates/layout/public-layout.html`
- Khai báo cấu hình Tailwind, Fonts (Playfair Display, Inter), Icons.
- Chứa thẻ `<div layout:fragment="content"></div>`.

#### [NEW] `src/main/resources/templates/fragments/public-header.html`
- Chứa thanh điều hướng (Navbar) với các liên kết `/home`, `/villas`, `/wellness`, `/spa`, `/culinary` để dễ dàng di chuyển qua lại.

#### [NEW] `src/main/resources/templates/fragments/public-footer.html`
- Chứa thông tin chân trang dùng chung cho cả 5 màn hình.

---

### 3. Giao diện các trang (Views)
Toàn bộ các file này sẽ chỉ chứa phần lõi `<main>` và kế thừa từ `public-layout.html`:
- **[NEW]** `src/main/resources/templates/public/home.html`
- **[NEW]** `src/main/resources/templates/public/wellness.html`
- **[NEW]** `src/main/resources/templates/public/villas.html`
- **[NEW]** `src/main/resources/templates/public/spa.html`
- **[NEW]** `src/main/resources/templates/public/culinary.html`

---

### 4. Static Resources (CSS/JS)
> [!IMPORTANT]
> Tuân thủ tuyệt đối **Nguyên tắc 4**: Mỗi trang sẽ có CSS/JS độc lập để tránh xung đột mã màu và cấu hình Tailwind.

- **[NEW]** `src/main/resources/static/css/public/home.css` & `js/public/home.js`
- **[NEW]** `src/main/resources/static/css/public/wellness.css` & `js/public/wellness.js`
- **[NEW]** `src/main/resources/static/css/public/villas.css` & `js/public/villas.js`
- **[NEW]** `src/main/resources/static/css/public/spa.css` & `js/public/spa.js`
- **[NEW]** `src/main/resources/static/css/public/culinary.css` & `js/public/culinary.js`
- Chứa Javascript xử lý sự kiện cuộn trang (scroll) đổi màu Navbar dùng chung.

## Verification Plan

### Manual Verification
1. Truy cập `http://localhost:8080/`.
2. Xác minh thanh Navbar có chứa đủ các link.
3. Click lần lượt vào `Retreats/Wellness`, `Villas`, `Spa`, `Culinary` trên menu để đảm bảo hệ thống tải đúng trang (200 OK) mà không bị đứt đoạn.
4. Kiểm tra cấu trúc thư mục đảm bảo mọi CSS/JS inline của 5 trang đều đã được bóc tách sạch sẽ.
