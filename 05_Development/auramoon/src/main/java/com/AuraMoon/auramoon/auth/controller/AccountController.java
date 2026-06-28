package com.AuraMoon.auramoon.auth.controller;

import com.AuraMoon.auramoon.auth.dto.AccountDTO;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
import com.AuraMoon.auramoon.auth.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/manager/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private com.AuraMoon.auramoon.auth.repository.UserRepository userRepository;

    private Integer getActorId(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByEmail(principal.getName())
                .map(User::getId)
                .orElse(null);
    }

    private String getViewName(String view, String requestedWith) {
        return "XMLHttpRequest".equals(requestedWith) ? view + " :: main-content" : view;
    }

    @GetMapping
    public String listAccounts(@RequestParam(required = false) String email,
                             @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
                             @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                             Model model) {
        List<User> accounts = accountService.getAllAccounts(email, isDeleted);
        model.addAttribute("accounts", accounts);
        model.addAttribute("searchEmail", email);
        model.addAttribute("isDeleted", isDeleted);
        return getViewName("auth/account-list", requestedWith);
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith, Model model) {
        model.addAttribute("accountDTO", new AccountDTO());
        model.addAttribute("roles", roleRepository.findAll());
        return getViewName("auth/account-create", requestedWith);
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute("accountDTO") AccountDTO accountDTO,
                              BindingResult result,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                              Principal principal,
                              Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return getViewName("auth/account-create", requestedWith);
        }

        try {
            Integer actorId = getActorId(principal);
            accountService.createAccount(accountDTO, actorId);
            
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("successMessage", "Thêm tài khoản thành công!");
                return listAccounts(null, false, requestedWith, model);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm tài khoản thành công!");
            return "redirect:/manager/account";
        } catch (Exception e) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return getViewName("auth/account-create", requestedWith);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, 
                               @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                               Model model) {
        try {
            AccountDTO accountDTO = accountService.getAccountById(id);
            model.addAttribute("accountDTO", accountDTO);
            model.addAttribute("roles", roleRepository.findAll());
            return getViewName("auth/account-edit", requestedWith);
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("errorMessage", e.getMessage());
                return listAccounts(null, false, requestedWith, model);
            }
            return "redirect:/manager/account";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateAccount(@PathVariable Integer id,
                              @Valid @ModelAttribute("accountDTO") AccountDTO accountDTO,
                              BindingResult result,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                              Principal principal,
                              Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return getViewName("auth/account-edit", requestedWith);
        }

        try {
            Integer actorId = getActorId(principal);
            accountService.updateAccount(id, accountDTO, actorId);
            
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("successMessage", "Cập nhật chức vụ thành công!");
                return listAccounts(null, false, requestedWith, model);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chức vụ thành công!");
            return "redirect:/manager/account";
        } catch (Exception e) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return getViewName("auth/account-edit", requestedWith);
        }
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateAccount(@PathVariable Integer id, 
                                  @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                  Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            Integer actorId = getActorId(principal);
            accountService.deactivateAccount(id, actorId);
            
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("successMessage", "Vô hiệu hóa tài khoản thành công!");
                return listAccounts(null, false, requestedWith, model);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa tài khoản thành công!");
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("errorMessage", e.getMessage());
                return listAccounts(null, false, requestedWith, model);
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/account";
    }

    @PostMapping("/restore/{id}")
    public String restoreAccount(@PathVariable Integer id, 
                               @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                               Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            Integer actorId = getActorId(principal);
            accountService.restoreAccount(id, actorId);
            
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("successMessage", "Khôi phục tài khoản thành công!");
                return listAccounts(null, true, requestedWith, model);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Khôi phục tài khoản thành công!");
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("errorMessage", e.getMessage());
                return listAccounts(null, true, requestedWith, model);
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/account?isDeleted=true";
    }
}
