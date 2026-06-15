package com.AuraMoon.auramoon.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping({ "/", "/home" })
    public String showHomePage(Model model) {
        model.addAttribute("pageTitle", "Trang Chủ - Xoai Aura Retreat");
        return "public/home";
    }

    @GetMapping("/villas")
    public String showVillasPage(Model model) {
        model.addAttribute("pageTitle", "Villas - Xoai Aura Retreat");
        return "public/villas";
    }

    @GetMapping("/wellness")
    public String showWellnessPage(Model model) {
        model.addAttribute("pageTitle", "Retreat & Wellness - Xoai Aura Retreat");
        return "public/wellness";
    }

    @GetMapping("/spa")
    public String showSpaPage(Model model) {
        model.addAttribute("pageTitle", "Aura Spa & Therapies - Xoai Aura Retreat");
        return "public/spa";
    }

    @GetMapping("/culinary")
    public String showCulinaryPage(Model model) {
        model.addAttribute("pageTitle", "Aura Culinary & Dining - Xoai Aura Retreat");
        return "public/culinary";
    }

    @PostMapping
    public String loginPost() {

        return "redirect:/home";
    }
}
