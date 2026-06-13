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

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // RPT-TC-006
    @Test
    @DisplayName("RPT-TC-006: Unauthorized Access Attempt")
    void testUnauthorizedAccessAttempt() throws Exception {
        mockMvc.perform(get("/manager/report"))
               .andExpect(status().isForbidden());
    }

    // RPT-TC-007
    @Test
    @DisplayName("RPT-TC-007: Verify Download Response Headers")
    void testVerifyDownloadResponseHeaders() throws Exception {
        mockMvc.perform(get("/manager/report/export")
                .param("startDate", "2026-06-01")
                .param("endDate", "2026-06-30"))
               .andExpect(status().isOk())
               .andExpect(header().exists("Content-Disposition"))
               .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }
}
