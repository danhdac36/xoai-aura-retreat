package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.masterdata.service.VillaTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/master-data/villa-types")
@RequiredArgsConstructor
public class VillaTypeMasterController {

    private final VillaTypeMasterService villaTypeService;

    @ModelAttribute("newVillaType")
    public VillaType defaultNewVillaType() { return new VillaType(); }
    @ModelAttribute("editVillaType")
    public VillaType defaultEditVillaType() { return new VillaType(); }

    @PostMapping("/create")
    public String createVillaType(
            @ModelAttribute("newVillaType") VillaType newType,
            BindingResult bindingResult, Model model) {
        validateVillaType(newType, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "villa-types");
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
            model.addAttribute("newVillaType", newType);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        villaTypeService.createVillaType(newType);
        model.addAttribute("activeTab", "villa-types");
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Thêm loại Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateVillaType(
            @PathVariable("id") Integer id,
            @ModelAttribute("editVillaType") VillaType editType,
            BindingResult bindingResult, Model model) {
        validateVillaType(editType, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "villa-types");
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
            model.addAttribute("editVillaType", editType);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        villaTypeService.updateVillaType(id, editType);
        model.addAttribute("activeTab", "villa-types");
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Cập nhật loại Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteVillaType(@PathVariable("id") Integer id, Model model) {
        villaTypeService.deleteVillaType(id);
        model.addAttribute("activeTab", "villa-types");
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Xóa loại Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateVillaType(VillaType type, BindingResult bindingResult) {
        if (type.getTypeName() == null || type.getTypeName().trim().isEmpty()) {
            bindingResult.rejectValue("typeName", "error.typeName", "Tên hạng villa không được để trống");
        }
        if (type.getPricePerDay() == null || type.getPricePerDay().compareTo(BigDecimal.ZERO) < 0) {
            bindingResult.rejectValue("pricePerDay", "error.pricePerDay", "Giá ngày phải lớn hơn hoặc bằng 0");
        }
    }
}
