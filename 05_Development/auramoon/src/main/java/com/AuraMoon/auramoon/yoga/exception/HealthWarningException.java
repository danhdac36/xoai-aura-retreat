package com.AuraMoon.auramoon.yoga.exception;

public class HealthWarningException extends RuntimeException {
    private final String errorCode;
    private final String warningCategory;

    public HealthWarningException(String errorCode, String warningCategory, String message) {
        super(message);
        this.errorCode = errorCode;
        this.warningCategory = warningCategory;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getWarningCategory() {
        return warningCategory;
    }
}
