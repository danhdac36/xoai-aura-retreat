package com.AuraMoon.auramoon.auth.entity;

import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "[USER]")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Column(name = "gender", length = 6)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 20)
    private String phone;

    @Convert(converter = AesDataEncryptor.class)
    @Column(name = "Identify_code", length = 255)
    private String identifyCode;

    @Lob
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "verify_token")
    private String verifyToken;

    @Column(name = "booking_id")
    private String bookingId;

}
