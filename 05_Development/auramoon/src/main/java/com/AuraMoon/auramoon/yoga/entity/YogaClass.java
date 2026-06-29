package com.AuraMoon.auramoon.yoga.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "YOGA_CLASS")
@SQLRestriction("is_delete = 0")
@SQLDelete(sql = "UPDATE yoga_class SET is_delete = 1 WHERE class_id = ?")
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
