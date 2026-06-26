package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.AuditLogDTO;
import org.springframework.data.domain.Page;

public interface IAuditLogService extends AuditLogService {
    Page<AuditLogDTO> getLogs(String actionType, int page, int size);
    String getDetailsById(int id);
}
