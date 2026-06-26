package com.AuraMoon.auramoon.audit.repository;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.fail;

public class AuditLogRepositoryTest {

    @Test
    public void shouldNotContainAnyDeleteMethodUsingReflection() {
        // Arrange
        Class<?> clazz = IAuditLogRepository.class;
        
        // Act
        Method[] methods = clazz.getMethods();
        
        // Assert
        for (Method method : methods) {
            String name = method.getName().toLowerCase();
            if (name.contains("delete") || name.contains("remove")) {
                fail("Repository contains forbidden method: " + method.getName() + " (Append-only violated!)");
            }
        }
    }
}
