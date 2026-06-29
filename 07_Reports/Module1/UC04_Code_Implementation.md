# Đề xuất Mã Nguồn: UC04 Master Data (Retreat Package & Spa Service)

Theo đúng **Nguyên tắc 2** và **Nguyên tắc 3** trong `principles.md`, dưới đây là toàn bộ mã nguồn được thiết kế cho các tầng Entity, Repository, DTO, và Service của chức năng quản lý danh mục. 
Vui lòng kiểm tra và phản hồi (Approve) trước khi tôi thực hiện đưa code này vào dự án chính thức.

## 1. Tầng Data (Entity & Repository)

### 1.1. RetreatPackage Entity
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/entity/RetreatPackage.java`

```java
package com.AuraMoon.auramoon.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "RETREAT_PACKAGE")
@SQLRestriction("is_delete = 0")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackage extends com.AuraMoon.auramoon.common.entity.BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "type_package", length = 50)
    private String typePackage;

    @Column(name = "package_name", length = 50)
    private String packageName;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "services", columnDefinition = "NVARCHAR(MAX)")
    private String services;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "price")
    private BigDecimal price;
}
```

### 1.2. TreatmentService Entity
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/entity/TreatmentService.java`

```java
package com.AuraMoon.auramoon.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "TREATMENT_SERVICE")
@SQLRestriction("is_delete = 0")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentService extends com.AuraMoon.auramoon.common.entity.BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "treatment_code", nullable = false, unique = true, length = 10)
    private String treatmentCode;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
```

### 1.3. Repositories
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/repository/...`

```java
package com.AuraMoon.auramoon.masterdata.repository;

import com.AuraMoon.auramoon.masterdata.entity.RetreatPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRetreatPackageRepository extends JpaRepository<RetreatPackage, Integer> {
}
```

```java
package com.AuraMoon.auramoon.masterdata.repository;

import com.AuraMoon.auramoon.masterdata.entity.TreatmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITreatmentServiceRepository extends JpaRepository<TreatmentService, Integer> {
    boolean existsByTreatmentCode(String treatmentCode);
    boolean existsByTreatmentCodeAndServiceIdNot(String treatmentCode, Integer serviceId);
}
```

## 2. Tầng Business Logic (DTO & Service)

### 2.1. DTO
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/dto/RetreatItineraryDto.java`

```java
package com.AuraMoon.auramoon.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetreatItineraryDto {
    private String generalDescription;
    private List<String> dailyActivities;
}
```

### 2.2. Service Interface
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/service/IMasterDataService.java`

```java
package com.AuraMoon.auramoon.masterdata.service;

import java.util.List;

public interface IMasterDataService<T, ID> {
    List<T> findAll();
    T findById(ID id);
    T create(T dto);
    T update(ID id, T dto);
    void softDelete(ID id);
}
```

### 2.3. RetreatPackage Service Impl
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/service/impl/RetreatPackageServiceImpl.java`

```java
package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.masterdata.dto.RetreatItineraryDto;
import com.AuraMoon.auramoon.masterdata.entity.RetreatPackage;
import com.AuraMoon.auramoon.masterdata.repository.IRetreatPackageRepository;
import com.AuraMoon.auramoon.masterdata.service.IMasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetreatPackageServiceImpl implements IMasterDataService<RetreatPackage, Integer> {

    private final IRetreatPackageRepository retreatPackageRepository;

    @Override
    public List<RetreatPackage> findAll() {
        return retreatPackageRepository.findAll();
    }

    @Override
    public RetreatPackage findById(Integer id) {
        return retreatPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
    }

    @Override
    public RetreatPackage create(RetreatPackage dto) {
        dto.setPackageId(null);
        return retreatPackageRepository.save(dto);
    }

    @Override
    public RetreatPackage update(Integer id, RetreatPackage dto) {
        RetreatPackage existing = findById(id);
        existing.setPackageName(dto.getPackageName());
        existing.setTypePackage(dto.getTypePackage());
        existing.setDurationDays(dto.getDurationDays());
        existing.setPrice(dto.getPrice());
        existing.setServices(dto.getServices());
        existing.setDescription(dto.getDescription());
        return retreatPackageRepository.save(existing);
    }

    @Override
    public void softDelete(Integer id) {
        RetreatPackage existing = findById(id);
        existing.setIsDelete(true);
        existing.setIsActive(false);
        retreatPackageRepository.save(existing);
    }

    /**
     * Parse chuỗi mô tả chứa [DAY] thành DTO.
     */
    public RetreatItineraryDto parseDescription(String rawDescription) {
        if (rawDescription == null || rawDescription.isEmpty()) {
            return new RetreatItineraryDto("", new ArrayList<>());
        }
        String[] parts = rawDescription.split("\\[DAY\\]");
        String general = parts[0].trim();
        List<String> activities = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            activities.add(parts[i].trim());
        }
        return new RetreatItineraryDto(general, activities);
    }
}
```

### 2.4. TreatmentService Impl
**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/masterdata/service/impl/TreatmentServiceImpl.java`

```java
package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.masterdata.entity.TreatmentService;
import com.AuraMoon.auramoon.masterdata.repository.ITreatmentServiceRepository;
import com.AuraMoon.auramoon.masterdata.service.IMasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentServiceImpl implements IMasterDataService<TreatmentService, Integer> {

    private final ITreatmentServiceRepository treatmentServiceRepository;

    @Override
    public List<TreatmentService> findAll() {
        return treatmentServiceRepository.findAll();
    }

    @Override
    public TreatmentService findById(Integer id) {
        return treatmentServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    @Override
    public TreatmentService create(TreatmentService dto) {
        if (treatmentServiceRepository.existsByTreatmentCode(dto.getTreatmentCode())) {
            throw new IllegalArgumentException("Treatment code already exists");
        }
        dto.setServiceId(null);
        return treatmentServiceRepository.save(dto);
    }

    @Override
    public TreatmentService update(Integer id, TreatmentService dto) {
        TreatmentService existing = findById(id);
        if (treatmentServiceRepository.existsByTreatmentCodeAndServiceIdNot(dto.getTreatmentCode(), id)) {
            throw new IllegalArgumentException("Treatment code already exists");
        }
        existing.setTreatmentCode(dto.getTreatmentCode());
        existing.setServiceName(dto.getServiceName());
        existing.setDurationMinutes(dto.getDurationMinutes());
        existing.setPrice(dto.getPrice());
        existing.setIsAvailable(dto.getIsAvailable());
        return treatmentServiceRepository.save(existing);
    }

    @Override
    public void softDelete(Integer id) {
        TreatmentService existing = findById(id);
        existing.setIsDelete(true);
        existing.setIsAvailable(false);
        treatmentServiceRepository.save(existing);
    }
}
```
