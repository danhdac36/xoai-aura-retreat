package com.AuraMoon.auramoon.yoga.service.impl;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaScheduleRepository;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationRequest;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationResponse;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.exception.HealthWarningException;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.yoga.dto.YogaParticipantResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YogaRegistrationServiceImplTest {

    @Mock
    private YogaScheduleRepository yogaScheduleRepository;
    @Mock
    private YogaRegistrationRepository yogaRegistrationRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PhysicalHealthProfileRepository physicalHealthProfileRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private YogaRegistrationServiceImpl yogaRegistrationService;

    private Booking checkedInBooking;
    private YogaSchedule activeSchedule;
    private AesDataEncryptor encryptor;

    @BeforeEach
    void setUp() {
        encryptor = new AesDataEncryptor();

        checkedInBooking = Booking.builder()
                .id(1)
                .guestId(100)
                .bookingStatus("Checked-In")
                .build();

        YogaClass yogaClass = YogaClass.builder()
                .id(10)
                .className("Hatha Yoga Trị Liệu")
                .durationMinutes(60)
                .build();

        YogaInstructor instructor = YogaInstructor.builder()
                .instructorId(200)
                .instructorCode("INST01")
                .build();

        activeSchedule = YogaSchedule.builder()
                .id(5)
                .yogaClass(yogaClass)
                .instructor(instructor)
                .location("Yoga Outdoor Studio")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .maxCapacity(15)
                .isDelete(false)
                .build();
    }

    @Test
    @DisplayName("YOGA-TC-001 - Đăng ký Yoga thành công (Happy Path)")
    void registerYogaClass_success() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(0L);
        when(yogaRegistrationRepository.findOverlappingRegistrations(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(Collections.emptyList());
        when(physicalHealthProfileRepository.findByUserId(100)).thenReturn(Optional.empty());

        YogaRegistration savedReg = YogaRegistration.builder()
                .id(101)
                .bookingId(1)
                .schedule(activeSchedule)
                .status("REGISTERED")
                .build();
        when(yogaRegistrationRepository.save(any(YogaRegistration.class))).thenReturn(savedReg);

        YogaRegistrationResponse response = yogaRegistrationService.registerYogaClass(request);

        assertNotNull(response);
        assertEquals(101, response.getRegistrationId());
        assertEquals("REGISTERED", response.getStatus());
        verify(yogaRegistrationRepository, times(1)).save(any(YogaRegistration.class));
    }

    @Test
    @DisplayName("YOGA-TC-001.5 - Báo lỗi khi đã đăng ký lịch học này từ trước")
    void registerYogaClass_alreadyRegistered_throwsException() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        
        YogaRegistration existing = YogaRegistration.builder().id(101).bookingId(1).schedule(activeSchedule).status("REGISTERED").build();
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.of(existing));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-007", ex.getErrorCode());
        assertEquals("Bạn đã đăng ký lớp học này rồi", ex.getMessage());
    }

    @Test
    @DisplayName("YOGA-TC-002 - Báo lỗi khi lớp học đã đầy sĩ số (Over-capacity)")
    void registerYogaClass_classFull_throwsException() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(15L);

        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-002", ex.getErrorCode());
    }

    @Test
    @DisplayName("YOGA-TC-003 - Báo lỗi khi trùng lịch lớp Yoga khác")
    void registerYogaClass_overlapYoga_throwsException() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(0L);

        // Giả lập có đăng ký Yoga khác chồng giờ
        YogaRegistration overlap = new YogaRegistration();
        when(yogaRegistrationRepository.findOverlappingRegistrations(eq(1), any(), any()))
                .thenReturn(List.of(overlap));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-003", ex.getErrorCode());
    }

    @Test
    @DisplayName("YOGA-TC-003.5 - Báo lỗi khi trùng lịch trị liệu Spa")
    void registerYogaClass_overlapSpa_throwsException() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(0L);
        when(yogaRegistrationRepository.findOverlappingRegistrations(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());

        // Giả lập có lịch Spa chồng giờ
        TreatmentBooking tb = TreatmentBooking.builder().status("Scheduled").build();
        Schedule spaSession = Schedule.builder()
                .startTime(activeSchedule.getStartTime().plusMinutes(10))
                .endTime(activeSchedule.getEndTime().minusMinutes(10))
                .treatmentBooking(tb)
                .build();

        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(List.of(spaSession));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-003", ex.getErrorCode());
    }

    @Test
    @DisplayName("YOGA-TC-004 - Báo lỗi khi phòng của khách chưa Checked-In")
    void registerYogaClass_notCheckedIn_throwsException() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false)
                .build();

        Booking unconfirmedBooking = Booking.builder()
                .id(1)
                .bookingStatus("CONFIRMED") // Chưa checked-in
                .build();
        when(bookingRepository.findById(1)).thenReturn(Optional.of(unconfirmedBooking));

        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-006", ex.getErrorCode());
    }

    @Test
    @DisplayName("YOGA-TC-005 - Cảnh báo sức khỏe khi phát hiện chấn thương gối")
    void registerYogaClass_kneeInjuryWarning() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(false) // Chưa bấm đồng ý tự chịu trách nhiệm
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(0L);
        when(yogaRegistrationRepository.findOverlappingRegistrations(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(Collections.emptyList());

        // Tạo hồ sơ sức khỏe chứa từ khóa chấn thương khớp gối
        PhysicalHealthProfile hp = PhysicalHealthProfile.builder()
                .userId(100)
                .medicalConditions(encryptor.convertToDatabaseColumn("Không"))
                .injuries(encryptor.convertToDatabaseColumn("Bị đau khớp gối trái"))
                .build();
        when(physicalHealthProfileRepository.findByUserId(100)).thenReturn(Optional.of(hp));

        HealthWarningException ex = assertThrows(HealthWarningException.class,
                () -> yogaRegistrationService.registerYogaClass(request));
        assertEquals("YOGA-005", ex.getErrorCode());
        assertEquals("KNEE", ex.getWarningCategory());
    }

    @Test
    @DisplayName("YOGA-TC-006 - Đăng ký thành công sau khi xác nhận tự chịu trách nhiệm chấn thương gối")
    void registerYogaClass_kneeInjuryWarning_confirmed() {
        YogaRegistrationRequest request = YogaRegistrationRequest.builder()
                .bookingId(1)
                .yogaScheduleId(5)
                .confirmHealthWarning(true) // Đã bấm đồng ý tự chịu trách nhiệm
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(checkedInBooking));
        when(yogaScheduleRepository.findByIdForUpdate(5)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findByBookingIdAndSchedule_IdAndStatus(1, 5, "REGISTERED"))
                .thenReturn(Optional.empty());
        when(yogaRegistrationRepository.countBySchedule_IdAndStatus(5, "REGISTERED")).thenReturn(0L);
        when(yogaRegistrationRepository.findOverlappingRegistrations(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());
        when(scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(1))
                .thenReturn(Collections.emptyList());

        // Lưu bản ghi đăng ký thành công
        YogaRegistration savedReg = YogaRegistration.builder()
                .id(102)
                .bookingId(1)
                .schedule(activeSchedule)
                .status("REGISTERED")
                .build();
        when(yogaRegistrationRepository.save(any(YogaRegistration.class))).thenReturn(savedReg);

        YogaRegistrationResponse response = yogaRegistrationService.registerYogaClass(request);

        assertNotNull(response);
        assertEquals(102, response.getRegistrationId());
        assertEquals("REGISTERED", response.getStatus());
        verify(yogaRegistrationRepository, times(1)).save(any(YogaRegistration.class));
        // Đảm bảo không gọi repository sức khỏe vì confirmHealthWarning = true
        verify(physicalHealthProfileRepository, never()).findByUserId(anyInt());
    }

    @Test
    @DisplayName("YOGA-TC-007 - Lấy danh sách lịch học Yoga khả dụng theo ngày")
    void getAvailableSchedules_success() {
        LocalDate date = LocalDate.of(2026, 6, 27);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        when(yogaScheduleRepository.findSchedulesByDateRange(start, end))
                .thenReturn(List.of(activeSchedule));

        List<YogaSchedule> result = yogaRegistrationService.getAvailableSchedules(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activeSchedule, result.get(0));
        verify(yogaScheduleRepository, times(1)).findSchedulesByDateRange(start, end);
    }

    @Test
    @DisplayName("YOGA-TC-013 - Giáo viên lấy danh sách học viên lớp mình dạy thành công")
    void getParticipantsForSchedule_success() {
        // Arrange
        Integer scheduleId = 5;
        Integer instructorUserId = 200;

        when(yogaScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(activeSchedule));

        YogaRegistration reg = YogaRegistration.builder()
                .id(101)
                .bookingId(1)
                .status("REGISTERED")
                .build();
        when(yogaRegistrationRepository.findBySchedule_IdAndStatus(scheduleId, "REGISTERED"))
                .thenReturn(List.of(reg));

        Villa villa = Villa.builder().villaCode("GV01").build();
        Booking booking = Booking.builder()
                .id(1)
                .guestId(100)
                .assignedVilla(villa)
                .build();
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        User guest = User.builder().id(100).fullName("Nguyễn Văn Khách").build();
        when(userRepository.findById(100)).thenReturn(Optional.of(guest));
        when(physicalHealthProfileRepository.findByUserId(100)).thenReturn(Optional.empty());

        // Act
        List<YogaParticipantResponse> result = yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nguyễn Văn Khách", result.get(0).getGuestName());
        assertEquals("GV01", result.get(0).getVillaName());
        assertEquals("Không", result.get(0).getInjuriesNote());
    }

    @Test
    @DisplayName("YOGA-TC-014 - Chặn giáo viên khác truy cập xem danh sách học viên")
    void getParticipantsForSchedule_unauthorized_throwsException() {
        // Arrange
        Integer scheduleId = 5;
        Integer otherInstructorUserId = 201;

        when(yogaScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(activeSchedule));

        // Act & Assert
        YogaBusinessException ex = assertThrows(YogaBusinessException.class,
                () -> yogaRegistrationService.getParticipantsForSchedule(scheduleId, otherInstructorUserId));
        assertEquals("YOGA-008", ex.getErrorCode());
    }

    @Test
    @DisplayName("YOGA-TC-015 - Xác thực tối thiểu hóa thông tin nhạy cảm (Data Minimization)")
    void getParticipantsForSchedule_dataMinimization() {
        // Arrange
        Integer scheduleId = 5;
        Integer instructorUserId = 200;

        when(yogaScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(activeSchedule));

        YogaRegistration reg = YogaRegistration.builder()
                .id(101)
                .bookingId(1)
                .status("REGISTERED")
                .build();
        when(yogaRegistrationRepository.findBySchedule_IdAndStatus(scheduleId, "REGISTERED"))
                .thenReturn(List.of(reg));

        Villa villa = Villa.builder().villaCode("GV01").build();
        Booking booking = Booking.builder()
                .id(1)
                .guestId(100)
                .assignedVilla(villa)
                .build();
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        User guest = User.builder().id(100).fullName("Nguyễn Văn Khách").build();
        when(userRepository.findById(100)).thenReturn(Optional.of(guest));

        // Tạo hồ sơ sức khỏe chứa chấn thương khớp gối & dị ứng cua biển
        PhysicalHealthProfile hp = PhysicalHealthProfile.builder()
                .userId(100)
                .medicalConditions(encryptor.convertToDatabaseColumn("Đau bao tử, Huyết áp cao"))
                .injuries(encryptor.convertToDatabaseColumn("Đau khớp gối trái, dị ứng cua biển"))
                .build();
        when(physicalHealthProfileRepository.findByUserId(100)).thenReturn(Optional.of(hp));

        // Act
        List<YogaParticipantResponse> result = yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        // Kỳ vọng chỉ hiển thị khớp gối và huyết áp cao. Dị ứng cua biển và đau bao tử bị lọc bỏ.
        assertEquals("Đau khớp gối trái", result.get(0).getInjuriesNote());
        assertEquals("Huyết áp cao", result.get(0).getMedicalConditionsNote());
    }

    @Test
    @DisplayName("YOGA-TC-016 - Trả về danh sách rỗng khi chưa có học viên đăng ký")
    void getParticipantsForSchedule_emptyList() {
        // Arrange
        Integer scheduleId = 5;
        Integer instructorUserId = 200;

        when(yogaScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(activeSchedule));
        when(yogaRegistrationRepository.findBySchedule_IdAndStatus(scheduleId, "REGISTERED"))
                .thenReturn(Collections.emptyList());

        // Act
        List<YogaParticipantResponse> result = yogaRegistrationService.getParticipantsForSchedule(scheduleId, instructorUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
