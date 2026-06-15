package com.AuraMoon.auramoon.fnb.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetail {
    private String field;
    private Object rejectedValue;
    private String allergenMatched;
}
