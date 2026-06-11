package com.AuraMoon.auramoon.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer id;

    @Column(name = "guest_id", nullable = false)
    private Integer guestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private RetreatPackage retreatPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_villa_id")
    private Villa assignedVilla;

    @Column(name = "checkin_date")
    private LocalDate checkinDate;

    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @Column(name = "total_guests")
    private Integer totalGuests;

    @Column(name = "booking_status", length = 10)
    private String bookingStatus;

    @Column(name = "payment_status", length = 10)
    private String paymentStatus;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
