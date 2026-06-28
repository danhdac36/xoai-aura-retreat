package com.AuraMoon.auramoon.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VILLA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Villa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "villa_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "villa_type", nullable = false)
    private VillaType villaType;

    @Column(name = "villa_code", nullable = false, unique = true, length = 10)
    private String villaCode;

    @Column(name = "limit_person")
    private Integer limitPerson;

    @Column(name = "villa_status", length = 10)
    private String villaStatus;

    @Column(name = "cleaning_status", length = 10)
    private String cleaningStatus;

    @Column(name = "maintenance_note", length = 500)
    private String maintenanceNote;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
