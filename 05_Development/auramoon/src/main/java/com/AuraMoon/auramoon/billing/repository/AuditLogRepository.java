package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AuditLog a WHERE a.actionType = :actionType AND CAST(a.timestamp AS date) = :date")
    boolean existsByActionTypeAndTimestampDate(@Param("actionType") String actionType, @Param("date") LocalDate date);

    List<AuditLog> findTop5ByActionTypeStartingWithOrderByTimestampDesc(String actionTypePrefix);

    // Dùng cho test mock
    default void saveAuditLog(String actionType, Integer actorId, String details) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setActorId(actorId);
        log.setDetails(details);
        log.setTimestamp(new java.util.Date());
        this.save(log);
    }
}
