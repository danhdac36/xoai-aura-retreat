package com.AuraMoon.auramoon.billing.service.impl;

import com.AuraMoon.auramoon.billing.dto.CheckoutViewDTO;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.entity.Payment;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.repository.PaymentRepository;
import com.AuraMoon.auramoon.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final GuestFolioRepository guestFolioRepository;
    private final FolioItemRepository folioItemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public CheckoutViewDTO getCheckoutData(Integer bookingId) {
        // 1. Lấy GuestFolio bằng bookingId
        GuestFolio folio = guestFolioRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("GuestFolio not found for bookingId: " + bookingId));

        // 2. Lấy toàn bộ FolioItem và gom nhóm tự động theo serviceCategory
        List<FolioItem> items = folioItemRepository.findByGuestFolioId(folio.getId());
        Map<String, List<FolioItem>> groupedServices = items.stream()
                .collect(Collectors.groupingBy(item -> item.getServiceCategory() != null ? item.getServiceCategory() : "Khác"));

        BigDecimal totalExtra = items.stream()
                .map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Lấy toàn bộ Payment thành công
        List<Payment> payments = paymentRepository.findByGuestFolioIdAndStatus(folio.getId(), "SUCCESS");
        BigDecimal totalPaid = payments.stream()
                .map(payment -> payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tính toán tổng chi phí và nợ còn lại
        BigDecimal packageAmount = folio.getTotalPackageAmount() != null ? folio.getTotalPackageAmount() : BigDecimal.ZERO;
        BigDecimal totalCost = packageAmount.add(totalExtra);
        BigDecimal balanceDue = totalCost.subtract(totalPaid);

        // 5. Đóng gói vào DTO
        return CheckoutViewDTO.builder()
                .folio(folio)
                .payments(payments)
                .totalPaid(totalPaid)
                .groupedExtraServices(groupedServices)
                .totalCost(totalCost)
                .balanceDue(balanceDue)
                .build();
    }
}
