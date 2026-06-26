package com.AuraMoon.auramoon.billing.service.impl;

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

    @Override
    public void logActivity(String actionType, Integer actorId, Integer targetId) {
        // Basic log
    }

    @Override
    public Page<AuditLogDTO> getLogs(String actionType, int page, int size) {
        Page<com.AuraMoon.auramoon.billing.entity.AuditLog> logs = auditLogRepository.findAll(PageRequest.of(page, size));
        return logs.map(log -> AuditLogDTO.builder()
                .logId(log.getId())
                .actionType(log.getActionType())
                .actorId(log.getActorId())
                .actorName("Admin_" + log.getActorId())
                .targetId(log.getTargetId())
                .details(log.getDetails())
                .timestamp(log.getTimestamp() != null ? log.getTimestamp().toString() : "")
                .build());
    }

    @Override
    public String getDetailsById(int id) {
        return "{}";
    }
}
