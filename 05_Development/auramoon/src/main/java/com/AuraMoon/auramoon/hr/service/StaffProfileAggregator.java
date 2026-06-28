package com.AuraMoon.auramoon.hr.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.repository.AuditLogRepository;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
public class StaffProfileAggregator implements IStaffProfileAggregator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private TreatmentBookingRepository treatmentBookingRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public FullStaffProfileDTO getAggregatedProfile(Long id, int page, int size, String filter) {
        Optional<User> userOpt = userRepository.findById(id.intValue());
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        FullStaffProfileDTO dto = new FullStaffProfileDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhone());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
            if ("THERAPIST".equals(user.getRole().getRoleName())) {
                Optional<Therapist> therapistOpt = therapistRepository.findById(user.getId());
                therapistOpt.ifPresent(therapist -> {
                    dto.setTherapistCode(therapist.getTherapistCode());
                    dto.setTherapistStatus(therapist.getStatus());
                    
                    Long sessions = treatmentBookingRepository.countByTherapistIdAndStatus(user.getId(), "COMPLETED");
                    dto.setCompletedSessions(sessions != null ? sessions : 0L);
                });
            }
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<com.AuraMoon.auramoon.billing.dto.AuditLogDTO> activitiesPage = Page.empty();
        
        if (user.getRole() != null && "GUEST".equalsIgnoreCase(user.getRole().getRoleName())) {
            Page<Booking> guestBookings;
            if (filter != null && !filter.isEmpty() && !filter.equalsIgnoreCase("ALL")) {
                guestBookings = bookingRepository.findByGuestIdAndBookingStatus(user.getId(), filter, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                guestBookings = bookingRepository.findByGuestId(user.getId(), PageRequest.of(page, size, Sort.by("id").descending()));
            }
            activitiesPage = guestBookings.map(b -> com.AuraMoon.auramoon.billing.dto.AuditLogDTO.builder()
                    .logId(b.getId())
                    .actionType("PACKAGE BOOKING")
                    .actorId(user.getId())
                    .targetId(b.getId())
                    .details(b.getRetreatPackage() != null ? "Package: " + b.getRetreatPackage().getPackageName() : "Package ID: " + b.getRetreatPackage().getId())
                    .timestamp(b.getCheckinDate() != null ? b.getCheckinDate().toString() : "")
                    .build());
        } else if (user.getRole() != null && "THERAPIST".equalsIgnoreCase(user.getRole().getRoleName())) {
            Page<Schedule> recentSchedules;
            if (filter != null && !filter.isEmpty() && !filter.equalsIgnoreCase("ALL")) {
                recentSchedules = scheduleRepository.findByTherapistIdAndTreatmentBookingStatusOrderByStartTimeDesc(user.getId(), filter, pageable);
            } else {
                recentSchedules = scheduleRepository.findByTherapistIdOrderByStartTimeDesc(user.getId(), pageable);
            }
            activitiesPage = recentSchedules.map(s -> {
                TreatmentBooking tb = s.getTreatmentBooking();
                return com.AuraMoon.auramoon.billing.dto.AuditLogDTO.builder()
                        .logId(tb != null ? tb.getId() : 0)
                        .actionType("SPA SESSION")
                        .actorId(user.getId())
                        .targetId(tb != null ? tb.getId() : 0)
                        .details(tb != null && tb.getTreatmentService() != null ? "Service: " + tb.getTreatmentService().getServiceName() : "Unknown Service")
                        .timestamp(s.getStartTime() != null ? s.getStartTime().toString() : "")
                        .build();
            });
        } else {
            Page<com.AuraMoon.auramoon.billing.entity.AuditLog> logs;
            if (filter != null && !filter.isEmpty() && !filter.equalsIgnoreCase("ALL")) {
                logs = auditLogRepository.findByActorIdAndActionTypeOrderByTimestampDesc(user.getId(), filter, pageable);
            } else {
                logs = auditLogRepository.findByActorIdOrderByTimestampDesc(user.getId(), pageable);
            }
            activitiesPage = logs.map(l -> com.AuraMoon.auramoon.billing.dto.AuditLogDTO.builder()
                    .logId(l.getId())
                    .actionType(l.getActionType())
                    .actorId(l.getActorId())
                    .targetId(l.getTargetId())
                    .details(l.getDetails())
                    .timestamp(l.getTimestamp() != null ? l.getTimestamp().toString() : "")
                    .build());
        }
        
        dto.setRecentActivitiesPage(activitiesPage);

        return dto;
    }
}
