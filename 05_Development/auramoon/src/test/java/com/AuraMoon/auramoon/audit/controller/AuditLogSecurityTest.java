package com.AuraMoon.auramoon.audit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditLogController.class)
public class AuditLogSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldBlockManagerFromAccessingAuditLogs() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/audit"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(get("/admin/audit/details/1"))
                .andExpect(status().isForbidden());
    }
}
