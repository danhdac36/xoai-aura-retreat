package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.billing.exception.PendingOrdersExistException;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.PaymentRepository;
import com.AuraMoon.auramoon.billing.service.BillingService;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final GuestFolioRepository guestFolioRepository;
    private final FolioItemRepository folioItemRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;

    @Override
    public CheckoutViewDTO getCheckoutData(Integer bookingId) {
        GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("GuestFolio not found for bookingId: " + bookingId));

        List<FolioItem> items = folioItemRepository.findByGuestFolioId(folio.getId());
        Map<String, List<FolioItem>> groupedServices = items.stream()
                .collect(Collectors.groupingBy(item -> item.getServiceCategory() != null ? item.getServiceCategory() : "Khác"));

        BigDecimal totalExtra = items.stream()
                .map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Payment> payments = paymentRepository.findByGuestFolioIdAndStatus(folio.getId(), "SUCCESS");
        BigDecimal totalPaid = payments.stream()
                .map(payment -> payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal packageAmount = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
        BigDecimal totalCost = packageAmount.add(totalExtra);
        BigDecimal balanceDue = totalCost.subtract(totalPaid);

        return CheckoutViewDTO.builder()
                .folio(folio)
                .payments(payments)
                .totalPaid(totalPaid)
                .groupedExtraServices(groupedServices)
                .totalCost(totalCost)
                .balanceDue(balanceDue)
                .build();
    }

    @Override
    @Transactional
    public Payment initiatePayment(Integer bookingId, String paymentMethod, String paymentGateway) {
        CheckoutViewDTO data = getCheckoutData(bookingId);
        GuestFolio folio = data.getFolio();

        // Kiểm tra BR-12: Có đơn PENDING hoặc PREPARING không
        boolean hasPending = folioItemRepository.existsByGuestFolioIdAndStatusIn(
                folio.getId(), Arrays.asList("PENDING", "PREPARING"));
        
        if (hasPending) {
            throw new PendingOrdersExistException("Cannot checkout because there are pending Spa or F&B orders.");
        }

        Payment payment = Payment.builder()
                .guestFolio(folio)
                .amount(data.getBalanceDue())
                .paymentMethod(paymentMethod)
                .paymentGateway(paymentGateway)
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void completePaymentAndCheckout(Integer paymentId, String transactionCode) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("SUCCESS");
        payment.setTransactionCode(transactionCode);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        GuestFolio folio = payment.getGuestFolio();
        folio.setStatus("PAID");
        guestFolioRepository.save(folio);

        Booking booking = bookingRepository.findById(folio.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setBookingStatus("COMPLETED");
        booking.setPaymentStatus("PAID");
        bookingRepository.save(booking);

        if (booking.getAssignedVilla() != null) {
            Villa villa = booking.getAssignedVilla();
            villa.setVillaStatus("VACANT_NEEDS_CLEANING");
            villaRepository.save(villa);
        }
    }

    @Override
    @Transactional
    public void markPaymentAsFailed(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("FAILED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
