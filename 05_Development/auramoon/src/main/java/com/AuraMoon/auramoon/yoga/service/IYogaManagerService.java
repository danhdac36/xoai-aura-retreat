package com.AuraMoon.auramoon.yoga.service;

import com.AuraMoon.auramoon.yoga.dto.YogaManagerDto.*;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import java.time.LocalDate;
import java.util.List;

public interface IYogaManagerService {
    
    // --- LỚP HỌC (YOGA_CLASS) ---
    YogaClassResponse createClass(YogaClassRequest request);
    YogaClassResponse updateClass(Integer classId, YogaClassRequest request);
    void deleteClass(Integer classId);
    List<YogaClassResponse> getAllActiveClasses();
    
    // --- LỊCH HỌC (YOGA_SCHEDULE) ---
    YogaScheduleResponse createSchedule(YogaScheduleRequest request);
    YogaScheduleResponse updateSchedule(Integer scheduleId, YogaScheduleRequest request);
    void deleteSchedule(Integer scheduleId);
    List<YogaScheduleResponse> getSchedulesByDate(LocalDate date);

    // --- NHÂN VIÊN / INSTRUCTORS ---
    List<YogaInstructor> getAllActiveInstructors();
}
