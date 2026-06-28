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
@AutoConfigureMockMvc
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuditLogService auditLogService;

    @Test
    public void shouldRenderDashboardForAdmin() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User adminUser = com.AuraMoon.auramoon.auth.entity.User.builder()
                .id(1).fullName("Admin").role(new com.AuraMoon.auramoon.auth.entity.Role(1, "ADMIN")).build();
        com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse mockAdminDetails = new com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse(adminUser);

        mockMvc.perform(get("/admin/audit")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockAdminDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/audit"));
    }

    @Test
    public void shouldDenyAccessToManager() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User managerUser = com.AuraMoon.auramoon.auth.entity.User.builder()
                .id(2).fullName("Manager").role(new com.AuraMoon.auramoon.auth.entity.Role(2, "MANAGER")).build();
        com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse mockManagerDetails = new com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse(managerUser);

        mockMvc.perform(get("/admin/audit")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockManagerDetails)))
                .andExpect(status().isForbidden());
    }
}

