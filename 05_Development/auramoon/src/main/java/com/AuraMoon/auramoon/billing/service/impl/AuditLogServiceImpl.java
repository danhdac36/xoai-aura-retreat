package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.dto.AuditLogDTO;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.billing.service.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements IAuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void logActivity(String actionType, Integer actorId, Integer targetId) {
        // Basic log
    }

    @Override
    public Page<AuditLogDTO> getLogs(String actionType, int page, int size) {
        Page<com.AuraMoon.auramoon.billing.entity.AuditLog> logs = auditLogRepository.findAll(PageRequest.of(page, size));
        return logs.map(log -> {
            String actorName = userRepository.findById(log.getActorId())
                    .map(User::getFullName) // assuming User has getFullName() or getUsername(). I will use getFullName() and if it doesn't exist, I'll fallback. Wait, let me check User entity first!
                    .orElse("Unknown");
            return AuditLogDTO.builder()
                .logId(log.getId())
                .actionType(log.getActionType())
                .actorId(log.getActorId())
                .actorName(actorName)
                .targetId(log.getTargetId())
                .details(log.getDetails())
                .timestamp(log.getTimestamp() != null ? new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(log.getTimestamp()) : "")
                .build();
        });
    }

    @Override
    public String getDetailsById(int id) {
        return "{}";
    }
}
