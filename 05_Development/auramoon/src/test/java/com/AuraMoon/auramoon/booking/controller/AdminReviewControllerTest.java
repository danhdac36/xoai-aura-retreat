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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AdminReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IManagerReviewService reviewService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldHideReviewForAdmin() throws Exception {
        doNothing().when(reviewService).hideReview(1);

        mockMvc.perform(post("/admin/reviews/1/hide").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/reviews"))
                .andExpect(flash().attributeExists("message"));

        verify(reviewService, times(1)).hideReview(1);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldDenyHideReviewForManager() throws Exception {
        mockMvc.perform(post("/admin/reviews/1/hide").with(csrf()))
                .andExpect(status().isForbidden());

        verify(reviewService, never()).hideReview(anyInt());
    }
}

