package com.AuraMoon.auramoon.yoga.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "YOGA_SCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private YogaClass yogaClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private YogaInstructor instructor;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;
}
