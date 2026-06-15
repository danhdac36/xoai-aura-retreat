package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.VillaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VillaTypeRepository extends JpaRepository<VillaType, Integer> {
}
