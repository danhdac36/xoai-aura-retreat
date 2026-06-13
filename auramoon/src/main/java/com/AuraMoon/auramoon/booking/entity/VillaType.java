package com.AuraMoon.auramoon.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "VILLA_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer id;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Lob
    @Column(name = "image")
    private String image;

    @Column(name = "price_per_day")
    private BigDecimal pricePerDay;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
