package com.AuraMoon.auramoon.yoga.exception;

public class YogaBusinessException extends RuntimeException {
    private final String errorCode;

    public YogaBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
