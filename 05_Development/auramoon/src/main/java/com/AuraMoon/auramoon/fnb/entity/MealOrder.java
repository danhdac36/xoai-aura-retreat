package com.AuraMoon.auramoon.fnb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEAL_ORDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MealOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_order_id")
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "folio_id", nullable = false)
    private Integer folioId;

    @Column(name = "guest_id", nullable = false)
    private Integer guestId;

    @CreatedDate
    @Column(name = "ordered_at", updatable = false)
    private LocalDateTime orderedAt;

    @Column(name = "ordered_by")
    private Integer orderedBy;

    @Column(name = "place_order", length = 100)
    private String placeOrder;

    @Lob
    @Column(name = "note")
    private String note;

    @Column(name = "order_status", length = 10)
    private String orderStatus;

    @Column(name = "serving_time", length = 20)
    private String servingTime;
}
