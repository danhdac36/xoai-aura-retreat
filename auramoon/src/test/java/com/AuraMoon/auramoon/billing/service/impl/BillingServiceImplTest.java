package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.PaymentRepository;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private VillaRepository villaRepository;

    @InjectMocks
    private BillingServiceImpl billingService;

    @Test
    @DisplayName("BIL21-TC-001 - Tổng hợp hóa đơn gộp thành công")
    void getCheckoutData_Success_CalculatesCorrectly() {
        // Arrange
        Integer bookingId = 1;
        GuestFolio folio = new GuestFolio();
        folio.setId(1);
        folio.setBookingId(bookingId);
        folio.setTotalPackageAmount(new BigDecimal("5000000"));

        FolioItem item1 = new FolioItem();
        item1.setServiceCategory("Extra Spa");
        item1.setAmount(new BigDecimal("500000"));

        FolioItem item2 = new FolioItem();
        item2.setServiceCategory("Extra F&B");
        item2.setAmount(new BigDecimal("200000"));

        Payment payment = new Payment();
        payment.setStatus("SUCCESS");
        payment.setAmount(new BigDecimal("2000000"));

        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(folio));
        when(folioItemRepository.findByGuestFolioId(1)).thenReturn(Arrays.asList(item1, item2));
        when(paymentRepository.findByGuestFolioIdAndStatus(1, "SUCCESS"))
                .thenReturn(Collections.singletonList(payment));

        // Act
        CheckoutViewDTO result = billingService.getCheckoutData(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(folio, result.getFolio());
        assertTrue(result.getGroupedExtraServices().containsKey("Extra Spa"));
        assertTrue(result.getGroupedExtraServices().containsKey("Extra F&B"));
        assertEquals(new BigDecimal("5700000"), result.getTotalCost());
        assertEquals(new BigDecimal("2000000"), result.getTotalPaid());
        assertEquals(new BigDecimal("3700000"), result.getBalanceDue());
    }

    @Test
    @DisplayName("BIL21-TC-002 - BookingId không tồn tại -> Exception")
    void getCheckoutData_BookingIdNotFound_ThrowsException() {
        // Arrange
        Integer bookingId = 999;
        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            billingService.getCheckoutData(bookingId);
        });

        assertTrue(exception.getMessage().contains("GuestFolio not found"));
    }

    @Test
    @DisplayName("BIL21-TC-003 - Folio không có FolioItem -> Tính toán đúng package duy nhất")
    void getCheckoutData_NoFolioItems_CalculatesCorrectly() {
        // Arrange
        Integer bookingId = 1;
        GuestFolio folio = new GuestFolio();
        folio.setId(1);
        folio.setBookingId(bookingId);
        folio.setTotalPackageAmount(new BigDecimal("5000000"));

        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(folio));
        when(folioItemRepository.findByGuestFolioId(1)).thenReturn(Collections.emptyList());
        when(paymentRepository.findByGuestFolioIdAndStatus(1, "SUCCESS")).thenReturn(Collections.emptyList());

        // Act
        CheckoutViewDTO result = billingService.getCheckoutData(bookingId);

        // Assert
        assertTrue(result.getGroupedExtraServices().isEmpty());
        assertEquals(new BigDecimal("5000000"), result.getTotalCost());
        assertEquals(BigDecimal.ZERO, result.getTotalPaid());
        assertEquals(new BigDecimal("5000000"), result.getBalanceDue());
    }

    @Test
    @DisplayName("BIL21-TC-004 - Gom nhóm FolioItem theo serviceCategory chính xác")
    void getCheckoutData_GroupByCategory_GroupsCorrectly() {
        // Arrange
        Integer bookingId = 1;
        GuestFolio folio = new GuestFolio();
        folio.setId(1);
        folio.setBookingId(bookingId);
        folio.setTotalPackageAmount(new BigDecimal("1000000"));

        FolioItem spa1 = new FolioItem();
        spa1.setServiceCategory("Extra Spa");
        spa1.setAmount(BigDecimal.TEN);

        FolioItem spa2 = new FolioItem();
        spa2.setServiceCategory("Extra Spa");
        spa2.setAmount(BigDecimal.TEN);

        FolioItem fb = new FolioItem();
        fb.setServiceCategory("Extra F&B");
        fb.setAmount(BigDecimal.TEN);

        FolioItem other = new FolioItem();
        other.setServiceCategory(null); // Should be grouped under "Khác"
        other.setAmount(BigDecimal.TEN);

        when(guestFolioRepository.findByBookingId(bookingId)).thenReturn(Optional.of(folio));
        when(folioItemRepository.findByGuestFolioId(1)).thenReturn(Arrays.asList(spa1, spa2, fb, other));
        when(paymentRepository.findByGuestFolioIdAndStatus(1, "SUCCESS")).thenReturn(Collections.emptyList());

        // Act
        CheckoutViewDTO result = billingService.getCheckoutData(bookingId);

        // Assert
        assertEquals(2, result.getGroupedExtraServices().get("Extra Spa").size());
        assertEquals(1, result.getGroupedExtraServices().get("Extra F&B").size());
        assertEquals(1, result.getGroupedExtraServices().get("Khác").size());
    }
}
