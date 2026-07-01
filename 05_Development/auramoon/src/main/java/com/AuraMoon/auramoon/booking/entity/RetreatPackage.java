package com.AuraMoon.auramoon.booking.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "RETREAT_PACKAGE")
@SQLRestriction("is_delete = 0")
@SQLDelete(sql = "UPDATE retreat_package SET is_delete = 1 WHERE package_id = ?")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetreatPackage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer id;

    @Column(name = "type_package", length = 50)
    private String typePackage;

    @Column(name = "package_name", length = 50)
    private String packageName;

    @Column(name = "duration_days")
    private Integer durationDays;

    @OneToMany(mappedBy = "retreatPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("dayNumber ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RetreatPackageItinerary> itineraries;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "price")
    private BigDecimal price;
}
