# EXPERIMENT DESIGN SPECIFICATION (EDS)
## Tài liệu Đặc tả Thiết kế Thực nghiệm

---

| Thông tin | Chi tiết |
|-----------|---------|
| **Tên dự án** | AuraMoon – Hệ thống quản lý Retreat Resort |
| **Phiên bản** | 2.0 |
| **Ngày tạo** | 2025 |
| **Module** | Module 2 – Duyệt & Lọc Gói Trị Liệu (UC06) |
| **Người thực hiện** | Nhóm 6 – SWP391 |
| **Trạng thái** | Draft |

---

## 1. GIỚI THIỆU

### 1.1 Mục đích

Tài liệu này đặc tả chi tiết thiết kế thực nghiệm (experiment design) cho việc kiểm thử tính năng UC06 – **Hiển thị và Lọc danh sách gói Retreat** của hệ thống AuraMoon. Tài liệu cung cấp đầy đủ thông tin về:

- Mô tả các thực nghiệm kiểm thử
- Input/Output của từng test
- Cơ sở kỳ vọng (oracle) của từng test case
- Cấu trúc test code tương ứng

### 1.2 Tài liệu tham chiếu

| Tài liệu | Mô tả |
|---------|-------|
| UC6.md | Phân tích chi tiết UC06 |
| UC_module2.md | Đặc tả Use Case Module 2 |
| RetreatPackageController.java | Controller layer |
| RetreatPackageServiceImpl.java | Service layer |
| RetreatPackageRepository.java | Repository layer |
| TDD.md | Test Design Document |

---

## 2. MÔ TẢ HỆ THỐNG DƯỚI KIỂM THỬ (SUT)

### 2.1 Kiến trúc hệ thống

```
┌────────────────────────────────────────────────────┐
│                  HTTP Request                      │
├────────────────────────────────────────────────────┤
│         RetreatPackageController (SUT-1)           │
│  GET /packages, GET /packages/{id}                 │
├────────────────────────────────────────────────────┤
│         RetreatPackageService (SUT-2)              │
│  getAllActivePackages(), searchPackages()           │
│  getPackageById(), getPopularPackages()             │
├────────────────────────────────────────────────────┤
│         RetreatPackageRepository (Mock)            │
│  JPA Repository – được mock trong unit test        │
└────────────────────────────────────────────────────┘
```

### 2.2 Các thành phần

| Thành phần | Vai trò | Trong unit test |
|-----------|---------|----------------|
| `RetreatPackageController` | Nhận HTTP request, gọi Service, trả về View | SUT khi test Controller |
| `RetreatPackageServiceImpl` | Xử lý business logic, chuyển đổi Entity→DTO | SUT khi test Service |
| `RetreatPackageRepository` | Truy vấn database | **Được mock** bằng Mockito |

---

## 3. ĐẶC TẢ THỰC NGHIỆM KIỂM THỬ

### 3.1 Nhóm thực nghiệm: SERVICE LAYER

**Mục tiêu:** Kiểm tra logic nghiệp vụ tại Service layer độc lập với Controller và Database.

**Cấu hình thực nghiệm:**

```java
@ExtendWith(MockitoExtension.class)
class RetreatPackageServiceImplTest {

    @Mock
    private RetreatPackageRepository retreatPackageRepository;

    @InjectMocks
    private RetreatPackageServiceImpl retreatPackageService;
}
```

---

#### EXP-SV-01: Hiển thị danh sách gói Retreat

**Mô tả thực nghiệm:**  
Kiểm tra phương thức `getAllActivePackages()` trả về đúng danh sách các gói đang hoạt động và không bị xóa.

**Input Setup:**

```java
// Dữ liệu đầu vào
pkg1 = RetreatPackage.builder()
    .id(1).typePackage("Yoga").packageName("Yoga Basic")
    .durationDays(3).price(new BigDecimal("3000000"))
    .isActive(true).build();
pkg1.setIsDelete(false);

// Mock hành vi
when(retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse())
    .thenReturn(List.of(pkg1, pkg2, pkg3));
```

**Oracle (Kỳ vọng):**

| Thuộc tính kiểm tra | Kỳ vọng |
|---------------------|---------|
| Kích thước danh sách | 3 |
| Các tên gói | "Yoga Basic", "Detox Premium", "Spa Luxury" |
| Không chứa trường nội bộ | isActive, isDelete, createdAt không lộ ra DTO |

**Test Code Mapping:**
- `getAllActivePackages_returnsAllActivePackages()`
- `getAllActivePackages_returnsEmptyList_whenNoActivePackages()`

**Lý giải oracle:**  
`getAllActivePackages()` phải map tất cả Entity thỏa điều kiện `isActive=true AND isDelete=false` thành DTO. DTO không được chứa các trường nội bộ.

---

#### EXP-SV-02: Lọc theo mục tiêu sức khỏe (typePackage)

**Mô tả thực nghiệm:**  
Kiểm tra `searchPackages(typePackage, null, null)` lọc đúng theo loại gói.

**Các biến thực nghiệm:**

| Biến | Giá trị thử nghiệm |
|------|-------------------|
| typePackage | "Yoga", "Detox", "Spa", "Meditation" (không tồn tại), null |
| durationDays | null |
| priceRange | null |

**Bảng kết quả kỳ vọng:**

| typePackage | Kỳ vọng |
|-------------|---------|
| "Yoga" | 1 gói, typePackage = "Yoga" |
| "Detox" | 1 gói, typePackage = "Detox" |
| "Meditation" | 0 gói (không tồn tại) |
| null | Tất cả gói |

**Test Code Mapping:**
- `searchPackages_filterByType_returnsMatchingPackages()`
- `searchPackages_filterByType_returnsEmpty_whenNoMatch()`
- `searchPackages_noTypeFilter_returnsAllPackages()`

---

#### EXP-SV-03: Lọc theo số ngày (durationDays)

**Mô tả thực nghiệm:**  
Kiểm tra `searchPackages(null, durationDays, null)` lọc đúng theo số ngày lưu trú.

**Các biến thực nghiệm:**

| Biến | Giá trị thử nghiệm |
|------|-------------------|
| durationDays | 3, 7, 14 (không tồn tại), null |

**Bảng kết quả kỳ vọng:**

| durationDays | Kỳ vọng |
|-------------|---------|
| 7 | 1 gói, durationDays = 7 |
| 14 | 0 gói (không tồn tại) |
| null | Tất cả gói |

**Test Code Mapping:**
- `searchPackages_filterByDurationDays_returnsMatchingPackages()`
- `searchPackages_filterByDurationDays_returnsEmpty_whenNoMatch()`

---

#### EXP-SV-04: Lọc theo mức giá (priceRange)

**Mô tả thực nghiệm:**  
Kiểm tra `searchPackages(null, null, priceRange)` lọc đúng theo dải giá. Hệ thống định nghĩa 3 dải giá cố định.

**Định nghĩa dải giá (theo Repository Query):**

| priceRange | Điều kiện SQL | Mô tả |
|------------|--------------|-------|
| `UNDER_5` | `price < 5,000,000` | Dưới 5 triệu VNĐ |
| `FROM_5_TO_10` | `price >= 5,000,000 AND price <= 10,000,000` | Từ 5 đến 10 triệu VNĐ |
| `OVER_10` | `price > 10,000,000` | Trên 10 triệu VNĐ |

**Dữ liệu thử nghiệm:**

| Gói | Giá | Thuộc dải |
|-----|-----|----------|
| pkg1 (Yoga Basic) | 3,000,000 VNĐ | UNDER_5 |
| pkg2 (Detox Premium) | 8,000,000 VNĐ | FROM_5_TO_10 |
| pkg3 (Spa Luxury) | 12,000,000 VNĐ | OVER_10 |

**Bảng kết quả kỳ vọng:**

| priceRange | Kỳ vọng |
|------------|---------|
| "UNDER_5" | 1 gói, price < 5,000,000 |
| "FROM_5_TO_10" | 1 gói, 5,000,000 ≤ price ≤ 10,000,000 |
| "OVER_10" | 1 gói, price > 10,000,000 |

**Test Code Mapping:**
- `searchPackages_filterByPriceRangeUnder5_returnsMatchingPackages()`
- `searchPackages_filterByPriceRangeFrom5To10_returnsMatchingPackages()`
- `searchPackages_filterByPriceRangeOver10_returnsMatchingPackages()`

---

#### EXP-SV-05: Kết hợp nhiều bộ lọc

**Mô tả thực nghiệm:**  
Kiểm tra tìm kiếm với đồng thời cả 3 bộ lọc: type + days + price.

**Input kết hợp:**

| type | durationDays | priceRange | Kỳ vọng |
|------|-------------|------------|---------|
| "Detox" | 7 | "FROM_5_TO_10" | 1 gói (pkg2) |

**Test Code Mapping:**
- `searchPackages_combinedFilters_returnsCorrectPackage()`

---

#### EXP-SV-06: Xem chi tiết từng gói

**Mô tả thực nghiệm:**  
Kiểm tra `getPackageById(id)` trả về đúng DTO hoặc ném exception.

**Các trường hợp thử nghiệm:**

| Trường hợp | Input | Oracle |
|-----------|-------|--------|
| Gói tồn tại | id = 1 | DTO với đúng id, name, price |
| Gói không tồn tại | id = 99 | RuntimeException("Retreat package not found") |
| Gói bị xóa (isDelete=true) | id hợp lệ nhưng isDelete=true | RuntimeException |

**Test Code Mapping:**
- `getPackageById_returnsCorrectDTO()`
- `getPackageById_throwsException_whenNotFound()`

---

#### EXP-SV-07: Gợi ý gói phổ biến

**Mô tả thực nghiệm:**  
Kiểm tra `getPopularPackages()` trả về tối đa 3 gói sắp xếp theo ID tăng dần.

**Oracle:**

| Điều kiện | Kỳ vọng |
|----------|---------|
| Có >= 3 gói active | Trả về đúng 3 gói |
| Không có gói active | Trả về danh sách rỗng |

**Test Code Mapping:**
- `getPopularPackages_returnsTop3Packages()`
- `getPopularPackages_returnsEmpty_whenNoActivePackages()`

---

### 3.2 Nhóm thực nghiệm: CONTROLLER LAYER

**Mục tiêu:** Kiểm tra Controller layer xử lý đúng HTTP request, truyền đúng tham số cho Service, và trả về đúng View/Model.

**Cấu hình thực nghiệm:**

```java
@WebMvcTest(RetreatPackageController.class)
class RetreatPackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetreatPackageService retreatPackageService;
}
```

**Lý do dùng `@WebMvcTest`:**  
- Chỉ load Spring MVC context (không load JPA, full context)
- MockMvc cho phép gửi HTTP request giả lập không cần server thật
- `@MockitoBean` thay thế Service thật bằng mock

---

#### EXP-CT-01: Hiển thị danh sách – HTTP GET /packages

**Mô tả thực nghiệm:**  
Kiểm tra endpoint `GET /packages` trả về đúng status, view, và model attributes.

**Input HTTP:**
```
GET /packages
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| HTTP Status | 200 OK |
| View name | "booking/packages" |
| model["packages"] | Danh sách DTO |
| model["types"] | Danh sách type |
| model["selectedType"] | "All" |

**Test Code Mapping:**
- `listPackages_rendersPackagesView_withPackagesList()`
- `listPackages_setsSelectedTypeToAll_whenNoTypeParam()`

---

#### EXP-CT-02: Lọc type – HTTP GET /packages?type=Yoga

**Mô tả thực nghiệm:**  
Kiểm tra `type` param được truyền đúng vào Service và phản ánh đúng trong model.

**Input HTTP:**
```
GET /packages?type=Yoga
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| Service được gọi với | searchPackages("Yoga", null, null) |
| model["selectedType"] | "Yoga" |
| model["packages"] | Danh sách gói Yoga |

**Test Code Mapping:**
- `listPackages_filterByType_passesTypeToService()`
- `listPackages_filterByType_setsSelectedTypeInModel()`

---

#### EXP-CT-03: Lọc số ngày – HTTP GET /packages?durationDays=7

**Mô tả thực nghiệm:**  
Kiểm tra `durationDays` param được parse sang Integer và truyền đúng vào Service.

**Input HTTP:**
```
GET /packages?durationDays=7
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| Service được gọi với | searchPackages(null, 7, null) |
| model["selectedDurationDays"] | Integer(7) |

**Test Code Mapping:**
- `listPackages_filterByDurationDays_passesDurationToService()`

---

#### EXP-CT-04: Lọc giá – HTTP GET /packages?priceRange=UNDER_5

**Mô tả thực nghiệm:**  
Kiểm tra `priceRange` param được truyền đúng vào Service.

**Input HTTP:**
```
GET /packages?priceRange=UNDER_5
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| Service được gọi với | searchPackages(null, null, "UNDER_5") |
| model["selectedPriceRange"] | "UNDER_5" |

**Test Code Mapping:**
- `listPackages_filterByPriceRange_passesPriceRangeToService()`

---

#### EXP-CT-05: Kết hợp 3 bộ lọc

**Input HTTP:**
```
GET /packages?type=Detox&durationDays=7&priceRange=FROM_5_TO_10
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| Service được gọi với | searchPackages("Detox", 7, "FROM_5_TO_10") |

**Test Code Mapping:**
- `listPackages_filterByAllCriteria_passesAllParamsToService()`

---

#### EXP-CT-06: Xem chi tiết gói – HTTP GET /packages/{id}

**Mô tả thực nghiệm:**  
Kiểm tra endpoint `GET /packages/1` render đúng view chi tiết.

**Input HTTP:**
```
GET /packages/1
```

**Bảng oracle:**

| Thuộc tính | Kỳ vọng |
|-----------|---------|
| HTTP Status | 200 OK |
| View name | "booking/package-detail" |
| model["pkg"] | DTO của gói id=1 |

**Test Code Mapping:**
- `packageDetail_rendersDetailView_withPackageData()`
- `packageDetail_throwsException_whenPackageNotFound()`

---

#### EXP-CT-07: Gợi ý khi không có kết quả

**Mô tả thực nghiệm:**  
Kiểm tra khi `packages` rỗng, Controller gọi `getPopularPackages()` và thêm vào model.

**Input HTTP:**
```
GET /packages?type=Unknown
```

**Bảng oracle:**

| Điều kiện | Kỳ vọng |
|----------|---------|
| packages rỗng | `getPopularPackages()` được gọi |
| model | Chứa "popularPackages" |
| packages không rỗng | `getPopularPackages()` KHÔNG được gọi |
| model | KHÔNG chứa "popularPackages" |

**Test Code Mapping:**
- `listPackages_addsPopularPackages_whenResultIsEmpty()`
- `listPackages_doesNotAddPopularPackages_whenResultIsNotEmpty()`

---

## 4. CẤU TRÚC FILE TEST

### 4.1 RetreatPackageServiceImplTest.java

```
src/test/java/com/AuraMoon/auramoon/booking/
└── RetreatPackageServiceImplTest.java
    ├── setUp() – khởi tạo dữ liệu test (pkg1, pkg2, pkg3)
    │
    ├── [EXP-SV-01] Hiển thị danh sách
    │   ├── getAllActivePackages_returnsAllActivePackages()
    │   └── getAllActivePackages_returnsEmptyList_whenNoActivePackages()
    │
    ├── [EXP-SV-02] Lọc type
    │   ├── searchPackages_filterByType_returnsMatchingPackages()
    │   ├── searchPackages_filterByType_returnsEmpty_whenNoMatch()
    │   └── searchPackages_noTypeFilter_returnsAllPackages()
    │
    ├── [EXP-SV-03] Lọc số ngày
    │   ├── searchPackages_filterByDurationDays_returnsMatchingPackages()
    │   └── searchPackages_filterByDurationDays_returnsEmpty_whenNoMatch()
    │
    ├── [EXP-SV-04] Lọc giá
    │   ├── searchPackages_filterByPriceRangeUnder5_returnsMatchingPackages()
    │   ├── searchPackages_filterByPriceRangeFrom5To10_returnsMatchingPackages()
    │   └── searchPackages_filterByPriceRangeOver10_returnsMatchingPackages()
    │
    ├── [EXP-SV-05] Kết hợp bộ lọc
    │   └── searchPackages_combinedFilters_returnsCorrectPackage()
    │
    ├── [EXP-SV-06] Chi tiết gói
    │   ├── getPackageById_returnsCorrectDTO()
    │   └── getPackageById_throwsException_whenNotFound()
    │
    └── [EXP-SV-07] Gợi ý phổ biến
        ├── getPopularPackages_returnsTop3Packages()
        └── getPopularPackages_returnsEmpty_whenNoActivePackages()
```

### 4.2 RetreatPackageControllerTest.java

```
src/test/java/com/AuraMoon/auramoon/booking/
└── RetreatPackageControllerTest.java
    ├── buildDTO() – helper tạo DTO test
    │
    ├── [EXP-CT-01] Hiển thị danh sách
    │   ├── listPackages_rendersPackagesView_withPackagesList()
    │   └── listPackages_setsSelectedTypeToAll_whenNoTypeParam()
    │
    ├── [EXP-CT-02] Lọc type
    │   ├── listPackages_filterByType_passesTypeToService()
    │   └── listPackages_filterByType_setsSelectedTypeInModel()
    │
    ├── [EXP-CT-03] Lọc số ngày
    │   └── listPackages_filterByDurationDays_passesDurationToService()
    │
    ├── [EXP-CT-04] Lọc giá
    │   └── listPackages_filterByPriceRange_passesPriceRangeToService()
    │
    ├── [EXP-CT-05] Kết hợp bộ lọc
    │   └── listPackages_filterByAllCriteria_passesAllParamsToService()
    │
    ├── [EXP-CT-06] Chi tiết gói
    │   ├── packageDetail_rendersDetailView_withPackageData()
    │   └── packageDetail_throwsException_whenPackageNotFound()
    │
    └── [EXP-CT-07] Gợi ý khi rỗng
        ├── listPackages_addsPopularPackages_whenResultIsEmpty()
        └── listPackages_doesNotAddPopularPackages_whenResultIsNotEmpty()
```

---

## 5. ĐẶC TẢ DỮ LIỆU THỬ NGHIỆM

### 5.1 Bộ dữ liệu chuẩn (Standard Dataset)

| Tên | typePackage | durationDays | price (VNĐ) | isActive | isDelete |
|-----|------------|-------------|------------|---------|---------|
| pkg1 | "Yoga" | 3 | 3,000,000 | true | false |
| pkg2 | "Detox" | 7 | 8,000,000 | true | false |
| pkg3 | "Spa" | 5 | 12,000,000 | true | false |

### 5.2 Đặc tả priceRange

| Hằng số | Điều kiện | Áp dụng cho gói |
|---------|-----------|----------------|
| `UNDER_5` | price < 5,000,000 | pkg1 |
| `FROM_5_TO_10` | price ≥ 5,000,000 AND price ≤ 10,000,000 | pkg2 |
| `OVER_10` | price > 10,000,000 | pkg3 |

---

## 6. PHÂN TÍCH RỦI RO

| Rủi ro | Mô tả | Biện pháp |
|--------|-------|----------|
| R1 – Type không phân biệt hoa thường | "Yoga" vs "yoga" có thể cho kết quả khác nhau | Test với giá trị đúng case |
| R2 – BigDecimal so sánh | `isBetween` của BigDecimal cần đúng precision | Sử dụng `isEqualByComparingTo` |
| R3 – Null parameter | `type=null` vs `type=""` hành vi khác nhau | Test cả 2 trường hợp |
| R4 – MockMvc và Spring context | `@WebMvcTest` không load đầy đủ context | Chú ý `@MockitoBean` đúng annotation |

---

## 7. TIÊU CHÍ PASS/FAIL

| Tiêu chí | Điều kiện PASS |
|---------|---------------|
| Functional | 100% test case positive phải PASS |
| Negative | 100% test case negative phải ném đúng exception hoặc trả về đúng status |
| Coverage | Tối thiểu 80% code coverage trên Service layer |
| Performance | Mỗi unit test hoàn thành < 500ms |
