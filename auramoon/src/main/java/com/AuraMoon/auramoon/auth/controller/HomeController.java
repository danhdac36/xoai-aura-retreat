package com.AuraMoon.auramoon.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
