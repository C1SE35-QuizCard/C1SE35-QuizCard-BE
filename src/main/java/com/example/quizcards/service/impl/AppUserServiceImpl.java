package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.exception.ErrorsDataException;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAppUserService;
import com.example.quizcards.utils.CodeRandom;
import com.example.quizcards.validation.PasswordConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements IAppUserService {
    private final int MAX_SIZE_PER_PAGE = 20;

    @Autowired
    private IAppUserRepository userRepository;

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
    public Page<IAppUserDTO> getAllUsers(int pages) {
        Pageable pageable = PageRequest.of(pages, MAX_SIZE_PER_PAGE);
        return userRepository.getAll(pageable);
    }

    @Override
    public List<IAppUserDTO> getAllUsers() {
        return userRepository.getAll();
    }

    @Override
    public IAppUserDTO detailUser(Long userId) {
        return userRepository.detailUser(userId);
    }


    @Override
    @Transactional
    public IAppUserDTO createAppUser(AppUserRequest request) {
        AppRole role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
        if (existsByUsername(request.getUsername()) || existsByEmail(request.getEmail())) {
            throw new RuntimeException("Username or email address already in use");
        }
        PasswordConstraintValidator validator = new PasswordConstraintValidator();
        if (!validator.isValid(request.getPassword(), null)) {
            throw new BadRequestException("Invalid password");
        }
        AppUser user = AppUser.builder()
                .address(request.getAddress())
                .avatar(request.getAvatar())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .hashPassword(passwordEncoder.encode(request.getPassword()))
                .enabled(request.getEnabled())
                .username(request.getUsername())
                .gender(request.getGender() != null && request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userCode(CodeRandom.generateRandomCode(28))
                .role(role)
                .build();
        return IAppUserDTO.AppUserDTO.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public IAppUserDTO updateAppUser(Long userId, AppUserRequest request) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (!up.getId().equals(appUser.getUserId()) &&
                appUser.getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            throw new AccessDeniedException("Cannot edit user with role admin");
        }
        AppRole role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
        if (!appUser.getUsername().equals(request.getUsername()) && existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username or email address already in use");
        }
        if (!appUser.getEmail().equals(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address already in use");
        }
        PasswordConstraintValidator validator = new PasswordConstraintValidator();
        if (request.getPassword() != null && !validator.isValid(request.getPassword(), null)) {
            throw new BadRequestException("Invalid password");
        }
        appUser.setAddress(request.getAddress() == null ? appUser.getAddress() : request.getAddress());
        appUser.setAvatar(request.getAvatar() == null ? appUser.getAvatar() : request.getAvatar());
        appUser.setDateOfBirth(request.getDateOfBirth() == null ? appUser.getDateOfBirth() : request.getDateOfBirth());
        appUser.setEmail(request.getEmail() == null ? appUser.getEmail() : request.getEmail());
        appUser.setHashPassword(request.getPassword() == null ? appUser.getHashPassword() :
                passwordEncoder.encode(request.getPassword()));
        appUser.setEnabled(request.getEnabled() == null ? appUser.getEnabled() : request.getEnabled());
        appUser.setUsername(request.getUsername() == null ? appUser.getUsername() : request.getUsername());
        appUser.setGender(request.getGender() == null ? appUser.getGender() : request.getGender());
        appUser.setPhoneNumber(request.getPhoneNumber() == null ? appUser.getPhoneNumber() : request.getPhoneNumber());
        appUser.setFirstName(request.getFirstName() == null ? appUser.getFirstName() : request.getFirstName());
        appUser.setLastName(request.getLastName() == null ? appUser.getLastName() : request.getLastName());
        appUser.setRole(request.getRoleId() == null ? appUser.getRole() : role);
        userRepository.save(appUser);
        return IAppUserDTO.AppUserDTO.from(userRepository.save(appUser));
    }

    @Override
    @Transactional
    public void deleteAppUser(Long userId) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (appUser.getRole().getRoleName().trim().equals("ROLE_ADMIN")) {
            throw new BadRequestException("Cannot delete admin");
        }
        userRepository.delete(appUser);
    }
}
