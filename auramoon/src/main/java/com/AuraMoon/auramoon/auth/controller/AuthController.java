package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.request.RegisterDto;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller

public class AuthController {

    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterDto registerDto, Model model) {
        try {
            userService.registerUser(registerDto);
            logger.info("User registered successfully: " + registerDto.getEmail());
            model.addAttribute("success", "Đăng ký thành công!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            logger.warning("Registration error: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDto", registerDto);
            return "auth/registration";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/auth")
    public String doLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // Xử lý đăng nhập
        User user = userService.authenticate(email, password);
        if (user == null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
            return "auth/login";
        }
        // Lưu user vào session
        session.setAttribute("currentUser", user);
        // Optional: set session timeout (seconds)
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}
