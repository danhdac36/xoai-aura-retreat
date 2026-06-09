package com.AuraMoon.auramoon.fnb.dto;

import java.time.LocalDate;
import java.util.List;

public class MealSelectionRequest {

    private Integer guestId;
    private Integer bookingId;
    private LocalDate mealDate;
    private String mealType;
    private List<Integer> menuItemIds;
    private String note;

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public List<Integer> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Integer> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}