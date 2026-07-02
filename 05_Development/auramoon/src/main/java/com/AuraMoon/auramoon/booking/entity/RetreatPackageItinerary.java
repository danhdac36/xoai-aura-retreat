package com.AuraMoon.auramoon.booking.entity;

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
    @Column(name = "itinerary_template_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RetreatPackage retreatPackage;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "activity_name", length = 100)
    private String activityName;

    @Column(name = "service_code", length = 10)
    private String serviceCode;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "description", length = 255)
    private String description;
}
