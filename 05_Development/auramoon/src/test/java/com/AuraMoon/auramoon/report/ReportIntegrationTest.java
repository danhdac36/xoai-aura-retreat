package com.AuraMoon.auramoon.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.AuraMoon.auramoon.auth.entity.User;

@SpringBootTest
@AutoConfigureMockMvc
class ReportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // RPT-TC-INT-001
    @Test
    @DisplayName("RPT-TC-INT-001: Full Export Flow (E2E)")
    void testFullExportFlow() throws Exception {
        User mockUser = new User();
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("MANAGER");
        mockUser.setRole(mockRole);

        mockMvc.perform(get("/manager/report/export")
                .sessionAttr("currentUser", mockUser)
                .param("startDate", "2026-06-01")
                .param("endDate", "2026-06-30")
                .param("reportType", "ALL"))
               .andExpect(status().isOk());
        // EXPECT RED: The bytes won't be parsable to XSSFWorkbook since it's null currently
    }
}
