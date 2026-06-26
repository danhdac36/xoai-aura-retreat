package com.AuraMoon.auramoon.yoga.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "YOGA_CLASS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer id;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
