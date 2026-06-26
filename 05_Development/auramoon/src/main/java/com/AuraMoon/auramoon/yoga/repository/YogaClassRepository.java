package com.AuraMoon.auramoon.yoga.repository;

import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface YogaClassRepository extends JpaRepository<YogaClass, Integer> {
    List<YogaClass> findByIsDeleteFalse();
}
