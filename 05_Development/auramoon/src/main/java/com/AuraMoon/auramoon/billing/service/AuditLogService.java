package com.AuraMoon.auramoon.billing.service;

public interface AuditLogService {
    void logActivity(String actionType, Integer actorId, Integer targetId);
}
