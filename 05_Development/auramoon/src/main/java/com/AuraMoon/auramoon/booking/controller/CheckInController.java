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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/receptionist")
@RequiredArgsConstructor
public class CheckInController {

    private final BookingRepository bookingRepository;
    private final VillaRepository villaRepository;
    private final CheckInService checkInService;

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        List<com.AuraMoon.auramoon.booking.dto.BookingDisplayDTO> bookings = checkInService.getAllBookingsForDisplay();
        // Lấy danh sách các Villa đang trống (AVAILABLE) để lễ tân gán khi check-in
        model.addAttribute("bookings", bookings);
        model.addAttribute("villas", villaRepository.findByVillaType_IdAndVillaStatusAndIsDeleteFalse(1, "AVAILABLE")); // default type 1 or list all
        model.addAttribute("allVillas", villaRepository.findAll());
        model.addAttribute("checkInRequest", new CheckInRequestDTO());
        return "reception/bookings";
    }

    @PostMapping("/check-in")
    public String performCheckIn(@ModelAttribute("checkInRequest") CheckInRequestDTO request,
                                 RedirectAttributes redirectAttributes) {
        try {
            checkInService.performCheckIn(request);
            redirectAttributes.addAttribute("success", "Check-in thành công!");
            return "redirect:/receptionist/bookings";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/receptionist/bookings";
        }
    }
}
