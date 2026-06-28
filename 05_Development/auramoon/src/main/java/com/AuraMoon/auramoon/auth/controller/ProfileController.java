package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.dto.PersonalProfileDto;
import com.AuraMoon.auramoon.auth.dto.MyAccountDto;
import com.AuraMoon.auramoon.auth.dto.ChangePasswordDto;
import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.auth.service.IProfileService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private IProfileService profileService;

    @GetMapping("/health")
    public String viewHealthProfile(Model model, Authentication authentication,
                                    @RequestParam(value = "success", required = false) String success,
                                    @RequestParam(value = "deleted", required = false) String deleted,
                                    @RequestParam(value = "error", required = false) String paramError) {
        Integer userId = extractUserId(authentication);
        if (userId == null) return "redirect:/auth/login";

        SensitiveProfileDto dto = profileService.getSensitiveProfile(userId);
        model.addAttribute("profileDto", dto);

        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Hồ sơ sức khỏe & dinh dưỡng đã được lưu thành công!");
        } else if ("true".equals(deleted)) {
            model.addAttribute("successMessage", "Dữ liệu nhạy cảm đã được xóa thành công!");
        }
        
        if ("true".equals(paramError)) {
            model.addAttribute("error", "Đã xảy ra lỗi khi xóa dữ liệu. Vui lòng thử lại.");
        }

        return "auth/update-health-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDto") SensitiveProfileDto dto,
                                BindingResult result, Model model, Authentication authentication) {
        // Exception E1
        if (!dto.isHasConsent()) {
            model.addAttribute("error", "MSG-03: Bạn phải đồng ý với các điều khoản bảo mật dữ liệu y tế để tiếp tục");
            return "auth/update-health-profile";

        }
      
        if (result.hasErrors()) {
            return "auth/update-health-profile";
        }

        try {
            Integer userId = extractUserId(authentication);
            if (userId == null) return "redirect:/auth/login";

            profileService.saveSensitiveProfile(dto, userId);
            return "redirect:/profile/health?success=true";
        } catch (Exception e) {
            // Exception E2
            model.addAttribute("error", "MSG-15: Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return "auth/update-health-profile";
        }
    }

    @PostMapping("/health/delete")
    public String deleteHealthProfile(Authentication authentication) {
        try {
            Integer userId = extractUserId(authentication);
            if (userId == null) return "redirect:/auth/login";

            profileService.deleteSensitiveProfile(userId);
            return "redirect:/profile/health?deleted=true";
        } catch (Exception e) {
            return "redirect:/profile/health?error=true";
        }
    }

    private Integer extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsResponse) {
            return ((UserDetailsResponse) authentication.getPrincipal()).getId();
        }
        return null;
    }

    @GetMapping("/me")
    public String viewPersonalProfile(Model model, Authentication authentication,
                              @RequestParam(value = "success", required = false) String success) {
        Integer userId = extractUserId(authentication);
        if (userId == null) return "redirect:/auth/login";

        PersonalProfileDto dto = profileService.getPersonalProfile(userId);
        model.addAttribute("personalProfileDto", dto);

        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công!");
        }

        return "auth/personal-profile";
    }

    @PostMapping("/me/update")
    public String updatePersonalProfile(@Valid @ModelAttribute("personalProfileDto") PersonalProfileDto dto,
                                BindingResult result, Model model, Authentication authentication) {
        if (dto.getDateOfBirth() != null && dto.getDateOfBirth().isAfter(java.time.LocalDate.now().minusYears(18))) {
            result.rejectValue("dateOfBirth", "error.dateOfBirth", "Bạn phải đủ 18 tuổi trở lên");
        }

        if (result.hasErrors()) {
            return "auth/personal-profile";
        }

        try {
            Integer userId = extractUserId(authentication);
            if (userId == null) return "redirect:/auth/login";

            profileService.savePersonalProfile(dto, userId);
            return "redirect:/profile/me?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return "auth/personal-profile";
        }
    }

    @GetMapping("/my-account")
    public String viewMyAccount(Model model, Authentication authentication,
                                @RequestParam(value = "success", required = false) String success) {
        Integer userId = extractUserId(authentication);
        if (userId == null) return "redirect:/auth/login";

        MyAccountDto dto = profileService.getMyAccountInfo(userId);
        model.addAttribute("myAccountDto", dto);
        
        if (!model.containsAttribute("changePasswordDto")) {
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
        }

        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
        }

        return "auth/my-account";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto,
                                 BindingResult result, Model model, Authentication authentication) {
        Integer userId = extractUserId(authentication);
        if (userId == null) return "redirect:/auth/login";

        if (result.hasErrors()) {
            MyAccountDto accountDto = profileService.getMyAccountInfo(userId);
            model.addAttribute("myAccountDto", accountDto);
            return "auth/my-account";
        }

        try {
            profileService.changePassword(userId, dto);
            return "redirect:/profile/my-account?success=true";
        } catch (IllegalArgumentException e) {
            MyAccountDto accountDto = profileService.getMyAccountInfo(userId);
            model.addAttribute("myAccountDto", accountDto);
            model.addAttribute("error", e.getMessage());
            return "auth/my-account";
        } catch (Exception e) {
            MyAccountDto accountDto = profileService.getMyAccountInfo(userId);
            model.addAttribute("myAccountDto", accountDto);
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
            return "auth/my-account";
        }
    }
}
