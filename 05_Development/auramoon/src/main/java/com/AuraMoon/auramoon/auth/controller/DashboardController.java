package com.AuraMoon.auramoon.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("authDashboardController")
public class DashboardController {

    @GetMapping("/profile/home")
    public String guestHome() {
        return "auth/guest_home";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "auth/admin_home";
    }

    @GetMapping("/receptionist/home")
    public String receptionistHome() {
        return "auth/receptionist_home";
    }

    @GetMapping("/therapist/home")
    public String therapistHome() {
        return "auth/therapist_home";
    }

    @GetMapping("/F&B/home")
    public String cheffHome() {
        return "auth/cheff_home";
    }

    @GetMapping("/management/home")
    public String managerHome() {
        return "auth/manager_home";
    }
}
