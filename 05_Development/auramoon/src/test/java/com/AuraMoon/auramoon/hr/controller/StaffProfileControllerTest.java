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
@AutoConfigureMockMvc
public class StaffProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStaffProfileAggregator aggregator;

    @Test
    public void shouldRenderStaffProfileForManager() throws Exception {
        FullStaffProfileDTO mockProfile = new FullStaffProfileDTO();
        mockProfile.setRecentActivitiesPage(org.springframework.data.domain.Page.empty());
        when(aggregator.getAggregatedProfile(eq(1L), anyInt(), anyInt(), any())).thenReturn(mockProfile);

        com.AuraMoon.auramoon.auth.entity.User managerUser = com.AuraMoon.auramoon.auth.entity.User.builder()
                .id(99).fullName("Manager").role(new com.AuraMoon.auramoon.auth.entity.Role(2, "MANAGER")).build();
        com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse mockManagerDetails = new com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse(managerUser);

        mockMvc.perform(get("/manager/staff/profile/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockManagerDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/staff-profile"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    public void shouldDenyAccessToReceptionist() throws Exception {
        com.AuraMoon.auramoon.auth.entity.User recUser = com.AuraMoon.auramoon.auth.entity.User.builder()
                .id(99).fullName("Receptionist").role(new com.AuraMoon.auramoon.auth.entity.Role(3, "RECEPTIONIST")).build();
        com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse mockRecDetails = new com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse(recUser);

        mockMvc.perform(get("/manager/staff/profile/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockRecDetails)))
                .andExpect(status().isForbidden());
    }
}

