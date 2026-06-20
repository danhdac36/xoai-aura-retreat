package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.exception.BookingNotFoundException;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.CheckInService;
import com.AuraMoon.auramoon.booking.service.VillaService;
import com.AuraMoon.auramoon.common.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final UserRepository userRepository;
    private final VillaService villaService;

    private static final java.util.logging.Logger auditLogger =
            java.util.logging.Logger.getLogger(CheckInServiceImpl.class.getName());

    @Override
    @Transactional
    public void performCheckIn(CheckInRequestDTO request) {

        // 1. Load Booking — BOOK-404 nếu không tìm thấy
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(
                        "[BOOK-404] Không tìm thấy đơn đặt phòng với ID: " + request.getBookingId()));

        // 2. Kiểm tra trạng thái Booking — chỉ CONFIRMED mới được check-in (BR Upstream)
        if (!BookingStatus.CONFIRMED.name().equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalStateException(
                    "[BOOK-400] Đơn đặt phòng #" + booking.getId() + " không ở trạng thái CONFIRMED (hiện tại: " + booking.getBookingStatus() + ")");
        }

        // 3. Load Villa — BOOK-411 nếu không tìm thấy
        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "[BOOK-411] Không tìm thấy biệt thự với ID: " + request.getVillaId()));

        // 4. Kiểm tra trạng thái Villa — chỉ AVAILABLE mới được gán (BR-03)
        if (!"AVAILABLE".equals(villa.getVillaStatus())) {
            throw new com.AuraMoon.auramoon.booking.exception.InvalidVillaAssignmentException(
                    "[BOOK-411] Biệt thự " + villa.getVillaCode() + " không khả dụng (trạng thái hiện tại: " + villa.getVillaStatus() + ")");
        }

        // 5. Kiểm tra sức chứa — BOOK-412 nếu không đủ (ADR-UC08-002)
        if (booking.getTotalGuests() != null && villa.getLimitPerson() != null
                && booking.getTotalGuests() > villa.getLimitPerson()) {
            throw new com.AuraMoon.auramoon.booking.exception.InvalidVillaAssignmentException(
                    "[BOOK-412] Biệt thự được chọn (sức chứa " + villa.getLimitPerson() + " người) không đủ chỗ cho đoàn khách (" + booking.getTotalGuests() + " người)!");
        }

        // 6. Load Guest — BOOK-413 nếu không tìm thấy
        User guest = userRepository.findById(booking.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "[BOOK-413] Không tìm thấy thông tin khách hàng với ID: " + booking.getGuestId()));

        // 7. Gán CCCD trực tiếp — JPA (AesDataEncryptor) tự động mã hóa trước khi lưu (BR-09, ADR-001)
        guest.setIdentifyCode(request.getIdentifyCode());

        // 8. Cập nhật trạng thái Booking và gán phòng (BR-02)
        booking.setBookingStatus(BookingStatus.CHECKED_IN.name());
        booking.setAssignedVilla(villa);

        // 9. Lưu thay đổi
        bookingRepository.save(booking);
        userRepository.save(guest);

        // 10. Cập nhật trạng thái Villa — OCCUPIED + CLEAN (State Machine UC08 - 6.3)
        villaService.updateVillaStatuses(villa.getId(), "OCCUPIED", "CLEAN");

        // 11. Ghi Audit Log đầy đủ (BR-15) — TUYỆT ĐỐI KHÔNG ghi identifyCode
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = (auth != null) ? auth.getName() : "SYSTEM";
        auditLogger.info("AUDIT LOG: [Check-In Successful]" +
                " | Booking ID: " + booking.getId() +
                " | Guest ID: " + guest.getId() +
                " | Physical Villa assigned: " + villa.getVillaCode() +
                " | Performed by: " + performedBy +
                " | Timestamp: " + Instant.now());
    }
}
