package com.example.quizcards.service;

import com.example.quizcards.entities.AppUser;

import java.util.Optional;

public interface IAppUserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUserCode(String userCode);
    Optional<AppUser> findById(Long id);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUserCode(String userCode);
    Optional<AppUser> findByUsernameOrEmail(String username, String email);
    void save(AppUser user);
}
