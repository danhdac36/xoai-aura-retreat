package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.masterdata.service.YogaClassMasterService;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/master-data/yoga-classes")
@RequiredArgsConstructor
public class YogaClassMasterController {

    private final YogaClassMasterService yogaClassService;

    @ModelAttribute("newYogaClass")
    public YogaClass defaultNewYogaClass() { return new YogaClass(); }
    @ModelAttribute("editYogaClass")
    public YogaClass defaultEditYogaClass() { return new YogaClass(); }

    @PostMapping("/create")
    public String createYogaClass(
            @ModelAttribute("newYogaClass") YogaClass newClass,
            BindingResult bindingResult, Model model) {
        validateYogaClass(newClass, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "yoga-classes");
            model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
            model.addAttribute("newYogaClass", newClass);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        yogaClassService.createYogaClass(newClass);
        model.addAttribute("activeTab", "yoga-classes");
        model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
        model.addAttribute("successMsg", "Thêm lớp Yoga thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateYogaClass(
            @PathVariable("id") Integer id,
            @ModelAttribute("editYogaClass") YogaClass editClass,
            BindingResult bindingResult, Model model) {
        validateYogaClass(editClass, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "yoga-classes");
            model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
            model.addAttribute("editYogaClass", editClass);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        yogaClassService.updateYogaClass(id, editClass);
        model.addAttribute("activeTab", "yoga-classes");
        model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
        model.addAttribute("successMsg", "Cập nhật lớp Yoga thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteYogaClass(@PathVariable("id") Integer id, Model model) {
        yogaClassService.deleteYogaClass(id);
        model.addAttribute("activeTab", "yoga-classes");
        model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
        model.addAttribute("successMsg", "Xóa lớp Yoga thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateYogaClass(YogaClass yc, BindingResult bindingResult) {
        if (yc.getClassName() == null || yc.getClassName().trim().isEmpty()) {
            bindingResult.rejectValue("className", "error.className", "Tên lớp yoga không được để trống");
        }
        if (yc.getDurationMinutes() == null || yc.getDurationMinutes() <= 0) {
            bindingResult.rejectValue("durationMinutes", "error.durationMinutes", "Thời lượng phải lớn hơn 0 phút");
        }
    }
}
