package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;

@ExtendWith(MockitoExtension.class)
class ItineraryServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.AuraMoon.auramoon.spa.repository.ScheduleRepository scheduleRepository;

    @Mock
    private com.AuraMoon.auramoon.fnb.repository.MealOrderRepository mealOrderRepository;

    @Mock
    private YogaRegistrationRepository yogaRegistrationRepository;

    @InjectMocks
    private ItineraryServiceImpl itineraryService;

    @Test
    @DisplayName("ITI10-TC-001 - Lấy Timeline lịch trình thành công cho gói Stress Relief")
    void getTimelineForGuest_Success_StressRelief() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .typePackage("stress relief")
                .build();

        Booking booking = Booking.builder()
                .id(101)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDateTime.of(2026, 6, 20, 0, 0))
                .checkoutDate(LocalDateTime.of(2026, 6, 22, 0, 0))
                .retreatPackage(retreatPackage)
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));
        when(yogaRegistrationRepository.findByBookingIdAndStatus(anyInt(), anyString())).thenReturn(Collections.emptyList());

        // Act
        ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);

        // Assert
        assertNotNull(timeline);
        assertEquals(101, timeline.getBookingId());
        assertEquals("Nguyễn Văn Khách", timeline.getGuestName());
        assertFalse(timeline.getEvents().isEmpty());
    }

    @Test
    @DisplayName("ITI10-TC-002 - Lấy Timeline lịch trình thành công cho gói Detox")
    void getTimelineForGuest_Success_Detox() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .typePackage("detox weight loss")
                .build();

        Booking booking = Booking.builder()
                .id(102)
                .guestId(guestId)
                .bookingStatus("CHECKED_IN")
                .checkinDate(LocalDateTime.of(2026, 6, 20, 0, 0))
                .checkoutDate(LocalDateTime.of(2026, 6, 22, 0, 0))
                .retreatPackage(retreatPackage)
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));
        when(yogaRegistrationRepository.findByBookingIdAndStatus(anyInt(), anyString())).thenReturn(Collections.emptyList());

        // Act
        ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);

        // Assert
        assertNotNull(timeline);
        assertEquals(102, timeline.getBookingId());
        assertFalse(timeline.getEvents().isEmpty());
    }

    @Test
    @DisplayName("ITI10-TC-003 - Khách hàng không tồn tại -> Exception")
    void getTimelineForGuest_GuestNotFound_ThrowsException() {
        // Arrange
        Integer guestId = 999;
        when(userRepository.findById(guestId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.getTimelineForGuest(guestId);
        });

        assertTrue(exception.getMessage().contains("Không tìm thấy khách hàng"));
    }

    @Test
    @DisplayName("ITI10-TC-004 - Khách hàng không có lịch đặt phòng -> Exception")
    void getTimelineForGuest_NoBookings_ThrowsException() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyễn Văn Khách")
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.getTimelineForGuest(guestId);
        });

        assertTrue(exception.getMessage().contains("chưa có bất kỳ lịch đặt phòng nào"));
    }

    @Test
    @DisplayName("ITI10-TC-005 - Lấy Timeline lịch trình thành công có kèm lịch Yoga đã đăng ký")
    void getTimelineForGuest_Success_WithYogaEvents() {
        // Arrange
        Integer guestId = 1;
        User guest = User.builder().id(guestId).fullName("Nguyễn Văn Khách").build();
        Booking booking = Booking.builder()
                .id(101)
                .guestId(guestId)
                .bookingStatus("CHECKED_IN")
                .checkinDate(LocalDateTime.of(2026, 6, 20, 10, 0))
                .checkoutDate(LocalDateTime.of(2026, 6, 22, 12, 0))
                .build();

        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking));

        // Mock Yoga registration
        User instructorUser = User.builder().fullName("Học viên Yoga GV").build();
        YogaInstructor instructor = YogaInstructor.builder().user(instructorUser).build();
        YogaClass yogaClass = YogaClass.builder().className("Hatha Yoga Class").durationMinutes(60).build();
        YogaSchedule schedule = YogaSchedule.builder()
                .id(201)
                .yogaClass(yogaClass)
                .instructor(instructor)
                .location("Phòng tập A")
                .startTime(LocalDateTime.of(2026, 6, 21, 8, 0))
                .endTime(LocalDateTime.of(2026, 6, 21, 9, 0))
                .isDelete(false)
                .build();

        YogaRegistration yogaReg = YogaRegistration.builder()
                .id(301)
                .bookingId(101)
                .schedule(schedule)
                .status("REGISTERED")
                .build();

        when(yogaRegistrationRepository.findByBookingIdAndStatus(101, "REGISTERED"))
                .thenReturn(Collections.singletonList(yogaReg));

        // Act
        ItineraryTimelineDTO timeline = itineraryService.getTimelineForGuest(guestId);

        // Assert
        assertNotNull(timeline);
        assertEquals(3, timeline.getEvents().size()); // 1. Checkin, 2. Yoga, 3. Checkout

        ItineraryTimelineDTO.TimelineEvent yogaEvent = timeline.getEvents().stream()
                .filter(e -> e.getEventName().contains("Yoga"))
                .findFirst().orElse(null);

        assertNotNull(yogaEvent);
        assertEquals("Yoga: Hatha Yoga Class", yogaEvent.getEventName());
        assertEquals("Phòng tập A", yogaEvent.getLocation());
        assertTrue(yogaEvent.getDescription().contains("GV Học viên Yoga GV"));
    }

    @Test
    @DisplayName("BKG-SVC-001 - Lấy danh sách lịch sử đặt phòng thành công")
    void getBookingHistory_Success() {
        // Arrange
        Integer guestId = 1;
        RetreatPackage retreatPackage = RetreatPackage.builder()
                .packageName("Gói Tĩnh Dưỡng Cuối Tuần")
                .build();

        Booking booking1 = Booking.builder()
                .id(101)
                .guestId(guestId)
                .checkinDate(LocalDateTime.of(2026, 5, 1, 14, 0))
                .checkoutDate(LocalDateTime.of(2026, 5, 3, 12, 0))
                .bookingStatus("CHECKED_OUT")
                .totalAmount(java.math.BigDecimal.valueOf(5000000))
                .retreatPackage(retreatPackage)
                .build();

        when(bookingRepository.findByGuestId(guestId)).thenReturn(Collections.singletonList(booking1));

        // Act
        // This method and DTO don't exist yet! Wait for implementation phase.
        java.util.List<com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO> dtos = itineraryService.getBookingHistory(guestId);

        // Assert
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO dto = dtos.get(0);
        assertEquals(101, dto.getBookingId());
        assertEquals("Gói Tĩnh Dưỡng Cuối Tuần", dto.getPackageName());
        assertEquals("CHECKED_OUT", dto.getStatus());
        assertEquals(5000000.0, dto.getTotalAmount());
    }
}
