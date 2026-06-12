package com.AuraMoon.auramoon.fnb.dto;

import java.util.List;

public class MealPrepResponse {
    private Integer orderId;
    private Integer bookingId;
    private Integer guestId;
    private String guestName;
    private String roomNumber;
    private String orderedAt;
    private String placeOrder;
    private String note;
    private String orderStatus;
    private String foodAllergies;
    private List<OrderItemDto> items;

    public MealPrepResponse() {}

    public MealPrepResponse(Integer orderId, Integer bookingId, Integer guestId, String guestName, 
                            String roomNumber, String orderedAt, String placeOrder, String note, 
                            String orderStatus, String foodAllergies, List<OrderItemDto> items) {
        this.orderId = orderId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.orderedAt = orderedAt;
        this.placeOrder = placeOrder;
        this.note = note;
        this.orderStatus = orderStatus;
        this.foodAllergies = foodAllergies;
        this.items = items;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(String orderedAt) {
        this.orderedAt = orderedAt;
    }

    public String getPlaceOrder() {
        return placeOrder;
    }

    public void setPlaceOrder(String placeOrder) {
        this.placeOrder = placeOrder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFoodAllergies() {
        return foodAllergies;
    }

    public void setFoodAllergies(String foodAllergies) {
        this.foodAllergies = foodAllergies;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
