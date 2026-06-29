package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.entity.VillaType;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.masterdata.service.*;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/master-data")
@RequiredArgsConstructor
public class MasterDataController {

    private final RetreatPackageMasterService retreatPackageService;
    private final TreatmentServiceMasterService treatmentServiceService;
    private final VillaTypeMasterService villaTypeService;
    private final VillaMasterService villaService;
    private final YogaClassMasterService yogaClassService;
    private final MenuItemMasterService menuItemService;

    // Default model attributes for safety against Thymeleaf rendering errors
    @ModelAttribute("newPackage")
    public RetreatPackage defaultNewPackage() { return new RetreatPackage(); }
    @ModelAttribute("editPackage")
    public RetreatPackage defaultEditPackage() { return new RetreatPackage(); }

    @ModelAttribute("newSpaService")
    public TreatmentService defaultNewSpaService() { return new TreatmentService(); }
    @ModelAttribute("editSpaService")
    public TreatmentService defaultEditSpaService() { return new TreatmentService(); }

    @ModelAttribute("newVillaType")
    public VillaType defaultNewVillaType() { return new VillaType(); }
    @ModelAttribute("editVillaType")
    public VillaType defaultEditVillaType() { return new VillaType(); }

    @ModelAttribute("newVilla")
    public Villa defaultNewVilla() { return new Villa(); }
    @ModelAttribute("editVilla")
    public Villa defaultEditVilla() { return new Villa(); }

    @ModelAttribute("newYogaClass")
    public YogaClass defaultNewYogaClass() { return new YogaClass(); }
    @ModelAttribute("editYogaClass")
    public YogaClass defaultEditYogaClass() { return new YogaClass(); }

    @ModelAttribute("newMenuItem")
    public MenuItem defaultNewMenuItem() { return new MenuItem(); }
    @ModelAttribute("editMenuItem")
    public MenuItem defaultEditMenuItem() { return new MenuItem(); }

    // 1. View main page
    @GetMapping
    public String viewMasterDataPage(Model model) {
        model.addAttribute("activeTab", "retreat-packages");
        populateCategoryModel("retreat-packages", model);
        return "admin/master-data/index";
    }

    // 2. GET Category Tab Content (AJAX)
    @GetMapping("/{category}")
    public String getTabContent(@PathVariable("category") String category, Model model) {
        model.addAttribute("activeTab", category);
        populateCategoryModel(category, model);
        return "admin/master-data/fragments :: tabContent";
    }

    private void populateCategoryModel(String category, Model model) {
        if ("retreat-packages".equalsIgnoreCase(category)) {
            model.addAttribute("packages", retreatPackageService.getAllRetreatPackages());
        } else if ("spa-services".equalsIgnoreCase(category)) {
            model.addAttribute("spaServices", treatmentServiceService.getAllTreatmentServices());
        } else if ("villa-types".equalsIgnoreCase(category)) {
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        } else if ("villas".equalsIgnoreCase(category)) {
            model.addAttribute("villas", villaService.getAllVillas());
            model.addAttribute("villaTypes", villaTypeService.getAllVillaTypes());
        } else if ("yoga-classes".equalsIgnoreCase(category)) {
            model.addAttribute("yogaClasses", yogaClassService.getAllYogaClasses());
        } else if ("menu-items".equalsIgnoreCase(category)) {
            model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        }
    }
}
