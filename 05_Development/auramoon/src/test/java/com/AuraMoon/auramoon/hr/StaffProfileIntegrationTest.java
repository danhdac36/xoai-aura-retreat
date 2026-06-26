package com.AuraMoon.auramoon.hr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StaffProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldExecuteFullFlowToGetTherapistProfile() throws Exception {
        // Note: Assumes seed data exists for user id 1
        // Act & Assert
        mockMvc.perform(get("/manager/staff/profile/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/staff-profile-details"))
                .andExpect(model().attributeExists("profile"));
    }
}
