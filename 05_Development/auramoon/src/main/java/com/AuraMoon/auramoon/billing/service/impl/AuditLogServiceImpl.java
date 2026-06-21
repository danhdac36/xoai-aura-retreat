package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.billing.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void logActivity(String actionType, Integer actorId, Integer targetId) {
        AuditLog log = AuditLog.builder()
                .actionType(actionType)
                .actorId(actorId)
                .targetId(targetId)
                .timestamp(new Date())
                .build();
        auditLogRepository.save(log);
    }
}
