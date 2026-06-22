package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.MealOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealOrderRepository extends JpaRepository<MealOrder, Integer> {
    List<MealOrder> findByBookingId(Integer bookingId);
}
