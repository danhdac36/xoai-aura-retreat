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
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ItineraryTimelineDTO getTimelineForGuest(Integer guestId) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + guestId));

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("Khách hàng chưa có bất kỳ lịch đặt phòng nào.");
        }

        // Chọn Booking mới nhất hoặc đang hoạt động (Checked-in / Confirmed)
        Booking activeBooking = bookings.stream()
                .filter(b -> "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus()) 
                        || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus()) 
                        || "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()))
                .findFirst()
                .orElse(bookings.get(bookings.size() - 1));

        List<ItineraryTimelineDTO.TimelineEvent> events = new ArrayList<>();

        LocalDate start = activeBooking.getCheckinDate();
        LocalDate end = activeBooking.getCheckoutDate();

        String packageType = "";
        if (activeBooking.getRetreatPackage() != null && activeBooking.getRetreatPackage().getTypePackage() != null) {
            packageType = activeBooking.getRetreatPackage().getTypePackage().trim().toLowerCase();
        }

        // 1. Nhận phòng (Check-in) vào ngày đầu tiên lúc 14:00
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Nhận phòng (Check-in)")
                .time(start.atTime(14, 0))
                .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
                .build());

        // 2. Tạo các sự kiện lặp lại hàng ngày trong suốt kỳ nghỉ
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // Hoạt động buổi sáng lúc 06:30 (trừ ngày nhận phòng)
            if (!date.equals(start)) {
                String eventName = "Luyện tập Yoga sáng";
                String eventDesc = "Tập Yoga khởi động ngày mới tràn đầy năng lượng tại bãi biển.";

                if (packageType.contains("stress")) {
                    eventName = "Thiền định & Thở chánh niệm";
                    eventDesc = "Tập thiền định sâu và các bài tập thở chánh niệm để làm dịu tâm trí, giảm bớt căng thẳng tích tụ.";
                } else if (packageType.contains("detox") || packageType.contains("weight") || packageType.contains("béo") || packageType.contains("cân")) {
                    eventName = "Vận động Cardio nhẹ nhàng";
                    eventDesc = "Hoạt động đi bộ nhanh hoặc các bài tập vận động thể chất nhẹ nhàng để kích hoạt quá trình trao đổi chất.";
                }

                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName(eventName)
                        .time(date.atTime(6, 30))
                        .description(eventDesc)
                        .build());
            }

            // Bữa trưa dinh dưỡng lúc 12:00 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                String eventName = "Bữa trưa dinh dưỡng";
                String eventDesc = "Thưởng thức bữa trưa thanh lọc theo chế độ ăn uống khoa học.";

                if (packageType.contains("stress")) {
                    eventName = "Bữa trưa thanh đạm giải tỏa căng thẳng";
                    eventDesc = "Thực đơn dinh dưỡng lành mạnh đặc biệt, hạn chế tối đa caffeine giúp xoa dịu thần kinh.";
                } else if (packageType.contains("detox") || packageType.contains("weight") || packageType.contains("béo") || packageType.contains("cân")) {
                    eventName = "Bữa trưa Detox & Ít calorie";
                    eventDesc = "Bữa trưa dinh dưỡng chuyên biệt, giàu chất xơ và vitamin giúp đào thải độc tố và hỗ trợ giảm cân.";
                }

                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName(eventName)
                        .time(date.atTime(12, 0))
                        .description(eventDesc)
                        .build());
            }

            // Trị liệu Spa lúc 15:30 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                String eventName = "Trị liệu Spa phục hồi";
                String eventDesc = "Buổi trị liệu massage tinh dầu giải độc tại phòng trị liệu Spa.";

                if (packageType.contains("stress")) {
                    eventName = "Trị liệu Spa giấc ngủ sâu";
                    eventDesc = "Liệu trình massage Aromatherapy với tinh dầu oải hương, kết hợp xông hơi đá nóng phục hồi giấc ngủ.";
                } else if (packageType.contains("detox") || packageType.contains("weight") || packageType.contains("béo") || packageType.contains("cân")) {
                    eventName = "Trị liệu Spa thải độc chuyên sâu";
                    eventDesc = "Massage phục hồi mô sâu (Deep tissue) kết hợp liệu pháp tắm bùn khoáng nóng giải trừ độc tố cơ thể.";
                }

                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName(eventName)
                        .time(date.atTime(15, 30))
                        .description(eventDesc)
                        .build());
            }

            // Bữa tối dinh dưỡng lúc 18:30 (trừ ngày trả phòng)
            if (!date.equals(end)) {
                String eventName = "Bữa tối dinh dưỡng";
                String eventDesc = "Bữa tối nhẹ nhàng kết hợp trà thảo mộc thư giãn.";

                if (packageType.contains("stress")) {
                    eventName = "Thưởng trà trị liệu & Thư giãn";
                    eventDesc = "Trải nghiệm thưởng trà thảo mộc organic kết hợp nhạc trị liệu tần số cao (Sound healing) giúp thư giãn tinh thần.";
                } else if (packageType.contains("detox") || packageType.contains("weight") || packageType.contains("béo") || packageType.contains("cân")) {
                    eventName = "Nước ép thanh lọc & Soup nhẹ";
                    eventDesc = "Bữa tối nhẹ nhàng thanh mát với soup dinh dưỡng và nước ép hữu cơ detox phục hồi cơ thể.";
                }

                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName(eventName)
                        .time(date.atTime(18, 30))
                        .description(eventDesc)
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

