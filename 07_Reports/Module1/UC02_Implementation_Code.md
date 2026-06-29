# Báo cáo Triển khai Mã nguồn (Implementation Source Code Report) - UC02

**Ngày tạo:** 2026-06-23
**Tuân thủ Principle:**

- Giao diện MVC, không dùng JSON REST API (Principle 1).
- Trình bày dạng Markdown chờ kiểm duyệt (Principle 2 & 3).
- Tách biệt CSS vào static/css (Principle 4).

Dưới đây là toàn bộ source code của các thành phần cần thiết để hoàn thành Use Case 02 theo bản Kế Hoạch Thực Thi (Execution Plan) và Đặc Tả (EDS). Vui lòng review và xác nhận để tôi tiến hành ghi đè vào source code thật.

---

### 1. `PhysicalHealthProfileRepository.java`

**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/spa/repository/PhysicalHealthProfileRepository.java`

```java
package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PhysicalHealthProfileRepository extends JpaRepository<PhysicalHealthProfile, Integer> {
    Optional<PhysicalHealthProfile> findByUserId(Integer userId);
}
```

---

### 2. `SensitiveProfileDto.java`

**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/auth/dto/SensitiveProfileDto.java`

```java
package com.AuraMoon.auramoon.auth.dto;

import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class SensitiveProfileDto {
    private String medicalConditions;
    private String injuries;
    private String foodAllergies;
    private String dietaryPreference;

    @AssertTrue(message = "MSG-03: Bạn phải đồng ý với các điều khoản bảo mật dữ liệu y tế để tiếp tục")
    private boolean hasConsent;

    private static final AesDataEncryptor encryptor = new AesDataEncryptor();

    public PhysicalHealthProfile toEncryptedPhysicalProfile(PhysicalHealthProfile profile) {
        if (profile == null) profile = new PhysicalHealthProfile();
        profile.setMedicalConditions(encryptor.convertToDatabaseColumn(medicalConditions));
        profile.setInjuries(encryptor.convertToDatabaseColumn(injuries));
        return profile;
    }

    public DietaryProfile toEncryptedDietaryProfile(DietaryProfile profile) {
        if (profile == null) profile = new DietaryProfile();
        profile.setFoodAllergies(encryptor.convertToDatabaseColumn(foodAllergies));
        profile.setDietaryPreference(encryptor.convertToDatabaseColumn(dietaryPreference));
        return profile;
    }

    public static SensitiveProfileDto fromEntities(PhysicalHealthProfile phys, DietaryProfile diet) {
        SensitiveProfileDto dto = new SensitiveProfileDto();
        if (phys != null) {
            dto.setMedicalConditions(encryptor.convertToEntityAttribute(phys.getMedicalConditions()));
            dto.setInjuries(encryptor.convertToEntityAttribute(phys.getInjuries()));
        }
        if (diet != null) {
            dto.setFoodAllergies(encryptor.convertToEntityAttribute(diet.getFoodAllergies()));
            dto.setDietaryPreference(encryptor.convertToEntityAttribute(diet.getDietaryPreference()));
        }
        dto.setHasConsent(false); // BR-08: Luôn bỏ check Consent khi load Form
        return dto;
    }
}
```

---

### 3. `IProfileService.java`

**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/auth/service/IProfileService.java`

```java
package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;

public interface IProfileService {
    SensitiveProfileDto getSensitiveProfile(Integer userId);
    void saveSensitiveProfile(SensitiveProfileDto inputDto, Integer userId);
}
```

---

### 4. `ProfileServiceImpl.java`

**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/auth/service/impl/ProfileServiceImpl.java`

```java
package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.entity.Consent;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.service.IProfileService;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ProfileServiceImpl implements IProfileService {

    @Autowired
    private PhysicalHealthProfileRepository physicalHealthProfileRepository;

    @Autowired
    private DietaryProfileRepository dietaryProfileRepository;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public SensitiveProfileDto getSensitiveProfile(Integer userId) {
        PhysicalHealthProfile phys = physicalHealthProfileRepository.findByUserId(userId).orElse(null);
        DietaryProfile diet = dietaryProfileRepository.findByUserId(userId).orElse(null);
        return SensitiveProfileDto.fromEntities(phys, diet);
    }

    @Override
    @Transactional
    public void saveSensitiveProfile(SensitiveProfileDto inputDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Lưu Medical Conditions (Mã hóa)
        PhysicalHealthProfile phys = physicalHealthProfileRepository.findByUserId(userId).orElse(null);
        phys = inputDto.toEncryptedPhysicalProfile(phys);
        phys.setUserId(userId);
        phys.setUpdatedAt(LocalDateTime.now());
        physicalHealthProfileRepository.save(phys);

        // Lưu Dietary Profile (Mã hóa)
        DietaryProfile diet = dietaryProfileRepository.findByUserId(userId).orElse(null);
        diet = inputDto.toEncryptedDietaryProfile(diet);
        diet.setUserId(userId);
        diet.setUpdatedAt(LocalDateTime.now());
        dietaryProfileRepository.save(diet);

        // Lưu Consent
        Consent consent = Consent.builder()
                .user(user)
                .consentStatus(true)
                .consentVersion("1.0")
                .build();
        consent.setUpdatedAt(LocalDateTime.now());
        consentRepository.save(consent);

        // Audit Log (BR-15)
        AuditLog log = AuditLog.builder()
                .actionType("UPDATE_HEALTH_PROFILE")
                .actorId(userId)
                .details("Guest updated health and dietary profile")
                .timestamp(new Date())
                .build();
        auditLogRepository.save(log);
    }
}
```

---

### 5. `ProfileController.java`

**Đường dẫn dự kiến:** `src/main/java/com/AuraMoon/auramoon/auth/controller/ProfileController.java`

```java
package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.auth.service.IProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private IProfileService profileService;

    @GetMapping("/me")
    public String viewProfile(Model model, Authentication authentication,
                              @RequestParam(value = "success", required = false) String success) {
        Integer userId = extractUserId(authentication);
        if (userId == null) return "redirect:/auth/login";

        SensitiveProfileDto dto = profileService.getSensitiveProfile(userId);
        model.addAttribute("profileDto", dto);

        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Hồ sơ sức khỏe & dinh dưỡng đã được lưu thành công!");
        }

        return "auth/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDto") SensitiveProfileDto dto,
                                BindingResult result, Model model, Authentication authentication) {
        // Exception E1
        if (!dto.isHasConsent()) {
            model.addAttribute("error", "MSG-03: Bạn phải đồng ý với các điều khoản bảo mật dữ liệu y tế để tiếp tục");
            return "auth/profile";
        }
      
        if (result.hasErrors()) {
            return "auth/profile";
        }

        try {
            Integer userId = extractUserId(authentication);
            if (userId == null) return "redirect:/auth/login";

            profileService.saveSensitiveProfile(dto, userId);
            return "redirect:/profile/me?success=true";
        } catch (Exception e) {
            // Exception E2
            model.addAttribute("error", "MSG-15: Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return "auth/profile";
        }
    }

    private Integer extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsResponse) {
            return ((UserDetailsResponse) authentication.getPrincipal()).getId();
        }
        return null;
    }
}
```

---

### 6. `profile.html` (Thymeleaf View)

**Đường dẫn dự kiến:** `src/main/resources/templates/auth/profile.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Health & Dietary Profile - AuraMoon</title>
    <!-- Tuân thủ Principle 4 -->
    <link rel="stylesheet" th:href="@{/css/auth/profile.css}">
</head>
<body>
<div class="profile-container">
    <h2>Hồ Sơ Sức Khỏe & Dinh Dưỡng</h2>
    <p>Thông tin của bạn được mã hóa an toàn và chỉ cung cấp cho chuyên gia trị liệu và đầu bếp để phục vụ bạn tốt hơn.</p>

    <!-- MSG-15 Hoặc MSG-03 -->
    <div th:if="${error}" class="alert alert-danger" th:text="${error}"></div>
    <!-- Success Message -->
    <div th:if="${successMessage}" class="alert alert-success" th:text="${successMessage}"></div>

    <form th:action="@{/profile/update}" th:object="${profileDto}" method="POST" class="profile-form">
  
        <!-- Khu vực 1: Sức khỏe Vật lý -->
        <fieldset>
            <legend>Tình trạng Sức khỏe (Physical Health)</legend>
            <div class="form-group">
                <label for="medicalConditions">Tình trạng Y tế hiện tại:</label>
                <textarea id="medicalConditions" th:field="*{medicalConditions}" rows="3" placeholder="Ví dụ: Tiểu đường, Cao huyết áp..."></textarea>
            </div>
            <div class="form-group">
                <label for="injuries">Chấn thương / Lưu ý Vật lý:</label>
                <textarea id="injuries" th:field="*{injuries}" rows="3" placeholder="Ví dụ: Đau mỏi thắt lưng..."></textarea>
            </div>
        </fieldset>

        <!-- Khu vực 2: Dinh dưỡng & Dị ứng -->
        <fieldset>
            <legend>Chế độ Dinh dưỡng (Diet & Allergies)</legend>
            <div class="form-group">
                <label for="foodAllergies">Dị ứng thực phẩm:</label>
                <textarea id="foodAllergies" th:field="*{foodAllergies}" rows="2" placeholder="Ví dụ: Hải sản, Đậu phộng..."></textarea>
            </div>
            <div class="form-group">
                <label for="dietaryPreference">Sở thích Ăn kiêng:</label>
                <textarea id="dietaryPreference" th:field="*{dietaryPreference}" rows="2" placeholder="Ví dụ: Ăn chay, Keto..."></textarea>
            </div>
        </fieldset>

        <!-- Khu vực 3: Đồng ý thu thập dữ liệu (BR-08) -->
        <div class="consent-group">
            <input type="checkbox" id="hasConsent" th:field="*{hasConsent}">
            <label for="hasConsent" class="consent-label">
                Tôi đồng ý cho phép Xoai Aura Retreat thu thập và xử lý các thông tin y tế trên nhằm mục đích cải thiện dịch vụ chăm sóc sức khỏe cá nhân. Tôi hiểu rằng dữ liệu này được mã hóa và bảo mật nghiêm ngặt.
            </label>
        </div>

        <button type="submit" class="btn-submit">Lưu Hồ Sơ</button>
    </form>
</div>
</body>
</html>
```

---

### 7. `profile.css` (Tách module độc lập)

**Đường dẫn dự kiến:** `src/main/resources/static/css/auth/profile.css`

```css
body { font-family: 'Segoe UI', Tahoma, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
.profile-container { max-width: 800px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
h2 { color: #2c3e50; text-align: center; }
.alert { padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; }
.alert-success { color: #155724; background-color: #d4edda; border-color: #c3e6cb; }
.alert-danger { color: #721c24; background-color: #f8d7da; border-color: #f5c6cb; }
fieldset { border: 1px solid #e2e8f0; border-radius: 5px; padding: 20px; margin-bottom: 25px; }
legend { font-weight: bold; color: #34495e; padding: 0 10px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 600; }
.form-group textarea { width: 100%; padding: 10px; border: 1px solid #ced4da; border-radius: 4px; resize: vertical; }
.consent-group { display: flex; align-items: flex-start; gap: 10px; background: #fdfbfb; padding: 15px; border-left: 4px solid #e74c3c; margin-bottom: 20px; }
.consent-label { font-size: 0.9em; color: #555; line-height: 1.4; }
.btn-submit { display: block; width: 100%; padding: 12px; background-color: #27ae60; color: white; border: none; border-radius: 4px; font-size: 1.1em; cursor: pointer; transition: 0.3s; }
.btn-submit:hover { background-color: #219a52; }
```
