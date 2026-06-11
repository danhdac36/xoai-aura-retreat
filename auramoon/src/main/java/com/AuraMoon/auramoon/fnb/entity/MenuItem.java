package com.AuraMoon.auramoon.fnb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MENU_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Integer id;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "price")
    private BigDecimal price;

    @Lob
    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
