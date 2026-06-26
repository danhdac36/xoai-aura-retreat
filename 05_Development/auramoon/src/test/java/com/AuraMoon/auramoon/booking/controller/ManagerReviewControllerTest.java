package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.service.IManagerReviewService;
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
public class ManagerReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IManagerReviewService reviewService;

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    public void shouldRenderDashboardForManager() throws Exception {
        when(reviewService.getMetrics()).thenReturn(null);
        when(reviewService.getVisibleReviews()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/manager/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/reviews"))
                .andExpect(model().attributeExists("metrics"))
                .andExpect(model().attributeExists("reviews"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void shouldDenyAccessToReceptionist() throws Exception {
        mockMvc.perform(get("/manager/reviews"))
                .andExpect(status().isForbidden());
    }
}

