package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.service.impl.TherapistScheduleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TherapistScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private TreatmentBookingRepository treatmentBookingRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PhysicalHealthProfileRepository physicalHealthProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TherapistScheduleServiceImpl therapistScheduleService;

    @Test
    @DisplayName("Cập nhật thành công trạng thái ca làm việc từ Scheduled sang Ongoing (Vào ca)")
    void updateSessionStatus_scheduledToOngoing_updatesStatusSuccessfully() {
        // Arrange
        Integer scheduleId = 1;
        String therapistCode = "T002";
        String newStatus = "Ongoing";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("Scheduled").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act
        therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus);

        // Assert
        assertEquals("Ongoing", booking.getStatus());
        verify(treatmentBookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Cập nhật thành công trạng thái ca làm việc từ Ongoing sang Completed (Hoàn thành)")
    void updateSessionStatus_ongoingToCompleted_updatesStatusSuccessfully() {
        // Arrange
        Integer scheduleId = 2;
        String therapistCode = "T002";
        String newStatus = "Completed";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("Ongoing").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act
        therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus);

        // Assert
        assertEquals("Completed", booking.getStatus());
        verify(treatmentBookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Cập nhật thành công trạng thái ca làm việc từ Scheduled sang No-Show (Vắng mặt)")
    void updateSessionStatus_scheduledToNoShow_updatesStatusSuccessfully() {
        // Arrange
        Integer scheduleId = 3;
        String therapistCode = "T002";
        String newStatus = "No-Show";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("Scheduled").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act
        therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus);

        // Assert
        assertEquals("No-Show", booking.getStatus());
        verify(treatmentBookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Lỗi khi hoàn tác từ trạng thái Ongoing về Scheduled (Hủy Vào ca)")
    void updateSessionStatus_ongoingToScheduled_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 4;
        String therapistCode = "T002";
        String newStatus = "Scheduled";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("Ongoing").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );
        assertEquals("SPA-015", exception.getErrorCode());
        verify(treatmentBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lỗi khi hoàn tác từ trạng thái Completed về Ongoing (Hủy Hoàn thành)")
    void updateSessionStatus_completedToOngoing_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 5;
        String therapistCode = "T002";
        String newStatus = "Ongoing";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("Completed").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );
        assertEquals("SPA-015", exception.getErrorCode());
        verify(treatmentBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lỗi khi hoàn tác từ trạng thái No-Show về Scheduled (Hủy Vắng mặt)")
    void updateSessionStatus_noShowToScheduled_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 6;
        String therapistCode = "T002";
        String newStatus = "Scheduled";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        TreatmentBooking booking = TreatmentBooking.builder().status("No-Show").build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(booking)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );
        assertEquals("SPA-015", exception.getErrorCode());
        verify(treatmentBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lỗi khi tham số trạng thái mới không hợp lệ")
    void updateSessionStatus_invalidNewStatus_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 7;
        String therapistCode = "T002";
        String newStatus = "InvalidStatus";

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );

        assertEquals("SPA-012", exception.getErrorCode());
        assertEquals("Trạng thái không hợp lệ", exception.getMessage());
        verifyNoInteractions(scheduleRepository, treatmentBookingRepository);
    }

    @Test
    @DisplayName("Lỗi khi không tìm thấy ca làm việc")
    void updateSessionStatus_scheduleNotFound_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 999;
        String therapistCode = "T002";
        String newStatus = "Ongoing";

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );

        assertEquals("SPA-013", exception.getErrorCode());
        assertEquals("Không tìm thấy ca trị liệu", exception.getMessage());
        verifyNoInteractions(treatmentBookingRepository);
    }

    @Test
    @DisplayName("Lỗi khi chuyên viên không được phân công cho ca này cập nhật")
    void updateSessionStatus_therapistNotAssigned_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 8;
        String therapistCode = "T002";
        String wrongTherapistCode = "T003";
        String newStatus = "Ongoing";

        Therapist therapist = Therapist.builder().therapistCode(wrongTherapistCode).build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );

        assertEquals("SPA-011", exception.getErrorCode());
        assertEquals("Bạn không có quyền cập nhật ca này", exception.getMessage());
        verifyNoInteractions(treatmentBookingRepository);
    }

    @Test
    @DisplayName("Lỗi khi không tìm thấy thông tin lượt đặt dịch vụ tương ứng")
    void updateSessionStatus_treatmentBookingNotFound_throwsSpaBusinessException() {
        // Arrange
        Integer scheduleId = 9;
        String therapistCode = "T002";
        String newStatus = "Ongoing";

        Therapist therapist = Therapist.builder().therapistCode(therapistCode).build();
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .therapist(therapist)
                .treatmentBooking(null)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> 
            therapistScheduleService.updateSessionStatus(scheduleId, therapistCode, newStatus)
        );

        assertEquals("SPA-013", exception.getErrorCode());
        assertEquals("Không tìm thấy lượt đặt dịch vụ tương ứng", exception.getMessage());
        verifyNoInteractions(treatmentBookingRepository);
    }
}
