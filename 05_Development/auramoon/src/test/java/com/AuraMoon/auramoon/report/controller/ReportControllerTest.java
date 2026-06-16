package com.AuraMoon.auramoon.report.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.AuraMoon.auramoon.report.service.ReportService;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // RPT-TC-006
    @Test
    @org.junit.jupiter.api.Disabled("Role check is handled by Module 1 UC")
    @DisplayName("RPT-TC-006: Unauthorized Access Attempt")
    void testUnauthorizedAccessAttempt() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("GUEST"); // Simulate GUEST
        mockUser.setRole(mockRole);

        mockMvc.perform(get("/manager/report")
                .sessionAttr("currentUser", mockUser))
                .andExpect(status().isForbidden());
    }

    // RPT-TC-007
    @Test
    @DisplayName("RPT-TC-007: Verify Download Response Headers")
    void testVerifyDownloadResponseHeaders() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("MANAGER");
        mockUser.setRole(mockRole);

        mockMvc.perform(get("/manager/report/export")
                .sessionAttr("currentUser", mockUser)
                .param("startDate", "2026-06-01")
                .param("endDate", "2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @MockitoBean
    private ReportService reportService;

    // RPT-TC-ERR-001
    @Test
    @DisplayName("RPT-TC-ERR-001: Controller Exception Handling")
    void testControllerExceptionHandling() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole2 = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole2.setRoleName("MANAGER");
        mockUser.setRole(mockRole2);

        when(reportService.generateReportData(any(), any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/manager/report")
                .sessionAttr("currentUser", mockUser))
                .andExpect(status().isInternalServerError());
    }
}
