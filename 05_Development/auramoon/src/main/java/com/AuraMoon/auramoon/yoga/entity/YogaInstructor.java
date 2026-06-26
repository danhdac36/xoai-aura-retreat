package com.AuraMoon.auramoon.yoga.entity;

import com.AuraMoon.auramoon.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "YOGA_INSTRUCTOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaInstructor {

    @Id
    @Column(name = "instructor_id")
    private Integer instructorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "instructor_id")
    private User user;

    @Column(name = "instructor_code", nullable = false, unique = true, length = 10)
    private String instructorCode;

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "AVAILABLE";

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
