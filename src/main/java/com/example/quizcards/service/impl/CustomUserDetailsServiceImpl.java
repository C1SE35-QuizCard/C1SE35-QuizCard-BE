package com.example.quizcards.service.impl;

import com.example.quizcards.entities.AppUser;
import com.example.quizcards.repository.AppUserRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ICustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService, ICustomUserDetailsService {
    @Autowired
    private AppUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        AppUser user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this username or email: %s", usernameOrEmail)));
        return UserPrincipal.create(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserById(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with id: %s", id)));

        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserByUsernameOnly(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this username: %s", username)));
        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserByEmailOnly(String email) throws  UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this email: %s", email)));
        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserByUserCodeOnly(String userCode) throws  UsernameNotFoundException {
        AppUser user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this code: %s", userCode)));
        return UserPrincipal.create(user);
    }
}