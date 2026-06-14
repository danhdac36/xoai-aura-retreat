package com.AuraMoon.auramoon.spa.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TREATMENT_BOOKING")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "create_at", updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class TreatmentBooking extends BaseEntity {

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
}
