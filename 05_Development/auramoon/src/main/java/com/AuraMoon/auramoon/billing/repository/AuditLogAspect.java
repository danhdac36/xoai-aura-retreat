package com.AuraMoon.auramoon.billing.repository;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    @Before("execution(* com.AuraMoon.auramoon.billing.repository.AuditLogRepository.delete*(..))")
    public void preventDelete() {
        throw new UnsupportedOperationException("Audit logs are append-only and cannot be deleted");
    }
}
