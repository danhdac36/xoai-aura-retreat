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
import com.AuraMoon.auramoon.common.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final VillaService villaService;

    @Override
    @Transactional
    public void performCheckIn(CheckInRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Không tìm thấy đơn đặt phòng với ID: " + request.getBookingId()));

        Villa villa = villaRepository.findById(request.getVillaId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Villa với ID: " + request.getVillaId()));

        User guest = userRepository.findById(booking.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + booking.getGuestId()));

        // Mã hóa thông tin CCCD/Passport
        String encryptedIdentity = encryptionService.encrypt(request.getIdentifyCode());
        guest.setIdentifyCode(encryptedIdentity);

        // Cập nhật trạng thái Booking và gán phòng
        booking.setBookingStatus("CHECKED-IN");
        booking.setAssignedVilla(villa);

        // Lưu thay đổi
        bookingRepository.save(booking);
        userRepository.save(guest);

        // Cập nhật trạng thái dọn dẹp và phòng ở của biệt thự vật lý
        villaService.updateVillaStatuses(villa.getId(), "OCCUPIED", "CLEANED");
    }
}

