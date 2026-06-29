package com.AuraMoon.auramoon.auth.service.impl;

import com.AuraMoon.auramoon.auth.dto.AccountDTO;
import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.service.AccountService;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createAccount(AccountDTO input, Integer actorId) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        Role role = roleRepository.findById(input.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chức vụ hợp lệ"));

        User user = new User();
        user.setEmail(input.getEmail());
        user.setPasswordHash(passwordEncoder.encode("123456!"));
        user.setFullName(input.getFullName());
        user.setPhone(input.getPhone());
        user.setIdentifyCode(input.getIdentifyCode());
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setIsDelete(false);
        user = userRepository.save(user);

        if ("THERAPIST".equals(role.getRoleName())) {
            Therapist therapist = new Therapist();
            therapist.setId(user.getId());
            therapist.setTherapistCode("TH" + String.format("%04d", user.getId()));
            therapist.setStatus("ACTIVE");
            therapistRepository.save(therapist);
        }

        AuditLog log = new AuditLog();
        log.setActionType("CREATE_ACCOUNT");
        log.setActorId(actorId);
        log.setTargetId(user.getId());
        log.setDetails("Đã tạo tài khoản mới: " + input.getEmail());
        log.setTimestamp(new Date());
        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public void deactivateAccount(Integer userId, Integer actorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        if ("ADMIN".equals(user.getRole().getRoleName())) {
            throw new IllegalArgumentException("Không thể vô hiệu hóa tài khoản ADMIN");
        }

        user.setStatus("INACTIVE");
        user.setIsDelete(true);
        userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setActionType("DEACTIVATE_ACCOUNT");
        log.setActorId(actorId);
        log.setTargetId(user.getId());
        log.setDetails("Vô hiệu hóa tài khoản: " + user.getEmail());
        log.setTimestamp(new Date());
        auditLogRepository.save(log);
    }

    @Override
    public AccountDTO getAccountById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        AccountDTO dto = new AccountDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIdentifyCode(user.getIdentifyCode());
        dto.setRoleId(user.getRole().getId());
        return dto;
    }

    @Override
    @Transactional
    public void updateAccount(Integer id, AccountDTO input, Integer actorId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        Role role = roleRepository.findById(input.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chức vụ hợp lệ"));

        // Update phone number
        user.setPhone(input.getPhone());

        // Handle role change side-effects
        if (!user.getRole().getId().equals(role.getId())) {
            // If changing to THERAPIST, ensure they exist in Therapist table
            if ("THERAPIST".equals(role.getRoleName())) {
                if (!therapistRepository.existsById(user.getId())) {
                    Therapist therapist = new Therapist();
                    therapist.setId(user.getId());
                    therapist.setTherapistCode("TH" + String.format("%04d", user.getId()));
                    therapist.setStatus("ACTIVE");
                    therapistRepository.save(therapist);
                }
            }
            user.setRole(role);
        }

        userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setActionType("UPDATE_ACCOUNT");
        log.setActorId(actorId);
        log.setTargetId(user.getId());
        log.setDetails("Cập nhật thông tin tài khoản: " + user.getEmail());
        log.setTimestamp(new Date());
        auditLogRepository.save(log);
    }

    @Override
    public List<User> getAllAccounts(String email, boolean isDeleted) {
        if (email != null && !email.trim().isEmpty()) {
            return isDeleted ? userRepository.findByIsDeleteTrueAndEmailContainingIgnoreCase(email.trim())
                             : userRepository.findByIsDeleteFalseAndEmailContainingIgnoreCase(email.trim());
        }
        return isDeleted ? userRepository.findByIsDeleteTrue() : userRepository.findByIsDeleteFalse();
    }

    @Override
    @Transactional
    public void restoreAccount(Integer userId, Integer actorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        user.setIsDelete(false);
        user.setStatus("ACTIVE");
        userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setActionType("RESTORE_ACCOUNT");
        log.setActorId(actorId);
        log.setTargetId(user.getId());
        log.setDetails("Khôi phục hoạt động tài khoản: " + user.getEmail());
        log.setTimestamp(new Date());
        auditLogRepository.save(log);
    }
}
