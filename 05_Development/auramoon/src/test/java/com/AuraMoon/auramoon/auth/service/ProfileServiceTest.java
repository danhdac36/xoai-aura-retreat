package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.entity.Consent;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.service.impl.ProfileServiceImpl;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import com.AuraMoon.auramoon.fnb.repository.DietaryProfileRepository;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private PhysicalHealthProfileRepository physicalHealthProfileRepository;

    @Mock
    private DietaryProfileRepository dietaryProfileRepository;

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private AesDataEncryptor encryptor;

    @BeforeEach
    public void setUp() {
        encryptor = new AesDataEncryptor();
    }

    @Test
    @DisplayName("UC02 - Lấy hồ sơ y tế & dinh dưỡng khi chưa có thông tin")
    public void getSensitiveProfile_noExistingProfile_returnsEmptyFieldsInDto() {
        // Arrange
        Integer userId = 1;
        when(physicalHealthProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(dietaryProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        SensitiveProfileDto result = profileService.getSensitiveProfile(userId);

        // Assert
        assertNotNull(result);
        assertNull(result.getMedicalConditions());
        assertNull(result.getInjuries());
        assertNull(result.getFoodAllergies());
        assertNull(result.getDietaryPreference());
        assertFalse(result.isHasConsent()); // BR-08
    }

    @Test
    @DisplayName("UC02 - Lấy hồ sơ y tế & dinh dưỡng và giải mã thành công")
    public void getSensitiveProfile_existingProfile_returnsDecryptedFieldsInDto() {
        // Arrange
        Integer userId = 1;
        
        String rawMed = "Tiểu đường";
        String rawInj = "Đau khớp gối";
        String rawAllergy = "Hải sản";
        String rawPref = "Ăn chay";

        PhysicalHealthProfile phys = PhysicalHealthProfile.builder()
                .userId(userId)
                .medicalConditions(encryptor.convertToDatabaseColumn(rawMed))
                .injuries(encryptor.convertToDatabaseColumn(rawInj))
                .build();

        DietaryProfile diet = DietaryProfile.builder()
                .userId(userId)
                .foodAllergies(encryptor.convertToDatabaseColumn(rawAllergy))
                .dietaryPreference(encryptor.convertToDatabaseColumn(rawPref))
                .build();

        when(physicalHealthProfileRepository.findByUserId(userId)).thenReturn(Optional.of(phys));
        when(dietaryProfileRepository.findByUserId(userId)).thenReturn(Optional.of(diet));

        // Act
        SensitiveProfileDto result = profileService.getSensitiveProfile(userId);

        // Assert
        assertNotNull(result);
        assertEquals(rawMed, result.getMedicalConditions());
        assertEquals(rawInj, result.getInjuries());
        assertEquals(rawAllergy, result.getFoodAllergies());
        assertEquals(rawPref, result.getDietaryPreference());
        assertFalse(result.isHasConsent()); // BR-08
    }

    @Test
    @DisplayName("UC02 - Lưu hồ sơ y tế & dinh dưỡng mới thành công với mã hóa, lưu Consent và ghi Audit Log")
    public void saveSensitiveProfile_newProfile_savesWithEncryptionAndLogs() {
        // Arrange
        Integer userId = 1;
        User mockUser = new User();
        mockUser.setId(userId);

        SensitiveProfileDto inputDto = new SensitiveProfileDto();
        inputDto.setMedicalConditions("Tiểu đường");
        inputDto.setInjuries("Đau vai");
        inputDto.setFoodAllergies("Đậu phộng");
        inputDto.setDietaryPreference("Keto");
        inputDto.setHasConsent(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(physicalHealthProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(dietaryProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        profileService.saveSensitiveProfile(inputDto, userId);

        // Assert
        // Verify PhysicalHealthProfile save
        ArgumentCaptor<PhysicalHealthProfile> physCaptor = ArgumentCaptor.forClass(PhysicalHealthProfile.class);
        verify(physicalHealthProfileRepository, times(1)).save(physCaptor.capture());
        PhysicalHealthProfile savedPhys = physCaptor.getValue();
        assertEquals(userId, savedPhys.getUserId());
        assertNotEquals("Tiểu đường", savedPhys.getMedicalConditions());
        assertEquals("Tiểu đường", encryptor.convertToEntityAttribute(savedPhys.getMedicalConditions()));
        assertEquals("Đau vai", encryptor.convertToEntityAttribute(savedPhys.getInjuries()));
        assertNotNull(savedPhys.getUpdatedAt());

        // Verify DietaryProfile save
        ArgumentCaptor<DietaryProfile> dietCaptor = ArgumentCaptor.forClass(DietaryProfile.class);
        verify(dietaryProfileRepository, times(1)).save(dietCaptor.capture());
        DietaryProfile savedDiet = dietCaptor.getValue();
        assertEquals(userId, savedDiet.getUserId());
        assertNotEquals("Đậu phộng", savedDiet.getFoodAllergies());
        assertEquals("Đậu phộng", encryptor.convertToEntityAttribute(savedDiet.getFoodAllergies()));
        assertEquals("Keto", encryptor.convertToEntityAttribute(savedDiet.getDietaryPreference()));
        assertNotNull(savedDiet.getUpdatedAt());

        // Verify Consent save
        ArgumentCaptor<Consent> consentCaptor = ArgumentCaptor.forClass(Consent.class);
        verify(consentRepository, times(1)).save(consentCaptor.capture());
        Consent savedConsent = consentCaptor.getValue();
        assertEquals(mockUser, savedConsent.getUser());
        assertTrue(savedConsent.getConsentStatus());
        assertEquals("1.0", savedConsent.getConsentVersion());
        assertNotNull(savedConsent.getUpdatedAt());

        // Verify AuditLog save (BR-15)
        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(auditCaptor.capture());
        AuditLog savedAudit = auditCaptor.getValue();
        assertEquals("UPDATE_HEALTH_PROFILE", savedAudit.getActionType());
        assertEquals(userId, savedAudit.getActorId());
        assertNotNull(savedAudit.getTimestamp());
    }
}
