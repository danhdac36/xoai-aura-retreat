package com.AuraMoon.auramoon.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CheckoutCompletedEvent extends ApplicationEvent {
    private final Integer bookingId;
    private final String guestEmail;
    private final Integer folioId;

    public CheckoutCompletedEvent(Object source, Integer bookingId, String guestEmail, Integer folioId) {
        super(source);
        this.bookingId = bookingId;
        this.guestEmail = guestEmail;
        this.folioId = folioId;
    }
}
