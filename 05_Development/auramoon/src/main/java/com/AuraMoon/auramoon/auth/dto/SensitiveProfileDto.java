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
