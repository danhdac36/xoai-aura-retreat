package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealOrderItemRepository extends JpaRepository<MealOrderItem, Integer> {

    @Query(value = "SELECT mi.menu_item_id, mi.item_name, moi.quantity, moi.price, mi.ingredient " +
            "FROM MEAL_ORDER_ITEM moi " +
            "JOIN MENU_ITEM mi ON moi.menu_item_id = mi.menu_item_id " +
            "WHERE moi.meal_order_id = :orderId", nativeQuery = true)
    List<Object[]> findChefDashboardItemsByOrderId(@Param("orderId") Integer orderId);
=======
import org.springframework.stereotype.Repository;

@Repository
public interface MealOrderItemRepository extends JpaRepository<MealOrderItem, Integer> {
>>>>>>> origin/SourceCode
}
