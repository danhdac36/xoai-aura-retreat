package com.AuraMoon.auramoon.spa.exception;

public class SpaBusinessException extends RuntimeException {
    private final String errorCode;

    public SpaBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
