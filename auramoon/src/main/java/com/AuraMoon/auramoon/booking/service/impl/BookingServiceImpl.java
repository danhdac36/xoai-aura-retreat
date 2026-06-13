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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RetreatPackageRepository retreatPackageRepository;
    private final VillaTypeRepository villaTypeRepository;
    private final GuestFolioRepository guestFolioRepository;
    private final VillaService villaService;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(Integer guestId, BookingRequestDTO request) {
        RetreatPackage retreatPackage = retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(request.getRetreatPackageId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói trị liệu với ID: " + request.getRetreatPackageId()));

        VillaType villaType = villaTypeRepository.findById(request.getVillaTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại biệt thự với ID: " + request.getVillaTypeId()));

        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = checkinDate.plusDays(retreatPackage.getDurationDays());

        boolean isAvailable = villaService.checkVillaAvailability(request.getVillaTypeId(), checkinDate, checkoutDate);
        if (!isAvailable) {
            throw new VillaNotAvailableException("Loại biệt thự đã chọn không còn phòng trống trong thời gian này.");
        }

        Booking booking = Booking.builder()
                .guestId(guestId)
                .retreatPackage(retreatPackage)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .totalGuests(request.getTotalGuests())
                .bookingStatus("PENDING")
                .paymentStatus("UNPAID")
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingResponseDTO.builder()
                .bookingId(savedBooking.getId())
                .guestId(savedBooking.getGuestId())
                .checkinDate(savedBooking.getCheckinDate())
                .checkoutDate(savedBooking.getCheckoutDate())
                .totalGuests(savedBooking.getTotalGuests())
                .bookingStatus(savedBooking.getBookingStatus())
                .paymentStatus(savedBooking.getPaymentStatus())
                .retreatPackageName(savedBooking.getRetreatPackage().getPackageName())
                .assignedVillaCode(savedBooking.getAssignedVilla() != null ? savedBooking.getAssignedVilla().getVillaCode() : null)
                .build();
    }

    @Override
    @Transactional
    public void confirmPayment(Integer bookingId, String transactionCode) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Không tìm thấy đơn đặt phòng với ID: " + bookingId));

        booking.setBookingStatus("CONFIRMED");
        booking.setPaymentStatus("DEPOSITED");
        bookingRepository.save(booking);

        GuestFolio guestFolio = GuestFolio.builder()
                .bookingId(bookingId)
                .totalPackageAmount(booking.getRetreatPackage().getPrice())
                .totalExtraFb(BigDecimal.ZERO)
                .finalAmount(booking.getRetreatPackage().getPrice())
                .status("PENDING")
                .build();
        guestFolioRepository.save(guestFolio);
    }
}

