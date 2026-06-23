package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PhysicalHealthProfileRepository extends JpaRepository<PhysicalHealthProfile, Integer> {
    Optional<PhysicalHealthProfile> findByUserId(Integer userId);
}
