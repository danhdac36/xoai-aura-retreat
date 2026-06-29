package com.AuraMoon.auramoon.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("authDashboardController")
public class DashboardController {

    @GetMapping("/admin/home")
    public String adminHome() {
        return "auth/admin_home";
    }

    @GetMapping("/manager/home")
    public String managerHome() {
        return "auth/manager_home";
    }

    @GetMapping("/receptionist/home")
    public String receptionistHome() {
        return "auth/receptionist_home";
    }

    @GetMapping("/therapist/home")
    public String therapistHome() {
        return "auth/therapist_home";
    }

    @GetMapping("/fnb/chef/home")
    public String cheffHome() {
        return "auth/cheff_home";
    }

    @GetMapping("/instructor/yoga/home")
    public String instructorHome() {
        return "auth/instructor_home";
    }
}
