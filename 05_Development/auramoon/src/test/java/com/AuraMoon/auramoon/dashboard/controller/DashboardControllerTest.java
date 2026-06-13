package com.AuraMoon.auramoon.dashboard.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // DASH-TC-006
    @Test
    @DisplayName("DASH-TC-006: Unauthorized Access Attempt")
    void testUnauthorizedAccessAttempt() throws Exception {
        // TDD RED Phase: Endpoint should return 403 Forbidden
        mockMvc.perform(get("/manager/dashboard"))
                .andExpect(status().isForbidden());
    }
}
