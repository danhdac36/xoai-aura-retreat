package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MealOrderRepository extends JpaRepository<MealOrder, Integer> {
    List<MealOrder> findAllByOrderedAtBetween(LocalDateTime start, LocalDateTime end);
}
