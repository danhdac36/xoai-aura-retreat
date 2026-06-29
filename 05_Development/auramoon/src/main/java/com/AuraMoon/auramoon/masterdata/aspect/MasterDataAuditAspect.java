package com.AuraMoon.auramoon.masterdata.aspect;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.billing.entity.AuditLog;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
@RequiredArgsConstructor
public class MasterDataAuditAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(
            pointcut = "execution(* com.AuraMoon.auramoon.masterdata.service.*MasterService.create*(..))",
            returning = "result"
    )
    public void logCreate(JoinPoint joinPoint, Object result) {
        if (result != null) {
            String className = result.getClass().getSimpleName();
            Integer id = getObjectId(result);
            writeLog("CREATE_" + className.toUpperCase(), id, 
                    String.format("Created %s: %s", className, result.toString()));
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.AuraMoon.auramoon.masterdata.service.*MasterService.update*(..))",
            returning = "result"
    )
    public void logUpdate(JoinPoint joinPoint, Object result) {
        if (result != null) {
            String className = result.getClass().getSimpleName();
            Integer id = getObjectId(result);
            writeLog("UPDATE_" + className.toUpperCase(), id, 
                    String.format("Updated %s: %s", className, result.toString()));
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.AuraMoon.auramoon.masterdata.service.*MasterService.delete*(..))"
    )
    public void logDelete(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String category = methodName.substring(6).toUpperCase(); // e.g. RETREATPACKAGE
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Integer) {
            Integer id = (Integer) args[0];
            writeLog("DELETE_" + category, id, 
                    String.format("Soft Deleted %s with ID: %d", category, id));
        }
    }

    private Integer getObjectId(Object obj) {
        if (obj instanceof RetreatPackage) return ((RetreatPackage) obj).getId();
        if (obj instanceof TreatmentService) return ((TreatmentService) obj).getId();
        if (obj instanceof VillaType) return ((VillaType) obj).getId();
        if (obj instanceof Villa) return ((Villa) obj).getId();
        if (obj instanceof YogaClass) return ((YogaClass) obj).getId();
        if (obj instanceof MenuItem) return ((MenuItem) obj).getId();
        return null;
    }

    private void writeLog(String actionType, Integer targetId, String details) {
        Integer actorId = 1; // Fallback default admin actor ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsResponse) {
            UserDetailsResponse user = (UserDetailsResponse) auth.getPrincipal();
            actorId = user.getId();
        }

        AuditLog log = AuditLog.builder()
                .actionType(actionType)
                .actorId(actorId)
                .targetId(targetId)
                .details(details)
                .timestamp(new Date())
                .build();

        auditLogRepository.save(log);
    }
}
