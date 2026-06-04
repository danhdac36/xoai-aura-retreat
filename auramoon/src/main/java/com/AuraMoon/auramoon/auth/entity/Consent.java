package com.AuraMoon.auramoon.auth.entity;

import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CONSENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
    @AttributeOverride(name = "updatedAt", column = @Column(name = "update_at"))
})
public class Consent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "consent_status")
    private Boolean consentStatus = false;

    @Column(name = "consent_version", length = 8)
    private String consentVersion;
}
