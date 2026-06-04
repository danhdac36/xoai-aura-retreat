package com.AuraMoon.auramoon.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_id")
    private GuestFolio guestFolio;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_method", length = 10)
    private String paymentMethod;

    @Column(name = "payment_gateway", length = 10)
    private String paymentGateway;

    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "status", length = 10)
    private String status;
}
