package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
}
