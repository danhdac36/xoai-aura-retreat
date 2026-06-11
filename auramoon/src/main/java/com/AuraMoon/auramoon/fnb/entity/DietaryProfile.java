package com.AuraMoon.auramoon.fnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DIETARY_PROFILE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietaryProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dietary_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Lob
    @Column(name = "food_allergies")
    private String foodAllergies;

    @Lob
    @Column(name = "diatary_preference")
    private String dietaryPreference;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
