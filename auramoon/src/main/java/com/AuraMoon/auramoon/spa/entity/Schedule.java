package com.AuraMoon.auramoon.spa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private TreatmentBooking treatmentBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_code", referencedColumnName = "therapist_code")
    private Therapist therapist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private TreatmentRoom room;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
