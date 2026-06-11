# TEST DESIGN DOCUMENT (TDD)
## Tài liệu Thiết kế Kiểm thử

---

| Thông tin | Chi tiết |
|-----------|---------|
| **Tên dự án** | AuraMoon – Hệ thống quản lý Retreat Resort |
| **Phiên bản** | 1.0 |
| **Ngày tạo** | 2025 |
| **Module** | Module 2 – Duyệt & Lọc Gói Trị Liệu (UC06) |
| **Người thực hiện** | Nhóm 6 – SWP391 |
| **Trạng thái** | Draft |

---

## 1. PHẠM VI KIỂM THỬ

### 1.1 Mục tiêu kiểm thử

Tài liệu này mô tả thiết kế kiểm thử cho tính năng **Hiển thị và Lọc danh sách gói Retreat** (UC06), bao gồm:

| # | Tính năng |
|---|-----------|
| F1 | Hiển thị danh sách gói Retreat |
| F2 | Lọc theo mục tiêu sức khỏe (typePackage) |
| F3 | Lọc theo số ngày (durationDays) |
| F4 | Lọc theo mức giá (priceRange) |
| F5 | Xem chi tiết từng gói |
| F6 | Hiển thị gợi ý khi không có kết quả |

### 1.2 Phạm vi không bao gồm

- Chức năng đặt gói (UC07)
- Chức năng thanh toán
- Chức năng quản lý gói (Admin)

---

## 2. CHIẾN LƯỢC KIỂM THỬ

### 2.1 Cấp độ kiểm thử

| Cấp độ | Phương pháp | Công cụ |
|--------|-------------|---------|
| Unit Test – Service Layer | White-box, Mock Repository | JUnit 5, Mockito |
| Unit Test – Controller Layer | White-box, MockMvc | JUnit 5, Spring MockMvc |

### 2.2 Loại kiểm thử

- **Functional Testing**: Kiểm tra các chức năng đúng theo yêu cầu
- **Boundary Value Testing**: Kiểm tra các giá trị biên của bộ lọc giá
- **Negative Testing**: Kiểm tra khi không có dữ liệu trả về

---

## 3. THIẾT KẾ TEST CASE

### 3.1 F1 – Hiển thị danh sách gói Retreat

#### TC-SV-01: Lấy tất cả gói đang hoạt động – có dữ liệu

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-01 |
| **Tên** | getAllActivePackages – trả về danh sách đầy đủ |
| **Layer** | Service |
| **Điều kiện tiên quyết** | Repository có 3 gói active |
| **Input** | Không có tham số |
| **Expected Output** | List có 3 phần tử, chứa đúng tên gói |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `getAllActivePackages_returnsAllActivePackages()` |

**Bước thực hiện:**
1. Mock `findByIsActiveTrueAndIsDeleteFalse()` trả về danh sách 3 gói
2. Gọi `retreatPackageService.getAllActivePackages()`
3. Kiểm tra kết quả có đúng 3 phần tử
4. Kiểm tra tên gói khớp

---

#### TC-SV-02: Lấy tất cả gói đang hoạt động – không có dữ liệu

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-02 |
| **Tên** | getAllActivePackages – trả về danh sách rỗng |
| **Layer** | Service |
| **Điều kiện tiên quyết** | Repository không có gói active |
| **Input** | Không có tham số |
| **Expected Output** | List rỗng (size = 0) |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `getAllActivePackages_returnsEmptyList_whenNoActivePackages()` |

---

#### TC-CT-01: Hiển thị trang danh sách – render đúng view và model

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-01 |
| **Tên** | GET /packages – render booking/packages với model |
| **Layer** | Controller |
| **Điều kiện tiên quyết** | Service trả về danh sách gói |
| **Input** | GET /packages |
| **Expected Output** | Status 200, view = "booking/packages", model có "packages" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_rendersPackagesView_withPackagesList()` |

---

#### TC-CT-02: selectedType mặc định là "All" khi không có tham số type

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-02 |
| **Tên** | GET /packages – selectedType = "All" khi không có type param |
| **Layer** | Controller |
| **Input** | GET /packages (không có param type) |
| **Expected Output** | model["selectedType"] = "All" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_setsSelectedTypeToAll_whenNoTypeParam()` |

---

### 3.2 F2 – Lọc theo mục tiêu sức khỏe

#### TC-SV-03: Lọc theo type trả về gói khớp

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-03 |
| **Tên** | searchPackages – lọc type "Yoga" trả về 1 gói |
| **Layer** | Service |
| **Input** | typePackage = "Yoga", durationDays = null, priceRange = null |
| **Expected Output** | List có 1 phần tử, typePackage = "Yoga" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_filterByType_returnsMatchingPackages()` |

---

#### TC-SV-04: Lọc theo type không có kết quả

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-04 |
| **Tên** | searchPackages – type "Meditation" không tồn tại |
| **Layer** | Service |
| **Input** | typePackage = "Meditation" |
| **Expected Output** | List rỗng |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `searchPackages_filterByType_returnsEmpty_whenNoMatch()` |

---

#### TC-CT-03: Controller lọc theo type – truyền đúng param

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-03 |
| **Tên** | GET /packages?type=Yoga – gọi service đúng tham số |
| **Layer** | Controller |
| **Input** | GET /packages?type=Yoga |
| **Expected Output** | Service được gọi với "Yoga", model["selectedType"] = "Yoga" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_filterByType_passesTypeToService()` |

---

### 3.3 F3 – Lọc theo số ngày

#### TC-SV-05: Lọc theo durationDays = 7

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-05 |
| **Tên** | searchPackages – lọc 7 ngày trả về 1 gói |
| **Layer** | Service |
| **Input** | durationDays = 7 |
| **Expected Output** | List có 1 phần tử, durationDays = 7 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_filterByDurationDays_returnsMatchingPackages()` |

---

#### TC-SV-06: Lọc theo durationDays không tìm thấy

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-06 |
| **Tên** | searchPackages – durationDays = 14 không tồn tại |
| **Layer** | Service |
| **Input** | durationDays = 14 |
| **Expected Output** | List rỗng |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `searchPackages_filterByDurationDays_returnsEmpty_whenNoMatch()` |

---

#### TC-CT-04: Controller lọc theo số ngày

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-04 |
| **Tên** | GET /packages?durationDays=7 – truyền đúng param |
| **Layer** | Controller |
| **Input** | GET /packages?durationDays=7 |
| **Expected Output** | Service gọi với durationDays=7, model["selectedDurationDays"] = 7 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_filterByDurationDays_passesDurationToService()` |

---

### 3.4 F4 – Lọc theo mức giá

#### TC-SV-07: Lọc giá UNDER_5 (< 5,000,000 VNĐ)

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-07 |
| **Tên** | searchPackages – priceRange UNDER_5 trả về 1 gói |
| **Layer** | Service |
| **Input** | priceRange = "UNDER_5" |
| **Expected Output** | List có 1 phần tử, price < 5,000,000 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_filterByPriceRangeUnder5_returnsMatchingPackages()` |

---

#### TC-SV-08: Lọc giá FROM_5_TO_10 (5,000,000 – 10,000,000 VNĐ)

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-08 |
| **Tên** | searchPackages – priceRange FROM_5_TO_10 trả về đúng gói |
| **Layer** | Service |
| **Input** | priceRange = "FROM_5_TO_10" |
| **Expected Output** | List có 1 phần tử, 5,000,000 ≤ price ≤ 10,000,000 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_filterByPriceRangeFrom5To10_returnsMatchingPackages()` |

---

#### TC-SV-09: Lọc giá OVER_10 (> 10,000,000 VNĐ)

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-09 |
| **Tên** | searchPackages – priceRange OVER_10 trả về đúng gói |
| **Layer** | Service |
| **Input** | priceRange = "OVER_10" |
| **Expected Output** | List có 1 phần tử, price > 10,000,000 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_filterByPriceRangeOver10_returnsMatchingPackages()` |

---

#### TC-SV-10: Kết hợp nhiều bộ lọc

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-10 |
| **Tên** | searchPackages – lọc type + days + price |
| **Layer** | Service |
| **Input** | type="Detox", durationDays=7, priceRange="FROM_5_TO_10" |
| **Expected Output** | Đúng 1 gói Detox, 7 ngày, giá 5-10 triệu |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `searchPackages_combinedFilters_returnsCorrectPackage()` |

---

#### TC-CT-05: Controller lọc theo priceRange

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-05 |
| **Tên** | GET /packages?priceRange=UNDER_5 – truyền đúng param |
| **Layer** | Controller |
| **Input** | GET /packages?priceRange=UNDER_5 |
| **Expected Output** | model["selectedPriceRange"] = "UNDER_5" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_filterByPriceRange_passesPriceRangeToService()` |

---

#### TC-CT-06: Controller kết hợp 3 bộ lọc

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-06 |
| **Tên** | GET /packages – kết hợp type + days + price |
| **Layer** | Controller |
| **Input** | GET /packages?type=Detox&durationDays=7&priceRange=FROM_5_TO_10 |
| **Expected Output** | Service được gọi với đúng 3 tham số |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_filterByAllCriteria_passesAllParamsToService()` |

---

### 3.5 F5 – Xem chi tiết từng gói

#### TC-SV-11: Lấy chi tiết gói theo ID tồn tại

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-11 |
| **Tên** | getPackageById – trả về đúng DTO |
| **Layer** | Service |
| **Input** | id = 1 |
| **Expected Output** | DTO với id=1, packageName="Yoga Basic", price=3,000,000 |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `getPackageById_returnsCorrectDTO()` |

---

#### TC-SV-12: Lấy chi tiết gói theo ID không tồn tại

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-12 |
| **Tên** | getPackageById – ném RuntimeException khi không tìm thấy |
| **Layer** | Service |
| **Input** | id = 99 (không tồn tại) |
| **Expected Output** | Throws RuntimeException("Retreat package not found") |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `getPackageById_throwsException_whenNotFound()` |

---

#### TC-CT-07: Controller hiển thị chi tiết gói

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-07 |
| **Tên** | GET /packages/1 – render package-detail view với model |
| **Layer** | Controller |
| **Input** | GET /packages/1 |
| **Expected Output** | Status 200, view = "booking/package-detail", model["pkg"] = DTO |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `packageDetail_rendersDetailView_withPackageData()` |

---

#### TC-CT-08: Controller xem chi tiết gói không tồn tại

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-08 |
| **Tên** | GET /packages/99 – lỗi 5xx khi không tìm thấy |
| **Layer** | Controller |
| **Input** | GET /packages/99 |
| **Expected Output** | Status 5xx Server Error |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `packageDetail_throwsException_whenPackageNotFound()` |

---

### 3.6 F6 – Hiển thị gợi ý khi không có kết quả

#### TC-SV-13: Lấy danh sách gợi ý Top 3

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-13 |
| **Tên** | getPopularPackages – trả về tối đa 3 gói |
| **Layer** | Service |
| **Điều kiện tiên quyết** | Repository có 3 gói active |
| **Input** | Không có tham số |
| **Expected Output** | List có 3 phần tử |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `getPopularPackages_returnsTop3Packages()` |

---

#### TC-SV-14: Gợi ý rỗng khi không có gói active

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-SV-14 |
| **Tên** | getPopularPackages – trả về rỗng khi không có gói |
| **Layer** | Service |
| **Input** | Repository rỗng |
| **Expected Output** | List rỗng |
| **Loại kiểm thử** | Negative |
| **Phương thức kiểm thử** | `getPopularPackages_returnsEmpty_whenNoActivePackages()` |

---

#### TC-CT-09: Controller thêm popularPackages khi kết quả rỗng

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-09 |
| **Tên** | GET /packages?type=Unknown – model có "popularPackages" |
| **Layer** | Controller |
| **Input** | GET /packages?type=Unknown (không có kết quả) |
| **Expected Output** | model["popularPackages"] = top 3 gói phổ biến |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_addsPopularPackages_whenResultIsEmpty()` |

---

#### TC-CT-10: Controller không thêm popularPackages khi có kết quả

| Thuộc tính | Nội dung |
|-----------|---------|
| **Test Case ID** | TC-CT-10 |
| **Tên** | GET /packages – model KHÔNG có "popularPackages" khi có kết quả |
| **Layer** | Controller |
| **Input** | GET /packages (có kết quả trả về) |
| **Expected Output** | model KHÔNG chứa "popularPackages" |
| **Loại kiểm thử** | Positive |
| **Phương thức kiểm thử** | `listPackages_doesNotAddPopularPackages_whenResultIsNotEmpty()` |

---

## 4. MA TRẬN TRUY VẾT TEST CASE

| Test Case ID | Tính năng | Layer | Loại | File Test |
|-------------|-----------|-------|------|-----------|
| TC-SV-01 | F1 – Hiển thị danh sách | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-02 | F1 – Hiển thị danh sách | Service | Negative | `RetreatPackageServiceImplTest.java` |
| TC-SV-03 | F2 – Lọc type | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-04 | F2 – Lọc type | Service | Negative | `RetreatPackageServiceImplTest.java` |
| TC-SV-05 | F3 – Lọc số ngày | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-06 | F3 – Lọc số ngày | Service | Negative | `RetreatPackageServiceImplTest.java` |
| TC-SV-07 | F4 – Lọc giá UNDER_5 | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-08 | F4 – Lọc giá FROM_5_TO_10 | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-09 | F4 – Lọc giá OVER_10 | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-10 | F4 – Kết hợp bộ lọc | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-11 | F5 – Chi tiết gói | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-12 | F5 – Chi tiết gói | Service | Negative | `RetreatPackageServiceImplTest.java` |
| TC-SV-13 | F6 – Gợi ý | Service | Positive | `RetreatPackageServiceImplTest.java` |
| TC-SV-14 | F6 – Gợi ý | Service | Negative | `RetreatPackageServiceImplTest.java` |
| TC-CT-01 | F1 – Hiển thị danh sách | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-02 | F1 – selectedType default | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-03 | F2 – Lọc type | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-04 | F3 – Lọc số ngày | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-05 | F4 – Lọc giá | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-06 | F4 – Kết hợp bộ lọc | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-07 | F5 – Chi tiết gói | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-08 | F5 – Chi tiết gói lỗi | Controller | Negative | `RetreatPackageControllerTest.java` |
| TC-CT-09 | F6 – Gợi ý khi rỗng | Controller | Positive | `RetreatPackageControllerTest.java` |
| TC-CT-10 | F6 – Không gợi ý khi có kết quả | Controller | Positive | `RetreatPackageControllerTest.java` |

---

## 5. MÔI TRƯỜNG KIỂM THỬ

| Thành phần | Phiên bản |
|-----------|---------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| JUnit | 5 (Jupiter) |
| Mockito | Tích hợp với Spring Boot Test |
| Build Tool | Maven |

---

## 6. KẾT QUẢ KIỂM THỬ THỰC TẾ

> *Phần này sẽ được điền sau khi chạy test.*

| Test Case ID | Kết quả | Ghi chú |
|-------------|---------|---------|
| TC-SV-01 | PASS / FAIL | |
| TC-SV-02 | PASS / FAIL | |
| TC-SV-03 | PASS / FAIL | |
| TC-SV-04 | PASS / FAIL | |
| TC-SV-05 | PASS / FAIL | |
| TC-SV-06 | PASS / FAIL | |
| TC-SV-07 | PASS / FAIL | |
| TC-SV-08 | PASS / FAIL | |
| TC-SV-09 | PASS / FAIL | |
| TC-SV-10 | PASS / FAIL | |
| TC-SV-11 | PASS / FAIL | |
| TC-SV-12 | PASS / FAIL | |
| TC-SV-13 | PASS / FAIL | |
| TC-SV-14 | PASS / FAIL | |
| TC-CT-01 | PASS / FAIL | |
| TC-CT-02 | PASS / FAIL | |
| TC-CT-03 | PASS / FAIL | |
| TC-CT-04 | PASS / FAIL | |
| TC-CT-05 | PASS / FAIL | |
| TC-CT-06 | PASS / FAIL | |
| TC-CT-07 | PASS / FAIL | |
| TC-CT-08 | PASS / FAIL | |
| TC-CT-09 | PASS / FAIL | |
| TC-CT-10 | PASS / FAIL | |
