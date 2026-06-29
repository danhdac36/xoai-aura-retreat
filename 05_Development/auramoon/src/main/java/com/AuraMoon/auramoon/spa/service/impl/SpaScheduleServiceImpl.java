package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.entity.TreatmentRoom;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentRoomRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.billing.service.IEmailNotificationService;
import com.AuraMoon.auramoon.spa.service.SpaScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpaScheduleServiceImpl implements SpaScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TreatmentRoomRepository roomRepository;
    private final TherapistRepository therapistRepository;
    private final TreatmentBookingRepository treatmentBookingRepository;
    private final TreatmentServiceRepository treatmentServiceRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final IEmailNotificationService emailNotificationService;

    public SpaScheduleServiceImpl(ScheduleRepository scheduleRepository,
            TreatmentRoomRepository roomRepository,
            TherapistRepository therapistRepository,
            TreatmentBookingRepository treatmentBookingRepository,
            TreatmentServiceRepository treatmentServiceRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            IEmailNotificationService emailNotificationService) {
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
        this.therapistRepository = therapistRepository;
        this.treatmentBookingRepository = treatmentBookingRepository;
        this.treatmentServiceRepository = treatmentServiceRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    @Transactional
    public SpaScheduleResponse scheduleSession(SpaScheduleRequest request) {
        // 1. Kiểm tra dịch vụ có nằm trong gói không (BR-05)
        List<TreatmentBooking> bookings = treatmentBookingRepository
                .findByBookingIdAndTreatmentService_Id(request.getBookingId(), request.getServiceId());

        TreatmentBooking booking = bookings.stream()
                .filter(b -> !"Scheduled".equals(b.getStatus()))
                .findFirst()
                .orElseThrow(() -> new SpaBusinessException("SPA-001",
                        "Service not found, not in package, or all sessions already scheduled"));

        TreatmentService service = treatmentServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Treatment Service not found"));

        // 1.5. Kiểm tra lịch hẹn Spa có nằm trong khoảng lưu trú không
        Booking guestBooking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Booking not found"));

        if (guestBooking.getBookingStatus() == null ||
                (!"Checked-In".equalsIgnoreCase(guestBooking.getBookingStatus())
                        && !"CHECKED_IN".equalsIgnoreCase(guestBooking.getBookingStatus()))) {
            throw new SpaBusinessException("SPA-012",
                    "Chỉ cho phép đặt lịch Spa đối với đơn đặt phòng có trạng thái Checked-In.");
        }

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime bookingCheckin = guestBooking.getCheckinDate();
        LocalDateTime bookingCheckout = guestBooking.getCheckoutDate();

        // 2. Tính thời gian kết thúc
        LocalDateTime endTime = startTime
                .plusMinutes(service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);

        if (bookingCheckin != null && bookingCheckout != null) {
            if (startTime.isBefore(bookingCheckin) || endTime.isAfter(bookingCheckout)) {
                throw new SpaBusinessException("SPA-011",
                        "Lịch hẹn Spa phải nằm trong thời gian lưu trú (từ " +
                                bookingCheckin + " đến " + bookingCheckout + ").");
            }
        }

        // 2.5. Kiểm tra trùng lịch Spa dựa trên số lượng khách (totalGuests)
        List<Schedule> existingSchedules = scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(request.getBookingId());
        long overlappingCount = existingSchedules.stream()
                .filter(s -> s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime))
                .count();

        int maxAllowedOverlapping = (guestBooking.getTotalGuests() != null) ? guestBooking.getTotalGuests() : 1;
        if (overlappingCount >= maxAllowedOverlapping) {
            if (maxAllowedOverlapping <= 1) {
                throw new SpaBusinessException("SPA-013",
                        "Quý khách không thể đặt 2 ca spa cùng một thời điểm.");
            } else {
                throw new SpaBusinessException("SPA-013",
                        "Số lượng ca spa trùng thời điểm vượt quá số lượng khách trong đơn đặt phòng (tối đa " + maxAllowedOverlapping + " người).");
            }
        }

        // 3. Tìm Phòng và Chuyên viên rảnh bằng Pessimistic Lock (BR-04)
        List<TreatmentRoom> availableRooms = roomRepository.findAvailableRoomsWithLock(startTime, endTime);
        if (availableRooms.isEmpty()) {
            throw new SpaBusinessException("SPA-010", "No available Therapist or Therapy Room could be found.");
        }
        TreatmentRoom selectedRoom = availableRooms.get(0);

        List<Therapist> availableTherapists = therapistRepository.findAvailableTherapistsWithLock(startTime, endTime);
        if (availableTherapists.isEmpty()) {
            throw new SpaBusinessException("SPA-010", "No available Therapist or Therapy Room could be found.");
        }

        // Cân bằng công việc: Lựa chọn Therapist có số ca làm việc ít nhất trong ngày
        Therapist selectedTherapist = availableTherapists.get(0);
        long minWorkload = Long.MAX_VALUE;
        LocalDateTime startOfDay = startTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startTime.toLocalDate().atTime(java.time.LocalTime.MAX);
        for (Therapist t : availableTherapists) {
            long workload = scheduleRepository.countDailySchedulesForTherapist(t.getId(), startOfDay, endOfDay);
            if (workload < minWorkload) {
                minWorkload = workload;
                selectedTherapist = t;
            }
        }

        // 4. Tạo bản ghi Schedule
        Schedule schedule = new Schedule();
        schedule.setTreatmentBooking(booking);
        schedule.setTherapist(selectedTherapist);
        schedule.setRoom(selectedRoom);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setIsDelete(false);

        schedule = scheduleRepository.save(schedule);

        // Update trạng thái TreatmentBooking và Note
        booking.setStatus("Scheduled");
        if (request.getNote() != null && !request.getNote().trim().isEmpty()) {
            booking.setNote(request.getNote());
        }
        treatmentBookingRepository.save(booking);

        // 5. Build response
        SpaScheduleResponse response = new SpaScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setTherapistCode(schedule.getTherapist().getTherapistCode());
        response.setRoomId(schedule.getRoom().getId());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());

        // 4.5. Gửi email nhắc lịch hẹn cho khách (Bọc trong try-catch để tránh rollback
        // giao dịch nếu lỗi mail)
        try {
            User guest = userRepository.findById(guestBooking.getGuestId()).orElse(null);
            if (guest != null && guest.getEmail() != null) {
                String formattedTime = startTime
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
                emailNotificationService.sendSpaBookingReminderEmail(
                        guest.getEmail(),
                        guest.getFullName() != null ? guest.getFullName() : "Guest",
                        service.getServiceName(),
                        selectedRoom.getRoomName(),
                        formattedTime);
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Không thể gửi email nhắc lịch Spa: " + e.getMessage());
        }

        return response;
    }

    @Override
    public List<String> getAvailableTimeSlots(LocalDate date, Integer durationMinutes, Integer bookingId) {
        long totalRooms = roomRepository.countByStatusAndIsDeleteFalse("AVAILABLE");
        long totalTherapists = therapistRepository.countByStatus("AVAILABLE");

        if (totalRooms == 0 || totalTherapists == 0) {
            return new ArrayList<>(); // No resources available at all
        }

        LocalDateTime checkinDate = null;
        LocalDateTime checkoutDate = null;
        if (bookingId != null) {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking != null) {
                checkinDate = booking.getCheckinDate();
                checkoutDate = booking.getCheckoutDate();
            }
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Schedule> daySchedules = scheduleRepository.findByStartTimeBetweenAndIsDeleteFalse(startOfDay, endOfDay);

        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = LocalTime.of(9, 0); // Open at 09:00
        LocalTime closeTime = LocalTime.of(22, 0); // Spa closes at 22:00 (last booking finishes at 22:00)

        LocalDateTime now = LocalDateTime.now();

        while (currentTime.plusMinutes(durationMinutes).isBefore(closeTime)
                || currentTime.plusMinutes(durationMinutes).equals(closeTime)) {
            LocalDateTime slotStart = date.atTime(currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

            // Filter out slots in the past relative to now
            if (slotStart.isBefore(now)) {
                currentTime = currentTime.plusMinutes(30);
                continue;
            }

            // Filter out slots strictly outside checkin/checkout LocalDateTime boundaries
            if (checkinDate != null && slotStart.isBefore(checkinDate)) {
                currentTime = currentTime.plusMinutes(30);
                continue;
            }
            if (checkoutDate != null && slotEnd.isAfter(checkoutDate)) {
                currentTime = currentTime.plusMinutes(30);
                continue;
            }

            // Find overlapping schedules
            List<Schedule> overlapping = daySchedules.stream()
                    .filter(s -> s.getStartTime().isBefore(slotEnd) && s.getEndTime().isAfter(slotStart))
                    .toList();

            // Count unique rooms and therapists used
            long usedRooms = overlapping.stream().map(s -> s.getRoom().getId()).distinct().count();
            long usedTherapists = overlapping.stream().map(s -> s.getTherapist().getId()).distinct().count();

            if (usedRooms < totalRooms && usedTherapists < totalTherapists) {
                availableSlots.add(String.format("%02d:%02d", currentTime.getHour(), currentTime.getMinute()));
            }

            currentTime = currentTime.plusMinutes(30); // 30-min intervals
        }

        return availableSlots;
    }
}
