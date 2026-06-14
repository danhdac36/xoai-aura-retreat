package com.AuraMoon.auramoon.dashboard.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.AuraMoon.auramoon.dashboard.service.DashboardService;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // DASH-TC-006
    @Test
    @org.junit.jupiter.api.Disabled("Role check is handled by Module 1 UC")
    @DisplayName("DASH-TC-006: Unauthorized Access Attempt")
    void testUnauthorizedAccessAttempt() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("GUEST"); // Simulate GUEST
        mockUser.setRole(mockRole);

        // TDD RED Phase: Endpoint should return 403 Forbidden
        mockMvc.perform(get("/manager/dashboard")
                .sessionAttr("currentUser", mockUser))
                .andExpect(status().isForbidden());
    }

    @MockitoBean
    private DashboardService dashboardService;

    // DASH-TC-ERR-001
    @Test
    @DisplayName("DASH-TC-ERR-001: Database Exception Handling")
    void testDatabaseExceptionHandling() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("MANAGER");
        mockUser.setRole(mockRole);

        when(dashboardService.getDashboardData(any(), any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/manager/dashboard")
                .sessionAttr("currentUser", mockUser))
                .andExpect(status().isInternalServerError());
    }
}
