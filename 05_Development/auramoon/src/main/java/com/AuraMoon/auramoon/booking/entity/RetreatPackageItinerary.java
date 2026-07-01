package com.AuraMoon.auramoon.booking.entity;

import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RETREAT_PACKAGE_ITINERARY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackageItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itinerary_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RetreatPackage retreatPackage;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private TreatmentService treatmentService;

    @Column(name = "meal_included")
    @Builder.Default
    private Boolean mealIncluded = false;

    @Column(name = "description", length = 255)
    private String description;
}
