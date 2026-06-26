package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.booking.dto.BookingRequestDTO;
import com.AuraMoon.auramoon.booking.dto.BookingResponseDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.booking.exception.BookingNotFoundException;
import com.AuraMoon.auramoon.booking.exception.VillaNotAvailableException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import com.AuraMoon.auramoon.booking.service.BookingService;
import com.AuraMoon.auramoon.booking.service.VillaService;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.entity.Consent;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final RetreatPackageRepository retreatPackageRepository;
        private final VillaTypeRepository villaTypeRepository;
        private final GuestFolioRepository guestFolioRepository;
        private final VillaService villaService;
        private final UserRepository userRepository;
        private final ConsentRepository consentRepository;
        private final TreatmentBookingRepository treatmentBookingRepository;
        private final TreatmentServiceRepository treatmentServiceRepository;

        @Override
        @Transactional
        public BookingResponseDTO createBooking(Integer guestId, BookingRequestDTO request) {
                if (request.getRetreatPackageId() == null) {
                        throw new IllegalArgumentException("Vui lòng chọn gói trị liệu.");
                }
                if (request.getVillaTypeId() == null) {
                        throw new IllegalArgumentException("Vui lòng chọn loại biệt thự lưu trú.");
                }

                if (guestId != null) {
                        java.util.List<String> activeStatuses = java.util.Arrays.asList("PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED-IN");
                        boolean hasActive = bookingRepository.existsByGuestIdAndBookingStatusIn(guestId, activeStatuses);
                        if (hasActive) {
                                throw new IllegalStateException("Bạn đang có một kỳ nghỉ chưa hoàn tất. Không thể đặt thêm gói mới!");
                        }
                }

                RetreatPackage retreatPackage = retreatPackageRepository
                                .findByIdAndIsActiveTrueAndIsDeleteFalse(request.getRetreatPackageId())
                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói trị liệu với ID: "
                                                + request.getRetreatPackageId()));

                VillaType villaType = villaTypeRepository.findById(request.getVillaTypeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Không tìm thấy loại biệt thự với ID: " + request.getVillaTypeId()));

                LocalDateTime checkinDate = request.getCheckinDate();
                LocalDateTime expectedCheckoutDate = checkinDate.plusDays(retreatPackage.getDurationDays());

                boolean isAvailable = villaService.checkVillaAvailability(request.getVillaTypeId(), checkinDate,
                                expectedCheckoutDate);
                if (!isAvailable) {
                        throw new VillaNotAvailableException(
                                        "Loại biệt thự đã chọn không còn phòng trống trong thời gian này.");
                }

                Booking booking = Booking.builder()
                                .guestId(guestId)
                                .retreatPackage(retreatPackage)
                                .checkinDate(checkinDate)
                                .checkoutDate(null)
                                .totalGuests(request.getTotalGuests())
                                .bookingStatus("PENDING")
                                .paymentStatus("UNPAID")
                                .build();

                Booking savedBooking = bookingRepository.save(booking);

                // Save privacy consent if provided during booking
                if (guestId != null && Boolean.TRUE.equals(request.getPrivacyConsent())) {
                        User guest = userRepository.findById(guestId)
                                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng với ID: " + guestId));
                        Consent consent = Consent.builder()
                                        .user(guest)
                                        .consentStatus(true)
                                        .consentVersion("v1.0")
                                        .build();
                        consentRepository.save(consent);
                }

                // Create GuestFolio immediately so that payment and deposits can reference it
                GuestFolio guestFolio = GuestFolio.builder()
                                .bookingId(savedBooking.getId())
                                .totalPackageAmount(savedBooking.getRetreatPackage().getPrice()
                                                .multiply(BigDecimal.valueOf(savedBooking.getTotalGuests()))
                                                .add(BigDecimal.valueOf(retreatPackage.getDurationDays())
                                                                .multiply(villaType.getPricePerDay())))
                                .totalExtraFb(BigDecimal.ZERO)
                                .finalAmount(savedBooking.getRetreatPackage().getPrice()
                                                .multiply(BigDecimal.valueOf(savedBooking.getTotalGuests()))
                                                .add(BigDecimal.valueOf(retreatPackage.getDurationDays())
                                                                .multiply(villaType.getPricePerDay())))
                                .status("OPEN")
                                .build();
                guestFolioRepository.save(guestFolio);

                return BookingResponseDTO.builder()
                                .bookingId(savedBooking.getId())
                                .guestId(savedBooking.getGuestId())
                                .checkinDate(savedBooking.getCheckinDate())
                                .checkoutDate(savedBooking.getCheckoutDate())
                                .totalGuests(savedBooking.getTotalGuests())
                                .bookingStatus(savedBooking.getBookingStatus())
                                .paymentStatus(savedBooking.getPaymentStatus())
                                .retreatPackageName(savedBooking.getRetreatPackage().getPackageName())
                                .assignedVillaCode(savedBooking.getAssignedVilla() != null
                                                ? savedBooking.getAssignedVilla().getVillaCode()
                                                : null)
                                .build();
        }

        @Override
        @Transactional
        public void confirmPayment(Integer bookingId, String transactionCode) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new BookingNotFoundException(
                                                "Không tìm thấy đơn đặt phòng với ID: " + bookingId));

                if ("CONFIRMED".equals(booking.getBookingStatus())) return;

                booking.setBookingStatus("CONFIRMED");
                booking.setPaymentStatus("PARTIAL");
                bookingRepository.save(booking);

                // Update GuestFolio status to "OPEN" upon successful deposit payment
                GuestFolio guestFolio = guestFolioRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new RuntimeException(
                                                "GuestFolio not found for bookingId: " + bookingId));
                guestFolio.setStatus("OPEN");
                guestFolioRepository.save(guestFolio);

                // Auto create TreatmentBooking (Spa Ticket) for the guest with default active service
                int durationDays = booking.getRetreatPackage().getDurationDays() != null 
                        ? booking.getRetreatPackage().getDurationDays() : 1;
                
                treatmentServiceRepository.findAll().stream()
                        .filter(s -> Boolean.TRUE.equals(s.getIsAvailable()) && Boolean.FALSE.equals(s.getIsDelete()))
                        .findFirst()
                        .ifPresent(service -> {
                            for (int i = 0; i < durationDays; i++) {
                                TreatmentBooking tb = new TreatmentBooking();
                                tb.setBookingId(bookingId);
                                tb.setTreatmentService(service);
                                tb.setStatus("PENDING");
                                tb.setIsDelete(false);
                                treatmentBookingRepository.save(tb);
                            }
                        });
        }

        @Override
        public boolean hasActiveBooking(Integer guestId) {
                if (guestId == null) return false;
                java.util.List<String> activeStatuses = java.util.Arrays.asList("PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED-IN");
                return bookingRepository.existsByGuestIdAndBookingStatusIn(guestId, activeStatuses);
        }
}
