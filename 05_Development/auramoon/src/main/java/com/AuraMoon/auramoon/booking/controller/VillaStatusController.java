package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/receptionist/villa")
@RequiredArgsConstructor
public class VillaStatusController {

    private final VillaRepository villaRepository;
    private final VillaService villaService;

    @GetMapping
    public String showVillaDiagram(Model model) {
        List<com.AuraMoon.auramoon.booking.dto.VillaDisplayDTO> villas = villaService.getAllVillasForDisplay();
        model.addAttribute("villas", villas);
        return "reception/villas";
    }

    @PostMapping("/status")
    public String updateVillaStatus(@RequestParam("villaId") Integer id,
                                    @RequestParam("villaStatus") String villaStatus,
                                    @RequestParam("cleaningStatus") String cleaningStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            villaService.updateVillaStatuses(id, villaStatus, cleaningStatus);
            redirectAttributes.addAttribute("success", "status_updated");
            return "redirect:/receptionist/villa";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/receptionist/villa";
        }
    }
}
