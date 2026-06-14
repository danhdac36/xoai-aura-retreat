package com.AuraMoon.auramoon.fnb.service;

import com.AuraMoon.auramoon.fnb.dto.ErrorDetail;
import lombok.Getter;

import java.util.List;

@Getter
public class FnbException extends RuntimeException {
    private final String errorCode;
    private final List<ErrorDetail> details;

    public FnbException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public FnbException(String errorCode, String message, List<ErrorDetail> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}
