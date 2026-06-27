package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.request.UserRegistrationDto;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new UserRegistrationDto());
        return "auth/registration"; // Maps to registration.html
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") UserRegistrationDto registrationDto,
            BindingResult result, Model model) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp!");
        }

        if (result.hasErrors()) {
            return "auth/registration";
        }

        try {
            authService.register(registrationDto);
            model.addAttribute("successMessage", "Link kích hoạt đã được gửi vào hòm thư " + registrationDto.getEmail()
                    + " của bạn. Vui lòng kiểm tra email.");
            return "auth/register-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/registration";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng liên hệ Admin.");
            return "auth/registration";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean isVerified = authService.verifyEmail(token);
        if (isVerified) {
            model.addAttribute("status", "success");
            model.addAttribute("message",
                    "Tài khoản của bạn đã được kích hoạt thành công! Giờ đây bạn có thể đăng nhập.");
        } else {
            model.addAttribute("status", "error");
            model.addAttribute("message", "Link kích hoạt không hợp lệ hoặc đã hết hạn.");
        }
        return "auth/verify-result";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }
}
