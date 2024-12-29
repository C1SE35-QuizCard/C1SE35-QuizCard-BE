package com.example.quizcards.service;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.entities.AppUser;
import org.springframework.data.domain.Page;

import java.util.List;
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

    Page<IAppUserDTO> getAllUsers(int pages);

    List<IAppUserDTO> getAllUsers();

    IAppUserDTO detailUser(Long userId);

    IAppUserDTO createAppUser(AppUserRequest request);

    IAppUserDTO updateAppUser(Long userId, AppUserRequest request);

    void deleteAppUser(Long userId);
}
