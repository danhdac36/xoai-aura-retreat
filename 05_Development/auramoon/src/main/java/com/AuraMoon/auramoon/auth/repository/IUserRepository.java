package com.AuraMoon.auramoon.auth.repository;

import com.AuraMoon.auramoon.auth.entity.Role;
import com.AuraMoon.auramoon.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User findByVerifyToken(String verifyToken);
}
