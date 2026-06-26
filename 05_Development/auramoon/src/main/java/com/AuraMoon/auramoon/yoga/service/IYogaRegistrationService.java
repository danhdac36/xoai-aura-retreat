package com.AuraMoon.auramoon.yoga.service;

import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationRequest;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationResponse;
import com.AuraMoon.auramoon.yoga.dto.YogaParticipantResponse;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import java.time.LocalDate;
import java.util.List;

public interface IYogaRegistrationService {
    YogaRegistrationResponse registerYogaClass(YogaRegistrationRequest request);

    List<YogaSchedule> getAvailableSchedules(LocalDate date);

    List<YogaSchedule> getInstructorSchedules(Integer instructorUserId, LocalDate date);

    List<YogaParticipantResponse> getParticipantsForSchedule(Integer scheduleId, Integer instructorUserId);
}
