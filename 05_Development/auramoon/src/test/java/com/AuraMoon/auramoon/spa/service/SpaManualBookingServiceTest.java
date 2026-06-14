package com.AuraMoon.auramoon.spa.service;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.entity.TreatmentRoom;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.SpaBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentRoomRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import com.AuraMoon.auramoon.spa.service.impl.SpaManualBookingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaManualBookingServiceTest {

    @Mock
    private SpaBookingRepository bookingRepository;

    @Mock
    private TreatmentBookingRepository treatmentBookingRepository;

    @Mock
    private TreatmentServiceRepository treatmentServiceRepository;

    @Mock
    private TreatmentRoomRepository roomRepository;

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BillingIntegrationService billingService;

    @InjectMocks
    private SpaManualBookingServiceImpl spaManualBookingService;

    @Test
    @DisplayName("Happy Path: Đặt lịch Spa ngoài gói thành công cho Guest Checked-In")
    void bookAdditionalService_happyPath_savesBookingAndCreatesFolioItem() {
        // Arrange
        Integer bookingId = 10;
        Integer serviceId = 5;
        Integer receptionistUserId = 2;
        Integer folioId = 100;
        LocalDateTime startTime = LocalDateTime.of(2026, 6, 15, 14, 0);

        Booking booking = Booking.builder()
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        TreatmentService service = TreatmentService.builder()
                .serviceName("Swedish Massage")
                .durationMinutes(60)
                .price(BigDecimal.valueOf(500000))
                .build();
        service.setId(serviceId);

        TreatmentRoom room = TreatmentRoom.builder().status("Active").build();
        room.setId(1);

        Therapist therapist = Therapist.builder().therapistCode("T002").status("Active").build();

        TreatmentBooking treatmentBooking = TreatmentBooking.builder()
                .bookingId(bookingId)
                .folioId(folioId)
                .treatmentService(service)
                .status("Scheduled")
                .build();
        treatmentBooking.setId(50);

        Schedule schedule = Schedule.builder()
                .treatmentBooking(treatmentBooking)
                .therapist(therapist)
                .room(room)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(60))
                .isDelete(false)
                .build();
        schedule.setId(1);

        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(bookingId);
        request.setServiceId(serviceId);
        request.setStartTime(startTime);
        request.setNote("Massage nhẹ");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(treatmentServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(billingService.findFolioIdByBookingId(bookingId)).thenReturn(Optional.of(folioId));
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(therapist));
        when(treatmentBookingRepository.save(any(TreatmentBooking.class))).thenReturn(treatmentBooking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        SpaScheduleResponse response = spaManualBookingService.bookAdditionalService(request, receptionistUserId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getScheduleId());
        assertEquals("T002", response.getTherapistCode());
        assertEquals(1, response.getRoomId());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(treatmentServiceRepository, times(1)).findById(serviceId);
        verify(billingService, times(1)).findFolioIdByBookingId(bookingId);
        verify(roomRepository, times(1)).findAvailableRoomsWithLock(any(), any());
        verify(therapistRepository, times(1)).findAvailableTherapistsWithLock(any(), any());
        verify(treatmentBookingRepository, times(1)).save(any(TreatmentBooking.class));
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
        
        verify(billingService, times(1)).createFolioItem(
                eq(folioId),
                eq(50),
                eq("Extra Spa"),
                contains("Swedish Massage"),
                eq(BigDecimal.valueOf(500000)),
                eq(receptionistUserId)
        );
    }

    @Test
    @DisplayName("Error Path 1: Báo lỗi SPA-002 khi Guest có trạng thái khác Checked-In")
    void bookAdditionalService_guestNotCheckedIn_throwsSpaBusinessException() {
        // Arrange
        Integer bookingId = 11;
        Integer serviceId = 5;
        Integer receptionistUserId = 2;
        LocalDateTime startTime = LocalDateTime.of(2026, 6, 15, 14, 0);

        Booking booking = Booking.builder()
                .bookingStatus("Confirmed") // Trạng thái chưa check-in
                .build();
        booking.setId(bookingId);

        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(bookingId);
        request.setServiceId(serviceId);
        request.setStartTime(startTime);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () ->
                spaManualBookingService.bookAdditionalService(request, receptionistUserId)
        );

        assertEquals("SPA-002", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Checked-In"));

        verify(treatmentServiceRepository, never()).findById(any());
        verify(billingService, never()).findFolioIdByBookingId(any());
        verify(treatmentBookingRepository, never()).save(any());
        verify(billingService, never()).createFolioItem(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Error Path 2: Báo lỗi SPA-010 khi không có chuyên viên trị liệu rảnh")
    void bookAdditionalService_noResourcesAvailable_throwsSpaBusinessException() {
        // Arrange
        Integer bookingId = 10;
        Integer serviceId = 5;
        Integer receptionistUserId = 2;
        Integer folioId = 100;
        LocalDateTime startTime = LocalDateTime.of(2026, 6, 15, 14, 0);

        Booking booking = Booking.builder()
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        TreatmentService service = TreatmentService.builder()
                .serviceName("Swedish Massage")
                .durationMinutes(60)
                .price(BigDecimal.valueOf(500000))
                .build();
        service.setId(serviceId);

        TreatmentRoom room = TreatmentRoom.builder().status("Active").build();
        room.setId(1);

        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(bookingId);
        request.setServiceId(serviceId);
        request.setStartTime(startTime);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(treatmentServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(billingService.findFolioIdByBookingId(bookingId)).thenReturn(Optional.of(folioId));
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(Collections.emptyList()); // Hết therapist

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () ->
                spaManualBookingService.bookAdditionalService(request, receptionistUserId)
        );

        assertEquals("SPA-010", exception.getErrorCode());
        verify(treatmentBookingRepository, never()).save(any());
        verify(scheduleRepository, never()).save(any());
        verify(billingService, never()).createFolioItem(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Error Path 3: Ghi nợ Billing lỗi, ném SPA-019 để kích hoạt rollback")
    void bookAdditionalService_billingFails_throwsSpaBusinessExceptionAndTriggersRollback() {
        // Arrange
        Integer bookingId = 10;
        Integer serviceId = 5;
        Integer receptionistUserId = 2;
        Integer folioId = 100;
        LocalDateTime startTime = LocalDateTime.of(2026, 6, 15, 14, 0);

        Booking booking = Booking.builder()
                .bookingStatus("Checked-In")
                .build();
        booking.setId(bookingId);

        TreatmentService service = TreatmentService.builder()
                .serviceName("Swedish Massage")
                .durationMinutes(60)
                .price(BigDecimal.valueOf(500000))
                .build();
        service.setId(serviceId);

        TreatmentRoom room = TreatmentRoom.builder().status("Active").build();
        room.setId(1);

        Therapist therapist = Therapist.builder().therapistCode("T002").status("Active").build();

        TreatmentBooking treatmentBooking = TreatmentBooking.builder()
                .bookingId(bookingId)
                .folioId(folioId)
                .treatmentService(service)
                .status("Scheduled")
                .build();
        treatmentBooking.setId(50);

        SpaScheduleRequest request = new SpaScheduleRequest();
        request.setBookingId(bookingId);
        request.setServiceId(serviceId);
        request.setStartTime(startTime);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(treatmentServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(billingService.findFolioIdByBookingId(bookingId)).thenReturn(Optional.of(folioId));
        when(roomRepository.findAvailableRoomsWithLock(any(), any())).thenReturn(List.of(room));
        when(therapistRepository.findAvailableTherapistsWithLock(any(), any())).thenReturn(List.of(therapist));
        when(treatmentBookingRepository.save(any(TreatmentBooking.class))).thenReturn(treatmentBooking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(new Schedule());

        // Cấu hình khi gọi BillingIntegrationService ném RuntimeException
        doThrow(new RuntimeException("Kết nối billing thất bại"))
                .when(billingService).createFolioItem(any(), any(), any(), any(), any(), any());

        // Act & Assert
        SpaBusinessException exception = assertThrows(SpaBusinessException.class, () ->
                spaManualBookingService.bookAdditionalService(request, receptionistUserId)
        );

        assertEquals("SPA-019", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("lỗi hệ thống trong quá trình xử lý giao dịch"));

        // Ghi chú: Rollback thực tế được Spring thực hiện thông qua chú thích @Transactional(rollbackFor = Exception.class)
        // khi ném bất kỳ Exception/RuntimeException nào từ luồng xử lý.
    }
}
