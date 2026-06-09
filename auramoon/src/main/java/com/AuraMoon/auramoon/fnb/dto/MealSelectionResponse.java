package com.AuraMoon.auramoon.fnb.dto;

import java.util.List;

public class MealSelectionResponse {

    private String message;
    private String status;
    private List<String> details;

    public MealSelectionResponse() {
    }

    public MealSelectionResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public MealSelectionResponse(String message, String status, List<String> details) {
        this.message = message;
        this.status = status;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
