package com.AuraMoon.auramoon.booking.service;

import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.dto.BookingRequestDTO;
import com.AuraMoon.auramoon.booking.dto.BookingResponseDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.booking.exception.VillaNotAvailableException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import com.AuraMoon.auramoon.booking.service.impl.BookingServiceImpl;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.entity.Consent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

        @Mock
        private BookingRepository bookingRepository;

        @Mock
        private VillaRepository villaRepository;

        @Mock
        private VillaTypeRepository villaTypeRepository;

        @Mock
        private RetreatPackageRepository retreatPackageRepository;

        @Mock
        private GuestFolioRepository guestFolioRepository;

        @Mock
        private VillaService villaService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private ConsentRepository consentRepository;

        @InjectMocks
        private BookingServiceImpl bookingService;

        @Test
        @DisplayName("UC07-TC-001: Đặt gói và biệt thự thành công khi còn phòng trống")
        public void createBooking_villasAvailable_savesSuccessfully() {
                // Arrange
                Integer guestId = 1;
                LocalDateTime checkinDate = LocalDateTime.now().plusDays(2);
                BookingRequestDTO request = BookingRequestDTO.builder()
                                .retreatPackageId(1)
                                .villaTypeId(2)
                                .checkinDate(checkinDate)
                                .totalGuests(2)
                                .build();

                RetreatPackage retreatPackage = RetreatPackage.builder()
                                .id(1)
                                .packageName("Mindfulness Retreat")
                                .durationDays(3)
                                .price(BigDecimal.valueOf(15000000))
                                .isActive(true)
                                .build();

                VillaType villaType = VillaType.builder()
                                .id(2)
                                .typeName("Ocean View Villa")
                                .pricePerDay(BigDecimal.valueOf(5000000))
                                .build();

                when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(1))
                                .thenReturn(Optional.of(retreatPackage));
                when(villaTypeRepository.findById(2)).thenReturn(Optional.of(villaType));
                when(villaService.checkVillaAvailability(eq(2), eq(checkinDate), eq(checkinDate.plusDays(3))))
                                .thenReturn(true);

                Booking savedBooking = Booking.builder()
                                .id(100)
                                .guestId(guestId)
                                .retreatPackage(retreatPackage)
                                .checkinDate(checkinDate)
                                .checkoutDate(checkinDate.plusDays(3))
                                .totalGuests(2)
                                .bookingStatus("PENDING")
                                .paymentStatus("UNPAID")
                                .build();

                when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

                // Act
                BookingResponseDTO response = bookingService.createBooking(guestId, request);

                // Assert
                assertNotNull(response);
                assertEquals(100, response.getBookingId());
                assertEquals("PENDING", response.getBookingStatus());
                assertEquals("UNPAID", response.getPaymentStatus());
                assertEquals("Mindfulness Retreat", response.getRetreatPackageName());
                assertEquals(checkinDate.plusDays(3), response.getCheckoutDate());
                verify(bookingRepository, times(1)).save(any(Booking.class));
        }

        @Test
        @DisplayName("UC07-TC-002: Đặt gói thất bại và ném lỗi BOOK-002 khi hết phòng trống")
        public void createBooking_noVillasAvailable_throwsBook002() {
                // Arrange
                Integer guestId = 1;
                LocalDateTime checkinDate = LocalDateTime.now().plusDays(2);
                BookingRequestDTO request = BookingRequestDTO.builder()
                                .retreatPackageId(1)
                                .villaTypeId(2)
                                .checkinDate(checkinDate)
                                .totalGuests(2)
                                .build();

                RetreatPackage retreatPackage = RetreatPackage.builder()
                                .id(1)
                                .durationDays(3)
                                .isActive(true)
                                .build();

                VillaType villaType = VillaType.builder()
                                .id(2)
                                .build();

                when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(1))
                                .thenReturn(Optional.of(retreatPackage));
                when(villaTypeRepository.findById(2)).thenReturn(Optional.of(villaType));
                when(villaService.checkVillaAvailability(eq(2), eq(checkinDate), eq(checkinDate.plusDays(3))))
                                .thenReturn(false);

                // Act & Assert
                assertThrows(VillaNotAvailableException.class, () -> {
                        bookingService.createBooking(guestId, request);
                });
                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("UC07-TC-003: Xác nhận thanh toán thành công, cập nhật trạng thái đặt phòng và mở Folio nợ")
        public void confirmPayment_validBooking_updatesStatusAndCreatesFolio() {
                // Arrange
                Integer bookingId = 100;
                String transactionCode = "TX_99999";

                RetreatPackage retreatPackage = RetreatPackage.builder()
                                .id(1)
                                .price(BigDecimal.valueOf(15000000))
                                .build();

                Booking booking = Booking.builder()
                                .id(bookingId)
                                .guestId(1)
                                .retreatPackage(retreatPackage)
                                .bookingStatus("PENDING")
                                .paymentStatus("UNPAID")
                                .build();

                GuestFolio guestFolio = GuestFolio.builder()
                                .bookingId(bookingId)
                                .status("OPEN")
                                .build();

                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
                when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(guestFolio));

                // Act
                bookingService.confirmPayment(bookingId, transactionCode);

                // Assert
                assertEquals("CONFIRMED", booking.getBookingStatus());
                assertEquals("PARTIAL", booking.getPaymentStatus());
                verify(bookingRepository, times(1)).save(booking);
                verify(guestFolioRepository, times(1)).save(any(GuestFolio.class));
        }

        @Test
        @DisplayName("UC07-TC-004: Đặt gói thành công và tự động lưu đồng ý bảo mật của khách hàng")
        public void createBooking_withPrivacyConsent_savesConsent() {
                // Arrange
                Integer guestId = 1;
                LocalDateTime checkinDate = LocalDateTime.now().plusDays(2);
                BookingRequestDTO request = BookingRequestDTO.builder()
                                .retreatPackageId(1)
                                .villaTypeId(2)
                                .checkinDate(checkinDate)
                                .totalGuests(2)
                                .privacyConsent(true)
                                .build();

                RetreatPackage retreatPackage = RetreatPackage.builder()
                                .id(1)
                                .packageName("Mindfulness Retreat")
                                .durationDays(3)
                                .price(BigDecimal.valueOf(15000000))
                                .isActive(true)
                                .build();

                VillaType villaType = VillaType.builder()
                                .id(2)
                                .typeName("Ocean View Villa")
                                .pricePerDay(BigDecimal.valueOf(5000000))
                                .build();

                User guest = User.builder()
                                .id(guestId)
                                .email("guest@auramoon.com")
                                .build();

                when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(1))
                                .thenReturn(Optional.of(retreatPackage));
                when(villaTypeRepository.findById(2)).thenReturn(Optional.of(villaType));
                when(villaService.checkVillaAvailability(eq(2), eq(checkinDate), eq(checkinDate.plusDays(3))))
                                .thenReturn(true);
                when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));

                Booking savedBooking = Booking.builder()
                                .id(100)
                                .guestId(guestId)
                                .retreatPackage(retreatPackage)
                                .checkinDate(checkinDate)
                                .checkoutDate(checkinDate.plusDays(3))
                                .totalGuests(2)
                                .bookingStatus("PENDING")
                                .paymentStatus("UNPAID")
                                .build();

                when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

                // Act
                BookingResponseDTO response = bookingService.createBooking(guestId, request);

                // Assert
                assertNotNull(response);
                assertEquals(100, response.getBookingId());
                verify(bookingRepository, times(1)).save(any(Booking.class));
                verify(consentRepository, times(1)).save(any(Consent.class));
        }
}
