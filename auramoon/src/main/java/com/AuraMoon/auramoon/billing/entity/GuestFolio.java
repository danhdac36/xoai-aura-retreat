package com.AuraMoon.auramoon.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GUEST_FOLIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestFolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folio_id")
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "total_package_amout") // typo in db, kept as is
    private BigDecimal totalPackageAmount;

    @Column(name = "total_extra_fb")
    private BigDecimal totalExtraFb;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
