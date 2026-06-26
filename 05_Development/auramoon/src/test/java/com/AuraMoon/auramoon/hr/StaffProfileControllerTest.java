package com.AuraMoon.auramoon.hr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StaffProfileController.class)
public class StaffProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void shouldReturn403ForUnauthorizedRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/manager/staff/profile/1"))
                .andExpect(status().isForbidden());
    }
}
