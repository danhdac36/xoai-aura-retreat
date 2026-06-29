package com.AuraMoon.auramoon.spa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;

@Entity
@Table(name = "TREATMENT_SERVICE")
@SQLRestriction("is_delete = 0")
@SQLDelete(sql = "UPDATE treatment_service SET is_delete = 1 WHERE service_id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer id;

    @Column(name = "treatment_code", nullable = false, unique = true, length = 10)
    private String treatmentCode;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
