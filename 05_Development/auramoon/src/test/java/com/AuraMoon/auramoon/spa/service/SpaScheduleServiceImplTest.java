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
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.billing.service.IEmailNotificationService;
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
    @Mock
    private UserRepository userRepository;
    @Mock
    private IEmailNotificationService emailNotificationService;

    @InjectMocks
    private SpaScheduleServiceImpl spaScheduleService;

    @Test
    @DisplayName("SPA-TC-001: Book lịch Spa thành công khi có đủ phòng và nhân viên")
    void scheduleSession_hasAvailableResources_shouldCreateScheduleSuccessfully() throws Exception {
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
        service.setServiceName("Body Massage");
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        guestBooking.setBookingStatus("Checked-In");
        guestBooking.setGuestId(5);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1)).thenReturn(Collections.emptyList());

        TreatmentRoom room = new TreatmentRoom();
        room.setId(5);
        room.setRoomName("VIP Room 1");
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));

        Therapist therapist = new Therapist();
        therapist.setTherapistCode("TH01");
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(therapist));

        User guest = new User();
        guest.setId(5);
        guest.setEmail("guest@example.com");
        guest.setFullName("Nguyen Van A");
        when(userRepository.findById(5)).thenReturn(Optional.of(guest));

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
        verify(emailNotificationService, times(1)).sendSpaBookingReminderEmail(
                eq("guest@example.com"),
                eq("Nguyen Van A"),
                eq("Body Massage"),
                eq("VIP Room 1"),
                any()
        );
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
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        guestBooking.setBookingStatus("Checked-In");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1)).thenReturn(Collections.emptyList());

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
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        guestBooking.setBookingStatus("Checked-In");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1)).thenReturn(Collections.emptyList());

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

    @Test
    @DisplayName("SPA-TC-005: Lựa chọn kỹ thuật viên có số ca làm việc ít nhất trong ngày")
    void scheduleSession_shouldSelectTherapistWithLeastWorkload() throws Exception {
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
        service.setServiceName("Body Massage");
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        guestBooking.setBookingStatus("Checked-In");
        guestBooking.setGuestId(5);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1)).thenReturn(Collections.emptyList());

        TreatmentRoom room = new TreatmentRoom();
        room.setId(5);
        room.setRoomName("VIP Room 1");
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));

        // Two therapists available
        Therapist t1 = new Therapist();
        t1.setId(101);
        t1.setTherapistCode("TH01");

        Therapist t2 = new Therapist();
        t2.setId(102);
        t2.setTherapistCode("TH02");

        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(t1, t2));

        // t1 has workload = 3, t2 has workload = 1. The system should pick t2!
        when(scheduleRepository.countDailySchedulesForTherapist(eq(101), any(), any())).thenReturn(3L);
        when(scheduleRepository.countDailySchedulesForTherapist(eq(102), any(), any())).thenReturn(1L);

        User guest = new User();
        guest.setId(5);
        guest.setEmail("guest@example.com");
        guest.setFullName("Nguyen Van A");
        when(userRepository.findById(5)).thenReturn(Optional.of(guest));

        Schedule savedSchedule = new Schedule();
        savedSchedule.setId(999);
        savedSchedule.setRoom(room);
        savedSchedule.setTherapist(t2); // expected selected therapist
        savedSchedule.setStartTime(request.getStartTime());
        savedSchedule.setEndTime(request.getStartTime().plusMinutes(60));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(savedSchedule);

        // Act
        SpaScheduleResponse response = spaScheduleService.scheduleSession(request);

        // Assert
        assertNotNull(response);
        assertEquals("TH02", response.getTherapistCode()); // Verify that t2 was chosen
        verify(scheduleRepository).save(org.mockito.ArgumentMatchers.argThat(s -> s.getTherapist().getId().equals(102)));
        verify(emailNotificationService, times(1)).sendSpaBookingReminderEmail(
                eq("guest@example.com"),
                eq("Nguyen Van A"),
                eq("Body Massage"),
                eq("VIP Room 1"),
                any()
        );
    }

    @Test
    @DisplayName("SPA-TC-006: Đơn 1 người đặt ca spa thứ 2 trùng giờ → thất bại")
    void scheduleSession_singleGuest_overlap_shouldThrowSpaBusinessException() {
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
        guestBooking.setTotalGuests(1); // 1 guest
        guestBooking.setBookingStatus("Checked-In");
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        // Mock 1 existing overlapping schedule
        Schedule existingSchedule = new Schedule();
        existingSchedule.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));
        existingSchedule.setEndTime(LocalDateTime.of(2024, 6, 20, 11, 0));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(List.of(existingSchedule));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> {
            spaScheduleService.scheduleSession(request);
        });

        assertEquals("SPA-013", exception.getErrorCode());
        assertEquals("Quý khách không thể đặt 2 ca spa cùng một thời điểm.", exception.getMessage());
    }

    @Test
    @DisplayName("SPA-TC-007: Đơn nhiều người đặt ca spa trùng giờ → thành công nếu chưa vượt quá số khách")
    void scheduleSession_multiGuest_overlapWithinLimit_shouldSucceed() throws Exception {
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
        service.setServiceName("Body Massage");
        when(treatmentServiceRepository.findById(10)).thenReturn(Optional.of(service));

        Booking guestBooking = new Booking();
        guestBooking.setTotalGuests(2); // 2 guests
        guestBooking.setBookingStatus("Checked-In");
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        guestBooking.setGuestId(5);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        // Mock 1 existing overlapping schedule (overlapping count 1 < 2, so it should be allowed)
        Schedule existingSchedule = new Schedule();
        existingSchedule.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));
        existingSchedule.setEndTime(LocalDateTime.of(2024, 6, 20, 11, 0));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(List.of(existingSchedule));

        TreatmentRoom room = new TreatmentRoom();
        room.setId(5);
        room.setRoomName("VIP Room 1");
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));

        Therapist therapist = new Therapist();
        therapist.setTherapistCode("TH01");
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(therapist));

        User guest = new User();
        guest.setId(5);
        guest.setEmail("guest@example.com");
        guest.setFullName("Nguyen Van A");
        when(userRepository.findById(5)).thenReturn(Optional.of(guest));

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
    }

    @Test
    @DisplayName("SPA-TC-008: Đơn nhiều người đặt ca spa trùng giờ → thất bại nếu vượt quá số khách")
    void scheduleSession_multiGuest_overlapExceedsLimit_shouldThrowSpaBusinessException() {
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
        guestBooking.setTotalGuests(2); // 2 guests
        guestBooking.setBookingStatus("Checked-In");
        guestBooking.setCheckinDate(LocalDateTime.of(2024, 6, 19, 14, 0));
        guestBooking.setCheckoutDate(LocalDateTime.of(2024, 6, 21, 12, 0));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(guestBooking));

        // Mock 2 existing overlapping schedules (overlapping count 2 >= 2, so it should fail)
        Schedule existingSchedule1 = new Schedule();
        existingSchedule1.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 0));
        existingSchedule1.setEndTime(LocalDateTime.of(2024, 6, 20, 11, 0));
        Schedule existingSchedule2 = new Schedule();
        existingSchedule2.setStartTime(LocalDateTime.of(2024, 6, 20, 10, 30));
        existingSchedule2.setEndTime(LocalDateTime.of(2024, 6, 20, 11, 30));
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(List.of(existingSchedule1, existingSchedule2));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () -> {
            spaScheduleService.scheduleSession(request);
        });

        assertEquals("SPA-013", exception.getErrorCode());
        assertEquals("Số lượng ca spa trùng thời điểm vượt quá số lượng khách trong đơn đặt phòng (tối đa 2 người).", exception.getMessage());
    }
}
