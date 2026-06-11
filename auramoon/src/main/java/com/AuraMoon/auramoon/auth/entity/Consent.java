package com.AuraMoon.auramoon.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONSENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "consent_status")
    @Builder.Default
    private Boolean consentStatus = false;

    @Column(name = "consent_version", length = 8)
    private String consentVersion;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_delete")
    @Builder.Default
    private Boolean isDelete = false;
}
