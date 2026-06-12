package com.AuraMoon.auramoon.fnb.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "MENU_ITEM")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "create_at", updatable = false)),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class MenuItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Integer id;

    @Column(name = "item_name", nullable = false, length = 20)
    private String itemName;

    @Column(name = "price")
    private BigDecimal price;

    @Lob
    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
