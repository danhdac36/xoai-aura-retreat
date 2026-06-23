package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealOrderRepository extends JpaRepository<MealOrder, Integer> {
    List<MealOrder> findByBookingId(Integer bookingId);
        @Query(value = "SELECT mo.meal_order_id, mo.booking_id, mo.guest_id, mo.place_order, mo.note, mo.order_status, mo.ordered_at "
                        +
                        "FROM MEAL_ORDER mo " +
                        "WHERE mo.ordered_at >= :startOfDay " +
                        "AND mo.ordered_at < :endOfDay " +
                        "ORDER BY mo.ordered_at DESC", nativeQuery = true)
        List<Object[]> findChefDashboardOrdersByDateRange(
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay);

        @Query(value = "SELECT COALESCE(SUM(moi.quantity), 0) " +
                        "FROM MEAL_ORDER_ITEM moi " +
                        "JOIN MEAL_ORDER mo ON moi.meal_order_id = mo.meal_order_id " +
                        "WHERE mo.booking_id = :bookingId " +
                        "AND mo.ordered_at >= :startOfDay " +
                        "AND mo.ordered_at < :endOfDay " +
                        "AND mo.order_status <> 'CANCELLED' " +
                        "AND NOT EXISTS (" +
                        "    SELECT 1 FROM FOLIO_ITEM fi " +
                        "    WHERE fi.reference_id = mo.meal_order_id " +
                        "      AND fi.service_category = 'Extra F&B'" +
                        ")", nativeQuery = true)
        int sumQuantityOfFreeMealOrdersToday(
                        @Param("bookingId") Integer bookingId,
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay);

}
