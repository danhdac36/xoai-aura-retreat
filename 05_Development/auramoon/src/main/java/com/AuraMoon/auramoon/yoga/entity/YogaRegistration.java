package com.AuraMoon.auramoon.yoga.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "YOGA_REGISTRATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yoga_schedule_id", nullable = false)
    private YogaSchedule schedule;

    @Builder.Default
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private String status = "REGISTERED"; // "REGISTERED", "CANCELLED"
}
