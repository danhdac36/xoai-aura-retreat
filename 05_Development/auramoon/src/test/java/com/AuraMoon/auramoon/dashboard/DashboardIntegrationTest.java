package com.AuraMoon.auramoon.dashboard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // DASH-TC-INT-001
    @Test
    @DisplayName("DASH-TC-INT-001: Full Dashboard Flow (E2E)")
    void testFullDashboardFlow() throws Exception {
        // Expected to fail initially (RED)
        mockMvc.perform(get("/manager/dashboard")
                .param("startDate", "2026-06-01")
                .param("endDate", "2026-06-30"))
               .andExpect(status().isOk())
               .andExpect(view().name("manager/dashboard"));
    }
}
