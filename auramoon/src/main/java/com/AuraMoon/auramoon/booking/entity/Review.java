package com.AuraMoon.auramoon.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REVIEW")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "rating")
    private Integer rating;

    @Lob
    @Column(name = "comment")
    private String comment;
}
