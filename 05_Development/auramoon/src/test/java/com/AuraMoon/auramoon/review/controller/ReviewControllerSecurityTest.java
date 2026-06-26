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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldReturn403WhenManagerTriesToHideReview() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/reviews/1/hide").with(csrf()))
                .andExpect(status().isForbidden());
                
        verify(reviewService, never()).hideReview(anyLong());
    }
}
