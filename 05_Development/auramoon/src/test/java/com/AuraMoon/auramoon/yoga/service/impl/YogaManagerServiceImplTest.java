package com.AuraMoon.auramoon.yoga.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.yoga.dto.YogaManagerDto.*;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.repository.YogaClassRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaInstructorRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YogaManagerServiceImplTest {

    @Mock
    private YogaClassRepository yogaClassRepository;

    @Mock
    private YogaScheduleRepository yogaScheduleRepository;

    @Mock
    private YogaInstructorRepository yogaInstructorRepository;

    @Mock
    private YogaRegistrationRepository yogaRegistrationRepository;

    @InjectMocks
    private YogaManagerServiceImpl yogaManagerService;

    private YogaClass hathaClass;
    private YogaInstructor activeInstructor;
    private YogaSchedule existingSchedule;

    @BeforeEach
    void setUp() {
        hathaClass = YogaClass.builder()
                .id(1)
                .className("Hatha Yoga")
                .description("Lớp học cơ bản")
                .durationMinutes(60)
                .imageUrl("/images/yoga/hatha.jpg")
                .isDelete(false)
                .build();

        User user = new User();
        user.setId(10);
        user.setFullName("Huấn Luyện Viên Test");

        activeInstructor = YogaInstructor.builder()
                .instructorId(10)
                .user(user)
                .instructorCode("YG0010")
                .status("AVAILABLE")
                .isDelete(false)
                .build();

        existingSchedule = YogaSchedule.builder()
                .id(100)
                .yogaClass(hathaClass)
                .instructor(activeInstructor)
                .location("Yoga Studio")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(60))
                .maxCapacity(15)
                .isDelete(false)
                .build();
    }

    // --- YOGA_CLASS TESTS ---

    @Test
    void createClass_success() {
        YogaClassRequest request = YogaClassRequest.builder()
                .className("Hatha Yoga")
                .description("Lớp học cơ bản")
                .durationMinutes(60)
                .imageUrl("/images/yoga/hatha.jpg")
                .build();

        when(yogaClassRepository.save(any(YogaClass.class))).thenReturn(hathaClass);

        YogaClassResponse response = yogaManagerService.createClass(request);

        assertNotNull(response);
        assertEquals(1, response.getClassId());
        assertEquals("Hatha Yoga", response.getClassName());
        assertEquals(60, response.getDurationMinutes());
    }

    @Test
    void createClass_invalidDuration_throwsException() {
        YogaClassRequest request = YogaClassRequest.builder()
                .className("Short Yoga")
                .durationMinutes(10) // Dưới 15 phút
                .build();

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createClass(request);
        });

        assertEquals("YOGA-001", ex.getErrorCode());
        assertTrue(ex.getMessage().contains("Thời lượng lớp học tối thiểu"));
    }

    @Test
    void updateClass_success() {
        YogaClassRequest request = YogaClassRequest.builder()
                .className("Hatha Yoga Updated")
                .description("Mô tả mới")
                .durationMinutes(75)
                .imageUrl("/images/yoga/hatha.jpg")
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaClassRepository.save(any(YogaClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YogaClassResponse response = yogaManagerService.updateClass(1, request);

        assertNotNull(response);
        assertEquals("Hatha Yoga Updated", response.getClassName());
        assertEquals(75, response.getDurationMinutes());
    }

    @Test
    void deleteClass_success() {
        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));

        yogaManagerService.deleteClass(1);

        assertTrue(hathaClass.getIsDelete());
        verify(yogaClassRepository, times(1)).save(hathaClass);
    }

    @Test
    void getAllActiveClasses_success() {
        when(yogaClassRepository.findByIsDeleteFalse()).thenReturn(Arrays.asList(hathaClass));

        List<YogaClassResponse> list = yogaManagerService.getAllActiveClasses();

        assertEquals(1, list.size());
        assertEquals("Hatha Yoga", list.get(0).getClassName());
    }

    // --- YOGA_SCHEDULE TESTS ---

    @Test
    void createSchedule_success() {
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(60);

        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(1)
                .instructorId(10)
                .location("Room 1")
                .startTime(start)
                .endTime(end)
                .maxCapacity(15)
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaInstructorRepository.findById(10)).thenReturn(Optional.of(activeInstructor));
        
        // Mocks for overlap checks (no overlaps found)
        when(yogaScheduleRepository.countOverlappingInstructorSchedules(eq(10), eq(start), eq(end), any())).thenReturn(0L);
        when(yogaScheduleRepository.countOverlappingLocationSchedules(eq("Room 1"), eq(start), eq(end), any())).thenReturn(0L);
        
        YogaSchedule saved = YogaSchedule.builder()
                .id(101)
                .yogaClass(hathaClass)
                .instructor(activeInstructor)
                .location("Room 1")
                .startTime(start)
                .endTime(end)
                .maxCapacity(15)
                .isDelete(false)
                .build();
        when(yogaScheduleRepository.save(any(YogaSchedule.class))).thenReturn(saved);

        YogaScheduleResponse response = yogaManagerService.createSchedule(request);

        assertNotNull(response);
        assertEquals(101, response.getScheduleId());
        assertEquals("Hatha Yoga", response.getClassName());
        assertEquals(10, response.getInstructorId());
    }

    @Test
    void createSchedule_classNotFound_throwsException() {
        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(999)
                .instructorId(10)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(1))
                .build();

        when(yogaClassRepository.findById(999)).thenReturn(Optional.empty());

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createSchedule(request);
        });

        assertEquals("YOGA-009", ex.getErrorCode());
    }

    @Test
    void createSchedule_instructorNotAvailable_throwsException() {
        activeInstructor.setStatus("BUSY"); // Không khả dụng

        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(1)
                .instructorId(10)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(1))
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaInstructorRepository.findById(10)).thenReturn(Optional.of(activeInstructor));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createSchedule(request);
        });

        assertEquals("YOGA-010", ex.getErrorCode());
    }

    @Test
    void createSchedule_invalidTimeLogic_throwsException() {
        LocalDateTime start = LocalDateTime.now().minusDays(1); // Trong quá khứ
        LocalDateTime end = start.plusHours(1);

        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(1)
                .instructorId(10)
                .startTime(start)
                .endTime(end)
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaInstructorRepository.findById(10)).thenReturn(Optional.of(activeInstructor));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createSchedule(request);
        });

        assertEquals("YOGA-011", ex.getErrorCode());
    }

    @Test
    void createSchedule_instructorOverlap_throwsException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(8).withMinute(0);
        LocalDateTime end = start.plusMinutes(60);

        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(1)
                .instructorId(10)
                .location("Room 2")
                .startTime(start)
                .endTime(end)
                .maxCapacity(15)
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaInstructorRepository.findById(10)).thenReturn(Optional.of(activeInstructor));
        
        // Mock overlapping instructor schedule count > 0
        when(yogaScheduleRepository.countOverlappingInstructorSchedules(eq(10), eq(start), eq(end), any())).thenReturn(1L);

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createSchedule(request);
        });

        assertEquals("YOGA-013", ex.getErrorCode());
    }

    @Test
    void createSchedule_locationOverlap_throwsException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(8).withMinute(0);
        LocalDateTime end = start.plusMinutes(60);

        YogaScheduleRequest request = YogaScheduleRequest.builder()
                .classId(1)
                .instructorId(10)
                .location("Room 1")
                .startTime(start)
                .endTime(end)
                .maxCapacity(15)
                .build();

        when(yogaClassRepository.findById(1)).thenReturn(Optional.of(hathaClass));
        when(yogaInstructorRepository.findById(10)).thenReturn(Optional.of(activeInstructor));
        
        // Mock overlap checks
        when(yogaScheduleRepository.countOverlappingInstructorSchedules(eq(10), eq(start), eq(end), any())).thenReturn(0L);
        // Mock overlapping location count > 0
        when(yogaScheduleRepository.countOverlappingLocationSchedules(eq("Room 1"), eq(start), eq(end), any())).thenReturn(1L);

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.createSchedule(request);
        });

        assertEquals("YOGA-014", ex.getErrorCode());
    }

    @Test
    void deleteSchedule_success() {
        when(yogaScheduleRepository.findById(100)).thenReturn(Optional.of(existingSchedule));
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(100, "REGISTERED")).thenReturn(0L);

        yogaManagerService.deleteSchedule(100);

        assertTrue(existingSchedule.getIsDelete());
        verify(yogaScheduleRepository, times(1)).save(existingSchedule);
    }

    @Test
    void deleteSchedule_activeRegistrationsExist_throwsException() {
        when(yogaScheduleRepository.findById(100)).thenReturn(Optional.of(existingSchedule));
        // Mock có 2 đăng ký chưa hủy
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(100, "REGISTERED")).thenReturn(2L);

        YogaBusinessException ex = assertThrows(YogaBusinessException.class, () -> {
            yogaManagerService.deleteSchedule(100);
        });

        assertEquals("YOGA-012", ex.getErrorCode());
        assertFalse(existingSchedule.getIsDelete());
    }

    @Test
    void getAllActiveInstructors_success() {
        when(yogaInstructorRepository.findByIsDeleteFalse()).thenReturn(Arrays.asList(activeInstructor));

        List<YogaInstructor> list = yogaManagerService.getAllActiveInstructors();

        assertEquals(1, list.size());
        assertEquals("YG0010", list.get(0).getInstructorCode());
    }
}
