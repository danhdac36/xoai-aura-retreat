package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.dto.TherapistScheduleDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.service.TherapistScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TherapistScheduleServiceImpl implements TherapistScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TreatmentBookingRepository treatmentBookingRepository;
    private final BookingRepository bookingRepository;
    private final PhysicalHealthProfileRepository physicalHealthProfileRepository;
    private final UserRepository userRepository;

    @Override
    public List<TherapistScheduleDto> getDailySchedule(String therapistCode, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepository.findDailyScheduleForTherapist(therapistCode, startOfDay,
                endOfDay);

        return schedules.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSessionStatus(Integer scheduleId, String therapistCode, String newStatus) {
        if (!"Scheduled".equalsIgnoreCase(newStatus) && !"Ongoing".equalsIgnoreCase(newStatus) &&
                !"Completed".equalsIgnoreCase(newStatus) && !"No-Show".equalsIgnoreCase(newStatus)) {
            throw new SpaBusinessException("SPA-012", "Trạng thái không hợp lệ");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new SpaBusinessException("SPA-013", "Không tìm thấy ca trị liệu"));

        if (schedule.getTherapist() == null || !schedule.getTherapist().getTherapistCode().equals(therapistCode)) {
            throw new SpaBusinessException("SPA-011", "Bạn không có quyền cập nhật ca này");
        }

        TreatmentBooking booking = schedule.getTreatmentBooking();
        if (booking == null) {
            throw new SpaBusinessException("SPA-013", "Không tìm thấy lượt đặt dịch vụ tương ứng");
        }

        // Standardize newStatus to Title Case ("Scheduled", "Ongoing", "Completed",
        // "No-Show")
        String normalizedStatus = newStatus.substring(0, 1).toUpperCase() + newStatus.substring(1).toLowerCase();
        if (newStatus.equalsIgnoreCase("no-show")) {
            normalizedStatus = "No-Show";
        }

        String currentStatus = booking.getStatus();
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            currentStatus = "Scheduled";
        }

        boolean isValid = false;
        if ("Scheduled".equalsIgnoreCase(currentStatus)) {
            if ("Ongoing".equalsIgnoreCase(normalizedStatus) || "No-Show".equalsIgnoreCase(normalizedStatus)) {
                isValid = true;
            }
        } else if ("Ongoing".equalsIgnoreCase(currentStatus)) {
            if ("Completed".equalsIgnoreCase(normalizedStatus)) {
                isValid = true;
            }
        }

        if (!isValid) {
            throw new SpaBusinessException("SPA-015", "Không thể chuyển trạng thái từ " + currentStatus + " sang " + normalizedStatus);
        }

        booking.setStatus(normalizedStatus);
        treatmentBookingRepository.save(booking);
    }

    private TherapistScheduleDto mapToDto(Schedule schedule) {
        String serviceName = "Unknown Service";
        String note = "";
        String status = "Scheduled";
        String guestName = "Khách ẩn danh";
        String medicalConditions = null;
        String injuries = null;

        if (schedule.getTreatmentBooking() != null) {
            TreatmentBooking trBooking = schedule.getTreatmentBooking();
            note = trBooking.getNote();
            if (trBooking.getTreatmentService() != null) {
                serviceName = trBooking.getTreatmentService().getServiceName();
            }
            String rawStatus = trBooking.getStatus();
            if (rawStatus != null && !rawStatus.trim().isEmpty()) {
                if (rawStatus.equalsIgnoreCase("completed")) {
                    status = "Completed";
                } else if (rawStatus.equalsIgnoreCase("ongoing")) {
                    status = "Ongoing";
                } else if (rawStatus.equalsIgnoreCase("no-show") || rawStatus.equalsIgnoreCase("noshow")
                        || rawStatus.equalsIgnoreCase("no_show")) {
                    status = "No-Show";
                } else if (rawStatus.equalsIgnoreCase("scheduled")) {
                    status = "Scheduled";
                } else {
                    status = rawStatus.substring(0, 1).toUpperCase() + rawStatus.substring(1).toLowerCase();
                }
            }

            Integer bookingId = trBooking.getBookingId();
            if (bookingId != null) {
                Booking booking = bookingRepository.findById(bookingId).orElse(null);
                if (booking != null) {
                    Integer guestId = booking.getGuestId();
                    if (guestId != null) {
                        User guest = userRepository.findById(guestId).orElse(null);
                        if (guest != null) {
                            guestName = guest.getFullName();
                        }
                        PhysicalHealthProfile hp = physicalHealthProfileRepository.findByUserId(guestId).orElse(null);
                        if (hp != null) {
                            AesDataEncryptor encryptor = new AesDataEncryptor();
                            medicalConditions = encryptor.convertToEntityAttribute(hp.getMedicalConditions());
                            injuries = encryptor.convertToEntityAttribute(hp.getInjuries());
                        }
                    }
                }
            }
        }

        String roomName = "Unknown Room";
        if (schedule.getRoom() != null) {
            roomName = schedule.getRoom().getRoomName();
        }

        return TherapistScheduleDto.builder()
                .scheduleId(schedule.getId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .serviceName(serviceName)
                .roomName(roomName)
                .note(note)
                .status(status)
                .guestName(guestName)
                .medicalConditions(medicalConditions)
                .injuries(injuries)
                .build();
    }
}
