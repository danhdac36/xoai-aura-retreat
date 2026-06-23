package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.dto.PersonalProfileDto;
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

    @Override
    public PersonalProfileDto getPersonalProfile(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        return PersonalProfileDto.fromEntity(user);
    }

    @Override
    @Transactional
    public void savePersonalProfile(PersonalProfileDto dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setIdentifyCode(dto.getIdentifyCode());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);

        AuditLog log = AuditLog.builder()
                .actionType("UPDATE_PERSONAL_PROFILE")
                .actorId(userId)
                .details("Guest updated personal profile")
                .timestamp(new Date())
                .build();
        auditLogRepository.save(log);
    }
}
