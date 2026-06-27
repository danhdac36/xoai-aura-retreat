package com.AuraMoon.auramoon.hr.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffProfileAggregator implements IStaffProfileAggregator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private TreatmentBookingRepository treatmentBookingRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public FullStaffProfileDTO getAggregatedProfile(Long id) {
        Optional<User> userOpt = userRepository.findById(id.intValue());
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        FullStaffProfileDTO dto = new FullStaffProfileDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhone());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
            if ("THERAPIST".equals(user.getRole().getRoleName())) {
                Optional<Therapist> therapistOpt = therapistRepository.findById(user.getId());
                therapistOpt.ifPresent(therapist -> {
                    dto.setTherapistCode(therapist.getTherapistCode());
                    dto.setTherapistStatus(therapist.getStatus());
                    
                    Long sessions = treatmentBookingRepository.countByTherapistIdAndStatus(user.getId(), "COMPLETED");
                    dto.setCompletedSessions(sessions != null ? sessions : 0L);
                });
            }
        }
        
        java.util.List<com.AuraMoon.auramoon.billing.entity.AuditLog> logs = auditLogRepository.findTop10ByActorIdOrderByTimestampDesc(user.getId());
        dto.setRecentActivities(logs.stream().map(l -> com.AuraMoon.auramoon.billing.dto.AuditLogDTO.builder()
                .logId(l.getId())
                .actionType(l.getActionType())
                .actorId(l.getActorId())
                .targetId(l.getTargetId())
                .details(l.getDetails())
                .timestamp(l.getTimestamp() != null ? l.getTimestamp().toString() : "")
                .build()).collect(java.util.stream.Collectors.toList()));

        
        return dto;
    }
}
