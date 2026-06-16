package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;

public interface INightAuditService {
    NightAuditResultDTO executeAudit(String triggerType, Integer actorId);
}
