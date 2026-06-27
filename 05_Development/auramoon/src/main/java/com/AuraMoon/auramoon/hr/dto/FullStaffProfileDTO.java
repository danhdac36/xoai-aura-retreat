package com.AuraMoon.auramoon.hr.dto;

import lombok.Data;
import java.util.List;
import com.AuraMoon.auramoon.billing.dto.AuditLogDTO;

@Data
public class FullStaffProfileDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String roleName;
    private String therapistCode;
    
    // New fields from EDS
    private String gender;
    private java.time.LocalDate dateOfBirth;
    private String avatar;
    private String status;
    private String therapistStatus;
    private long completedSessions;
    
    // Explicitly null fields to satisfy redaction test
    private String password;
    private String passwordHash;
    
    private List<AuditLogDTO> recentActivities;
}
