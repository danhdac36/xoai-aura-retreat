package com.AuraMoon.auramoon.spa.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PHYSICAL_HEALTH_PROFILE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PhysicalHealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Lob
    @Column(name = "medical_conditions")
    private String medicalConditions;

    @Lob
    @Column(name = "injuries")
    private String injuries;

    @Column(name = "update_at")
    private String updateAt;
}
