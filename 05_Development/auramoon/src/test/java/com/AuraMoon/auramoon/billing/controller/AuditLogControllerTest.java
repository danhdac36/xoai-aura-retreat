package com.AuraMoon.auramoon.billing.controller;

import com.AuraMoon.auramoon.billing.service.IAuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuditLogService auditLogService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRenderDashboardForAdmin() throws Exception {
        mockMvc.perform(get("/admin/audit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/audit"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldDenyAccessToManager() throws Exception {
        mockMvc.perform(get("/admin/audit"))
                .andExpect(status().isForbidden());
    }
}

