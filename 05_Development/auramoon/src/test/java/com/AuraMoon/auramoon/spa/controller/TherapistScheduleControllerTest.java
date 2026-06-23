package com.AuraMoon.auramoon.spa.controller;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TherapistScheduleControllerTest {

        private MockMvc mockMvc;

        @Mock
        private TherapistScheduleService therapistScheduleService;

        @Mock
        private com.AuraMoon.auramoon.spa.repository.TherapistRepository therapistRepository;

        @InjectMocks
        private TherapistScheduleController therapistScheduleController;

        private MockHttpSession session;

        @BeforeEach
        public void setup() {
                com.AuraMoon.auramoon.auth.entity.User mockUser = new com.AuraMoon.auramoon.auth.entity.User();
        mockUser.setId(1);
        com.AuraMoon.auramoon.auth.entity.Role mockRole = new com.AuraMoon.auramoon.auth.entity.Role();
        mockRole.setRoleName("ROLE_USER");
        mockUser.setRole(mockRole);

        mockMvc = MockMvcBuilders.standaloneSetup(therapistScheduleController)
            .setCustomArgumentResolvers(new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                    return parameter.getParameterType().isAssignableFrom(com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse.class);
                }
                @Override
                public Object resolveArgument(org.springframework.core.MethodParameter parameter, org.springframework.web.method.support.ModelAndViewContainer mavContainer, org.springframework.web.context.request.NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                    return new com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse(mockUser);
                }
            })
            .build();
        session = new MockHttpSession();
        }

        @Test
        public void getDailySchedule_ValidTherapistWithSchedules_ReturnsViewAndModel() throws Exception {
                // Arrange
                String therapistCode = "TH01";
                session.setAttribute("therapistCode", therapistCode);
                String dateStr = "2026-06-15";
                LocalDate targetDate = LocalDate.parse(dateStr);

                List<TherapistScheduleDto> schedules = Arrays.asList(
                                TherapistScheduleDto.builder()
                                                .scheduleId(1)
                                                .serviceName("Massage")
                                                .roomName("Room 1")
                                                .startTime(LocalDateTime.of(2026, 6, 15, 9, 0))
                                                .build(),
                                TherapistScheduleDto.builder()
                                                .scheduleId(2)
                                                .serviceName("Facial")
                                                .roomName("Room 2")
                                                .startTime(LocalDateTime.of(2026, 6, 15, 11, 0))
                                                .build());

                when(therapistScheduleService.getDailySchedule(eq(therapistCode), eq(targetDate)))
                                .thenReturn(schedules);
                when(therapistRepository.findById(1)).thenReturn(java.util.Optional.of(com.AuraMoon.auramoon.spa.entity.Therapist.builder().therapistCode("TH01").build()));

                // Act & Assert
                mockMvc.perform(get("/therapist/schedules/daily")
                                .param("date", dateStr)
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("spa/therapist-schedule"))
                                .andExpect(model().attributeExists("schedules"))
                                .andExpect(model().attribute("schedules", hasSize(2)));
        }

        @Test
        public void getDailySchedule_ValidTherapistNoSchedules_ReturnsViewWithEmptyList() throws Exception {
                // Arrange
                String therapistCode = "TH02";
                session.setAttribute("therapistCode", therapistCode);
                String dateStr = "2026-06-15";
                LocalDate targetDate = LocalDate.parse(dateStr);

                when(therapistScheduleService.getDailySchedule(eq(therapistCode), eq(targetDate)))
                                .thenReturn(Collections.emptyList());
                when(therapistRepository.findById(1)).thenReturn(java.util.Optional.of(com.AuraMoon.auramoon.spa.entity.Therapist.builder().therapistCode("TH02").build()));

                // Act & Assert
                mockMvc.perform(get("/therapist/schedules/daily")
                                .param("date", dateStr)
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("spa/therapist-schedule"))
                                .andExpect(model().attributeExists("schedules"))
                                .andExpect(model().attribute("schedules", hasSize(0)));
        }

        @Test
        public void getDailySchedule_InvalidOrMissingTherapistCode_RedirectsToLogin() throws Exception {
                // Arrange
                // No therapistCode in session

                // Act & Assert
                mockMvc.perform(get("/therapist/schedules/daily")
                                .param("date", "2026-06-15")
                                .session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/auth/login"));
        }

        @Test
        public void updateStatus_ValidRequest_RedirectsWithSuccess() throws Exception {
                // Arrange
                Integer scheduleId = 1;
                String status = "Completed";
                String dateStr = "2026-06-15";
                String therapistCode = "TH01";
                session.setAttribute("therapistCode", therapistCode);

                doNothing().when(therapistScheduleService).updateSessionStatus(eq(scheduleId), eq(therapistCode),
                                eq(status));
                when(therapistRepository.findById(1)).thenReturn(java.util.Optional.of(com.AuraMoon.auramoon.spa.entity.Therapist.builder().therapistCode("TH01").build()));

                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/therapist/schedules/update-status")
                                .param("scheduleId", scheduleId.toString())
                                .param("status", status)
                                .param("date", dateStr)
                                .session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/therapist/schedules/daily?date=" + dateStr))
                                .andExpect(flash().attribute("successMessage", "Cập nhật trạng thái thành công"));
        }

        @Test
        public void updateStatus_ServiceThrowsException_RedirectsWithError() throws Exception {
                // Arrange
                Integer scheduleId = 1;
                String status = "Completed";
                String dateStr = "2026-06-15";
                String therapistCode = "TH01";
                session.setAttribute("therapistCode", therapistCode);

                doThrow(new com.AuraMoon.auramoon.spa.exception.SpaBusinessException("SPA-012",
                                "Trạng thái không hợp lệ"))
                                .when(therapistScheduleService)
                                .updateSessionStatus(eq(scheduleId), eq(therapistCode), eq(status));
                when(therapistRepository.findById(1)).thenReturn(java.util.Optional.of(com.AuraMoon.auramoon.spa.entity.Therapist.builder().therapistCode("TH01").build()));

                // Act & Assert
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/therapist/schedules/update-status")
                                .param("scheduleId", scheduleId.toString())
                                .param("status", status)
                                .param("date", dateStr)
                                .session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/therapist/schedules/daily?date=" + dateStr))
                                .andExpect(flash().attribute("errorMessage", "Trạng thái không hợp lệ"));
        }
}
