package com.AuraMoon.auramoon.dashboard.dto;

import lombok.Data;

@Data
public class TransactionSummary {
    private String guestName;
    private String service;
    private String status;
    private String date;
    private Double amount;
}
