package com.AuraMoon.auramoon.booking.controller;

import com.AuraMoon.auramoon.booking.dto.CheckInRequestDTO;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import com.AuraMoon.auramoon.booking.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reception")
@RequiredArgsConstructor
public class CheckInController {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final CheckInService checkInService;

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        List<Booking> bookings = bookingRepository.findAll();
        // Lấy danh sách các Villa đang trống (AVAILABLE) để lễ tân gán khi check-in
        model.addAttribute("bookings", bookings);
        model.addAttribute("villas", villaRepository.findByVillaType_IdAndVillaStatusAndIsDeleteFalse(1, "AVAILABLE")); // default type 1 or list all
        model.addAttribute("allVillas", villaRepository.findAll());
        model.addAttribute("checkInRequest", new CheckInRequestDTO());
        return "reception/bookings";
    }

    @PostMapping("/checkin")
    public String performCheckIn(@ModelAttribute("checkInRequest") CheckInRequestDTO request, Model model) {
        try {
            checkInService.performCheckIn(request);
            return "redirect:/reception/bookings?success=Check-in thành công!";
        } catch (Exception e) {
            return "redirect:/reception/bookings?error=" + e.getMessage();
        }
    }
}
