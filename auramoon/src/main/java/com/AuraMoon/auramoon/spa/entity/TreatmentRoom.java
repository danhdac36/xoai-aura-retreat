package com.AuraMoon.auramoon.spa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TREATMENT_ROOM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer id;

    @Column(name = "room_code", nullable = false, unique = true, length = 10)
    private String roomCode;

    @Lob
    @Column(name = "image")
    private String image;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
