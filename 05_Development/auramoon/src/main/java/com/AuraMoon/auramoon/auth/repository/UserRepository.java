package com.AuraMoon.auramoon.auth.repository;

import com.AuraMoon.auramoon.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    List<User> findByIsDeleteFalse();
    List<User> findByIsDeleteFalseAndEmailContainingIgnoreCase(String email);
    List<User> findByIsDeleteTrue();
    List<User> findByIsDeleteTrueAndEmailContainingIgnoreCase(String email);
    java.util.Optional<User> findByEmail(String email);
    List<User> findByFullNameContainingIgnoreCase(String fullName);
}
