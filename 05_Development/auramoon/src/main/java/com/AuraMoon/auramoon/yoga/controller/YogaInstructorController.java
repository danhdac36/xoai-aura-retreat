package com.AuraMoon.auramoon.yoga.controller;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.yoga.dto.YogaParticipantResponse;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.service.IYogaRegistrationService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling endpoints for Yoga Instructors.
 * - Page view: GET /instructor/yoga/schedule
 * - REST API: GET /instructor/yoga/schedules/{scheduleId}/participants
 * Author: Lê Đức Dương
 */
@Controller
@RequestMapping("/instructor/yoga")
public class YogaInstructorController {

    private final IYogaRegistrationService yogaRegistrationService;
    private final YogaRegistrationRepository yogaRegistrationRepository;

    public YogaInstructorController(IYogaRegistrationService yogaRegistrationService,
            YogaRegistrationRepository yogaRegistrationRepository) {
        this.yogaRegistrationService = yogaRegistrationService;
        this.yogaRegistrationRepository = yogaRegistrationRepository;
    }

    /**
     * Renders the instructor's daily schedule page.
     * Shows yoga classes assigned to the current instructor for a selected date.
     */
    @GetMapping("/schedule")
    public String viewSchedule(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetailsResponse userDetails,
            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        List<YogaSchedule> schedules = yogaRegistrationService.getInstructorSchedules(userDetails.getId(), date);

        // Calculate registered counts for each schedule
        Map<Integer, Long> registeredCounts = new HashMap<>();
        for (YogaSchedule s : schedules) {
            long count = yogaRegistrationRepository.countBySchedule_IdAndStatus(s.getId(), "REGISTERED");
            registeredCounts.put(s.getId(), count);
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedDate", date.toString());
        model.addAttribute("registeredCounts", registeredCounts);

        return "yoga/instructor-schedule";
    }

    /**
     * REST API endpoint to get participants for a specific schedule.
     * Used by AJAX calls from the instructor schedule page.
     */
    @GetMapping("/schedules/{scheduleId}/participants")
    @ResponseBody
    public ResponseEntity<?> getParticipants(
            @PathVariable("scheduleId") Integer scheduleId,
            @AuthenticationPrincipal UserDetailsResponse userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", Map.of("code", "IAM-001", "message", "Authentication required")));
            }

            List<YogaParticipantResponse> response = yogaRegistrationService.getParticipantsForSchedule(scheduleId,
                    userDetails.getId());
            return ResponseEntity.ok(response);

        } catch (YogaBusinessException e) {
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode());
            errorDetail.put("message", e.getMessage());
            errorWrapper.put("error", errorDetail);

            HttpStatus status = HttpStatus.BAD_REQUEST;
            if ("YOGA-008".equals(e.getErrorCode()) || "YOGA-006".equals(e.getErrorCode())) {
                status = HttpStatus.FORBIDDEN;
            }
            return ResponseEntity.status(status).body(errorWrapper);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorWrapper = new HashMap<>();
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", "SYS-500");
            errorDetail.put("message", "System Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            errorWrapper.put("error", errorDetail);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
        }
    }
}
