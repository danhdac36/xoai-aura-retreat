package com.AuraMoon.auramoon.billing.exception;

public class NightAuditAlreadyExecutedException extends RuntimeException {
    public NightAuditAlreadyExecutedException(String message) {
        super(message);
    }
}
