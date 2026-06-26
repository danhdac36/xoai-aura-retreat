package com.AuraMoon.auramoon.yoga.repository;

import com.AuraMoon.auramoon.yoga.entity.YogaInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface YogaInstructorRepository extends JpaRepository<YogaInstructor, Integer> {
    Optional<YogaInstructor> findByInstructorCodeAndIsDeleteFalse(String code);
}
