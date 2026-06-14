package com.AuraMoon.auramoon.booking.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "BOOKING")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "create_at", updatable = false)),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer id;

    // Cross-module relation to auth.User
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
}
