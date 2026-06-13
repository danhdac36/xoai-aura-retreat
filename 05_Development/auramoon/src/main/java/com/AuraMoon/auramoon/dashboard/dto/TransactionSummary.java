package com.AuraMoon.auramoon.dashboard.dto;

import lombok.Data;

@Data
public class TransactionSummary {
    private String description;
    private String date;
    private Double amount;
}
