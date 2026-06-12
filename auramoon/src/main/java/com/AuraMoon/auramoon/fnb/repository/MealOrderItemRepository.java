package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealOrderItemRepository extends JpaRepository<MealOrderItem, Integer> {
    List<MealOrderItem> findAllByMealOrderId(Integer mealOrderId);
}