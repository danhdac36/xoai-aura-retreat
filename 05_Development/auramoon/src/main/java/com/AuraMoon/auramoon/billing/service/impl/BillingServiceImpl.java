package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.common.enums.PaymentTransactionStatus;
import com.AuraMoon.auramoon.common.enums.GuestFolioStatus;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.PaymentRepository;
import com.AuraMoon.auramoon.billing.service.BillingService;
import com.AuraMoon.auramoon.billing.exception.PendingOrdersExistException;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AuraMoon.auramoon.auth.repository.IUserRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.billing.dto.CheckoutCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        private final IUserRepository userRepository;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        public CheckoutViewDTO getCheckoutData(Integer bookingId) {
                GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new RuntimeException(
                                                "GuestFolio not found for bookingId: " + bookingId));

                List<FolioItem> items = folioItemRepository.findByGuestFolioId(folio.getId());
                Map<String, List<FolioItem>> groupedServices = items.stream()
                                .collect(Collectors
                                                .groupingBy(item -> item.getServiceCategory() != null
                                                                ? item.getServiceCategory()
                                                                : "Khác"));

                BigDecimal totalExtra = items.stream()
                                .map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<Payment> payments = paymentRepository.findByGuestFolioIdAndStatus(folio.getId(),
                                PaymentTransactionStatus.SUCCESS.name());
                BigDecimal totalPaid = payments.stream()
                                .map(payment -> payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal packageAmount = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount()
                                : BigDecimal.ZERO;
                BigDecimal totalCost = packageAmount.add(totalExtra);
                BigDecimal balanceDue = totalCost.subtract(totalPaid);

                return CheckoutViewDTO.builder()
                                .folio(folio)
                                .payments(payments)
                                .groupedExtraServices(groupedServices)
                                .totalCost(totalCost)
                                .totalPaid(totalPaid)
                                .balanceDue(balanceDue)
                                .build();
        }

        @Override
        @Transactional
        public Payment initiatePayment(Integer bookingId, String method, String gateway) {
                GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new RuntimeException("Folio not found"));

                List<FolioItem> items = folioItemRepository.findByGuestFolioId(folio.getId());
                boolean hasPendingOrders = items.stream()
                                .anyMatch(item -> "PENDING".equalsIgnoreCase(item.getStatus()));
                if (hasPendingOrders) {
                        throw new PendingOrdersExistException(
                                        "Khách không thể check-out vì còn đơn Spa/F&B đang chờ xử lý.");
                }

                CheckoutViewDTO data = getCheckoutData(bookingId);
                BigDecimal amountToPay = data.getBalanceDue();

                if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("No balance due");
                }

                Payment payment = Payment.builder()
                                .guestFolio(folio)
                                .amount(amountToPay)
                                .paymentMethod(method)
                                .paymentGateway(gateway)
                                .paymentDate(LocalDateTime.now())
                                .status(PaymentTransactionStatus.PENDING.name())
                                .build();

                return paymentRepository.save(payment);
        }

        @Override
        @Transactional
        public void completePaymentAndCheckout(Integer paymentId, String transactionCode) {
                Payment payment = paymentRepository.findById(paymentId)
                                .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setStatus(PaymentTransactionStatus.SUCCESS.name());
                payment.setTransactionCode(transactionCode);
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);

                GuestFolio folio = payment.getGuestFolio();
                folio.setStatus(GuestFolioStatus.CLOSED.name());
                guestFolioRepository.save(folio);

                Booking booking = bookingRepository.findById(folio.getBookingId())
                                .orElseThrow(() -> new RuntimeException("Booking not found"));
                booking.setBookingStatus("CHECKED_OUT");
                booking.setPaymentStatus("PAID");
                booking.setCheckoutDate(LocalDateTime.now());
                bookingRepository.save(booking);

                if (booking.getAssignedVilla() != null) {
                        Villa villa = booking.getAssignedVilla();
                        villa.setVillaStatus("AVAILABLE");
                        villa.setCleaningStatus("DIRTY");
                        villaRepository.save(villa);
                }

                User guest = userRepository.findById(booking.getGuestId()).orElse(null);
                String guestEmail = (guest != null) ? guest.getEmail() : null;
                eventPublisher.publishEvent(
                                new CheckoutCompletedEvent(this, booking.getId(), guestEmail, folio.getId()));
        }

        @Override
        @Transactional
        public void completeCheckoutWithoutPayment(Integer bookingId) {
                GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new RuntimeException("Folio not found"));

                folio.setStatus(GuestFolioStatus.CLOSED.name());
                guestFolioRepository.save(folio);

                Booking booking = bookingRepository.findById(folio.getBookingId())
                                .orElseThrow(() -> new RuntimeException("Booking not found"));
                booking.setBookingStatus("CHECKED_OUT");
                booking.setPaymentStatus("PAID");
                booking.setCheckoutDate(LocalDateTime.now());
                bookingRepository.save(booking);

                if (booking.getAssignedVilla() != null) {
                        Villa villa = booking.getAssignedVilla();
                        villa.setVillaStatus("AVAILABLE");
                        villa.setCleaningStatus("DIRTY");
                        villaRepository.save(villa);
                }

                User guest = userRepository.findById(booking.getGuestId()).orElse(null);
                String guestEmail = (guest != null) ? guest.getEmail() : null;
                eventPublisher.publishEvent(
                                new CheckoutCompletedEvent(this, booking.getId(), guestEmail, folio.getId()));
        }

        @Override
        @Transactional
        public void markPaymentAsFailed(Integer paymentId) {
                Payment payment = paymentRepository.findById(paymentId)
                                .orElseThrow(() -> new RuntimeException("Payment not found"));
                payment.setStatus(PaymentTransactionStatus.FAILED.name());
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);
        }

        @Override
        @Transactional(readOnly = true)
        public Payment getPaymentById(Integer paymentId) {
                Payment payment = paymentRepository.findById(paymentId)
                                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
                // Initialize lazy association
                if (payment.getGuestFolio() != null) {
                        payment.getGuestFolio().getBookingId();
                }
                return payment;
        }
}
