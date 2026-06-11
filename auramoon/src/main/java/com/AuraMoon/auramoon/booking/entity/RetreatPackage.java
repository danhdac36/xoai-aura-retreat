package com.AuraMoon.auramoon.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RETREAT_PACKAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer id;

    @Column(name = "type_package", length = 50)
    private String typePackage;

    @Column(name = "package_name", length = 50)
    private String packageName;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Lob
    @Column(name = "services")
    private String services;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
