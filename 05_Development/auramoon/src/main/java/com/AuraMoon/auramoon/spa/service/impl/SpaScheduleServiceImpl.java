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

    public SpaScheduleServiceImpl(ScheduleRepository scheduleRepository,
            TreatmentRoomRepository roomRepository,
            TherapistRepository therapistRepository,
            TreatmentBookingRepository treatmentBookingRepository,
            TreatmentServiceRepository treatmentServiceRepository,
            BookingRepository bookingRepository) {
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
        this.therapistRepository = therapistRepository;
        this.treatmentBookingRepository = treatmentBookingRepository;
        this.treatmentServiceRepository = treatmentServiceRepository;
        this.bookingRepository = bookingRepository;
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

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime bookingCheckin = guestBooking.getCheckinDate();
        LocalDateTime bookingCheckout = guestBooking.getCheckoutDate();

        if (bookingCheckin != null && bookingCheckout != null) {
            LocalDate spaDate = startTime.toLocalDate();
            if (spaDate.isBefore(bookingCheckin.toLocalDate()) || spaDate.isAfter(bookingCheckout.toLocalDate())) {
                throw new SpaBusinessException("SPA-011",
                        "Lịch hẹn Spa phải nằm trong thời gian lưu trú (từ " +
                                bookingCheckin.toLocalDate() + " đến " + bookingCheckout.toLocalDate() + ").");
            }
        }

        // 2. Tính thời gian kết thúc
        LocalDateTime endTime = startTime
                .plusMinutes(service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);

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
        Therapist selectedTherapist = availableTherapists.get(0);

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

        return response;
    }

    @Override
    public List<String> getAvailableTimeSlots(LocalDate date, Integer durationMinutes) {
        long totalRooms = roomRepository.countByStatusAndIsDeleteFalse("AVAILABLE");
        long totalTherapists = therapistRepository.countByStatus("AVAILABLE");

        if (totalRooms == 0 || totalTherapists == 0) {
            return new ArrayList<>(); // No resources available at all
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Schedule> daySchedules = scheduleRepository.findByStartTimeBetweenAndIsDeleteFalse(startOfDay, endOfDay);

        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = LocalTime.of(9, 0); // Open at 09:00
        LocalTime closeTime = LocalTime.of(22, 0); // Spa closes at 22:00 (last booking finishes at 22:00)

        while (currentTime.plusMinutes(durationMinutes).isBefore(closeTime)
                || currentTime.plusMinutes(durationMinutes).equals(closeTime)) {
            LocalDateTime slotStart = date.atTime(currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

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
