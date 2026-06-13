package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.BookingRequestDTO;
import com.AuraMoon.auramoon.booking.dto.BookingResponseDTO;
import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.repository.VillaTypeRepository;
import com.AuraMoon.auramoon.booking.service.BookingService;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String createBooking(@ModelAttribute("bookingRequest") BookingRequestDTO request) {
        // Giả lập guestId = 1 (hoặc lấy từ session nếu có hệ thống Auth)
        Integer guestId = 1;
        try {
            BookingResponseDTO response = bookingService.createBooking(guestId, request);
            // Redirect sang controller thanh toán đặt cọc của module billing
            return "redirect:/billing/deposit/pay?bookingId=" + response.getBookingId();
        } catch (Exception e) {
            return "redirect:/packages/" + request.getRetreatPackageId() + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/success")
    public String bookingSuccess(@RequestParam("bookingId") Integer bookingId,
                                 Model model) {
        model.addAttribute("bookingId", bookingId);
        return "booking/success";
    }
}
