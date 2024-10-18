package com.example.quizcards.repository;

import com.example.quizcards.entities.AppUser;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUserCode(String userCode);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUserCode(String userCode);
    Optional<AppUser> findByUsernameOrEmail(String username, String email);
}
