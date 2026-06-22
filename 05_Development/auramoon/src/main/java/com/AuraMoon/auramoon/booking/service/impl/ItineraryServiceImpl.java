package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.dto.ItineraryTimelineDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.service.ItineraryService;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import com.AuraMoon.auramoon.fnb.repository.MealOrderRepository;
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
    private final ScheduleRepository scheduleRepository;
    private final MealOrderRepository mealOrderRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ItineraryTimelineDTO getTimelineForGuest(Integer guestId) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + guestId));

        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("Khách hàng chưa có bất kỳ lịch đặt phòng nào.");
        }

        Booking activeBooking = bookings.stream()
                .filter(b -> "CHECKED_IN".equalsIgnoreCase(b.getBookingStatus()) 
                        || "CHECKED-IN".equalsIgnoreCase(b.getBookingStatus()) 
                        || "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()))
                .findFirst()
                .orElse(bookings.get(bookings.size() - 1));

        List<ItineraryTimelineDTO.TimelineEvent> events = new ArrayList<>();

        LocalDate start = activeBooking.getCheckinDate();
        LocalDate end = activeBooking.getCheckoutDate();

        // 1. Nhận phòng (Check-in)
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Nhận phòng (Check-in)")
                .time(start.atTime(14, 0))
                .location("Sảnh Lễ tân")
                .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
                .build());

        // 2. Lấy dữ liệu Spa Scheduled thực tế
        List<Schedule> spaSchedules = scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(activeBooking.getId());
        for (Schedule schedule : spaSchedules) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Trị liệu: " + schedule.getTreatmentBooking().getTreatmentService().getServiceName())
                .time(schedule.getStartTime())
                .location(schedule.getRoom() != null ? schedule.getRoom().getRoomName() : "Aura Spa")
                .description("Liệu trình Spa thư giãn cơ thể.")
                .build());
        }

        // 3. Lấy dữ liệu Bữa ăn thực tế
        List<MealOrder> mealOrders = mealOrderRepository.findByBookingId(activeBooking.getId());
        for (MealOrder meal : mealOrders) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Bữa ăn Cá nhân hóa")
                .time(meal.getOrderedAt() != null ? meal.getOrderedAt() : start.atTime(12, 0))
                .location("Nhà hàng Thực dưỡng")
                .description("Bữa ăn theo Dietary Profile: " + (meal.getNote() != null ? meal.getNote() : "Thanh lọc cơ thể"))
                .build());
        }

        // 4. Trả phòng (Check-out)
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Trả phòng (Check-out)")
                .time(end.atTime(12, 0))
                .location("Sảnh Lễ tân")
                .description("Hoàn tất thủ tục thanh toán và check-out phòng.")
                .build());

        events.sort(Comparator.comparing(ItineraryTimelineDTO.TimelineEvent::getTime));

        return ItineraryTimelineDTO.builder()
                .bookingId(activeBooking.getId())
                .guestName(guest.getFullName())
                .packageName(activeBooking.getRetreatPackage() != null ? activeBooking.getRetreatPackage().getPackageName() : "Chưa đăng ký gói")
                .villaName(activeBooking.getAssignedVilla() != null ? activeBooking.getAssignedVilla().getVillaCode() : "Chưa xếp phòng")
                .checkinDate(start.atStartOfDay())
                .checkoutDate(end.atStartOfDay())
                .bookingStatus(activeBooking.getBookingStatus())
                .events(events)
                .build();
    }
}
