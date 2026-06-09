package com.AuraMoon.auramoon.fnb.repository;

import com.AuraMoon.auramoon.fnb.entity.DietaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietaryProfileRepository extends JpaRepository<DietaryProfile, Integer> {
    Optional<DietaryProfile> findByUserId(Integer userId);
}