package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
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
import com.AuraMoon.auramoon.common.service.EncryptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private EncryptionService encryptionService;

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
        String encryptedIdentity = "ENCRYPTED_012345678901";

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode(rawIdentity)
                .villaId(villaId)
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
                .build();

        // The booking requires a villa type that matches the physical villa type
        // Let's assume there is a way we verify this or retreatPackage is associated with a VillaType, or the user selects it.
        // Wait, in our Booking entity, let's check: Booking has assignedVilla.
        // In BookingRequestDTO, we have villaTypeId. But in Booking entity we don't store villaTypeId directly, but let's assume retreatPackage has it or we can check villa type matches.
        // In our test, let's mock the physical Villa to match the expected booking's villa type. Let's assume booking has a target villa type or the physical villa type is correct.
        // Let's mock a Villa with type 2.
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

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(villaRepository.findById(villaId)).thenReturn(Optional.of(villa));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(encryptionService.encrypt(rawIdentity)).thenReturn(encryptedIdentity);

        // Act
        checkInService.performCheckIn(request);

        // Assert
        assertEquals("CHECKED-IN", booking.getBookingStatus());
        assertEquals(villa, booking.getAssignedVilla());
        assertEquals(encryptedIdentity, guest.getIdentifyCode());

        verify(bookingRepository, times(1)).save(booking);
        verify(userRepository, times(1)).save(guest);
        verify(villaService, times(1)).updateVillaStatuses(villaId, "OCCUPIED", "CLEANED");
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
}
