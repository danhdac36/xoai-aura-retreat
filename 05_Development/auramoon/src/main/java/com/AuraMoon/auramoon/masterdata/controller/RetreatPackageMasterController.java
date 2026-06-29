package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.masterdata.service.RetreatPackageMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/master-data/retreat-packages")
@RequiredArgsConstructor
public class RetreatPackageMasterController {

    private final RetreatPackageMasterService retreatPackageService;

    @ModelAttribute("newPackage")
    public RetreatPackage defaultNewPackage() { return new RetreatPackage(); }
    @ModelAttribute("editPackage")
    public RetreatPackage defaultEditPackage() { return new RetreatPackage(); }

    @PostMapping("/create")
    public String createRetreatPackage(
            @ModelAttribute("newPackage") RetreatPackage newPkg,
            BindingResult bindingResult, Model model) {
        validateRetreatPackage(newPkg, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "retreat-packages");
            model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
            model.addAttribute("newPackage", newPkg);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        retreatPackageService.createRetreatPackage(newPkg);
        model.addAttribute("activeTab", "retreat-packages");
        model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
        model.addAttribute("successMsg", "Thêm gói nghỉ dưỡng thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateRetreatPackage(
            @PathVariable("id") Integer id,
            @ModelAttribute("editPackage") RetreatPackage editPkg,
            BindingResult bindingResult, Model model) {
        validateRetreatPackage(editPkg, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "retreat-packages");
            model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
            model.addAttribute("editPackage", editPkg);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        retreatPackageService.updateRetreatPackage(id, editPkg);
        model.addAttribute("activeTab", "retreat-packages");
        model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
        model.addAttribute("successMsg", "Cập nhật gói nghỉ dưỡng thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteRetreatPackage(@PathVariable("id") Integer id, Model model) {
        retreatPackageService.deleteRetreatPackage(id);
        model.addAttribute("activeTab", "retreat-packages");
        model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
        model.addAttribute("successMsg", "Xóa gói nghỉ dưỡng thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleRetreatPackageStatus(@PathVariable("id") Integer id, Model model) {
        RetreatPackage pkg = retreatPackageService.getRetreatPackageById(id);
        pkg.setIsActive(!pkg.getIsActive());
        retreatPackageService.updateRetreatPackage(id, pkg);
        model.addAttribute("activeTab", "retreat-packages");
        model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
        model.addAttribute("successMsg", "Thay đổi trạng thái hoạt động thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateRetreatPackage(RetreatPackage pkg, BindingResult bindingResult) {
        if (pkg.getPackageName() == null || pkg.getPackageName().trim().isEmpty()) {
            bindingResult.rejectValue("packageName", "error.packageName", "Tên gói không được để trống");
        }
        if (pkg.getTypePackage() == null || pkg.getTypePackage().trim().isEmpty()) {
            bindingResult.rejectValue("typePackage", "error.typePackage", "Loại gói không được để trống");
        }
        if (pkg.getDurationDays() == null || pkg.getDurationDays() <= 0) {
            bindingResult.rejectValue("durationDays", "error.durationDays", "Số ngày nghỉ phải lớn hơn 0");
        }
        if (pkg.getPrice() == null || pkg.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            bindingResult.rejectValue("price", "error.price", "Đơn giá phải lớn hơn hoặc bằng 0");
        }
    }
}
