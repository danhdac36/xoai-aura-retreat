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
            // Redirect sang trang giả lập cổng thanh toán
            return "redirect:/booking/payment/callback?bookingId=" + response.getBookingId() + "&transactionCode=TX_AURA_" + System.currentTimeMillis();
        } catch (Exception e) {
            return "redirect:/packages/" + request.getRetreatPackageId() + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/payment/callback")
    public String paymentCallback(@RequestParam("bookingId") Integer bookingId,
                                  @RequestParam("transactionCode") String transactionCode,
                                  Model model) {
        try {
            bookingService.confirmPayment(bookingId, transactionCode);
            model.addAttribute("bookingId", bookingId);
            model.addAttribute("transactionCode", transactionCode);
            return "booking/success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
