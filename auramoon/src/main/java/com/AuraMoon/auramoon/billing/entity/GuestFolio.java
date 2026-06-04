package com.AuraMoon.auramoon.billing.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "GUEST_FOLIO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "create_at", updatable = false)),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class GuestFolio extends BaseEntity {

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
}
