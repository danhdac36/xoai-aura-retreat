package com.AuraMoon.auramoon.spa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TREATMENT_BOOKING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "treatment_id")
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "folio_id")
    private Integer folioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private TreatmentService treatmentService;

    @Lob
    @Column(name = "note")
    private String note;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
