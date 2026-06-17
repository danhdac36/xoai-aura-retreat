package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.BookingRequestDTO;
import com.AuraMoon.auramoon.booking.dto.BookingResponseDTO;
import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import com.AuraMoon.auramoon.booking.service.BookingService;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.AuraMoon.auramoon.auth.dto.response.UserDetailsResponse;
import com.AuraMoon.auramoon.auth.entity.User;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RetreatPackageService retreatPackageService;
    private final VillaTypeRepository villaTypeRepository;

    @GetMapping("/create")
    public String showCreateForm(@RequestParam("packageId") Integer packageId, Model model) {
        RetreatPackageDTO retreatPackage = retreatPackageService.getPackageById(packageId);
        model.addAttribute("pkg", retreatPackage);
        model.addAttribute("villaTypes", villaTypeRepository.findAll());
        model.addAttribute("bookingRequest", new BookingRequestDTO());
        return "booking/create-form";
    }

    @PostMapping("/create")
    public String createBooking(@ModelAttribute("bookingRequest") BookingRequestDTO request,
            @AuthenticationPrincipal UserDetailsResponse currentUser) {
        Integer guestId = null;
        if (currentUser != null) {
            guestId = currentUser.getId();
        }
        /*
         * if (currentUser == null) {
         * return "redirect:/login";
         * }
         * Integer guestId = currentUser.getId();
         */
        try {
            BookingResponseDTO response = bookingService.createBooking(guestId, request);
            // Redirect sang controller thanh toán đặt cọc của module billing
            return "redirect:/billing/deposit/pay?bookingId=" + response.getBookingId();
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            try {
                errorMsg = java.net.URLEncoder.encode(errorMsg, "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                // ignore
            }
            return "redirect:/packages/" + request.getRetreatPackageId() + "?error=" + errorMsg;
        }
    }

    @GetMapping("/success")
    public String bookingSuccess(@RequestParam("bookingId") Integer bookingId,
            Model model) {
        model.addAttribute("bookingId", bookingId);
        return "booking/success";
    }
}
