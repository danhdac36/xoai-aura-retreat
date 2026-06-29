package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.masterdata.service.VillaMasterService;
import com.AuraMoon.auramoon.masterdata.service.VillaTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/master-data/villas")
@RequiredArgsConstructor
public class VillaMasterController {

    private final VillaMasterService villaService;
    private final VillaTypeMasterService villaTypeService;

    @ModelAttribute("newVilla")
    public Villa defaultNewVilla() { return new Villa(); }
    @ModelAttribute("editVilla")
    public Villa defaultEditVilla() { return new Villa(); }

    @PostMapping("/create")
    public String createVilla(
            @ModelAttribute("newVilla") Villa newVilla,
            BindingResult bindingResult, Model model) {
        validateVilla(newVilla, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "villas");
            model.addAttribute("villas", villaService.getAllVillas());
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
            model.addAttribute("newVilla", newVilla);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        villaService.createVilla(newVilla);
        model.addAttribute("activeTab", "villas");
        model.addAttribute("villas", villaService.getAllVillas());
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Thêm Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateVilla(
            @PathVariable("id") Integer id,
            @ModelAttribute("editVilla") Villa editVilla,
            BindingResult bindingResult, Model model) {
        validateVilla(editVilla, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "villas");
            model.addAttribute("villas", villaService.getAllVillas());
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
            model.addAttribute("editVilla", editVilla);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        villaService.updateVilla(id, editVilla);
        model.addAttribute("activeTab", "villas");
        model.addAttribute("villas", villaService.getAllVillas());
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Cập nhật Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteVilla(@PathVariable("id") Integer id, Model model) {
        villaService.deleteVilla(id);
        model.addAttribute("activeTab", "villas");
        model.addAttribute("villas", villaService.getAllVillas());
        model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        model.addAttribute("successMsg", "Xóa Villa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateVilla(Villa villa, BindingResult bindingResult) {
        if (villa.getVillaCode() == null || villa.getVillaCode().trim().isEmpty()) {
            bindingResult.rejectValue("villaCode", "error.villaCode", "Mã Villa không được để trống");
        }
        if (villa.getLimitPerson() == null || villa.getLimitPerson() <= 0) {
            bindingResult.rejectValue("limitPerson", "error.limitPerson", "Giới hạn số người phải lớn hơn 0");
        }
        if (villa.getVillaType() == null || villa.getVillaType().getId() == null) {
            bindingResult.rejectValue("villaType", "error.villaType", "Hạng Villa không hợp lệ");
        }
    }
}
