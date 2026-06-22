package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.entity.Consent;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.booking.exception.BookingNotFoundException;
import com.AuraMoon.auramoon.booking.exception.InvalidVillaAssignmentException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.impl.CheckInServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private VillaRepository villaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private VillaService villaService;

    @InjectMocks
    private CheckInServiceImpl checkInService;

    @Test
    @DisplayName("UC08-TC-001: Check-in thành công, gán phòng vật lý và mã hóa số định danh")
    public void checkIn_validRequest_updatesStatusesAndEncryptsID() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;
        String rawIdentity = "012345678901";

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode(rawIdentity)
                .villaId(villaId)
                .fullName("Nguyen Van B")
                .phone("0987654321")
                .gender("Female")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .build();

        VillaType villaType = VillaType.builder()
                .id(2)
                .typeName("Ocean View Villa")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .id(1)
                .packageName("Detox Program")
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .retreatPackage(retreatPackage)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now())
                .build();

        Villa villa = Villa.builder()
                .id(villaId)
                .villaCode("VIL-101")
                .villaType(villaType)
                .villaStatus("AVAILABLE")
                .build();

        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyen Van A")
                .status("ACTIVE")
                .build();

        Consent consent = Consent.builder()
                .id(1)
                .user(guest)
                .consentStatus(true)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(guestId)).thenReturn(Optional.of(consent));

        // Act
        checkInService.performCheckIn(request);

        // Assert
        assertEquals("CHECKED_IN", booking.getBookingStatus());
        assertEquals(villa, booking.getAssignedVilla());
        assertEquals(rawIdentity, guest.getIdentifyCode());
        assertEquals("Nguyen Van B", guest.getFullName());
        assertEquals("0987654321", guest.getPhone());
        assertEquals("Female", guest.getGender());
        assertEquals(LocalDate.of(1995, 5, 5), guest.getDateOfBirth());

        verify(bookingRepository, times(1)).save(booking);
        verify(userRepository, times(1)).save(guest);
        verify(villaService, times(1)).updateVillaStatuses(villaId, "OCCUPIED", "CLEAN");
    }

    @Test
    @DisplayName("UC08-TC-002: Check-in thất bại và ném lỗi khi không tìm thấy đặt phòng")
    public void checkIn_bookingNotFound_throwsException() {
        // Arrange
        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(999)
                .identifyCode("123")
                .villaId(5)
                .build();

        when(bookingRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookingNotFoundException.class, () -> {
            checkInService.performCheckIn(request);
        });
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("UC08-TC-003: Check-in thất bại và ném lỗi khi ngày check-in ở tương lai")
    public void checkIn_futureCheckinDate_throwsException() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode("012345678901")
                .villaId(villaId)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now().plusDays(1)) // Ngày nhận phòng ở tương lai
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            checkInService.performCheckIn(request);
        });
        assertTrue(exception.getMessage().contains("Không thể check-in trước ngày nhận phòng thực tế"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("UC08-TC-004: Check-in thất bại khi khách hàng chưa có bản ghi đồng ý bảo mật")
    public void checkIn_noConsent_throwsException() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode("012345678901")
                .villaId(villaId)
                .build();

        Villa villa = Villa.builder()
                .id(villaId)
                .villaCode("VIL-101")
                .villaStatus("AVAILABLE")
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now())
                .build();

        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyen Van A")
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(guestId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            checkInService.performCheckIn(request);
        });
        assertTrue(exception.getMessage().contains("Khách hàng chưa đồng ý điều khoản bảo mật dữ liệu cá nhân"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("UC08-TC-005: Check-in thất bại khi trạng thái đồng ý bảo mật của khách hàng là false")
    public void checkIn_consentDenied_throwsException() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode("012345678901")
                .villaId(villaId)
                .build();

        Villa villa = Villa.builder()
                .id(villaId)
                .villaCode("VIL-101")
                .villaStatus("AVAILABLE")
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now())
                .build();

        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyen Van A")
                .build();

        Consent consent = Consent.builder()
                .id(1)
                .user(guest)
                .consentStatus(false) // Consent denied/not agreed
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(guestId)).thenReturn(Optional.of(consent));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            checkInService.performCheckIn(request);
        });
        assertTrue(exception.getMessage().contains("Khách hàng chưa đồng ý điều khoản bảo mật dữ liệu cá nhân"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("UC08-TC-006: Check-in thành công khi lễ tân xác nhận khách đồng ý bảo mật tại quầy")
    public void checkIn_receptionistConfirmsConsent_savesConsentAndSucceeds() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;
        String rawIdentity = "012345678901";

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode(rawIdentity)
                .villaId(villaId)
                .privacyConsent(true)
                .build();

        VillaType villaType = VillaType.builder()
                .id(2)
                .typeName("Ocean View Villa")
                .build();

        RetreatPackage retreatPackage = RetreatPackage.builder()
                .id(1)
                .packageName("Detox Program")
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .retreatPackage(retreatPackage)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDate.now())
                .build();

        Villa villa = Villa.builder()
                .id(villaId)
                .villaCode("VIL-101")
                .villaType(villaType)
                .villaStatus("AVAILABLE")
                .build();

        User guest = User.builder()
                .id(guestId)
                .fullName("Nguyen Van A")
                .status("ACTIVE")
                .build();

        Consent consent = Consent.builder()
                .id(1)
                .user(guest)
                .consentStatus(true)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(guestId)).thenReturn(Optional.of(consent));

        // Act
        checkInService.performCheckIn(request);

        // Assert
        assertEquals("CHECKED_IN", booking.getBookingStatus());
        assertEquals(villa, booking.getAssignedVilla());
        assertEquals(rawIdentity, guest.getIdentifyCode());

        verify(consentRepository, times(1)).save(any(Consent.class));
        verify(bookingRepository, times(1)).save(booking);
        verify(userRepository, times(1)).save(guest);
        verify(villaService, times(1)).updateVillaStatuses(villaId, "OCCUPIED", "CLEAN");
    }
}
