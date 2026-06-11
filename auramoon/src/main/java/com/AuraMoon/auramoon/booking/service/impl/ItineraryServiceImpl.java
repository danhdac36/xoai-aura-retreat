package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public ItineraryTimelineDTO getTimelineForGuest(Integer guestId) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + guestId));

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("Khách hàng chưa có bất kỳ lịch đặt phòng nào.");
        }

        // Chọn Booking mới nhất hoặc đang hoạt động (Checked-in / Confirmed)
        Booking activeBooking = bookings.stream()
                .filter(b -> "CHECKED-IN".equals(b.getBookingStatus()) || "CONFIRMED".equals(b.getBookingStatus()))
                .findFirst()
                .orElse(bookings.get(bookings.size() - 1));

        List<ItineraryTimelineDTO.TimelineEvent> events = new ArrayList<>();

        LocalDate start = activeBooking.getCheckinDate();
        LocalDate end = activeBooking.getCheckoutDate();

        // 1. Nhận phòng (Check-in) vào ngày đầu tiên lúc 14:00
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Nhận phòng (Check-in)")
                .time(start.atTime(14, 0))
                .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
                .build());

        // 2. Tạo các sự kiện lặp lại hàng ngày trong suốt kỳ nghỉ
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // Yoga buổi sáng lúc 06:30 (trừ ngày nhận phòng)
            if (!date.equals(start)) {
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Luyện tập Yoga sáng")
                        .time(date.atTime(6, 30))
                        .description("Tập Yoga khởi động ngày mới tràn đầy năng lượng tại bãi biển.")
                        .build());
            }

            // Bữa trưa dinh dưỡng lúc 12:00 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Bữa trưa dinh dưỡng")
                        .time(date.atTime(12, 0))
                        .description("Thưởng thức bữa trưa thanh lọc theo chế độ ăn uống khoa học.")
                        .build());
            }

            // Trị liệu Spa lúc 15:30 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Trị liệu Spa phục hồi")
                        .time(date.atTime(15, 30))
                        .description("Buổi trị liệu massage tinh dầu giải độc tại phòng trị liệu Spa.")
                        .build());
            }

            // Bữa tối dinh dưỡng lúc 18:30 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Bữa tối dinh dưỡng")
                        .time(date.atTime(18, 30))
                        .description("Bữa tối nhẹ nhàng kết hợp trà thảo mộc thư giãn.")
                        .build());
            }
        }

        // 3. Trả phòng (Check-out) vào ngày cuối cùng lúc 12:00
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Trả phòng (Check-out)")
                .time(end.atTime(12, 0))
                .description("Hoàn tất thủ tục thanh toán Folio và check-out phòng.")
                .build());

        // Sắp xếp các sự kiện theo trình tự thời gian tăng dần
        events.sort(Comparator.comparing(ItineraryTimelineDTO.TimelineEvent::getTime));

        return ItineraryTimelineDTO.builder()
                .bookingId(activeBooking.getId())
                .guestName(guest.getFullName())
                .events(events)
                .build();
    }
}

