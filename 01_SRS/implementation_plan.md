# Kế Hoạch Triển Khai Chi Tiết - UC06: Duyệt và lọc các "Gói trị liệu" theo mục tiêu sức khỏe

Tài liệu này trình bày thiết kế kỹ thuật và kế hoạch từng bước để triển khai **UC06: Khách duyệt và lọc các "Gói trị liệu"** trong dự án `auramoon`.

---

## I. Yêu Cầu Giao Diện & Trải Nghiệm (UI/UX)
Giao diện hiển thị danh sách gói trị liệu sẽ được thiết kế theo phong cách hiện đại (Premium design), sử dụng CSS thuần (Vanilla CSS) phối hợp hiệu ứng Glassmorphism và màu sắc HSL hài hòa để mang lại cảm giác thư thái của một Wellness Resort.

---

## II. Các Thay Đổi Mã Nguồn Đề Xuất (Proposed Changes)

Chúng ta sẽ tạo mới các lớp Java cho các tầng (Repository, Service, Controller, DTO) và tệp giao diện Thymeleaf (HTML/CSS) tương ứng.

### 1. Tầng Repository
#### [NEW] [RetreatPackageRepository.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/RetreatPackageRepository.java)
Tạo tầng truy vấn dữ liệu kế thừa `JpaRepository`:
- Lấy danh sách toàn bộ các gói trị liệu đang hoạt động (`isActive = true`).
- Lọc các gói trị liệu theo phân loại mục tiêu sức khỏe (`typePackage`).
- Lấy danh sách các phân loại độc bản (`typePackage` duy nhất) để làm danh mục lọc động trên giao diện.

```java
package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetreatPackageRepository extends JpaRepository<RetreatPackage, Integer> {
    List<RetreatPackage> findByIsActiveTrue();
    List<RetreatPackage> findByTypePackageAndIsActiveTrue(String typePackage);

    @Query("SELECT DISTINCT r.typePackage FROM RetreatPackage r WHERE r.isActive = true")
    List<String> findDistinctTypePackageByIsActiveTrue();
}
```

---

### 2. Tầng DTO (Data Transfer Object)
#### [NEW] [RetreatPackageDTO.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/dto/RetreatPackageDTO.java)
Lớp vận chuyển dữ liệu từ Service sang Controller và View, tránh expose trực tiếp Entity:

```java
package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackageDTO {
    private Integer id;
    private String typePackage;
    private String packageName;
    private Integer durationDays;
    private String services;
    private String description;
    private BigDecimal price;
}
```

---

### 3. Tầng Service
#### [NEW] [RetreatPackageService.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/RetreatPackageService.java)
Khai báo các phương thức nghiệp vụ:

```java
package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;

import java.util.List;

public interface RetreatPackageService {
    List<RetreatPackageDTO> getAllActivePackages();
    List<RetreatPackageDTO> getPackagesByType(String typePackage);
    List<String> getAllActivePackageTypes();
}
```

#### [NEW] [RetreatPackageServiceImpl.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/service/impl/RetreatPackageServiceImpl.java)
Hiện thực hóa các phương thức nghiệp vụ, thực hiện ánh xạ (map) từ thực thể `RetreatPackage` sang `RetreatPackageDTO`.

```java
package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetreatPackageServiceImpl implements RetreatPackageService {

    private final RetreatPackageRepository retreatPackageRepository;

    @Override
    public List<RetreatPackageDTO> getAllActivePackages() {
        return retreatPackageRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RetreatPackageDTO> getPackagesByType(String typePackage) {
        return retreatPackageRepository.findByTypePackageAndIsActiveTrue(typePackage)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllActivePackageTypes() {
        return retreatPackageRepository.findDistinctTypePackageByIsActiveTrue();
    }

    private RetreatPackageDTO convertToDTO(RetreatPackage retreatPackage) {
        return RetreatPackageDTO.builder()
                .id(retreatPackage.getId())
                .typePackage(retreatPackage.getTypePackage())
                .packageName(retreatPackage.getPackageName())
                .durationDays(retreatPackage.getDurationDays())
                .services(retreatPackage.getServices())
                .description(retreatPackage.getDescription())
                .price(retreatPackage.getPrice())
                .build();
    }
}
```

---

### 4. Tầng Controller
#### [NEW] [RetreatPackageController.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/controller/RetreatPackageController.java)
Tạo Controller điều hướng request:
- Ánh xạ đường dẫn `GET /packages`.
- Nhận tham số tùy chọn `type` (ví dụ: `/packages?type=Detox`).
- Đẩy dữ liệu danh sách gói (`packages`) và danh sách các mục tiêu lọc (`types`) vào Spring Model.

```java
package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/packages")
@RequiredArgsConstructor
public class RetreatPackageController {

    private final RetreatPackageService retreatPackageService;

    @GetMapping
    public String listPackages(@RequestParam(value = "type", required = false) String type, Model model) {
        List<RetreatPackageDTO> packages;
        if (type != null && !type.trim().isEmpty()) {
            packages = retreatPackageService.getPackagesByType(type);
            model.addAttribute("selectedType", type);
        } else {
            packages = retreatPackageService.getAllActivePackages();
            model.addAttribute("selectedType", "All");
        }

        model.addAttribute("packages", packages);
        model.addAttribute("types", retreatPackageService.getAllActivePackageTypes());
        return "booking/packages";
    }
}
```

---

### 5. Giao Diện Thymeleaf
#### [NEW] [packages.html](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/packages.html)
Trang giao diện người dùng hiển thị danh sách gói trị liệu:
- Sử dụng Thymeleaf để lặp và hiển thị danh sách gói trị liệu.
- Thiết kế thanh bên (Sidebar) hiển thị danh sách các mục tiêu lọc (tất cả, detox, yoga, stress relief...) một cách trực quan.
- Thiết kế layout lưới (Grid) hiển thị các thẻ gói trị liệu (Card) với đầy đủ thông tin.
- Tích hợp mã CSS tùy biến (Vanilla CSS) với phối màu sang trọng, hiện đại, phông chữ Outfit/Inter từ Google Fonts, và các hiệu ứng di chuột (hover micro-animations).

---

## III. Kế Hoạch Xác Minh (Verification Plan)

### Kiểm Thử Tự Động (Automated Tests)
* Chạy các bài kiểm thử unit test cho `RetreatPackageService` để kiểm tra kết quả lọc.
* Chạy build dự án bằng lệnh Maven: `mvnw clean compile` để đảm bảo code không lỗi cú pháp.

### Kiểm Thử Thủ Công (Manual Verification)
1. Khởi chạy ứng dụng và truy cập `http://localhost:8080/packages`.
2. Kiểm tra xem giao diện có tải và hiển thị danh sách đầy đủ.
3. Click vào các link lọc loại gói ở Sidebar để kiểm tra URL chuyển đổi đúng định dạng `/packages?type=...` và dữ liệu được lọc chính xác.
