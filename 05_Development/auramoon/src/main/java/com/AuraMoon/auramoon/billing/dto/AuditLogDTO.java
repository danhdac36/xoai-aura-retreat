package com.AuraMoon.auramoon.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLogDTO {
    private Integer logId;
    private String actionType;
    private Integer actorId;
    private String actorName;
    private Integer targetId;
    private String details;
    private String timestamp;
}
