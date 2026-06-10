package com.AuraMoon.auramoon.fnb.controller;

import com.AuraMoon.auramoon.fnb.dto.MealSelectionRequest;
import com.AuraMoon.auramoon.fnb.dto.MealSelectionResponse;
import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.service.MealSelectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fnb/selection")
public class MealSelectionController {

    private final MealSelectionService mealSelectionService;

    public MealSelectionController(MealSelectionService mealSelectionService) {
        this.mealSelectionService = mealSelectionService;
    }

    @GetMapping("/menu")
    public List<com.AuraMoon.auramoon.fnb.dto.MenuItemResponse> getFilteredMenu(@RequestParam Integer guestId) {
        return mealSelectionService.getFilteredMenuForGuest(guestId);
    }

    @PostMapping("/select")
    public MealSelectionResponse selectDailyMeals(@RequestBody MealSelectionRequest request) {
        return mealSelectionService.selectDailyMeals(request);
    }
}
