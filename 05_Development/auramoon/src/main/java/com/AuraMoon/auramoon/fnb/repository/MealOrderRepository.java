package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealOrderRepository extends JpaRepository<MealOrder, Integer> {
}
