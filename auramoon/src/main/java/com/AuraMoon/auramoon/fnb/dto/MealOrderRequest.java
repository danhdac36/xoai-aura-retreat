package com.AuraMoon.auramoon.fnb.dto;

import java.util.List;

public class MealOrderRequest {

    private Integer guestId;
    private Integer bookingId;
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