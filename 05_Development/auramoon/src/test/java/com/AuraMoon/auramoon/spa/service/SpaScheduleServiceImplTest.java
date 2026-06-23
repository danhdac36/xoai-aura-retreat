package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.entity.TreatmentRoom;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentRoomRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import com.AuraMoon.auramoon.spa.service.impl.SpaScheduleServiceImpl;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TreatmentRoomRepository roomRepository;
    @Mock
    private TherapistRepository therapistRepository;
    @Mock
    private TreatmentBookingRepository treatmentBookingRepository;
    @Mock
    private TreatmentServiceRepository treatmentServiceRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private SpaScheduleServiceImpl spaScheduleService;

    @Test
    @DisplayName("SPA-TC-001: Book lịch Spa thành công khi có đủ phòng và nhân viên")
    void scheduleSession_hasAvailableResources_shouldCreateScheduleSuccessfully() {
        // Arrange
        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(1);
        request.setServiceId(10);
        request.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));

        TreatmentBooking booking = new TreatmentBooking();
        booking.setId(100);
        when(treatmentBookingRepository.findByBookingIdAndTreatmentService_Id(1, 10)).thenReturn(List.of(booking));

        TreatmentService service = new TreatmentService();
        service.setDurationMinutes(60);
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setCheckinDate(LocalDate.of(2024, 6, 19));
        guestBooking.setCheckoutDate(LocalDate.of(2024, 6, 21));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        TreatmentRoom room = new TreatmentRoom();
        room.setId(5);
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));

        Therapist therapist = new Therapist();
        therapist.setTherapistCode("TH01");
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(therapist));

        Schedule savedSchedule = new Schedule();
        savedSchedule.setId(999);
        savedSchedule.setRoom(room);
        savedSchedule.setTherapist(therapist);
        savedSchedule.setStartTime(request.getStartTime());
        savedSchedule.setEndTime(request.getStartTime().plusMinutes(60));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(savedSchedule);

        // Act
        SpaScheduleResponse response = spaScheduleService.scheduleSession(request);

        // Assert
        assertNotNull(response);
        assertEquals(999, response.getScheduleId());
        assertEquals("TH01", response.getTherapistCode());
        assertEquals(5, response.getRoomId());
        assertEquals("Scheduled", booking.getStatus());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
        verify(treatmentBookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("SPA-TC-002: Báo lỗi khi hết Phòng rảnh (Conflict Trùng Lịch)")
    void scheduleSession_noAvailableRoom_shouldThrowSpaBusinessException() {
        // Arrange
        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(1);
        request.setServiceId(10);
        request.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));

        when(treatmentBookingRepository.findByBookingIdAndTreatmentService_Id(1, 10))
                .thenReturn(List.of(new TreatmentBooking()));

        TreatmentService service = new TreatmentService();
        service.setDurationMinutes(60);
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setCheckinDate(LocalDate.of(2024, 6, 19));
        guestBooking.setCheckoutDate(LocalDate.of(2024, 6, 21));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(Collections.emptyList());

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> {
            spaScheduleService.scheduleSession(request);
        });

        assertEquals("SPA-010", exception.getErrorCode());
        assertEquals("No available Therapist or Therapy Room could be found.", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("SPA-TC-003: Báo lỗi khi hết Chuyên viên rảnh (Conflict Trùng Lịch)")
    void scheduleSession_noAvailableTherapist_shouldThrowSpaBusinessException() {
        // Arrange
        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(1);
        request.setServiceId(10);
        request.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));

        when(treatmentBookingRepository.findByBookingIdAndTreatmentService_Id(1, 10))
                .thenReturn(List.of(new TreatmentBooking()));

        TreatmentService service = new TreatmentService();
        service.setDurationMinutes(60);
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setCheckinDate(LocalDate.of(2024, 6, 19));
        guestBooking.setCheckoutDate(LocalDate.of(2024, 6, 21));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(new TreatmentRoom()));
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(Collections.emptyList());

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> {
            spaScheduleService.scheduleSession(request);
        });

        assertEquals("SPA-010", exception.getErrorCode());
        assertEquals("No available Therapist or Therapy Room could be found.", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("SPA-TC-004: Báo lỗi khi Dịch vụ không nằm trong gói Retreat của khách")
    void scheduleSession_serviceNotInPackage_shouldThrowSpaBusinessException() {
        // Arrange
        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(1);
        request.setServiceId(10);
        request.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));

        when(treatmentBookingRepository.findByBookingIdAndTreatmentService_Id(1, 10))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> {
            spaScheduleService.scheduleSession(request);
        });

        assertEquals("SPA-001", exception.getErrorCode());
        assertEquals("Service not found, not in package, or all sessions already scheduled", exception.getMessage());
        verify(roomRepository, never()).findAvailableRoomsWithLock(any(), any());
    }
}
