package com.AuraMoon.auramoon.review.controller;

import com.AuraMoon.auramoon.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldRenderDashboardForManager() throws Exception {
        // Arrange
        // Assume ReviewMetricsDTO is returned
        when(reviewService.getDashboardMetrics()).thenReturn(null); 

        // Act & Assert
        mockMvc.perform(get("/admin/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reviews"))
                .andExpect(model().attributeExists("metrics"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirectAfterAdminHideReview() throws Exception {
        // Arrange
        doNothing().when(reviewService).hideReview(1L);

        // Act & Assert
        mockMvc.perform(post("/admin/reviews/1/hide").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reviews"))
                .andExpect(flash().attributeExists("message"));
                
        verify(reviewService, times(1)).hideReview(1L);
    }
}
