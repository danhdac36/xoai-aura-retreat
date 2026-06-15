package com.AuraMoon.auramoon.spa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "THERAPIST")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Therapist {

    @Id
    @Column(name = "therapist_id")
    private Integer id; // Also maps to user_id

    @Column(name = "therapist_code", nullable = false, unique = true, length = 6)
    private String therapistCode;

    @Column(name = "status", length = 10)
    private String status;
}
