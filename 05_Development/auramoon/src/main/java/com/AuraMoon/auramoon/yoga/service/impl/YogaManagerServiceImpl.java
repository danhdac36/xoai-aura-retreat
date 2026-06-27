package com.AuraMoon.auramoon.yoga.service.impl;

import com.AuraMoon.auramoon.yoga.dto.YogaManagerDto.*;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.repository.YogaClassRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaInstructorRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaScheduleRepository;
import com.AuraMoon.auramoon.yoga.service.IYogaManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YogaManagerServiceImpl implements IYogaManagerService {

    private final YogaClassRepository yogaClassRepository;
    private final YogaScheduleRepository yogaScheduleRepository;
    private final YogaInstructorRepository yogaInstructorRepository;
    private final YogaRegistrationRepository yogaRegistrationRepository;

    public YogaManagerServiceImpl(
            YogaClassRepository yogaClassRepository,
            YogaScheduleRepository yogaScheduleRepository,
            YogaInstructorRepository yogaInstructorRepository,
            YogaRegistrationRepository yogaRegistrationRepository) {
        this.yogaClassRepository = yogaClassRepository;
        this.yogaScheduleRepository = yogaScheduleRepository;
        this.yogaInstructorRepository = yogaInstructorRepository;
        this.yogaRegistrationRepository = yogaRegistrationRepository;
    }

    @Override
    @Transactional
    public YogaClassResponse createClass(YogaClassRequest request) {
        if (request.getDurationMinutes() == null || request.getDurationMinutes() < 15) {
            throw new YogaBusinessException("YOGA-001", "Thời lượng lớp học tối thiểu phải là 15 phút");
        }

        YogaClass yogaClass = YogaClass.builder()
                .className(request.getClassName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .imageUrl(request.getImageUrl())
                .isDelete(false)
                .build();

        YogaClass saved = yogaClassRepository.save(yogaClass);
        return mapToClassResponse(saved);
    }

    @Override
    @Transactional
    public YogaClassResponse updateClass(Integer classId, YogaClassRequest request) {
        YogaClass yogaClass = yogaClassRepository.findById(classId)
                .orElseThrow(() -> new YogaBusinessException("YOGA-009", "Lớp học không tồn tại"));

        if (Boolean.TRUE.equals(yogaClass.getIsDelete())) {
            throw new YogaBusinessException("YOGA-009", "Lớp học đã bị xóa");
        }

        if (request.getDurationMinutes() == null || request.getDurationMinutes() < 15) {
            throw new YogaBusinessException("YOGA-001", "Thời lượng lớp học tối thiểu phải là 15 phút");
        }

        yogaClass.setClassName(request.getClassName());
        yogaClass.setDescription(request.getDescription());
        yogaClass.setDurationMinutes(request.getDurationMinutes());
        yogaClass.setImageUrl(request.getImageUrl());

        YogaClass saved = yogaClassRepository.save(yogaClass);
        return mapToClassResponse(saved);
    }

    @Override
    @Transactional
    public void deleteClass(Integer classId) {
        YogaClass yogaClass = yogaClassRepository.findById(classId)
                .orElseThrow(() -> new YogaBusinessException("YOGA-009", "Lớp học không tồn tại"));
        yogaClass.setIsDelete(true);
        yogaClassRepository.save(yogaClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaClassResponse> getAllActiveClasses() {
        return yogaClassRepository.findByIsDeleteFalse().stream()
                .map(this::mapToClassResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public YogaScheduleResponse createSchedule(YogaScheduleRequest request) {
        YogaClass yogaClass = yogaClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-009", "Lớp học không tồn tại"));

        if (Boolean.TRUE.equals(yogaClass.getIsDelete())) {
            throw new YogaBusinessException("YOGA-009", "Lớp học đã bị xóa");
        }

        YogaInstructor instructor = yogaInstructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-010", "Huấn luyện viên không tồn tại"));

        if (Boolean.TRUE.equals(instructor.getIsDelete()) || !"AVAILABLE".equals(instructor.getStatus())) {
            throw new YogaBusinessException("YOGA-010", "Huấn luyện viên không khả dụng");
        }

        validateScheduleTime(request.getStartTime(), request.getEndTime(), yogaClass.getDurationMinutes());
        validateOverlap(request.getInstructorId(), request.getLocation(), request.getStartTime(), request.getEndTime(), null);

        YogaSchedule schedule = YogaSchedule.builder()
                .yogaClass(yogaClass)
                .instructor(instructor)
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxCapacity(request.getMaxCapacity())
                .isDelete(false)
                .build();

        YogaSchedule saved = yogaScheduleRepository.save(schedule);
        return mapToScheduleResponse(saved);
    }

    @Override
    @Transactional
    public YogaScheduleResponse updateSchedule(Integer scheduleId, YogaScheduleRequest request) {
        YogaSchedule schedule = yogaScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new YogaBusinessException("YOGA-001", "Lịch học không tồn tại"));

        if (Boolean.TRUE.equals(schedule.getIsDelete())) {
            throw new YogaBusinessException("YOGA-001", "Lịch học đã bị xóa");
        }

        YogaClass yogaClass = yogaClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-009", "Lớp học không tồn tại"));

        if (Boolean.TRUE.equals(yogaClass.getIsDelete())) {
            throw new YogaBusinessException("YOGA-009", "Lớp học đã bị xóa");
        }

        YogaInstructor instructor = yogaInstructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-010", "Huấn luyện viên không tồn tại"));

        if (Boolean.TRUE.equals(instructor.getIsDelete()) 
                || (!"AVAILABLE".equals(instructor.getStatus()) && !instructor.getInstructorId().equals(schedule.getInstructor().getInstructorId()))) {
            throw new YogaBusinessException("YOGA-010", "Huấn luyện viên không khả dụng");
        }

        validateScheduleTime(request.getStartTime(), request.getEndTime(), yogaClass.getDurationMinutes());
        validateOverlap(request.getInstructorId(), request.getLocation(), request.getStartTime(), request.getEndTime(), scheduleId);

        schedule.setYogaClass(yogaClass);
        schedule.setInstructor(instructor);
        schedule.setLocation(request.getLocation());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxCapacity(request.getMaxCapacity());

        YogaSchedule saved = yogaScheduleRepository.save(schedule);
        return mapToScheduleResponse(saved);
    }

    @Override
    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        YogaSchedule schedule = yogaScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new YogaBusinessException("YOGA-001", "Lịch học không tồn tại"));

        if (Boolean.TRUE.equals(schedule.getIsDelete())) {
            return;
        }

        long activeRegistrations = yogaRegistrationRepository.countBySchedule_IdAndStatus(scheduleId, "REGISTERED");
        if (activeRegistrations > 0) {
            throw new YogaBusinessException("YOGA-012", "Không thể xóa lịch học đã có học viên đăng ký");
        }

        schedule.setIsDelete(true);
        yogaScheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaScheduleResponse> getSchedulesByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return yogaScheduleRepository.findSchedulesByDateRange(start, end).stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaInstructor> getAllActiveInstructors() {
        return yogaInstructorRepository.findByIsDeleteFalse();
    }

    private void validateScheduleTime(LocalDateTime startTime, LocalDateTime endTime, int durationMinutes) {
        if (startTime == null || endTime == null) {
            throw new YogaBusinessException("YOGA-011", "Thời gian học không được để trống");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new YogaBusinessException("YOGA-011", "Thời gian bắt đầu học phải ở tương lai");
        }
        if (endTime.isBefore(startTime) || !endTime.isEqual(startTime.plusMinutes(durationMinutes))) {
            throw new YogaBusinessException("YOGA-011", "Thời gian kết thúc không khớp với thời lượng lớp học");
        }
    }

    private void validateOverlap(Integer instructorId, String location, LocalDateTime startTime, LocalDateTime endTime, Integer excludeScheduleId) {
        long instructorOverlapCount = yogaScheduleRepository.countOverlappingInstructorSchedules(instructorId, startTime, endTime, excludeScheduleId);
        if (instructorOverlapCount > 0) {
            throw new YogaBusinessException("YOGA-013", "Giáo viên đã bị trùng lịch dạy vào khung giờ này");
        }

        long locationOverlapCount = yogaScheduleRepository.countOverlappingLocationSchedules(location, startTime, endTime, excludeScheduleId);
        if (locationOverlapCount > 0) {
            throw new YogaBusinessException("YOGA-014", "Địa điểm đã bị trùng lịch sử dụng vào khung giờ này");
        }
    }

    private YogaClassResponse mapToClassResponse(YogaClass yogaClass) {
        return YogaClassResponse.builder()
                .classId(yogaClass.getId())
                .className(yogaClass.getClassName())
                .description(yogaClass.getDescription())
                .durationMinutes(yogaClass.getDurationMinutes())
                .imageUrl(yogaClass.getImageUrl())
                .build();
    }

    private YogaScheduleResponse mapToScheduleResponse(YogaSchedule schedule) {
        long countRegistered = yogaRegistrationRepository.countBySchedule_IdAndStatus(schedule.getId(), "REGISTERED");

        return YogaScheduleResponse.builder()
                .scheduleId(schedule.getId())
                .classId(schedule.getYogaClass().getId())
                .className(schedule.getYogaClass().getClassName())
                .durationMinutes(schedule.getYogaClass().getDurationMinutes())
                .instructorId(schedule.getInstructor().getInstructorId())
                .instructorName(schedule.getInstructor().getUser() != null ? schedule.getInstructor().getUser().getFullName() : null)
                .location(schedule.getLocation())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .maxCapacity(schedule.getMaxCapacity())
                .countRegistered((int) countRegistered)
                .build();
    }
}
