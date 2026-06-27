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
import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.booking.repository.ReviewRepository;
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
    private final YogaRegistrationRepository yogaRegistrationRepository;
    private final ReviewRepository reviewRepository;

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

        LocalDateTime start = activeBooking.getCheckinDate() != null ? activeBooking.getCheckinDate()
                : LocalDateTime.now();
        LocalDateTime end = activeBooking.getCheckoutDate();

        // 1. Nhận phòng (Check-in)
        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Nhận phòng (Check-in dự kiến/thực tế)")
                .time(start)
                .location("Sảnh Lễ tân")
                .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
                .build());

        // 2. Lấy dữ liệu Spa Scheduled thực tế
        List<Schedule> spaSchedules = scheduleRepository
                .findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(activeBooking.getId());
        for (Schedule schedule : spaSchedules) {
            String serviceName = "Dịch vụ Spa";
            if (schedule.getTreatmentBooking() != null
                    && schedule.getTreatmentBooking().getTreatmentService() != null) {
                serviceName = schedule.getTreatmentBooking().getTreatmentService().getServiceName();
            }
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Trị liệu: " + serviceName)
                    .time(schedule.getStartTime() != null ? schedule.getStartTime() : start)
                    .location(schedule.getRoom() != null ? schedule.getRoom().getRoomName() : "Aura Spa")
                    .description("Liệu trình Spa thư giãn cơ thể.")
                    .build());
        }

        // 3. Lấy dữ liệu Bữa ăn thực tế
        List<MealOrder> mealOrders = mealOrderRepository.findByBookingId(activeBooking.getId());
        for (MealOrder meal : mealOrders) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Bữa ăn Cá nhân hóa")
                    .time(meal.getOrderedAt() != null ? meal.getOrderedAt() : start.plusHours(2))
                    .location("Nhà hàng Thực dưỡng")
                    .description("Bữa ăn theo Dietary Profile: "
                            + (meal.getNote() != null ? meal.getNote() : "Thanh lọc cơ thể"))
                    .build());
        }

        // 3.5 Lấy dữ liệu Đăng ký Yoga thực tế
        List<YogaRegistration> yogaRegistrations = yogaRegistrationRepository
                .findByBookingIdAndStatus(activeBooking.getId(), "REGISTERED");
        for (YogaRegistration reg : yogaRegistrations) {
            if (reg.getSchedule() != null && !Boolean.TRUE.equals(reg.getSchedule().getIsDelete())) {
                String instructorName = (reg.getSchedule().getInstructor() != null
                        && reg.getSchedule().getInstructor().getUser() != null)
                                ? reg.getSchedule().getInstructor().getUser().getFullName()
                                : "Huấn luyện viên";
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Yoga: " + reg.getSchedule().getYogaClass().getClassName())
                        .time(reg.getSchedule().getStartTime())
                        .location(reg.getSchedule().getLocation() != null ? reg.getSchedule().getLocation()
                                : "Phòng tập Yoga")
                        .description("Tham gia lớp học Yoga hướng dẫn bởi GV " + instructorName + ". Thời lượng: "
                                + reg.getSchedule().getYogaClass().getDurationMinutes() + " phút.")
                        .build());
            }
        }

        // 4. Trả phòng (Check-out) - Chỉ hiện nếu đã xác định được giờ checkout
        if (end != null) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Trả phòng (Check-out)")
                    .time(end)
                    .location("Sảnh Lễ tân")
                    .description("Hoàn tất thủ tục thanh toán và check-out phòng.")
                    .build());
        }

        events.sort(Comparator.comparing(ItineraryTimelineDTO.TimelineEvent::getTime,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return ItineraryTimelineDTO.builder()
                .bookingId(activeBooking.getId())
                .guestName(guest.getFullName())
                .packageName(
                        activeBooking.getRetreatPackage() != null ? activeBooking.getRetreatPackage().getPackageName()
                                : "Chưa đăng ký gói")
                .villaName(activeBooking.getAssignedVilla() != null ? activeBooking.getAssignedVilla().getVillaCode()
                        : "Chưa xếp phòng")
                .checkinDate(start)
                .checkoutDate(end)
                .bookingStatus(activeBooking.getBookingStatus())
                .hasReviewed("CHECKED_OUT".equalsIgnoreCase(activeBooking.getBookingStatus())
                        && reviewRepository.existsByBookingId(activeBooking.getId()))
                .events(events)
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO> getBookingHistory(Integer guestId) {
        List<Booking> bookings = bookingRepository.findByGuestId(guestId);
        List<com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO> history = new ArrayList<>();
        for (Booking b : bookings) {
            String packageName = b.getRetreatPackage() != null ? b.getRetreatPackage().getPackageName() : "Chưa đăng ký gói";
            String villaName = b.getAssignedVilla() != null ? b.getAssignedVilla().getVillaCode() : "Chưa xếp phòng";
            history.add(com.AuraMoon.auramoon.booking.dto.BookingHistoryDTO.builder()
                    .bookingId(b.getId())
                    .packageName(packageName)
                    .villaName(villaName)
                    .checkInDate(b.getCheckinDate())
                    .checkOutDate(b.getCheckoutDate())
                    .totalAmount(b.getRetreatPackage() != null && b.getRetreatPackage().getPrice() != null ? b.getRetreatPackage().getPrice().doubleValue() : 0.0)
                    .status(b.getBookingStatus())
                    .build());
        }
        return history;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ItineraryTimelineDTO getTimelineForBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking với ID: " + bookingId));

        User guest = userRepository.findById(booking.getGuestId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));

        return buildTimelineForBooking(booking, guest);
    }

    private ItineraryTimelineDTO buildTimelineForBooking(Booking activeBooking, User guest) {
        List<ItineraryTimelineDTO.TimelineEvent> events = new ArrayList<>();

        LocalDateTime start = activeBooking.getCheckinDate() != null ? activeBooking.getCheckinDate() : LocalDateTime.now();
        LocalDateTime end = activeBooking.getCheckoutDate();

        events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                .eventName("Nhận phòng (Check-in dự kiến/thực tế)")
                .time(start)
                .location("Sảnh Lễ tân")
                .description("Nhận Villa và bắt đầu kỳ nghỉ dưỡng.")
                .build());

        List<Schedule> spaSchedules = scheduleRepository
                .findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(activeBooking.getId());
        for (Schedule schedule : spaSchedules) {
            String serviceName = "Dịch vụ Spa";
            if (schedule.getTreatmentBooking() != null && schedule.getTreatmentBooking().getTreatmentService() != null) {
                serviceName = schedule.getTreatmentBooking().getTreatmentService().getServiceName();
            }
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Trị liệu: " + serviceName)
                    .time(schedule.getStartTime() != null ? schedule.getStartTime() : start)
                    .location(schedule.getRoom() != null ? schedule.getRoom().getRoomName() : "Aura Spa")
                    .description("Liệu trình Spa thư giãn cơ thể.")
                    .build());
        }

        List<MealOrder> mealOrders = mealOrderRepository.findByBookingId(activeBooking.getId());
        for (MealOrder meal : mealOrders) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Bữa ăn Cá nhân hóa")
                    .time(meal.getOrderedAt() != null ? meal.getOrderedAt() : start.plusHours(2))
                    .location("Nhà hàng Thực dưỡng")
                    .description("Bữa ăn theo Dietary Profile: " + (meal.getNote() != null ? meal.getNote() : "Thanh lọc cơ thể"))
                    .build());
        }

        List<YogaRegistration> yogaRegistrations = yogaRegistrationRepository
                .findByBookingIdAndStatus(activeBooking.getId(), "REGISTERED");
        for (YogaRegistration reg : yogaRegistrations) {
            if (reg.getSchedule() != null && !Boolean.TRUE.equals(reg.getSchedule().getIsDelete())) {
                String instructorName = (reg.getSchedule().getInstructor() != null
                        && reg.getSchedule().getInstructor().getUser() != null)
                                ? reg.getSchedule().getInstructor().getUser().getFullName()
                                : "Huấn luyện viên";
                events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                        .eventName("Yoga: " + reg.getSchedule().getYogaClass().getClassName())
                        .time(reg.getSchedule().getStartTime())
                        .location(reg.getSchedule().getLocation() != null ? reg.getSchedule().getLocation() : "Phòng tập Yoga")
                        .description("Tham gia lớp học Yoga hướng dẫn bởi GV " + instructorName + ". Thời lượng: "
                                + reg.getSchedule().getYogaClass().getDurationMinutes() + " phút.")
                        .build());
            }
        }

        if (end != null) {
            events.add(ItineraryTimelineDTO.TimelineEvent.builder()
                    .eventName("Trả phòng (Check-out)")
                    .time(end)
                    .location("Sảnh Lễ tân")
                    .description("Hoàn tất thủ tục thanh toán và check-out phòng.")
                    .build());
        }

        events.sort(Comparator.comparing(ItineraryTimelineDTO.TimelineEvent::getTime,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return ItineraryTimelineDTO.builder()
                .bookingId(activeBooking.getId())
                .guestName(guest.getFullName())
                .packageName(activeBooking.getRetreatPackage() != null ? activeBooking.getRetreatPackage().getPackageName() : "Chưa đăng ký gói")
                .villaName(activeBooking.getAssignedVilla() != null ? activeBooking.getAssignedVilla().getVillaCode() : "Chưa xếp phòng")
                .checkinDate(start)
                .checkoutDate(end)
                .bookingStatus(activeBooking.getBookingStatus())
                .hasReviewed("CHECKED_OUT".equalsIgnoreCase(activeBooking.getBookingStatus())
                        && reviewRepository.existsByBookingId(activeBooking.getId()))
                .events(events)
                .build();
    }
}
