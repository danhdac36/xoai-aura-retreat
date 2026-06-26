package com.AuraMoon.auramoon.yoga.controller;

import com.AuraMoon.auramoon.yoga.dto.YogaManagerDto.*;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import com.AuraMoon.auramoon.yoga.service.IYogaManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class YogaManagerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IYogaManagerService yogaManagerService;

    @InjectMocks
    private YogaManagerController yogaManagerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(yogaManagerController).build();
    }

    // --- YOGA_CLASS MVC TESTS ---

    @Test
    void showClassesPage_success() throws Exception {
        YogaClassResponse response = YogaClassResponse.builder()
                .classId(1)
                .className("Hatha Yoga")
                .durationMinutes(60)
                .build();
        when(yogaManagerService.getAllActiveClasses()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/manager/yoga/classes"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/yoga-classes"))
                .andExpect(model().attributeExists("classes"))
                .andExpect(model().attributeExists("classRequest"));
    }

    @Test
    void createClass_success() throws Exception {
        YogaClassResponse response = YogaClassResponse.builder()
                .classId(1)
                .className("Vinyasa Flow")
                .durationMinutes(75)
                .build();
        when(yogaManagerService.createClass(any(YogaClassRequest.class))).thenReturn(response);

        mockMvc.perform(post("/manager/yoga/classes/create")
                .param("className", "Vinyasa Flow")
                .param("durationMinutes", "75")
                .param("description", "Flow class")
                .param("imageUrl", "/images/vinyasa.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/classes"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void createClass_validationError() throws Exception {
        mockMvc.perform(post("/manager/yoga/classes/create")
                .param("className", "") // Blank name
                .param("durationMinutes", "5")) // < 15 mins
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/classes"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void updateClass_success() throws Exception {
        YogaClassResponse response = YogaClassResponse.builder()
                .classId(1)
                .className("Hatha Updated")
                .durationMinutes(60)
                .build();
        when(yogaManagerService.updateClass(eq(1), any(YogaClassRequest.class))).thenReturn(response);

        mockMvc.perform(post("/manager/yoga/classes/edit/1")
                .param("className", "Hatha Updated")
                .param("durationMinutes", "60"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/classes"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteClass_success() throws Exception {
        doNothing().when(yogaManagerService).deleteClass(1);

        mockMvc.perform(post("/manager/yoga/classes/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/classes"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // --- YOGA_SCHEDULE MVC TESTS ---

    @Test
    void showSchedulesPage_success() throws Exception {
        YogaScheduleResponse schedule = YogaScheduleResponse.builder()
                .scheduleId(100)
                .className("Hatha Yoga")
                .location("Yoga Room")
                .build();
        when(yogaManagerService.getSchedulesByDate(any(LocalDate.class))).thenReturn(Collections.singletonList(schedule));
        when(yogaManagerService.getAllActiveClasses()).thenReturn(Collections.emptyList());
        when(yogaManagerService.getAllActiveInstructors()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/manager/yoga/schedules").param("date", "2026-06-28"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/yoga-schedules"))
                .andExpect(model().attributeExists("schedules"))
                .andExpect(model().attributeExists("classes"))
                .andExpect(model().attributeExists("instructors"))
                .andExpect(model().attribute("selectedDate", LocalDate.parse("2026-06-28")));
    }

    @Test
    void createSchedule_success() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(8).withMinute(0);
        LocalDateTime end = start.plusMinutes(60);

        YogaScheduleResponse response = YogaScheduleResponse.builder()
                .scheduleId(101)
                .className("Hatha Yoga")
                .build();
        when(yogaManagerService.createSchedule(any(YogaScheduleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/manager/yoga/schedules/create")
                .param("classId", "1")
                .param("instructorId", "10")
                .param("location", "Yoga Studio")
                .param("startTime", start.toString())
                .param("endTime", end.toString())
                .param("maxCapacity", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/schedules"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void createSchedule_validationError() throws Exception {
        mockMvc.perform(post("/manager/yoga/schedules/create")
                .param("classId", "")
                .param("location", "")
                .param("maxCapacity", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/schedules"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void deleteSchedule_success() throws Exception {
        doNothing().when(yogaManagerService).deleteSchedule(100);

        mockMvc.perform(post("/manager/yoga/schedules/delete/100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/yoga/schedules"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
