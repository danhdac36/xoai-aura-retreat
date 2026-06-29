package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.masterdata.service.TreatmentServiceMasterService;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/master-data/spa-services")
@RequiredArgsConstructor
public class TreatmentServiceMasterController {

    private final TreatmentServiceMasterService treatmentServiceService;

    @ModelAttribute("newSpaService")
    public TreatmentService defaultNewSpaService() { return new TreatmentService(); }
    @ModelAttribute("editSpaService")
    public TreatmentService defaultEditSpaService() { return new TreatmentService(); }

    @PostMapping("/create")
    public String createSpaService(
            @ModelAttribute("newSpaService") TreatmentService newSvc,
            BindingResult bindingResult, Model model) {
        validateSpaService(newSvc, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "spa-services");
            model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
            model.addAttribute("newSpaService", newSvc);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        treatmentServiceService.createTreatmentService(newSvc);
        model.addAttribute("activeTab", "spa-services");
        model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
        model.addAttribute("successMsg", "Thêm dịch vụ Spa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateSpaService(
            @PathVariable("id") Integer id,
            @ModelAttribute("editSpaService") TreatmentService editSvc,
            BindingResult bindingResult, Model model) {
        validateSpaService(editSvc, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "spa-services");
            model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
            model.addAttribute("editSpaService", editSvc);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        treatmentServiceService.updateTreatmentService(id, editSvc);
        model.addAttribute("activeTab", "spa-services");
        model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
        model.addAttribute("successMsg", "Cập nhật dịch vụ Spa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteSpaService(@PathVariable("id") Integer id, Model model) {
        treatmentServiceService.deleteTreatmentService(id);
        model.addAttribute("activeTab", "spa-services");
        model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
        model.addAttribute("successMsg", "Xóa dịch vụ Spa thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleSpaServiceStatus(@PathVariable("id") Integer id, Model model) {
        TreatmentService svc = treatmentServiceService.getTreatmentServiceById(id);
        svc.setIsAvailable(!svc.getIsAvailable());
        treatmentServiceService.updateTreatmentService(id, svc);
        model.addAttribute("activeTab", "spa-services");
        model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
        model.addAttribute("successMsg", "Thay đổi trạng thái khả dụng thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateSpaService(TreatmentService svc, BindingResult bindingResult) {
        if (svc.getTreatmentCode() == null || svc.getTreatmentCode().trim().isEmpty()) {
            bindingResult.rejectValue("treatmentCode", "error.treatmentCode", "Mã dịch vụ không được để trống");
        }
        if (svc.getServiceName() == null || svc.getServiceName().trim().isEmpty()) {
            bindingResult.rejectValue("serviceName", "error.serviceName", "Tên dịch vụ không được để trống");
        }
        if (svc.getDurationMinutes() == null || svc.getDurationMinutes() <= 0) {
            bindingResult.rejectValue("durationMinutes", "error.durationMinutes", "Thời lượng phải lớn hơn 0 phút");
        }
        if (svc.getPrice() == null || svc.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            bindingResult.rejectValue("price", "error.price", "Đơn giá phải lớn hơn hoặc bằng 0");
        }
    }
}
