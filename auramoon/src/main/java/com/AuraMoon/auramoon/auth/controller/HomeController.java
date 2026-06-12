package com.AuraMoon.auramoon.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String login() {
        return "auth/home";
    }

    @PostMapping
    public String loginPost() {

        return "redirect:/home";
    }
}
