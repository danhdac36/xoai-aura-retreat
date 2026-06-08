package com.AuraMoon.auramoon.fnb.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DIETARY_PROFILE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class DietaryProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dietary_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Lob
    @Column(name = "food_allergies")
    private String foodAllergies;

    @Lob
    @Column(name = "diatary_preference")
    private String dietaryPreference; // DB has 'diatary_preference', java uses 'dietaryPreference'
}
