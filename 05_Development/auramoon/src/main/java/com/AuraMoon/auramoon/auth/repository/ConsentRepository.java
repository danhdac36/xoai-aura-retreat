package com.AuraMoon.auramoon.auth.repository;

import com.AuraMoon.auramoon.auth.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Integer> {
    Optional<Consent> findFirstByUser_IdOrderByUpdatedAtDesc(Integer userId);
}
