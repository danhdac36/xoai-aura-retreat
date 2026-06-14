package com.AuraMoon.auramoon.booking.exception;

public class BookingNotCompletedException extends RuntimeException {
    public BookingNotCompletedException(String message) {
        super(message);
    }
}
