package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.VillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reception/villas")
@RequiredArgsConstructor
public class VillaStatusController {

    private final VillaRepository villaRepository;
    private final VillaService villaService;

    @GetMapping
    public String showVillaDiagram(Model model) {
        List<Villa> villas = villaRepository.findAll();
        model.addAttribute("villas", villas);
        return "reception/villas";
    }

    @PostMapping("/{id}/status")
    public String updateVillaStatus(@PathVariable("id") Integer id,
                                    @RequestParam("villaStatus") String villaStatus,
                                    @RequestParam("cleaningStatus") String cleaningStatus) {
        try {
            villaService.updateVillaStatuses(id, villaStatus, cleaningStatus);
            return "redirect:/reception/villas?success=Cập nhật trạng thái Villa thành công!";
        } catch (Exception e) {
            return "redirect:/reception/villas?error=" + e.getMessage();
        }
    }
}
