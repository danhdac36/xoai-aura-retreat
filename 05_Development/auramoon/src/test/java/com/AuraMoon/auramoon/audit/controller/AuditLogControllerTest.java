package com.AuraMoon.auramoon.audit.controller;

import com.AuraMoon.auramoon.audit.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRenderDashboardForAdmin() throws Exception {
        // Arrange
        when(auditLogService.getLogs()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/admin/audit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/audit"))
                .andExpect(model().attributeExists("logs"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnJsonForLogDetails() throws Exception {
        // Arrange
        when(auditLogService.getDetailsById(1L)).thenReturn("{\"amount\": 100}");

        // Act & Assert
        mockMvc.perform(get("/admin/audit/details/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.amount").value(100));
    }
}
