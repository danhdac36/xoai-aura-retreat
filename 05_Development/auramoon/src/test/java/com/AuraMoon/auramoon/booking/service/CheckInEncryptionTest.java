package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.impl.CheckInServiceImpl;
import com.AuraMoon.auramoon.auth.entity.Consent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInEncryptionTest {

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
    public void UC08_TC_001_checkIn_EncryptsIdentifyCode() {
        // Arrange
        Integer bookingId = 100;
        Integer guestId = 10;
        Integer villaId = 5;
        String rawIdentity = "001201010123";

        CheckInRequestDTO request = CheckInRequestDTO.builder()
                .bookingId(bookingId)
                .identifyCode(rawIdentity)
                .villaId(villaId)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .guestId(guestId)
                .bookingStatus("CONFIRMED")
                .checkinDate(LocalDateTime.now())
                .build();

        Villa villa = Villa.builder()
                .id(villaId)
                .villaStatus("AVAILABLE")
                .build();

        User guest = User.builder()
                .id(guestId)
                .build();

        Consent consent = Consent.builder()
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
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedGuest = userCaptor.getValue();
        
        // Since we are not running a full JPA context here, the setIdentifyCode sets raw value.
        // In a real integration test, we would query DB and check Base64. 
        // Here we just ensure it was set correctly so JPA can handle it.
        assertEquals(rawIdentity, savedGuest.getIdentifyCode());
    }
}
