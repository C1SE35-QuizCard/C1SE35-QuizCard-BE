package com.example.quizcards.service.impl;

import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.service.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppUserServiceImpl implements IAppUserService {
    @Autowired
    private IAppUserRepository userRepository;

    @Autowired
    private IAppRoleRepository roleRepository;
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserCode(String userCode) {
        return userRepository.existsByUserCode(userCode);
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<AppUser> findByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode);
    }

    @Override
    public Optional<AppUser> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    @Transactional
    public void save(AppUser user) {
        userRepository.save(user);
    }
    public AppUser updateUserRole(Long userId, Long roleId) {

        Optional<AppUser> appUserOpt = userRepository.findById(userId);
        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();

            Optional<AppRole> appRoleOpt = roleRepository.findById(roleId);
            if (appRoleOpt.isPresent()) {
                AppRole appRole = appRoleOpt.get();
                appUser.setRole(appRole);

                return userRepository.save(appUser);
            } else {
                throw new RuntimeException("Role not found with id: " + roleId);
            }
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
