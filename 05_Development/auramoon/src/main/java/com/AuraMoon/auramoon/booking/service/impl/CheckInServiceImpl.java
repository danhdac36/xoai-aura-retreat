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
import com.AuraMoon.auramoon.auth.entity.Consent;
import com.AuraMoon.auramoon.auth.repository.ConsentRepository;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final UserRepository userRepository;
    private final VillaService villaService;
    private final ConsentRepository consentRepository;
    private final PhysicalHealthProfileRepository healthProfileRepository;

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

        if (booking.getCheckinDate() != null && booking.getCheckinDate().toLocalDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException(
                    "[BOOK-400] Không thể check-in trước ngày nhận phòng thực tế (" + booking.getCheckinDate() + ")");
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

        // 6.1 Kiểm tra sự đồng ý bảo mật (Consent Check)
        if (Boolean.TRUE.equals(request.getPrivacyConsent())) {
            Consent newConsent = Consent.builder()
                    .user(guest)
                    .consentStatus(true)
                    .consentVersion("v1.0")
                    .build();
            consentRepository.save(newConsent);
        }

        Consent consent = consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(guest.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "[BOOK-400] Khách hàng chưa đồng ý điều khoản bảo mật dữ liệu cá nhân (CCCD)"));
        if (!Boolean.TRUE.equals(consent.getConsentStatus())) {
            throw new IllegalStateException(
                    "[BOOK-400] Khách hàng chưa đồng ý điều khoản bảo mật dữ liệu cá nhân (CCCD)");
        }

        // 7. Cập nhật thông tin cá nhân và số định danh của khách (JPA converter tự động mã hóa CCCD)
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            guest.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            guest.setPhone(request.getPhone().trim());
        }
        if (request.getGender() != null && !request.getGender().trim().isEmpty()) {
            guest.setGender(request.getGender().trim());
        }
        if (request.getDateOfBirth() != null) {
            guest.setDateOfBirth(request.getDateOfBirth());
        }
        guest.setIdentifyCode(request.getIdentifyCode());

        // 8. Cập nhật trạng thái Booking và gán phòng (BR-02)
        booking.setBookingStatus(BookingStatus.CHECKED_IN.name());
        booking.setAssignedVilla(villa);
        
        // 8.1 Cập nhật thời gian thực tế theo Chu kỳ 24h
        LocalDateTime now = LocalDateTime.now();
        booking.setCheckinDate(now);
        int durationDays = booking.getRetreatPackage() != null && booking.getRetreatPackage().getDurationDays() != null 
                            ? booking.getRetreatPackage().getDurationDays() : 1;
        booking.setCheckoutDate(now.plusDays(durationDays));

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

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<com.AuraMoon.auramoon.booking.dto.BookingDisplayDTO> getAllBookingsForDisplay() {
        java.util.List<Booking> bookings = bookingRepository.findAll();
        java.util.List<com.AuraMoon.auramoon.booking.dto.BookingDisplayDTO> dtos = new java.util.ArrayList<>();
        for (Booking b : bookings) {
            boolean consentApproved = consentRepository.findFirstByUser_IdOrderByUpdatedAtDesc(b.getGuestId())
                    .map(Consent::getConsentStatus)
                    .orElse(false);

            User guest = userRepository.findById(b.getGuestId()).orElse(null);
            String guestName = guest != null ? guest.getFullName() : null;
            String guestPhone = guest != null ? guest.getPhone() : null;
            String guestGender = guest != null ? guest.getGender() : null;
            LocalDate guestDateOfBirth = guest != null ? guest.getDateOfBirth() : null;

            String medicalConditions = null;
            String injuries = null;
            if (guest != null) {
                PhysicalHealthProfile hp = healthProfileRepository.findByUserId(guest.getId()).orElse(null);
                if (hp != null) {
                    medicalConditions = hp.getMedicalConditions();
                    injuries = hp.getInjuries();
                }
            }

            dtos.add(com.AuraMoon.auramoon.booking.dto.BookingDisplayDTO.builder()
                    .id(b.getId())
                    .guestId(b.getGuestId())
                    .packageName(b.getRetreatPackage() != null ? b.getRetreatPackage().getPackageName() : "N/A")
                    .checkinDate(b.getCheckinDate())
                    .checkoutDate(b.getCheckoutDate())
                    .assignedVillaCode(b.getAssignedVilla() != null ? b.getAssignedVilla().getVillaCode() : null)
                    .requestedVillaTypeName(b.getRequestedVillaType() != null ? b.getRequestedVillaType().getTypeName() : "Không xác định")
                    .bookingStatus(b.getBookingStatus())
                    .consentApproved(consentApproved)
                    .guestName(guestName)
                    .guestPhone(guestPhone)
                    .guestGender(guestGender)
                    .guestDateOfBirth(guestDateOfBirth)
                    .medicalConditions(medicalConditions)
                    .injuries(injuries)
                    .build());
        }
        return dtos;
    }
}
