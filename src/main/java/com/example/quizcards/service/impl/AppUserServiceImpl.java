package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.exception.ErrorsDataException;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.service.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements IAppUserService {
    @Autowired
    private IAppUserRepository userRepository;

    private final int MAX_SIZE_PER_PAGE = 20;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public Page<IAppUserDTO> getAllUsers(int pages){
        Pageable pageable = PageRequest.of(pages, MAX_SIZE_PER_PAGE);
        return userRepository.getAll(pageable);
    }

    @Override
    public IAppUserDTO detailUser(Long userId){
        return userRepository.detailUser(userId);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateAppUser(Long userId, AppUserRequest request){
        Optional<AppUser> appUserOpt = userRepository.findById(userId);
        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();

            if (userRepository.existsByUsernameExcludingUserId(request.getUsername(), userId)) {
                throw new ErrorsDataException("Register failed", Map.of("username", "Username is already taken"),
                        HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByEmailExcludingUserId(request.getEmail(), userId  )) {
                throw new ErrorsDataException("Register failed", Map.of("email", "Email is already registered"),
                        HttpStatus.BAD_REQUEST);
            }

            appUser.setAddress(request.getAddress());
            appUser.setAvatar(request.getAvatar());
            appUser.setDateOfBirth(request.getDateOfBirth());
            appUser.setEmail(request.getEmail());
            appUser.setHashPassword(passwordEncoder.encode(request.getHashPassword()));
            appUser.setEnabled(request.getEnabled());
            appUser.setUsername(request.getUsername());
            appUser.setGender(request.getGender());
            appUser.setPhoneNumber(request.getPhoneNumber());
            appUser.setFirstName(request.getFirstName());
            appUser.setLastName(request.getLastName());
            appUser.setRole(roleRepository.findById(request.getRoleId()).orElse(null));
            userRepository.save(appUser);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "App user updated successfully", HttpStatus.OK, userId));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "User not found with id: " + userId));
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteAppUser(Long userId){
        Optional<AppUser> appUserOpt = userRepository.findById(userId);
        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();
            if(appUser.getRole().getRoleName().trim().equals("ROLE_ADMIN")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Can not delete Admin"));
            }else{
                userRepository.delete(appUser);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "App user deleted successfully", HttpStatus.OK));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "User not found with id: " + userId));
    }
}
