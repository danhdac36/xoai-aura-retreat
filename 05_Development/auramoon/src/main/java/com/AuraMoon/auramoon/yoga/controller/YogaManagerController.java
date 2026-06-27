package com.AuraMoon.auramoon.yoga.controller;

import com.AuraMoon.auramoon.yoga.dto.YogaManagerDto.*;
import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.service.IYogaManagerService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager/yoga")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class YogaManagerController {

    private final IYogaManagerService yogaManagerService;

    public YogaManagerController(IYogaManagerService yogaManagerService) {
        this.yogaManagerService = yogaManagerService;
    }

    // --- YOGA CLASS VIEWS & FORMS ---

    @GetMapping("/classes")
    public String showClassesPage(Model model) {
        List<YogaClassResponse> classes = yogaManagerService.getAllActiveClasses();
        model.addAttribute("classes", classes);
        if (!model.containsAttribute("classRequest")) {
            model.addAttribute("classRequest", new YogaClassRequest());
        }
        return "manager/yoga-classes";
    }

    @PostMapping("/classes/create")
    public String createClass(
            @Valid @ModelAttribute("classRequest") YogaClassRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.classRequest", bindingResult);
            redirectAttributes.addFlashAttribute("classRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/manager/yoga/classes";
        }

        try {
            yogaManagerService.createClass(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm lớp học mới thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("classRequest", request);
        }
        return "redirect:/manager/yoga/classes";
    }

    @PostMapping("/classes/edit/{classId}")
    public String updateClass(
            @PathVariable Integer classId,
            @Valid @ModelAttribute("classRequest") YogaClassRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/manager/yoga/classes";
        }

        try {
            yogaManagerService.updateClass(classId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin lớp học thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/yoga/classes";
    }

    @PostMapping("/classes/delete/{classId}")
    public String deleteClass(@PathVariable Integer classId, RedirectAttributes redirectAttributes) {
        try {
            yogaManagerService.deleteClass(classId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa lớp học thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/yoga/classes";
    }

    // --- YOGA SCHEDULE VIEWS & FORMS ---

    @GetMapping("/schedules")
    public String showSchedulesPage(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<YogaScheduleResponse> schedules = yogaManagerService.getSchedulesByDate(date);
        List<YogaClassResponse> classes = yogaManagerService.getAllActiveClasses();
        List<YogaInstructor> instructors = yogaManagerService.getAllActiveInstructors();

        model.addAttribute("schedules", schedules);
        model.addAttribute("classes", classes);
        model.addAttribute("instructors", instructors);
        model.addAttribute("selectedDate", date);
        if (!model.containsAttribute("scheduleRequest")) {
            model.addAttribute("scheduleRequest", new YogaScheduleRequest());
        }
        return "manager/yoga-schedules";
    }

    @PostMapping("/schedules/create")
    public String createSchedule(
            @Valid @ModelAttribute("scheduleRequest") YogaScheduleRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.scheduleRequest", bindingResult);
            redirectAttributes.addFlashAttribute("scheduleRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/manager/yoga/schedules";
        }

        try {
            yogaManagerService.createSchedule(request);
            redirectAttributes.addFlashAttribute("successMessage", "Xếp lịch học thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("scheduleRequest", request);
        }
        return "redirect:/manager/yoga/schedules";
    }

    @PostMapping("/schedules/edit/{scheduleId}")
    public String updateSchedule(
            @PathVariable Integer scheduleId,
            @Valid @ModelAttribute("scheduleRequest") YogaScheduleRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/manager/yoga/schedules";
        }

        try {
            yogaManagerService.updateSchedule(scheduleId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lịch học thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/yoga/schedules";
    }

    @PostMapping("/schedules/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable Integer scheduleId, RedirectAttributes redirectAttributes) {
        try {
            yogaManagerService.deleteSchedule(scheduleId);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch học thành công");
        } catch (YogaBusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/yoga/schedules";
    }
}
