package com.AuraMoon.auramoon.hr.controller;

import com.AuraMoon.auramoon.hr.service.IStaffProfileAggregator;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class StaffProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStaffProfileAggregator aggregator;

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    public void shouldRenderStaffProfileForManager() throws Exception {
        FullStaffProfileDTO mockProfile = new FullStaffProfileDTO();
        when(aggregator.getAggregatedProfile(1L)).thenReturn(mockProfile);

        mockMvc.perform(get("/manager/staff/profile/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/staff-profile"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void shouldDenyAccessToReceptionist() throws Exception {
        mockMvc.perform(get("/manager/staff/profile/1"))
                .andExpect(status().isForbidden());
    }
}

