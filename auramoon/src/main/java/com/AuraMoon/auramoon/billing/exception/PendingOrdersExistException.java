package com.AuraMoon.auramoon.billing.exception;

public class PendingOrdersExistException extends RuntimeException {
    public PendingOrdersExistException(String message) {
        super(message);
    }
}
