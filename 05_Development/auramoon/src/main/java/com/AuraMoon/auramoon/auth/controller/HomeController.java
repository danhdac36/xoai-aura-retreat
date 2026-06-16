package com.AuraMoon.auramoon.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/home";
            } else if ("ROLE_GUEST".equals(role)) {
                return "redirect:/profile/home";
            } else if ("ROLE_RECEPTIONIST".equals(role)) {
                return "redirect:/receptionist/home";
            } else if ("ROLE_THERAPIST".equals(role)) {
                return "redirect:/therapist/home";
            } else if ("ROLE_CHEFF".equals(role)) {
                return "redirect:/F&B/home";
            } else if ("ROLE_MANAGER".equals(role)) {
                return "redirect:/management/home";
            }
        }

        return "redirect:/auth/login";
    }

    @GetMapping({"/", "/home"})
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
