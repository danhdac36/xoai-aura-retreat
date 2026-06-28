package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/villas")
@RequiredArgsConstructor
public class PublicVillaController {

    private final VillaTypeRepository villaTypeRepository;

    @GetMapping
    public String showNewVillasPage(Model model) {
        model.addAttribute("pageTitle", "Villas - Xoai Aura Retreat");
        model.addAttribute("villaTypes", villaTypeRepository.findAll());
        return "public/new-villas";
    }
}
