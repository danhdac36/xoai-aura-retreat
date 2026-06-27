package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.spa.dto.ScheduleDto;
import com.AuraMoon.auramoon.spa.dto.TherapistDetailDto;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.service.SpaManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaManagerServiceImpl implements SpaManagerService {

    private final TherapistRepository therapistRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public List<TherapistDetailDto> getAllTherapistsWithDetails() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return therapistRepository.findAllTherapistsWithDetails(startOfDay, endOfDay);
    }

    @Override
    @Transactional
    public void updateTherapistStatus(String therapistCode, String newStatus, Integer actorId) {
        if (!"AVAILABLE".equalsIgnoreCase(newStatus) && !"BUSY".equalsIgnoreCase(newStatus)
                && !"OFF_DUTY".equalsIgnoreCase(newStatus)) {
            throw new SpaBusinessException("SPA-131-01", "Trạng thái không hợp lệ");
        }

        Therapist therapist = therapistRepository.findByTherapistCode(therapistCode);
        if (therapist == null) {
            throw new SpaBusinessException("SPA-131-02", "Không tìm thấy nhân viên");
        }

        if ("OFF_DUTY".equalsIgnoreCase(newStatus)) {
            // Auto-Reassignment logic
            LocalDateTime now = LocalDateTime.now();
            List<Schedule> futureSchedules = scheduleRepository.findFutureSchedules(therapistCode, now);

            for (Schedule schedule : futureSchedules) {
                // Find a replacement therapist
                List<Therapist> availableTherapists = therapistRepository
                        .findAvailableTherapistsWithLock(schedule.getStartTime(), schedule.getEndTime());

                // Filter out the current therapist
                Therapist replacement = availableTherapists.stream()
                        .filter(t -> !t.getTherapistCode().equals(therapistCode))
                        .findFirst()
                        .orElse(null);

                if (replacement == null) {
                    throw new SpaBusinessException("SPA-131-03", "Không thể chuyển ca tự động. Ca lúc "
                            + schedule.getStartTime() + " không có nhân viên thay thế.");
                }

                schedule.setTherapist(replacement);
                scheduleRepository.save(schedule);
            }
        }

        therapist.setStatus(newStatus.toUpperCase());
        therapistRepository.save(therapist);
    }

    @Override
    public List<ScheduleDto> getScheduleForTherapist(String therapistCode, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return scheduleRepository.findScheduleDtoForTherapist(therapistCode, startOfDay, endOfDay);
    }
}
