package com.AuraMoon.auramoon.billing.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    public void shouldThrowExceptionOnDeleteById() {
        assertThatThrownBy(() -> auditLogRepository.deleteById(1))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("append-only");
    }

    @Test
    public void shouldThrowExceptionOnDeleteAll() {
        assertThatThrownBy(() -> auditLogRepository.deleteAll())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("append-only");
    }
}

