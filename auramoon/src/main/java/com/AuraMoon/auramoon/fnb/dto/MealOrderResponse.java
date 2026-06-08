package com.AuraMoon.auramoon.fnb.dto;

public class MealOrderResponse {

    private String message;
    private String status;

    public MealOrderResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}