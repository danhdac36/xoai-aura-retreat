package com.AuraMoon.auramoon.fnb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MENU_ITEM")
@SQLRestriction("is_delete = 0")
@SQLDelete(sql = "UPDATE menu_item SET is_delete = 1 WHERE menu_item_id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

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

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}