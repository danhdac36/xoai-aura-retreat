package com.AuraMoon.auramoon.spa.repository;

import com.AuraMoon.auramoon.spa.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Integer> {
    
    @Query(value = "SELECT u.full_name FROM THERAPIST t JOIN [USER] u ON t.therapist_id = u.user_id WHERE t.therapist_code = :code", nativeQuery = true)
    String findTherapistNameByCode(@Param("code") String code);
}
